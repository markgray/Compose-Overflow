/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetcaster.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

/**
 * WearAppTheme is the custom Material Theme for Wear OS applications.
 *
 * This function applies a specific Material Theme configuration to the composable content it wraps,
 * utilizing a predefined color palette (`wearColorPalette`) and typography (`Typography`) designed
 * for Wear OS devices. It also defaults to using the standard Material Wear shapes, which are
 * recommended for their optimization on both round and non-round screens.
 *
 * @param content The composable content to which the Wear OS Material Theme will be applied.
 * This is a lambda function that takes no parameters and returns a `Composable` unit.
 */
@Composable
fun WearAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = wearColorPalette,
        typography = Typography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = content
    )
}
