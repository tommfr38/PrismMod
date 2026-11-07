package net.tomi.prismmod.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.tomi.prismmod.PrismMod;
import net.tomi.prismmod.item.PrismPickaxeItem;
import net.tomi.prismmod.item.PrismPowderItem;
import net.tomi.prismmod.item.PrismSwordItem;
import net.tomi.prismmod.item.PylonBlockItem;
import net.tomi.prismmod.item.TooltipBlockItem;
import net.tomi.prismmod.item.TooltipItem;

import java.util.function.BiFunction;
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

    /**
     * The pickaxe's tier: netherite's numbers, with mining speed 5% up (9.0 to 9.45).
     */
    public static final ToolMaterial PRISM_PICKAXE_TIER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            512,   // deliberately below netherite (2031)
            9.45F, // netherite 9.0 x 1.05
            4.0F,  // netherite
            15,    // netherite
            PrismTags.PRISM_REPAIR_MATERIALS
    );

    public static final Item PRISM_GEM = register("prism",
            props -> props.rarity(Rarity.RARE));

    /**
     * Attack damage shows as 25 in the tooltip: the player's base 1 plus this 24.
     * -2.4F attack speed matches every vanilla sword.
     */
    public static final Item PRISM_SWORD = register("prism_sword",
            props -> props.sword(PRISM, 24.0F, -2.4F).rarity(Rarity.EPIC),
            PrismSwordItem::new);

    /** Matches a netherite pickaxe: 1 base + 4 tier + 1 = 6 damage, -2.8 attack speed. */
    public static final Item PRISM_PICKAXE = register("prism_pickaxe",
            props -> props.pickaxe(PRISM_PICKAXE_TIER, 1.0F, -2.8F).rarity(Rarity.EPIC).fireResistant(),
            PrismPickaxeItem::new);

    public static final Item PRISM_BLOCK = registerBlockItem("prism_block", PrismBlocks.PRISM_BLOCK);

    public static final Item PRISM_ORE = registerBlockItem("prism_ore", PrismBlocks.PRISM_ORE, Rarity.UNCOMMON);
    public static final Item DEEPSLATE_PRISM_ORE = registerBlockItem("deepslate_prism_ore", PrismBlocks.DEEPSLATE_PRISM_ORE, Rarity.UNCOMMON);
    public static final Item DIAMOND_GRINDER = registerBlockItem("diamond_grinder", PrismBlocks.DIAMOND_GRINDER, Rarity.UNCOMMON);

    public static final Item EGG_RECLAIMER = registerBlockItem("egg_reclaimer", PrismBlocks.EGG_RECLAIMER, Rarity.EPIC);

    public static final Item PYLON = registerBlockItem("pylon", PrismBlocks.PYLON, Rarity.RARE, PylonBlockItem::new);

    public static final Item PYLON_CORE = register("pylon_core", props -> props.rarity(Rarity.RARE), TooltipItem::new);
    public static final Item PYLON_SHELL = register("pylon_shell", props -> props, TooltipItem::new);
    public static final Item PYLON_CONDUCTOR = register("pylon_conductor", props -> props, TooltipItem::new);

    public static final Item DIAMOND_POWDER = register("diamond_powder", props -> props, TooltipItem::new);

    public static final Item PRISM_POWDER = register("prism_powder",
            props -> props.rarity(Rarity.RARE), PrismPowderItem::new);

    static Item registerBlockItem(String name, Block block) {
        return registerBlockItem(name, block, Rarity.RARE);
    }

    static Item registerBlockItem(String name, Block block, Rarity rarity) {
        return registerBlockItem(name, block, rarity, TooltipBlockItem::new);
    }

    static Item registerBlockItem(String name, Block block, Rarity rarity,
                                  BiFunction<Block, Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, PrismMod.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key,
                factory.apply(block, new Item.Properties()
                        .useBlockDescriptionPrefix()
                        .rarity(rarity)
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
