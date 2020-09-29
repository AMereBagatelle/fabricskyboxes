package io.github.amerebagatelle.fabricskyboxes.util;

public class Utils {
    public static int getTicksBetween(int start, int end) {
        if (end < start) start += 24000;
        return end - start;
    }
}
