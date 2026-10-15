package net.tomi.prismmod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.tomi.prismmod.init.PrismItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Prism Powder is made inside a bundle: put {@link #POWDER_NEEDED} Diamond Powder and a
 * Pink Dye into any bundle and it turns into Prism Powder by itself.
 *
 * <p>There is no hook for "something went into a bundle", and a crafting grid cannot hold
 * fifty of anything, so players' bundles are checked a few times a second instead. Anything
 * in the bundle beyond the powder and the dye is handed back.
 */
public class PrismPowderMaking {

    public static final int POWDER_NEEDED = 50;
    private static final int CHECK_INTERVAL = 5;

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % CHECK_INTERVAL != 0) {
                return;
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                check(player);
            }
        });
    }

    private static void check(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(ItemTags.BUNDLES) && convert(player, stack)) {
                inventory.setItem(i, new ItemStack(PrismItems.PRISM_POWDER));
            }
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.is(ItemTags.BUNDLES) && convert(player, carried)) {
            player.containerMenu.setCarried(new ItemStack(PrismItems.PRISM_POWDER));
        }
    }

    /** Returns true if the bundle held a full recipe; leftovers have already been returned. */
    private static boolean convert(ServerPlayer player, ItemStack bundle) {
        BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return false;
        }
        int powder = 0;
        int dye = 0;
        for (ItemStack s : (Iterable<ItemStack>) contents.itemCopyStream()::iterator) {
            if (s.is(PrismItems.DIAMOND_POWDER)) {
                powder += s.getCount();
            } else if (s.is(Items.PINK_DYE)) {
                dye += s.getCount();
            }
        }
        if (powder < POWDER_NEEDED || dye < 1) {
            return false;
        }
        int powderToSkip = POWDER_NEEDED;
        int dyeToSkip = 1;
        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStack s : (Iterable<ItemStack>) contents.itemCopyStream()::iterator) {
            if (s.is(PrismItems.DIAMOND_POWDER)) {
                int used = Math.min(powderToSkip, s.getCount());
                powderToSkip -= used;
                s.shrink(used);
            } else if (s.is(Items.PINK_DYE)) {
                int used = Math.min(dyeToSkip, s.getCount());
                dyeToSkip -= used;
                s.shrink(used);
            }
            if (!s.isEmpty()) {
                leftovers.add(s);
            }
        }
        for (ItemStack s : leftovers) {
            if (!player.getInventory().add(s)) {
                player.drop(s, false);
            }
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 1.0F, 1.2F);
        return true;
    }
}
