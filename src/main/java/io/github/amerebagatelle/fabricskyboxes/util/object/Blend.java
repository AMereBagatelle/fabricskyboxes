package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;

import java.util.function.Consumer;

public class Blend {
    public static final Blend DEFAULT = new Blend("", Blender.DEFAULT);
    public static final Blend DECORATIONS = new Blend("decorations", Blender.DECORATIONS);
    public static Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "").forGetter(Blend::getType),
            Blender.CODEC.optionalFieldOf("blender", Blender.DEFAULT).forGetter(Blend::getBlender)
    ).apply(instance, Blend::new));
    private final String type;
    private final Blender blender;

    private final Consumer<Float> blendFunc;

    public Blend(String type, Blender blender) {
        this.type = type;
        this.blender = blender;

        if (!type.isEmpty()) {
            switch (type) {
                case "add" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                };
                case "subtract" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ZERO);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                case "multiply" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, alpha);
                };
                case "screen" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                case "replace" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                };
                case "alpha" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                };
                case "burn" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                case "dodge" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(Blender.Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                case "disable" -> blendFunc = (alpha) -> {
                    RenderSystem.disableBlend();
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                };
                case "decorations" -> blendFunc = Blender.DECORATIONS::applyBlendFunc;
                case "custom" -> blendFunc = this.blender::applyBlendFunc;
                default -> {
                    FabricSkyBoxesClient.getLogger().error("Blend mode is set to an invalid or unsupported value.");
                    blendFunc = (alpha) -> {
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                    };
                }
            }
        } else {
            blendFunc = (alpha) -> {
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            };
        }
    }

    public void applyBlendFunc(float alpha) {
        blendFunc.accept(alpha);
    }

    public String getType() {
        return type;
    }

    public Blender getBlender() {
        return blender;
    }
}
