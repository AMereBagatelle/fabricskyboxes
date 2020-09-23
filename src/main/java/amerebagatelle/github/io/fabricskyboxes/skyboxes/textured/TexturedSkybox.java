package amerebagatelle.github.io.fabricskyboxes.skyboxes.textured;

import amerebagatelle.github.io.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import net.minecraft.client.util.math.MatrixStack;

public class TexturedSkybox extends AbstractSkybox {
    public float[] axis;
    public boolean blend;

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
    }
}
