package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.common.collect.Range;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

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
        if (!this.properties.getFade().isAlwaysOn()) {
            int currentTime = (int) (Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() % 24000); // modulo so that it's bound to 24000
            int durationIn = Utils.getTicksBetween(this.properties.getFade().getStartFadeIn(), this.properties.getFade().getEndFadeIn());
            int durationOut = Utils.getTicksBetween(this.properties.getFade().getStartFadeOut(), this.properties.getFade().getEndFadeOut());

            int startFadeIn = this.properties.getFade().getStartFadeIn() % 24000;
            int endFadeIn = this.properties.getFade().getEndFadeIn() % 24000;

            if (endFadeIn < startFadeIn) {
                endFadeIn += 24000;
            }

            int startFadeOut = this.properties.getFade().getStartFadeOut() % 24000;
            int endFadeOut = this.properties.getFade().getEndFadeOut() % 24000;

            if (startFadeOut < endFadeIn) {
                startFadeOut += 24000;
            }

            if (endFadeOut < startFadeOut) {
                endFadeOut += 24000;
            }

            int tempInTime = currentTime;

            if (tempInTime < startFadeIn) {
                tempInTime += 24000;
            }

            int tempFullTime = currentTime;

            if (tempFullTime < endFadeIn) {
                tempFullTime += 24000;
            }

            int tempOutTime = currentTime;

            if (tempOutTime < startFadeOut) {
                tempOutTime += 24000;
            }

            float maxPossibleAlpha;

            if (startFadeIn < tempInTime && endFadeIn >= tempInTime) {
                maxPossibleAlpha = 1f - (((float) (endFadeIn - tempInTime)) / durationIn); // fading in

            } else if (endFadeIn < tempFullTime && startFadeOut >= tempFullTime) {
                maxPossibleAlpha = 1f; // fully faded in

            } else if (startFadeOut < tempOutTime && endFadeOut >= tempOutTime) {
                maxPossibleAlpha = (float) (endFadeOut - tempOutTime) / durationOut; // fading out

            } else {
                maxPossibleAlpha = 0f; // default not showing
            }

            maxPossibleAlpha *= this.properties.getMaxAlpha();
            if (checkBiomes() && checkXRanges() && checkYRanges() && checkZRanges() && checkWeather() && checkEffect() && checkLoop()) { // check if environment is invalid
                if (alpha >= maxPossibleAlpha) {
                    alpha = maxPossibleAlpha;
                } else {
                    alpha += this.properties.getTransitionSpeed();
                    if (alpha > maxPossibleAlpha) alpha = maxPossibleAlpha;
                }
            } else {
                if (alpha > 0f) {
                    alpha -= this.properties.getTransitionSpeed();
                    if (alpha < 0f) alpha = 0f;
                } else {
                    alpha = 0f;
                }
            }
        } else {
            if (checkBiomes() && checkXRanges() && checkYRanges() && checkZRanges() && checkWeather() && checkEffect() && checkLoop()) { // check if environment is invalid
                alpha = 1f;
            } else {
                alpha = 0f;
            }
        }

        // sanity checks
        if (alpha < 0f) alpha = 0f;
        if (alpha > 1f) alpha = 1f;

        return alpha;
    }

    /**
     * @return Whether the current biomes and dimensions are valid for this skybox.
     */
    protected boolean checkBiomes() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(client.world);
        Objects.requireNonNull(client.player);
        if (this.conditions.getWorlds().isEmpty() || this.conditions.getWorlds().contains(client.world.getDimension().getEffects())) {
            return this.conditions.getBiomes().isEmpty() || this.conditions.getBiomes().contains(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(client.player.getBlockPos())));
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
            if (thickFog)
                return false;

            CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
            if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW || cameraSubmersionType == CameraSubmersionType.LAVA)
                return false;

            if (camera.getFocusedEntity() instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(StatusEffects.BLINDNESS))
                return false;

        } else {
            if (camera.getFocusedEntity() instanceof LivingEntity livingEntity) {
                return this.conditions.getEffects().stream().noneMatch(identifier -> Registry.STATUS_EFFECT.get(identifier) != null && livingEntity.hasStatusEffect(Registry.STATUS_EFFECT.get(identifier)));
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
        Biome.Precipitation precipitation = world.getBiome(player.getBlockPos()).getPrecipitation();
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
        if (!SkyboxManager.getInstance().hasRenderedDecorations()) {
            Vec3f rotationStatic = decorations.getRotation().getStatic();
            Vec3f rotationAxis = decorations.getRotation().getAxis();

            RenderSystem.enableTexture();
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
            ClientWorld world = MinecraftClient.getInstance().world;
            assert world != null;
            RenderSystem.enableTexture();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationAxis.getX()));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationAxis.getY()));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationAxis.getZ()));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F * decorations.getRotation().getRotationSpeed()));
            matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(rotationAxis.getZ()));
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(rotationAxis.getY()));
            matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(rotationAxis.getX()));
            // sun
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            Matrix4f matrix4f2 = matrices.peek().getModel();
            float s = 30.0F;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            if (decorations.isSunEnabled()) {
                RenderSystem.setShaderTexture(0, this.decorations.getSunTexture());
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                bufferBuilder.vertex(matrix4f2, -s, 100.0F, -s).texture(0.0F, 0.0F).next();
                bufferBuilder.vertex(matrix4f2, s, 100.0F, -s).texture(1.0F, 0.0F).next();
                bufferBuilder.vertex(matrix4f2, s, 100.0F, s).texture(1.0F, 1.0F).next();
                bufferBuilder.vertex(matrix4f2, -s, 100.0F, s).texture(0.0F, 1.0F).next();
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
            }
            // moon
            s = 20.0F;
            if (decorations.isMoonEnabled()) {
                RenderSystem.setShaderTexture(0, this.decorations.getMoonTexture());
                int u = world.getMoonPhase();
                int v = u % 4;
                int w = u / 4 % 2;
                float x = v / 4.0F;
                float p = w / 2.0F;
                float q = (v + 1) / 4.0F;
                float r = (w + 1) / 2.0F;
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                bufferBuilder.vertex(matrix4f2, -s, -100.0F, s).texture(q, r).next();
                bufferBuilder.vertex(matrix4f2, s, -100.0F, s).texture(x, r).next();
                bufferBuilder.vertex(matrix4f2, s, -100.0F, -s).texture(x, p).next();
                bufferBuilder.vertex(matrix4f2, -s, -100.0F, -s).texture(q, p).next();
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
            }
            // stars
            if (decorations.isStarsEnabled()) {
                RenderSystem.disableTexture();
                float ab = world.method_23787(tickDelta) * s;
                if (ab > 0.0F) {
                    RenderSystem.setShaderColor(ab, ab, ab, ab);
                    worldRendererAccess.getStarsBuffer().setShader(matrices.peek().getModel(), matrix4f, GameRenderer.getPositionShader());
                }
            }
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            matrices.pop();
        }
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
        return this.getAlpha() > SkyboxManager.MINIMUM_ALPHA;
    }

    @Override
    public boolean isActiveLater() {
        return this.updateAlpha() > SkyboxManager.MINIMUM_ALPHA;
    }
}
