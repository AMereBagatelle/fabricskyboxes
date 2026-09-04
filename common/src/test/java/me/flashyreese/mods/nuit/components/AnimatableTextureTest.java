package me.flashyreese.mods.nuit.components;

import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnimatableTextureTest {
    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {
                        "texture": "test:resourcelocation"
                    }
                    """,
            """
                    {
                        "texture": "test:resourcelocation",
                        "gridColumns": 1
                    }
                    """,
            """
                    {
                        "texture": "test:resourcelocation",
                        "gridRows": 1
                    }
                    """,
            """
                    {
                        "texture": "test:resourcelocation",
                        "duration": 1
                    }
                    """,
            """
                    {
                        "texture": "test:resourcelocation",
                        "interpolate": true
                    }
                    """,
            """
                    {
                        "texture": "test:resourcelocation",
                        "frameDuration": {
                            "1": 10,
                            "2": 20
                        }
                    }
                    """
    })
    public void testCorrectParse(String json) {
        var jsonOb = JsonTestHelper.readJson(json);
        assertDoesNotThrow(() -> AnimatableTexture.CODEC.decode(JsonOps.INSTANCE, jsonOb).getOrThrow().getFirst());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {
                    }
                    """,
    })
    public void testIncorrectParse(String json) {
        var jsonOb = JsonTestHelper.readJson(json);
        assertThrows(IllegalStateException.class, () -> AnimatableTexture.CODEC.decode(JsonOps.INSTANCE, jsonOb).getOrThrow().getFirst());
    }

    @Test
    public void updatesFramesFromGameTimeAndInterpolates() {
        AnimatableTexture animation = animation(true, 100L, Map.of());

        animation.update(0L, 0.0F);
        assertEquals(new UVRange(0.0F, 0.0F, 0.5F, 1.0F), animation.getCurrentFrame());
        assertEquals(new UVRange(0.5F, 0.0F, 1.0F, 1.0F), animation.getNextFrame());

        animation.update(1L, 0.0F);
        assertEquals(0.5F, animation.getFrameBlend(), 0.0001F);

        animation.update(2L, 0.0F);
        assertEquals(new UVRange(0.5F, 0.0F, 1.0F, 1.0F), animation.getCurrentFrame());
        assertEquals(0.0F, animation.getFrameBlend(), 0.0001F);
    }

    @Test
    public void respectsPerFrameDurationsAndInterpolationFlag() {
        AnimatableTexture animation = animation(false, 100L, Map.of(1, 50L, 2, 150L));
        animation.update(1L, 0.0F);

        assertEquals(new UVRange(0.5F, 0.0F, 1.0F, 1.0F), animation.getCurrentFrame());
        assertEquals(0.0F, animation.getFrameBlend(), 0.0001F);
    }

    private static AnimatableTexture animation(boolean interpolate, long duration, Map<Integer, Long> frameDurations) {
        return new AnimatableTexture(
                new Texture(ResourceLocation.fromNamespaceAndPath("test", "animation.png")),
                UVRange.of(),
                2,
                1,
                duration,
                interpolate,
                frameDurations
        );
    }
}
