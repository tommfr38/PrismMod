package net.tomi.prismmod.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.tomi.prismmod.PrismMod;

public class PrismSounds {

    /**
     * Played when a pylon teleports a player. assets/prismmod/sounds.json points it at the
     * enderman teleport audio, but giving it its own event lets it carry its own subtitle.
     */
    public static final SoundEvent PYLON_ACTIVATE = register("pylon_activate");

    private static SoundEvent register(String name) {
        Identifier id = PrismMod.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() {
    }
}
