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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
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
