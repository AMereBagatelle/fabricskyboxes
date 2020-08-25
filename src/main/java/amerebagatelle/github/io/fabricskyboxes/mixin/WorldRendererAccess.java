package amerebagatelle.github.io.fabricskyboxes.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccess {
    @Accessor
    TextureManager getTextureManager();

}
