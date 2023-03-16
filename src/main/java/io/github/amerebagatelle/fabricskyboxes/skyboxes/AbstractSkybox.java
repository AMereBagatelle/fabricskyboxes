package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.common.collect.Range;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.Constants;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

/**
 * All classes that implement {@link AbstractSkybox} should
 * have a default constructor as it is required when checking
 * the type of the skybox.
 */
public abstract class AbstractSkybox implements FSBSkybox {

    /**
     * The current alpha for the skybox. Expects all skyboxes extending this to accommodate this.
     * This variable is responsible for fading in/out skyboxes.
     */
    public transient float alpha;
    protected Properties properties;
    protected Conditions conditions = Conditions.DEFAULT;
    protected Decorations decorations = Decorations.DEFAULT;
    private Float fadeInDelta = null;
    private Float fadeOutDelta = null;

    protected AbstractSkybox() {
    }

    protected AbstractSkybox(Properties properties, Conditions conditions, Decorations decorations) {
        this.properties = properties;
        this.conditions = conditions;
        this.decorations = decorations;
    }

    /**
     * @return Whether the value is within any of the minMaxEntries.
     */
    private static boolean checkRanges(double value, List<MinMaxEntry> minMaxEntries) {
        return minMaxEntries.isEmpty() || minMaxEntries.stream()
                .anyMatch(minMaxEntry -> Range.closed(minMaxEntry.getMin(), minMaxEntry.getMax())
                        .contains((float) value));
    }

    /**
     * Calculates the alpha value for the current time and conditions and returns it.
     *
     * @return The new alpha value.
     */
    @Override
    public final float updateAlpha() {
        int currentTime = (int) (Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() % 24000);

        boolean shouldRender = Utils.isInTimeInterval(currentTime, this.properties.getFade().getStartFadeIn(), this.properties.getFade().getStartFadeOut() - 1);

        if ((shouldRender || this.properties.getFade().isAlwaysOn()) && checkBiomes() && checkXRanges() && checkYRanges() && checkZRanges() && checkWeather() && checkEffect() && checkLoop()) {
            if (this.alpha < this.properties.getMaxAlpha()) {
                // Check if currentTime is at the beginning of fadeIn
                if (this.properties.getFade().getStartFadeIn() == currentTime && this.fadeInDelta == null) {
                    float f1 = Utils.normalizeTime(this.properties.getMaxAlpha(), currentTime, this.properties.getFade().getStartFadeIn(), this.properties.getFade().getEndFadeIn());
                    float f2 = Utils.normalizeTime(this.properties.getMaxAlpha(), currentTime + 1, this.properties.getFade().getStartFadeIn(), this.properties.getFade().getEndFadeIn());
                    this.fadeInDelta = f2 - f1;
                }

                this.alpha += Objects.requireNonNullElseGet(this.fadeInDelta, () -> this.properties.getMaxAlpha() / this.properties.getTransitionInDuration());
            } else {
                this.alpha = this.properties.getMaxAlpha();
                if (this.fadeInDelta != null) {
                    this.fadeInDelta = null;
                }
            }
        } else {
            if (this.alpha > 0f) {
                // Check if currentTime is at the beginning of fadeOut
                if (this.properties.getFade().getStartFadeOut() == currentTime && this.fadeOutDelta == null) {
                    float f1 = Utils.normalizeTime(this.properties.getMaxAlpha(), currentTime, this.properties.getFade().getStartFadeOut(), this.properties.getFade().getEndFadeOut());
                    float f2 = Utils.normalizeTime(this.properties.getMaxAlpha(), currentTime + 1, this.properties.getFade().getStartFadeOut(), this.properties.getFade().getEndFadeOut());
                    this.fadeOutDelta = f2 - f1;
                }

                this.alpha -= Objects.requireNonNullElseGet(this.fadeOutDelta, () -> this.properties.getMaxAlpha() / this.properties.getTransitionOutDuration());
            } else {
                this.alpha = 0F;
                if (this.fadeOutDelta != null) {
                    this.fadeOutDelta = null;
                }
            }
        }

        this.alpha = MathHelper.clamp(this.alpha, 0F, this.properties.getMaxAlpha());

        return this.alpha;
    }

