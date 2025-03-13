/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.util

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Applies a radial gradient scrim effect in the foreground emanating from the top
 * center quarter of the element.
 *
 * This modifier creates a radial gradient that starts with the specified [color] at the center
 * and fades to transparent towards the edges. The center of the gradient is slightly offset
 * vertically towards the top of the composable. The gradient's radius is half of the larger
 * dimension (width or height) of the composable.
 *
 * The gradient effect provides a visual cue, often used to highlight a central area or to
 * softly obscure the edges of a composable.
 *
 * @param color The color to use for the center of the radial gradient. The gradient will transition
 * from this color to transparent.
 * @return A [Modifier] that draws a radial gradient scrim as a background.
 */
fun Modifier.radialGradientScrim(color: Color): Modifier {
    val radialGradient: ShaderBrush = object : ShaderBrush() {
        /**
         * Creates a radial gradient shader for a given size.
         *
         * This function generates a shader that creates a radial gradient effect, emanating
         * from a center point slightly above the vertical center of the provided size.
         * The gradient transitions from a specified color to transparent, with a smooth
         * fade-out towards the edges.
         *
         * @param size The size (width and height) of the area where the shader will be applied.
         *   This determines the center, radius, and overall dimensions of the gradient.
         * @return A [Shader] object representing the radial gradient. This can be used
         *   to fill shapes or backgrounds with the defined gradient effect.
         *
         * @see RadialGradientShader
         * @see Size
         * @see Color
         */
        override fun createShader(size: Size): Shader {
            val largerDimension: Float = max(size.height, size.width)
            return RadialGradientShader(
                center = size.center.copy(y = size.height / 4),
                colors = listOf(color, Color.Transparent),
                radius = largerDimension / 2,
                colorStops = listOf(0f, 0.9f)
            )
        }
    }
    return this.background(brush = radialGradient)
}

/**
 * Draws a vertical gradient scrim in the foreground using [VerticalGradientElement].
 *
 * @param color The color of the gradient scrim.
 * @param startYPercentage The start y value, in percentage of the layout's height (0f to 1f)
 * @param endYPercentage The end y value, in percentage of the layout's height (0f to 1f). This
 * value can be smaller than [startYPercentage]. If that is the case, then the gradient direction
 * will reverse (decaying downwards, instead of decaying upwards).
 * @param decay The exponential decay to apply to the gradient. Defaults to `1.0f` which is
 * a linear gradient.
 * @param numStops The number of color stops to draw in the gradient. Higher numbers result in
 * the higher visual quality at the cost of draw performance. Defaults to `16`.
 */
fun Modifier.verticalGradientScrim(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) startYPercentage: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) endYPercentage: Float = 1f,
    decay: Float = 1.0f,
    numStops: Int = 16
): Modifier = this then VerticalGradientElement(
    color = color,
    startYPercentage = startYPercentage,
    endYPercentage = endYPercentage,
    decay = decay,
    numStops = numStops
)

/**
 * The [ModifierNodeElement] that manages the [Modifier.Node] instance [VerticalGradientModifier]
 * which is used to draw a vertical gradient scrim.
 *
 * This element defines the properties and behavior for drawing a vertical gradient
 * overlay on a composable. It supports linear and non-linear (decaying) gradients,
 * allowing for various visual effects.
 *
 * @property color The base color of the gradient. The gradient will transition from transparent
 * (alpha = 0) to this color.
 * @property startYPercentage The starting percentage of the gradient along the vertical axis
 * (0.0f - top, 1.0f - bottom). Defaults to 0f.
 * @property endYPercentage The ending percentage of the gradient along the vertical axis
 * (0.0f - top, 1.0f - bottom). Defaults to 1f.
 * @property decay The decay factor controlling the non-linearity of the gradient. A value of 1.0f
 * represents a linear gradient. Values greater than 1.0f will result in a faster fade-out from the
 * `color`. Values less than 1 will result in a slower fade out. Defaults to 1.0f.
 * @property numStops The number of color stops used to create the gradient when `decay` is not 1.0f
 * (non-linear). More stops result in a smoother gradient. Defaults to 16.
 */
