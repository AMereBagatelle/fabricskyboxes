package io.github.amerebagatelle.fabricskyboxes.util;

import com.google.common.collect.Range;
import com.mojang.serialization.Codec;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.FogRGBA;
import io.github.amerebagatelle.fabricskyboxes.util.object.MinMaxEntry;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import io.github.amerebagatelle.fabricskyboxes.util.object.UVRange;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Utils {

    /**
     * Maps input intersection to output intersection, does so by taking in input and output UV ranges and then mapping the input intersection to the output intersection.
     *
     * @param input             The input UV range
     * @param output            The output UV range
     * @param inputIntersection The input intersection
     * @return The output intersection
     */
    public static UVRange mapUVRanges(UVRange input, UVRange output, UVRange inputIntersection) {
        float u1 = (inputIntersection.getMinU() - input.getMinU()) / (input.getMaxU() - input.getMinU()) * (output.getMaxU() - output.getMinU()) + output.getMinU();
        float u2 = (inputIntersection.getMaxU() - input.getMinU()) / (input.getMaxU() - input.getMinU()) * (output.getMaxU() - output.getMinU()) + output.getMinU();
        float v1 = (inputIntersection.getMinV() - input.getMinV()) / (input.getMaxV() - input.getMinV()) * (output.getMaxV() - output.getMinV()) + output.getMinV();
        float v2 = (inputIntersection.getMaxV() - input.getMinV()) / (input.getMaxV() - input.getMinV()) * (output.getMaxV() - output.getMinV()) + output.getMinV();
        return new UVRange(u1, v1, u2, v2);
    }

    /**
     * Finds the intersection between two UV ranges
     *
     * @param first  First UV range
     * @param second Second UV range
     * @return The intersection between the two UV ranges, if none is found, null is returned
     */
    public static UVRange findUVIntersection(UVRange first, UVRange second) {
        float intersectionMinU = Math.max(first.getMinU(), second.getMinU());
        float intersectionMaxU = Math.min(first.getMaxU(), second.getMaxU());
        float intersectionMinV = Math.max(first.getMinV(), second.getMinV());
        float intersectionMaxV = Math.min(first.getMaxV(), second.getMaxV());

        if (intersectionMaxU >= intersectionMinU && intersectionMaxV >= intersectionMinV) {
            return new UVRange(intersectionMinU, intersectionMinV, intersectionMaxU, intersectionMaxV);
        } else {
            // No intersection
            return null;
        }
    }

    /**
     * @return Whether the value is within any of the minMaxEntries.
     */
    public static boolean checkRanges(double value, List<MinMaxEntry> minMaxEntries) {
        return minMaxEntries.isEmpty() || minMaxEntries.stream()
                .anyMatch(minMaxEntry -> Range.closed(minMaxEntry.getMin(), minMaxEntry.getMax())
                        .contains((float) value));
    }

    /**
     * Helper method to log warnings when normalizing/debugging
     *
     * @param initialValue Initial value
     * @param finalValue   Final value
     * @param message      Desired message
     * @param <T>          Any type
     * @return finalValue
     */
    public static <T> T warnIfDifferent(T initialValue, T finalValue, String message) {
        if (!initialValue.equals(finalValue) && FabricSkyBoxesClient.config().generalSettings.debugMode) {
            FabricSkyBoxesClient.getLogger().warn(message);
        }
        return finalValue;
    }

    /**
     * Normalizes any tick time outside 0-23999
     *
     * @param tickTime Time in ticks
     * @return Normalized tickTime
     */
    public static int normalizeTickTime(long tickTime) {
        long result = tickTime % 24000;
        return (int) (result >= 0 ? result : result + 24000);
    }

    /**
     * Calculates the rotation in degrees for skybox rotations
     *
     * @param rotationSpeed    Rotation speed
     * @param timeShift        Time shift (by default 0, OptiFine starts at 18000)
     * @param isSkyboxRotation Whether it is a skybox rotation or decoration rotation
     * @param world            Client world
     * @return Rotation in degrees
     */
    public static double calculateRotation(double rotationSpeed, int timeShift, boolean isSkyboxRotation, ClientWorld world) {
        if (rotationSpeed != 0F) {
            long timeOfDay = world.getTimeOfDay() + timeShift;
            double rotationFraction = timeOfDay / (24000.0D / rotationSpeed);
            double skyAngle = MathHelper.floorMod(rotationFraction, 1);
            if (isSkyboxRotation) {
                return 360D * skyAngle;
            } else {
                return 360D * world.getDimension().getSkyAngle((long) (24000 * skyAngle));
            }
        } else {
            return 0D;
        }
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

    /**
     * Calculates the fade alpha
     *
     * @param maxAlpha     The maximum alpha value
     * @param minAlpha     The minimum alpha value
     * @param currentTime  The current world time
     * @param startFadeIn  The fade in start time
     * @param endFadeIn    The fade in end time
     * @param startFadeOut The fade out start time
     * @param endFadeOut   The fade out end time
     * @return Fade Alpha
     */
    public static float calculateFadeAlphaValue(float maxAlpha, float minAlpha, int currentTime, int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut) {
        if (isInTimeInterval(currentTime, endFadeIn, startFadeOut)) {
            return maxAlpha;
        } else if (isInTimeInterval(currentTime, startFadeIn, endFadeIn)) {
            int fadeInDuration = calculateCyclicTimeDistance(startFadeIn, endFadeIn);
            int timePassedSinceFadeInStart = calculateCyclicTimeDistance(startFadeIn, currentTime);
            return minAlpha + ((float) timePassedSinceFadeInStart / fadeInDuration) * (maxAlpha - minAlpha);
        } else if (isInTimeInterval(currentTime, startFadeOut, endFadeOut)) {
            int fadeOutDuration = calculateCyclicTimeDistance(startFadeOut, endFadeOut);
            int timePassedSinceFadeOutStart = calculateCyclicTimeDistance(startFadeOut, currentTime);
            return maxAlpha + ((float) timePassedSinceFadeOutStart / fadeOutDuration) * (minAlpha - maxAlpha);
        } else {
            return minAlpha;
        }
    }

    /**
     * Calculates the cyclic distance (duration) between two time points on a cyclic timescale.
     *
     * @param startTime The first time point.
     * @param endTime   The second time point.
     * @return The cyclic distance between the two time points.
     */
    public static int calculateCyclicTimeDistance(int startTime, int endTime) {
        return (endTime - startTime + 24000) % 24000;
    }

    /**
     * Blends all fog colors using the alpha blending formula: (source * source_alpha) + (destination * (1 - source_alpha)).
     *
     * @param skyboxList      List of skyboxes to blend the fog colors from.
     * @param initialFogColor The initial fog color to be blended with the skybox fog colors.
     * @return The final blended fog color.
     */
    public static FogRGBA alphaBlendFogColors(List<Skybox> skyboxList, RGBA initialFogColor) {
        List<FogRGBA> activeColors = skyboxList.stream()
                .filter(Skybox::isActive) // check if active
                .filter(FSBSkybox.class::isInstance) // check if our own skybox impl
                .map(FSBSkybox.class::cast) // cast to our own skybox impl
                .filter(fsbSkybox -> fsbSkybox.getProperties().isChangeFog())// check if fog is changed
                .map(fsbSkybox -> new FogRGBA(fsbSkybox.getProperties().getFogColors().getRed(),
                        fsbSkybox.getProperties().getFogColors().getGreen(),
                        fsbSkybox.getProperties().getFogColors().getBlue(),
                        fsbSkybox.getAlpha() / fsbSkybox.getProperties().getMaxAlpha(),
                        fsbSkybox.getProperties().getFogColors().getAlpha()))
                .toList(); // map RGB fog colors and A to skybox alpha
        if (activeColors.isEmpty()) {
            return null;
        } else {
            FogRGBA destination = new FogRGBA(initialFogColor);
            for (FogRGBA source : activeColors) {
                // Alpha blending
                float sourceAlphaInv = 1f - source.getAlpha();

                float red = (source.getRed() * source.getAlpha()) + (destination.getRed() * sourceAlphaInv);
                float green = (source.getGreen() * source.getAlpha()) + (destination.getGreen() * sourceAlphaInv);
                float blue = (source.getBlue() * source.getAlpha()) + (destination.getBlue() * sourceAlphaInv);
                float alpha = (source.getAlpha() * source.getAlpha()) + (destination.getAlpha() * sourceAlphaInv);
                float density = (source.getDensity() * source.getAlpha()) + (destination.getDensity() * sourceAlphaInv);

                destination = new FogRGBA(red, green, blue, alpha, density);
            }
            return destination;
        }
    }

    /**
     * Uses weighted additive color mixing and then applies the alpha blending formula: (source * source_alpha) + (destination * (1 - source_alpha)) with the initial fog color.
     *
     * @param skyboxList      List of skyboxes to blend the fog colors from.
     * @param initialFogColor The initial fog color to be blended with the skybox fog colors.
     * @return The weighted additive color with the final blended color using the alpha blending formula along with the initial fog color.
     */
    public static RGBA weightedAdditiveBlendFogColors(List<Skybox> skyboxList, RGBA initialFogColor) {
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
        final RGBA originalFogColorModified = new RGBA(initialFogColor.getRed() * diffMul, initialFogColor.getGreen() * diffMul, initialFogColor.getBlue() * diffMul);
        final RGBA activeColorsMixedFinal = new RGBA(activeColorsMixed.getRed() * activeColorsMaxAlpha, activeColorsMixed.getGreen() * activeColorsMaxAlpha, activeColorsMixed.getBlue() * activeColorsMaxAlpha);

        return new RGBA(originalFogColorModified.getRed() + activeColorsMixedFinal.getRed(), originalFogColorModified.getGreen() + activeColorsMixedFinal.getGreen(), originalFogColorModified.getBlue() + activeColorsMixedFinal.getBlue());
    }

    /**
     * Calculates the condition alpha
     *
     * @param maxAlpha  The maximum alpha value
     * @param minAlpha  The minimum alpha value
     * @param lastAlpha The last condition alpha value
     * @param duration  The duration
     * @param in        Whether it will transition in or out
     * @return condition alpha
     */
    public static float calculateConditionAlphaValue(float maxAlpha, float minAlpha, float lastAlpha, int duration, boolean in) {
        if (duration == 0) {
            return lastAlpha;
        } else if (in && maxAlpha == lastAlpha) {
            return maxAlpha;
        } else if (!in && lastAlpha == minAlpha) {
            return minAlpha;
        } else {
            float alphaChange = (maxAlpha - minAlpha) / duration;
            float result = in ? lastAlpha + alphaChange : lastAlpha - alphaChange;
            return MathHelper.clamp(result, minAlpha, maxAlpha);
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
