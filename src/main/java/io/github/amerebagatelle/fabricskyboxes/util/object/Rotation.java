package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3f;

public class Rotation {
    public static final Rotation DEFAULT = new Rotation(true, new Vec3f(0F, 0F, 0F), new Vec3f(0F, 0F, 0F), 0, 0, 0);
    public static final Rotation DECORATIONS = new Rotation(false, new Vec3f(0F, 0F, 0F), new Vec3f(0F, 0F, 0F), 0, 0, 1);
    private static final Codec<Vec3f> VEC_3_F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        if (list.size() < 3) {
            return DataResult.error("Incomplete number of elements in vector");
        }
        return DataResult.success(new Vec3f(list.get(0), list.get(1), list.get(2)));
    }, (vec) -> ImmutableList.of(vec.getX(), vec.getY(), vec.getZ()));
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("skyboxRotation", true).forGetter(Rotation::getSkyboxRotation),
            VEC_3_F.optionalFieldOf("static", new Vec3f(0F, 0F, 0F)).forGetter(Rotation::getStatic),
            VEC_3_F.optionalFieldOf("axis", new Vec3f(0F, 0F, 0F)).forGetter(Rotation::getAxis),
            Codec.FLOAT.optionalFieldOf("rotationSpeedX", 0F).forGetter(Rotation::getRotationSpeedX),
            Codec.FLOAT.optionalFieldOf("rotationSpeedY", 0F).forGetter(Rotation::getRotationSpeedY),
            Codec.FLOAT.optionalFieldOf("rotationSpeedZ", 0F).forGetter(Rotation::getRotationSpeedZ)
    ).apply(instance, Rotation::new));
    private final boolean skyboxRotation;
    private final Vec3f staticRot;
    private final Vec3f axisRot;
    private final float rotationSpeedX;
    private final float rotationSpeedY;
    private final float rotationSpeedZ;

    public Rotation(boolean skyboxRotation, Vec3f staticRot, Vec3f axisRot, float rotationSpeedX, float rotationSpeedY, float rotationSpeedZ) {
        this.skyboxRotation = skyboxRotation;
        this.staticRot = staticRot;
        this.axisRot = axisRot;
        this.rotationSpeedX = rotationSpeedX;
        this.rotationSpeedY = rotationSpeedY;
        this.rotationSpeedZ = rotationSpeedZ;
    }

    public boolean getSkyboxRotation() {
        return skyboxRotation;
    }

    public Vec3f getStatic() {
        return this.staticRot;
    }

    public Vec3f getAxis() {
        return this.axisRot;
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