private data class VerticalGradientElement(
    var color: Color,
    var startYPercentage: Float = 0f,
    var endYPercentage: Float = 1f,
    var decay: Float = 1.0f,
    var numStops: Int = 16
) : ModifierNodeElement<VerticalGradientModifier>() {
    /**
     * Creates a lambda function that defines how to draw a vertical gradient within a [DrawScope].
     *
     * This function generates a vertical gradient brush based on the provided parameters
     * and returns a lambda that can be used with Compose's `drawBehind` or other drawing
     * modifiers. The gradient can have either linear or non-linear decay, controlled by
     * the `decay` parameter.
     *
     * @param color The base color of the gradient. The alpha value will be modified to create the
     * gradient effect.
     * @param numStops The number of color stops to use in the gradient when `decay` is not 1.
     * Ignored if `decay` is 1. Must be at least 2.
     * @param decay The decay exponent that controls the non-linearity of the gradient's alpha
     * change. A value of 1 represents linear decay. Values greater than 1 result in slower initial
     * decay, while values less than 1 result in faster initial decay. Must be greater than 0.
     * @param startYPercentage The starting Y-coordinate of the gradient as a percentage of the
     * total height (0.0 to 1.0).
     * @param endYPercentage The ending Y-coordinate of the gradient as a percentage of the total
     * height (0.0 to 1.0).
     * @return A lambda function of type `DrawScope.() -> Unit` that, when invoked, draws a
     * vertical gradient within the provided `DrawScope`.
     */
    fun createOnDraw(): DrawScope.() -> Unit {
        val colors: List<Color> = if (decay != 1f) {
            // If we have a non-linear decay, we need to create the color gradient steps
            // manually
            val baseAlpha: Float = color.alpha
            List(numStops) { i: Int ->
                val x: Float = i * 1f / (numStops - 1)
                val opacity: Float = x.pow(decay)
                color.copy(alpha = baseAlpha * opacity)
            }
        } else {
            // If we have a linear decay, we just create a simple list of start + end colors
            listOf(color.copy(alpha = 0f), color)
        }

        val brush: Brush =
            // Reverse the gradient if decaying downwards
            Brush.verticalGradient(
                colors = if (startYPercentage < endYPercentage) colors else colors.reversed(),
            )

        return {
            val topLeft = Offset(0f, size.height * min(startYPercentage, endYPercentage))
            val bottomRight =
                Offset(size.width, size.height * max(startYPercentage, endYPercentage))

            drawRect(
                topLeft = topLeft,
                size = Rect(topLeft = topLeft, bottomRight = bottomRight).size,
                brush = brush
            )
        }
    }

    /**
     * Creates a [VerticalGradientModifier] with the specified drawing logic.
     *
     * This function is responsible for creating a modifier that applies a vertical gradient to the
     * composable it modifies. It achieves this by leveraging the [VerticalGradientModifier] class
     * and delegating the actual drawing operation to the function returned by [createOnDraw].
     *
     * @return A [VerticalGradientModifier] instance configured with the drawing logic from
     * [createOnDraw].
     */
    override fun create() = VerticalGradientModifier(onDraw = createOnDraw())

    /**
     * Called when a modifier is applied to a Layout whose inputs have changed from the previous
     * application. This function will have the current node instance passed in as a parameter,
     * and it is expected that the node will be brought up to date.
     *
     * @param node The current node instance.
     */
    override fun update(node: VerticalGradientModifier) {
        node.onDraw = createOnDraw()
    }

    /**
     * Allow this custom modifier to be inspected in the layout inspector
     */
    override fun InspectorInfo.inspectableProperties() {
        name = "verticalGradientScrim"
        properties["color"] = color
        properties["startYPercentage"] = startYPercentage
        properties["endYPercentage"] = endYPercentage
        properties["decay"] = decay
        properties["numStops"] = numStops
    }
}

/**
 * A modifier that draws a vertical gradient before drawing the content.
 *
 * This modifier utilizes a `DrawModifierNode` to efficiently apply a custom
 * drawing operation within the composition. It allows for custom gradient
 * drawing logic to be defined via the `onDraw` lambda, which is invoked
 * within the drawing scope. The existing content is then drawn on top of the
 * gradient.
 *
 * @property onDraw A lambda function that provides the drawing instructions for
 * the vertical gradient. It receives a [DrawScope] instance as its context,
 * enabling access to drawing primitives and the current drawing canvas.
 */
private class VerticalGradientModifier(
    var onDraw: DrawScope.() -> Unit
) : Modifier.Node(), DrawModifierNode {

    override fun ContentDrawScope.draw() {
        onDraw()
        drawContent()
    }
}
