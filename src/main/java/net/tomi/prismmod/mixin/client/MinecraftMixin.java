package net.tomi.prismmod.mixin.client;

import net.minecraft.client.Minecraft;
import net.tomi.prismmod.client.PrismModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    /**
     * Vanilla calls this every tick with "is the attack button held". Holding right-click
     * over a block with a Prism Pickaxe counts as holding attack, so the block is mined
     * with the normal break time and the normal cracking animation.
     */
    @ModifyVariable(method = "continueAttack", at = @At("HEAD"), argsOnly = true)
    private boolean prismmod$rightClickMines(boolean attackHeld) {
        return PrismModClient.rightClickMining((Minecraft) (Object) this) || attackHeld;
    }
}
