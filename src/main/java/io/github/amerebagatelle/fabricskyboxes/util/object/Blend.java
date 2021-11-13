package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import org.lwjgl.opengl.GL14;

public class Blend {
    public static final Blend DEFAULT = new Blend("", 0, 0, 0);
    public static Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "").forGetter(Blend::getType),
            Codec.INT.optionalFieldOf("sFactor", 0).forGetter(Blend::getSFactor),
            Codec.INT.optionalFieldOf("dFactor", 0).forGetter(Blend::getDFactor),
            Codec.INT.optionalFieldOf("equation", 0).forGetter(Blend::getEquation)
    ).apply(instance, Blend::new));
    private final String type;
    private final int sFactor;
    private final int dFactor;
    private final int equation;

    private final Runnable blendFunc;

    public Blend(String type, int sFactor, int dFactor, int equation) {
        this.type = type;
        this.sFactor = sFactor;
        this.dFactor = dFactor;
        this.equation = equation;

        if (!type.isEmpty()) {
            switch (type) {
                case "add" -> blendFunc = () -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                };
                case "subtract" -> blendFunc = () -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(GL14.GL_FUNC_SUBTRACT);
                };
                case "multiply" -> blendFunc = () -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO);
                    RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                };
                case "screen" -> blendFunc = () -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                    RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                };
                case "replace" -> blendFunc = () -> {
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                    RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                };
                default -> {
                    FabricSkyBoxesClient.getLogger().error("Blend mode is set to an invalid or unsupported value.");
                    blendFunc = RenderSystem::defaultBlendFunc;
                }
            }
        } else if (sFactor != 0 && dFactor != 0) {
            blendFunc = () -> {
                RenderSystem.blendFunc(sFactor, dFactor);
                RenderSystem.blendEquation(equation);
            };
        } else {
            blendFunc = RenderSystem::defaultBlendFunc;
        }
    }

    public void applyBlendFunc() {
        blendFunc.run();
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
}
