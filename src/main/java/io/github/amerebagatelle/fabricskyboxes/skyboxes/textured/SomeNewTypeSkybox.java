package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
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
            Animation.CODEC.listOf().optionalFieldOf("animations", new ArrayList<>()).forGetter(SomeNewTypeSkybox::getAnimations)
    ).apply(instance, SomeNewTypeSkybox::new));
    protected final List<Animation> animations;
    private final UVRanges uvRanges;

    private final float quadSize = 100F;
    private final UVRange quad = new UVRange(-this.quadSize, -this.quadSize, this.quadSize, this.quadSize);

    public SomeNewTypeSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend, List<Animation> animations) {
        super(properties, conditions, decorations, blend);
        this.animations = animations;
        this.uvRanges = Util.make(() -> new UVRanges(
                new UVRange(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1),
                new UVRange(2.0F / 3.0F, 0, 1, 1.0F / 2.0F),
                new UVRange(2.0F / 3.0F, 1.0F / 2.0F, 1, 1),
                new UVRange(0, 1.0F / 2.0F, 1.0F / 3.0F, 1),
                new UVRange(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F),
                new UVRange(0, 0, 1.0F / 3.0F, 1.0F / 2.0F)
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

        for (int i = 0; i < 6; ++i) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west
            UVRange faceUVRange = this.uvRanges.byId(i);
            matrices.push();

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

            // animations
            for (Animation animation : this.animations) {
                UVRange intersect = Utils.calculateUVIntersection(faceUVRange, animation.getUvRanges()); // todo: cache this intersections so we don't waste gpu cycles
                if (intersect != null) {
                    UVRange intersectionOnCurrentTexture = Utils.mapUVRanges(faceUVRange, this.quad, intersect);
                    UVRange intersectionOnCurrentFrame = Utils.mapUVRanges(animation.getUvRanges(), animation.getCurrentFrame(), intersect);

                    // Render the quad at the calculated position
                    RenderSystem.setShaderTexture(0, animation.getTexture().getTextureId());
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMinU(), -this.quadSize, intersectionOnCurrentTexture.getMinV()).texture(intersectionOnCurrentFrame.getMinU(), intersectionOnCurrentFrame.getMinV()).color(1f, 1f, 1f, alpha).next();
                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMinU(), -this.quadSize, intersectionOnCurrentTexture.getMaxV()).texture(intersectionOnCurrentFrame.getMinU(), intersectionOnCurrentFrame.getMaxV()).color(1f, 1f, 1f, alpha).next();
                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMaxU(), -this.quadSize, intersectionOnCurrentTexture.getMaxV()).texture(intersectionOnCurrentFrame.getMaxU(), intersectionOnCurrentFrame.getMaxV()).color(1f, 1f, 1f, alpha).next();
                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMaxU(), -this.quadSize, intersectionOnCurrentTexture.getMinV()).texture(intersectionOnCurrentFrame.getMaxU(), intersectionOnCurrentFrame.getMinV()).color(1f, 1f, 1f, alpha).next();
                    tessellator.draw();
                }
            }

            matrices.pop();
        }
    }

    @Override
    public void tick(ClientWorld clientWorld) {
        super.tick(clientWorld); // Don't remove :)
        for (Animation animation : this.animations) {
            animation.tick(clientWorld.getTimeOfDay());
        }
    }

    public List<Animation> getAnimations() {
        return animations;
    }
}
