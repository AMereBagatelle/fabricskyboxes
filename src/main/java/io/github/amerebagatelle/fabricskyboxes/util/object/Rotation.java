package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;

public class Rotation {
    private static final Codec<Vector3f> VEC_3_F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        if (list.size() < 3) {
            return DataResult.error("Incomplete number of elements in vector");
        }
        return DataResult.success(new Vector3f(list.get(0), list.get(1), list.get(2)));
    }, (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z()));
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VEC_3_F.optionalFieldOf("static", new Vector3f(0F, 0F, 0F)).forGetter(Rotation::getStatic),
            VEC_3_F.optionalFieldOf("axis", new Vector3f(0F, 0F, 0F)).forGetter(Rotation::getAxis),
            Codec.FLOAT.optionalFieldOf("rotationSpeed", 1F).forGetter(Rotation::getRotationSpeed)
    ).apply(instance, Rotation::new));
    public static final Rotation DEFAULT = new Rotation(new Vector3f(0F, 0F, 0F), new Vector3f(0F, 0F, 0F), 1);

    private final Vector3f staticRot;
    private final Vector3f axisRot;
    private final float rotationSpeed;

    public Rotation(Vector3f staticRot, Vector3f axisRot, float rotationSpeed) {
        this.staticRot = staticRot;
        this.axisRot = axisRot;
        this.rotationSpeed = rotationSpeed;
    }

    public Vector3f getStatic() {
        return this.staticRot;
    }

    public Vector3f getAxis() {
        return this.axisRot;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }
}
