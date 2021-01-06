package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.lwjgl.opengl.GL14;

public class Blend {
    public static final Blend DEFAULT = new Blend(null, 0, 0);
    public static Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", null).forGetter(Blend::getType),
            Codec.INT.optionalFieldOf("sFactor", 0).forGetter(Blend::getSFactor),
            Codec.INT.optionalFieldOf("dFactor", 0).forGetter(Blend::getDFactor)
    ).apply(instance, Blend::new));
    private final String type;
    private final int sFactor;
    private final int dFactor;

    private final Runnable blendFunc;

    public Blend(String type, int sFactor, int dFactor) {
        this.type = type;
        this.sFactor = sFactor;
        this.dFactor = dFactor;

        if (type != null && !type.isEmpty()) {
            switch (type) {
                case "add":
                    blendFunc = () -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                    };
                    break;

                case "subtract":
                    blendFunc = () -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(GL14.GL_FUNC_SUBTRACT);
                    };
                    break;

                case "multiply":
                    blendFunc = () -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO);
                        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                    };
                    break;

                case "screen":
                    blendFunc = () -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                    };
                    break;

                case "replace":
                    blendFunc = () -> {
                        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
                    };
                    break;

                default:
                    throw new IllegalStateException("Blend mode is set to an invalid or unsupported value.");
            }
        } else if (sFactor != 0 && dFactor != 0) {
            blendFunc = () -> RenderSystem.blendFunc(sFactor, dFactor);
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
}
