package net.tomi.prismmod.init;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class PrismTabs {

    // The CreativeModeTabs constants are private in 26.1, so rebuild the keys here
    // rather than access-widening vanilla.
    private static final ResourceKey<CreativeModeTab> COMBAT =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("combat"));
    private static final ResourceKey<CreativeModeTab> INGREDIENTS =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("ingredients"));
    private static final ResourceKey<CreativeModeTab> BUILDING_BLOCKS =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("building_blocks"));

    public static void init() {
        CreativeModeTabEvents.modifyOutputEvent(COMBAT)
                .register(output -> output.accept(PrismItems.PRISM_SWORD));
        CreativeModeTabEvents.modifyOutputEvent(INGREDIENTS)
                .register(output -> output.accept(PrismItems.PRISM_INGOT));
        CreativeModeTabEvents.modifyOutputEvent(BUILDING_BLOCKS)
                .register(output -> output.accept(PrismItems.PRISM_BLOCK));
    }
}
