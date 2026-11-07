package net.tomi.prismmod;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.tomi.prismmod.init.PrismBlocks;
import net.tomi.prismmod.init.PrismItems;
import net.tomi.prismmod.init.PrismMenus;
import net.tomi.prismmod.init.PrismSounds;
import net.tomi.prismmod.init.PrismTabs;
import net.tomi.prismmod.pylon.PylonNetworking;
import org.slf4j.Logger;

public class PrismMod implements ModInitializer {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "prismmod";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    @Override
    public void onInitialize() {
        PrismSounds.init();
        PrismBlocks.init();
        PrismItems.init();
        PrismMenus.init();
        PrismTabs.init();
        PrismPowderMaking.init();
        PrismRightClicks.init();
        PrismPickaxeMining.init();
        PylonNetworking.init();
    }
}
