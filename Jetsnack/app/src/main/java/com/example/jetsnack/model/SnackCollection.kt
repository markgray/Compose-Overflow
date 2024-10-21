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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import com.example.jetsnack.ui.home.cart.CartViewModel
import kotlinx.coroutines.flow.StateFlow
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

    /**
     * Returns the [Snack] in our [List] of [Snack] field field [snacks] whose [Snack.id] is equal
     * to our [Long] parameter [snackId].
     *
     * @param snackId the [Snack.id] of the [Snack] that is wanted.
     * @return the [Snack] in our [List] of [Snack] field field [snacks] whose [Snack.id] is equal
     * to our [Long] parameter [snackId].
     */
    fun getSnack(snackId: Long): Snack = snacks.find { it.id == snackId }!!

    /**
     * Returns our [List] of [SnackCollection] field [related].
     */
    fun getRelated(@Suppress("UNUSED_PARAMETER") snackId: Long): List<SnackCollection> = related

    /**
     * Returns our [SnackCollection] field [inspiredByCart].
     */
    fun getInspiredByCart(): SnackCollection = inspiredByCart

    /**
     * Returns our [List] of [Filter] field [filters]
     */
    fun getFilters(): List<Filter> = filters

    /**
     * Returns our [List] of [Filter] field [priceFilters]
     */
    fun getPriceFilters(): List<Filter> = priceFilters

    /**
     * Returns our [List] of [OrderLine] field [cart]
     */
    fun getCart(): List<OrderLine> = cart

    /**
     * Returns our [List] of [Filter] field [sortFilters]
     */
    fun getSortFilters(): List<Filter> = sortFilters

    /**
     * Returns our [List] of [Filter] field [categoryFilters]
     */
    fun getCategoryFilters(): List<Filter> = categoryFilters

    /**
     * Returns our [String] field [sortDefault]
     */
    fun getSortDefault(): String = sortDefault

    /**
     * Returns our [List] of [Filter] field [lifeStyleFilters]
     */
    fun getLifeStyleFilters(): List<Filter> = lifeStyleFilters
}

/**
 * Static data
 */

/**
 * Our [SnackCollection] field [tastyTreats] named "Android's picks" consists of the [List.subList]
 * or the [Snack]s between index 0 and index 13 in our [List] of [Snack] field [snacks].
 */
private val tastyTreats = SnackCollection(
    id = 1L,
    name = "Android's picks",
    type = CollectionType.Highlight,
    snacks = snacks.subList(0, 13)
)

/**
 * Our [SnackCollection] field [popular] named "Popular on Jetsnack" consists of the [List.subList]
 * or the [Snack]s between index 14 and index 19 in our [List] of [Snack] field [snacks].
 */
private val popular = SnackCollection(
    id = Random.nextLong(),
    name = "Popular on Jetsnack",
    snacks = snacks.subList(14, 19)
)

/**
 * Our [SnackCollection] field [wfhFavs] named "WFH favourites" is a copy of our [SnackCollection]
 * field [tastyTreats]
 */
private val wfhFavs = tastyTreats.copy(
    id = Random.nextLong(),
    name = "WFH favourites"
)

/**
 * Our [SnackCollection] field [newlyAdded] named "Newly Added" is a copy of our [SnackCollection]
 * field [popular]
 */
private val newlyAdded = popular.copy(
    id = Random.nextLong(),
    name = "Newly Added"
)

/**
 * Our [SnackCollection] field [exclusive] named "Only on Jetsnack" is a copy of our [SnackCollection]
 * field [tastyTreats]
 */
private val exclusive = tastyTreats.copy(
    id = Random.nextLong(),
    name = "Only on Jetsnack"
)

/**
 * Our [SnackCollection] field [also] named "Customers also bought" is a copy of our [SnackCollection]
 * field [tastyTreats]
 */
private val also = tastyTreats.copy(
    id = Random.nextLong(),
    name = "Customers also bought"
)

/**
 * Our [SnackCollection] field [inspiredByCart] named "Inspired by your cart" is a copy of our
 * [SnackCollection] field [tastyTreats]
 */
private val inspiredByCart = tastyTreats.copy(
    id = Random.nextLong(),
    name = "Inspired by your cart"
)

/**
 * This is displayed in a `SnackCollectionList` Composable with each [SnackCollection] displayed
 * using a [com.example.jetsnack.ui.components.SnackCollection] Composable in a [LazyColumn].
 */
private val snackCollections: List<SnackCollection> = listOf(
    tastyTreats,
    popular,
    wfhFavs,
    newlyAdded,
    exclusive
)

/**
 * This [List] of [SnackCollection] is displayed in a `com.example.jetsnack.ui.snackdetail.Body` that
 * is part of the [com.example.jetsnack.ui.snackdetail.SnackDetail] Composable that is displayed when
 * the user clicks on one of the [Snack]s.
 * TODO: The navigation performed is rather difficult to grok, so needs much further study.
 */
private val related: List<SnackCollection> = listOf(
    also.copy(id = Random.nextLong()),
    popular.copy(id = Random.nextLong())
)

/**
 * This is read by the [StateFlow] wrapped [List] of [OrderLine] property [CartViewModel.orderLines]
 * which is collected by the [com.example.jetsnack.ui.home.cart.Cart] Composable to feed data for the
 * `CartContent` Composable to display. (This is displayed when the user is viewing the "MY CART"
 * Screen which is displayed when the user clicks the "shopping cart" icon on the "HOME" screen).
 */
private val cart: List<OrderLine> = listOf(
    OrderLine(snacks[4], 2),
    OrderLine(snacks[6], 3),
    OrderLine(snacks[8], 1)
)

/**
 * This used to hold an order that is in our [List] of [OrderLine] field [cart]
 *
 * @param snack the [Snack] that has been ordered.
 * @param count the number of [Snack] in [snack] that has been ordered.
 */
@Immutable
data class OrderLine(
    val snack: Snack,
    val count: Int
)
