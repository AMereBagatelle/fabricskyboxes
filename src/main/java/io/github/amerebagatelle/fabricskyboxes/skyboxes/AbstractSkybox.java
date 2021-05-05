package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * All classes that implement {@link AbstractSkybox} should
 * have a default constructor as it is required when checking
 * the type of the skybox.
 */
public abstract class AbstractSkybox {
    /**
     * The current alpha for the skybox. Expects all skyboxes extending this to accommodate this.
     * This variable is responsible for fading in/out skyboxes.
     */
    public transient float alpha;

    // ! These are the options variables.  Do not mess with these.
    protected Fade fade = Fade.ZERO;
    protected float maxAlpha = 1f;
    protected float transitionSpeed = 1;
    protected boolean changeFog = false;
    protected RGBA fogColors = RGBA.ZERO;
    protected boolean renderSunSkyColorTint = true;
    protected boolean shouldRotate = false;
    protected List<String> weather = new ArrayList<>();
    protected List<Identifier> biomes = new ArrayList<>();
    protected Decorations decorations = Decorations.DEFAULT;
    /**
     * Stores identifiers of <b>worlds</b>, not dimension types.
     */
    protected List<Identifier> worlds = new ArrayList<>();
    protected List<HeightEntry> heightRanges = Lists.newArrayList();

    /**
     * The main render method for a skybox.
     * Override this if you are creating a skybox from this one.
     *
     * @param worldRendererAccess Access to the worldRenderer as skyboxes often require it.
     * @param matrices            The current MatrixStack.
     * @param tickDelta           The current tick delta.
     */
    public abstract void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    protected AbstractSkybox() {
    }

