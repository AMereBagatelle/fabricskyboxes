package me.flashyreese.mods.nuit.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NuitShaderResourcesTest {
    private static final String SHADER_DEFINITION =
            "assets/nuit/shaders/core/frame_blended_skybox.json";

    @Test
    public void frameBlendedShaderReferencesPackagedStages() throws IOException {
        ClassLoader classLoader = NuitShaderResourcesTest.class.getClassLoader();

        try (InputStream stream = classLoader.getResourceAsStream(SHADER_DEFINITION)) {
            assertNotNull(stream, "Missing frame-blended shader definition");
            JsonObject definition = JsonParser.parseReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            assertStageExists(classLoader, definition.get("vertex").getAsString(), ".vsh");
            assertStageExists(classLoader, definition.get("fragment").getAsString(), ".fsh");
        }
    }

    private static void assertStageExists(ClassLoader classLoader, String identifier, String extension) {
        String[] components = identifier.split(":", 2);
        String namespace = components.length == 2 ? components[0] : "minecraft";
        String path = components.length == 2 ? components[1] : components[0];
        String resource = "assets/" + namespace + "/shaders/core/" + path + extension;

        assertNotNull(classLoader.getResource(resource), () -> "Missing shader stage " + resource);
    }
}
