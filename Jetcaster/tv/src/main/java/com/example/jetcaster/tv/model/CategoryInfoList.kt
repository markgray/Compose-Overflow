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
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.asExternalModel

/**
 * Represents a list of [CategoryInfo] objects.
 *
 * This class is an immutable wrapper around a list of [CategoryInfo] instances. It leverages the
 * delegated `List` interface to provide direct access to the underlying list's functionality.
 *
 * @property member The underlying list of [CategoryInfo] objects.
 * @constructor Creates a new `CategoryInfoList` with the given list of [CategoryInfo].
 */
@Immutable
data class CategoryInfoList(val member: List<CategoryInfo>) : List<CategoryInfo> by member {

    /**
     * Transforms a list of CategoryInfo objects (implicitly available in the context) into a list
     * of Category objects.
     *
     * This function utilizes the `map` higher-order function to iterate over the collection of
     * `CategoryInfo` instances and apply the `intoCategory` transformation function to each element.
     * This results in a new list containing `Category` objects, each derived from the corresponding
     * `CategoryInfo` object.
     *
     * @return A new list containing `Category` objects, derived from the `CategoryInfo` objects
     * in the original collection.
     * @see CategoryInfo.intoCategory
     */
    fun intoCategoryList(): List<Category> {
        return map(CategoryInfo::intoCategory)
    }

    companion object {
        /**
         * Converts a list of internal `Category` objects to a `CategoryInfoList` containing
         * external `CategoryInfo` models.
         *
         * This function iterates through a list of `Category` objects, transforms each `Category`
         * into its external representation using the `asExternalModel()` method, and then wraps
         * the resulting list of external models into a `CategoryInfoList`.
         *
         * @param list The list of internal `Category` objects to be converted.
         * @return A `CategoryInfoList` containing the corresponding external `CategoryInfo` models.
         */
        fun from(list: List<Category>): CategoryInfoList {
            val member = list.map(Category::asExternalModel)
            return CategoryInfoList(member)
        }
    }
}

/**
 * Converts a [CategoryInfo] object into a [Category] object.
 *
 * This function takes a [CategoryInfo] instance and extracts its `id` and `name` properties
 * to construct a new [Category] object. This allows for a convenient transformation
 * between the data representation of a category (CategoryInfo) and the domain
 * representation (Category).
 *
 * @receiver The [CategoryInfo] object to convert.
 * @return A new [Category] object with the `id` and `name` from the [CategoryInfo].
 * @see CategoryInfo
 * @see Category
 */
private fun CategoryInfo.intoCategory(): Category {
    return Category(id, name)
}
