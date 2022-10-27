package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccess {
    @Accessor
    VertexBuffer getLightSkyBuffer();

    @Accessor
    VertexBuffer getStarsBuffer();

    @Accessor
    VertexBuffer getDarkSkyBuffer();

    @Deprecated
    @Accessor("SUN")
    static Identifier getSun() {
        throw new AssertionError();
    }

    @Deprecated
    @Accessor("MOON_PHASES")
    static Identifier getMoonPhases(){
        throw new AssertionError();
    }
}
