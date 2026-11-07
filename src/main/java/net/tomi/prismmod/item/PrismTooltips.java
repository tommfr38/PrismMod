package net.tomi.prismmod.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * Description lines for Prism items. They live in the lang file as
 * {@code tooltip.prismmod.<item>.1}, {@code .2} and so on, and an item with none gets no
 * extra tooltip, so adding a description is only a lang entry.
 */
public class PrismTooltips {

    public static void add(ItemStack stack, Consumer<Component> out) {
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        Language language = Language.getInstance();
        for (int line = 1; ; line++) {
            String key = "tooltip.prismmod." + path + "." + line;
            if (!language.has(key)) {
                return;
            }
            out.accept(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }
}
