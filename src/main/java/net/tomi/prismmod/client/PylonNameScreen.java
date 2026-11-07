package net.tomi.prismmod.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.tomi.prismmod.pylon.PylonNetwork;
import net.tomi.prismmod.pylon.PylonNetworking;

/**
 * The popup that appears when a pylon is placed. Closing it any way, including Escape,
 * keeps whatever is in the box, which starts as the suggested "Pylon n".
 */
public class PylonNameScreen extends Screen {

    private final BlockPos pos;
    private final String suggested;
    private EditBox nameBox;
    private boolean sent;

    public PylonNameScreen(BlockPos pos, String suggested) {
        super(Component.translatable("screen.prismmod.pylon_name"));
        this.pos = pos;
        this.suggested = suggested;
    }

    @Override
    protected void init() {
        nameBox = new EditBox(font, width / 2 - 100, height / 2 - 10, 200, 20, title);
        nameBox.setMaxLength(PylonNetwork.MAX_NAME_LENGTH);
        nameBox.setValue(suggested);
        addRenderableWidget(nameBox);
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> finish())
                .bounds(width / 2 - 50, height / 2 + 20, 100, 20).build());
        setInitialFocus(nameBox);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, height / 2 - 30, 0xFFFFFFFF);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isConfirmation()) {
            finish();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        finish();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void finish() {
        if (!sent) {
            sent = true;
            ClientPlayNetworking.send(new PylonNetworking.Rename(pos, nameBox.getValue()));
        }
        minecraft.setScreen(null);
    }
}
