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

package com.example.jetsnack.ui

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.home.FilterScreen
import com.example.jetsnack.ui.snackdetail.SnackDetail

/**
 * This is used to create a `key` to the [SharedContentState] that governs the use of the [Modifier]
 * created by the [SharedTransitionScope.sharedBounds] extension function in multiple places.
 */
data class SnackSharedElementKey(
    /**
     * The [Snack.id] of the [Snack] that is being shared using the [SharedTransitionScope].
     */
    val snackId: Long,
    /**
     * A [String] identifying the origin of the shared transition to be used.
     */
    val origin: String,
    /**
     * The [SnackSharedElementType] of the element that is being shared.
     */
    val type: SnackSharedElementType
)

/**
 * This is used to identify the type of element that is being shared using the shared transition.
 */
enum class SnackSharedElementType {
    /**
     * Used when the bounds of the shared transition are being animated. In our case this is used
     * to animate the `size` of the [RoundedCornerShape] that is shared between screens.
     */
    Bounds,
    /**
     * The element being shared is the image of the [Snack] in a [SnackImage]
     */
    Image,
    /**
     * The element being shared is a [Text] displaying the [Snack.name] of the [Snack].
     */
    Title,
    /**
     * The element being shared is a [Text] displaying the [Snack.tagline] of the [Snack].
     */
    Tagline,
    /**
     * The element being shared is the background gradient from a highlighted [SnackCollection].
     * It becomes the background gradient used for the `Header` of the [SnackDetail] Composable.
     */
    Background
}

/**
 * Used as the `key` of the [SharedContentState] that governs the use of the [Modifier] created
 * by [SharedTransitionScope.sharedBounds] extension function when the "Filters" [IconButton] of
 * [FilterBar] is clicked and the transition to [FilterScreen] occurs.
 */
object FilterSharedElementKey
