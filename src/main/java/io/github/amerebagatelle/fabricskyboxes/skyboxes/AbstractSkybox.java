package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    private int lastTime = -2;
    private float conditionAlpha = 0f;


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

        boolean condition = this.checkConditions();

        float fadeAlpha = 1f;
        if (this.properties.getFade().isAlwaysOn()) {
            this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, this.conditionAlpha, condition ? this.properties.getTransitionInDuration() : this.properties.getTransitionOutDuration(), condition);
        } else {
            fadeAlpha = Utils.calculateFadeAlphaValue(1f, currentTime, this.properties.getFade().getStartFadeIn(), this.properties.getFade().getEndFadeIn(), this.properties.getFade().getStartFadeOut(), this.properties.getFade().getEndFadeOut());

            if (this.lastTime == currentTime - 1 || this.lastTime == currentTime) { // Check if time is ticking or if time is same (doDaylightCycle gamerule)
                this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, this.conditionAlpha, condition ? this.properties.getTransitionInDuration() : this.properties.getTransitionOutDuration(), condition);
            } else {
                this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, this.conditionAlpha, FabricSkyBoxesClient.config().generalSettings.unexpectedTransitionDuration, condition);
            }
        }

        this.alpha = fadeAlpha * this.conditionAlpha * this.properties.getMaxAlpha();

        this.alpha = MathHelper.clamp(this.alpha, 0F, this.properties.getMaxAlpha());
        this.lastTime = currentTime;

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
        return this.conditions.getBiomes().isEmpty() || this.conditions.getBiomes().contains(client.world.getRegistryManager().get(RegistryKeys.BIOME).getId(client.world.getBiome(client.player.getBlockPos()).value()));
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
        return this.conditions.getWorlds().isEmpty() || this.conditions.getWorlds().contains(client.world.getDimension().effects());
    }

    /*
		Check if an effect that should prevent skybox from showing
     */
    protected boolean checkEffects() {
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
        Biome.Precipitation precipitation = world.getBiome(player.getBlockPos()).value().getPrecipitation();
        if (this.conditions.getWeathers().size() > 0) {
            if (this.conditions.getWeathers().contains(Weather.THUNDER) && world.isThundering()) {
                return true;
            }
            if (this.conditions.getWeathers().contains(Weather.RAIN) && world.isRaining()) {
                return true;
            }
            if (this.conditions.getWeathers().contains(Weather.SNOW) && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
                return true;
            }
            if (this.conditions.getWeathers().contains(Weather.BIOME_RAIN) && world.isRaining() && precipitation == Biome.Precipitation.RAIN) {
                return true;
            }
            return this.conditions.getWeathers().contains(Weather.CLEAR) && !world.isRaining() && !world.isThundering();
        } else {
            return true;
        }
    }

    public abstract SkyboxType<? extends AbstractSkybox> getType();

    public void renderDecorations(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, BufferBuilder bufferBuilder, float alpha) {
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        Vector3f rotationStatic = this.decorations.getRotation().getStatic();
        Vector3f rotationAxis = this.decorations.getRotation().getAxis();
        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;

        // Custom Blender
        this.decorations.getBlend().applyBlendFunc(alpha);
        matrices.push();

        // axis rotation
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationAxis.x()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAxis.y()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAxis.z()));

        // Vanilla rotation
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
        // Iris Compat
        //matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(IrisCompat.getSunPathRotation()));
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * this.decorations.getRotation().getRotationSpeed()));

        // Custom rotation
        double timeRotationX = this.decorations.getRotation().getRotationSpeedX() != 0F ? this.decorations.getRotation().getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedX()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedX()), 1))) : 0D;
        double timeRotationY = this.decorations.getRotation().getRotationSpeedY() != 0F ? this.decorations.getRotation().getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedY()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedY()), 1))) : 0D;
        double timeRotationZ = this.decorations.getRotation().getRotationSpeedZ() != 0F ? this.decorations.getRotation().getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedZ()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.decorations.getRotation().getRotationSpeedZ()), 1))) : 0D;
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) timeRotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) timeRotationY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) timeRotationZ));

        // axis rotation
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(rotationAxis.z()));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(rotationAxis.y()));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(rotationAxis.x()));

        // static rotation
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationStatic.x()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationStatic.y()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationStatic.z()));

        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        // Sun
        if (this.decorations.isSunEnabled()) {
            RenderSystem.setShaderTexture(0, this.decorations.getSunTexture());
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, -30.0F).texture(0.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, -30.0F).texture(1.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, 30.0F).texture(1.0F, 1.0F).next();
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, 30.0F).texture(0.0F, 1.0F).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        }
        // Moon
        if (this.decorations.isMoonEnabled()) {
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
        RenderSystem.disableTexture();
        // Stars
        if (this.decorations.isStarsEnabled()) {
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
        matrices.pop();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
}
