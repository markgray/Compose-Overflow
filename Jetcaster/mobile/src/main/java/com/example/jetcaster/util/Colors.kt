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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

/**
 * Calculates the contrast ratio between this color and a background color.
 *
 * The contrast ratio is a measure of the difference in luminance or perceived brightness between
 * two colors. It ranges from 1:1 (no contrast, e.g., white on white) to 21:1 (maximum contrast,
 * e.g., black on white). A higher contrast ratio indicates better readability and accessibility,
 * especially for users with visual impairments.
 *
 * This function first composites the foreground color over the background if the foreground color
 * is semi-transparent. Then, it calculates the luminance of both the (potentially composited)
 * foreground and background colors, adding a small offset (0.05) to avoid division by zero or
 * extremely low values. Finally, it returns the ratio of the higher luminance to the lower
 * luminance, representing the contrast ratio.
 *
 * @param background The background color against which to calculate the contrast.
 * @return The contrast ratio between this color and the background color.
 *         A value of 1.0f or less indicates very low or no contrast.
 *         A higher value indicates higher contrast.
 *         The value will always be greater than or equal to 1.0f.
 */
@Suppress("unused")
fun Color.contrastAgainst(background: Color): Float {
    val fg: Color = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance: Float = fg.luminance() + 0.05f
    val bgLuminance: Float = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}
