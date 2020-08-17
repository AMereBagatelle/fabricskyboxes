package amerebagatelle.github.io.fabricskyboxes.mixin;

import amerebagatelle.github.io.fabricskyboxes.util.SkyboxManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Nullable
    private VertexBuffer lightSkyBuffer;

    @Shadow @Nullable private VertexBuffer darkSkyBuffer;

    @Shadow @Nullable private VertexBuffer starsBuffer;

    @Shadow @Final private VertexFormat skyVertexFormat;

    @Shadow @Final private TextureManager textureManager;

    @Shadow @Final private MinecraftClient client;

    /**
     * @author AMereBagatelle
     * @reason Because custom skies are hard, okay?
     */
    @Overwrite()
    public void renderSky(MatrixStack matrices, float tickDelta) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        skyboxManager.renderSkyboxes(matrices, tickDelta, lightSkyBuffer, darkSkyBuffer, starsBuffer, skyVertexFormat, textureManager);
    }
}
