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

package com.example.jetcaster.tv.ui.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowScope
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.model.CategoryInfoList
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.component.Catalog
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * The main composable function for the Discover screen.
 *
 * This screen displays a list of podcasts, categorized by different topics.
 * It allows the user to browse through podcasts, select categories, and play episodes.
 *
 * @param showPodcastDetails A lambda function that is invoked when a podcast is selected.
 * It takes a [PodcastInfo] object as a parameter and navigates to the podcast details screen.
 * @param playEpisode A lambda function that is invoked when an episode is selected for playback.
 * It takes a [PlayerEpisode] object as a parameter, representing the episode to be played.
 * @param modifier Modifier for styling and layout of the Discover screen. Our caller the `Route`
 * method of `JetcasterApp` passes us a [Modifier.fillMaxSize].
 * @param discoverScreenViewModel The ViewModel associated with the Discover screen, using Hilt
 * for dependency injection. Defaults to a new [DiscoverScreenViewModel] if not provided.
 */
@Composable
fun DiscoverScreen(
    showPodcastDetails: (PodcastInfo) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    discoverScreenViewModel: DiscoverScreenViewModel = hiltViewModel()
) {
    val uiState: DiscoverScreenUiState by discoverScreenViewModel.uiState.collectAsState()

    when (val s: DiscoverScreenUiState = uiState) {
        DiscoverScreenUiState.Loading -> {
            Loading(
                modifier = Modifier
                    .fillMaxSize()
                    .then(other = modifier)
            )
        }

        is DiscoverScreenUiState.Ready -> {
            CatalogWithCategorySelection(
                categoryInfoList = s.categoryInfoList,
                podcastList = s.podcastList,
                selectedCategory = s.selectedCategory,
                latestEpisodeList = s.latestEpisodeList,
                onPodcastSelected = showPodcastDetails,
                onCategorySelected = discoverScreenViewModel::selectCategory,
                onEpisodeSelected = {
                    discoverScreenViewModel.play(it)
                    playEpisode(it)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .then(other = modifier)
            )
        }
    }
}

/**
 * Displays a catalog of podcasts and latest episodes, along with a tab row for category selection.
 *
 * This composable provides a UI for browsing podcasts and episodes, categorized by different
 * categories. It utilizes a tab row for category selection, allowing users to switch between
 * different categories and view the corresponding podcasts and episodes.
 *
 * We start by initializing and remembering two [FocusRequester] variables `focusRequester` and
 * `selectedTab` using the [FocusRequester.createRefs] method. Then we use [LaunchedEffect] with
 * a `key1` of [Unit] to launch a coroutine that calls the [FocusRequester.requestFocus] method of
 * [FocusRequester] variable `focusRequester` when the [LaunchedEffect] enters the composition. We
 * then initialize our [Int] variable `selectedTabIndex` using the [List.indexOf] method of
 * [CategoryInfoList] parameter [categoryInfoList] to find the index of the `element` which is
 * [CategoryInfo] parameter [selectedCategory].
 *
 * Our root composable is a [Catalog] whose arguments are:
 *  - `podcastList` is our [PodcastList] parameter [podcastList].
 *  - `latestEpisodeList` is our [EpisodeList] parameter [latestEpisodeList]
 *  - `onPodcastSelected` is a lambda that accepts the [PodcastInfo] passed the lambda in variable
 *  `it`, then calls the [FocusRequester.saveFocusedChild] method of [FocusRequester] variable
 *  `focusRequester`, then calls our lambda paramter [onPodcastSelected] with the [PodcastInfo]
 *  passed the lambda in `it`.
 *  - `onEpisodeSelected` is a lambda that accepts the [PlayerEpisode] passed the lambda in variable
 *  `it`, then calls the [FocusRequester.saveFocusedChild] method of [FocusRequester] variable
 *  `focusRequester`, then calls our lambda paramter [onEpisodeSelected] with the [PlayerEpisode]
 *  passed the lambda in `it`.
 *  - `modifier` chains a [Modifier.focusRequester] to our [Modifier] parameter [modifier] whose
 *  `focusRequester` argument is our [FocusRequester] variable `focusRequester`
 *  - `state` is our [LazyListState] parameter [state].
 *
 * In the `header` Composable lambda argument of the [Catalog] we compose a [TabRow] whose
 * `selectedTabIndex` argument is our [Int] variable `selectedTabIndex`, and whose `modifier`
 * argument is a [Modifier.focusProperties] whose `enter` is a lambda that returns our [FocusRequester]
 * variable `selectedTab`.
 *
 * In the [TabRowScope] `tabs` composable lambda argument of the [TabRow] we use the
 * [Iterable.forEachIndexed] method of [CategoryInfoList] parameter [categoryInfoList] to loop
 * through all of its elements capturing the index in [Int] variable `index` and the [CategoryInfo]
 * in variable `category` and in the `action` lambda argument we initialize our [Modifier] variable
 * `tabModifier` to a [Modifier.focusRequester] whose `focusRequester` argument is our [FocusRequester]
 * variable `selectedTab` if `selectedTabIndex` is equal to `index` or to an empty [Modifier] is it
 * is not. We then compose a [Tab] whose arguments are:
 *  - `selected` is `true` if `index` is equal to `selectedTabIndex`
 *  - `onFocus` is a lambda that calls our lambda parameter [onCategorySelected] with the current
 *  [CategoryInfo] in varible `category`
 *  - `modifier` is our [Modifier] variable `tabModifier`,
 *
 * In the [RowScope] `content` composable lambda argument of the [Tab] we compose a [Text] whose
 * `text` argument is the [CategoryInfo.name] of the [CategoryInfo] in variable `category` and
 * whose `modifier` argument is a [Modifier.padding] that adds the [PaddingValues] constant
 * `JetcasterAppDefaults.padding.tab` (a [PaddingValues] that adds 16.dp to the `horizontal` sides
 * and 5.dp to the `vertical` sides).
 *
 * @param categoryInfoList A list of [CategoryInfo] representing the available categories.
 * @param podcastList A list of [PodcastInfo] to be displayed in the catalog.
 * @param selectedCategory The currently selected [CategoryInfo].
 * @param latestEpisodeList A list of [PlayerEpisode] representing the latest episodes.
 * @param onPodcastSelected Callback to be invoked with the selected [PodcastInfo] when a podcast
 * is selected.
 * @param onEpisodeSelected Callback to be invoked with the selected [PlayerEpisode] when an episode
 * is selected.
 * @param onCategorySelected Callback to be invoked with the selected [CategoryInfo] when a category
 * is selected.
 * @param modifier Modifier for styling and layout of the catalog. Our caller [DiscoverScreen] passes
 * us a [Modifier.fillMaxSize] to which it chains its own [Modifier] parameter.
 * @param state The [LazyListState] controlling the scroll position of the catalog.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CatalogWithCategorySelection(
    categoryInfoList: CategoryInfoList,
    podcastList: PodcastList,
    selectedCategory: CategoryInfo,
    latestEpisodeList: EpisodeList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    onEpisodeSelected: (PlayerEpisode) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    val (focusRequester: FocusRequester, selectedTab: FocusRequester) = remember {
        FocusRequester.createRefs()
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    val selectedTabIndex: Int = categoryInfoList.indexOf(element = selectedCategory)

    Catalog(
        podcastList = podcastList,
        latestEpisodeList = latestEpisodeList,
        onPodcastSelected = {
            focusRequester.saveFocusedChild()
            onPodcastSelected(it)
        },
        onEpisodeSelected = {
            focusRequester.saveFocusedChild()
            onEpisodeSelected(it)
        },
        modifier = modifier.focusRequester(focusRequester = focusRequester),
        state = state,
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.focusProperties {
                enter = {
                    selectedTab
                }
            }
        ) {
            categoryInfoList.forEachIndexed { index: Int, category: CategoryInfo ->
                val tabModifier: Modifier = if (selectedTabIndex == index) {
                    Modifier.focusRequester(focusRequester = selectedTab)
                } else {
                    Modifier
                }

                Tab(
                    selected = index == selectedTabIndex,
                    onFocus = {
                        onCategorySelected(category)
                    },
                    modifier = tabModifier,
                ) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(paddingValues = JetcasterAppDefaults.padding.tab)
                    )
                }
            }
        }
    }
}
