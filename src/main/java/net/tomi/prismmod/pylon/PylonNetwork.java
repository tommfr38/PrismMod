package net.tomi.prismmod.pylon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.tomi.prismmod.PrismMod;

import java.util.ArrayList;
import java.util.List;

/**
 * Every pylon placed in the world, across all dimensions, with its name. Saved with the
 * world, in the overworld's data so one list serves every dimension.
 */
public class PylonNetwork extends SavedData {

    public static final int MAX_PYLONS = 10;
    public static final int MAX_NAME_LENGTH = 24;

    public record Entry(ResourceKey<Level> dimension, BlockPos pos, String name) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(Entry::dimension),
                BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
                Codec.STRING.fieldOf("name").forGetter(Entry::name)
        ).apply(instance, Entry::new));
    }

    public static final Codec<PylonNetwork> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("pylons").forGetter(network -> network.entries)
    ).apply(instance, PylonNetwork::new));

    // A saved-data type needs a data fix type, and this is the vanilla one that stays out of
    // the way: its fixers only touch command storage, which this is not.
    public static final SavedDataType<PylonNetwork> TYPE = new SavedDataType<>(
            PrismMod.id("pylon_network"), PylonNetwork::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    private final List<Entry> entries;

    public PylonNetwork() {
        this.entries = new ArrayList<>();
    }

    private PylonNetwork(List<Entry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public static PylonNetwork get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public List<Entry> entries() {
        return List.copyOf(entries);
    }

    public int size() {
        return entries.size();
    }

    public boolean isFull() {
        return entries.size() >= MAX_PYLONS;
    }

    public Entry find(ResourceKey<Level> dimension, BlockPos pos) {
        for (Entry entry : entries) {
            if (entry.dimension().equals(dimension) && entry.pos().equals(pos)) {
                return entry;
            }
        }
        return null;
    }

    /** The first "Pylon n" not already taken. */
    public String defaultName() {
        for (int n = 1; ; n++) {
            String candidate = "Pylon " + n;
            if (entries.stream().noneMatch(entry -> entry.name().equals(candidate))) {
                return candidate;
            }
        }
    }

    /** Returns false if the network is full or the pylon is already in it. */
    public boolean add(ResourceKey<Level> dimension, BlockPos pos, String name) {
        if (isFull() || find(dimension, pos) != null) {
            return false;
        }
        entries.add(new Entry(dimension, pos.immutable(), name));
        setDirty();
        return true;
    }

    public void remove(ResourceKey<Level> dimension, BlockPos pos) {
        if (entries.removeIf(entry -> entry.dimension().equals(dimension) && entry.pos().equals(pos))) {
            setDirty();
        }
    }

    public void rename(ResourceKey<Level> dimension, BlockPos pos, String name) {
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (entry.dimension().equals(dimension) && entry.pos().equals(pos)) {
                entries.set(i, new Entry(dimension, entry.pos(), name));
                setDirty();
                return;
            }
        }
    }

    /** Trims, strips control characters and caps the length. Blank comes back blank. */
    public static String cleanName(String raw) {
        StringBuilder out = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (c >= ' ' && c != 127) {
                out.append(c);
            }
        }
        String name = out.toString().trim();
        return name.length() > MAX_NAME_LENGTH ? name.substring(0, MAX_NAME_LENGTH).trim() : name;
    }
}
