package net.tomi.prismmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.tomi.prismmod.PrismMod;
import net.tomi.prismmod.PrismPickaxeMining;
import net.tomi.prismmod.PrismRightClicks;
import net.tomi.prismmod.init.PrismItems;
import net.tomi.prismmod.init.PrismMenus;
import net.tomi.prismmod.item.PrismPickaxeItem;
import net.tomi.prismmod.pylon.PylonNetworking;
import org.lwjgl.glfw.GLFW;

public class PrismModClient implements ClientModInitializer {

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(PrismMod.id("prism"));

    /** Backtick by default. Switches Prism's Power and the pickaxe's right-click mining on and off. */
    private static final KeyMapping TOGGLE_RIGHT_CLICKS = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.prismmod.toggle_right_clicks", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY));

    /** What the server was last told about right-click mining. */
    private static boolean sentRightMining;

    @Override
    public void onInitializeClient() {
        MenuScreens.register(PrismMenus.DIAMOND_GRINDER, DiamondGrinderScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(PylonNetworking.OpenName.TYPE, (payload, context) ->
                context.client().setScreen(new PylonNameScreen(payload.pos(), payload.suggested())));
        ClientPlayNetworking.registerGlobalReceiver(PylonNetworking.OpenList.TYPE, (payload, context) ->
                context.client().setScreen(new PylonListScreen(payload)));
        ClientPlayNetworking.registerGlobalReceiver(PylonNetworking.Full.TYPE, (payload, context) ->
                PylonNetworking.setClientFull(payload.full()));
        // A full network in one world must not block placing in the next; the server sends the
        // real state as the player joins.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                PylonNetworking.setClientFull(false));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_RIGHT_CLICKS.consumeClick()) {
                boolean enabled = !PrismRightClicks.isClientEnabled();
                PrismRightClicks.setClientEnabled(enabled);
                ClientPlayNetworking.send(new PrismRightClicks.Payload(enabled, true));
            }
        });
        // The setting outlives a world, the server's copy does not.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            sentRightMining = false;
            ClientPlayNetworking.send(new PrismRightClicks.Payload(PrismRightClicks.isClientEnabled(), false));
        });
    }

    /**
     * True while the player is holding right-click on any breakable block with a Prism Pickaxe.
     * Called every tick from {@code MinecraftMixin}, which is also where the server is told,
     * so the news always reaches it ahead of the block-breaking packets of the same tick.
     *
     * <p>The first tick of a hold also starts the break the way a left-click press does.
     * Vanilla's continue-breaking step assumes a break was already started, and if you
     * mine the same block twice in a row it resumes the old, abandoned one without telling
     * the server, which leaves the crack on the block and never breaks it.
     */
    public static boolean rightClickMining(Minecraft client) {
        BlockHitResult hit = client.hitResult instanceof BlockHitResult block
                && block.getType() == HitResult.Type.BLOCK ? block : null;
        if (!client.options.keyUse.isDown()) {
            PrismRightClicks.setMiningArmed(false);
        }
        boolean active = hit != null && client.player != null && client.level != null && client.screen == null
                && client.options.keyUse.isDown()
                && PrismRightClicks.isClientEnabled()
                && PrismRightClicks.isMiningArmed()
                && client.player.getMainHandItem().is(PrismItems.PRISM_PICKAXE)
                && PrismPickaxeItem.canBreak(client.level, hit.getBlockPos());
        if (active && !sentRightMining && !client.options.keyAttack.isDown()
                && client.gameMode.startDestroyBlock(hit.getBlockPos(), hit.getDirection())) {
            client.level.addBreakingBlockEffect(hit.getBlockPos(), hit.getDirection());
            client.player.swing(InteractionHand.MAIN_HAND);
        }
        if (active != sentRightMining && client.getConnection() != null) {
            sentRightMining = active;
            ClientPlayNetworking.send(new PrismPickaxeMining.Payload(active));
        }
        return active;
    }
}
