package net.tomi.prismmod;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.tomi.prismmod.init.PrismItems;
import net.tomi.prismmod.item.PrismPickaxeItem;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The server half of the Prism Pickaxe's right-click mining.
 *
 * <p>The client tells us when the player is mining with the right button. When a block then
 * breaks under a Prism Pickaxe, the eight blocks around it, in the plane of the face that
 * was being mined, break too, each through the player's normal break path (Fortune, Silk
 * Touch, one durability each). Ordinary left-click mining never triggers it.
 */
public class PrismPickaxeMining {

    public record Payload(boolean active) implements CustomPacketPayload {
        public static final Type<Payload> TYPE = new Type<>(PrismMod.id("right_click_mining"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Payload::active,
                Payload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static final Set<UUID> RIGHT_MINING = ConcurrentHashMap.newKeySet();
    /** The face being mined, noted just before the block goes and used just after. */
    private static final Map<UUID, Direction> FACES = new ConcurrentHashMap<>();
    private static boolean breakingNeighbours;

    public static void init() {
        PayloadTypeRegistry.serverboundPlay().register(Payload.TYPE, Payload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Payload.TYPE, (payload, context) -> {
            UUID id = context.player().getUUID();
            if (payload.active()) {
                RIGHT_MINING.add(id);
            } else {
                RIGHT_MINING.remove(id);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            RIGHT_MINING.remove(handler.getPlayer().getUUID());
            FACES.remove(handler.getPlayer().getUUID());
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (!breakingNeighbours && player instanceof ServerPlayer && isRightMining(player)) {
                HitResult hit = player.pick(player.blockInteractionRange(), 1.0F, false);
                if (hit instanceof BlockHitResult block && block.getBlockPos().equals(pos)) {
                    FACES.put(player.getUUID(), block.getDirection());
                }
            }
            return true;
        });

        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            Direction face = FACES.remove(player.getUUID());
            if (face == null || breakingNeighbours || !(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            breakingNeighbours = true;
            try {
                breakAround(serverPlayer, pos, face);
            } finally {
                breakingNeighbours = false;
            }
        });
    }

    private static boolean isRightMining(Player player) {
        ItemStack held = player.getMainHandItem();
        return held.is(PrismItems.PRISM_PICKAXE)
                && RIGHT_MINING.contains(player.getUUID())
                && PrismRightClicks.isEnabled(player);
    }

    private static void breakAround(ServerPlayer player, BlockPos center, Direction face) {
        for (BlockPos pos : PrismPickaxeItem.neighbours(center, face)) {
            if (!player.getMainHandItem().is(PrismItems.PRISM_PICKAXE)) {
                return; // it broke on the way
            }
            if (PrismPickaxeItem.canBreak(player.level(), pos)) {
                player.gameMode.destroyBlock(pos);
            }
        }
    }
}
