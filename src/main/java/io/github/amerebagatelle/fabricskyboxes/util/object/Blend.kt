package io.github.amerebagatelle.fabricskyboxes.util.`object`

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor
import com.mojang.blaze3d.systems.RenderSystem
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient
import org.lwjgl.opengl.GL14

data class Blend(
    val type: String?,
    val sFactor: Int?,
    val dFactor: Int?,
    val equation: Int?
) {
    private val blendFunc: (Float) -> (Unit)

    init {
        if (type != null) {
            when (type) {
                "add" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE)
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
                }

                "subtract" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ZERO)
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(alpha!!, alpha!!, alpha!!, 1.0f)
                }

                "multiply" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO)
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(alpha!!, alpha!!, alpha!!, alpha!!)
                }

                "screen" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR)
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(alpha!!, alpha!!, alpha!!, 1.0f)
                }

                "replace" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.ZERO, GlStateManager.DstFactor.ONE)
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
                }

                "alpha" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(
                        SrcFactor.SRC_ALPHA,
                        GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
                    )
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
                }

                "burn" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR)
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(alpha!!, alpha!!, alpha!!, 1.0f)
                }

                "dodge" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.DST_COLOR, GlStateManager.DstFactor.ONE)
                    RenderSystem.blendEquation(Equation.ADD.value)
                    RenderSystem.setShaderColor(alpha!!, alpha!!, alpha!!, 1.0f)
                }

                "darken" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.ONE, GlStateManager.DstFactor.ONE)
                    RenderSystem.blendEquation(Equation.MIN.value)
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
                }

                "lighten" -> blendFunc = { alpha: Float? ->
                    RenderSystem.blendFunc(SrcFactor.ONE, GlStateManager.DstFactor.ONE)
                    RenderSystem.blendEquation(Equation.MAX.value)
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
                }

                else -> {
                    FabricSkyBoxesClient.getLogger().error("Blend mode is set to an invalid or unsupported value.")
                    blendFunc = { alpha: Float? ->
                        RenderSystem.defaultBlendFunc()
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
                    }
                }
            }
        } else if (this.isValidFactor(sFactor ?: 0) && this.isValidFactor(dFactor ?: 0) && this.isValidEquation(equation ?: 0)) {
            blendFunc = { alpha: Float? ->
                RenderSystem.blendFunc(sFactor!!, dFactor!!)
                RenderSystem.blendEquation(equation!!)
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
            }
        } else {
            blendFunc = { alpha: Float? ->
                RenderSystem.defaultBlendFunc()
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha!!)
            }
        }
    }

    fun applyBlendFunc(alpha: Float) = blendFunc.invoke(alpha)

    private fun isValidFactor(factor: Int): Boolean {
        return SrcFactor.values().count { factor1: SrcFactor -> factor == factor1.value } == 1
    }

    private fun isValidEquation(equation: Int): Boolean {
        return Equation.values().count { equation1: Equation -> equation == equation1.value } == 1
    }

    enum class Equation(val value: Int) {
        ADD(GL14.GL_FUNC_ADD),
        SUBTRACT(GL14.GL_FUNC_SUBTRACT),
        REVERSE_SUBTRACT(GL14.GL_FUNC_REVERSE_SUBTRACT),
        MIN(GL14.GL_MIN),
        MAX(GL14.GL_MAX)
    }
}
