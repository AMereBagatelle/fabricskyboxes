package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import org.lwjgl.opengl.GL14;

import java.util.Arrays;
import java.util.function.Consumer;

public class Blend {
    public static final Blend DEFAULT = new Blend("", 0, 0, 0);
    public static Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "").forGetter(Blend::getType),
            Codec.INT.optionalFieldOf("sFactor", -1).forGetter(Blend::getSFactor),
            Codec.INT.optionalFieldOf("dFactor", -1).forGetter(Blend::getDFactor),
            Codec.INT.optionalFieldOf("equation", -1).forGetter(Blend::getEquation)
    ).apply(instance, Blend::new));
    private final String type;
    private final int sFactor;
    private final int dFactor;
    private final int equation;

    private final Consumer<Float> blendFunc;

    public Blend(String type, int sFactor, int dFactor, int equation) {
        this.type = type;
        this.sFactor = sFactor;
        this.dFactor = dFactor;
        this.equation = equation;

        if (!type.isEmpty()) {
            switch (type) {
                case "add":
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
                    };
                    break;
                case "subtract": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ZERO);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(alpha, alpha, alpha, 1.0F);
                    };
                    break;
                case "multiply": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(alpha, alpha, alpha, alpha);
                    };
                    break;
                case "screen": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(alpha, alpha, alpha, 1.0F);
                    };
                    break;
                case "replace": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
                    };
                    break;
                case "alpha": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
                    };
                    break;
                case "burn": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(alpha, alpha, alpha, 1.0F);
                    };
                    break;
                case "dodge": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(Equation.ADD.value);
                        RenderSystem.color4f(alpha, alpha, alpha, 1.0F);
                    };
                    break;
                case "darken": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(Equation.MIN.value);
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
                    };
                    break;
                case "lighten": 
                    blendFunc = (alpha) -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(Equation.MAX.value);
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
                    };
                    break;
                default:
                    FabricSkyBoxesClient.getLogger().error("Blend mode is set to an invalid or unsupported value.");
                    blendFunc = (alpha) -> {
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
                    };
                    break;
            }
        } else if (this.isValidFactor(sFactor) && this.isValidFactor(dFactor) && this.isValidEquation(equation)) {
            blendFunc = (alpha) -> {
                RenderSystem.blendFunc(sFactor, dFactor);
                RenderSystem.blendEquation(equation);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            };
        } else {
            blendFunc = (alpha) -> {
                RenderSystem.defaultBlendFunc();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            };
        }
    }

    public void applyBlendFunc(float alpha) {
        blendFunc.accept(alpha);
    }

    public String getType() {
        return type;
    }

    public int getSFactor() {
        return sFactor;
    }

    public int getDFactor() {
        return dFactor;
    }

    public int getEquation() {
        return equation;
    }

    public boolean isValidFactor(int factor) {
        return Arrays.stream(GlStateManager.SrcFactor.values()).filter(factor1 -> factor == factor1.field_22545).count() == 1;
    }

    public boolean isValidEquation(int equation) {
        return Arrays.stream(Equation.values()).filter(equation1 -> equation == equation1.value).count() == 1;
    }

    public enum Equation {
        ADD(GL14.GL_FUNC_ADD),
        SUBTRACT(GL14.GL_FUNC_SUBTRACT),
        REVERSE_SUBTRACT(GL14.GL_FUNC_REVERSE_SUBTRACT),
        MIN(GL14.GL_MIN),
        MAX(GL14.GL_MAX);

        public final int value;

        Equation(int value) {
            this.value = value;
        }
    }
}
