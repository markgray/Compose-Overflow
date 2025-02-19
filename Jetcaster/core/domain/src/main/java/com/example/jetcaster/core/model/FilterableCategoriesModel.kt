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

package com.example.jetcaster.core.model

/**
 * Model holding a list of categories and the selected category in the collection.
 *
 * This class holds a list of available categories and the currently selected category.
 * It also provides a convenient way to check if the model is considered "empty,"
 * which is defined as having either no categories or no selected category.
 *
 * @property categories The list of available categories. Defaults to an empty list.
 * @property selectedCategory The currently selected category. Can be `null` if no category is
 * selected. Defaults to `null`.
 * @property isEmpty A boolean flag indicating whether the model is considered empty. It is true if
 * either the [List] of [CategoryInfo] field [categories] is empty or [CategoryInfo] field
 * [selectedCategory] is `null`, otherwise `false`.
 */
data class FilterableCategoriesModel(
    val categories: List<CategoryInfo> = emptyList(),
    val selectedCategory: CategoryInfo? = null
) {
    val isEmpty: Boolean = categories.isEmpty() || selectedCategory == null
}
