package net.tomi.prismmod.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;

/** Which dimension a pylon was placed in, which decides the colour of the block. */
public enum PylonDimension implements StringRepresentable {
    /** Any dimension that is not one of the three vanilla ones: the Aether, Twilight Forest and so on. */
    OTHER("other"),
    OVERWORLD("overworld"),
    NETHER("nether"),
    END("end");

    private final String name;

    PylonDimension(String name) {
        this.name = name;
    }

    public static PylonDimension of(ResourceKey<Level> dimension) {
        if (dimension.equals(Level.OVERWORLD)) {
            return OVERWORLD;
        }
        if (dimension.equals(Level.NETHER)) {
            return NETHER;
        }
        if (dimension.equals(Level.END)) {
            return END;
        }
        return OTHER;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
