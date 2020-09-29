package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class FogColorMixin {

    @Shadow
    private static float red;

    @Shadow
    private static float blue;

    @Shadow
    private static float green;

    /**
     * Checks if we should change the fog color to whatever the skybox set it to, and sets it.
     */
    @Inject(method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/BackgroundRenderer;lastWaterFogColorUpdateTime:J", ordinal = 5))
    private static void modifyColors(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci) {
        if (SkyboxManager.shouldChangeFog) {
            red = SkyboxManager.fogRed;
            blue = SkyboxManager.fogBlue;
            green = SkyboxManager.fogGreen;
            SkyboxManager.shouldChangeFog = false;
        }
    }
}
