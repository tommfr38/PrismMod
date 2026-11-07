package net.tomi.prismmod.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.tomi.prismmod.init.PrismBlocks;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Prism Pickaxe's right click treats different blocks. Plain blocks are mined. A block
 * with a right-click of its own (a chest, a furnace) keeps it, and crouching while right-clicking
 * mines it instead. Run with {@code ./gradlew runClientGameTest}.
 */
public class BlockVarietyGameTest implements FabricClientGameTest {

    private static final int RIGHT_MOUSE = 1;

    private static final String[] PLAIN = {
            "minecraft:dirt", "minecraft:oak_log", "minecraft:sand", "minecraft:glass", "minecraft:oak_leaves"
    };
    private static final String[] INTERACTIVE = {
            "minecraft:crafting_table", "minecraft:chest", "minecraft:furnace", "minecraft:barrel",
            "minecraft:anvil"
    };

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            TestServerContext server = world.getServer();
            server.runCommand("gamemode survival @p");
            server.runCommand("fill -6 90 -6 6 98 6 minecraft:stone");
            server.runCommand("give @p prismmod:prism_pickaxe");
            context.waitTicks(10);

            List<String> problems = new ArrayList<>();

            for (String block : PLAIN) {
                setUp(server, context, block);
                boolean ok = hold(context, server, false, 150);
                System.out.println("[varietytest] plain " + block + ": broken=" + ok);
                if (!ok) {
                    problems.add(block + " should be mined by a plain right-click");
                }
                closeScreen(context);
            }

            for (String block : INTERACTIVE) {
                setUp(server, context, block);
                boolean plainBroke = hold(context, server, false, 25);
                String screen = context.computeOnClient(mc -> String.valueOf(mc.screen));
                System.out.println("[varietytest] interactive " + block + " plain click: broken=" + plainBroke + " screen=" + screen);
                if (plainBroke) {
                    problems.add(block + " was broken by a plain right-click, it should keep its own");
                }
                closeScreen(context);

                setUp(server, context, block);
                boolean crouchBroke = hold(context, server, true, 150);
                System.out.println("[varietytest] interactive " + block + " crouch click: broken=" + crouchBroke);
                if (!crouchBroke) {
                    problems.add(block + " was not mined by crouch + right-click");
                }
                closeScreen(context);
            }

            // The Egg Reclaimer still reclaims, and a Pylon still opens its list.
            server.runCommand("fill -4 99 -4 4 99 4 minecraft:air");
            server.runCommand("setblock 0 99 0 prismmod:egg_reclaimer");
            server.runCommand("tp @p 0.5 100.0 0.5 0 90");
            context.waitTicks(8);
            context.getInput().pressMouse(RIGHT_MOUSE);
            context.waitTicks(15);
            boolean egg = server.computeOnServer(s -> !s.overworld().getEntitiesOfClass(ItemEntity.class,
                    new AABB(-3, 95, -3, 4, 104, 4), e -> e.getItem().is(Items.DRAGON_EGG)).isEmpty());
            boolean kept = isBlock(server, PrismBlocks.EGG_RECLAIMER);
            System.out.println("[varietytest] egg reclaimer: egg=" + egg + " kept=" + kept);
            if (!egg || !kept) {
                problems.add("egg reclaimer (egg=" + egg + ", kept=" + kept + ")");
            }

            server.runCommand("setblock 0 99 0 prismmod:pylon");
            server.runCommand("give @p prismmod:prism_pickaxe");
            context.waitTicks(5);
            context.getInput().pressMouse(RIGHT_MOUSE);
            context.waitTicks(15);
            String pylonScreen = context.computeOnClient(mc -> String.valueOf(mc.screen));
            boolean pylonKept = isBlock(server, PrismBlocks.PYLON);
            System.out.println("[varietytest] pylon: screen=" + pylonScreen + " kept=" + pylonKept);
            if (!pylonScreen.contains("PylonListScreen") || !pylonKept) {
                problems.add("pylon (screen=" + pylonScreen + ", kept=" + pylonKept + ")");
            }

            if (!problems.isEmpty()) {
                throw new AssertionError(problems.toString());
            }
        }
    }

    private static void setUp(TestServerContext server, ClientGameTestContext context, String block) {
        server.runCommand("fill -4 99 -4 4 99 4 minecraft:air");
        server.runCommand("fill -2 99 -2 2 99 2 " + block);
        server.runCommand("tp @p 0.5 100.0 0.5 0 90");
        context.waitTicks(8);
    }

    /** Holds right-click (and optionally crouch) until the centre block is gone or the time is up. */
    private static boolean hold(ClientGameTestContext context, TestServerContext server, boolean crouch, int maxTicks) {
        if (crouch) {
            context.getInput().holdShift();
            context.waitTicks(3); // crouch first, as a player does, so the click sees it
        }
        context.getInput().holdMouse(RIGHT_MOUSE);
        int waited = 0;
        while (waited < maxTicks && !centreBroken(server)) {
            context.waitTick();
            waited++;
        }
        context.getInput().releaseMouse(RIGHT_MOUSE);
        if (crouch) {
            context.getInput().releaseShift();
        }
        return centreBroken(server);
    }

    private static void closeScreen(ClientGameTestContext context) {
        context.runOnClient(mc -> mc.setScreen(null));
        context.waitTicks(5);
    }

    private static boolean centreBroken(TestServerContext server) {
        return server.computeOnServer(s -> s.overworld().getBlockState(new BlockPos(0, 99, 0)).isAir());
    }

    private static boolean isBlock(TestServerContext server, net.minecraft.world.level.block.Block block) {
        return server.computeOnServer(s -> s.overworld().getBlockState(new BlockPos(0, 99, 0)).is(block));
    }
}
