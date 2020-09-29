package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.LinkedList;

public class SkyboxManager {
    private static final SkyboxManager INSTANCE = new SkyboxManager();

    public static boolean shouldChangeFog;
    public static float fogRed;
    public static float fogBlue;
    public static float fogGreen;

    private boolean decorationsRendered;

    private static final ArrayList<Class<? extends AbstractSkybox>> skyboxTypes = new ArrayList<>();

    public static void addSkyboxType(Class<? extends AbstractSkybox> skyboxClass) {
        skyboxTypes.add(skyboxClass);
    }

    public static ArrayList<Class<? extends AbstractSkybox>> getSkyboxTypes() {
        return skyboxTypes;
    }

    private static final ArrayList<AbstractSkybox> skyboxes = new ArrayList<>();
    private final LinkedList<AbstractSkybox> activeSkyboxes = new LinkedList<>();

    public void addSkybox(AbstractSkybox skybox) {
        skyboxes.add(skybox);
    }

    public void clearSkyboxes() {
        skyboxes.clear();
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
