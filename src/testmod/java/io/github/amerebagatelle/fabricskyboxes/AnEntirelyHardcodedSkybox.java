package io.github.amerebagatelle.fabricskyboxes;

import com.mojang.serialization.Codec;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.DefaultProperties;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;

public class AnEntirelyHardcodedSkybox extends MonoColorSkybox {
    public static final AnEntirelyHardcodedSkybox INSTANCE = new AnEntirelyHardcodedSkybox(Test.PROPS, Test.CONDITIONS, Test.DECORATIONS, new RGBA(1, 0, 1, 1));
    public static final Codec<AnEntirelyHardcodedSkybox> CODEC = Codec.unit(() -> INSTANCE);

    public AnEntirelyHardcodedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, RGBA color) {
        super(properties, conditions, decorations, color);
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return Init.TYPE;
    }
}
