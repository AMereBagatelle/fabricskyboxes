package io.github.amerebagatelle.fabricskyboxes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
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
    private final ArrayList<AbstractSkybox> skyboxes = new ArrayList<>();
    /**
     * Stores a list of permanent skyboxes
     *
     * @see #addPermanentSkybox(AbstractSkybox)
     */
    private final ArrayList<AbstractSkybox> permanentSkyboxes = new ArrayList<>();
    private final LinkedList<AbstractSkybox> activeSkyboxes = new LinkedList<>();

    public void addSkybox(AbstractSkybox skybox) {
        skyboxes.add(Objects.requireNonNull(skybox));
    }

    /**
     * Permanent skyboxes are never cleared after a resource reload. This is
     * useful when adding skyboxes through code as resource reload listeners
     * have no defined order of being called.
     * @param skybox the skybox to be added to the list of permanent skyboxes
     */
    public void addPermanentSkybox(@NotNull AbstractSkybox skybox) {
        Preconditions.checkNotNull(skybox, "Skybox was null");
        this.permanentSkyboxes.add(skybox);
    }

    @Internal
    public void clearSkyboxes() {
        skyboxes.clear();
        activeSkyboxes.clear();
    }

    @Internal
    public float getTotalAlpha() {
        return (float) StreamSupport.stream(Iterables.concat(this.skyboxes, this.permanentSkyboxes).spliterator(), false).mapToDouble(AbstractSkybox::getAlpha).sum();
    }

    @Internal
    public void renderSkyboxes(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        this.skyboxes.stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        this.permanentSkyboxes.stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        // whether we should render the decorations, makes sure we don't get two suns
        decorationsRendered = false;
        this.activeSkyboxes.forEach(skybox -> skybox.render(worldRendererAccess, matrices, tickDelta));
        this.activeSkyboxes.removeIf((skybox) -> skybox.getAlpha() <= MINIMUM_ALPHA);
    }

    @Internal
    public boolean hasRenderedDecorations() {
        if (decorationsRendered) {
            return true;
        } else {
            decorationsRendered = true;
            return false;
        }
    }

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }
}
