package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BackgroundRenderer.class)
public class SunSkyColorMixin {
    @ModifyConstant(
            method = "render",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;")),
            constant = @Constant(intValue = 4, ordinal = 0)
    )
    private static int renderSkyColor(int original) {
        Skybox skybox = SkyboxManager.getInstance().getCurrentSkybox();
        if (skybox instanceof FSBSkybox fsbSkybox) {
            if (!fsbSkybox.getProperties().isRenderSunSkyTint()) {
                return Integer.MAX_VALUE;
            }
        }
        return original;
    }
}
