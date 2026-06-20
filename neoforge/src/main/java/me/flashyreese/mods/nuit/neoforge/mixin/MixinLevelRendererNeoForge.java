package me.flashyreese.mods.nuit.neoforge.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import me.flashyreese.mods.nuit.render.NuitSkyboxRenderHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.world.level.MoonPhase;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRendererNeoForge {
    @Unique
    private static float nuit$tickDelta;

    @Unique
    private boolean nuit$skipVanillaSky;

    @Inject(
            method = "addSkyPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/Camera;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
            at = @At("HEAD")
    )
    private void nuit$captureTickDelta(
            FrameGraphBuilder frameGraphBuilder,
            Camera camera,
            GpuBufferSlice fogParameters,
            CallbackInfo ci
    ) {
        nuit$tickDelta = camera.getPartialTickTime();
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Inject(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At("HEAD")
    )
    private void nuit$resetSkyReplacement(
            SkyRenderState skyRenderState,
            Matrix4f projectionMatrix,
            GpuBufferSlice fogParameters,
            SkyRenderer skyRenderer,
            CallbackInfo ci
    ) {
        this.nuit$skipVanillaSky = false;
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Inject(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFog(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
                    shift = At.Shift.AFTER,
                    remap = true
            )
    )
    private void nuit$renderCustomSkyboxes(
            SkyRenderState skyRenderState,
            Matrix4f projectionMatrix,
            GpuBufferSlice fogParameters,
            SkyRenderer skyRenderer,
            CallbackInfo ci
    ) {
        this.nuit$skipVanillaSky = NuitSkyboxRenderHooks.renderCustomSkyboxes(
                fogParameters,
                skyRenderer,
                nuit$tickDelta
        );
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Redirect(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V",
                    remap = true
            )
    )
    private void nuit$skipEndSky(SkyRenderer skyRenderer) {
        if (!this.nuit$skipVanillaSky) {
            skyRenderer.renderEndSky();
        }
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Redirect(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndFlash(Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
                    remap = true
            )
    )
    private void nuit$skipEndFlash(
            SkyRenderer skyRenderer,
            PoseStack poseStack,
            float intensity,
            float xAngle,
            float yAngle
    ) {
        if (!this.nuit$skipVanillaSky) {
            skyRenderer.renderEndFlash(poseStack, intensity, xAngle, yAngle);
        }
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Redirect(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(I)V",
                    remap = true
            )
    )
    private void nuit$skipSkyDisc(SkyRenderer skyRenderer, int color) {
        if (!this.nuit$skipVanillaSky) {
            skyRenderer.renderSkyDisc(color);
        }
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Redirect(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;FI)V",
                    remap = true
            )
    )
    private void nuit$skipSunriseAndSunset(
            SkyRenderer skyRenderer,
            PoseStack poseStack,
            float sunAngle,
            int color
    ) {
        if (!this.nuit$skipVanillaSky) {
            skyRenderer.renderSunriseAndSunset(poseStack, sunAngle, color);
        }
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Redirect(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V",
                    remap = true
            )
    )
    private void nuit$skipSunMoonAndStars(
            SkyRenderer skyRenderer,
            PoseStack poseStack,
            float sunAngle,
            float moonAngle,
            float starAngle,
            MoonPhase moonPhase,
            float rainBrightness,
            float starBrightness
    ) {
        if (!this.nuit$skipVanillaSky) {
            skyRenderer.renderSunMoonAndStars(
                    poseStack,
                    sunAngle,
                    moonAngle,
                    starAngle,
                    moonPhase,
                    rainBrightness,
                    starBrightness
            );
        }
    }

    @Dynamic("NeoForge replaces the vanilla sky pass body with this synthetic lambda")
    @Redirect(
            method = "lambda$addSkyPass$8(Lnet/minecraft/client/renderer/state/SkyRenderState;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/SkyRenderer;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc()V",
                    remap = true
            )
    )
    private void nuit$skipDarkDisc(SkyRenderer skyRenderer) {
        if (!this.nuit$skipVanillaSky) {
            skyRenderer.renderDarkDisc();
        }
    }
}
