package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.Fade;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.HeightEntry;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.RGBA;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.Textures;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.Weather;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class TexturedSkybox extends AbstractSkybox {
    public static final Codec<TexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Fade.CODEC.fieldOf("fade").forGetter((box) -> box.fade),
            Codec.FLOAT.xmap(f -> MathHelper.clamp(f, .0F, 1.0F), Function.identity()).optionalFieldOf("maxAlpha", 1.0F).forGetter((box) -> box.maxAlpha),
            Codec.FLOAT.xmap(f -> MathHelper.clamp(f, .0F, 1.0F), Function.identity()).optionalFieldOf("transitionSpeed", 1.0F).forGetter((box) -> box.transitionSpeed),
            Codec.BOOL.optionalFieldOf("changeFog", false).forGetter((box) -> box.changeFog),
            RGBA.CODEC.optionalFieldOf("fogColors", RGBA.ZERO).forGetter(box -> box.fogColors),
            Codec.BOOL.optionalFieldOf("shouldRotate", false).forGetter((box) -> box.shouldRotate),
            Codec.BOOL.optionalFieldOf("decorations", false).forGetter((box) -> box.decorations),
            Weather.CODEC.listOf().optionalFieldOf("weather", Lists.newArrayList(Weather.values())).forGetter((box) -> box.weather.stream().map(Weather::fromString).collect(Collectors.toList())),
            Identifier.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter((box) -> box.biomes),
            Identifier.CODEC.listOf().optionalFieldOf("dimensions", ImmutableList.of()).forGetter((box) -> box.dimensions),
            HeightEntry.CODEC.listOf().optionalFieldOf("heightRanges", ImmutableList.of()).forGetter((box) -> box.heightRanges),
            Textures.CODEC.fieldOf("textures").forGetter(TexturedSkybox::getTextures),
            Codec.FLOAT.listOf().fieldOf("axis").forGetter(box -> box.axis)
    ).apply(instance, TexturedSkybox::new));

    public Textures textures;

    public List<Float> axis;

    public TexturedSkybox(Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean shouldRotate, boolean decorations, List<Weather> weather, List<Identifier> biomes, List<Identifier> dimensions, List<HeightEntry> heightRanges, Textures textures, List<Float> axis) {
        super(fade, maxAlpha, transitionSpeed, changeFog, fogColors, shouldRotate, decorations, weather.stream().map(Weather::toString).collect(Collectors.toList()), biomes, dimensions, heightRanges);
        this.textures = textures;
        this.axis = axis;
    }

    public TexturedSkybox(Textures textures, List<Float> axis) {
        this.textures = textures;
        this.axis = axis;
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        TextureManager textureManager = worldRendererAccess.getTextureManager();
        ClientWorld world = MinecraftClient.getInstance().world;

        assert world != null;
        float timeRotation = !this.shouldRotate ? this.axis.get(1) : this.axis.get(1) + ((float) world.getTimeOfDay() / 24000) * 360;
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotation));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(this.axis.get(0)));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.axis.get(2)));
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
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(1f, 1f, 1f, this.alpha).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 1.0F).color(1f, 1f, 1f, this.alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(1.0F, 1.0F).color(1f, 1f, 1f, this.alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(1.0F, 0.0F).color(1f, 1f, 1f, this.alpha).next();
            tessellator.draw();
            matrices.pop();
        }

        matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(this.axis.get(2)));
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(this.axis.get(0)));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(timeRotation));

        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F));
        this.renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, this.alpha);
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(-90.0F));

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public Textures getTextures() {
        return this.textures;
    }
}
