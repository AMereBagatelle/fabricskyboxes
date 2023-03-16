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
    public static final Blend DEFAULT = new Blend("", 0, 0, 0, false, false, false, true);
    public static Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "").forGetter(Blend::getType),
            Codec.INT.optionalFieldOf("sFactor", -1).forGetter(Blend::getSFactor),
            Codec.INT.optionalFieldOf("dFactor", -1).forGetter(Blend::getDFactor),
            Codec.INT.optionalFieldOf("equation", -1).forGetter(Blend::getEquation),
            Codec.BOOL.optionalFieldOf("redAlphaEnabled", false).forGetter(Blend::isRedAlphaEnabled),
            Codec.BOOL.optionalFieldOf("greenAlphaEnabled", false).forGetter(Blend::isGreenAlphaEnabled),
            Codec.BOOL.optionalFieldOf("blueAlphaEnabled", false).forGetter(Blend::isBlueAlphaEnabled),
            Codec.BOOL.optionalFieldOf("alphaEnabled", true).forGetter(Blend::isAlphaEnabled)
    ).apply(instance, Blend::new));
    private final String type;
    private final int sFactor;
    private final int dFactor;
    private final int equation;
    private final boolean redAlphaEnabled;
    private final boolean greenAlphaEnabled;
    private final boolean blueAlphaEnabled;
    private final boolean alphaEnabled;

    private final Consumer<Float> blendFunc;

    public Blend(String type, int sFactor, int dFactor, int equation, boolean redAlphaEnabled, boolean greenAlphaEnabled, boolean blueAlphaEnabled, boolean alphaEnabled) {
        this.type = type;
        this.sFactor = sFactor;
        this.dFactor = dFactor;
        this.equation = equation;
        this.redAlphaEnabled = redAlphaEnabled;
        this.greenAlphaEnabled = greenAlphaEnabled;
        this.blueAlphaEnabled = blueAlphaEnabled;
        this.alphaEnabled = alphaEnabled;

        if (!type.isEmpty()) {
            switch (type) {
                case "add" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                };
                case "subtract" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ZERO);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                case "multiply" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, alpha);
                };
                case "screen" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                case "replace" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                };
                case "alpha" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                };
                case "burn" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                case "dodge" -> blendFunc = (alpha) -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(Equation.ADD.value);
                    RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
                };
                default -> {
                    FabricSkyBoxesClient.getLogger().error("Blend mode is set to an invalid or unsupported value.");
                    blendFunc = (alpha) -> {
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                    };
                }
            }
        } else if (this.isValidFactor(sFactor) && this.isValidFactor(dFactor) && this.isValidEquation(equation)) {
            blendFunc = (alpha) -> {
                RenderSystem.blendFunc(sFactor, dFactor);
                RenderSystem.blendEquation(equation);
                RenderSystem.setShaderColor(redAlphaEnabled ? alpha : 1.0F, greenAlphaEnabled ? alpha : 1.0F, blueAlphaEnabled ? alpha : 1.0F, alphaEnabled ? alpha : 1.0F);
            };
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

    public int getSFactor() {
        return sFactor;
    }

    public int getDFactor() {
        return dFactor;
    }

    public int getEquation() {
        return equation;
    }

    public boolean isRedAlphaEnabled() {
        return redAlphaEnabled;
    }

    public boolean isGreenAlphaEnabled() {
        return greenAlphaEnabled;
    }

    public boolean isBlueAlphaEnabled() {
        return blueAlphaEnabled;
    }

    public boolean isAlphaEnabled() {
        return alphaEnabled;
    }

    public boolean isValidFactor(int factor) {
        return Arrays.stream(GlStateManager.SrcFactor.values()).filter(factor1 -> factor == factor1.value).count() == 1;
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
