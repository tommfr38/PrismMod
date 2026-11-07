package net.tomi.prismmod.pylon;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.tomi.prismmod.init.PrismSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tomi.prismmod.PrismMod;
import net.tomi.prismmod.init.PrismBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Packets and server rules for the pylon network. The server owns the list; the client only
 * shows it and sends back what the player chose, and every request is checked against where
 * the player actually is.
 */
public class PylonNetworking {

    /** How close a player must be to a pylon to name it or travel from it. */
    private static final double REACH_SQUARED = 10.0 * 10.0;

    public record ListEntry(Identifier dimension, BlockPos pos, String name) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ListEntry> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, ListEntry::dimension,
                BlockPos.STREAM_CODEC, ListEntry::pos,
                ByteBufCodecs.stringUtf8(PylonNetwork.MAX_NAME_LENGTH * 4), ListEntry::name,
                ListEntry::new);
    }

    /** Server to client: a pylon was just placed, ask for its name. */
    public record OpenName(BlockPos pos, String suggested) implements CustomPacketPayload {
        public static final Type<OpenName> TYPE = new Type<>(PrismMod.id("pylon_open_name"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenName> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, OpenName::pos,
                ByteBufCodecs.stringUtf8(PylonNetwork.MAX_NAME_LENGTH * 4), OpenName::suggested,
                OpenName::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Server to client: show every pylon; {@code here} is the one that was clicked. */
    public record OpenList(Identifier hereDimension, BlockPos here, List<ListEntry> entries) implements CustomPacketPayload {
        public static final Type<OpenList> TYPE = new Type<>(PrismMod.id("pylon_open_list"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenList> CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, OpenList::hereDimension,
                BlockPos.STREAM_CODEC, OpenList::here,
                ListEntry.STREAM_CODEC.apply(ByteBufCodecs.list(PylonNetwork.MAX_PYLONS)), OpenList::entries,
                OpenList::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Client to server: the name the player typed for the pylon at {@code pos}. */
    public record Rename(BlockPos pos, String name) implements CustomPacketPayload {
        public static final Type<Rename> TYPE = new Type<>(PrismMod.id("pylon_rename"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Rename> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Rename::pos,
                ByteBufCodecs.stringUtf8(PylonNetwork.MAX_NAME_LENGTH * 4), Rename::name,
                Rename::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Client to server: travel to a pylon from the list. */
    public record Travel(Identifier dimension, BlockPos pos) implements CustomPacketPayload {
        public static final Type<Travel> TYPE = new Type<>(PrismMod.id("pylon_travel"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Travel> CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Travel::dimension,
                BlockPos.STREAM_CODEC, Travel::pos,
                Travel::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Server to client: whether the network is at its limit, so the client can refuse to place
     * an extra pylon itself instead of showing one the server will take back.
     */
    public record Full(boolean full) implements CustomPacketPayload {
        public static final Type<Full> TYPE = new Type<>(PrismMod.id("pylon_full"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Full> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Full::full,
                Full::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** The client's copy of {@link Full}. Only the client sets or reads it. */
    private static volatile boolean clientFull = false;

    public static boolean isClientFull() {
        return clientFull;
    }

    public static void setClientFull(boolean full) {
        clientFull = full;
    }

    private record Opened(ResourceKey<Level> dimension, BlockPos pos) {
    }

    /** The pylon each player last opened the list at, so a travel request can be checked. */
    private static final Map<UUID, Opened> OPENED = new ConcurrentHashMap<>();

    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(OpenName.TYPE, OpenName.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenList.TYPE, OpenList.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(Full.TYPE, Full.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(Rename.TYPE, Rename.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(Travel.TYPE, Travel.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(Rename.TYPE, (payload, context) ->
                rename(context.player(), payload));
        ServerPlayNetworking.registerGlobalReceiver(Travel.TYPE, (payload, context) ->
                travel(context.player(), payload));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                OPENED.remove(handler.getPlayer().getUUID()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ServerPlayNetworking.send(handler.getPlayer(), new Full(PylonNetwork.get(server).isFull())));
    }

    /** Tells every player whether the network is full. Call after a pylon joins or leaves it. */
    public static void syncFull(MinecraftServer server) {
        Full payload = new Full(PylonNetwork.get(server).isFull());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void askForName(ServerPlayer player, BlockPos pos, String suggested) {
        ServerPlayNetworking.send(player, new OpenName(pos, suggested));
    }

    public static void openList(ServerPlayer player, BlockPos here) {
        ServerLevel level = player.level();
        PylonNetwork network = PylonNetwork.get(level.getServer());
        List<ListEntry> entries = new ArrayList<>();
        for (PylonNetwork.Entry entry : network.entries()) {
            entries.add(new ListEntry(entry.dimension().identifier(), entry.pos(), entry.name()));
        }
        OPENED.put(player.getUUID(), new Opened(level.dimension(), here));
        ServerPlayNetworking.send(player, new OpenList(level.dimension().identifier(), here, entries));
    }

    private static void rename(ServerPlayer player, Rename payload) {
        ServerLevel level = player.level();
        PylonNetwork network = PylonNetwork.get(level.getServer());
        if (network.find(level.dimension(), payload.pos()) == null
                || player.blockPosition().distSqr(payload.pos()) > REACH_SQUARED) {
            return;
        }
        String name = PylonNetwork.cleanName(payload.name());
        if (!name.isEmpty()) {
            network.rename(level.dimension(), payload.pos(), name);
        }
    }

    private static void travel(ServerPlayer player, Travel payload) {
        MinecraftServer server = player.level().getServer();
        PylonNetwork network = PylonNetwork.get(server);
        Opened opened = OPENED.get(player.getUUID());
        if (opened == null || !opened.dimension().equals(player.level().dimension())
                || player.blockPosition().distSqr(opened.pos()) > REACH_SQUARED) {
            return;
        }
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, payload.dimension());
        PylonNetwork.Entry target = network.find(dimension, payload.pos());
        ServerLevel targetLevel = server.getLevel(dimension);
        if (target == null || targetLevel == null) {
            player.sendOverlayMessage(Component.translatable("message.prismmod.pylon_missing"));
            return;
        }
        if (!targetLevel.getBlockState(target.pos()).is(PrismBlocks.PYLON)) {
            // It was removed some way the network did not see.
            network.remove(dimension, target.pos());
            player.sendOverlayMessage(Component.translatable("message.prismmod.pylon_missing"));
            return;
        }
        BlockPos stand = standingSpot(targetLevel, target.pos());
        if (stand == null) {
            player.sendOverlayMessage(Component.translatable("message.prismmod.pylon_blocked", target.name()));
            return;
        }

        effects(player.level(), player.position().x, player.position().y, player.position().z);
        player.teleportTo(targetLevel, stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5,
                Set.of(), player.getYRot(), player.getXRot(), true);
        player.resetFallDistance();
        effects(targetLevel, stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5);
    }

    /** Somewhere to stand next to or on top of the pylon: two blocks of head room. */
    private static BlockPos standingSpot(ServerLevel level, BlockPos pylon) {
        List<BlockPos> candidates = new ArrayList<>();
        candidates.add(pylon.above());
        for (Direction side : Direction.Plane.HORIZONTAL) {
            candidates.add(pylon.relative(side));
        }
        for (BlockPos spot : candidates) {
            if (isFree(level, spot) && isFree(level, spot.above())) {
                return spot;
            }
        }
        return null;
    }

    private static boolean isFree(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getCollisionShape(level, pos).isEmpty() && state.getFluidState().isEmpty();
    }

    private static void effects(Level level, double x, double y, double z) {
        level.playSound(null, x, y, z, PrismSounds.PYLON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 0.5F);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y + 1.0, z, 30, 0.3, 0.6, 0.3, 0.1);
        }
    }
}
