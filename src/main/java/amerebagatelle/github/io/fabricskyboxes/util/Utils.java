package amerebagatelle.github.io.fabricskyboxes.util;

public class Utils {
    public static float calculateBrightness(int phase, float delta, float duration) {
        switch (phase) {
            case 1:
                return 1f-(delta/duration);

            case 2:
                return delta/duration;

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
        if(end < start) start += 24000;
        return end-start;
    }

    public static float blend(int a, int b, float ratio) {
        if (ratio > 1f) {
            ratio = 1f;
        } else if (ratio < 0f) {
            ratio = 0f;
        }
        float iRatio = 1.0f - ratio;

        int aA = (a >> 24 & 0xff);
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24 & 0xff);
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int A = (int)((aA * iRatio) + (int)(bA * ratio));
        int R = (int)((aR * iRatio) + (int)(bR * ratio));
        int G = (int)((aG * iRatio) + (int)(bG * ratio));
        int B = (int)((aB * iRatio) + (int)(bB * ratio));

        return A << 24 | R << 16 | G << 8 | B;
    }
}
