package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractSkybox {
    public float alpha;

    public int startFadeIn = 0;
    public int endFadeIn = 0;
    public int startFadeOut = 0;
    public int endFadeOut = 0;

    public abstract void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    public float getAlpha() {
        int currentTime = (int) MinecraftClient.getInstance().world.getTimeOfDay();
        int duration = Utils.getTicksBetween(startFadeIn, endFadeIn);
        int phase = 0;
        if (startFadeIn < currentTime && endFadeIn > currentTime) {
            phase = 1;
        } else if (endFadeIn < currentTime && startFadeOut > currentTime) {
            phase = 3; // fully faded in
        } else if (startFadeOut < currentTime && endFadeOut > currentTime) {
            phase = 2;
        }
        float result;
        switch (phase) {
            case 1:
                result = 1f - (((float) (startFadeIn + duration - currentTime)) / duration);
                break;

            case 2:
                result = (float) (endFadeOut - currentTime) / duration;
                break;

            case 3:
                result = 1f;
                break;

            default:
                result = 0f;
        }
        alpha = result;
        return alpha;
    }
}
