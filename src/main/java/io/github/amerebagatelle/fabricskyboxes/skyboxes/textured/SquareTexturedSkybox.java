package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.DefaultProperties;
import io.github.amerebagatelle.fabricskyboxes.util.object.Textures;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;

public class SquareTexturedSkybox extends TexturedSkybox {
    public static Codec<SquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DefaultProperties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getDefaultProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.NO_CONDITIONS).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Codec.BOOL.fieldOf("blend").forGetter(TexturedSkybox::isBlend),
            Textures.CODEC.fieldOf("textures").forGetter(SquareTexturedSkybox::getTextures)
    ).apply(instance, SquareTexturedSkybox::new));
    public Textures textures;

    public SquareTexturedSkybox() {
    }

    public SquareTexturedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, boolean blend, Textures textures) {
        super(properties, conditions, decorations, blend);
        this.textures = textures;
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        TextureManager textureManager = worldRendererAccess.getTextureManager();

        textureManager.bindTexture(this.textures.getBottom());
        for (int i = 0; i < 6; ++i) {
            matrices.push();

            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west

            if (i == 1) {
                textureManager.bindTexture(this.textures.getNorth());
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            }

            if (i == 2) {
                textureManager.bindTexture(this.textures.getSouth());
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            }

            if (i == 3) {
                textureManager.bindTexture(this.textures.getTop());
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            }

            if (i == 4) {
                textureManager.bindTexture(this.textures.getEast());
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            }

            if (i == 5) {
                textureManager.bindTexture(this.textures.getWest());
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 1.0F).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(1.0F, 1.0F).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(1.0F, 0.0F).color(1f, 1f, 1f, alpha).next();
            tessellator.draw();
            matrices.pop();
        }
    }

    public Textures getTextures() {
        return this.textures;
    }
}
