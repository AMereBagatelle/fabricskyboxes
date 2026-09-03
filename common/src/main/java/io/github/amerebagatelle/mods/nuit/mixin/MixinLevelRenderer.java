package io.github.amerebagatelle.mods.nuit.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.material.FogType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer {
    @Shadow
    protected abstract boolean doesMobEffectBlockSky(Camera camera);

    /**
     * Replaces the vanilla 1.21.1 sky only when Nuit has active skyboxes.
     */
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void nuit$renderCustomSkyboxes(
            Matrix4f modelViewMatrix,
            Matrix4f projectionMatrix,
            float tickDelta,
            Camera camera,
            boolean thickFog,
            Runnable fogCallback,
            CallbackInfo ci
    ) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (!skyboxManager.isEnabled() || skyboxManager.getActiveSkyboxes().isEmpty()) {
            return;
        }

        FogType cameraSubmersionType = camera.getFluidInCamera();
        boolean renderSky = !thickFog
                && cameraSubmersionType != FogType.POWDER_SNOW
                && cameraSubmersionType != FogType.LAVA
                && cameraSubmersionType != FogType.WATER
                && !this.doesMobEffectBlockSky(camera);
        if (renderSky) {
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(modelViewMatrix);
            skyboxManager.renderSkyboxes(
                    (SkyRendererAccessor) this,
                    poseStack,
                    projectionMatrix,
                    tickDelta,
                    camera,
                    thickFog,
                    fogCallback
            );
        }
        ci.cancel();
    }
}
