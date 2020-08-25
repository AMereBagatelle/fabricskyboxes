package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractSkybox {
    public final MinecraftClient client = MinecraftClient.getInstance();
    public float alpha;

    public abstract void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);
}
