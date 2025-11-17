/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetnews.glance.ui.theme

import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import com.example.jetnews.ui.theme.DarkColors
import com.example.jetnews.ui.theme.LightColors

/**
 * A color scheme for the Jetnews Glance widget.
 *
 * This object provides a set of [ColorProviders] that are adapted from the main app's
 * Material 3 color scheme (`LightColors` and `DarkColors`), making them suitable for
 * use in a Glance App Widget. It ensures the widget's appearance is consistent with
 * the main application's light and dark themes.
 */
object JetnewsGlanceColorScheme {
    /**
     * A set of [ColorProviders] for the Jetnews Glance widget, adapted from the main app's
     * Material 3 color scheme.
     *
     * This provides both light and dark color schemes, ensuring the widget's appearance is
     * consistent with the main application's themes.
     *
     * @see com.example.jetnews.ui.theme.LightColors
     * @see com.example.jetnews.ui.theme.DarkColors
     */
    val colors: ColorProviders = ColorProviders(
        light = LightColors,
        dark = DarkColors
    )

    /**
     * A subtle outline color variant, often used for dividers or component borders.
     *
     * This color is derived from the `onSurface` color of the light and dark themes,
     * but with a very low alpha to create a faint, decorative, and non-intrusive separation.
     */
    val outlineVariant: ColorProvider = ColorProvider(
        day = LightColors.onSurface.copy(alpha = 0.1f),
        night = DarkColors.onSurface.copy(alpha = 0.1f)
    )
}
