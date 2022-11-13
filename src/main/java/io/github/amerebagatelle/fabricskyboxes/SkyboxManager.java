package io.github.amerebagatelle.fabricskyboxes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.github.amerebagatelle.fabricskyboxes.api.FabricSkyBoxesApi;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.internal.Metadata;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SkyboxManager implements FabricSkyBoxesApi {
    private static final SkyboxManager INSTANCE = new SkyboxManager();

    public static final double MINIMUM_ALPHA = 0.001;

    public Skybox currentSkybox = null;

    private boolean enabled = true;

    private boolean decorationsRendered;

    private final Predicate<? super Skybox> renderPredicate = (skybox) -> !this.activeSkyboxes.contains(skybox) && skybox.getAlpha() >= MINIMUM_ALPHA;

    private final Map<Identifier, Skybox> skyboxMap = new Object2ObjectLinkedOpenHashMap<>();
    /**
     * Stores a list of permanent skyboxes
     *
     * @see #addPermanentSkybox(Identifier, Skybox)
     */
    private final List<Skybox> permanentSkyboxes = new ArrayList<>();
    private final List<Skybox> activeSkyboxes = new LinkedList<>();

    public void addSkybox(Identifier identifier, JsonObject jsonObject) {
        Skybox skybox = SkyboxManager.parseSkyboxJson(identifier, new JsonObjectWrapper(jsonObject));
        if (skybox != null) {
            this.skyboxMap.put(identifier, skybox);
            this.sortSkybox();
        }
    }

    public void addSkybox(Identifier identifier, Skybox skybox) {
        Preconditions.checkNotNull(skybox, "Skybox was null");
        this.skyboxMap.put(identifier, skybox);
        this.sortSkybox();
    }

    /**
     * Sorts skyboxes by ascending order with priority field. Skyboxes with
     * identical priority will not be re-ordered, this will largely come down to
     * the alphabetical order that Minecraft resources load in.
     * <p>
     * Minecraft's resource loading order example:
     * "fabricskyboxes:sky/overworld_sky1.json"
     * "fabricskyboxes:sky/overworld_sky10.json"
     * "fabricskyboxes:sky/overworld_sky11.json"
     * "fabricskyboxes:sky/overworld_sky2.json"
     */
    private void sortSkybox() {
        Map<Identifier, Skybox> newSortedMap = this.skyboxMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(skybox -> skybox.getProperties().getPriority())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (skybox, skybox2) -> skybox, Object2ObjectLinkedOpenHashMap::new));
        this.skyboxMap.clear();
        this.skyboxMap.putAll(newSortedMap);
    }

    /**
     * Permanent skyboxes are never cleared after a resource reload. This is
     * useful when adding skyboxes through code as resource reload listeners
     * have no defined order of being called.
     *
     * @param skybox the skybox to be added to the list of permanent skyboxes
     */
    public void addPermanentSkybox(@NotNull Identifier identifier, @NotNull Skybox skybox) {
        Preconditions.checkNotNull(skybox, "Skybox was null");
        this.permanentSkyboxes.add(skybox);
    }

    @Internal
    public void clearSkyboxes() {
        this.skyboxMap.clear();
        this.activeSkyboxes.clear();
    }

    @Internal
    public float getTotalAlpha() {
        return (float) StreamSupport.stream(Iterables.concat(this.skyboxMap.values(), this.permanentSkyboxes).spliterator(), false).mapToDouble(Skybox::updateAlpha).sum();
    }

    @Internal
    public void renderSkyboxes(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog) {
        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        this.skyboxMap.values().stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        this.permanentSkyboxes.stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        // whether we should render the decorations, makes sure we don't get two suns
        this.decorationsRendered = false;
        this.activeSkyboxes.sort((skybox1, skybox2) -> skybox1.getAlpha() >= skybox2.getAlpha() ? 0 : 1);
        this.activeSkyboxes.forEach(skybox -> {
            this.currentSkybox = skybox;
            skybox.render(worldRendererAccess, matrices, matrix4f, tickDelta, camera, thickFog);
        });
        this.activeSkyboxes.removeIf((skybox) -> skybox.updateAlpha() <= MINIMUM_ALPHA);
    }

    @Internal
    public boolean hasRenderedDecorations() {
        if (this.decorationsRendered) {
            return true;
        } else {
            this.decorationsRendered = true;
            return false;
        }
    }

    public static AbstractSkybox parseSkyboxJson(Identifier id, JsonObjectWrapper objectWrapper) {
        AbstractSkybox skybox;
        Metadata metadata;

        try {
            metadata = Metadata.CODEC.decode(JsonOps.INSTANCE, objectWrapper.getFocusedObject()).getOrThrow(false, System.err::println).getFirst();
        } catch (RuntimeException e) {
            FabricSkyBoxesClient.getLogger().warn("Skipping invalid skybox " + id.toString(), e);
            FabricSkyBoxesClient.getLogger().warn(objectWrapper.toString());
            return null;
        }

        SkyboxType<? extends AbstractSkybox> type = SkyboxType.REGISTRY.get(metadata.getType());
        Preconditions.checkNotNull(type, "Unknown skybox type: " + metadata.getType().getPath().replace('_', '-'));
        if (metadata.getSchemaVersion() == 1) {
            Preconditions.checkArgument(type.isLegacySupported(), "Unsupported schema version '1' for skybox type " + type.getName());
            FabricSkyBoxesClient.getLogger().debug("Using legacy deserializer for skybox " + id.toString());
            skybox = type.instantiate();
            //noinspection ConstantConditions
            type.getDeserializer().getDeserializer().accept(objectWrapper, skybox);
        } else {
            skybox = type.getCodec(metadata.getSchemaVersion()).decode(JsonOps.INSTANCE, objectWrapper.getFocusedObject()).getOrThrow(false, System.err::println).getFirst();
        }
        return skybox;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }
}
