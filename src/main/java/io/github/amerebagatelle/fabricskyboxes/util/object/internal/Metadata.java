package io.github.amerebagatelle.fabricskyboxes.util.object.internal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;

import net.minecraft.util.Identifier;

public class Metadata {
    public static final Codec<Metadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("schemaVersion").forGetter(Metadata::getSchemaVersion),
            SkyboxType.SKYBOX_ID_CODEC.fieldOf("id").forGetter(Metadata::getId)
    ).apply(instance, Metadata::new));

    private final int schemaVersion;
    private final Identifier id;

    public Metadata(int schemaVersion, Identifier id) {
        this.schemaVersion = schemaVersion;
        this.id = id;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public Identifier getId() {
        return this.id;
    }
}
