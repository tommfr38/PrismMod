package net.tomi.prismmod.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.tomi.prismmod.pylon.PylonNetwork;
import net.tomi.prismmod.pylon.PylonNetworking;

import java.util.List;

/** Every active pylon with its dimension. Pick one to travel to it. */
public class PylonListScreen extends Screen {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP = 4;
    /** Below this window height the list is split into two columns so ten pylons still fit. */
    private static final int TWO_COLUMN_HEIGHT = 320;

    private final PylonNetworking.OpenList data;
    private int listTop;

    public PylonListScreen(PylonNetworking.OpenList data) {
        super(Component.translatable("screen.prismmod.pylon_list"));
        this.data = data;
    }

    @Override
    protected void init() {
        List<PylonNetworking.ListEntry> entries = data.entries();
        int columns = height < TWO_COLUMN_HEIGHT ? 2 : 1;
        int rows = Math.max(1, (entries.size() + columns - 1) / columns);
        int listWidth = columns * BUTTON_WIDTH + (columns - 1) * GAP;
        int listHeight = rows * (BUTTON_HEIGHT + GAP) - GAP;
        int left = (width - listWidth) / 2;
        listTop = (height - listHeight) / 2 + 8;

        for (int i = 0; i < entries.size(); i++) {
            PylonNetworking.ListEntry entry = entries.get(i);
            int column = i / rows;
            int row = i % rows;
            boolean here = entry.dimension().equals(data.hereDimension()) && entry.pos().equals(data.here());

            Component text = Component.literal(entry.name()).append(Component.literal(" - "))
                    .append(PylonDimensions.label(entry.dimension()));
            if (here) {
                text = text.copy().append(Component.translatable("screen.prismmod.pylon_here"));
            }
            Button button = Button.builder(text, b -> travel(entry))
                    .bounds(left + column * (BUTTON_WIDTH + GAP), listTop + row * (BUTTON_HEIGHT + GAP),
                            BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();
            button.active = !here;
            addRenderableWidget(button);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, listTop - 22, 0xFFFFFFFF);
        graphics.centeredText(font, Component.translatable("screen.prismmod.pylon_count",
                data.entries().size(), PylonNetwork.MAX_PYLONS), width / 2, listTop - 11, 0xFFAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void travel(PylonNetworking.ListEntry entry) {
        ClientPlayNetworking.send(new PylonNetworking.Travel(entry.dimension(), entry.pos()));
        minecraft.setScreen(null);
    }
}
