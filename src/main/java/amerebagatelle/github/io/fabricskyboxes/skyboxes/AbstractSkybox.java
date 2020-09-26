package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.FabricSkyBoxesClient;
import amerebagatelle.github.io.fabricskyboxes.SkyboxManager;
import amerebagatelle.github.io.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.util.JsonObjectWrapper;
import amerebagatelle.github.io.fabricskyboxes.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;

public abstract class AbstractSkybox {
    /**
     * The current alpha for the skybox.  Expects all skyboxes extending this to accommodate this.
     * This variable is responsible for fading in/out skyboxes.
     */
    public float alpha;

    // ! These are the options variables.  Do not mess with these.
    public int startFadeIn = 0;
    public int endFadeIn = 0;
    public int startFadeOut = 0;
    public int endFadeOut = 0;
    public float maxAlpha = 1f;
    public float transitionSpeed = 1;
    public boolean changeFog = false;
    public float fogRed = 0;
    public float fogGreen = 0;
    public float fogBlue = 0;
    public boolean shouldRotate = false;
    public boolean decorations = false;
    public ArrayList<String> weather = new ArrayList<>();
    public ArrayList<Identifier> biomes = new ArrayList<>();
    public ArrayList<Identifier> dimensions = new ArrayList<>();
    public ArrayList<Float[]> heightRanges = new ArrayList<>();

    /**
     * The main render method for a skybox.
     * Override this if you are creating a skybox from this one.
     *
     * @param worldRendererAccess Access to the worldRenderer as skyboxes often require it.
     * @param matrices            The current MatrixStack.
     * @param tickDelta           The current tick delta.
     */
    public abstract void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    /**
     * Calculates the alpha value for the current time and conditions and returns it.
     *
     * @return The new alpha value.
     */
    public float getAlpha() {
        // this probably can take a good bit of performance improvement, idk tho
        assert MinecraftClient.getInstance().world != null;
        int currentTime = (int) MinecraftClient.getInstance().world.getTimeOfDay();
        int duration = Utils.getTicksBetween(startFadeIn, endFadeIn);
        int phase = 0; // default not showing
        if (startFadeIn < currentTime && endFadeIn >= currentTime) {
            phase = 1; // fading out
        } else if (endFadeIn < currentTime && startFadeOut >= currentTime) {
            phase = 3; // fully faded in
        } else if (startFadeOut < currentTime && endFadeOut >= currentTime) {
            phase = 2; // fading in
        }

        float maxPossibleAlpha;
        switch (phase) {
            case 1:
                maxPossibleAlpha = 1f - (((float) (startFadeIn + duration - currentTime)) / duration);
                break;

            case 2:
                maxPossibleAlpha = (float) (endFadeOut - currentTime) / duration;
                break;

            case 3:
                maxPossibleAlpha = 1f;
                break;

            default:
                maxPossibleAlpha = 0f;
        }
        maxPossibleAlpha *= maxAlpha;
        if (checkBiomes() && checkHeights() && checkWeather()) { // check if environment is invalid
            if (alpha >= maxPossibleAlpha) {
                alpha = maxPossibleAlpha;
            } else {
                alpha += (Math.pow(alpha, transitionSpeed + 2f) + 0.005f);
                if (alpha > maxPossibleAlpha) alpha = maxPossibleAlpha;
            }
        } else {
            if (alpha > 0f) {
                alpha -= (Math.pow(alpha, transitionSpeed + 2f) + 0.005f);
                if (alpha < 0f) alpha = 0f;
            } else {
                alpha = 0f;
            }
        }

        if (alpha > 0.1 && changeFog) {
            SkyboxManager.shouldChangeFog = true;
            SkyboxManager.fogRed = fogRed;
            SkyboxManager.fogBlue = fogBlue;
            SkyboxManager.fogGreen = fogGreen;
        }

        return alpha;
    }

