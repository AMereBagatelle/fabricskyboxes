package io.github.amerebagatelle.fabricskyboxes.api.skyboxes;

import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.Properties;

public interface FSBSkybox extends Skybox {
    float getAlpha();

    float updateAlpha();

    Properties getProperties();

    Conditions getConditions();

    Decorations getDecorations();
}
