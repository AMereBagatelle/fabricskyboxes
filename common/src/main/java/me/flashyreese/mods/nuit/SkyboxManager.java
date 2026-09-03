package me.flashyreese.mods.nuit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import me.flashyreese.mods.nuit.api.NuitApi;
import me.flashyreese.mods.nuit.api.NuitPlatformHelper;
import me.flashyreese.mods.nuit.api.skyboxes.Skybox;
import me.flashyreese.mods.nuit.components.ClockSource;
import me.flashyreese.mods.nuit.components.Metadata;
import me.flashyreese.mods.nuit.components.Rotation;
import me.flashyreese.mods.nuit.mixin.SkyRendererAccessor;
import me.flashyreese.mods.nuit.skybox.DefaultHandler;
import me.flashyreese.mods.nuit.skybox.SkyboxType;
import me.flashyreese.mods.nuit.skybox.TextureRegistrar;
import me.flashyreese.mods.nuit.skybox.decorations.DecorationBox;
import me.flashyreese.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;

import java.util.*;

public class SkyboxManager implements NuitApi {
    private static final SkyboxManager INSTANCE = new SkyboxManager();
    private final List<ResourceLocation> preloadedTextures = new ArrayList<>();
    private final Map<ResourceLocation, Skybox> skyboxMap = new Object2ObjectLinkedOpenHashMap<>();
    /**
     * Stores a list of permanent skyboxes
     *
     * @see #addPermanentSkybox(ResourceLocation, Skybox)
     */
    private final Map<ResourceLocation, Skybox> permanentSkyboxMap = new Object2ObjectLinkedOpenHashMap<>();
    private final List<Skybox> activeSkyboxes = new LinkedList<>();
    private Skybox currentSkybox = null;
    private CelestialController celestialController = null;
    private boolean celestialControllerConflict;
    private boolean enabled = true;

    public static Optional<Skybox> parseSkyboxJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        Metadata metadata;

        try {
            metadata = Metadata.CODEC.decode(JsonOps.INSTANCE, jsonObject).getOrThrow().getFirst();
        } catch (RuntimeException e) {
            NuitClient.getLogger().warn("Skipping invalid skybox {}", resourceLocation.toString(), e);
            NuitClient.getLogger().warn(jsonObject.toString());
            return Optional.empty();
        }

        SkyboxType<? extends Skybox> type = NuitPlatformHelper.INSTANCE.getSkyboxTypeRegistry().get(metadata.type());
        if (type == null) {
            NuitClient.getLogger().warn("Skipping skybox {} with unknown type {}", resourceLocation.toString(), metadata.type().getPath().replace('_', '-'));
            return Optional.empty();
        }

