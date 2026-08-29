package net.tomi.prismmod.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.tomi.prismmod.PrismMod;
import net.tomi.prismmod.item.PrismSwordItem;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class PrismItems {

    /**
     * Tool tier for Prism gear.
     *
     * <p>attackDamageBonus is 0 so the whole number lives at the {@code sword(...)}
     * call site below — one place to tune, no hidden addition.
     */
    public static final ToolMaterial PRISM = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            64,   // durability: 64 hits before breaking
            9.0F, // mining speed, same as netherite
            0.0F, // attack damage bonus
            22,   // enchantment value, gold-tier so the table offers the good stuff
            PrismTags.PRISM_REPAIR_MATERIALS
    );

    public static final Item PRISM_INGOT = register("prism_ingot",
            props -> props.rarity(Rarity.RARE));

    /**
     * Attack damage shows as 20 in the tooltip: the player's base 1 plus this 19.
     * -2.4F attack speed matches every vanilla sword.
     */
    public static final Item PRISM_SWORD = register("prism_sword",
            props -> props.sword(PRISM, 19.0F, -2.4F).rarity(Rarity.EPIC),
            PrismSwordItem::new);

    public static final Item PRISM_BLOCK = registerBlockItem("prism_block", PrismBlocks.PRISM_BLOCK);

    static Item registerBlockItem(String name, Block block) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, PrismMod.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key,
                new BlockItem(block, new Item.Properties()
                        .useBlockDescriptionPrefix()
                        .rarity(Rarity.RARE)
                        .setId(key)));
    }

    static Item register(String name, UnaryOperator<Item.Properties> extra) {
        return register(name, extra, Item::new);
    }

    static Item register(String name, UnaryOperator<Item.Properties> extra,
                         Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, PrismMod.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key,
                factory.apply(extra.apply(new Item.Properties()).setId(key)));
    }

    public static void init() {
    }
}
