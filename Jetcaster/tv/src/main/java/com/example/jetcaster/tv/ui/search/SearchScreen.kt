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

package com.example.jetcaster.tv.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ColorScheme
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.Typography
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.CategorySelection
import com.example.jetcaster.tv.model.CategorySelectionList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.PodcastCard
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * The main composable for the Search Screen.
 *
 * This screen allows users to search for podcasts based on keywords and categories.
 * It displays different UI states based on the current state of the search:
 *  - **Loading:** Shows a loading indicator while the initial data or search results are
 *  being fetched.
 *  - **Ready:** Shows the initial search interface with an input field for keywords and
 *  a list of selectable categories.
 *  - **HasResult:** Shows the search results, including the keyword, selected categories,
 *  and a list of matching podcasts.
 *
 * We start by initializing our [State] wrapped [SearchScreenUiState] variable `uiState` to the value
 * returned by the [StateFlow.collectAsState] method of the [SearchScreenViewModel.uiStateFlow]
 * property of our [SearchScreenViewModel] parameter [searchScreenViewModel]. Then in a `when`
 * statement we copy `uiState` to variable `s` and switch based on its type:
 *  - **SearchScreenUiState.Loading:** If the type is [SearchScreenUiState.Loading], we display
 *  a [Loading] composable with its `modifier` argument our [Modifier] parameter [modifier].
 *  - **SearchScreenUiState.Ready:** If the type is [SearchScreenUiState.Ready], we display
 *  a [Ready] composable with its `keyword` argument the value of the [SearchScreenUiState.Ready.keyword]
 *  property of `s`, `categorySelectionList` argument the value of the
 *  [SearchScreenUiState.Ready.categorySelectionList] property of `s`, `onKeywordInput` argument the
 *  [SearchScreenViewModel.setKeyword] method of our [SearchScreenViewModel] parameter
 *  [searchScreenViewModel], `onCategorySelected` argument the
 *  [SearchScreenViewModel.addCategoryToSelectedCategoryList] method of our [SearchScreenViewModel]
 *  parameter [searchScreenViewModel], `onCategoryUnselected` argument the
 *  [SearchScreenViewModel.removeCategoryFromSelectedCategoryList] method of our
 *  [SearchScreenViewModel] parameter [searchScreenViewModel], and `modifier` argument our
 *  [Modifier] parameter [modifier].
 *  - **SearchScreenUiState.HasResult:** If the type is [SearchScreenUiState.HasResult], we display
 *  a [HasResult] composable with its `keyword` argument the value of the
 *  [SearchScreenUiState.HasResult.keyword] property of `s`, `categorySelectionList` argument the
 *  value of the [SearchScreenUiState.HasResult.categorySelectionList] property of `s`,
 *  `podcastList` argument the value of the [SearchScreenUiState.HasResult.result] property of
 *  `s`, `onKeywordInput` argument the [SearchScreenViewModel.setKeyword] method of our
 *  [SearchScreenViewModel] parameter [searchScreenViewModel], `onCategorySelected` argument the
 *  [SearchScreenViewModel.addCategoryToSelectedCategoryList] method of our [SearchScreenViewModel]
 *  parameter [searchScreenViewModel], `onCategoryUnselected` argument the
 *  [SearchScreenViewModel.removeCategoryFromSelectedCategoryList] method of our [SearchScreenViewModel]
 *  parameter [searchScreenViewModel], `onPodcastSelected` argument our [onPodcastSelected] parameter,
 *  and `modifier` argument our [Modifier] parameter [modifier].
 *
 * @param onPodcastSelected A callback function that is invoked when a podcast is selected from the
 * results. It receives the [PodcastInfo] object of the selected podcast.
 * @param modifier [Modifier] for styling and layout customization. Our caller the `Route` method of
 * `JetcasterApp` passes us a [Modifier.fillMaxSize] with a [Modifier.padding] chained to it
 * that adds the [PaddingValues] constant `JetcasterAppDefaults.overScanMargin.default`
 * (top = 40.dp, bottom = 40.dp, start = 80.dp, end = 80.dp) to our padding.
 * @param searchScreenViewModel The view model responsible for managing the state and logic of the
 * search screen. It's provided using Hilt for dependency injection. Defaults to a
 * [SearchScreenViewModel] instance injected by Hilt.
 */
