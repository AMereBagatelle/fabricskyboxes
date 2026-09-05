package me.flashyreese.mods.nuit.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NuitRenderBackendTest {
    @Test
    void transformScopeUsesAndRestoresTheSuppliedPoseStack() {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(2.0F, 3.0F, 4.0F);
        Matrix4f expectedBase = new Matrix4f(poseStack.last().pose());

        try (NuitRenderBackend.TransformScope transform = NuitRenderBackend.pushTransform(poseStack)) {
            assertSame(poseStack, transform.poseStack());
            transform.poseStack().mulPose(new Matrix4f().rotationY(0.75F));
            assertFalse(expectedBase.equals(transform.poseStack().last().pose(), 1.0E-6F));
        }

        assertTrue(expectedBase.equals(poseStack.last().pose(), 1.0E-6F));
    }
}
