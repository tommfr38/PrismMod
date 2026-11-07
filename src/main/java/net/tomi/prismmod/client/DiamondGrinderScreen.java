package net.tomi.prismmod.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.tomi.prismmod.PrismMod;
import net.tomi.prismmod.menu.DiamondGrinderMenu;

public class DiamondGrinderScreen extends AbstractContainerScreen<DiamondGrinderMenu> {

    private static final Identifier TEXTURE = PrismMod.id("textures/gui/container/diamond_grinder.png");

    public DiamondGrinderScreen(DiamondGrinderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F,
                imageWidth, imageHeight, 256, 256);
    }
}
