package me.flashyreese.mods.nuit.components;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionsTest {
    @Test
    public void decodesModernSkyboxCondition() {
        Conditions conditions = decode("""
                {"skyboxes":{"entries":["minecraft:none"]}}
                """);
        assertEquals(
                ResourceLocation.fromNamespaceAndPath("minecraft", "none"),
                conditions.getSkyboxes().entries().getFirst()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "minecraft:overworld, minecraft:overworld",
            "minecraft:the_nether, minecraft:none",
            "minecraft:nether, minecraft:none",
            "minecraft:the_end, minecraft:end",
            "example:custom, example:custom"
    })
    public void normalizesLegacyWorldCondition(String input, String expected) {
        Conditions conditions = decode("{\"worlds\":{\"entries\":[\"" + input + "\"]}}");
        assertEquals(
                ResourceLocation.parse(expected),
                conditions.getSkyboxes().entries().getFirst()
        );
    }

    @Test
    public void modernFieldWinsWhenBothArePresent() {
        Conditions conditions = decode("""
                {
                  "skyboxes":{"entries":["minecraft:end"]},
                  "worlds":{"entries":["minecraft:the_end"]}
                }
                """);
        assertEquals(
                ResourceLocation.fromNamespaceAndPath("minecraft", "end"),
                conditions.getSkyboxes().entries().getFirst()
        );
    }

    private static Conditions decode(String json) {
        return Conditions.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json)).getOrThrow();
    }
}
