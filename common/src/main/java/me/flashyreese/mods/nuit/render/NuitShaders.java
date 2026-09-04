package me.flashyreese.mods.nuit.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import me.flashyreese.mods.nuit.NuitClient;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public final class NuitShaders {
    public static final ResourceLocation FRAME_BLENDED_SKYBOX_ID =
            ResourceLocation.fromNamespaceAndPath(NuitClient.MOD_ID, "frame_blended_skybox");
    public static final VertexFormat FRAME_BLENDED_SKYBOX_FORMAT = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV0", VertexFormatElement.UV0)
            .add("UV1", VertexFormatElement.UV1)
            .add("Color", VertexFormatElement.COLOR)
            .build();

    private static volatile ShaderInstance frameBlendedSkybox;

    private NuitShaders() {
    }

    public static ShaderInstance getFrameBlendedSkybox() {
        return frameBlendedSkybox;
    }

    public static void setFrameBlendedSkybox(ShaderInstance shader) {
        frameBlendedSkybox = shader;
    }
}
