package amerebagatelle.github.io.fabricskyboxes.util;

import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.MonoColorSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.OverworldSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.SquareTextureSkybox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Array;
import java.util.*;

public class SkyboxManager {
    private static final SkyboxManager INSTANCE = new SkyboxManager();

    private static final AbstractSkybox OVERWORLD_SKYBOX = new OverworldSkybox();
    private static final AbstractSkybox END_SKYBOX = new SquareTextureSkybox(new Identifier("textures/environment/end_sky.png"));

    private static final ArrayList<AbstractSkybox> skyboxes = new ArrayList<>();

    public void renderSkyboxes(MatrixStack matrices, float tickDelta, VertexBuffer lightSkyBuffer, VertexBuffer darkSkyBuffer, VertexBuffer starsBuffer, VertexFormat skyVertexFormat, TextureManager textureManager) {
        MinecraftClient client = MinecraftClient.getInstance();
        float totalBrightness = 0;
        LinkedList<AbstractSkybox> renderingSkyboxes = new LinkedList<>();
        for(AbstractSkybox skybox : skyboxes) {
            skybox.updateBrightness(client);
            if(skybox.getBrightness() > 0) {
                totalBrightness += skybox.getBrightness();
                renderingSkyboxes.add(skybox);
            }
        }
        if(totalBrightness <= 0.75f) {
            if(client.world.getSkyProperties().getSkyType() == SkyProperties.SkyType.END) {
                renderingSkyboxes.addFirst(END_SKYBOX);
            } else if(client.world.getSkyProperties().getSkyType() == SkyProperties.SkyType.NORMAL) {
                OVERWORLD_SKYBOX.brightness = 1f - totalBrightness;
                renderingSkyboxes.addFirst(OVERWORLD_SKYBOX);
            }
        }
        for (AbstractSkybox renderingSkybox : renderingSkyboxes) {
            renderingSkybox.render(matrices, tickDelta, lightSkyBuffer, darkSkyBuffer, starsBuffer, skyVertexFormat, textureManager, renderingSkybox.getBrightness());
        }
    }

    public void addSkybox(AbstractSkybox skybox) {
        skyboxes.add(skybox);
    }

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }
}
