package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class AnimatedSquareTexturedSkybox extends SquareTexturedSkybox {
    public static final Codec<AnimatedSquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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
            Textures.CODEC.listOf().fieldOf("animationTextures").forGetter(AnimatedSquareTexturedSkybox::getTexturesList),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.DEFAULT).forGetter(TexturedSkybox::getRotation),
            Codec.BOOL.fieldOf("blend").forGetter(TexturedSkybox::isBlend),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Codec.FLOAT.fieldOf("framesPerSecond").forGetter(AnimatedSquareTexturedSkybox::getFramesPerSecond)
    ).apply(instance, AnimatedSquareTexturedSkybox::new));
    public List<Textures> texturesList;
    private float framesPerSecond;
    private long frameTimeMilliSeconds;
    private int count = 0;
    private long lastTime = 0L;

    public AnimatedSquareTexturedSkybox() {
    }

    public AnimatedSquareTexturedSkybox(Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean shouldRotate, List<Weather> weather, List<Identifier> biomes, List<Identifier> dimensions, List<HeightEntry> heightRanges, List<Textures> texturesList, Rotation rotation, boolean blend, Decorations decorations, float framesPerSecond) {
        super(fade, maxAlpha, transitionSpeed, changeFog, fogColors, shouldRotate, weather, biomes, dimensions, heightRanges, texturesList.get(0), rotation, blend, decorations);
        this.texturesList = texturesList;
        this.framesPerSecond = framesPerSecond;

        if (framesPerSecond > 0 && framesPerSecond <= 360)
            this.frameTimeMilliSeconds = (long) (1000F / framesPerSecond);
        else
            this.frameTimeMilliSeconds = 16L;
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        if (this.lastTime == 0L) this.lastTime = System.currentTimeMillis();
        this.textures = this.getTexturesList().get(this.count);

        super.renderSkybox(worldRendererAccess, matrices, tickDelta);

        if (System.currentTimeMillis() >= (this.lastTime + this.frameTimeMilliSeconds)) {
            if (this.count < this.getTexturesList().size()) {
                if (this.count + 1 == this.getTexturesList().size()) {
                    this.count = 0;
                } else {
                    this.count++;
                }
            }
            this.lastTime = System.currentTimeMillis();
        }
    }

    @Override
    public Codec<? extends AbstractSkybox> getCodec(int schemaVersion) {
        if (schemaVersion == 2) {
            return CODEC;
        }
        return null;
    }

    @Override
    public String getType() {
        return "animated-square-textured";
    }

    @Override
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        super.parseJson(jsonObjectWrapper);
    }

    public List<Textures> getTexturesList() {
        return this.texturesList;
    }

    public float getFramesPerSecond() {
        return this.framesPerSecond;
    }
}