@Composable
fun SearchScreen(
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
) {
    val uiState: SearchScreenUiState by searchScreenViewModel.uiStateFlow.collectAsState()

    when (val s: SearchScreenUiState = uiState) {
        SearchScreenUiState.Loading -> Loading(modifier = modifier)
        is SearchScreenUiState.Ready -> Ready(
            keyword = s.keyword,
            categorySelectionList = s.categorySelectionList,
            onKeywordInput = searchScreenViewModel::setKeyword,
            onCategorySelected = searchScreenViewModel::addCategoryToSelectedCategoryList,
            onCategoryUnselected = searchScreenViewModel::removeCategoryFromSelectedCategoryList,
            modifier = modifier
        )

        is SearchScreenUiState.HasResult -> HasResult(
            keyword = s.keyword,
            categorySelectionList = s.categorySelectionList,
            podcastList = s.result,
            onKeywordInput = searchScreenViewModel::setKeyword,
            onCategorySelected = searchScreenViewModel::addCategoryToSelectedCategoryList,
            onCategoryUnselected = searchScreenViewModel::removeCategoryFromSelectedCategoryList,
            onPodcastSelected = onPodcastSelected,
            modifier = modifier,
        )
    }
}

/**
 * A composable function that displays the "Ready" state of the UI, primarily focusing on input
 * controls. This function wraps the [Controls] composable and pre-configures it for the "Ready"
 * state, including requesting focus.
 *
 * Our root composable is a [Controls] whose arguments are:
 *  - `keyword` is our [String] parameter [keyword].
 *  - `categorySelectionList` is our [CategorySelectionList] parameter [categorySelectionList].
 *  - `onKeywordInput` is our [onKeywordInput] lambda parameter.
 *  - `onCategorySelected` is our [onCategorySelected] lambda parameter.
 *  - `onCategoryUnselected` is our [onCategoryUnselected] lambda parameter.
 *  - `modifier` is our [Modifier] parameter [modifier].
 *  - `toRequestFocus` is `true`.
 *
 * @param keyword The current keyword input string.
 * @param categorySelectionList The list of categories and their selection status.
 * @param onKeywordInput Callback function triggered when the keyword input changes. It provides
 * the new keyword string.
 * @param onCategorySelected Callback function triggered when a category is selected. It provides
 * the [CategoryInfo] of the selected category.
 * @param onCategoryUnselected Callback function triggered when a category is unselected. It
 * provides the [CategoryInfo] of the unselected category.
 * @param modifier Modifier to be applied to the underlying [Controls] composable.
 *
 * @see Controls
 * @see CategorySelectionList
 * @see CategoryInfo
 */
@Composable
private fun Ready(
    keyword: String,
    categorySelectionList: CategorySelectionList,
    onKeywordInput: (String) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Controls(
        keyword = keyword,
        categorySelectionList = categorySelectionList,
        onKeywordInput = onKeywordInput,
        onCategorySelected = onCategorySelected,
        onCategoryUnselected = onCategoryUnselected,
        modifier = modifier,
        toRequestFocus = true
    )
}

