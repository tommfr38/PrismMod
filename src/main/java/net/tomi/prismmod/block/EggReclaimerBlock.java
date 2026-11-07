package net.tomi.prismmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.tomi.prismmod.init.PrismItems;

import java.util.List;

/**
 * Gives the dragon egg back for a Prism Sword or Prism Pickaxe, so the dragon can be
 * respawned without giving up the tool. Right-click it holding one: the tool is taken, and
 * it and the egg come back out together.
 *
 * <p>The returned tool is stamped, and a stamped tool is refused, so one tool yields one
 * egg for good. A freshly crafted tool costs an egg to make, so it is unstamped.
 */
public class EggReclaimerBlock extends Block {

    public static final MapCodec<EggReclaimerBlock> CODEC = simpleCodec(EggReclaimerBlock::new);

    private static final String RECLAIMED_KEY = "prismmod_egg_reclaimed";
    private static final int PICKUP_DELAY_TICKS = 40;

    public EggReclaimerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public static boolean isReclaimed(ItemStack sword) {
        return sword.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getBooleanOr(RECLAIMED_KEY, false);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(PrismItems.PRISM_SWORD) && !stack.is(PrismItems.PRISM_PICKAXE)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        if (isReclaimed(stack)) {
            player.sendOverlayMessage(Component.translatable("message.prismmod.egg_already_reclaimed"));
            return InteractionResult.SUCCESS;
        }

        ItemStack sword = stack.copy();
        CustomData.update(DataComponents.CUSTOM_DATA, sword, tag -> tag.putBoolean(RECLAIMED_KEY, true));
        ItemLore lore = sword.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        sword.set(DataComponents.LORE, lore.withLineAdded(
                Component.translatable("item.prismmod.prism_sword.reclaimed")
                        .withStyle(Style.EMPTY.withItalic(false).withColor(0xB06A8E))));
        player.setItemInHand(hand, ItemStack.EMPTY);

        server.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, 1.2F, 0.8F);
        server.playSound(null, pos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.BLOCKS, 0.4F, 0.6F);
        server.sendParticles(ParticleTypes.REVERSE_PORTAL,
                pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 40, 0.3, 0.3, 0.3, 0.05);

        popOut(server, pos, new ItemStack(Items.DRAGON_EGG), -0.06);
        popOut(server, pos, sword, 0.06);
        return InteractionResult.SUCCESS;
    }

    /** Spawns above the block, with a pickup delay so the player sees both come out. */
    private static void popOut(ServerLevel level, BlockPos pos, ItemStack stack, double sideways) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, stack);
        entity.setDeltaMovement(sideways, 0.25, 0.0);
        entity.setPickUpDelay(PICKUP_DELAY_TICKS);
        level.addFreshEntity(entity);
    }
}
