package io.github.amerebagatelle.fabricskyboxes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import com.google.common.collect.Iterables;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;

import net.minecraft.client.util.math.MatrixStack;

public class SkyboxManager {
    private static final SkyboxManager INSTANCE = new SkyboxManager();

    public static boolean shouldChangeFog;
    public static float fogRed;
    public static float fogBlue;
    public static float fogGreen;

    private boolean decorationsRendered;

    private final Predicate<? super AbstractSkybox> renderPredicate = (skybox) -> !this.activeSkyboxes.contains(skybox) && skybox.alpha >= 0.1;
    private final ArrayList<AbstractSkybox> skyboxes = new ArrayList<>();
    private final ArrayList<AbstractSkybox> permanentSkyboxes = new ArrayList<>();
    private final LinkedList<AbstractSkybox> activeSkyboxes = new LinkedList<>();

    public void addSkybox(AbstractSkybox skybox) {
        skyboxes.add(Objects.requireNonNull(skybox));
    }

    public void addPermanentSkybox(AbstractSkybox skybox) {
        this.permanentSkyboxes.add(Objects.requireNonNull(skybox));
    }

    public void clearSkyboxes() {
        skyboxes.clear();
        activeSkyboxes.clear();
    }

    public float getTotalAlpha() {
        return (float) StreamSupport.stream(Iterables.concat(this.skyboxes, this.permanentSkyboxes).spliterator(), false).mapToDouble(AbstractSkybox::getAlpha).sum();
    }

    public void renderSkyboxes(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        this.skyboxes.stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        this.permanentSkyboxes.stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        // whether we should render the decorations, makes sure we don't get two suns
        decorationsRendered = false;
        this.activeSkyboxes.forEach(skybox -> skybox.render(worldRendererAccess, matrices, tickDelta));
        this.activeSkyboxes.removeIf((skybox) -> skybox.getAlpha() <= 0.1);
    }

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
