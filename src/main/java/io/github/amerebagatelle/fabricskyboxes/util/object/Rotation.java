package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.util.math.Vector3f;

import java.util.List;

public class Rotation {
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.listOf().optionalFieldOf("rotationStatic", ImmutableList.of(0f, 0f, 0f)).forGetter(Rotation::getRotationStatic),
            Codec.FLOAT.listOf().optionalFieldOf("rotationAxis", ImmutableList.of(0f, 0f, 0f)).forGetter(Rotation::getRotationAxis)
    ).apply(instance, Rotation::new));
    public static final Rotation DEFAULT = new Rotation(ImmutableList.of(0f, 0f, 0f), ImmutableList.of(0f, 0f, 0f));

    private final List<Float> rotationStatic;
    private final List<Float> rotationAxis;

    public Rotation(List<Float> rotationStatic, List<Float> rotationAxis) {
        this.rotationStatic = rotationStatic;
        this.rotationAxis = rotationAxis;
    }

    public List<Float> getRotationStatic() {
        return rotationStatic;
    }

    public List<Float> getRotationAxis() {
        return rotationAxis;
    }

    public Vector3f getRotationAxisVector() {
        return new Vector3f(rotationAxis.get(0) / 90, rotationAxis.get(1) / 90, rotationAxis.get(2) / 90);
    }
}
