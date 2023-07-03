package io.github.amerebagatelle.fabricskyboxes.util;

import com.google.common.collect.Range;
import com.mojang.serialization.Codec;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.MinMaxEntry;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    /**
     * Checks whether current time is within start and end time, this method also supports roll over checks
     *
     * @param currentTime The current world time
     * @param startTime   The start time
     * @param endTime     The end time
     * @return Whether current time is within start and end time
     */
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

    public static RGBA blendFogColorsFromSkies(List<Skybox> skyboxList, RGBA originalFogColor) {
        float[] colorSum = new float[4];
        List<RGBA> activeColors = skyboxList.stream()
                .filter(Skybox::isActive)
                .filter(FSBSkybox.class::isInstance)
                .map(FSBSkybox.class::cast)
                .filter(fsbSkybox -> fsbSkybox.getProperties().isChangeFog())
                .map(fsbSkybox -> new RGBA(fsbSkybox.getProperties().getFogColors().getRed(),
                    fsbSkybox.getProperties().getFogColors().getGreen(),
                    fsbSkybox.getProperties().getFogColors().getBlue(),
                        fsbSkybox.getAlpha() / fsbSkybox.getProperties().getMaxAlpha()))
                .toList();
        if (activeColors.size() == 0) {
            return null;
        }
        for (RGBA rgba : activeColors) {
            colorSum[0] += rgba.getRed() * rgba.getAlpha();
            colorSum[1] += rgba.getGreen() * rgba.getAlpha();
            colorSum[2] += rgba.getBlue() * rgba.getAlpha();
            colorSum[3] += rgba.getAlpha(); // this should never be zero.
        }
        float finalAlpha = colorSum[3];
        final RGBA activeColorsMixed = new RGBA(colorSum[0] / finalAlpha, colorSum[1] / finalAlpha, colorSum[2] / finalAlpha);

        Optional<RGBA> activeColorsHighestAlpha = activeColors.stream().max(Comparator.comparingDouble(RGBA::getAlpha));
        float activeColorsMaxAlpha = activeColorsHighestAlpha.get().getAlpha();

        float diffMul = 1f - activeColorsMaxAlpha;
        final RGBA originalFogColorModified = new RGBA(originalFogColor.getRed() * diffMul, originalFogColor.getGreen() * diffMul, originalFogColor.getBlue() * diffMul);
        final RGBA activeColorsMixedFinal = new RGBA(activeColorsMixed.getRed() * activeColorsMaxAlpha, activeColorsMixed.getGreen() * activeColorsMaxAlpha, activeColorsMixed.getBlue() * activeColorsMaxAlpha);

        return new RGBA(originalFogColorModified.getRed() + activeColorsMixedFinal.getRed(), originalFogColorModified.getGreen() + activeColorsMixedFinal.getGreen(), originalFogColorModified.getBlue() + activeColorsMixedFinal.getBlue());
    }

    /**
     * Calculates the fade alpha
     *
     * @param maxAlpha     The maximum alpha value
     * @param currentTime  The current world time
     * @param startFadeIn  The fade in start time
     * @param endFadeIn    The fade in end time
     * @param startFadeOut The fade out start time
     * @param endFadeOut   The fade out end time
     * @return Fade Alpha
     */
    public static float calculateFadeAlphaValue(float maxAlpha, int currentTime, int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut) {
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

    /**
     * Calculates the condition alpha
     *
     * @param maxAlpha  The maximum alpha value
     * @param lastAlpha The last condition alpha value
     * @param duration  The duration
     * @param in        Whether it will transition in or out
     * @return condition alpha
     */
    public static float calculateConditionAlphaValue(float maxAlpha, float lastAlpha, int duration, boolean in) {
        if (duration == 0) {
            return lastAlpha;
        } else if (in && maxAlpha == lastAlpha) {
            return maxAlpha;
        } else if (!in && lastAlpha == 0f) {
            return 0f;
        } else {
            float alphaChange = maxAlpha / duration;
            float result = in ? lastAlpha + alphaChange : lastAlpha - alphaChange;
            return MathHelper.clamp(result, 0f, maxAlpha);
        }
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