/**
 * Displays the search result screen, including search controls and the list of podcasts.
 *
 * This composable handles the display of search results based on a keyword and selected categories.
 * It also provides controls for interacting with the search, such as changing the keyword,
 * selecting/unselecting categories, and selecting a podcast from the results.
 *
 * Our root composable is a [SearchResult] whose arguments are:
 *  - `podcastList` is our [PodcastList] parameter [podcastList].
 *  - `onPodcastSelected` is our [onPodcastSelected] lambda parameter.
 *  - `header` is a lambda that composes a [Controls] whose `keyword` argument is our [String]
 *  parameter [keyword], `categorySelectionList` argument our [CategorySelectionList] parameter
 *  [categorySelectionList], `onKeywordInput` argument our [onKeywordInput] lambda parameter,
 *  `onCategorySelected` argument our [onCategorySelected] lambda parameter, and
 *  `onCategoryUnselected` argument our [onCategoryUnselected] lambda parameter.
 *  - `modifier` is our [Modifier] parameter [modifier].
 *
 * @param keyword The current search keyword.
 * @param categorySelectionList The list of categories available for selection and their current
 * selection state.
 * @param podcastList The list of podcasts matching the current search criteria.
 * @param onKeywordInput Callback triggered when the user inputs a new keyword. It is called with
 * the updated keyword as its argument.
 * @param onCategorySelected Callback triggered when the user selects a category. It is called with
 * the selected [CategoryInfo] as its argument.
 * @param onCategoryUnselected Callback triggered when the user unselects a category. It is called
 * with the unselected [CategoryInfo] as its argument.
 * @param onPodcastSelected Callback triggered when the user selects a podcast. It is called the
 * selected [PodcastInfo] as its argument.
 * @param modifier [Modifier] for styling and layout customization. Our caller [SearchScreen] calls
 * us with its own [Modifier] parameter which traces back to a [Modifier.fillMaxSize] with a
 * [Modifier.padding] chained to it that adds the [PaddingValues] constant
 * `JetcasterAppDefaults.overScanMargin.default` (top = 40.dp, bottom = 40.dp, start = 80.dp,
 * end = 80.dp) to our padding.
 */
@Composable
private fun HasResult(
    keyword: String,
    categorySelectionList: CategorySelectionList,
    podcastList: PodcastList,
    onKeywordInput: (String) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchResult(
        podcastList = podcastList,
        onPodcastSelected = onPodcastSelected,
        header = {
            Controls(
                keyword = keyword,
                categorySelectionList = categorySelectionList,
                onKeywordInput = onKeywordInput,
                onCategorySelected = onCategorySelected,
                onCategoryUnselected = onCategoryUnselected,
            )
        },
        modifier = modifier
    )
}

