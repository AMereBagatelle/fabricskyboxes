package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import org.lwjgl.opengl.GL14;

import java.util.Arrays;
import java.util.function.Consumer;

public class Blender {
    public static final Blender DEFAULT = new Blender(false, -1, -1, -1, -1, -1, false, false, false, true);
    public static final Blender DECORATIONS = new Blender(true, 770, 1, 32774, 1, 0, false, false, false, true);

    public static Codec<Blender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("separateFunction", false).forGetter(Blender::isSeparateFunction),
            Codec.INT.optionalFieldOf("sourceFactor", -1).forGetter(Blender::getsFactor),
            Codec.INT.optionalFieldOf("destinationFactor", -1).forGetter(Blender::getdFactor),
            Codec.INT.optionalFieldOf("equation", -1).forGetter(Blender::getEquation),
            Codec.INT.optionalFieldOf("sourceFactorAlpha", -1).forGetter(Blender::getsFactor2),
            Codec.INT.optionalFieldOf("destinationFactorAlpha", -1).forGetter(Blender::getDestinationFactorAlpha),
            Codec.BOOL.optionalFieldOf("redAlphaEnabled", false).forGetter(Blender::isRedAlphaEnabled),
            Codec.BOOL.optionalFieldOf("greenAlphaEnabled", false).forGetter(Blender::isGreenAlphaEnabled),
            Codec.BOOL.optionalFieldOf("blueAlphaEnabled", false).forGetter(Blender::isBlueAlphaEnabled),
            Codec.BOOL.optionalFieldOf("alphaEnabled", true).forGetter(Blender::isAlphaEnabled)
    ).apply(instance, Blender::new));

    private final boolean separateFunction;

    private final int sFactor;
    private final int dFactor;
    private final int equation;

    private final int sourceFactorAlpha;
    private final int destinationFactorAlpha;

    private final boolean redAlphaEnabled;
    private final boolean greenAlphaEnabled;
    private final boolean blueAlphaEnabled;
    private final boolean alphaEnabled;

    private final Consumer<Float> blendFunc;

    public Blender(boolean separateFunction, int sFactor, int dFactor, int equation, int sourceFactorAlpha, int destinationFactorAlpha, boolean redAlphaEnabled, boolean greenAlphaEnabled, boolean blueAlphaEnabled, boolean alphaEnabled) {
        this.separateFunction = separateFunction;
        this.sFactor = sFactor;
        this.dFactor = dFactor;
        this.equation = equation;
        this.sourceFactorAlpha = sourceFactorAlpha;
        this.destinationFactorAlpha = destinationFactorAlpha;
        this.redAlphaEnabled = redAlphaEnabled;
        this.greenAlphaEnabled = greenAlphaEnabled;
        this.blueAlphaEnabled = blueAlphaEnabled;
        this.alphaEnabled = alphaEnabled;

        if ((this.separateFunction && this.isValidFactor(sFactor) && this.isValidFactor(dFactor) && this.isValidFactor(sourceFactorAlpha) && this.isValidFactor(destinationFactorAlpha) && this.isValidEquation(equation)) || (this.isValidFactor(sFactor) && this.isValidFactor(dFactor) && this.isValidEquation(equation))) {
            this.blendFunc = (alpha) -> {
                if (this.separateFunction) {
                    RenderSystem.blendFuncSeparate(this.sFactor, this.dFactor, this.sourceFactorAlpha, this.destinationFactorAlpha);
                } else {
                    RenderSystem.blendFunc(this.sFactor, this.dFactor);
                }
                RenderSystem.blendEquation(this.equation);
                RenderSystem.setShaderColor(this.redAlphaEnabled ? alpha : 1.0F, this.greenAlphaEnabled ? alpha : 1.0F, this.blueAlphaEnabled ? alpha : 1.0F, this.alphaEnabled ? alpha : 1.0F);
            };
        } else {
            this.blendFunc = (alpha) -> {
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            };
        }
    }

    public void applyBlendFunc(float alpha) {
        this.blendFunc.accept(alpha);
    }

    public boolean isSeparateFunction() {
        return separateFunction;
    }

    public int getsFactor() {
        return sFactor;
    }

    public int getdFactor() {
        return dFactor;
    }

    public int getEquation() {
        return equation;
    }

    public int getsFactor2() {
        return sourceFactorAlpha;
    }

    public int getDestinationFactorAlpha() {
        return destinationFactorAlpha;
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
