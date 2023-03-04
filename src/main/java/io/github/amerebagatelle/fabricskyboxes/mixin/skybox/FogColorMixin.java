package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.util.Constants;
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
    @Inject(method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/BackgroundRenderer;lastWaterFogColorUpdateTime:J", ordinal = 6))
    private static void modifyColors(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci) {
        Skybox skybox = SkyboxManager.getInstance().getCurrentSkybox();
        if (skybox instanceof FSBSkybox fsbSkybox && fsbSkybox.getAlpha() > Constants.MINIMUM_ALPHA && fsbSkybox.getProperties().isChangeFog()) {
            red = fsbSkybox.getProperties().getFogColors().getRed();
            green = fsbSkybox.getProperties().getFogColors().getGreen();
            blue = fsbSkybox.getProperties().getFogColors().getBlue();
        }
    }
}
