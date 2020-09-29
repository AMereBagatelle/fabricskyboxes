package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import java.util.List;
import java.util.Objects;
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
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class MonoColorSkybox extends AbstractSkybox {
    public static final Codec<MonoColorSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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
            RGBA.CODEC.fieldOf("color").forGetter((box) -> null)
    ).apply(instance, MonoColorSkybox::new));

    private final float red;
    private final float blue;
    private final float green;

    public MonoColorSkybox(float red, float blue, float green) {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public MonoColorSkybox(Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean shouldRotate, boolean decorations, List<Weather> weather, List<Identifier> biomes, List<Identifier> dimensions, List<HeightEntry> heightRanges, RGBA color) {
        super(fade, maxAlpha, transitionSpeed, changeFog, fogColors, shouldRotate, decorations, weather.stream().map(Weather::toString).collect(Collectors.toList()), biomes, dimensions, heightRanges);
        this.red = color.getRed();
        this.blue = color.getBlue();
        this.green = color.getGreen();
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        if (this.alpha > 0) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = Objects.requireNonNull(client.world);
            RenderSystem.disableTexture();
            BackgroundRenderer.setFogBlack();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.depthMask(false);
            RenderSystem.enableFog();
            RenderSystem.color3f(this.red, this.blue, this.green);
            worldRendererAccess.getLightSkyBuffer().bind();
            worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
            worldRendererAccess.getLightSkyBuffer().draw(matrices.peek().getModel(), 7);
            VertexBuffer.unbind();
            worldRendererAccess.getSkyVertexFormat().endDrawing();
            RenderSystem.disableFog();
            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float[] skyColor = world.getSkyProperties().getSkyColor(world.getSkyAngle(tickDelta), tickDelta);
            float skySide;
            float skyColorGreen;
            float o;
            float p;
            float q;
            if (skyColor != null) {
                RenderSystem.disableTexture();
                RenderSystem.shadeModel(7425);
                matrices.push();
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                skySide = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(skySide));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                float skyColorRed = skyColor[0];
                skyColorGreen = skyColor[1];
                float skyColorBlue = skyColor[2];
                Matrix4f matrix4f = matrices.peek().getModel();
                bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(skyColorRed, skyColorGreen, skyColorBlue, skyColor[3]).next();

                for (int n = 0; n <= 16; ++n) {
                    o = (float) n * 6.2831855F / 16.0F;
                    p = MathHelper.sin(o);
                    q = MathHelper.cos(o);
                    bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * skyColor[3]).color(skyColor[0], skyColor[1], skyColor[2], 0.0F).next();
                }

                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
                matrices.pop();
                RenderSystem.shadeModel(7424);
            }

            RenderSystem.enableTexture();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F));

            this.renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, this.alpha);

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableFog();
            matrices.pop();
            RenderSystem.disableTexture();
            RenderSystem.color3f(0.0F, 0.0F, 0.0F);
            assert client.player != null;
            double d = client.player.getCameraPosVec(tickDelta).y - world.getLevelProperties().getSkyDarknessHeight();
            if (d < 0.0D) {
                matrices.push();
                matrices.translate(0.0D, 12.0D, 0.0D);
                worldRendererAccess.getDarkSkyBuffer().bind();
                worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
                worldRendererAccess.getDarkSkyBuffer().draw(matrices.peek().getModel(), 7);
                VertexBuffer.unbind();
                worldRendererAccess.getSkyVertexFormat().endDrawing();
                matrices.pop();
            }

            if (world.getSkyProperties().isAlternateSkyColor()) {
                RenderSystem.color3f(this.red * 0.2F + 0.04F, this.blue * 0.2F + 0.04F, this.green * 0.6F + 0.1F);
            } else {
                RenderSystem.color3f(this.red, this.blue, this.green);
            }

            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
            RenderSystem.disableFog();
        }
    }
}
