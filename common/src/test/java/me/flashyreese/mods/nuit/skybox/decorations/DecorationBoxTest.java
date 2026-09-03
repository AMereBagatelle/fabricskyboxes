package me.flashyreese.mods.nuit.skybox.decorations;

import me.flashyreese.mods.nuit.components.Blend;
import me.flashyreese.mods.nuit.components.Conditions;
import me.flashyreese.mods.nuit.components.Properties;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DecorationBoxTest {
    private static final ResourceLocation SUN =
            ResourceLocation.fromNamespaceAndPath("test", "textures/sky/sun.png");
    private static final ResourceLocation MOON =
            ResourceLocation.fromNamespaceAndPath("test", "textures/sky/moon.png");

    @Test
    public void disabledDecorationsDoNotRegisterTextures() {
        assertTrue(decoration(false, false).getTexturesToRegister().isEmpty());
    }

    @Test
    public void enabledCustomSunAndMoonRegisterTextures() {
        assertEquals(List.of(SUN, MOON), decoration(true, true).getTexturesToRegister());
    }

    private static DecorationBox decoration(boolean showSun, boolean showMoon) {
        return new DecorationBox(
                Properties.decorations(),
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
