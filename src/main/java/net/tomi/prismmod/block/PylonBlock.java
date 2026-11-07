package net.tomi.prismmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.tomi.prismmod.pylon.PylonNetwork;
import net.tomi.prismmod.pylon.PylonNetworking;

/**
 * A pylon. Placing one adds it to the {@link PylonNetwork} and asks the player to name it;
 * right-clicking one opens the list of every pylon to travel between. The colour of the
 * block records the dimension it was placed in.
 */
public class PylonBlock extends Block {

    public static final MapCodec<PylonBlock> CODEC = simpleCodec(PylonBlock::new);

    public static final EnumProperty<PylonDimension> DIMENSION = EnumProperty.create("dimension", PylonDimension.class);

    public PylonBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DIMENSION, PylonDimension.OTHER));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIMENSION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(DIMENSION, PylonDimension.of(context.getLevel().dimension()));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level instanceof ServerLevel server) {
            PylonNetwork network = PylonNetwork.get(server.getServer());
            String name = network.defaultName();
            if (network.add(server.dimension(), pos, name)) {
                PylonNetworking.syncFull(server.getServer());
                if (placer instanceof ServerPlayer player) {
                    PylonNetworking.askForName(player, pos, name);
                }
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel server && player instanceof ServerPlayer serverPlayer) {
            PylonNetwork network = PylonNetwork.get(server.getServer());
            // A pylon that got here without being placed by hand (a command, a structure) joins
            // the network the first time it is used, if there is room.
            if (network.find(server.dimension(), pos) == null) {
                if (!network.add(server.dimension(), pos, network.defaultName())) {
                    serverPlayer.sendOverlayMessage(Component.translatable("message.prismmod.pylon_limit", PylonNetwork.MAX_PYLONS));
                    return InteractionResult.SUCCESS;
                }
                PylonNetworking.syncFull(server.getServer());
            }
            PylonNetworking.openList(serverPlayer, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        if (!level.getBlockState(pos).is(this)) {
            PylonNetwork.get(level.getServer()).remove(level.dimension(), pos);
            PylonNetworking.syncFull(level.getServer());
        }
    }
}
