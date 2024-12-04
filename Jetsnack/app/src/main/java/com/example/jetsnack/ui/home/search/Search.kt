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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import kotlinx.coroutines.CoroutineScope

/**
 * Composed when the user navigates to [HomeSections.SEARCH.route]. Our root composable is a
 * [JetsnackSurface] whose `modifier` argument is a [Modifier.fillMaxSize] that has it occupy its
 * entire incoming size constraints. In its `content` Composable lambda argument we have a [Column]
 * in whose [ColumnScope] `content` Composable lambda argument we have:
 *  - a [Spacer] whose `modifier` argument is a [Modifier.statusBarsPadding] that adds padding to
 *  accommodate the status bars insets.
 *  - a [SearchBar] whose [TextFieldValue] `query` argument is the [SearchState.query] of our
 *  [SearchState] parameter [state], whose lambda `onQueryChange` argument is a lambda that sets the
 *  [SearchState.query] of [state] to the [TextFieldValue] it is called with, whose [Boolean]
 *  `searchFocused` argument is the [SearchState.focused] of [state], whose `onSearchFocusChange`
 *  lambda argument is a lambda that sets the [SearchState.focused] of [state] to the [Boolean] it
 *  it is passed, whose lambda `onClearQuery` argument is a lambda that sets the [SearchState.query]
 *  of [state] to a new instance of [TextFieldValue] whose `text` argument is the empty [String], and
 *  whose [Boolean] `searching` argument is the [SearchState.searching] of [state].
 *  - a [JetsnackDivider]
 *  - a [LaunchedEffect] whose `key1` is the [TextFieldValue.text] of the [SearchState.query] of
 *  [state], and in its [CoroutineScope] `block` we set the [SearchState.searching] of [state] to
 *  `true`, set the [SearchState.searchResults] of [state] to the [SearchRepo.search] for the `query`
 *  of the [TextFieldValue.text] of [SearchState.query] of [state], and set the [SearchState.searching]
 *  of [state] to `false` after the call to [SearchRepo.search] completes.
 *
 *  - When the [SearchState.searchDisplay] of [state] is:
 *  - [SearchDisplay.Categories] we call the [SearchCategories] Composable with the [SearchState.categories]
 *  of [state] as its [SearchCategories] `categories` argument.
 *  - [SearchDisplay.Suggestions] we call the [SearchSuggestions] Composable with its `suggestions`
 *  argument the [SearchState.suggestions] of [state], and its `onSuggestionSelect` lambda argument
 *  a lambda that sets the [SearchState.query] of [state] to the [String] passed the lambda.
 *  - [SearchDisplay.Results] we call the [SearchResults] Composable with its `searchResults` argument
 *  the [SearchState.searchResults] of [state] and its `onSnackClick` lambda argument our lambda
 *  parameter [onSnackClick].
 *  - [SearchDisplay.NoResults] we call the [NoResults] Composable with the [String] `query` argument
 *  passed to it the [TextFieldValue.text] of the [SearchState.query] of [state].
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

            LaunchedEffect(key1 = state.query.text) {
                state.searching = true
                state.searchResults = SearchRepo.search(query = state.query.text)
                state.searching = false
            }
            when (state.searchDisplay) {
                SearchDisplay.Categories -> SearchCategories(categories = state.categories)
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

/**
 * This enum is used to select which Composable is to be displayed in [Search]. The
 * [SearchState.searchDisplay] property uses a `when` in its `get` method to select
 * amongst the possible values based on the values of other properties to the [SearchState].
 */
enum class SearchDisplay {
    /**
     * This selects the [SearchCategories] Composable. It is the default when the user first opens
     * the [Search] screen before entering any text. The `get` of [SearchState.searchDisplay]
     * returns [SearchDisplay.Categories] when the [SearchState.categories] is `false` and the
     * [TextFieldValue.text] of the [SearchState.query] is empty.
     */
    Categories,

