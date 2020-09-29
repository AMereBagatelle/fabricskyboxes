package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.gson.JsonParseException;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class MonoColorSkybox extends AbstractSkybox {
    private RGBA color;

    public MonoColorSkybox() {
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        if (alpha > 0) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = client.world;
            RenderSystem.disableTexture();
            BackgroundRenderer.setFogBlack();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.depthMask(false);
            RenderSystem.enableFog();
            RenderSystem.color3f(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
            worldRendererAccess.getLightSkyBuffer().bind();
            worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
            worldRendererAccess.getLightSkyBuffer().draw(matrices.peek().getModel(), 7);
            VertexBuffer.unbind();
            worldRendererAccess.getSkyVertexFormat().endDrawing();
            RenderSystem.disableFog();
            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float[] skyColor = world.getSkyProperties().getSkyColor(world.getSkyAngle(tickDelta), tickDelta);
            float skySide;
            float skyColorGreen;
            float o;
            float p;
            float q;
            if (skyColor != null) {
                RenderSystem.disableTexture();
                RenderSystem.shadeModel(7425);
                matrices.push();
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                skySide = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(skySide));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                float skyColorRed = skyColor[0];
                skyColorGreen = skyColor[1];
                float skyColorBlue = skyColor[2];
                Matrix4f matrix4f = matrices.peek().getModel();
                bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(skyColorRed, skyColorGreen, skyColorBlue, skyColor[3]).next();

                for (int n = 0; n <= 16; ++n) {
                    o = (float) n * 6.2831855F / 16.0F;
                    p = MathHelper.sin(o);
                    q = MathHelper.cos(o);
                    bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * skyColor[3]).color(skyColor[0], skyColor[1], skyColor[2], 0.0F).next();
                }

                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
                matrices.pop();
                RenderSystem.shadeModel(7424);
            }

            renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, alpha);

            RenderSystem.disableTexture();
            RenderSystem.color3f(0.0F, 0.0F, 0.0F);
            assert client.player != null;
            double d = client.player.getCameraPosVec(tickDelta).y - world.getLevelProperties().getSkyDarknessHeight();
            if (d < 0.0D) {
                matrices.push();
                matrices.translate(0.0D, 12.0D, 0.0D);
                worldRendererAccess.getDarkSkyBuffer().bind();
                worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
                worldRendererAccess.getDarkSkyBuffer().draw(matrices.peek().getModel(), 7);
                VertexBuffer.unbind();
                worldRendererAccess.getSkyVertexFormat().endDrawing();
                matrices.pop();
            }

            if (world.getSkyProperties().isAlternateSkyColor()) {
                RenderSystem.color3f(this.color.getRed() * 0.2F + 0.04F, this.color.getBlue() * 0.2F + 0.04F, this.color.getGreen() * 0.6F + 0.1F);
            } else {
                RenderSystem.color3f(this.color.getRed(), this.color.getBlue(), this.color.getGreen());
            }

            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
            RenderSystem.disableFog();
        }
    }

    @Override
    public String getType() {
        return "monocolor";
    }

    @Override
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        super.parseJson(jsonObjectWrapper);
        try {
            this.color = new RGBA(jsonObjectWrapper.get("red").getAsFloat(), jsonObjectWrapper.get("blue").getAsFloat(), jsonObjectWrapper.get("green").getAsFloat());
        } catch (NullPointerException e) {
            throw new JsonParseException("Could not get a required field for skybox of type " + getType());
        }
    }
}
