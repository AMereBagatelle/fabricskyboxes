package amerebagatelle.github.io.fabricskyboxes.util;

public class Utils {
    /**
     * Gets the amount of ticks in between start and end, on a 24000 tick system.
     *
     * @param start The start of the time you wish to measure
     * @param end   The end of the time you wish to measure
     * @return The amount of ticks in between start and end
     */
    public static int getTicksBetween(int start, int end) {
        if (end < start) start += 24000;
        return end - start;
    }
}
