package me.flashyreese.mods.nuit;

import me.flashyreese.mods.nuit.components.Blend;
import me.flashyreese.mods.nuit.components.ClockSource;
import me.flashyreese.mods.nuit.components.Conditions;
import me.flashyreese.mods.nuit.components.Fade;
import me.flashyreese.mods.nuit.components.Fog;
import me.flashyreese.mods.nuit.components.Properties;
import me.flashyreese.mods.nuit.components.Rotation;
import me.flashyreese.mods.nuit.skybox.decorations.DecorationBox;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkyboxManagerTest {
    private static final ResourceLocation SUN = ResourceLocation.fromNamespaceAndPath("minecraft", "sun.png");
    private static final ResourceLocation MOON = ResourceLocation.fromNamespaceAndPath("minecraft", "moon.png");

    @Test
    public void onlyRotatingSunDecorationsControlGlobalCelestialEffects() {
        assertTrue(SkyboxManager.celestialControllerFor(decoration(true, false, 1.0F)).isPresent());
        assertFalse(SkyboxManager.celestialControllerFor(decoration(false, true, 1.0F)).isPresent());
        assertFalse(SkyboxManager.celestialControllerFor(decoration(true, false, 0.0F)).isPresent());
    }

    private static DecorationBox decoration(boolean showSun, boolean showMoon, float speed) {
        Rotation rotation = new Rotation(
                true,
                Map.of(),
                Map.of(0L, new Quaternionf()),
                24000L,
                speed
        );
        Properties properties = new Properties(
                0,
                ClockSource.defaultClock(),
                Fade.of(),
                20,
                20,
                Fog.of(),
                true,
                true,
                rotation
        );
        return new DecorationBox(
                properties,
                Conditions.of(),
                SUN,
                MOON,
                showSun,
                showMoon,
                false,
                Blend.decorations()
        );
    }
}
