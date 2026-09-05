package me.flashyreese.mods.nuit.skybox;

import com.google.gson.JsonParser;
import me.flashyreese.mods.nuit.SkyboxManager;
import me.flashyreese.mods.nuit.api.NuitApi;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkyboxTypeTest {
    @Test
    public void registersAndFindsAddonSkyboxTypes() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("test", "registered_skybox");
        SkyboxType<MonoColorSkybox> type = NuitApi.registerSkyboxType(
                id,
                1,
                MonoColorSkybox.CODEC
        );

        assertSame(type, SkyboxType.get(id).orElseThrow());
        assertTrue(NuitApi.getRegisteredSkyboxTypes().contains(type));

        var json = JsonParser.parseString("""
                {
                    "schemaVersion": 1,
                    "type": "test:registered_skybox"
                }
                """).getAsJsonObject();
        assertTrue(SkyboxManager.parseSkyboxJson(
                ResourceLocation.fromNamespaceAndPath("test", "instance"),
                json
        ).orElseThrow() instanceof MonoColorSkybox);
    }
}
