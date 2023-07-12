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
    public static final Blender DEFAULT = new Blender(false, 770, 1, 32774, 0, 0, false, false, false, true);
    public static final Blender DECORATIONS = new Blender(true, 770, 1, 32774, 1, 0, false, false, false, true);

    public static Codec<Blender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("separateFunction", false).forGetter(Blender::isSeparateFunction),
            Codec.INT.optionalFieldOf("sourceFactor", 770).forGetter(Blender::getSourceFactor),
            Codec.INT.optionalFieldOf("destinationFactor", 1).forGetter(Blender::getDestinationFactor),
            Codec.INT.optionalFieldOf("equation", 32774).forGetter(Blender::getEquation),
            Codec.INT.optionalFieldOf("sourceFactorAlpha", 0).forGetter(Blender::getSourceFactorAlpha),
            Codec.INT.optionalFieldOf("destinationFactorAlpha", 0).forGetter(Blender::getDestinationFactorAlpha),
            Codec.BOOL.optionalFieldOf("redAlphaEnabled", false).forGetter(Blender::isRedAlphaEnabled),
            Codec.BOOL.optionalFieldOf("greenAlphaEnabled", false).forGetter(Blender::isGreenAlphaEnabled),
            Codec.BOOL.optionalFieldOf("blueAlphaEnabled", false).forGetter(Blender::isBlueAlphaEnabled),
            Codec.BOOL.optionalFieldOf("alphaEnabled", true).forGetter(Blender::isAlphaEnabled)
    ).apply(instance, Blender::new));

    private final boolean separateFunction;

    private final int sourceFactor;
    private final int destinationFactor;
    private final int equation;

    private final int sourceFactorAlpha;
    private final int destinationFactorAlpha;

    private final boolean redAlphaEnabled;
    private final boolean greenAlphaEnabled;
    private final boolean blueAlphaEnabled;
    private final boolean alphaEnabled;

    private final Consumer<Float> blendFunc;

    public Blender(boolean separateFunction, int sourceFactor, int destinationFactor, int equation, int sourceFactorAlpha, int destinationFactorAlpha, boolean redAlphaEnabled, boolean greenAlphaEnabled, boolean blueAlphaEnabled, boolean alphaEnabled) {
        this.separateFunction = separateFunction;
        this.sourceFactor = sourceFactor;
        this.destinationFactor = destinationFactor;
        this.equation = equation;
        this.sourceFactorAlpha = sourceFactorAlpha;
        this.destinationFactorAlpha = destinationFactorAlpha;
        this.redAlphaEnabled = redAlphaEnabled;
        this.greenAlphaEnabled = greenAlphaEnabled;
        this.blueAlphaEnabled = blueAlphaEnabled;
        this.alphaEnabled = alphaEnabled;

        if ((this.separateFunction && this.isValidFactor(sourceFactor) && this.isValidFactor(destinationFactor) && this.isValidFactor(sourceFactorAlpha) && this.isValidFactor(destinationFactorAlpha) && this.isValidEquation(equation)) || (this.isValidFactor(sourceFactor) && this.isValidFactor(destinationFactor) && this.isValidEquation(equation))) {
            this.blendFunc = (alpha) -> {
                if (this.separateFunction) {
                    RenderSystem.blendFuncSeparate(this.sourceFactor, this.destinationFactor, this.sourceFactorAlpha, this.destinationFactorAlpha);
                } else {
                    RenderSystem.blendFunc(this.sourceFactor, this.destinationFactor);
                }
                RenderSystem.blendEquation(this.equation);
                RenderSystem.setShaderColor(this.redAlphaEnabled ? alpha : 1.0F, this.greenAlphaEnabled ? alpha : 1.0F, this.blueAlphaEnabled ? alpha : 1.0F, this.alphaEnabled ? alpha : 1.0F);
            };
        } else {
            FabricSkyBoxesClient.getLogger().error("Invalid custom blender values!");
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

    public int getSourceFactor() {
        return sourceFactor;
    }

    public int getDestinationFactor() {
        return destinationFactor;
    }

    public int getEquation() {
        return equation;
    }

    public int getSourceFactorAlpha() {
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
