package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class SquareTexturedSkybox extends TexturedSkybox {
    public static Codec<SquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DefaultProperties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getDefaultProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.NO_CONDITIONS).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
            Textures.CODEC.fieldOf("textures").forGetter(s -> s.textures)
    ).apply(instance, SquareTexturedSkybox::new));
    public Textures textures;

    public SquareTexturedSkybox() {
    }

    public SquareTexturedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, Blend blend, Textures textures) {
        super(properties, conditions, decorations, blend);
        this.textures = textures;
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.SQUARE_TEXTURED_SKYBOX;
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

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
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            } else if (i == 2) {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            } else if (i == 3) {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            } else if (i == 4) {
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            } else if (i == 5) {
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(tex.getMinU(), tex.getMinV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(tex.getMinU(), tex.getMaxV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(tex.getMaxU(), tex.getMaxV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(tex.getMaxU(), tex.getMinV()).color(1f, 1f, 1f, alpha).next();
            tessellator.draw();
            matrices.pop();
        }
    }
}