    protected AbstractSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations) {
        this.fade = properties.getFade();
        this.maxAlpha = properties.getMaxAlpha();
        this.transitionSpeed = properties.getTransitionSpeed();
        this.changeFog = properties.isChangeFog();
        this.fogColors = properties.getFogColors();
        this.renderSunSkyColorTint = properties.isRenderSunSkyTint();
        this.shouldRotate = properties.isShouldRotate();
        this.weather = conditions.getWeathers().stream().map(Weather::toString).distinct().collect(Collectors.toList());
        this.biomes = conditions.getBiomes();
        this.worlds = conditions.getWorlds();
        this.heightRanges = conditions.getHeights();
        this.decorations = decorations;
    }

    /**
     * Calculates the alpha value for the current time and conditions and returns it.
     *
     * @return The new alpha value.
     */
    public final float getAlpha() {
        if (!fade.isAlwaysOn()) {
            int currentTime = (int) Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() % 24000; // modulo so that it's bound to 24000
            int durationin = Utils.getTicksBetween(this.fade.getStartFadeIn(), this.fade.getEndFadeIn());
            int durationout = Utils.getTicksBetween(this.fade.getStartFadeOut(), this.fade.getEndFadeOut());

            int startFadeIn = this.fade.getStartFadeIn() % 24000;
            int endFadeIn = this.fade.getEndFadeIn() % 24000;

            if (endFadeIn < startFadeIn) {
                endFadeIn += 24000;
            }

            int startFadeOut = this.fade.getStartFadeOut() % 24000;
            int endFadeOut = this.fade.getEndFadeOut() % 24000;

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
                maxPossibleAlpha = 1f - (((float) (endFadeIn - tempInTime)) / durationin); // fading in

            } else if (endFadeIn < tempFullTime && startFadeOut >= tempFullTime) {
                maxPossibleAlpha = 1f; // fully faded in

            } else if (startFadeOut < tempOutTime && endFadeOut >= tempOutTime) {
                maxPossibleAlpha = (float) (endFadeOut - tempOutTime) / durationout; // fading out

            } else {
                maxPossibleAlpha = 0f; // default not showing
            }

            maxPossibleAlpha *= maxAlpha;
            if (checkBiomes() && checkHeights() && checkWeather() && checkEffect()) { // check if environment is invalid
                if (alpha >= maxPossibleAlpha) {
                    alpha = maxPossibleAlpha;
                } else {
                    alpha += transitionSpeed;
                    if (alpha > maxPossibleAlpha) alpha = maxPossibleAlpha;
                }
            } else {
                if (alpha > 0f) {
                    alpha -= transitionSpeed;
                    if (alpha < 0f) alpha = 0f;
                } else {
                    alpha = 0f;
                }
            }
        } else {
            alpha = 1f;
        }

        if (alpha > SkyboxManager.MINIMUM_ALPHA) {
            if (changeFog) {
                SkyboxManager.shouldChangeFog = true;
                SkyboxManager.fogRed = this.fogColors.getRed();
                SkyboxManager.fogBlue = this.fogColors.getBlue();
                SkyboxManager.fogGreen = this.fogColors.getGreen();
            }
            if (!renderSunSkyColorTint) {
                SkyboxManager.renderSunriseAndSet = false;
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
        if (worlds.isEmpty()|| worlds.contains(client.world.getRegistryKey().getValue())) {
            return biomes.isEmpty()|| biomes.contains(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(client.player.getBlockPos())));
        }
        return false;
    }
    
    /*
		Check if player has an effect that should prevent skybox from showing
     */
    protected boolean checkEffect() {
    	ClientPlayerEntity player = MinecraftClient.getInstance().player;
    	Collection<StatusEffectInstance> activeEffects = player.getStatusEffects();
    	if (!activeEffects.isEmpty()) {
    		for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(activeEffects)) {
    			StatusEffect statusEffect = statusEffectInstance.getEffectType();
    			if (statusEffect.equals(StatusEffects.BLINDNESS)) {
    				return false;
    			}
    		}
    	}
    	return true;
    }

    /**
     * @return Whether the current heights are valid for this skybox.
     */
    protected boolean checkHeights() {
        double playerHeight = Objects.requireNonNull(MinecraftClient.getInstance().player).getY();
        boolean inRange = false;
        for (HeightEntry heightRange : this.heightRanges) {
            inRange = heightRange.getMin() < playerHeight && heightRange.getMax() > playerHeight;
            if (inRange) break;
        }
        return this.heightRanges.isEmpty() || inRange;
    }

    /**
     * @return Whether the current weather is valid for this skybox.
     */
    protected boolean checkWeather() {
        ClientWorld world = Objects.requireNonNull(MinecraftClient.getInstance().world);
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        Biome.Precipitation precipitation = world.getBiome(player.getBlockPos()).getPrecipitation();
        if (weather.size() > 0) {
            if (weather.contains("thunder") && world.isThundering()) {
                return true;
            } else if (weather.contains("snow") && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
                return true;
            } else if (weather.contains("rain") && world.isRaining() && !world.isThundering()) {
                return true;
            } else return weather.contains("clear");
        } else {
            return true;
        }
    }

    public abstract SkyboxType<? extends AbstractSkybox> getType();

    public void renderDecorations(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, BufferBuilder bufferBuilder, float alpha) {
        if (!SkyboxManager.getInstance().hasRenderedDecorations()) {
            Vector3f rotationStatic = decorations.getRotation().getStatic();
            Vector3f rotationAxis = decorations.getRotation().getAxis();

            RenderSystem.enableTexture();
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
            ClientWorld world = MinecraftClient.getInstance().world;
            assert world != null;
            RenderSystem.enableTexture();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationAxis.getX()));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationAxis.getY()));
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationAxis.getZ()));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F * decorations.getRotation().getRotationSpeed()));
            matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(rotationAxis.getZ()));
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(rotationAxis.getY()));
            matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(rotationAxis.getX()));
            float r = 1.0F - world.getRainGradient(tickDelta);
            // sun
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            Matrix4f matrix4f2 = matrices.peek().getModel();
            float s = 30.0F;
            if (decorations.isSunEnabled()) {
                worldRendererAccess.getTextureManager().bindTexture(this.decorations.getSunTexture());
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
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
                worldRendererAccess.getTextureManager().bindTexture(this.decorations.getMoonTexture());
                int t = world.getMoonPhase();
                int u = t % 4;
                int v = t / 4 % 2;
                float w = (float) (u) / 4.0F;
                float o = (float) (v) / 2.0F;
                float p = (float) (u + 1) / 4.0F;
                float q = (float) (v + 1) / 2.0F;
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
                bufferBuilder.vertex(matrix4f2, -s, -100.0F, s).texture(p, q).next();
                bufferBuilder.vertex(matrix4f2, s, -100.0F, s).texture(w, q).next();
                bufferBuilder.vertex(matrix4f2, s, -100.0F, -s).texture(w, o).next();
                bufferBuilder.vertex(matrix4f2, -s, -100.0F, -s).texture(p, o).next();
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
            }
            // stars
            if (decorations.isStarsEnabled()) {
                RenderSystem.disableTexture();
                float aa = world.method_23787(tickDelta) * r;
                if (aa > 0.0F) {
                    RenderSystem.color4f(aa, aa, aa, aa);
                    worldRendererAccess.getStarsBuffer().bind();
                    worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
                    worldRendererAccess.getStarsBuffer().draw(matrices.peek().getModel(), 7);
                    VertexBuffer.unbind();
                    worldRendererAccess.getSkyVertexFormat().endDrawing();
                }
            }
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableFog();
            matrices.pop();
        }
    }

    public Fade getFade() {
        return this.fade;
    }

    public float getMaxAlpha() {
        return this.maxAlpha;
    }

    public float getTransitionSpeed() {
        return this.transitionSpeed;
    }

    public boolean isChangeFog() {
        return this.changeFog;
    }

    public RGBA getFogColors() {
        return this.fogColors;
    }

    public boolean isRenderSunSkyColorTint() {
        return this.renderSunSkyColorTint;
    }

    public boolean isShouldRotate() {
        return this.shouldRotate;
    }

    public Decorations getDecorations() {
        return this.decorations;
    }

    public List<String> getWeather() {
        return this.weather;
    }

    public List<Identifier> getBiomes() {
        return this.biomes;
    }

    public List<Identifier> getWorlds() {
        return this.worlds;
    }

    public List<HeightEntry> getHeightRanges() {
        return this.heightRanges;
    }

    public DefaultProperties getDefaultProperties() {
        return DefaultProperties.ofSkybox(this);
    }

    public Conditions getConditions() {
        return Conditions.ofSkybox(this);
    }
}
