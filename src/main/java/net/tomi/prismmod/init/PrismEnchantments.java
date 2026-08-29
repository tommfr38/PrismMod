package net.tomi.prismmod.init;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.tomi.prismmod.PrismMod;

public class PrismEnchantments {

    /**
     * Defined in data/prismmod/enchantment/prisms_power.json. The definition carries no
     * effects of its own — it exists to bind to the Prism Sword and be read here. The
     * launch itself is item code, because no enchantment effect component moves the player.
     */
    public static final ResourceKey<Enchantment> PRISMS_POWER =
            ResourceKey.create(Registries.ENCHANTMENT, PrismMod.id("prisms_power"));

    public static boolean hasPrismsPower(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Holder<Enchantment> held : enchantments.keySet()) {
            if (held.is(PRISMS_POWER)) {
                return true;
            }
        }
        return false;
    }
}
