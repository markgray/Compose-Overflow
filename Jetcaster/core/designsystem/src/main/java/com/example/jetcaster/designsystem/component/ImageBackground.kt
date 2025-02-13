/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * Displays an image with a background color scrim overlay.
 *
 * This composable displays an image loaded from the given URL. If the URL is valid,
 * the image will be loaded and displayed. On top of the image, a semi-transparent
 * color scrim will be drawn, allowing the underlying image to be partially visible
 * while adding a color wash effect.
 *
 * If the image fails to load, a transparent canvas will be displayed and overlayed with the scrim.
 *
 * @param url The URL of the image to load. If `null` or empty, no image will be loaded.
 * @param color The color of the scrim to overlay on the image. The alpha value of this color
 * determines the intensity of the scrim. A fully opaque color will completely obscure the image,
 * while a transparent color will have no effect.
 * @param modifier Modifier to apply to the outer layout.
 */
@Composable
fun ImageBackgroundColorScrim(
    url: String?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        url = url,
        modifier = modifier,
        overlay = {
            drawRect(color = color)
        }
    )
}

/**
 * Displays an image with a radial gradient scrim overlay.
 *
 * This composable displays an image fetched from the given URL and overlays it with a radial gradient.
 * The gradient starts from the bottom of the image and fades outwards, creating a visually appealing
 * scrim effect. The blend mode is set to `Multiply` to darken the image where the gradient overlaps.
 *
 * @param url The URL of the image to display. If `null`, no image is displayed and only the
 * gradient is drawn.
 * @param colors A list of colors to use for the radial gradient. The gradient will transition
 * between these colors from the center outwards.
 * @param modifier Modifier to apply to the underlying layout.
 */
@Composable
fun ImageBackgroundRadialGradientScrim(
    url: String?,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        url = url,
        modifier = modifier,
        overlay = {
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset(0f, size.height),
                radius = size.width * 1.5f
            )
            drawRect(brush = brush, blendMode = BlendMode.Multiply)
        }
    )
}

/**
 * A composable function that displays an image from a given URL with a customizable overlay.
 *
 * This function uses [AsyncImage] to load an image from the provided URL and displays it
 * (scaled by 150%)? . It also allows you to draw an overlay on top of the image using the [overlay]
 * lambda.
 *
 * The image is scaled to fill the available width and crop the excess, maintaining the aspect ratio.
 *
 * @param url The URL of the image to load. Can be `null`, in which case no image is displayed.
 * @param overlay A lambda that defines how to draw the overlay on top of the image. This lambda
 * receives a [DrawScope] which can be used to draw custom shapes, text, or other graphics on top
 * of the image. This lambda will be executed after the image is drawn.
 * @param modifier The [Modifier] to be applied to the image. Defaults to [Modifier].
 */
@Composable
fun ImageBackground(
    url: String?,
    overlay: DrawScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            }
    )
}
