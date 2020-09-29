package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.SkyboxManager;
import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.Fade;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.RGBA;
import amerebagatelle.github.io.fabricskyboxes.util.Utils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;

public abstract class AbstractSkybox {
    public float alpha;
    private Fade fade;
    public float maxAlpha = 1f;
    public float transitionSpeed = 1;
    public boolean changeFog = false;
    private RGBA fogColors;
    public boolean shouldRotate = false;
    public boolean decorations = false;
    public ArrayList<String> weather = new ArrayList<>();
    public ArrayList<Identifier> biomes = new ArrayList<>();
    public ArrayList<Identifier> dimensions = new ArrayList<>();
    public ArrayList<Float[]> heightRanges = new ArrayList<>();

    public int startFadeIn = 0;
    public int endFadeIn = 0;
    public int startFadeOut = 0;
    public int endFadeOut = 0;
    public float fogRed = 0;
    public float fogGreen = 0;
    public float fogBlue = 0;

    public abstract void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    public float getAlpha() {
        // this probably can take a good bit of performance improvement, idk tho
        assert MinecraftClient.getInstance().world != null;
        int currentTime = (int) MinecraftClient.getInstance().world.getTimeOfDay();
        int duration = Utils.getTicksBetween(this.startFadeIn, this.endFadeIn);
        int phase = 0; // default not showing
        if (this.startFadeIn < currentTime && this.endFadeIn >= currentTime) {
            phase = 1; // fading out
        } else if (this.endFadeIn < currentTime && this.startFadeOut >= currentTime) {
            phase = 3; // fully faded in
        } else if (this.startFadeOut < currentTime && this.endFadeOut >= currentTime) {
            phase = 2; // fading in
        }

        float maxPossibleAlpha;
        switch (phase) {
            case 1:
                maxPossibleAlpha = 1f - (((float) (this.startFadeIn + duration - currentTime)) / duration);
                break;

            case 2:
                maxPossibleAlpha = (float) (this.endFadeOut - currentTime) / duration;
                break;

            case 3:
                maxPossibleAlpha = 1f;
                break;

            default:
                maxPossibleAlpha = 0f;
        }
        maxPossibleAlpha *= this.maxAlpha;
        if (this.checkBiomes() && this.checkHeights() && this.checkWeather()) { // check if environment is invalid
            if (this.alpha >= maxPossibleAlpha) {
                this.alpha = maxPossibleAlpha;
            } else {
                this.alpha += (Math.pow(this.alpha, this.transitionSpeed + 2f) + 0.005f);
                if (this.alpha > maxPossibleAlpha) this.alpha = maxPossibleAlpha;
            }
        } else {
            if (this.alpha > 0f) {
                this.alpha -= (Math.pow(this.alpha, this.transitionSpeed + 2f) + 0.005f);
                if (this.alpha < 0f) this.alpha = 0f;
            } else {
                this.alpha = 0f;
            }
        }

        if (this.alpha > 0.1 && this.changeFog) {
            SkyboxManager.shouldChangeFog = true;
            SkyboxManager.fogRed = this.fogRed;
            SkyboxManager.fogBlue = this.fogBlue;
            SkyboxManager.fogGreen = this.fogGreen;
        }

        return this.alpha;
    }

    private boolean checkBiomes() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;
        assert client.player != null;
        if (this.dimensions.size() == 0 || this.dimensions.contains(client.world.getRegistryKey().getValue())) {
            return this.biomes.size() == 0 || this.biomes.contains(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(client.player.getBlockPos())));
        }
        return false;
    }

    private boolean checkHeights() {
        assert MinecraftClient.getInstance().player != null;
        double playerHeight = MinecraftClient.getInstance().player.getY();
        boolean inRange = false;
        for (Float[] heightRange : this.heightRanges) {
            inRange = heightRange[0] < playerHeight && heightRange[1] > playerHeight;
            if (inRange) break;
        }
        return this.heightRanges.size() == 0 || inRange;
    }

    private boolean checkWeather() {
        ClientWorld world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert world != null;
        assert player != null;
        Biome.Precipitation precipitation = world.getBiome(player.getBlockPos()).getPrecipitation();
        if (this.weather.size() > 0) {
            if (this.weather.contains("thunder") && world.isThundering()) {
                return true;
            } else if (this.weather.contains("rain") && world.isRaining() && !world.isThundering() && precipitation == Biome.Precipitation.RAIN) {
                return true;
            } else if (this.weather.contains("snow") && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
                return true;
            } else return this.weather.contains("clear");
        } else {
            return true;
        }
    }

    public void renderDecorations(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, BufferBuilder bufferBuilder, float alpha) {
        if (this.decorations && !SkyboxManager.getInstance().hasRenderedDecorations()) {
            ClientWorld world = MinecraftClient.getInstance().world;
            assert world != null;
            float r = 1.0F - world.getRainGradient(tickDelta);
            // sun
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            Matrix4f matrix4f2 = matrices.peek().getModel();
            float s = 30.0F;
            worldRendererAccess.getTextureManager().bindTexture(worldRendererAccess.getSUN());
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -s, 100.0F, -s).texture(0.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, s, 100.0F, -s).texture(1.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, s, 100.0F, s).texture(1.0F, 1.0F).next();
            bufferBuilder.vertex(matrix4f2, -s, 100.0F, s).texture(0.0F, 1.0F).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            // moon
            s = 20.0F;
            worldRendererAccess.getTextureManager().bindTexture(worldRendererAccess.getMOON_PHASES());
            int t = world.getMoonPhase();
            int u = t % 4;
            int v = t / 4 % 2;
            float w = (float) (u) / 4.0F;
            float o = (float) (v) / 2.0F;
            float p = (float) (u + 1) / 4.0F;
            float q = (float) (v + 1) / 2.0F;
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -s, -100.0F, s).texture(p, q).next();
            bufferBuilder.vertex(matrix4f2, s, -100.0F, s).texture(w, q).next();
            bufferBuilder.vertex(matrix4f2, s, -100.0F, -s).texture(w, o).next();
            bufferBuilder.vertex(matrix4f2, -s, -100.0F, -s).texture(p, o).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            // stars
            RenderSystem.disableTexture();
            float aa = world.method_23787(tickDelta) * r;
            if (aa > 0.0F) {
                RenderSystem.color4f(aa, aa, aa, aa);
                worldRendererAccess.getStarsBuffer().bind();
                worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
                worldRendererAccess.getStarsBuffer().draw(matrices.peek().getModel(), 7);
                VertexBuffer.unbind();
                worldRendererAccess.getSkyVertexFormat().endDrawing();
            }
        }
    }
}
