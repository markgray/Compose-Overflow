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

package com.example.jetcaster.designsystem.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.jetcaster.core.designsystem.R

/**
 * Represents the [Montserrat] font family, including various font weights.
 *
 * This FontFamily includes the following weights:
 * - Light (Weight 300)
 * - Regular (Weight 400)
 * - Medium (Weight 500)
 * - SemiBold (Weight 600)
 *
 * To use this font, simply reference `Montserrat` in your composable functions
 * within a `TextStyle`. For example:
 *
 * ```kotlin
 * Text(
 *     text = "Hello, Montserrat!",
 *     style = TextStyle(
 *         fontFamily = Montserrat,
 *         fontWeight = FontWeight.Medium,
 *         fontSize = 16.sp
 *     )
 * )
 * ```
 *
 * Ensure that the corresponding font files (montserrat_light.ttf,
 * montserrat_regular.ttf, montserrat_medium.ttf, montserrat_semibold.ttf)
 * are included in your project's `res/font` directory.
 */
val Montserrat: FontFamily = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)
