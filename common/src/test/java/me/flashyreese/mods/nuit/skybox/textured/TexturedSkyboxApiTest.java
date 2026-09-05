package me.flashyreese.mods.nuit.skybox.textured;

import com.mojang.blaze3d.vertex.PoseStack;
import me.flashyreese.mods.nuit.components.Blend;
import me.flashyreese.mods.nuit.components.Conditions;
import me.flashyreese.mods.nuit.components.Properties;
import me.flashyreese.mods.nuit.mixin.SkyRendererAccessor;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TexturedSkyboxApiTest {
    @Test
    void legacySubclassExtensionDescriptorsRemainCompatible() throws ReflectiveOperationException {
        Constructor<TexturedSkybox> constructor = TexturedSkybox.class.getDeclaredConstructor(
                Properties.class,
                Conditions.class,
                Blend.class
        );
        assertTrue(Modifier.isProtected(constructor.getModifiers()));

        Method render = TexturedSkybox.class.getDeclaredMethod(
                "render",
                SkyRendererAccessor.class,
                PoseStack.class,
                Matrix4f.class,
                float.class,
                Camera.class,
                boolean.class,
                Runnable.class
        );
        assertTrue(Modifier.isPublic(render.getModifiers()));
        assertTrue(Modifier.isFinal(render.getModifiers()));

        Method renderSkybox = TexturedSkybox.class.getDeclaredMethod(
                "renderSkybox",
                SkyRendererAccessor.class,
                PoseStack.class,
                Matrix4f.class,
                float.class,
                Camera.class,
                boolean.class,
                Runnable.class
        );
        assertTrue(Modifier.isPublic(renderSkybox.getModifiers()));
        assertTrue(Modifier.isAbstract(renderSkybox.getModifiers()));
    }
}
