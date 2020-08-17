package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.util.Utils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSkybox {
    public final MinecraftClient client = MinecraftClient.getInstance();

    // Textures
    public static final Identifier SUN = new Identifier("textures/environment/sun.png");
    public static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");

    // Configuration
    public int startFadeIn;
    public int endFadeIn;
    public int endFadeOut;
    public String blend;
    public boolean rotate;
    public float speed;
    public Vec3d axis;
    public String weather;
    public ArrayList<Identifier> biomes;
    public int[] heights;
    public int transition;

    public float brightness = 0f;

    public abstract void render(MatrixStack matrixStack, float delta, VertexBuffer lightSkyBuffer, VertexBuffer darkSkyBuffer, VertexBuffer starsBuffer, VertexFormat skyVertexFormat, TextureManager textureManager, float alpha);

    public void renderSun(TextureManager textureManager, BufferBuilder bufferBuilder, Matrix4f matrix4f, float s) {
        textureManager.bindTexture(SUN);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -s, 100.0F, -s).texture(0.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f, s, 100.0F, -s).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f, s, 100.0F, s).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(matrix4f, -s, 100.0F, s).texture(0.0F, 1.0F).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public void renderMoon(TextureManager textureManager, BufferBuilder bufferBuilder, Matrix4f matrix4f, float s, float p, float q, float w, float o) {
        textureManager.bindTexture(MOON_PHASES);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -s, -100.0F, s).texture(p, q).next();
        bufferBuilder.vertex(matrix4f, s, -100.0F, s).texture(w, q).next();
        bufferBuilder.vertex(matrix4f, s, -100.0F, -s).texture(w, o).next();
        bufferBuilder.vertex(matrix4f, -s, -100.0F, -s).texture(p, o).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public void renderStars(ClientWorld world, float delta, float r, VertexBuffer starsBuffer, VertexFormat skyVertexFormat, MatrixStack matrixStack) {
        float aa = world.method_23787(delta) * r;
        if (aa > 0.0F) {
            RenderSystem.color4f(aa, aa, aa, aa);
            starsBuffer.bind();
            skyVertexFormat.startDrawing(0L);
            starsBuffer.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            skyVertexFormat.endDrawing();
        }
    }

    public void updateBrightness(MinecraftClient client) {
        if(checkBiomes() && checkHeights() && checkWeather()) {
            int time = (int) client.world.getTimeOfDay();
            int duration = Utils.getDuration(startFadeIn, endFadeIn);
            int phase = Utils.getPhase(startFadeIn, endFadeOut, time, duration);
            int delta = 0;
            switch (phase) {
                case 1:
                    delta = startFadeIn + duration - time;
                    break;

                case 2:
                    delta = endFadeOut - time;
            }
            brightness = Utils.calculateBrightness(phase, delta, duration);
        } else {
            brightness = 0;
        }
    }

    private boolean checkBiomes() {
        return biomes.size() == 0 || biomes.contains(Registry.BIOME.getId(client.world.getBiome(client.player.getBlockPos())));
    }

    private boolean checkHeights() {
        assert client.player != null;
        double y = client.player.getY();
        return heights.length % 2 == 1 || (heights[0] < y && heights[1] > y);
    }

    private boolean checkWeather() {
        assert client.world != null;
        if(client.world.isThundering()) {
            return weather.contains("thunder");
        } else if(client.world.isRaining()) {
            return weather.contains("rain");
        } else {
            return weather.contains("clear");
        }
    }

    public float getBrightness() {
        return brightness;
    }
}
