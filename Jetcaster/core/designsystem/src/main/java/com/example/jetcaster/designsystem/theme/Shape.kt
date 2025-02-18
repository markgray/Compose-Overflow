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

package com.example.jetcaster.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Defines the [MaterialTheme.shapes] shape scheme for the Jetcaster application.
 *
 * This property provides a set of [Shapes] used throughout the Jetcaster app's UI.
 * It specifies the corner rounding for various UI elements, categorized by size:
 *
 * - **small**:  Represents small UI elements like buttons or chips. It uses a fully rounded
 *   shape (50% corner radius).
 * - **medium**:  Represents medium-sized UI elements like cards or dialogs. It uses a rounded
 *   corner shape with a radius of 8dp.
 * - **large**:  Represents large UI elements like sheets or containers. It uses a rounded corner
 *   shape with a radius of 16dp.
 *
 * These shapes are designed to provide a consistent and visually appealing look and feel
 * across the entire application.
 *
 * @see Shapes.small
 * @see Shapes.medium
 * @see Shapes.large
 */
val JetcasterShapes: Shapes = Shapes(
    small = RoundedCornerShape(percent = 50),
    medium = RoundedCornerShape(size = 8.dp),
    large = RoundedCornerShape(size = 16.dp)
)
