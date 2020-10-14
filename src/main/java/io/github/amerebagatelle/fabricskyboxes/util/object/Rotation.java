package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.util.math.Vector3f;

import java.util.List;

public class Rotation {
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.listOf().optionalFieldOf("static", ImmutableList.of(0F, 0F, 0F)).forGetter(Rotation::getStatic),
            Codec.FLOAT.listOf().optionalFieldOf("axis", ImmutableList.of(0F, 0F, 0F)).forGetter(Rotation::getAxis)
    ).apply(instance, Rotation::new));
    public static final Rotation DEFAULT = new Rotation(ImmutableList.of(0F, 0F, 0F), ImmutableList.of(0F, 0F, 0F));

    private final List<Float> staticRot;
    private final List<Float> axisRot;

    public Rotation(List<Float> staticRot, List<Float> axisRot) {
        this.staticRot = Lists.newArrayList(staticRot.get(0), staticRot.get(1), staticRot.get(2));
        this.axisRot = Lists.newArrayList(axisRot.get(0), axisRot.get(1), axisRot.get(2));
    }

    public List<Float> getStatic() {
        return this.staticRot;
    }

    public List<Float> getAxis() {
        return this.axisRot;
    }

    public Vector3f getRotationAxisVector() {
        return new Vector3f(this.axisRot.get(0) / 90, this.axisRot.get(1) / 90, this.axisRot.get(2) / 90);
    }
}
