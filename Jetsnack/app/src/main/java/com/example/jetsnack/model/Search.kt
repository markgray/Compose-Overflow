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
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import com.example.jetsnack.R
import com.example.jetsnack.ui.components.VerticalGrid
import com.example.jetsnack.ui.home.JetsnackBottomNavigationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * A fake repo for searching. Used by [com.example.jetsnack.ui.home.search.Search].
 */
object SearchRepo {
    /**
     * Returns our [List] of [SearchCategoryCollection] property [searchCategoryCollections].
     */
    fun getCategories(): List<SearchCategoryCollection> = searchCategoryCollections

    /**
     * Returns our [List] of [SearchSuggestionGroup] property [searchSuggestions].
     */
    fun getSuggestions(): List<SearchSuggestionGroup> = searchSuggestions

    /**
     * Peforms a search of our [List] of [Snack] field [snacks] for entries whose [Snack.name]
     * contains its [String] parameter [query], and returns the result as a [List] of [Snack].
     * We call [withContext] with its coroutine `context` argument [Dispatchers.Default] which
     * exectutes its `block` lambda argument suspending until it completes. In that lambda we
     * [delay] for 200ms, then call the [List.filter] method of our [List] of [Snack] parameter
     * [snacks] to build a [List] of [Snack] whose [Snack.name] constains our [String] parameter
     * [query] and then [withContext] resume and returns this result.
     *
     * @param query The [String] to search [List] of [Snack] property [snacks] for.
     * @return a [List] of [Snack] whose [Snack.name] contains [String] parameter [query].
     */
    suspend fun search(query: String): List<Snack> = withContext(context = Dispatchers.Default) {
        delay(timeMillis = 200L) // simulate an I/O delay
        snacks.filter { it.name.contains(other = query, ignoreCase = true) }
    }
}

/**
 * Used to hold a [List] of [SearchCategory] which are (both) displayed when the "Magnifying Glass"
 * [JetsnackBottomNavigationItem] at the bottom of the "Home" screen is clicked.
 *
 * @param id a unique number identifying this [SearchCategoryCollection] (unused)
 * @param name a name which is displayed in a [Text] "Title" above the [VerticalGrid] that displays
 * all of the [SearchCategory] displayed in its [VerticalGrid] of
 * [com.example.jetsnack.ui.home.search.SearchCategory] Composables.
 * @param categories the [List] of [SearchCategory] this [SearchCategoryCollection] holds.
 */
@Immutable
data class SearchCategoryCollection(
    val id: Long,
    val name: String,
    val categories: List<SearchCategory>
)

/**
 * Displayed in a [com.example.jetsnack.ui.home.search.SearchCategory] Composable when the
 * [SearchCategoryCollection] containing it in its [SearchCategoryCollection.categories] is
 * displayed after the "Magnifying Glass" [JetsnackBottomNavigationItem] at the bottom of the
 * "Home" screen is clicked.
 *
 * @param name the name of this [SearchCategory] it is displayed in a [Text] at the left side of
 * a [com.example.jetsnack.ui.home.search.SearchCategory] Composable.
 * @param imageRes a resource ID of a jpeg to display in a [com.example.jetsnack.ui.components.SnackImage]
 * that is a the right side of a [com.example.jetsnack.ui.home.search.SearchCategory] Composable..
 */
@Immutable
data class SearchCategory(
    val name: String,
    val imageRes: Int
)

/**
 * Used to hold a grouping of a [List] of [String] that is displayed when the search box at the top
 * of the "Search" screen is clicked. There are two lists used by the app "Recent Searches" and
 * "Popular Searches". These are displayed in a [com.example.jetsnack.ui.home.search.SearchSuggestions]
 * Composable.
 *
 * @param id an unique ID number (unused)
 * @param name a [String] naming this group, in our case "Recent searches", or "Popular searches".
 * It is displayed using a `SuggestionHeader` (a stylized [Text]) at the top of the
 * [com.example.jetsnack.ui.home.search.SearchSuggestions] Composable.
 * @param suggestions a [List] of different suggested words to search for each displayed in a
 * `Suggestion` (a stylized [Text]) at the bottom of the
 * [com.example.jetsnack.ui.home.search.SearchSuggestions] Composable.
 */
@Immutable
data class SearchSuggestionGroup(
    val id: Long,
    val name: String,
    val suggestions: List<String>
)

/*
 * Static data
 */

/**
 * Our [List] of two [SearchCategoryCollection], "Categories", and "Lifestyles". These are displayed
 * in a [com.example.jetsnack.ui.home.search.SearchCategories] Composable. (Which is a [LazyColumn]
 * holding a `SearchCategoryCollection` for each of them).
 */
private val searchCategoryCollections = listOf(
    SearchCategoryCollection(
        id = 0L,
        name = "Categories",
        categories = listOf(
            SearchCategory(
                name = "Chips & crackers",
                imageRes = R.drawable.chips
            ),
            SearchCategory(
                name = "Fruit snacks",
                imageRes = R.drawable.fruit,
            ),
            SearchCategory(
                name = "Desserts",
                imageRes = R.drawable.desserts
            ),
            SearchCategory(
                name = "Nuts",
                imageRes = R.drawable.nuts,
            )
        )
    ),
    SearchCategoryCollection(
        id = 1L,
        name = "Lifestyles",
        categories = listOf(
            SearchCategory(
                name = "Organic",
                imageRes = R.drawable.organic
            ),
            SearchCategory(
                name = "Gluten Free",
                imageRes = R.drawable.gluten_free
            ),
            SearchCategory(
                name = "Paleo",
                imageRes = R.drawable.paleo,
            ),
            SearchCategory(
                name = "Vegan",
                imageRes = R.drawable.vegan,
            ),
            SearchCategory(
                name = "Vegetarian",
                imageRes = R.drawable.organic,
            ),
            SearchCategory(
                name = "Whole30",
                imageRes = R.drawable.paleo
            )
        )
    )
)

/**
 * This is our [List] of two [SearchSuggestionGroup] "Recent searches" and "Popular searches". They
 * are displayed in a [com.example.jetsnack.ui.home.search.SearchSuggestions] Composable.
 */
private val searchSuggestions = listOf(
    SearchSuggestionGroup(
        id = 0L,
        name = "Recent searches",
        suggestions = listOf(
            "Cheese",
            "Apple Sauce"
        )
    ),
    SearchSuggestionGroup(
        id = 1L,
        name = "Popular searches",
        suggestions = listOf(
            "Organic",
            "Gluten Free",
            "Paleo",
            "Vegan",
            "Vegitarian",
            "Whole30"
        )
    )
)
