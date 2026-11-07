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

    private static final ResourceKey<CreativeModeTab> TOOLS =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("tools_and_utilities"));
    private static final ResourceKey<CreativeModeTab> NATURAL =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("natural_blocks"));
    private static final ResourceKey<CreativeModeTab> FUNCTIONAL =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("functional_blocks"));

    public static void init() {
        CreativeModeTabEvents.modifyOutputEvent(NATURAL).register(output -> {
            output.accept(PrismItems.PRISM_ORE);
            output.accept(PrismItems.DEEPSLATE_PRISM_ORE);
        });
        CreativeModeTabEvents.modifyOutputEvent(FUNCTIONAL)
                .register(output -> {
                    output.accept(PrismItems.DIAMOND_GRINDER);
                    output.accept(PrismItems.EGG_RECLAIMER);
                    output.accept(PrismItems.PYLON);
                });
        CreativeModeTabEvents.modifyOutputEvent(TOOLS)
                .register(output -> output.accept(PrismItems.PRISM_PICKAXE));
        CreativeModeTabEvents.modifyOutputEvent(COMBAT)
                .register(output -> output.accept(PrismItems.PRISM_SWORD));
        CreativeModeTabEvents.modifyOutputEvent(INGREDIENTS)
                .register(output -> {
                    output.accept(PrismItems.PRISM_GEM);
                    output.accept(PrismItems.DIAMOND_POWDER);
                    output.accept(PrismItems.PRISM_POWDER);
                    output.accept(PrismItems.PYLON_CORE);
                    output.accept(PrismItems.PYLON_SHELL);
                    output.accept(PrismItems.PYLON_CONDUCTOR);
                });
        CreativeModeTabEvents.modifyOutputEvent(BUILDING_BLOCKS)
                .register(output -> output.accept(PrismItems.PRISM_BLOCK));
    }
}
