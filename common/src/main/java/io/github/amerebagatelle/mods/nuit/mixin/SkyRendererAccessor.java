package io.github.amerebagatelle.mods.nuit.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Compatibility view of the sky buffers owned directly by {@link LevelRenderer} in Minecraft 1.21.1.
 */
@Mixin(LevelRenderer.class)
public interface SkyRendererAccessor {
    @Accessor("SUN_LOCATION")
    static ResourceLocation getSun() {
        return ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
    }

    @Accessor("MOON_LOCATION")
    static ResourceLocation getMoonPhases() {
        return ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    }

    @Accessor("END_SKY_LOCATION")
    static ResourceLocation getEndSky() {
        return ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
    }

    @Accessor("skyBuffer")
    VertexBuffer getTopSkyBuffer();

    @Accessor("starBuffer")
    VertexBuffer getStarsBuffer();

    @Accessor("darkBuffer")
    VertexBuffer getBottomSkyBuffer();
}
