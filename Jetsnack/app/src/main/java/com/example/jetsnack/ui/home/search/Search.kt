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

package com.example.jetsnack.ui.home.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.SearchCategoryCollection
import com.example.jetsnack.model.SearchRepo
import com.example.jetsnack.model.SearchSuggestionGroup
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.navigation.JetsnackNavController
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * Composed when the user navigates to [HomeSections.SEARCH.route]. Our root composable is a
 * [JetsnackSurface] whose `modifier` argument is a [Modifier.fillMaxSize] that has it occupy its
 * entire incoming size constraints. In its `content` Composable lambda argument we have a [Column]
 * in whose [ColumnScope] `content` Composable lambda argument we have:
 *  - a [Spacer] whose `modifier` argument is a [Modifier.statusBarsPadding] that adds padding to
 *  accommodate the status bars insets.
 *  - a [SearchBar] whose `query` argument is the [SearchState.query] of our [SearchState] parameter
 *  [state], whose `onQueryChange` argument is a lambda that sets the [SearchState.query] of [state]
 *  to the [TextFieldValue] it is called with, whose `searchFocused` argument is the [SearchState.focused]
 *  of [state], whose `onSearchFocusChange` argument is a lambda that sets the [SearchState.focused]
 *  to the [Boolean] it is passed, whose `onClearQuery` argument is a lambda that sets the
 *  [SearchState.query] of [state] to a new instance of [TextFieldValue] whose `text` argument is the
 *  empty [String], and whose `searching` argument is the [SearchState.searching] of [state].
 *
 * @param onSnackClick a lambda that should be called when Camposable displaying a [Snack] is clicked
 * with the [Snack.id] of the [Snack] and a [String]. It traces back to a call to `navigateToSnackDetail`
 * in the class [JetsnackNavController].
 * @param modifier a [Modifier] instance that our caller can use to modidfy our appearance and/or
 * behavior. Our caller passes its [Modifier] parameter which traces back to a [Modifier.padding]
 * that adds the [PaddingValues] that are passed to content of the [JetsnackScaffold] it is in with
 * a [consumeWindowInsets] chained to that called with those same [PaddingValues].
 * @param state the [SearchState] to use to communicate between the different composables we contain.
 */
@Composable
fun Search(
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    state: SearchState = rememberSearchState()
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Column {
            Spacer(modifier = Modifier.statusBarsPadding())
            SearchBar(
                query = state.query,
                onQueryChange = { state.query = it },
                searchFocused = state.focused,
                onSearchFocusChange = { state.focused = it },
                onClearQuery = { state.query = TextFieldValue(text = "") },
                searching = state.searching
            )
            JetsnackDivider()

            LaunchedEffect(state.query.text) {
                state.searching = true
                state.searchResults = SearchRepo.search(query = state.query.text)
                state.searching = false
            }
            when (state.searchDisplay) {
                SearchDisplay.Categories -> SearchCategories(state.categories)
                SearchDisplay.Suggestions -> SearchSuggestions(
                    suggestions = state.suggestions,
                    onSuggestionSelect = { suggestion: String ->
                        state.query = TextFieldValue(suggestion)
                    }
                )

                SearchDisplay.Results -> SearchResults(
                    searchResults = state.searchResults,
                    onSnackClick = onSnackClick
                )

                SearchDisplay.NoResults -> NoResults(query = state.query.text)
            }
        }
    }
}

enum class SearchDisplay {
    Categories, Suggestions, Results, NoResults
}

@Composable
private fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    categories: List<SearchCategoryCollection> = SearchRepo.getCategories(),
    suggestions: List<SearchSuggestionGroup> = SearchRepo.getSuggestions(),
    filters: List<Filter> = SnackRepo.getFilters(),
    searchResults: List<Snack> = emptyList()
): SearchState {
    return remember {
        SearchState(
            query = query,
            focused = focused,
            searching = searching,
            categories = categories,
            suggestions = suggestions,
            filters = filters,
            searchResults = searchResults
        )
    }
}

@Stable
class SearchState(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    categories: List<SearchCategoryCollection>,
    suggestions: List<SearchSuggestionGroup>,
    filters: List<Filter>,
    searchResults: List<Snack>
) {
    var query: TextFieldValue by mutableStateOf(query)
    var focused: Boolean by mutableStateOf(focused)
    var searching: Boolean by mutableStateOf(searching)
    var categories: List<SearchCategoryCollection> by mutableStateOf(categories)
    var suggestions: List<SearchSuggestionGroup> by mutableStateOf(suggestions)
    var filters: List<Filter> by mutableStateOf(filters)
    var searchResults: List<Snack> by mutableStateOf(searchResults)
    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.Categories
            focused && query.text.isEmpty() -> SearchDisplay.Suggestions
            searchResults.isEmpty() -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }
}

@Composable
private fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    searchFocused: Boolean,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(
        color = JetsnackTheme.colors.uiFloated,
        contentColor = JetsnackTheme.colors.textSecondary,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (query.text.isEmpty()) {
                SearchHint()
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
            ) {
                if (searchFocused) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            tint = JetsnackTheme.colors.iconPrimary,
                            contentDescription = stringResource(R.string.label_back)
                        )
                    }
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            onSearchFocusChange(it.isFocused)
                        }
                )
                if (searching) {
                    CircularProgressIndicator(
                        color = JetsnackTheme.colors.iconPrimary,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(36.dp)
                    )
                } else {
                    Spacer(Modifier.width(IconSize)) // balance arrow icon
                }
            }
        }
    }
}

private val IconSize = 48.dp

@Composable
private fun SearchHint() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            tint = JetsnackTheme.colors.textHelp,
            contentDescription = stringResource(R.string.label_search)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.search_jetsnack),
            color = JetsnackTheme.colors.textHelp
        )
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchBarPreview() {
    JetsnackTheme {
        JetsnackSurface {
            SearchBar(
                query = TextFieldValue(""),
                onQueryChange = { },
                searchFocused = false,
                onSearchFocusChange = { },
                onClearQuery = { },
                searching = false
            )
        }
    }
}
