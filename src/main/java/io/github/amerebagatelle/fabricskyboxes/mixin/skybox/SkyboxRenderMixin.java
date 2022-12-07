package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SkyboxRenderMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Runnable runnable, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled()) {
            runnable.run();
            float total = skyboxManager.getTotalAlpha();
            skyboxManager.renderSkyboxes((WorldRendererAccess) this, matrices, matrix4f, tickDelta, this.client.gameRenderer.getCamera());
            if (total > SkyboxManager.MINIMUM_ALPHA) {
                ci.cancel();
            }
        }
    }
}
