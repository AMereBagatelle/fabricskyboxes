package io.github.amerebagatelle.fabricskyboxes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.internal.Metadata;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class SkyboxManager {
    private static final SkyboxManager INSTANCE = new SkyboxManager();

    public static final double MINIMUM_ALPHA = 0.001;

    public static boolean shouldChangeFog;
    public static float fogRed;
    public static float fogBlue;
    public static float fogGreen;

    public static boolean renderSunriseAndSet;

    private boolean decorationsRendered;

    private final Predicate<? super AbstractSkybox> renderPredicate = (skybox) -> !this.activeSkyboxes.contains(skybox) && skybox.alpha >= MINIMUM_ALPHA;
    private final List<AbstractSkybox> skyboxes = new ArrayList<>();
    /**
     * Stores a list of permanent skyboxes
     *
     * @see #addPermanentSkybox(AbstractSkybox)
     */
    private final List<AbstractSkybox> permanentSkyboxes = new ArrayList<>();
    private final List<AbstractSkybox> activeSkyboxes = new LinkedList<>();

    public void addSkybox(Identifier identifier, JsonObject jsonObject) {
        AbstractSkybox skybox = SkyboxManager.parseSkyboxJson(identifier, new JsonObjectWrapper(jsonObject));
        if (skybox != null) {
            this.addSkybox(skybox);
        }
    }

    public void addSkybox(AbstractSkybox skybox) {
        this.skyboxes.add(Objects.requireNonNull(skybox));
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
        this.skyboxes.sort(Comparator.comparingInt(AbstractSkybox::getPriority));
    }

    /**
     * Permanent skyboxes are never cleared after a resource reload. This is
     * useful when adding skyboxes through code as resource reload listeners
     * have no defined order of being called.
     *
     * @param skybox the skybox to be added to the list of permanent skyboxes
     */
    public void addPermanentSkybox(@NotNull AbstractSkybox skybox) {
        Preconditions.checkNotNull(skybox, "Skybox was null");
        this.permanentSkyboxes.add(skybox);
    }

    @Internal
    public void clearSkyboxes() {
        this.skyboxes.clear();
        this.activeSkyboxes.clear();
    }

    @Internal
    public float getTotalAlpha() {
        return (float) StreamSupport.stream(Iterables.concat(this.skyboxes, this.permanentSkyboxes).spliterator(), false).mapToDouble(AbstractSkybox::updateAlpha).sum();
    }

    @Internal
    public void renderSkyboxes(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera) {
        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        this.skyboxes.stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        this.permanentSkyboxes.stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        // whether we should render the decorations, makes sure we don't get two suns
        this.decorationsRendered = false;
        this.activeSkyboxes.sort((skybox1, skybox2) -> skybox1.alpha >= skybox2.alpha ? 0 : 1);
        this.activeSkyboxes.forEach(skybox -> skybox.render(worldRendererAccess, matrices, matrix4f, tickDelta, camera));
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

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }
}
