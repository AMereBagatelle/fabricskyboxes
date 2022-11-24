package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SkyboxRenderMixin {
    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled()) {
            runnable.run();
            float total = skyboxManager.getTotalAlpha();
            skyboxManager.renderSkyboxes((WorldRendererAccess) this, matrices, matrix4f, tickDelta, camera, bl);
            if (total > SkyboxManager.MINIMUM_ALPHA) {
                ci.cancel();
            }
        }
    }
}