    /**
     * @return Whether the current biomes and dimensions are valid for this skybox.
     */
    private boolean checkBiomes() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;
        assert client.player != null;
        if (dimensions.size() == 0 || dimensions.contains(client.world.getRegistryKey().getValue())) {
            return biomes.size() == 0 || biomes.contains(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(client.player.getBlockPos())));
        }
        return false;
    }

    /**
     * @return Whether the current heights are valid for this skybox.
     */
    private boolean checkHeights() {
        assert MinecraftClient.getInstance().player != null;
        double playerHeight = MinecraftClient.getInstance().player.getY();
        boolean inRange = false;
        for (Float[] heightRange : heightRanges) {
            inRange = heightRange[0] < playerHeight && heightRange[1] > playerHeight;
            if (inRange) break;
        }
        return heightRanges.size() == 0 || inRange;
    }

    /**
     * @return Whether the current weather is valid for this skybox.
     */
    private boolean checkWeather() {
        ClientWorld world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert world != null;
        assert player != null;
        Biome.Precipitation precipitation = world.getBiome(player.getBlockPos()).getPrecipitation();
        if (weather.size() > 0) {
            if (weather.contains("thunder") && world.isThundering()) {
                return true;
            } else if (weather.contains("rain") && world.isRaining() && !world.isThundering() && precipitation == Biome.Precipitation.RAIN) {
                return true;
            } else if (weather.contains("snow") && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
                return true;
            } else return weather.contains("clear");
        } else {
            return true;
        }
    }

    public void renderDecorations(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, BufferBuilder bufferBuilder, float alpha) {
        if (decorations && !SkyboxManager.getInstance().hasRenderedDecorations()) {
            RenderSystem.enableTexture();
            matrices.push();
            ClientWorld world = MinecraftClient.getInstance().world;
            assert world != null;
            RenderSystem.enableTexture();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F));
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
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableFog();
            matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F));
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.pop();
        }
    }

    /**
     * @return A string identifying your skybox type to be used in json parsing.
     */
    public abstract String getType();

    /**
     * Method for option parsing by json.  Override and extend this if your skybox has options of its own.
     */
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        try {
            startFadeIn = jsonObjectWrapper.get("startFadeIn").getAsInt();
            endFadeIn = jsonObjectWrapper.get("endFadeIn").getAsInt();
            startFadeOut = jsonObjectWrapper.get("startFadeOut").getAsInt();
            endFadeOut = jsonObjectWrapper.get("endFadeOut").getAsInt();
        } catch (NullPointerException e) {
            throw new JsonParseException("Could not get a required field for skybox of type " + getType());
        }
        // alpha changing
        maxAlpha = jsonObjectWrapper.getOptionalFloat("maxAlpha", 1f);
        transitionSpeed = jsonObjectWrapper.getOptionalFloat("transitionSpeed", 1f);
        // rotation
        shouldRotate = jsonObjectWrapper.getOptionalBoolean("shouldRotate", false);
        // decorations
        decorations = jsonObjectWrapper.getOptionalBoolean("decorations", false);
        // fog
        changeFog = jsonObjectWrapper.getOptionalBoolean("changeFog", false);
        fogRed = jsonObjectWrapper.getOptionalFloat("fogRed", 0f);
        fogGreen = jsonObjectWrapper.getOptionalFloat("fogGreen", 0f);
        fogBlue = jsonObjectWrapper.getOptionalFloat("fogBlue", 0f);
        // environment specifications
        JsonElement element;
        element = jsonObjectWrapper.getOptionalValue("weather");
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    weather.add(jsonElement.getAsString());
                }
            } else if (JsonHelper.isString(element)) {
                weather.add(element.getAsString());
            }
        }
        element = jsonObjectWrapper.getOptionalValue("biomes");
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    biomes.add(new Identifier(jsonElement.getAsString()));
                }
            } else if (JsonHelper.isString(element)) {
                biomes.add(new Identifier(element.getAsString()));
            }
        }
        element = jsonObjectWrapper.getOptionalValue("dimensions");
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    dimensions.add(new Identifier(jsonElement.getAsString()));
                }
            } else if (JsonHelper.isString(element)) {
                dimensions.add(new Identifier(element.getAsString()));
            }
        }
        element = jsonObjectWrapper.getOptionalValue("heightRanges");
        if (element != null) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                JsonArray insideArray = jsonElement.getAsJsonArray();
                float low = insideArray.get(0).getAsFloat();
                float high = insideArray.get(1).getAsFloat();
                if (high > low) {
                    heightRanges.add(new Float[]{low, high});
                } else {
                    FabricSkyBoxesClient.getLogger().warn("Skybox " + getType() + " contains invalid height ranges.");
                }
            }
        }
    }
}
