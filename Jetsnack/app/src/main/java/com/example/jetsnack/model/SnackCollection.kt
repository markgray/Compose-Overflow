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

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import kotlin.random.Random

/**
 * This class is used to hold a [List] of [Snack] whose entries are grouped together for some reason
 * or other.
 *
 * @param id a unique ID number.
 * @param name a [String] naming this [SnackCollection]. It is displayed in a [Text] rendered by the
 * [com.example.jetsnack.ui.components.SnackCollection] Composable as the title above the display
 * of its [List] of [Snack] property [snacks] which are displayed in a [LazyRow] below it.
 * @param snacks the [List] of [Snack] that this [SnackCollection] holds. They are each displayed
 * in a `HighlightSnackItem` inside a [LazyRow] by the `HighlightedSnacks` Composable if their
 * [type] is [CollectionType.Highlight] or in a `Snacks` if they are [CollectionType.Normal] (none
 * of them are).
 * @param type the [CollectionType] of this [SnackCollection], either [CollectionType.Normal] or
 * [CollectionType.Highlight]
 */
@Immutable
data class SnackCollection(
    val id: Long,
    val name: String,
    val snacks: List<Snack>,
    val type: CollectionType = CollectionType.Normal
)

/**
 * The type of this [SnackCollection].
 */
enum class CollectionType {
    /**
     * When a [SnackCollection] is of this [CollectionType] the [Snack]s in its [List] of [Snack]
     * property [snacks] should be displayed in a `Snacks` Composable.
     */
    Normal,

    /**
     * When a [SnackCollection] is of this [CollectionType] the [Snack]s in its [List] of [Snack]
     * property [snacks] should be displayed in a `HighlightSnackItem` Composable.
     */
    Highlight
}

/**
 * A fake repo
 */
object SnackRepo {
    /**
     * Returns our [List] of [SnackCollection] field [snackCollections].
     */
    fun getSnacks(): List<SnackCollection> = snackCollections
    fun getSnack(snackId: Long) = snacks.find { it.id == snackId }!!
    fun getRelated(@Suppress("UNUSED_PARAMETER") snackId: Long) = related
    fun getInspiredByCart() = inspiredByCart
    fun getFilters() = filters
    fun getPriceFilters() = priceFilters
    fun getCart() = cart
    fun getSortFilters() = sortFilters
    fun getCategoryFilters() = categoryFilters
    fun getSortDefault() = sortDefault
    fun getLifeStyleFilters() = lifeStyleFilters
}

/**
 * Static data
 */

private val tastyTreats = SnackCollection(
    id = 1L,
    name = "Android's picks",
    type = CollectionType.Highlight,
    snacks = snacks.subList(0, 13)
)

private val popular = SnackCollection(
    id = Random.nextLong(),
    name = "Popular on Jetsnack",
    snacks = snacks.subList(14, 19)
)

private val wfhFavs = tastyTreats.copy(
    id = Random.nextLong(),
    name = "WFH favourites"
)

private val newlyAdded = popular.copy(
    id = Random.nextLong(),
    name = "Newly Added"
)

private val exclusive = tastyTreats.copy(
    id = Random.nextLong(),
    name = "Only on Jetsnack"
)

private val also = tastyTreats.copy(
    id = Random.nextLong(),
    name = "Customers also bought"
)

private val inspiredByCart = tastyTreats.copy(
    id = Random.nextLong(),
    name = "Inspired by your cart"
)

private val snackCollections = listOf(
    tastyTreats,
    popular,
    wfhFavs,
    newlyAdded,
    exclusive
)

private val related = listOf(
    also.copy(id = Random.nextLong()),
    popular.copy(id = Random.nextLong())
)

private val cart = listOf(
    OrderLine(snacks[4], 2),
    OrderLine(snacks[6], 3),
    OrderLine(snacks[8], 1)
)

@Immutable
data class OrderLine(
    val snack: Snack,
    val count: Int
)
