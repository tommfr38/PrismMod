package net.tomi.prismmod.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.tomi.prismmod.PrismMod;
import net.tomi.prismmod.block.DiamondGrinderBlock;

public class PrismBlocks {

    public static final ResourceKey<Block> PRISM_BLOCK_KEY = key("prism_block");

    public static final Block PRISM_BLOCK = Registry.register(
            BuiltInRegistries.BLOCK,
            PRISM_BLOCK_KEY,
            new Block(BlockBehaviour.Properties.of()
                    .mapColor(DyeColor.MAGENTA)
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .setId(PRISM_BLOCK_KEY)));

    public static final ResourceKey<Block> PRISM_ORE_KEY = key("prism_ore");

    public static final Block PRISM_ORE = Registry.register(
            BuiltInRegistries.BLOCK,
            PRISM_ORE_KEY,
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .mapColor(DyeColor.PINK)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .setId(PRISM_ORE_KEY)));

    public static final ResourceKey<Block> DEEPSLATE_PRISM_ORE_KEY = key("deepslate_prism_ore");

    public static final Block DEEPSLATE_PRISM_ORE = Registry.register(
            BuiltInRegistries.BLOCK,
            DEEPSLATE_PRISM_ORE_KEY,
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .mapColor(DyeColor.PINK)
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)
                    .setId(DEEPSLATE_PRISM_ORE_KEY)));

    public static final ResourceKey<Block> DIAMOND_GRINDER_KEY = key("diamond_grinder");

    public static final Block DIAMOND_GRINDER = Registry.register(
            BuiltInRegistries.BLOCK,
            DIAMOND_GRINDER_KEY,
            new DiamondGrinderBlock(BlockBehaviour.Properties.of()
                    .mapColor(DyeColor.BLACK)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
                    .setId(DIAMOND_GRINDER_KEY)));

    private static ResourceKey<Block> key(String name) {
        return ResourceKey.create(Registries.BLOCK, PrismMod.id(name));
    }

    public static void init() {
    }
}
