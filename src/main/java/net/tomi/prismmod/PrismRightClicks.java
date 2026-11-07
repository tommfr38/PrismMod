package net.tomi.prismmod;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The "Prism Right Clicks" switch. While it is off, Prism's Power and the Prism Pickaxe's
 * 3x3 right-click mining do nothing and let the right click fall through, so a shield in
 * the other hand works.
 *
 * <p>The key lives on the client, but the items decide on the server, so the client sends
 * its state whenever it changes and once on joining. The server keeps it per player, and
 * both sides must agree because the sword and pickaxe run their checks on both.
 */
public class PrismRightClicks {

    public record Payload(boolean enabled, boolean announce) implements CustomPacketPayload {
        public static final Type<Payload> TYPE = new Type<>(PrismMod.id("right_clicks"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Payload::enabled,
                ByteBufCodecs.BOOL, Payload::announce,
                Payload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** The local player's own setting. Only read on the logical client. */
    private static volatile boolean clientEnabled = true;
    /**
     * Set on the client when a right click fell through to the Prism Pickaxe, meaning the block
     * under the crosshair has no right-click of its own (or the player is crouching). Right-click
     * mining only runs while this is set, so a chest opens normally and crouch-right-click mines it.
     */
    private static volatile boolean miningArmed;
    private static final Map<UUID, Boolean> SERVER = new ConcurrentHashMap<>();

    public static boolean isEnabled(Player player) {
        if (player.level().isClientSide()) {
            return clientEnabled;
        }
        return SERVER.getOrDefault(player.getUUID(), true);
    }

    public static boolean isClientEnabled() {
        return clientEnabled;
    }

    public static void setClientEnabled(boolean enabled) {
        clientEnabled = enabled;
    }

    public static boolean isMiningArmed() {
        return miningArmed;
    }

    public static void setMiningArmed(boolean armed) {
        miningArmed = armed;
    }

    public static void init() {
        PayloadTypeRegistry.serverboundPlay().register(Payload.TYPE, Payload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Payload.TYPE, (payload, context) -> {
            SERVER.put(context.player().getUUID(), payload.enabled());
            if (payload.announce()) {
                context.player().sendOverlayMessage(Component.translatable(payload.enabled()
                        ? "message.prismmod.right_clicks_on"
                        : "message.prismmod.right_clicks_off"));
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                SERVER.remove(handler.getPlayer().getUUID()));
    }
}
