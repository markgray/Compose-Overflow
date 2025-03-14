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

package com.example.jetcaster.util

import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * Indicates whether the current window size class is considered "compact".
 *
 * A window is considered compact if either its width or its height falls within the
 * [WindowWidthSizeClass.COMPACT] or [WindowHeightSizeClass.COMPACT] categories, respectively.
 * This typically represents smaller screens, like those found on most phones in portrait
 * or smaller landscape orientations.
 *
 * @see WindowSizeClass
 * @see WindowWidthSizeClass
 * @see WindowHeightSizeClass
 */
val WindowSizeClass.isCompact: Boolean
    get() = windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
        windowHeightSizeClass == WindowHeightSizeClass.COMPACT