    /**
     * This selects the [SearchSuggestions] Composable. It is called when the user clicks in the
     * "Search Jetsnack" [SearchBar] but has not yet entered any text. The `get` of
     * [SearchState.searchDisplay] returns [SearchDisplay.Suggestions] when the [SearchState.focused]
     * is `true` and the [TextFieldValue.text] of the [SearchState.query] is empty.
     */
    Suggestions,

    /**
     * This selects the [SearchResults] Composable. It is called when the user has entered text and
     * some results are found. The `get` of the [SearchState.searchDisplay] returns this as its `else`
     * clause when the [SearchState.searchResults] is not empty, and the [TextFieldValue.text] of
     * the [SearchState.query] is not empty.
     */
    Results,

    /**
     * This selects the [NoResults] Composable. It is called when the user has entered text but no
     * results are found. The `get` of the [SearchState.searchDisplay] returns this when the
     * [SearchState.searchResults] is empty, but the [TextFieldValue.text] of the [SearchState.query]
     * is not empty, and the [SearchState.focused] is `true`
     */
    NoResults
}

/**
 * Creates and returns a [SearchState] that is remembered across compositions. We just call [remember]
 * for a new instance of [SearchState] constructed using the default values of our parameters because
 * our caller [Search] does not pass us any.
 *
 * @param query the initial value for [SearchState.query] is an empty [TextFieldValue].
 * @param focused the initial value for [SearchState.focused] is `false`.
 * @param searching the initial value for [SearchState.searching] is `false`.
 * @param categories the initial value for [SearchState.categories] is the [List] of
 * [SearchCategoryCollection] returned by the [SearchRepo.getCategories] method.
 * @param filters the initial value for [SearchState.filters] is the [List] of [Filter] returned
 * by the [SnackRepo.getFilters] method.
 * @param searchResults the initial value for [SearchState.searchResults] is an empty [List] of
 * [Snack].
 */
@Composable
private fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(text = ""),
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

/**
 * A state holder for the state of the [Search] Composable.
 *
 * @param query the [TextFieldValue] to use to initialize our [MutableState] wrapped [TextFieldValue]
 * property [SearchState.query].
 * @param focused the [Boolean] to use to initialize our [MutableState] wrapped [Boolean] property
 * [SearchState.focused].
 * @param searching the [Boolean] to use to initialize our [MutableState] wrapped [Boolean] property
 * [SearchState.searching].
 * @param categories the [List] of [SearchCategoryCollection] to use to initialize our [MutableState]
 * wrapped [List] of [SearchCategoryCollection] property [SearchState.categories].
 * @param suggestions the [List] of [SearchSuggestionGroup] to use to initialize our [MutableState]
 * wrapped [List] of [SearchSuggestionGroup] property [SearchState.suggestions].
 * @param filters the [List] of [Filter] to use to initialize our [MutableState] wrapped [List] of
 * [Filter] property [SearchState.filters].
 * @param searchResults the [List] of [Snack] to use to initialize our [MutableState] wrapped [List]
 * of [Snack] property [SearchState.searchResults].
 */
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
    /**
     * This is the current [TextFieldValue] that the user has entered in the [SearchBar].
     */
    var query: TextFieldValue by mutableStateOf(value = query)

    /**
     * `true` if the [SearchBar] is currently focused.
     */
    var focused: Boolean by mutableStateOf(value = focused)

    /**
     * `true` while the [SearchRepo.search] method is being executed by the [LaunchedEffect] of [Search]
     */
    var searching: Boolean by mutableStateOf(value = searching)

    /**
     * The [List] of [SearchCategoryCollection] that should be displayed in the [SearchCategories]
     * Composable.
     */
    var categories: List<SearchCategoryCollection> by mutableStateOf(value = categories)

    /**
     * The [List] of [SearchSuggestionGroup] that should be displayed in the [SearchSuggestions]
     * Composable.
     */
    var suggestions: List<SearchSuggestionGroup> by mutableStateOf(value = suggestions)

    /**
     * The [List] of [Filter] returned by the [SnackRepo.getFilters] method (does not appear to be used)
     */
    var filters: List<Filter> by mutableStateOf(value = filters)

    /**
     * The [List] of [Snack] that should be displayed in the [SearchResults] Composable. It is set by
     * a call to the [SearchRepo.search] method in the [LaunchedEffect] of [Search].
     */
    var searchResults: List<Snack> by mutableStateOf(value = searchResults)

    /**
     * The [SearchDisplay] that should be displayed in the [Search] Composable.
     */
    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.Categories
            focused && query.text.isEmpty() -> SearchDisplay.Suggestions
            searchResults.isEmpty() -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }
}

