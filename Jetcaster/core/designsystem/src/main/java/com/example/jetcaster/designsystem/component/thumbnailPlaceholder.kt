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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.example.jetcaster.designsystem.theme.surfaceVariantDark
import com.example.jetcaster.designsystem.theme.surfaceVariantLight

/**
 * Creates a default solid color brush for thumbnail placeholders.
 *
 * This function provides a simple way to create a [Brush] that can be used as a placeholder for
 * thumbnails while they are loading or if they fail to load. It uses a solid color, defaulting to
 * the value provided by [thumbnailPlaceHolderDefaultColor].
 *
 * @param color The color to use for the brush. Defaults to the color returned by
 * [thumbnailPlaceHolderDefaultColor].
 * @return A [Brush] instance filled with the specified color.
 */
@Composable
internal fun thumbnailPlaceholderDefaultBrush(
    color: Color = thumbnailPlaceHolderDefaultColor()
): Brush {
    return SolidColor(value = color)
}

/**
 * Determines the default placeholder color for thumbnails based on the current dark mode setting.
 *
 * This function provides a color suitable for use as a placeholder background
 * when loading thumbnails, ensuring it adapts to the user's system theme.
 *
 * @param isInDarkMode [Boolean] indicating if the current UI is in dark mode. Defaults to the
 * system's dark mode setting using [isSystemInDarkTheme].
 * @return A [Color] representing the default thumbnail placeholder color. Returns [surfaceVariantDark]
 * if in dark mode, [surfaceVariantLight] otherwise.
 *
 * @see isSystemInDarkTheme
 * @see surfaceVariantDark
 * @see surfaceVariantLight
 */
@Composable
private fun thumbnailPlaceHolderDefaultColor(
    isInDarkMode: Boolean = isSystemInDarkTheme()
): Color {
    return if (isInDarkMode) {
        surfaceVariantDark
    } else {
        surfaceVariantLight
    }
}
