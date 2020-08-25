package amerebagatelle.github.io.fabricskyboxes.util;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class Utils {
    public static float calculateBrightness(int phase, float delta, float duration) {
        switch (phase) {
            case 1:
                return 1f - (delta / duration);

            case 2:
                return delta / duration;

            case 3:
                return 1f;

            default:
                return 0f;
        }
    }

    public static int getPhase(int startFadeIn, int endFadeOut, int currentTime, int duration) {
        if(startFadeIn < currentTime && startFadeIn+duration > currentTime) {
            return 1;
        } else if(startFadeIn+duration < currentTime && endFadeOut-duration > currentTime) {
            return 3;
        } else if(endFadeOut-duration < currentTime && endFadeOut > currentTime) {
            return 2;
        } else {
            return 0;
        }
    }

    public static int getDuration(int start, int end) {
        if (end < start) start += 24000;
        return end - start;
    }

    public static Identifier getJsonStringAsId(String name, JsonObject json) {
        return new Identifier(json.get(name).getAsString());
    }
}
