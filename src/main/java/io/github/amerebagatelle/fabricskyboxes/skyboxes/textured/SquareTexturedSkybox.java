package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.stream.Collectors;

public class SquareTexturedSkybox extends TexturedSkybox {
    public static final Codec<SquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Fade.CODEC.fieldOf("fade").forGetter(AbstractSkybox::getFade),
            Utils.getClampedFloat(.0F, 1.0F).optionalFieldOf("maxAlpha", 1.0F).forGetter(AbstractSkybox::getMaxAlpha),
            Utils.getClampedFloat(.0F, 1.0F).optionalFieldOf("transitionSpeed", 1.0F).forGetter(AbstractSkybox::getTransitionSpeed),
            Codec.BOOL.optionalFieldOf("changeFog", false).forGetter(AbstractSkybox::isChangeFog),
            RGBA.CODEC.optionalFieldOf("fogColors", RGBA.ZERO).forGetter(AbstractSkybox::getFogColors),
            Codec.BOOL.optionalFieldOf("shouldRotate", false).forGetter(AbstractSkybox::isShouldRotate),
            Weather.CODEC.listOf().optionalFieldOf("weather", Lists.newArrayList(Weather.values())).forGetter((box) -> box.getWeather().stream().map(Weather::fromString).collect(Collectors.toList())),
            Identifier.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter(AbstractSkybox::getBiomes),
            Identifier.CODEC.listOf().optionalFieldOf("dimensions", ImmutableList.of()).forGetter(AbstractSkybox::getWorlds),
            HeightEntry.CODEC.listOf().optionalFieldOf("heightRanges", ImmutableList.of()).forGetter(AbstractSkybox::getHeightRanges),
            Textures.CODEC.fieldOf("textures").forGetter(SquareTexturedSkybox::getTextures),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.DEFAULT).forGetter(TexturedSkybox::getRotation),
            Codec.BOOL.fieldOf("blend").forGetter(TexturedSkybox::isBlend),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations)
    ).apply(instance, SquareTexturedSkybox::new));
    public Textures textures;

    public SquareTexturedSkybox() {
    }

    public SquareTexturedSkybox(Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean shouldRotate, List<Weather> weather, List<Identifier> biomes, List<Identifier> dimensions, List<HeightEntry> heightRanges, Textures textures, Rotation rotation, boolean blend, Decorations decorations) {
        super(fade, maxAlpha, transitionSpeed, changeFog, fogColors, shouldRotate, weather.stream().map(Weather::toString).collect(Collectors.toList()), biomes, dimensions, heightRanges, rotation, blend, decorations);
        this.textures = textures;
    }

    @Override
    public Codec<? extends AbstractSkybox> getCodec(int schemaVersion) {
        if (schemaVersion == 2) {
            return CODEC;
        }
        return null;
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

    @Override
    public String getType() {
        return "square-textured";
    }

    @Override
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        super.parseJson(jsonObjectWrapper);
        try {
            this.textures = new Textures(
                    jsonObjectWrapper.getJsonStringAsId("texture_north"),
                    jsonObjectWrapper.getJsonStringAsId("texture_south"),
                    jsonObjectWrapper.getJsonStringAsId("texture_east"),
                    jsonObjectWrapper.getJsonStringAsId("texture_west"),
                    jsonObjectWrapper.getJsonStringAsId("texture_top"),
                    jsonObjectWrapper.getJsonStringAsId("texture_bottom")
            );
        } catch (NullPointerException e) {
            throw new JsonParseException("Could not get a required field for skybox of type " + getType());
        }
    }

    public Textures getTextures() {
        return this.textures;
    }
}
