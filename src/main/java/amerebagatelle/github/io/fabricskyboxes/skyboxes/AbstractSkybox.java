package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;

public abstract class AbstractSkybox {
    public float alpha;

    public int startFadeIn = 0;
    public int endFadeIn = 0;
    public int startFadeOut = 0;
    public int endFadeOut = 0;
    public float maxAlpha = 1f;
    public float transitionSpeed = 1;
    public ArrayList<String> weather = new ArrayList<>();
    public ArrayList<Identifier> biomes = new ArrayList<>();
    public ArrayList<Identifier> dimensions = new ArrayList<>();
    public ArrayList<Float[]> heightRanges = new ArrayList<>();

    public abstract void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    public float getAlpha() {
        // this probably can take a good bit of performance improvement, idk tho
        assert MinecraftClient.getInstance().world != null;
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
        if (checkBiomes() && checkHeights() && checkWeather()) { // check if environment is invalid
            if (alpha >= maxPossibleAlpha) {
                alpha = maxPossibleAlpha;
            } else {
                alpha += (Math.pow(alpha, transitionSpeed + 2f) + 0.005f);
                if (alpha > maxPossibleAlpha) alpha = maxPossibleAlpha;
            }
        } else {
            if (alpha > 0f) {
                alpha -= (Math.pow(alpha, transitionSpeed + 2f) + 0.005f);
                if (alpha < 0f) alpha = 0f;
            } else {
                alpha = 0f;
            }
        }
        return alpha;
    }

    private boolean checkBiomes() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;
        assert client.player != null;
        if (dimensions.size() == 0 || dimensions.contains(client.world.getRegistryKey().getValue())) {
            return biomes.size() == 0 || biomes.contains(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(client.player.getBlockPos())));
        }
        return false;
    }

    private boolean checkHeights() {
        assert MinecraftClient.getInstance().player != null;
        double playerHeight = MinecraftClient.getInstance().player.getY();
        boolean inRange = false;
        for (Float[] heightRange : heightRanges) {
            inRange = heightRange[0] < playerHeight && heightRange[1] > playerHeight;
            if (inRange) break;
        }
        return heightRanges.size() == 0 || inRange;
    }

    private boolean checkWeather() {
        ClientWorld world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert world != null;
        assert player != null;
        Biome.Precipitation precipitation = world.getBiome(player.getBlockPos()).getPrecipitation();
        if (weather.size() > 0) {
            if (weather.contains("thunder") && world.isThundering()) {
                return true;
            } else if (weather.contains("rain") && world.isRaining() && !world.isThundering() && precipitation == Biome.Precipitation.RAIN) {
                return true;
            } else if (weather.contains("snow") && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
                return true;
            } else return weather.contains("clear");
        } else {
            return true;
        }
    }
}
