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

package com.example.jetsnack.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.FilterChip
import com.example.jetsnack.ui.home.FilterChipSection
import com.example.jetsnack.ui.home.FilterScreen
import com.example.jetsnack.ui.home.SortFilters
import com.example.jetsnack.ui.home.SortFiltersSection
import com.example.jetsnack.ui.home.SortOption

/**
 * This class holds the state of the various [FilterChip] that are at the top of the home screen in
 * a [FilterBar] (our [List] of [Filter] property [filters]), in a [FilterChipSection] (our [List]
 * of [Filter] property [priceFilters]), in a [SortOption] displayed in a [SortFilters] (our our
 * [List] of [Filter] property [sortFilters]), in a [FilterChipSection] (our [List] of [Filter]
 * property [categoryFilters]), and in a [FilterChipSection] (our [List] of [Filter] property
 * [lifeStyleFilters])
 *
 * @param name [String] describing the type of filtering that ths [FilterChip] will perform if it is
 * in a selected state.
 * @param enabled the initial state of the [MutableState] wrapped [Boolean] that is accessed via the
 * [Filter.enabled] property.
 * @param icon an [ImageVector] that will be displayed by the [SortFilters] in the [SortFiltersSection]
 * of the home screen (which is opened by clicking on the icon at the far left oa the [FilterScreen]
 */
@Stable
class Filter(
    val name: String,
    enabled: Boolean = false,
    val icon: ImageVector? = null
) {
    /**
     * This is used to hold and set the selected state of the [FilterChip] using this [Filter]
     */
    val enabled: MutableState<Boolean> = mutableStateOf(enabled)
}

/**
 * These [Filter] are used for the state of the various [FilterChip] that are at the top of the home
 * screen in a [FilterBar].
 */
val filters: List<Filter> = listOf(
    Filter(name = "Organic"),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory")
)

/**
 * These [Filter] are used for the state of the various [FilterChip] in the [FilterChipSection]
 * whose title is "Price".
 */
val priceFilters: List<Filter> = listOf(
    Filter(name = "$"),
    Filter(name = "$$"),
    Filter(name = "$$$"),
    Filter(name = "$$$$")
)

/**
 * These [Filter] are used for the state of the various [SortOption] in the [SortFilters]
 * Composable.
 */
val sortFilters: List<Filter> = listOf(
    Filter(name = "Android's favorite (default)", icon = Icons.Filled.Android),
    Filter(name = "Rating", icon = Icons.Filled.Star),
    Filter(name = "Alphabetical", icon = Icons.Filled.SortByAlpha)
)

/**
 * These [Filter] are used for the state of the various [FilterChip] in the [FilterChipSection]
 * whose title is "Category".
 */
val categoryFilters: List<Filter> = listOf(
    Filter(name = "Chips & crackers"),
    Filter(name = "Fruit snacks"),
    Filter(name = "Desserts"),
    Filter(name = "Nuts")
)

/**
 * These [Filter] are used for the state of the various [FilterChip] in the [FilterChipSection]
 * whose title is "LifeStyle".
 */
val lifeStyleFilters: List<Filter> = listOf(
    Filter(name = "Organic"),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory")
)

/**
 * This is used as the initial value of the [Filter.name] used by [SortFiltersSection].
 */
var sortDefault: String = sortFilters[0].name
