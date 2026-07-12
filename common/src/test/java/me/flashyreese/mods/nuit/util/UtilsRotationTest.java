package me.flashyreese.mods.nuit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsRotationTest {
    @Test
    public void skyboxRotationUsesUniformClockAngle() {
        double rotation = Utils.calculateRotation(1.0D, true, 6000L, 282.0D);

        assertEquals(90.0D, rotation);
    }

    @Test
    public void decorationRotationUsesVanillaSunAngle() {
        double rotation = Utils.calculateRotation(1.0D, false, 6000L, 282.0D);

        assertEquals(282.0D, rotation);
    }

    @Test
    public void zeroSpeedDisablesBothRotationModes() {
        assertEquals(0.0D, Utils.calculateRotation(0.0D, true, 6000L, 282.0D));
        assertEquals(0.0D, Utils.calculateRotation(0.0D, false, 6000L, 282.0D));
    }
}
