package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SkyboxRenderMixin {
    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled()) {
            float total = skyboxManager.getTotalAlpha();
            skyboxManager.renderSkyboxes((WorldRendererAccess) this, matrices, tickDelta);
            if (total > SkyboxManager.MINIMUM_ALPHA) {
                ci.cancel();
            }
        }
    }
}
