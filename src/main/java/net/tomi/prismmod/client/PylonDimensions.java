package net.tomi.prismmod.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Locale;

/** How a dimension is named and coloured in the pylon list, matching the pylon block colours. */
public class PylonDimensions {

    private static final int GREEN = 0x55FF55;
    private static final int RED = 0xFF5555;
    private static final int YELLOW = 0xFFFF55;
    private static final int PINK = 0xFF8CC8;

    public static Component label(Identifier dimension) {
        String text;
        if (dimension.equals(Level.OVERWORLD.identifier())) {
            text = "Overworld";
        } else if (dimension.equals(Level.NETHER.identifier())) {
            text = "The Nether";
        } else if (dimension.equals(Level.END.identifier())) {
            text = "The End";
        } else {
            text = titleCase(dimension.getPath());
        }
        return Component.literal(text).withColor(color(dimension));
    }

    private static int color(Identifier dimension) {
        if (dimension.equals(Level.OVERWORLD.identifier())) {
            return GREEN;
        }
        if (dimension.equals(Level.NETHER.identifier())) {
            return RED;
        }
        if (dimension.equals(Level.END.identifier())) {
            return YELLOW;
        }
        return PINK;
    }

    /** "twilight_forest" becomes "Twilight Forest". */
    private static String titleCase(String path) {
        StringBuilder out = new StringBuilder();
        for (String word : path.split("[_/]")) {
            if (!word.isEmpty()) {
                if (out.length() > 0) {
                    out.append(' ');
                }
                out.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
            }
        }
        return out.toString();
    }
}
