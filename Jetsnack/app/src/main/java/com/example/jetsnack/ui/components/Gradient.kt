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

package com.example.jetsnack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates a [DrawModifier] that draws a restangle after the layout's contents. The rectangle uses
 * a [Brush.linearGradient] constructed from our [List] of [Color] parameter [colors] as its `brush`
 * argument, and our [BlendMode] parameter [blendMode] as its `blendMode` argument.
 */
fun Modifier.diagonalGradientTint(
    colors: List<Color>,
    blendMode: BlendMode
): Modifier = drawWithContent {
    drawContent()
    drawRect(
        brush = Brush.linearGradient(colors),
        blendMode = blendMode
    )
}

/**
 * Creates a [Modifier.background] whose `brush` argument is a [Brush.horizontalGradient] whose
 * `colors` argument is our [List] of [Color] parameter [colors], whose `startX` argument is minus
 * our [Float] parameter [offset], whose `endX` argument is our [Float] parameter [width] minus our
 * [Float] parameter [offset], and whose `tileMode` argument is [TileMode.Mirror].
 */
fun Modifier.offsetGradientBackground(
    colors: List<Color>,
    width: Float,
    offset: Float = 0f
): Modifier = background(
    brush = Brush.horizontalGradient(
        colors = colors,
        startX = -offset,
        endX = width - offset,
        tileMode = TileMode.Mirror
    )
)

/**
 * Creates a [Modifier.drawBehind] which draws a rectangle behind the `content` using as its `brush`
 * argument a [Brush.horizontalGradient] whose `colors` argument is our [List] of [Color] parameter
 * [colors], whose `startX` argument is minus the value our lambda returning [Float] parameter [offset]
 * returns, whose `endX` argument is the [Float] returned by our lambda returning [Float] parameter
 * [width] minus the [Float] returned by our lambda returning [Float] parameter [offset], and whose
 * `tileMode` argument is [TileMode.Mirror].
 */
fun Modifier.offsetGradientBackground(
    colors: List<Color>,
    width: Density.() -> Float,
    offset: Density.() -> Float = { 0f }
): Modifier = drawBehind {
    val actualOffset: Float = offset()

    drawRect(
        brush = Brush.horizontalGradient(
            colors = colors,
            startX = -actualOffset,
            endX = width() - actualOffset,
            tileMode = TileMode.Mirror
        )
    )
}

/**
 *
 */
fun Modifier.diagonalGradientBorder(
    colors: List<Color>,
    borderSize: Dp = 2.dp,
    shape: Shape
): Modifier = border(
    width = borderSize,
    brush = Brush.linearGradient(colors),
    shape = shape
)

/**
 *
 */
fun Modifier.fadeInDiagonalGradientBorder(
    showBorder: Boolean,
    colors: List<Color>,
    borderSize: Dp = 2.dp,
    shape: Shape
): Modifier = composed {
    val animatedColors = List(colors.size) { i ->
        animateColorAsState(
            if (showBorder) colors[i] else colors[i].copy(alpha = 0f),
            label = "animated color"
        ).value
    }
    diagonalGradientBorder(
        colors = animatedColors,
        borderSize = borderSize,
        shape = shape
    )
}
