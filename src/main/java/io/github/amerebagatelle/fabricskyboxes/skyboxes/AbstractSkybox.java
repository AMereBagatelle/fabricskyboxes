package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.Constants;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.Properties;
import io.github.amerebagatelle.fabricskyboxes.util.object.Weather;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

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
     * Calculates the alpha value for the current time and conditions and returns it.
     *
     * @return The new alpha value.
     */
    @Override
    public final float updateAlpha() {
        int currentTime = (int) (Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() % 24000);

        boolean shouldRender = Utils.isInTimeInterval(currentTime, this.properties.getFade().getStartFadeIn(), this.properties.getFade().getStartFadeOut() - 1);

        if ((shouldRender || this.properties.getFade().isAlwaysOn()) && this.checkConditions()) {
            if (this.alpha < this.properties.getMaxAlpha()) {
                // Check if currentTime is at the beginning of fadeIn
                if (this.properties.getFade().getStartFadeIn() == currentTime && this.fadeInDelta == null) {
                    float f1 = Utils.normalizeTime(this.properties.getMaxAlpha(), currentTime, this.properties.getFade().getStartFadeIn(), this.properties.getFade().getEndFadeIn());
                    float f2 = Utils.normalizeTime(this.properties.getMaxAlpha(), currentTime + 1, this.properties.getFade().getStartFadeIn(), this.properties.getFade().getEndFadeIn());
                    this.fadeInDelta = f2 - f1;
                }

                if (this.fadeInDelta == null) {
                    this.alpha += this.properties.getMaxAlpha() / this.properties.getTransitionInDuration();
                } else {
                    this.alpha += this.fadeInDelta;
                }
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

                if (this.fadeOutDelta == null) {
                    this.alpha -= this.properties.getMaxAlpha() / this.properties.getTransitionOutDuration();
                } else {
                    this.alpha -= this.fadeOutDelta;
                }
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
     * @return Whether all conditions were met
     */
    protected boolean checkConditions() {
        return this.checkDimensions() && this.checkWorlds() && this.checkBiomes() && this.checkXRanges() &&
                this.checkYRanges() && this.checkZRanges() && this.checkWeather() && this.checkEffects() &&
                this.checkLoop();
    }

    /**
     * @return Whether the current biomes and dimensions are valid for this skybox.
     */
    protected boolean checkBiomes() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(client.world);
        Objects.requireNonNull(client.player);
        return this.conditions.getBiomes().isEmpty() || this.conditions.getBiomes().contains(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(client.player.getBlockPos())));
    }

    /**
     * @return Whether the current dimension identifier is valid for this skybox
     */
    protected boolean checkDimensions() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(client.world);
        return this.conditions.getDimensions().isEmpty() || this.conditions.getDimensions().contains(client.world.getRegistryKey().getValue());
    }

    /**
     * @return Whether the current dimension sky effect is valid for this skybox
     */
    protected boolean checkWorlds() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(client.world);
        return this.conditions.getWorlds().isEmpty() || this.conditions.getWorlds().contains(client.world.getDimension().getSkyProperties());
    }

    /*
		Check if an effect that should prevent skybox from showing
     */
    protected boolean checkEffects() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(client.player);

        if (this.conditions.getEffects().isEmpty()) {
            return !client.player.hasStatusEffect(StatusEffects.BLINDNESS);
        } else {
            return this.conditions.getEffects().stream().noneMatch(identifier -> Registry.STATUS_EFFECT.get(identifier) != null && client.player.hasStatusEffect(Registry.STATUS_EFFECT.get(identifier)));
        }
    }

    /**
     * @return Whether the current x values are valid for this skybox.
     */
    protected boolean checkXRanges() {
        double playerX = Objects.requireNonNull(MinecraftClient.getInstance().player).getX();
        return Utils.checkRanges(playerX, this.conditions.getXRanges());
    }

    /**
     * @return Whether the current y values are valid for this skybox.
     */
    protected boolean checkYRanges() {
        double playerY = Objects.requireNonNull(MinecraftClient.getInstance().player).getY();
        return Utils.checkRanges(playerY, this.conditions.getYRanges());
    }

    /**
     * @return Whether the current z values are valid for this skybox.
     */
    protected boolean checkZRanges() {
        double playerZ = Objects.requireNonNull(MinecraftClient.getInstance().player).getZ();
        return Utils.checkRanges(playerZ, this.conditions.getZRanges());
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

            return Utils.checkRanges(currentDay, this.conditions.getLoop().getRanges());
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
            }
            if (this.conditions.getWeathers().contains(Weather.SNOW) && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
                return true;
            }
            if (this.conditions.getWeathers().contains(Weather.RAIN) && world.isRaining() && !world.isThundering()) {
                return true;
            }
            return this.conditions.getWeathers().contains(Weather.CLEAR) && !world.isRaining();
        } else {
            return true;
        }
    }

    public abstract SkyboxType<? extends AbstractSkybox> getType();

    public void renderDecorations(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, BufferBuilder bufferBuilder, float alpha) {
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        Vec3f rotationStatic = this.decorations.getRotation().getStatic();
        Vec3f rotationAxis = this.decorations.getRotation().getAxis();
        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;

        // Custom Blender
        this.decorations.getBlend().applyBlendFunc(alpha);
        matrices.push();

        // axis rotation
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationAxis.getX()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationAxis.getY()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationAxis.getZ()));

        // Vanilla rotation
        //matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        // Iris Compat
        //matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(IrisCompat.getSunPathRotation()));
        //matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F * this.decorations.getRotation().getRotationSpeed()));

        // Custom rotation
        double timeRotationX = this.decorations.getRotation().getRotationSpeedX() != 0F ? this.decorations.getRotation().getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedX()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedX()), 1))) : 0D;
        double timeRotationY = this.decorations.getRotation().getRotationSpeedY() != 0F ? this.decorations.getRotation().getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedY()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedY()), 1))) : 0D;
        double timeRotationZ = this.decorations.getRotation().getRotationSpeedZ() != 0F ? this.decorations.getRotation().getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedZ()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedZ()), 1))) : 0D;
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((float) timeRotationX));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) timeRotationY));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) timeRotationZ));

        // axis rotation
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(rotationAxis.getZ()));
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(rotationAxis.getY()));
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(rotationAxis.getX()));

        // static rotation
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));

        Matrix4f matrix4f2 = matrices.peek().getModel();
        // Sun
        if (this.decorations.isSunEnabled()) {
            worldRendererAccess.getTextureManager().bindTexture(this.decorations.getSunTexture());
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, -30.0F).texture(0.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, -30.0F).texture(1.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, 30.0F).texture(1.0F, 1.0F).next();
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, 30.0F).texture(0.0F, 1.0F).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
        }
        // Moon
        if (this.decorations.isMoonEnabled()) {
            worldRendererAccess.getTextureManager().bindTexture(this.decorations.getMoonTexture());
            int moonPhase = world.getMoonPhase();
            int xCoord = moonPhase % 4;
            int yCoord = moonPhase / 4 % 2;
            float startX = xCoord / 4.0F;
            float startY = yCoord / 2.0F;
            float endX = (xCoord + 1) / 4.0F;
            float endY = (yCoord + 1) / 2.0F;
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -20.0F, -100.0F, 20.0F).texture(endX, endY).next();
            bufferBuilder.vertex(matrix4f2, 20.0F, -100.0F, 20.0F).texture(startX, endY).next();
            bufferBuilder.vertex(matrix4f2, 20.0F, -100.0F, -20.0F).texture(startX, startY).next();
            bufferBuilder.vertex(matrix4f2, -20.0F, -100.0F, -20.0F).texture(endX, startY).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
        }
        RenderSystem.disableTexture();
        // Stars
        if (this.decorations.isStarsEnabled()) {
            float i = 1.0F - world.getRainGradient(tickDelta);
            float brightness = world.method_23787(tickDelta) * i;
            if (brightness > 0.0F) {
                RenderSystem.color4f(brightness, brightness, brightness, brightness);
                worldRendererAccess.getStarsBuffer().bind();
                worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
                worldRendererAccess.getStarsBuffer().draw(matrices.peek().getModel(), 7);
                VertexBuffer.unbind();
                worldRendererAccess.getSkyVertexFormat().endDrawing();
            }
        }
        matrices.pop();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
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
