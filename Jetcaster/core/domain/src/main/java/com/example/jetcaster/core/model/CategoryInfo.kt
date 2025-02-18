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

import com.example.jetcaster.core.data.database.model.Category

/**
 * Represents information about a [Category].
 *
 * This data class holds the essential details of a [Category], including its unique identifier and name.
 *
 * @property id The unique identifier of the category.
 * @property name The name of the category. This is a descriptive string that identifies the category.
 */
data class CategoryInfo(
    val id: Long,
    val name: String
)

/**
 * Represents the category "Technology", which is used as the default category in the application.
 * This constant is used to identify content, items, or entities that belong to the technology domain.
 * Examples of content that might be categorized under "Technology" include:
 * - Articles about software development
 * - News related to the latest gadgets
 * - Tutorials on programming languages
 * - Reviews of tech products
 * - Information on artificial intelligence
 */
const val CategoryTechnology: String = "Technology"

/**
 * Converts a [Category] entity to a [CategoryInfo] external model.
 *
 * This function takes a [Category] object and extracts its essential properties (id and name)
 * to create a corresponding [CategoryInfo] object, which is suitable for exposing
 * to external layers or APIs.
 *
 * @return A [CategoryInfo] object representing the external model of the [Category].
 * @receiver The [Category] object to be converted.
 */
fun Category.asExternalModel(): CategoryInfo =
    CategoryInfo(
        id = id,
        name = name
    )
