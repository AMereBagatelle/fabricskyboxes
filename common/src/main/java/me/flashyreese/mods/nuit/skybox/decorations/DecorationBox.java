package me.flashyreese.mods.nuit.skybox.decorations;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.flashyreese.mods.nuit.components.Blend;
import me.flashyreese.mods.nuit.components.Conditions;
import me.flashyreese.mods.nuit.components.Properties;
import me.flashyreese.mods.nuit.mixin.SkyRendererAccessor;
import me.flashyreese.mods.nuit.skybox.AbstractSkybox;
import me.flashyreese.mods.nuit.skybox.TextureRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class DecorationBox extends AbstractSkybox implements TextureRegistrar {
    public static Codec<DecorationBox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.optionalFieldOf("properties", Properties.decorations()).forGetter(DecorationBox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.of()).forGetter(DecorationBox::getConditions),
            ResourceLocation.CODEC.optionalFieldOf("sun", SkyRendererAccessor.getSun()).forGetter(DecorationBox::getSunTexture),
            ResourceLocation.CODEC.optionalFieldOf("moon", SkyRendererAccessor.getMoonPhases()).forGetter(DecorationBox::getMoonTexture),
            Codec.BOOL.optionalFieldOf("showSun", false).forGetter(DecorationBox::isSunEnabled),
            Codec.BOOL.optionalFieldOf("showMoon", false).forGetter(DecorationBox::isMoonEnabled),
            Codec.BOOL.optionalFieldOf("showStars", false).forGetter(DecorationBox::isStarsEnabled),
            Blend.CODEC.optionalFieldOf("blend", Blend.decorations()).forGetter(DecorationBox::getBlend)
    ).apply(instance, DecorationBox::new));

    private final ResourceLocation sunTexture;
    private final ResourceLocation moonTexture;
    private final boolean sunEnabled;
    private final boolean moonEnabled;
    private final boolean starsEnabled;
    private final Blend blend;

    public DecorationBox(Properties properties, Conditions conditions, ResourceLocation sun, ResourceLocation moon, boolean sunEnabled, boolean moonEnabled, boolean starsEnabled, Blend blend) {
        this.properties = properties;
        this.conditions = conditions;
        this.sunTexture = sun;
        this.moonTexture = moon;
        this.sunEnabled = sunEnabled;
        this.moonEnabled = moonEnabled;
        this.starsEnabled = starsEnabled;
        this.blend = blend;
    }

    @Override
    public void render(SkyRendererAccessor skyRendererAccessor, PoseStack poseStack, Matrix4f projectionMatrix,
                       float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        if (this.alpha <= 0.0F) {
            return;
        }

        RenderSystem.enableBlend();
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);

        // Custom Blender
        this.blend.apply(this.alpha);
        poseStack.pushPose();

        // static
        this.properties.rotation().apply(poseStack, level, this.properties.clock(), tickDelta);

        // Iris Compat
        //poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(IrisCompat.getSunPathRotation()));
        //poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(level.getSkyAngle(tickDelta) * 360.0F * this.decorations.getRotation().getRotationSpeed()));

        Matrix4f matrix4f2 = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        // Sun
        if (this.sunEnabled) {
            this.renderSun(matrix4f2);
        }

        // Moon
        if (this.moonEnabled) {
            this.renderMoon(matrix4f2, level);
        }

        // Stars
        if (this.starsEnabled) {
            this.renderStars(skyRendererAccessor, level, poseStack, projectionMatrix, tickDelta, fogCallback);
        }

        poseStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void renderSun(Matrix4f matrix4f) {
        RenderSystem.setShaderTexture(0, this.sunTexture);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX
        );
        bufferBuilder.addVertex(matrix4f, -30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, 30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, 30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, -30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public void renderMoon(Matrix4f matrix4f, ClientLevel level) {
        int moonPhase = level.getMoonPhase();
        int xCoord = moonPhase % 4;
        int yCoord = moonPhase / 4 % 2;
        float startX = xCoord / 4.0F;
        float startY = yCoord / 2.0F;
        float endX = (xCoord + 1) / 4.0F;
        float endY = (yCoord + 1) / 2.0F;
        RenderSystem.setShaderTexture(0, this.moonTexture);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX
        );
        bufferBuilder.addVertex(matrix4f, -20.0F, -100.0F, 20.0F).setUv(endX, endY);
        bufferBuilder.addVertex(matrix4f, 20.0F, -100.0F, 20.0F).setUv(startX, endY);
        bufferBuilder.addVertex(matrix4f, 20.0F, -100.0F, -20.0F).setUv(startX, startY);
        bufferBuilder.addVertex(matrix4f, -20.0F, -100.0F, -20.0F).setUv(endX, startY);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public void renderStars(SkyRendererAccessor skyRendererAccessor, ClientLevel level, PoseStack poseStack,
                            Matrix4f projectionMatrix, float tickDelta, Runnable fogCallback) {
        float brightness = level.getStarBrightness(tickDelta);
        if (brightness > 0.0F) {
            RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);
            FogRenderer.setupNoFog();
            skyRendererAccessor.getStarsBuffer().bind();
            skyRendererAccessor.getStarsBuffer().drawWithShader(
                    poseStack.last().pose(),
                    projectionMatrix,
                    RenderSystem.getShader()
            );
            VertexBuffer.unbind();
            fogCallback.run();
        }
    }

    public ResourceLocation getSunTexture() {
        return this.sunTexture;
    }

    public ResourceLocation getMoonTexture() {
        return this.moonTexture;
    }

    public boolean isSunEnabled() {
        return this.sunEnabled;
    }

    public boolean isMoonEnabled() {
        return this.moonEnabled;
    }

    public boolean isStarsEnabled() {
        return this.starsEnabled;
    }

    public Blend getBlend() {
        return this.blend;
    }

    @Override
    public List<ResourceLocation> getTexturesToRegister() {
        List<ResourceLocation> textures = new ArrayList<>(2);
        if (this.sunEnabled) {
            textures.add(this.sunTexture);
        }
        if (this.moonEnabled) {
            textures.add(this.moonTexture);
        }
        return textures;
    }
}