/**
 * A composable function that displays controls for filtering and selecting podcast categories.
 * It includes a keyword input field and a category selection list.
 *
 * We start by composing a [LaunchedEffect] whose `key1` argument is our [Boolean] parameter
 * [toRequestFocus] (causes it to be rerun whenever [toRequestFocus] changes value). In its
 * [CoroutineScope] `block` lambda argument, if our [Boolean] parameter [toRequestFocus] is `true`,
 * call the [FocusRequester.requestFocus] method of our [FocusRequester] parameter [focusRequester]
 * to request focus on our [CategorySelection] composable.
 *
 * Our root composable is a [Column] whose arguments are:
 *  - `verticalArrangement` is a [Arrangement.spacedBy] with its `space` argument the constant
 *  `JetcasterAppDefaults.gap.item` (20.dp)
 *  - `modifier` is our [Modifier] parameter [modifier].
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we first compose a
 * [KeywordInput] whose arguments are:
 *  - `keyword` is our [String] parameter [keyword].
 *  - `onKeywordInput` is our [onKeywordInput] lambda parameter.
 *
 * Then we compose a [CategorySelection] whose arguments are:
 *  - `categorySelectionList` is our [CategorySelectionList] parameter [categorySelectionList].
 *  - `onCategorySelected` is our [onCategorySelected] lambda parameter.
 *  - `onCategoryUnselected` is our [onCategoryUnselected] lambda parameter.
 *  - `modifier` is a [Modifier.focusRestorer] with a [Modifier.focusRequester] chained to it whose
 *  `focusRequester` argument is our [FocusRequester] parameter [focusRequester].
 *
 * @param keyword The current keyword entered by the user for filtering.
 * @param categorySelectionList The list of categories with their selection status.
 * @param onKeywordInput Callback to be invoked when the user inputs a new keyword. It is called
 * with the updated keyword as its argument.
 * @param onCategorySelected Callback to be invoked when the user selects a category. It is called
 * with the selected [CategoryInfo] as its argument.
 * @param onCategoryUnselected Callback to be invoked when a category is unselected by the user.
 * It is called with the unselected [CategoryInfo] as its argument.
 * @param modifier [Modifier] for the outer layout of the controls.
 * @param focusRequester A [FocusRequester] used to request focus on the category selection.
 * @param toRequestFocus A [Boolean] flag indicating whether to request focus on the category
 * selection.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Controls(
    keyword: String,
    categorySelectionList: CategorySelectionList,
    onKeywordInput: (String) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    toRequestFocus: Boolean = false
) {
    LaunchedEffect(key1 = toRequestFocus) {
        if (toRequestFocus) {
            focusRequester.requestFocus()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.item),
        modifier = modifier
    ) {
        KeywordInput(
            keyword = keyword,
            onKeywordInput = onKeywordInput,
        )
        CategorySelection(
            categorySelectionList = categorySelectionList,
            onCategorySelected = onCategorySelected,
            onCategoryUnselected = onCategoryUnselected,
            modifier = Modifier
                .focusRestorer()
                .focusRequester(focusRequester = focusRequester)
        )
    }
}

/**
 * A composable function that provides a styled input field for keyword entry.
 *
 * This function creates a text field with a search icon, a rounded background,
 * and specific text styles. It's designed for users to input a search keyword.
 *
 * We start by initializing our [TextStyle] variable `textStyle` to a copy of the
 * [Typography.bodyMedium] of our custom [MaterialTheme.typography] with its [Color]`color`
 * property set to the [ColorScheme.onSurfaceVariant] of our custom [MaterialTheme.colorScheme].
 *
 * Then we initialize our [SolidColor] variable `cursorBrush` to a [SolidColor] with its [Color]
 * `value` argument set to [ColorScheme.onSurfaceVariant] of our custom [MaterialTheme.colorScheme].
 *
 * Our root composable is a [BasicTextField] whose arguments are:
 *  - `value` is our [String] parameter [keyword].
 *  - `onValueChange` is our [onKeywordInput] lambda parameter.
 *  - `textStyle` is our [TextStyle] variable `textStyle`.
 *  - `cursorBrush` is our [SolidColor] variable `cursorBrush`.
 *  - `modifier` is our [Modifier] parameter [modifier].
 *  - `keyboardOptions` is a [KeyboardOptions] with its [KeyboardOptions.imeAction] set to
 *  [ImeAction.Next].
 *
 * The `decorationBox` argument of the [BasicTextField] is a lambda that composes a [Box] whose
 * `modifier` argument is a [Modifier.fillMaxWidth] with a [Modifier.background] chained to it whose
 * [Color] `color` argument is the [ColorScheme.surfaceVariant] of our custom
 * [MaterialTheme.colorScheme] and whose `shape` argument is a [RoundedCornerShape] with its
 * `percent` argument set to `50`. In the [BoxScope]  `content` composable lambda argument of the
 * [Box] we compose a [Row] whose `modifier` argument is a [Modifier.padding] with its `horizontal`
 * argument set to `16.dp` and its `vertical` argument set to `12.dp`. In the [RowScope] `content`
 * composable lambda argument of the [Row] we first compose an [Icon] whose arguments are:
 *  - `imageVector` is the [ImageVector] drawn by [Icons.Filled.Search].
 *  - `contentDescription` is the [String] resource whose `id` is `R.string.label_search`
 *  ("Search podcasts by keyword").
 *  - `modifier` is a [Modifier.padding] with its `end` argument set to `12.dp`.
 *
 * At the end of the [Row] we compose the `innerTextField` composable lambda that is passed to the
 * `decorationBox` lambda.
 *
 * @param keyword The current keyword value in the input field.
 * @param onKeywordInput A callback function that is invoked when the keyword value changes.
 * It receives the new keyword string as its argument.
 * @param modifier [Modifier] to apply to the input field.
 */
