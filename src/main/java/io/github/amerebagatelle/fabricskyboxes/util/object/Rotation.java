package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Rotation {
    public static final Rotation DEFAULT = new Rotation(true, new Vector3f(0F, 0F, 0F), new Vector3f(0F, 0F, 0F), new Vector3i(0, 0, 0), 0, 0, 0);
    public static final Rotation DECORATIONS = new Rotation(false, new Vector3f(0F, 0F, 0F), new Vector3f(0F, 0F, 0F), new Vector3i(0, 0, 0), 0, 0, 1);
    private static final Codec<Vector3f> VEC_3_F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        if (list.size() < 3) {
            return DataResult.error(() -> "Incomplete number of elements in vector");
        }
        return DataResult.success(new Vector3f(list.get(0), list.get(1), list.get(2)));
    }, (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z()));
    private static final Codec<Vector3i> VEC_3_I = Codec.INT.listOf().comapFlatMap((list) -> {
        if (list.size() < 3) {
            return DataResult.error(() -> "Incomplete number of elements in vector");
        }
        return DataResult.success(new Vector3i(list.get(0), list.get(1), list.get(2)));
    }, (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z()));
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("skyboxRotation", true).forGetter(Rotation::getSkyboxRotation),
            VEC_3_F.optionalFieldOf("static", new Vector3f(0F, 0F, 0F)).forGetter(Rotation::getStatic),
            VEC_3_F.optionalFieldOf("axis", new Vector3f(0F, 0F, 0F)).forGetter(Rotation::getAxis),
            VEC_3_I.optionalFieldOf("timeShift", new Vector3i(0, 0, 0)).forGetter(Rotation::getTimeShift),
            Codec.FLOAT.optionalFieldOf("rotationSpeedX", 0F).forGetter(Rotation::getRotationSpeedX),
            Codec.FLOAT.optionalFieldOf("rotationSpeedY", 0F).forGetter(Rotation::getRotationSpeedY),
            Codec.FLOAT.optionalFieldOf("rotationSpeedZ", 0F).forGetter(Rotation::getRotationSpeedZ)
    ).apply(instance, Rotation::new));
    private final boolean skyboxRotation;
    private final Vector3f staticRot;
    private final Vector3f axisRot;
    private final Vector3i timeShift;
    private final float rotationSpeedX;
    private final float rotationSpeedY;
    private final float rotationSpeedZ;

    public Rotation(boolean skyboxRotation, Vector3f staticRot, Vector3f axisRot, Vector3i timeShift, float rotationSpeedX, float rotationSpeedY, float rotationSpeedZ) {
        this.skyboxRotation = skyboxRotation;
        this.staticRot = staticRot;
        this.axisRot = axisRot;
        this.timeShift = timeShift;
        this.rotationSpeedX = rotationSpeedX;
        this.rotationSpeedY = rotationSpeedY;
        this.rotationSpeedZ = rotationSpeedZ;
    }

    public boolean getSkyboxRotation() {
        return skyboxRotation;
    }

    public Vector3f getStatic() {
        return this.staticRot;
    }

    public Vector3f getAxis() {
        return this.axisRot;
    }

    public Vector3i getTimeShift() {
        return timeShift;
    }

    public float getRotationSpeedX() {
        return rotationSpeedX;
    }

    public float getRotationSpeedY() {
        return rotationSpeedY;
    }

    public float getRotationSpeedZ() {
        return rotationSpeedZ;
    }
}
