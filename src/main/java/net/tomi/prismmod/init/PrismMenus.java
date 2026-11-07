package net.tomi.prismmod.init;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.tomi.prismmod.PrismMod;
import net.tomi.prismmod.menu.DiamondGrinderMenu;

public class PrismMenus {

    /** Opening data is the grinder's position, so the client menu can find its block. */
    public static final MenuType<DiamondGrinderMenu> DIAMOND_GRINDER = Registry.register(
            BuiltInRegistries.MENU,
            PrismMod.id("diamond_grinder"),
            new ExtendedMenuType<>(DiamondGrinderMenu::new, BlockPos.STREAM_CODEC));

    public static void init() {
    }
}
