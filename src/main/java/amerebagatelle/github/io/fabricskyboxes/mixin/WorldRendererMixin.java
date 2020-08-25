package amerebagatelle.github.io.fabricskyboxes.mixin;

import amerebagatelle.github.io.fabricskyboxes.SkyboxManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        SkyboxManager.getInstance().renderSkyboxes((WorldRendererAccess) this, matrices, tickDelta);
        if (SkyboxManager.getInstance().getTotalAlpha() > 0.1) {
            ci.cancel();
        }
    }
}
