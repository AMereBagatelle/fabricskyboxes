package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public abstract class AbstractSkybox {
    public float alpha;

    public int startFadeIn = 0;
    public int endFadeIn = 0;
    public int startFadeOut = 0;
    public int endFadeOut = 0;
    public float maxAlpha = 1f;
    public float transitionSpeed = 1;
    public ArrayList<Identifier> biomes;
    public ArrayList<Identifier> dimensions;

    public abstract void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    public float getAlpha() {
        int currentTime = (int) MinecraftClient.getInstance().world.getTimeOfDay();
        int duration = Utils.getTicksBetween(startFadeIn, endFadeIn);
        int phase = 0; // default not showing
        if (startFadeIn < currentTime && endFadeIn > currentTime) {
            phase = 1; // fading out
        } else if (endFadeIn < currentTime && startFadeOut > currentTime) {
            phase = 3; // fully faded in
        } else if (startFadeOut < currentTime && endFadeOut > currentTime) {
            phase = 2; // fading in
        }
        // phase set to 5 if it is currently a invalid situation for the skybox to appear

        float maxPossibleAlpha;
        switch (phase) {
            case 1:
                maxPossibleAlpha = 1f - (((float) (startFadeIn + duration - currentTime)) / duration);
                break;

            case 2:
                maxPossibleAlpha = (float) (endFadeOut - currentTime) / duration;
                break;

            case 3:
                maxPossibleAlpha = 1f;
                break;

            default:
                maxPossibleAlpha = 0f;
        }
        maxPossibleAlpha *= maxAlpha;
        if (checkBiomes()) {
            if (alpha >= maxPossibleAlpha) {
                alpha = maxPossibleAlpha;
            } else {
                alpha += (Math.pow(alpha, transitionSpeed) / 10 + 0.05f);
                if (alpha > maxPossibleAlpha) alpha = maxPossibleAlpha;
            }
        }
        return alpha;
    }

    private boolean checkBiomes() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;
        assert client.player != null;
        if (dimensions == null || dimensions.contains(client.world.getRegistryKey().getValue())) {
            return biomes == null || biomes.contains(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(client.player.getBlockPos())));
        }
        return false;
    }
}
