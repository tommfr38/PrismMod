package net.tomi.prismmod.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.tomi.prismmod.PrismMod;

public class PrismTags {

    /**
     * Anvil repair material for Prism gear. Backed by
     * data/prismmod/tags/item/prism_repair_materials.json.
     */
    public static final TagKey<Item> PRISM_REPAIR_MATERIALS =
            TagKey.create(Registries.ITEM, PrismMod.id("prism_repair_materials"));
}
