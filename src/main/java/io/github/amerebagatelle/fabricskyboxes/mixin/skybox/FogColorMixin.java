package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.FogRGBA;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class FogColorMixin {

    @Unique
    private static float density;

    @Unique
    private static boolean modifyDensity;

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
        FogRGBA fogColor = Utils.alphaBlendFogColors(SkyboxManager.getInstance().getActiveSkyboxes(), new RGBA(red, green, blue));
        if (SkyboxManager.getInstance().isEnabled() && fogColor != null) {
            red = fogColor.getRed();
            green = fogColor.getGreen();
            blue = fogColor.getBlue();
            density = fogColor.getDensity();
            modifyDensity = true;
        } else {
            modifyDensity = false;
        }
    }

    @Redirect(method = "setFogBlack", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogColor(FFF)V"))
    private static void redirectSetShaderFogColor(float red, float green, float blue) {
        if (modifyDensity) {
            RenderSystem.setShaderFogColor(red, green, blue, density);
        } else {
            RenderSystem.setShaderFogColor(red, green, blue);
        }
    }
}