/**
 * Displayed in the [Search] Composable. Our root Composable is a [JetsnackSurface] whose [Color]
 * `color` argument is the [JetsnackColors.uiFloated] of our custom [JetsnackTheme.colors], whose
 * [Color] `contentColor` argument the [JetsnackColors.textSecondary] of our custom
 * [JetsnackTheme.colors], whose [Shape] `shape` argument is the [Shapes.small] of our custom
 * [MaterialTheme.shapes], and whose [Modifier] `modifier` argument chains a [Modifier.fillMaxWidth]
 * to our [Modifier] parameter [modifier] to have it occupy its entire incoming width constaint, with
 * a [Modifier.height] chained to that which sets its `height` to 56.dp, followed by a chain to a
 * [Modifier.padding] that adds 24.dp padding to each `horizontal` side and 8.dp to each `vertical`
 * side. In its `content` Composable lambda argument we have a [Box] whose `modifier` argument is
 * a [Modifier.fillMaxSize] to have it occupy its entire incoming size constraints, and in its
 * [BoxScope] `content` Composable lambda argument if the [TextFieldValue.text]  of our
 * [TextFieldValue] parameter [query] is empty we compose a [SearchHint] Composable to provide the
 * user with a hint in any case we Compose a [Row] whose `verticalAlignment` argument is set to
 * [Alignment.CenterVertically] to center its children vertically, and whose `modifier` argument
 * is a [Modifier.fillMaxSize] to have it occupy its entire incoming size constraints, with a
 * [Modifier.wrapContentHeight] to have it occupy its entire incoming height constraints. In its
 * [RowScope] `content` Composable lambda argument we have:
 *  - if our [Boolean] parameter [searchFocused] is `true` we compose an [IconButton] whose `onClick`
 *  argument our lambda parameter [onClearQuery], and whose `content` Composable lambda argument is
 *  an [Icon] whose [ImageVector] `imageVector` argument is the [Icons.AutoMirrored.Outlined.ArrowBack]
 *  (an arrow pointing to the left), whose [Color] `tint` argument is the [JetsnackColors.iconPrimary]
 *  of our custom [JetsnackTheme.colors], and whose `contentDescription` argument is the string
 *  whose resource ID is `R.string.label_back` ("Back").
 *  - a [BasicTextField] whose [TextFieldValue] `value` argument is our [TextFieldValue] parameter
 *  [query], whose `onValueChange` lambda argument is our lambda parameter [onQueryChange], and whose
 *  [Modifier] `modifier` argument is a [RowScope.weight] which sets its `weight` to `1f` to have it
 *  take up all horizontal space after its unweighted siblings have been measured and placed, and
 *  whose `onFocusChanged` lambda argument is a lambda that calls our lambda parameter [onSearchFocusChange]
 *  with the [FocusState.isFocused] property of the [FocusState] parameter the lambda.
 *  - if our [Boolean] parameter [searching] is `true` we compose a [CircularProgressIndicator]
 *  whose [Color] `color` argument is the [JetsnackColors.iconPrimary] of our custom [JetsnackTheme.colors]
 *  and whose [Modifier] `modifier` argument is a [Modifier.padding] that adds 6.dp to each `horizontal`
 *  side with a [Modifier.size] that sets its `size` to 36.dp. If our [Boolean] parameter [searching]
 *  is `false` we compose a [Spacer] whose `modifier` argument is a [Modifier.width] that sets its
 *  `width` to [IconSize] to balance the arrow icon.
 *
 * @param query the current [TextFieldValue] that the user has entered. Our caller [Search] passes
 * us the value of the [SearchState.query] of the latest [SearchState].
 * @param onQueryChange a lambda that we should call with the new [TextFieldValue] when the user
 * modifies it. Our caller [Search] passes us a lambda that sets the [SearchState.query] of the
 * latest [SearchState] to the [TextFieldValue] we pass the lambda.
 * @param searchFocused `true` if our [BasicTextField] has the focus. Our caller [Search] passes
 * us the [SearchState.focused] property of the current [SearchState].
 * @param onSearchFocusChange a lambda to be called when the focus state of our [BasicTextField]
 * changes. Our caller [Search] passes us a lambda that sets the [SearchState.focused] property
 * of the current [SearchState] to the [Boolean] we pass the lambda.
 * @param onClearQuery a lambda to be called when the user indicates he wishes to clear the current
 * query. Our caller [Search] passes us a lambda that sets the [SearchState.query] property of the
 * current [SearchState] to a [TextFieldValue] whose `text` is the empty [String].
 * @param searching `true` while the [SearchRepo.search] method is being executed by the
 * [LaunchedEffect] of [Search]. Our caller [Search] passes us the [SearchState.searching]
 * property of the latest [SearchState].
 * @param modifier a [Modifier] instance that our caller can use to modidfy our appearance and/or
 * behavior. Our caller [Search] does not pass us one so the empty, default, or starter [Modifier]
 * that contains no elements is used
 */
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
            .height(height = 56.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                        .weight(weight = 1f)
                        .onFocusChanged {
                            onSearchFocusChange(it.isFocused)
                        }
                )
                if (searching) {
                    CircularProgressIndicator(
                        color = JetsnackTheme.colors.iconPrimary,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(size = 36.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(width = IconSize)) // balance arrow icon
                }
            }
        }
    }
}

