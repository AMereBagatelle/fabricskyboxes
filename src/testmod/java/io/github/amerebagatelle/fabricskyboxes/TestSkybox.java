package io.github.amerebagatelle.fabricskyboxes;

import com.mojang.serialization.Codec;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;

public class TestSkybox extends MonoColorSkybox {
    public static final TestSkybox INSTANCE = new TestSkybox(TestClientModInitializer.PROPS, TestClientModInitializer.CONDITIONS, TestClientModInitializer.DECORATIONS, new RGBA(1, 0, 1, 1), Blend.DEFAULT);
    public static final Codec<TestSkybox> CODEC = Codec.unit(() -> INSTANCE);

    public TestSkybox(Properties properties, Conditions conditions, Decorations decorations, RGBA color, Blend blend) {
        super(properties, conditions, decorations, color, blend);
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return TestClientModInitializer.TYPE;
    }
}
