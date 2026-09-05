package me.flashyreese.mods.nuit.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import me.flashyreese.mods.nuit.components.Blend;
import me.flashyreese.mods.nuit.components.Blender;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/** Shared render-state and immediate-draw helpers for Minecraft 1.21.1. */
@ApiStatus.Internal
public final class NuitRenderBackend {
    private NuitRenderBackend() {
    }

    public static TransformScope pushTransform(PoseStack poseStack) {
        return new TransformScope(poseStack);
    }

    public static void beginSkybox(Blend blend, float alpha, Supplier<ShaderInstance> shader) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(shader);
        blend.apply(alpha);
    }

    public static void endSkybox() {
        RenderSystem.depthMask(true);
        endBlend();
    }

    public static void endBlend() {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendEquation(Blender.Equation.ADD.value);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void draw(MeshData meshData, Supplier<ShaderInstance> shader) {
        configure(meshData, () -> RenderSystem.setShader(shader));
        BufferUploader.drawWithShader(meshData);
    }

    public static void drawTextured(
            MeshData meshData,
            Supplier<ShaderInstance> shader,
            ResourceLocation texture
    ) {
        configure(meshData, () -> {
            RenderSystem.setShader(shader);
            RenderSystem.setShaderTexture(0, texture);
        });
        BufferUploader.drawWithShader(meshData);
    }

    private static void configure(MeshData meshData, Runnable configuration) {
        try {
            configuration.run();
        } catch (RuntimeException | Error throwable) {
            try {
                meshData.close();
            } catch (RuntimeException | Error suppressed) {
                throwable.addSuppressed(suppressed);
            }
            throw throwable;
        }
    }

    /** Restores one pushed pose when closed. */
    public static final class TransformScope implements AutoCloseable {
        private final PoseStack poseStack;
        private boolean closed;

        private TransformScope(PoseStack poseStack) {
            this.poseStack = poseStack;
            this.poseStack.pushPose();
        }

        public PoseStack poseStack() {
            return this.poseStack;
        }

        @Override
        public void close() {
            if (!this.closed) {
                this.poseStack.popPose();
                this.closed = true;
            }
        }
    }
}
