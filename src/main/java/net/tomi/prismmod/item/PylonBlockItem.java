package net.tomi.prismmod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.tomi.prismmod.pylon.PylonNetwork;
import net.tomi.prismmod.pylon.PylonNetworking;

/**
 * Refuses to place a pylon once the network already holds the maximum. The client refuses too,
 * from the state the server syncs, so a refused pylon never flashes into the world or leaves
 * the hand.
 */
public class PylonBlockItem extends TooltipBlockItem {

    public PylonBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (context.getLevel().isClientSide()) {
            if (PylonNetworking.isClientFull()) {
                return InteractionResult.FAIL;
            }
        } else if (context.getLevel() instanceof ServerLevel level && PylonNetwork.get(level.getServer()).isFull()) {
            Player player = context.getPlayer();
            if (player != null) {
                player.sendOverlayMessage(Component.translatable("message.prismmod.pylon_limit", PylonNetwork.MAX_PYLONS));
            }
            // If the client placed it anyway (its copy of the limit was stale), it already took
            // the pylon out of its hand; resend the inventory so the pylon shows up again.
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.containerMenu.sendAllDataToRemote();
            }
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }
}