@Composable
private fun KeywordInput(
    keyword: String,
    onKeywordInput: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val cursorBrush = SolidColor(value = MaterialTheme.colorScheme.onSurfaceVariant)
    BasicTextField(
        value = keyword,
        onValueChange = onKeywordInput,
        textStyle = textStyle,
        cursorBrush = cursorBrush,
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(percent = 50)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.label_search),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    innerTextField()
                }
            }
        }
    )
}

/**
 * Displays a selectable list of category chips, allowing the user to select or unselect categories.
 *
 * This composable uses a [FlowRow] to display a list of [FilterChip] components. Each chip
 * represents a category and can be toggled between selected and unselected states.
 *
 * Our root composable is a [FlowRow] whose arguments are:
 *  - `modifier` is our [Modifier] parameter [modifier].
 *  - `horizontalArrangement` is a [Arrangement.spacedBy] with its `space` argument the constant
 *  `JetcasterAppDefaults.gap.chip` (8.dp)
 *  - `verticalArrangement` is a [Arrangement.spacedBy] with its `space` argument the constant
 *  `JetcasterAppDefaults.gap.chip` (8.dp)
 *
 * In the [FlowRowScope] `content` composable lambda argument of the [FlowRow] we use the
 * [Iterable.forEach] method of our [CategorySelectionList] parameter [categorySelectionList] to
 * iterate over each [CategorySelection] object. We capture each [CategorySelection] in variable
 * `category` and then compose a [FilterChip] whose `selected` argument is the value of the
 * [CategorySelection.isSelected] property of `category`, `onClick` argument a lambda that
 * invokes the [onCategorySelected] lambda parameter if the [CategorySelection.isSelected] property
 * of `category` is `false`, or the [onCategoryUnselected] lambda parameter if it is `true` with the
 * [CategoryInfo] of `category` as their arguments.
 *
 * In the `content` composable lambda argument of the [FilterChip] we compose a [Text] whose
 * `text` argument is the [String] value of the [CategoryInfo.name] property of the
 * [CategorySelection.categoryInfo] property of `category`.
 *
 * @param categorySelectionList A list of [CategorySelection] objects, each containing information
 * about a category and its selected state.
 * @param onCategorySelected A callback function invoked when a category is selected. It is called
 * with the [CategoryInfo] of the selected category.
 * @param onCategoryUnselected A callback function invoked when a category is unselected. It is
 * called with the [CategoryInfo] of the unselected category.
 * @param modifier An optional [Modifier] to customize the layout and appearance of the [FlowRow]
 * containing the category chips.
 */
@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CategorySelection(
    categorySelectionList: CategorySelectionList,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.chip),
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.chip),
    ) {
        categorySelectionList.forEach { category: CategorySelection ->
            FilterChip(
                selected = category.isSelected,
                onClick = {
                    if (category.isSelected) {
                        onCategoryUnselected(category.categoryInfo)
                    } else {
                        onCategorySelected(category.categoryInfo)
                    }
                }
            ) {
                Text(text = category.categoryInfo.name)
            }
        }
    }
}

@Composable
private fun SearchResult(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 4),
        horizontalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.podcastRow),
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.podcastRow),
        modifier = modifier,
    ) {
        item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
            header()
        }
        items(items = podcastList) { podcast: PodcastInfo ->
            PodcastCard(podcastInfo = podcast, onClick = { onPodcastSelected(podcast) })
        }
    }
}
