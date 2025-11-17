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

import androidx.compose.ui.unit.sp
import androidx.glance.text.TextStyle

/**
 * A collection of [TextStyle]s for the Jetnews Glance widget.
 *
 * These styles are simplified versions of the main app's typography, suitable for the constraints
 * of a Glance widget.
 */
object JetnewsGlanceTextStyles {
    /**
     * The largest body style.
     *
     * This is used for the main text of a story.
     */
    val bodyLarge: TextStyle = TextStyle(fontSize = 16.sp)
    
    /**
     * A small-sized text style for body content, typically used for metadata, captions, or less
     * important information.
     */
    val bodySmall: TextStyle = TextStyle(fontSize = 12.sp)
}
