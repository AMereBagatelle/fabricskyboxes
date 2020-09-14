package amerebagatelle.github.io.fabricskyboxes;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
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
        for (AbstractSkybox skybox : skyboxes) {
            if (!activeSkyboxes.contains(skybox) && skybox.alpha >= 0.1) {
                activeSkyboxes.add(skybox);
            }
        }
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
