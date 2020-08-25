package amerebagatelle.github.io.fabricskyboxes.util;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class Utils {
    public static int getTicksBetween(int start, int end) {
        if (end < start) start += 24000;
        return end - start;
    }

    public static Identifier getJsonStringAsId(String name, JsonObject json) {
        return new Identifier(json.get(name).getAsString());
    }
}
