package net.tomi.prismmod.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tomi.prismmod.init.PrismEnchantments;

/**
 * The Prism Sword. An ordinary sword in every respect except one: with Prism's Power on
 * it, hold right-click to draw it back and release to be thrown in the direction you are
 * looking, the way a firework carries you under an elytra.
 *
 * <p>Every other enchantment works on it normally — the sword is in #minecraft:swords, so
 * Sharpness, Mending and the rest apply as they would to any sword. Prism's Power is the
 * one that will not go anywhere else, because its definition names this sword as the only
 * item it supports.
 */
public class PrismSwordItem extends Item {

    /** 3 seconds. */
    private static final int COOLDOWN_TICKS = 60;
    /** Boost speed. Riptide III is about 3.0. */
    private static final double BOOST_POWER = 3.0;
    /** Lift off the ground first, or the boost is swallowed by the block underfoot. */
    private static final double GROUND_CLEARANCE = 1.2;
    /** Quarter of a second: long enough to see the wind-up, short enough to feel instant. */
    private static final int WINDUP_TICKS = 5;
    private static final int USE_DURATION = 72000;

    public PrismSwordItem(Properties properties) {
        super(properties);
    }

    /** Drives the arm pose. The drawn-back sword itself is a model swap, in items/prism_sword.json. */
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Without the enchantment this is an ordinary sword.
        if (!PrismEnchantments.hasPrismsPower(stack) || player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (USE_DURATION - timeLeft < WINDUP_TICKS) {
            return false;
        }
        if (!PrismEnchantments.hasPrismsPower(stack) || player.getCooldowns().isOnCooldown(stack)) {
            return false;
        }

        // Deliberately unguarded by side. A player's own movement is driven by their
        // client, so a boost applied only on the server is overwritten immediately;
        // vanilla's riptide launch runs on both sides for the same reason.
        if (player.onGround()) {
            player.move(MoverType.SELF, new Vec3(0.0, GROUND_CLEARANCE, 0.0));
        }
        Vec3 look = player.getLookAngle();
        player.push(look.x * BOOST_POWER, look.y * BOOST_POWER, look.z * BOOST_POWER);

        // Same durability path as a swing, so Unbreaking gets its usual roll.
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        player.awardStat(Stats.ITEM_USED.get(this));

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
