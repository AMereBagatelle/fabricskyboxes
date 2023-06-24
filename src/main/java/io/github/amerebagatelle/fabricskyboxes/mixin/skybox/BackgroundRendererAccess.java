package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BackgroundRenderer.class)
public interface BackgroundRendererAccess {
    @Accessor("red")
    static float getRed() {
        throw new AssertionError();
    }

    @Accessor("green")
    static float getGreen() {
        throw new AssertionError();
    }

    @Accessor("blue")
    static float getBlue() {
        throw new AssertionError();
    }
}
