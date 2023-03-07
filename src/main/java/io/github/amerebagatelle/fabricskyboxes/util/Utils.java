package io.github.amerebagatelle.fabricskyboxes.util;

import com.mojang.serialization.Codec;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Function;

public class Utils {
    /**
     * Gets the amount of ticks in between start and end, on a 24000 tick system.
     *
     * @param start The start of the time you wish to measure
     * @param end   The end of the time you wish to measure
     * @return The amount of ticks in between start and end
     */
    public static int getTicksBetween(int start, int end) {
        if (end < start) end += 24000;
        return end - start;
    }

    public static boolean isInTimeInterval(int currentTime, int startTime, int endTime) {
        if (currentTime >= 24000 && currentTime < 0) {
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

    public static float clampColor(float value, float startColor, float endColor) {
        return MathHelper.clamp(value, Math.min(startColor, endColor), Math.max(startColor, endColor));
    }

    public static float getColorStepSize(float startColor, float endColor, int duration) {
        return duration == 0 || startColor == endColor ? 0 : (endColor - startColor) / duration;
    }

    public static RGBA normalizeFogColors(List<Skybox> skyboxList) {
        float[] colorSum = new float[3];
        int count = 0;
        for (Skybox skybox : skyboxList) {
            if (skybox instanceof FSBSkybox fsbSkybox) {
                if (fsbSkybox.getProperties().isChangeFog()) {
                    RGBA colors = fsbSkybox.getProperties().getFogColors();
                    colorSum[0] += colors.getRed();
                    colorSum[1] += colors.getGreen();
                    colorSum[2] += colors.getBlue();
                    count++;
                }
            }
        }
        if (count == 0) {
            return null;
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
