package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyAngle(F)F"))
    private static float fsb$redirectSkyAngle(ClientWorld instance, float v) {
        if (SkyboxManager.getInstance().isEnabled() && !SkyboxManager.getInstance().getActiveSkyboxes().isEmpty()) {
            Objects.requireNonNull(MinecraftClient.getInstance().world);
            float skyAngleModified = MathHelper.floorMod(MinecraftClient.getInstance().world.getTimeOfDay() / 24000F + 0.75F, 1);
            return skyAngleModified;
        }
        return instance.getSkyAngle(v);
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyAngleRadians(F)F"))
    private static float fsb$redirectSkyAngleRadian(ClientWorld instance, float v) {
        if (SkyboxManager.getInstance().isEnabled() && !SkyboxManager.getInstance().getActiveSkyboxes().isEmpty()) {
            Objects.requireNonNull(MinecraftClient.getInstance().world);
            float skyAngle = MathHelper.floorMod(MinecraftClient.getInstance().world.getTimeOfDay() / 24000F + 0.75F, 1);
            float skyAngleRadian = skyAngle * (float) (Math.PI * 2);
            return skyAngleRadian;
        }
        return instance.getSkyAngleRadians(v);
    }
}
