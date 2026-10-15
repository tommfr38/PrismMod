package net.tomi.prismmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.tomi.prismmod.init.PrismItems;

/**
 * A stonecutter that grinds gems. It has no screen: right-click it with a diamond to turn
 * that diamond into two Diamond Powder. Turning powder into Prism Powder happens in a
 * bundle, see {@link net.tomi.prismmod.PrismPowderMaking}.
 */
public class DiamondGrinderBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<DiamondGrinderBlock> CODEC = simpleCodec(DiamondGrinderBlock::new);

    public static final int POWDER_PER_DIAMOND = 2;

    private static final VoxelShape SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 9.0 / 16.0, 1.0);

    public DiamondGrinderBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.DIAMOND)) {
            if (level instanceof ServerLevel server) {
                stack.shrink(1);
                give(player, new ItemStack(PrismItems.DIAMOND_POWDER, POWDER_PER_DIAMOND));
                effects(server, pos, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static void effects(ServerLevel level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.8F, pitch);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.05);
    }
}
