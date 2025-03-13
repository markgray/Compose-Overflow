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

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable

/**
 * Adds a single item to a [LazyVerticalGrid] that spans the entire width of the grid.
 *
 * This function is a convenience wrapper around [LazyGridScope.item] that automatically sets
 * the [GridItemSpan] to `currentLineSpan` ensuring the item takes up all available columns
 * in the current row. This is particularly useful for adding full-width elements like
 * headers, footers, or separators within a grid layout.
 *
 * @param key An optional stable and unique key that identifies the item. Using the same key
 * for multiple items in the grid is not allowed. If you don't specify a key, the position
 * of the item in the list will be used as a key.
 * @param contentType An optional content type that describes the item. This can be used for
 * optimizations and to differentiate items with different layouts or data.
 * @param content The composable content of the full-width item.
 */
fun LazyGridScope.fullWidthItem(
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyGridItemScope.() -> Unit
): Unit = item(
    span = { GridItemSpan(currentLineSpan = this.maxLineSpan) },
    key = key,
    contentType = contentType,
    content = content
)