    /**
     * @return Whether the current biomes and dimensions are valid for this skybox.
     */
    protected boolean checkBiomes() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(client.world);
        Objects.requireNonNull(client.player);
        if ((this.conditions.getWorlds().isEmpty() || this.conditions.getWorlds().contains(client.world.getDimension().effects())) && (this.conditions.getDimensions().isEmpty() || this.conditions.getDimensions().contains(client.world.getRegistryKey().getValue()))) {
            return this.conditions.getBiomes().isEmpty() || this.conditions.getBiomes().contains(client.world.getRegistryManager().get(RegistryKeys.BIOME).getId(client.world.getBiome(client.player.getBlockPos()).value()));
        }
        return false;
    }

    /*
		Check if an effect that should prevent skybox from showing
     */
    protected boolean checkEffect() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(client.world);

        Camera camera = client.gameRenderer.getCamera();

        if (this.conditions.getEffects().isEmpty()) {
            // Vanilla checks
            boolean thickFog = client.world.getDimensionEffects().useThickFog(MathHelper.floor(camera.getPos().getX()), MathHelper.floor(camera.getPos().getY())) || client.inGameHud.getBossBarHud().shouldThickenFog();
            if (thickFog) {
                // Render skybox in thick fog, enabled by default
                return this.properties.isRenderInThickFog();
            }

            CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
            if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW || cameraSubmersionType == CameraSubmersionType.LAVA)
                return false;

            return !(camera.getFocusedEntity() instanceof LivingEntity livingEntity) || (!livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) && !livingEntity.hasStatusEffect(StatusEffects.DARKNESS));

        } else {
            if (camera.getFocusedEntity() instanceof LivingEntity livingEntity) {
                return this.conditions.getEffects().stream().noneMatch(identifier -> client.world.getRegistryManager().get(RegistryKeys.STATUS_EFFECT).get(identifier) != null && livingEntity.hasStatusEffect(client.world.getRegistryManager().get(RegistryKeys.STATUS_EFFECT).get(identifier)));
            }
        }
        return true;
    }

    /**
     * @return Whether the current x values are valid for this skybox.
     */
    protected boolean checkXRanges() {
        double playerX = Objects.requireNonNull(MinecraftClient.getInstance().player).getX();
        return checkRanges(playerX, this.conditions.getXRanges());
    }

    /**
     * @return Whether the current y values are valid for this skybox.
     */
    protected boolean checkYRanges() {
        double playerY = Objects.requireNonNull(MinecraftClient.getInstance().player).getY();
        return checkRanges(playerY, this.conditions.getYRanges());
    }

    /**
     * @return Whether the current z values are valid for this skybox.
     */
    protected boolean checkZRanges() {
        double playerZ = Objects.requireNonNull(MinecraftClient.getInstance().player).getZ();
        return checkRanges(playerZ, this.conditions.getZRanges());
    }

    /**
     * @return Whether the current loop is valid for this skybox.
     */
    protected boolean checkLoop() {
        if (!this.conditions.getLoop().getRanges().isEmpty() && this.conditions.getLoop().getDays() > 0) {
            double currentTime = Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() - this.properties.getFade().getStartFadeIn();
            while (currentTime < 0) {
                currentTime += 24000 * this.conditions.getLoop().getDays();
            }

            double currentDay = (currentTime / 24000D) % this.conditions.getLoop().getDays();

            return checkRanges(currentDay, this.conditions.getLoop().getRanges());
        }
        return true;
    }

    /**
     * @return Whether the current weather is valid for this skybox.
     */
    protected boolean checkWeather() {
        ClientWorld world = Objects.requireNonNull(MinecraftClient.getInstance().world);
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        Biome.Precipitation precipitation = world.getBiome(player.getBlockPos()).value().getPrecipitation(player.getBlockPos());
        if (this.conditions.getWeathers().size() > 0) {
            if (this.conditions.getWeathers().contains(Weather.THUNDER) && world.isThundering()) {
                return true;
            } else if (this.conditions.getWeathers().contains(Weather.SNOW) && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
                return true;
            } else if (this.conditions.getWeathers().contains(Weather.RAIN) && world.isRaining() && !world.isThundering()) {
                return true;
            } else return this.conditions.getWeathers().contains(Weather.CLEAR) && !world.isRaining();
        } else {
            return true;
        }
    }

    public abstract SkyboxType<? extends AbstractSkybox> getType();

    public void renderDecorations(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, BufferBuilder bufferBuilder, float alpha) {
        Vector3f rotationStatic = decorations.getRotation().getStatic();
        Vector3f rotationAxis = decorations.getRotation().getAxis();
        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;

        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrices.push();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        // static rotation
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationStatic.x()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationStatic.y()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationStatic.z()));

        // axis rotation
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationAxis.x()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAxis.y()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAxis.z()));

        // Vanilla rotation
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * this.decorations.getRotation().getRotationSpeed()));

        float timeRotation = this.decorations.getRotation().getRotationSpeed() != 0F ? 360F * MathHelper.floorMod(world.getTimeOfDay() / (24000 / this.decorations.getRotation().getRotationSpeed()), 1) : 0;
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(timeRotation));

        // fixme: add rotationSpeed but for decorations?
        /* matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * decorations.getRotation().getRotationSpeed()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * decorations.getRotation().getRotationSpeedX()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * decorations.getRotation().getRotationSpeedY()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * decorations.getRotation().getRotationSpeedZ()));*/

        // axis rotation
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(rotationAxis.z()));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(rotationAxis.y()));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(rotationAxis.x()));


        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        // Sun
        if (this.decorations.isSunEnabled() && !SkyboxManager.getInstance().hasRenderedSun()) {
            RenderSystem.setShaderTexture(0, this.decorations.getSunTexture());
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, -30.0F).texture(0.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, -30.0F).texture(1.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, 30.0F).texture(1.0F, 1.0F).next();
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, 30.0F).texture(0.0F, 1.0F).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        }
        // Moon
        if (this.decorations.isMoonEnabled() && !SkyboxManager.getInstance().hasRenderedMoon()) {
            RenderSystem.setShaderTexture(0, this.decorations.getMoonTexture());
            int moonPhase = world.getMoonPhase();
            int xCoord = moonPhase % 4;
            int yCoord = moonPhase / 4 % 2;
            float startX = xCoord / 4.0F;
            float startY = yCoord / 2.0F;
            float endX = (xCoord + 1) / 4.0F;
            float endY = (yCoord + 1) / 2.0F;
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -20.0F, -100.0F, 20.0F).texture(endX, endY).next();
            bufferBuilder.vertex(matrix4f2, 20.0F, -100.0F, 20.0F).texture(startX, endY).next();
            bufferBuilder.vertex(matrix4f2, 20.0F, -100.0F, -20.0F).texture(startX, startY).next();
            bufferBuilder.vertex(matrix4f2, -20.0F, -100.0F, -20.0F).texture(endX, startY).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        }
        // Stars
        if (this.decorations.isStarsEnabled() && !SkyboxManager.getInstance().hasRenderedStars()) {
            float i = 1.0F - world.getRainGradient(tickDelta);
            float brightness = world.method_23787(tickDelta) * i;
            if (brightness > 0.0F) {
                RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);
                BackgroundRenderer.clearFog();
                worldRendererAccess.getStarsBuffer().bind();
                worldRendererAccess.getStarsBuffer().draw(matrices.peek().getPositionMatrix(), matrix4f, GameRenderer.getPositionProgram());
                VertexBuffer.unbind();
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        matrices.pop();
    }

    @Override
    public Decorations getDecorations() {
        return this.decorations;
    }

    @Override
    public Properties getProperties() {
        return this.properties; // Properties.ofSkybox(this);
    }

    @Override
    public Conditions getConditions() {
        return this.conditions; // Conditions.ofSkybox(this);
    }

    @Override
    public float getAlpha() {
        return this.alpha;
    }

    @Override
    public int getPriority() {
        return this.properties.getPriority();
    }

    @Override
    public boolean isActive() {
        return this.getAlpha() > Constants.MINIMUM_ALPHA;
    }

    @Override
    public boolean isActiveLater() {
        final float oldAlpha = this.alpha;
        if (this.updateAlpha() > Constants.MINIMUM_ALPHA) {
            this.alpha = oldAlpha;
            return true;
        }
        return false;
    }
}
