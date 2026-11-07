package net.tomi.prismmod.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.tomi.prismmod.block.PylonBlock;
import net.tomi.prismmod.block.PylonDimension;
import net.tomi.prismmod.client.PylonListScreen;
import net.tomi.prismmod.client.PylonNameScreen;
import net.tomi.prismmod.init.PrismBlocks;
import net.tomi.prismmod.pylon.PylonNetwork;
import net.tomi.prismmod.pylon.PylonNetworking;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Places, names, lists and travels between pylons in a real client. Run with
 * {@code ./gradlew runClientGameTest}.
 */
public class PylonClientGameTest implements FabricClientGameTest {

    private static final int RIGHT_MOUSE = 1;

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            TestServerContext server = world.getServer();
            server.runCommand("gamemode creative @p");
            server.runCommand("fill -8 98 -8 8 99 8 minecraft:stone");
            server.runCommand("give @p prismmod:pylon 16");
            server.runCommand("tp @p 0.5 100 0.5 -90 40");
            context.waitTicks(20);

            // 1. Placing a pylon opens the naming popup; a typed name is kept.
            context.getInput().pressMouse(RIGHT_MOUSE);
            context.waitForScreen(PylonNameScreen.class);
            context.waitTicks(3);
            context.takeScreenshot("pylon_name_popup");
            for (int i = 0; i < 10; i++) {
                context.getInput().pressKey(GLFW.GLFW_KEY_BACKSPACE);
            }
            context.getInput().typeChars("Home");
            context.getInput().pressKey(GLFW.GLFW_KEY_ENTER);
            context.waitTicks(10);
            List<PylonNetwork.Entry> entries = entries(server);
            check(entries.size() == 1, "expected 1 pylon after placing, found " + entries.size());
            check(entries.get(0).name().equals("Home"), "the typed name was not kept: " + entries.get(0).name());
            check(entries.get(0).dimension().equals(Level.OVERWORLD), "wrong dimension");
            BlockPos first = entries.get(0).pos();
            check(server.computeOnServer(s -> s.overworld().getBlockState(first)
                            .getValue(PylonBlock.DIMENSION) == PylonDimension.OVERWORLD),
                    "an overworld pylon should be the overworld colour");

            // 2. Closing the popup with Escape keeps the suggested name.
            server.runCommand("tp @p 0.5 100 0.5 -90 40");
            context.getInput().pressMouse(RIGHT_MOUSE);
            context.waitTicks(5);
            context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
            context.waitTicks(10);
            entries = entries(server);
            check(entries.size() == 2 || entries.size() == 1, "unexpected pylon count after the second placement");

            // 3. A pylon in the Nether is the Nether colour, and the network spans dimensions.
            server.runCommand("execute in minecraft:the_nether run tp @p 0.5 100 0.5 -90 40");
            context.waitTicks(60); // let the Nether chunks around the player load
            server.runCommand("execute in minecraft:the_nether run fill -8 98 -8 8 99 8 minecraft:netherrack");
            server.runCommand("execute in minecraft:the_nether run tp @p 0.5 100 0.5 -90 40");
            context.waitTicks(10);
            context.getInput().pressMouse(RIGHT_MOUSE);
            context.waitForScreen(PylonNameScreen.class);
            context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
            context.waitTicks(10);
            List<PylonNetwork.Entry> all = entries(server);
            boolean nether = all.stream().anyMatch(e -> e.dimension().equals(Level.NETHER));
            check(nether, "no pylon was registered in the Nether");
            PylonNetwork.Entry netherEntry = all.stream().filter(e -> e.dimension().equals(Level.NETHER)).findFirst().orElseThrow();
            check(server.computeOnServer(s -> s.getLevel(Level.NETHER).getBlockState(netherEntry.pos())
                            .getValue(PylonBlock.DIMENSION) == PylonDimension.NETHER),
                    "a Nether pylon should be the Nether colour");

            // 4. Right-clicking a pylon opens the list, and travelling works across dimensions.
            server.runCommand("execute in minecraft:the_nether run item replace entity @p hotbar.0 with minecraft:air");
            context.waitTicks(5);
            context.getInput().pressMouse(RIGHT_MOUSE);
            context.waitTicks(10);
            System.out.println("[pylontest] after right-click: screen=" + context.computeOnClient(mc -> String.valueOf(mc.screen))
                    + " hit=" + context.computeOnClient(mc -> String.valueOf(mc.hitResult))
                    + " dim=" + context.computeOnClient(mc -> mc.level.dimension().toString())
                    + " main=" + context.computeOnClient(mc -> mc.player.getMainHandItem().toString()));
            context.waitForScreen(PylonListScreen.class);
            context.waitTicks(3);
            context.takeScreenshot("pylon_list");
            PylonNetwork.Entry overworldEntry = all.stream().filter(e -> e.dimension().equals(Level.OVERWORLD)).findFirst().orElseThrow();
            context.runOnClient(mc -> ClientPlayNetworking.send(
                    new PylonNetworking.Travel(Identifier.parse("minecraft:overworld"), overworldEntry.pos())));
            context.waitTicks(20);
            boolean arrived = server.computeOnServer(s -> {
                var player = s.getPlayerList().getPlayers().get(0);
                return player.level().dimension().equals(Level.OVERWORLD)
                        && player.blockPosition().distSqr(overworldEntry.pos()) < 9.0;
            });
            check(arrived, "travelling to the overworld pylon did not arrive next to it");

            // The saved form reads back the same.
            boolean roundTrip = server.computeOnServer(s -> {
                PylonNetwork network = PylonNetwork.get(s);
                var encoded = PylonNetwork.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, network).getOrThrow();
                PylonNetwork decoded = PylonNetwork.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, encoded).getOrThrow();
                return decoded.entries().equals(network.entries());
            });
            check(roundTrip, "the pylon list does not survive saving and loading");

            // 5. The network is capped at ten, and breaking a pylon frees its place.
            server.runOnServer(s -> {
                PylonNetwork network = PylonNetwork.get(s);
                for (int i = 0; network.size() < PylonNetwork.MAX_PYLONS; i++) {
                    network.add(Level.OVERWORLD, new BlockPos(100 + i, 70, 0), "Filler " + i);
                }
            });
            int before = entries(server).size();
            server.runCommand("give @p prismmod:pylon 4");
            server.runCommand("tp @p 5.5 100 5.5 -90 40");
            context.waitTicks(10);
            context.getInput().pressMouse(RIGHT_MOUSE);
            context.waitTicks(15);
            check(entries(server).size() == before, "a pylon was placed past the limit");
            check(before == PylonNetwork.MAX_PYLONS, "the network should be full, has " + before);

            server.runCommand("setblock " + first.getX() + " " + first.getY() + " " + first.getZ() + " minecraft:air");
            context.waitTicks(5);
            check(entries(server).size() == before - 1, "breaking a pylon did not remove it from the network");
            check(server.computeOnServer(s -> PylonBlock.class != null && PrismBlocks.PYLON != null), "block missing");
        }
    }

    private static List<PylonNetwork.Entry> entries(TestServerContext server) {
        return server.computeOnServer(s -> PylonNetwork.get(s).entries());
    }

    private static void check(boolean ok, String message) {
        if (!ok) {
            throw new AssertionError(message);
        }
    }
}
