package net.tomi.prismmod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.tomi.prismmod.init.PrismBlocks;

import java.util.function.Consumer;

/**
 * Prism Powder, after Terraria's purification powder: right-click a block and every stone
 * or deepslate within {@link #RADIUS} of it turns into the matching Prism Ore. One use
 * per powder.
 */
public class PrismPowderItem extends Item {

    /** 1 is a 3x3x3 cube around the clicked block. */
    public static final int RADIUS = 1;

    public PrismPowderItem(Properties properties) {
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
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        if (!(level instanceof ServerLevel server)) {
            return convertible(level, center) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        int changed = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -RADIUS, -RADIUS),
                center.offset(RADIUS, RADIUS, RADIUS))) {
            BlockState state = server.getBlockState(pos);
            BlockState ore = null;
            if (state.is(Blocks.STONE)) {
                ore = PrismBlocks.PRISM_ORE.defaultBlockState();
            } else if (state.is(Blocks.DEEPSLATE)) {
                ore = PrismBlocks.DEEPSLATE_PRISM_ORE.defaultBlockState();
            }
            if (ore != null) {
                server.setBlock(pos, ore, 3);
                server.sendParticles(ParticleTypes.END_ROD,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.3, 0.3, 0.3, 0.02);
                changed++;
            }
        }
        if (changed == 0) {
            return InteractionResult.PASS;
        }
        server.playSound(null, center, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        Player player = context.getPlayer();
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean convertible(Level level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -RADIUS, -RADIUS),
                center.offset(RADIUS, RADIUS, RADIUS))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE)) {
                return true;
            }
        }
        return false;
    }
}
