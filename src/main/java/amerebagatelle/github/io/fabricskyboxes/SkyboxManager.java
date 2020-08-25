package amerebagatelle.github.io.fabricskyboxes;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;

public class SkyboxManager {
    private static final SkyboxManager INSTANCE = new SkyboxManager();

    private static final ArrayList<AbstractSkybox> skyboxes = new ArrayList<>();

    public void addSkybox(AbstractSkybox skybox) {
        skyboxes.add(skybox);
    }

    public float getTotalAlpha() {
        float f = 0f;
        for (AbstractSkybox skybox : skyboxes) {
            f += updateSkyboxAlpha(skybox);
        }
        return f;
    }

    private float updateSkyboxAlpha(AbstractSkybox skybox) {
        skybox.alpha = 1f;
        return skybox.alpha;
    }

    public void renderSkyboxes(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        for (AbstractSkybox skybox : skyboxes) {
            skybox.render(worldRendererAccess, matrices, tickDelta);
        }
    }

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }
}
