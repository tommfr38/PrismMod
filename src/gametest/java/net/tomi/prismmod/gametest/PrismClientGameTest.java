package net.tomi.prismmod.gametest;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.block.Blocks;

import java.lang.reflect.Field;

/**
 * Mines with the Prism Pickaxe's right click in a real client. Run with
 * {@code ./gradlew runClientGameTest}.
 */
public class PrismClientGameTest implements FabricClientGameTest {

    private static final int RIGHT_MOUSE = 1;

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            TestServerContext server = world.getServer();
            server.runCommand("gamemode survival @p");
            server.runCommand("fill -5 98 -5 5 99 5 minecraft:obsidian");
            server.runCommand("give @p prismmod:prism_pickaxe");
            server.runCommand("tp @p 0.5 100 0.5 0 90");
            context.waitTicks(20);

            // Mining the same block twice in a row must start a fresh break each time, and
            // leave no crack behind when the button is let go.
            for (int attempt = 1; attempt <= 2; attempt++) {
                context.getInput().holdMouse(RIGHT_MOUSE);
                context.waitTicks(15);
                check(context.computeOnClient(mc -> mc.gameMode.isDestroying()),
                        "attempt " + attempt + ": the break never started");
                context.getInput().releaseMouse(RIGHT_MOUSE);
                context.waitTicks(5);
                check(crackCount(context) == 0, "attempt " + attempt + ": a crack is left after releasing");
            }

            // Letting the crosshair slide off the block also clears it.
            context.getInput().holdMouse(RIGHT_MOUSE);
            context.waitTicks(10);
            server.runCommand("tp @p 0.5 100 0.5 0 -90");
            context.waitTicks(5);
            context.getInput().releaseMouse(RIGHT_MOUSE);
            context.waitTicks(5);
            check(crackCount(context) == 0, "a crack is left after looking away");

            // A finished break takes the 3x3 around it and nothing else.
            server.runCommand("fill -5 98 -5 5 99 5 minecraft:stone");
            server.runCommand("tp @p 0.5 100 0.5 0 90");
            context.waitTicks(10);
            context.getInput().holdMouse(RIGHT_MOUSE);
            context.waitFor(mc -> mc.level.getBlockState(new BlockPos(0, 99, 0)).isAir(), 100);
            context.getInput().releaseMouse(RIGHT_MOUSE);
            context.waitTicks(10);
            int broken = server.computeOnServer(s -> {
                int count = 0;
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (s.overworld().getBlockState(new BlockPos(x, 99, z)).isAir()) {
                            count++;
                        }
                    }
                }
                return count;
            });
            boolean outsideIntact = server.computeOnServer(s ->
                    s.overworld().getBlockState(new BlockPos(2, 99, 0)).is(Blocks.STONE)
                            && s.overworld().getBlockState(new BlockPos(0, 99, 2)).is(Blocks.STONE));
            check(broken == 9, "expected the 3x3 to break, " + broken + " of 9 did");
            check(outsideIntact, "blocks outside the 3x3 were broken");
            check(crackCount(context) == 0, "a crack is left after the break");
        }
    }

    private static void check(boolean ok, String message) {
        if (!ok) {
            throw new AssertionError(message);
        }
    }

    private static int crackCount(ClientGameTestContext context) {
        return context.computeOnClient(PrismClientGameTest::crackCount);
    }

    @SuppressWarnings("unchecked")
    private static int crackCount(Minecraft mc) {
        try {
            Field field = mc.levelRenderer.getClass().getDeclaredField("destroyingBlocks");
            field.setAccessible(true);
            return ((Int2ObjectMap<BlockDestructionProgress>) field.get(mc.levelRenderer)).size();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