/**
 * The default size of an [Icon], used as the width of the [Spacer] at the end of the [SearchBar]
 * when the [IconButton] is not being composed.
 */
private val IconSize = 48.dp

/**
 * Displayed as a hint in the [SearchBar] when the [TextFieldValue.text] of the [TextFieldValue] of
 * the [BasicTextField] is empty. Our root Composable is a [Row] whose `verticalAlignment` argument
 * is an [Alignment.CenterVertically] to center its children vertically, and whose `modifier`
 * argument is a [Modifier.fillMaxSize] to have it occupy its entire incoming size constraints, with
 * a [Modifier.wrapContentSize] chained to that to allow it to measure at its desired size without
 * regard for the incoming measurement minimum width or minimum height constraints. In its [RowScope]
 * `content` Composable lambda argument we have:
 *  - an [Icon] whose [ImageVector] `imageVector` argument is thean [Icon] whose [ImageVector]
 *  `imageVector` argument is the [ImageVector] drawn by [Icons.Outlined.Search] (a magnifying glass),
 *  whose [Color] `tint` argument is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors],
 *  and whose `contentDescription` argument is the string whose resource ID is `R.string.label_search`
 *  ("Perform Search").
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [Modifier.width] that sets its `width` to
 *  `8.dp`.
 *  - a [Text] whose [String] `text` argument is the string whose resource ID is `R.string.search_jetsnack`
 *  ("Search Jetsnack"), and whose [Color] `color` argument is the [JetsnackColors.textHelp] of our
 *  custom [JetsnackTheme.colors].
 */
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
        Spacer(modifier = Modifier.width(width = 8.dp))
        Text(
            text = stringResource(R.string.search_jetsnack),
            color = JetsnackTheme.colors.textHelp
        )
    }
}

/**
 * Three Previews of our [SearchBar] using different device settings.
 */
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
