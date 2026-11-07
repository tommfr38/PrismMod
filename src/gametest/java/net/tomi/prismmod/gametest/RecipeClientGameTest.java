package net.tomi.prismmod.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.tomi.prismmod.init.PrismItems;

import java.util.ArrayList;
import java.util.List;

/** Crafts the pylon chain in the real recipe manager: core, shell, conductor, then the pylon. */
public class RecipeClientGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getServer().runCommand("give @p prismmod:pylon_core");
            world.getServer().runCommand("give @p prismmod:pylon_shell");
            world.getServer().runCommand("give @p prismmod:pylon_conductor");
            world.getServer().runCommand("give @p prismmod:pylon");
            context.waitTicks(10);
            context.takeScreenshot("pylon_items_hotbar");
            world.getServer().runOnServer(server -> {
                Item o = Items.OBSIDIAN;
                Item p = PrismItems.PRISM_GEM;
                Item n = Items.NETHER_STAR;
                expect(server, PrismItems.PYLON_CORE, o, p, o, o, n, o, o, p, o);

                Item d = PrismItems.DIAMOND_POWDER;
                expect(server, PrismItems.PYLON_SHELL, d, d, d, d, PrismItems.PRISM_GEM, d, d, d, d);
                // the old sea lantern recipe is gone
                CraftingInput oldShell = input(d, d, d, d, Items.SEA_LANTERN, d, d, d, d);
                if (server.overworld().recipeAccess().getRecipeFor(RecipeType.CRAFTING, oldShell, server.overworld()).isPresent()) {
                    throw new AssertionError("a sea lantern should no longer craft a pylon shell");
                }

                Item r = Items.REDSTONE;
                expect(server, PrismItems.PYLON_CONDUCTOR, r, r, r, r, p, r, r, r, r);
                // the ore block is not the gem and must not stand in for it
                CraftingInput withOre = input(r, r, r, r, PrismItems.PRISM_ORE, r, r, r, r);
                if (server.overworld().recipeAccess().getRecipeFor(RecipeType.CRAFTING, withOre, server.overworld()).isPresent()) {
                    throw new AssertionError("prism ore blocks should not craft a conductor");
                }

                Item s = PrismItems.PYLON_SHELL;
                Item c = PrismItems.PYLON_CONDUCTOR;
                Item k = PrismItems.PYLON_CORE;
                expect(server, PrismItems.PYLON, s, c, s, c, k, c, s, c, s);

                // a wrong arrangement must not make a pylon
                CraftingInput wrong = input(c, s, c, s, k, s, c, s, c);
                if (server.overworld().recipeAccess().getRecipeFor(RecipeType.CRAFTING, wrong, server.overworld()).isPresent()) {
                    throw new AssertionError("shells and conductors swapped should not craft a pylon");
                }
            });
        }
    }

    private static CraftingInput input(Item... items) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Item item : items) {
            stacks.add(new ItemStack(item));
        }
        return CraftingInput.of(3, 3, stacks);
    }

    private static void expect(net.minecraft.server.MinecraftServer server, Item result, Item... grid) {
        var level = server.overworld();
        var recipe = level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input(grid), level);
        if (recipe.isEmpty()) {
            throw new AssertionError("no recipe found for " + result);
        }
        ItemStack out = recipe.get().value().assemble(input(grid));
        if (!out.is(result)) {
            throw new AssertionError("expected " + result + " but the grid crafts " + out);
        }
    }
}
