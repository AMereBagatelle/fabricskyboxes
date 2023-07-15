package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class SomeNewTypeSkybox extends TexturedSkybox {
    public static Codec<SomeNewTypeSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.DEFAULT).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
            Texture.CODEC.fieldOf("texture").forGetter(SomeNewTypeSkybox::getTexture),
            Animation.CODEC.listOf().optionalFieldOf("animations", new ArrayList<>()).forGetter(SomeNewTypeSkybox::getAnimations)
    ).apply(instance, SomeNewTypeSkybox::new));
    protected final Texture texture;
    protected final List<Animation> animations;
    private final Textures textures;

    public SomeNewTypeSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend, Texture texture, List<Animation> animations) {
        super(properties, conditions, decorations, blend);
        this.texture = texture;
        this.animations = animations;
        this.textures = Util.make(() -> new Textures(
                texture.withUV(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1),
                texture.withUV(2.0F / 3.0F, 0, 1, 1.0F / 2.0F),
                texture.withUV(2.0F / 3.0F, 1.0F / 2.0F, 1, 1),
                texture.withUV(0, 1.0F / 2.0F, 1.0F / 3.0F, 1),
                texture.withUV(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F),
                texture.withUV(0, 0, 1.0F / 3.0F, 1.0F / 2.0F)
        ));
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.SOME_NEW_TYPE_SKYBOX;
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, Camera camera, boolean thickFog) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        
        final float QUAD_SIZE = 100F;

        for (int i = 0; i < 6; ++i) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west
            Texture tex = this.textures.byId(i);
            matrices.push();

            RenderSystem.setShaderTexture(0, tex.getTextureId());

            if (i == 1) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            } else if (i == 2) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            } else if (i == 3) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            } else if (i == 4) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
            } else if (i == 5) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -QUAD_SIZE, -QUAD_SIZE, -QUAD_SIZE).texture(tex.getMinU(), tex.getMinV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, -QUAD_SIZE, -QUAD_SIZE, QUAD_SIZE).texture(tex.getMinU(), tex.getMaxV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, QUAD_SIZE, -QUAD_SIZE, QUAD_SIZE).texture(tex.getMaxU(), tex.getMaxV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, QUAD_SIZE, -QUAD_SIZE, -QUAD_SIZE).texture(tex.getMaxU(), tex.getMinV()).color(1f, 1f, 1f, alpha).next();
            tessellator.draw();

            // animations
            for (Animation animation : animations) {
                animation.tick(camera.getFocusedEntity().getWorld().getTimeOfDay()); // todo: we should have tick method for Skyboxes

                UVRanges intersect = this.calculateIntersectionRange(tex, animation.getUvRanges());
                if (intersect != null) {
                    float u1 = -QUAD_SIZE + (intersect.getMinU() - tex.getMinU()) / (tex.getMaxU() - tex.getMinU()) * (QUAD_SIZE + QUAD_SIZE);
                    float v1 = QUAD_SIZE + (intersect.getMinV() - tex.getMinV()) / (tex.getMaxV() - tex.getMinV()) * (-QUAD_SIZE - QUAD_SIZE);
                    float u2 = -QUAD_SIZE + (intersect.getMaxU() - tex.getMinU()) / (tex.getMaxU() - tex.getMinU()) * (QUAD_SIZE + QUAD_SIZE);
                    float v2 = QUAD_SIZE + (intersect.getMaxV() - tex.getMinV()) / (tex.getMaxV() - tex.getMinV()) * (-QUAD_SIZE - QUAD_SIZE);

                    // Render the quad at the calculated position
                    RenderSystem.setShaderTexture(0, animation.getTexture().getTextureId());
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                    bufferBuilder.vertex(matrix4f, u1, -QUAD_SIZE, v2).texture(animation.getCurrentFrame().getMinU(), animation.getCurrentFrame().getMinV()).color(1f, 1f, 1f, alpha).next();
                    bufferBuilder.vertex(matrix4f, u2, -QUAD_SIZE, v2).texture(animation.getCurrentFrame().getMinU(), animation.getCurrentFrame().getMaxV()).color(1f, 1f, 1f, alpha).next();
                    bufferBuilder.vertex(matrix4f, u2, -QUAD_SIZE, v1).texture(animation.getCurrentFrame().getMaxU(), animation.getCurrentFrame().getMaxV()).color(1f, 1f, 1f, alpha).next();
                    bufferBuilder.vertex(matrix4f, u1, -QUAD_SIZE, v1).texture(animation.getCurrentFrame().getMaxU(), animation.getCurrentFrame().getMinV()).color(1f, 1f, 1f, alpha).next();
                    tessellator.draw();
                }
            }

            matrices.pop();
        }
    }

    public UVRanges calculateIntersectionRange(UVRanges faceUvRange, UVRanges replacement) {
        float intersectionMinU = Math.max(faceUvRange.getMinU(), replacement.getMinU());
        float intersectionMaxU = Math.min(faceUvRange.getMaxU(), replacement.getMaxU());
        float intersectionMinV = Math.max(faceUvRange.getMinV(), replacement.getMinV());
        float intersectionMaxV = Math.min(faceUvRange.getMaxV(), replacement.getMaxV());

        if (intersectionMaxU >= intersectionMinU && intersectionMaxV >= intersectionMinV) {
            return new UVRanges(intersectionMinU, intersectionMinV, intersectionMaxU, intersectionMaxV);
        } else {
            // No intersection
            return null;
        }
    }

    public Texture getTexture() {
        return this.texture;
    }

    public List<Animation> getAnimations() {
        return animations;
    }
}
