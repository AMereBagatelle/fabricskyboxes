package io.github.amerebagatelle.fabricskyboxes.tests;

import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    public void init() {
        int currentTime = 10000;
        int startFadeIn = 12000;
        int endFadeIn = 14000;
        int startFadeOut = 16000;
        int endFadeOut = 18000;
        float result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 0f;

        currentTime = 13000;
        result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 0.5f;

        currentTime = 14000;
        result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 1f;

        currentTime = 15000;
        result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 1f;

        currentTime = 17000;
        result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 0.5f;

        currentTime = 18000;
        result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 0f;

        startFadeIn = 18000;
        endFadeIn = 20000;
        startFadeOut = 4000;
        endFadeOut = 6000;
        currentTime = 16000;
        result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 0f;

        startFadeOut = 11500;
        endFadeOut = 14000;
        startFadeIn = 21667;
        endFadeIn = 167;
        currentTime = 23999;
        result = Utils.calculateFadeAlphaValue(1f, 0f, currentTime, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        assert result == 0.9328f;
    }
}
