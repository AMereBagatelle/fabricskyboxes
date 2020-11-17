package io.github.amerebagatelle.fabricskyboxes.util.object.internal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import net.minecraft.util.Identifier;

public class Metadata {
    public static final Codec<Metadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("schemaVersion").forGetter(Metadata::getSchemaVersion),
            SkyboxType.SKYBOX_ID_CODEC.fieldOf("type").forGetter(Metadata::getType)
    ).apply(instance, Metadata::new));

    private final int schemaVersion;
    private final Identifier type;

    public Metadata(int schemaVersion, Identifier type) {
        this.schemaVersion = schemaVersion;
        this.type = type;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public Identifier getType() {
        return this.type;
    }
}
