package io.github.amerebagatelle.mods.nuit.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.components.RGB;
import io.github.amerebagatelle.mods.nuit.skybox.decorations.DecorationBox;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {
    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;

    /**
     * Blends the active skyboxes into the fog color computed by Minecraft.
     */
    @Inject(
            method = "setupColor",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/FogRenderer;biomeChangedTime:J",
                    ordinal = 6
            )
    )
    private static void nuit$modifyColors(
            Camera camera,
            float tickDelta,
            ClientLevel level,
            int renderDistance,
            float skyDarken,
            CallbackInfo ci
    ) {
        RGB initialFogColor = new RGB(fogRed, fogGreen, fogBlue);
        RGB fogColor = Utils.alphaBlendFogColors(SkyboxManager.getInstance().getActiveSkyboxes(), initialFogColor);
        if (SkyboxManager.getInstance().isEnabled() && !fogColor.equals(initialFogColor)) {
            fogRed = fogColor.getRed();
            fogGreen = fogColor.getGreen();
            fogBlue = fogColor.getBlue();
        }
    }

    /**
     * Supplies the alpha overload directly when a skybox changes fog density. Injecting at the method head avoids an
     * unstable remap of RenderSystem's external helper while preserving vanilla's exact call when density is 1.
     */
    @Inject(method = "levelFogColor", at = @At("HEAD"), cancellable = true)
    private static void nuit$setFogDensity(CallbackInfo ci) {
        float initialFogDensity = 1.0F;
        float fogDensity = Utils.alphaBlendFogDensity(
                SkyboxManager.getInstance().getActiveSkyboxes(),
                initialFogDensity
        );
        if (SkyboxManager.getInstance().isEnabled() && fogDensity != initialFogDensity) {
            RenderSystem.setShaderFogColor(fogRed, fogGreen, fogBlue, fogDensity);
            ci.cancel();
        }
    }

    @Redirect(
            method = "setupColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"
            )
    )
    private static float nuit$redirectSkyAngle(ClientLevel level, float tickDelta) {
        if (nuit$usesUniformSkyboxRotation()) {
            return Mth.positiveModulo(level.getDayTime() / 24000F + 0.75F, 1.0F);
        }
        return level.getTimeOfDay(tickDelta);
    }

    @Redirect(
            method = "setupColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSunAngle(F)F"
            )
    )
    private static float nuit$redirectSkyAngleRadians(ClientLevel level, float tickDelta) {
        if (nuit$usesUniformSkyboxRotation()) {
            float skyAngle = Mth.positiveModulo(level.getDayTime() / 24000F + 0.75F, 1.0F);
            return skyAngle * Mth.TWO_PI;
        }
        return level.getSunAngle(tickDelta);
    }

    @ModifyConstant(
            method = "setupColor",
            slice = @Slice(from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"
            )),
            constant = @Constant(intValue = 4, ordinal = 0)
    )
    private static int nuit$renderSkyColor(int original) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        Skybox skybox = skyboxManager.getCurrentSkybox();
        if (skyboxManager.isEnabled()
                && skybox instanceof NuitSkybox nuitSkybox
                && !nuitSkybox.getProperties().renderSunSkyTint()) {
            return Integer.MAX_VALUE;
        }
        return original;
    }

    private static boolean nuit$usesUniformSkyboxRotation() {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        return skyboxManager.isEnabled() && skyboxManager.getActiveSkyboxes().stream().anyMatch(
                skybox -> skybox instanceof DecorationBox decorationBox
                        && decorationBox.getProperties().rotation().skyboxRotation()
        );
    }
}
