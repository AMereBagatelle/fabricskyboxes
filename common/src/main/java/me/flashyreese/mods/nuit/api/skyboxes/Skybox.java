package me.flashyreese.mods.nuit.api.skyboxes;

import com.mojang.blaze3d.vertex.PoseStack;
import me.flashyreese.mods.nuit.mixin.SkyRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public interface Skybox {
    default int getLayer() {
        return 0;
    }

    void render(SkyRendererAccessor skyRendererAccessor, PoseStack poseStack, Matrix4f projectionMatrix,
                float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback);

    void tick(ClientLevel clientLevel);

    boolean isActive();
}
