package net.tomi.prismmod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.tomi.prismmod.PrismRightClicks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Prism Pickaxe. Left-click mining is a netherite pickaxe, a little quicker.
 *
 * <p>Hold right-click on a block and it mines exactly as it would on left-click, with the
 * same break time. The client drives that (see {@code MinecraftMixin}); when the block
 * finally breaks, {@link net.tomi.prismmod.PrismPickaxeMining} breaks the eight blocks
 * around it as well.
 *
 * <p>This class only claims the right click. Returning CONSUME stops it reaching the other
 * hand, because a raised shield would block mining. With "Prism Right Clicks" switched off,
 * or on a block that cannot be broken, it passes and a shield works.
 *
 * <p>A block with a right-click of its own (a chest, a furnace, a lever) keeps it, as it does for
 * any held item. Crouching skips that, so crouch + right-click mines such a block.
 */
public class PrismPickaxeItem extends Item {

    public PrismPickaxeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> out, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, out, flag);
        PrismTooltips.add(stack, out);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || context.getHand() != InteractionHand.MAIN_HAND
                || !PrismRightClicks.isEnabled(player)
                || !canBreak(context.getLevel(), context.getClickedPos())) {
            return InteractionResult.PASS;
        }
        // Vanilla only gets here when the block did not use the click itself: a chest, furnace
        // or lever takes it first, unless the player is crouching.
        if (context.getLevel().isClientSide()) {
            PrismRightClicks.setMiningArmed(true);
        }
        return InteractionResult.CONSUME;
    }

    /** Any block right-click mining may take: everything but air, fluids and bedrock-style unbreakables. */
    public static boolean canBreak(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir()
                && !(state.getBlock() instanceof LiquidBlock)
                && state.getDestroySpeed(level, pos) >= 0.0F;
    }

    /**
     * The eight blocks around {@code center} in the plane of the face being mined. Client and
     * server both use this, so the cracks drawn are the blocks that break.
     */
    public static List<BlockPos> neighbours(BlockPos center, Direction face) {
        Direction.Axis axis = face.getAxis();
        Direction a = axis == Direction.Axis.X ? Direction.UP : Direction.EAST;
        Direction b = axis == Direction.Axis.Z ? Direction.UP : Direction.SOUTH;
        List<BlockPos> around = new ArrayList<>(8);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    around.add(center.relative(a, i).relative(b, j));
                }
            }
        }
        return around;
    }
}
