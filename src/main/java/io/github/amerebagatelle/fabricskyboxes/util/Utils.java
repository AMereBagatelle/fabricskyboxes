package io.github.amerebagatelle.fabricskyboxes.util;

import com.google.common.collect.Range;
import com.mojang.serialization.Codec;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.MinMaxEntry;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Function;

public class Utils {
    /**
     * @return Whether the value is within any of the minMaxEntries.
     */
    public static boolean checkRanges(double value, List<MinMaxEntry> minMaxEntries) {
        return minMaxEntries.isEmpty() || minMaxEntries.stream()
                .anyMatch(minMaxEntry -> Range.closed(minMaxEntry.getMin(), minMaxEntry.getMax())
                        .contains((float) value));
    }

    /**
     * Normalizes any tick time outside 0-23999
     *
     * @param tickTime Time in ticks
     * @return Normalized tickTime
     */
    public static int normalizeTickTime(int tickTime) {
        int result = tickTime % 24000;
        return result >= 0 ? result : result + 24000;
    }

    public static boolean isInTimeInterval(int currentTime, int startTime, int endTime) {
        if (currentTime < 0 || currentTime >= 24000) {
            throw new RuntimeException("Invalid current time, value must be between 0-23999: " + currentTime);
        }
        if (startTime <= endTime) {
            return currentTime >= startTime && currentTime <= endTime;
        } else {
            return currentTime >= startTime || currentTime <= endTime;
        }
    }

    public static float normalizeTime(float maxAlpha, int currentTime, int startTime, int endTime) {
        if (!isInTimeInterval(currentTime, startTime, endTime))
            return 0f;

        int range = (endTime - startTime + 24000) % 24000;
        if (range == 0) {
            return 0f;
        }

        float position = (float) ((currentTime - startTime + 24000) % 24000) / range;
        return position * maxAlpha;
    }

    public static float calculateFadeAlphaNormal(float maxAlpha, int currentTime, int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut) {
        if (isInTimeInterval(currentTime, endFadeIn, startFadeOut)) {
            return maxAlpha;
        } else if (isInTimeInterval(currentTime, startFadeIn, endFadeIn)) {
            return ((float) (currentTime - startFadeIn) / (endFadeIn - startFadeIn)) * maxAlpha;
        } else if (isInTimeInterval(currentTime, startFadeOut, endFadeOut)) {
            return 1.0f - ((float) (currentTime - startFadeOut) / (endFadeOut - startFadeOut)) * maxAlpha;
        } else {
            return 0f;
        }
    }

    public static float calculateFadeAlphaUnexpected(float maxAlpha, float lastAlpha, int duration, boolean in) {
        if (in && maxAlpha == lastAlpha) {
            return maxAlpha;
        } else if (!in && lastAlpha == 0f) {
            return 0f;
        } else {
            float alphaChange = maxAlpha / duration;
            return in ? lastAlpha + alphaChange : lastAlpha - alphaChange;
        }
    }

    public static RGBA blendFogColorsFromSkies(List<Skybox> skyboxList, RGBA originalFogColor) {
        float[] colorSum = new float[3];
        int count = 0;
        List<RGBA> activeColors = skyboxList.stream()
                .filter(FSBSkybox.class::isInstance)
                .map(FSBSkybox.class::cast)
                .filter(fsbSkybox -> fsbSkybox.getProperties().isChangeFog())
                .map(fsbSkybox -> new RGBA(fsbSkybox.getProperties().getFogColors().getRed() * fsbSkybox.getAlpha() / fsbSkybox.getProperties().getMaxAlpha(),
                        fsbSkybox.getProperties().getFogColors().getGreen() * fsbSkybox.getAlpha() / fsbSkybox.getProperties().getMaxAlpha(),
                        fsbSkybox.getProperties().getFogColors().getBlue() * fsbSkybox.getAlpha() / fsbSkybox.getProperties().getMaxAlpha()))
                .toList();
        for (RGBA rgba : activeColors) {
            colorSum[0] += rgba.getRed();
            colorSum[1] += rgba.getGreen();
            colorSum[2] += rgba.getBlue();
            count++;
        }
        if (count == 0) {
            return null;
        }
        if (originalFogColor != null) {
            colorSum[0] += originalFogColor.getRed();
            colorSum[1] += originalFogColor.getGreen();
            colorSum[2] += originalFogColor.getBlue();
            count++;
        }
        float invCount = 1.0f / count;
        return new RGBA(colorSum[0] * invCount, colorSum[1] * invCount, colorSum[2] * invCount);
    }

    public static Codec<Integer> getClampedInteger(int min, int max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        }
        return Codec.INT.xmap(f -> MathHelper.clamp(f, min, max), Function.identity());
    }

    public static Codec<Float> getClampedFloat(float min, float max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        }
        return Codec.FLOAT.xmap(f -> MathHelper.clamp(f, min, max), Function.identity());
    }

    public static Codec<Double> getClampedDouble(double min, double max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        }
        return Codec.DOUBLE.xmap(f -> MathHelper.clamp(f, min, max), Function.identity());
    }
}
