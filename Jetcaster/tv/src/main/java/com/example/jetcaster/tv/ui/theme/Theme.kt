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

package com.example.jetcaster.tv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme

/**
 *  JetcasterTheme is the application's custom theme, built on top of Material Design 3.
 *
 *  It provides consistent theming across the Jetcaster application, adapting to
 *  the user's system preference for dark mode or light mode.
 *
 *  @param isInDarkTheme Specifies whether the theme should be in dark mode. Defaults
 *  to the system's dark mode setting.
 *  @param content The composable content to be displayed within this theme.
 */
@Composable
fun JetcasterTheme(
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        colorSchemeForDarkMode
    } else {
        colorSchemeForLightMode
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
