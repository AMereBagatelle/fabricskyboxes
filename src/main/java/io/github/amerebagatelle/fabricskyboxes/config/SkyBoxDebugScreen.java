package io.github.amerebagatelle.fabricskyboxes.config;

import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SkyBoxDebugScreen extends Screen implements HudRenderCallback {
    public SkyBoxDebugScreen(Text title) {
        super(title);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.onHudRender(context, delta);
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (FabricSkyBoxesClient.config().generalSettings.debugHud || MinecraftClient.getInstance().currentScreen == this) {
            int yPadding = 2;
            for (Map.Entry<Identifier, Skybox> identifierSkyboxEntry : SkyboxManager.getInstance().getSkyboxMap().entrySet()) {
                Skybox activeSkybox = identifierSkyboxEntry.getValue();
                if (activeSkybox instanceof FSBSkybox fsbSkybox && fsbSkybox.isActive()) {
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, identifierSkyboxEntry.getKey() + " " + activeSkybox.getPriority() + " " + fsbSkybox.getAlpha(), 2, yPadding, 0xffffffff, false);
                    yPadding += 14;
                }
            }
        }
    }
}
