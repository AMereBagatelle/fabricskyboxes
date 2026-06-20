package me.flashyreese.mods.nuit.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.nuit.SkyboxManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class NuitSkyboxRenderHooks {
    private NuitSkyboxRenderHooks() {
    }

    public static boolean renderCustomSkyboxes(
            GpuBufferSlice fogParameters,
            SkyRenderer skyRenderer,
            float tickDelta
    ) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (!skyboxManager.isEnabled() || !skyboxManager.hasActiveRenderableSkyboxes()) {
            return false;
        }

        Matrix4f skyModelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
        skyModelViewMatrix.setTranslation(0.0F, 0.0F, 0.0F);
        Matrix4fStack skyModelViewStack = new Matrix4fStack(32);
        skyModelViewStack.set(skyModelViewMatrix);
        skyboxManager.renderSkyboxes(
                skyRenderer,
                skyModelViewStack,
                tickDelta,
                Minecraft.getInstance().gameRenderer.getMainCamera(),
                fogParameters
        );
        return true;
    }
}
