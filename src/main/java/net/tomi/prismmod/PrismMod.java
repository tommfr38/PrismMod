package net.tomi.prismmod;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.tomi.prismmod.init.PrismBlocks;
import net.tomi.prismmod.init.PrismItems;
import net.tomi.prismmod.init.PrismTabs;
import org.slf4j.Logger;

public class PrismMod implements ModInitializer {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "prismmod";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    @Override
    public void onInitialize() {
        PrismBlocks.init();
        PrismItems.init();
        PrismTabs.init();
        PrismPowderMaking.init();
    }
}
