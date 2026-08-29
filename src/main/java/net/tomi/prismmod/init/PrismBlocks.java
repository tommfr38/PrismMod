package net.tomi.prismmod.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.tomi.prismmod.PrismMod;

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

    private static ResourceKey<Block> key(String name) {
        return ResourceKey.create(Registries.BLOCK, PrismMod.id(name));
    }

    public static void init() {
    }
}
