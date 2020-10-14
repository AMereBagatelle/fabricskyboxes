package io.github.amerebagatelle.fabricskyboxes;

import com.google.common.collect.Lists;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class SkyboxManager {
    private static final SkyboxManager INSTANCE = new SkyboxManager();

    public static boolean shouldChangeFog;
    public static float fogRed;
    public static float fogBlue;
    public static float fogGreen;

    private boolean decorationsRendered;

    private static final List<Supplier<? extends AbstractSkybox>> SKYBOX_TYPES = Lists.newArrayList();

    public static void addSkyboxType(Supplier<? extends AbstractSkybox> skyboxSupplier) {
        SKYBOX_TYPES.add(skyboxSupplier);
    }

    public static List<Supplier<? extends AbstractSkybox>> getSkyboxTypes() {
        return SKYBOX_TYPES;
    }

    private static final ArrayList<AbstractSkybox> skyboxes = new ArrayList<>();
    private final LinkedList<AbstractSkybox> activeSkyboxes = new LinkedList<>();

    public void addSkybox(AbstractSkybox skybox) {
        skyboxes.add(Objects.requireNonNull(skybox));
    }

    public void clearSkyboxes() {
        skyboxes.clear();
        activeSkyboxes.clear();
    }

    public float getTotalAlpha() {
        float f = 0f;
        for (AbstractSkybox skybox : skyboxes) {
            f += skybox.getAlpha();
        }
        return f;
    }

    public void renderSkyboxes(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        for (AbstractSkybox skybox : skyboxes) {
            if (!activeSkyboxes.contains(skybox) && skybox.alpha >= 0.1) {
                activeSkyboxes.add(skybox);
            }
        }
        // whether we should render the decorations, makes sure we don't get two suns
        decorationsRendered = false;
        for (AbstractSkybox skybox : activeSkyboxes) {
            skybox.render(worldRendererAccess, matrices, tickDelta);
        }
        activeSkyboxes.removeIf((skybox) -> skybox.getAlpha() <= 0.1);
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