        try {
            return Optional.of(type.getCodec(metadata.schemaVersion()).decode(JsonOps.INSTANCE, jsonObject).getOrThrow().getFirst());
        } catch (RuntimeException e) {
            NuitClient.getLogger().warn("Skipping invalid skybox {}", resourceLocation.toString(), e);
            NuitClient.getLogger().warn(jsonObject.toString());
            return Optional.empty();
        }
    }

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }

    public void addSkybox(ResourceLocation resourceLocation, JsonObject jsonObject) {
        Optional<Skybox> skybox = SkyboxManager.parseSkyboxJson(resourceLocation, jsonObject);
        if (skybox.isPresent()) {
            NuitClient.getLogger().info("Adding skybox {}", resourceLocation.toString());
            this.addSkybox(resourceLocation, skybox.get());
        }
    }

    public void addSkybox(ResourceLocation resourceLocation, Skybox skybox) {
        Preconditions.checkNotNull(resourceLocation, "Identifier was null");
        Preconditions.checkNotNull(skybox, "Skybox was null");
        DefaultHandler.addConditions(skybox);

        if (skybox instanceof TextureRegistrar textureRegistrar) {
            textureRegistrar.getTexturesToRegister().forEach((theResourceLocation) -> {
                Minecraft.getInstance().getTextureManager().register(theResourceLocation, new SimpleTexture(theResourceLocation));
                this.preloadedTextures.add(theResourceLocation);
            });
        }

        this.skyboxMap.put(resourceLocation, skybox);
    }

    /**
     * Permanent skyboxes are never cleared after a resource reload. This is
     * useful when adding skyboxes through code as resource reload listeners
     * have no defined order of being called.
     *
     * @param skybox the skybox to be added to the list of permanent skyboxes
     */
    public void addPermanentSkybox(ResourceLocation resourceLocation, Skybox skybox) {
        Preconditions.checkNotNull(resourceLocation, "Identifier was null");
        Preconditions.checkNotNull(skybox, "Skybox was null");
        DefaultHandler.addConditions(skybox);
        this.permanentSkyboxMap.put(resourceLocation, skybox);
    }

    @Internal
    public void clearSkyboxes() {
        DefaultHandler.clearConditionsExcept(this.permanentSkyboxMap.values());
        this.skyboxMap.clear();
        this.activeSkyboxes.clear();
        this.currentSkybox = null;
        this.celestialController = null;
        this.celestialControllerConflict = false;
        this.preloadedTextures.forEach(texture -> Minecraft.getInstance().getTextureManager().release(texture));
        this.preloadedTextures.clear();
    }

    @Internal
    public void renderSkyboxes(SkyRendererAccessor skyRendererAccessor, PoseStack poseStack, Matrix4f projectionMatrix,
                               float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        for (Skybox skybox : this.activeSkyboxes) {
            this.currentSkybox = skybox;
            skybox.render(skyRendererAccessor, poseStack, projectionMatrix, tickDelta, camera, thickFog, fogCallback);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Skybox getCurrentSkybox() {
        return this.currentSkybox;
    }

    /**
     * Returns the single coherent decoration rotation that may control global celestial effects.
     */
    @Internal
    public Optional<CelestialController> getCelestialController() {
        return Optional.ofNullable(this.celestialController);
    }

    @Override
    public List<Skybox> getActiveSkyboxes() {
        return this.activeSkyboxes;
    }

    public void tick(ClientLevel level) {
        for (Skybox skybox : Iterables.concat(this.skyboxMap.values(), this.permanentSkyboxMap.values())) {
            skybox.tick(level);
        }

        this.activeSkyboxes.removeIf(skybox -> !skybox.isActive());

        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        for (Skybox skybox : Iterables.concat(this.skyboxMap.values(), this.permanentSkyboxMap.values())) {
            if (!this.activeSkyboxes.contains(skybox) && skybox.isActive()) {
                this.activeSkyboxes.add(skybox);
            }
        }
        this.activeSkyboxes.sort(Comparator.comparingInt(Skybox::getLayer));
        this.updateCelestialController();
    }

    private void updateCelestialController() {
        Set<CelestialController> controllers = new LinkedHashSet<>();
        for (Skybox skybox : this.activeSkyboxes) {
            if (skybox instanceof DecorationBox decorationBox) {
                celestialControllerFor(decorationBox).ifPresent(controllers::add);
            }
        }

        if (controllers.size() == 1) {
            this.celestialController = controllers.iterator().next();
            this.celestialControllerConflict = false;
            return;
        }

        this.celestialController = null;
        if (controllers.size() > 1 && !this.celestialControllerConflict) {
            NuitClient.getLogger().warn(
                    "Active sun decorations use conflicting celestial clocks or rotations; "
                            + "global sunrise and fog orientation will remain dimension-driven"
            );
        }
        this.celestialControllerConflict = controllers.size() > 1;
    }

    static Optional<CelestialController> celestialControllerFor(DecorationBox decorationBox) {
        Rotation rotation = decorationBox.getProperties().rotation();
        if (!decorationBox.isSunEnabled()
                || !rotation.skyboxRotation()
                || rotation.axis().isEmpty()
                || rotation.speed() == 0.0F) {
            return Optional.empty();
        }
        return Optional.of(new CelestialController(decorationBox.getProperties().clock(), rotation));
    }

    public Map<ResourceLocation, Skybox> getSkyboxMap() {
        return this.skyboxMap;
    }

    public record CelestialController(ClockSource clock, Rotation rotation) {
        public CelestialController {
            Objects.requireNonNull(clock, "Celestial clock cannot be null");
            Objects.requireNonNull(rotation, "Celestial rotation cannot be null");
        }

        public double getSkyAngleDegrees(ClientLevel level, float tickDelta) {
            double angle = Utils.calculateRotation(
                    this.rotation.speed(),
                    true,
                    level,
                    this.clock,
                    tickDelta,
                    0.0D
            );
            return Mth.positiveModulo(angle + 270.0D, 360.0D);
        }
    }
}
