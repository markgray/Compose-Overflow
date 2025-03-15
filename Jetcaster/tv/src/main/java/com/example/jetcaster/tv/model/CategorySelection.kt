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

package com.example.jetcaster.tv.model

import androidx.compose.runtime.Immutable
import com.example.jetcaster.core.model.CategoryInfo

/**
 * Represents a category and its selection state.
 *
 * This data class combines information about a category ([CategoryInfo]) with a boolean
 * flag indicating whether the category is currently selected. It is designed to be used
 * in scenarios where a user can select or deselect categories from a list.
 *
 * @property categoryInfo The [CategoryInfo] object containing the details of the category.
 * @property isSelected A boolean value indicating whether the category is selected.
 * Defaults to `false`.
 */
data class CategorySelection(val categoryInfo: CategoryInfo, val isSelected: Boolean = false)

/**
 * Represents a list of [CategorySelection] objects.
 *
 * This class is an immutable data class that wraps a list of [CategorySelection]
 * objects and implements the [List] interface by delegation. This means it
 * behaves like a standard Kotlin [List] but its contents are specifically
 * designed for managing a selection of categories.
 *
 * The `by member` clause allows `CategorySelectionList` to delegate all of its
 * `List` interface methods (e.g., `size`, `get`, `iterator`, etc.) directly to the
 * underlying `member` list. This effectively makes `CategorySelectionList` act as
 * a specialized view or wrapper around the core list of [CategorySelection] items.
 *
 * @property member The underlying list of [CategorySelection] objects. This list is the core data
 * structure that holds the category selection information.
 * @constructor Creates a new `CategorySelectionList` with the provided list of [CategorySelection].
 */
@Immutable
data class CategorySelectionList(
    val member: List<CategorySelection>
) : List<CategorySelection> by member
