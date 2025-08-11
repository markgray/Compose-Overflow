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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.tv.model.CategoryInfoList
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [ViewModel] used by [DiscoverScreen]. It is injected by Hilt, which also supplies the dependencies
 * required by its constructor thanks to the [Inject] annotation.
 *
 * @param podcastsRepository the [PodcastsRepository] to be used for managing podcasts, episodes,
 * and categories.
 * @param categoryStore the [CategoryStore] to be used for managing categories and their
 * relationships with podcasts and episodes.
 * @param episodePlayer the [EpisodePlayer] to use to play a [PlayerEpisode].
 */
@HiltViewModel
class DiscoverScreenViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val categoryStore: CategoryStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     * A [MutableStateFlow] that represents the currently selected category.
     */
    private val _selectedCategory: MutableStateFlow<CategoryInfo?> =
        MutableStateFlow(null)

    /**
     * A [Flow] that emits a list of [CategoryInfo]. It uses the
     * [CategoryStore.categoriesSortedByPodcastCount] method to fetch the list of [Category], then
     * maps it to a list of [CategoryInfo] objects.
     */
    private val categoryListFlow: Flow<List<CategoryInfo>> = categoryStore
        .categoriesSortedByPodcastCount()
        .map { categoryList: List<Category> ->
            categoryList.map { category: Category ->
                CategoryInfo(
                    id = category.id,
                    name = category.name.filter { !it.isWhitespace() }
                )
            }
        }

    /**
     * A [Flow] that combines [categoryListFlow] and [_selectedCategory] to determine the currently
     * selected category. If [_selectedCategory] is null, it will emit the first category in the
     * [categoryListFlow]. Otherwise, it will emit the value of [_selectedCategory].
     */
    private val selectedCategoryFlow: Flow<CategoryInfo?> = combine(
        categoryListFlow,
        _selectedCategory
    ) { categoryList: List<CategoryInfo>, category: CategoryInfo? ->
        category ?: categoryList.firstOrNull()
    }

    /**
     * A [Flow] that emits a list of [PodcastInfo] based on the currently selected category. It uses
     * [flatMapLatest] to react to changes in [selectedCategoryFlow] and fetch relevant podcasts. It
     * limits the number of results to 10.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastInSelectedCategory: Flow<List<PodcastInfo>> =
        selectedCategoryFlow.flatMapLatest {
            if (it != null) {
                categoryStore.podcastsInCategorySortedByPodcastCount(it.id, limit = 10)
            } else {
                flowOf(emptyList())
            }
        }.map { list: List<PodcastWithExtraInfo> ->
            list.map { it.asExternalModel() }
        }

    /**
     * A [Flow] that emits a list of [EpisodeList] based on the currently selected category. It yses
     * [flatMapLatest] to react to changes in [selectedCategoryFlow] and fetch relevant episodes. It
     * receives a maximum of 20 episodes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val latestEpisodeFlow: Flow<EpisodeList> = selectedCategoryFlow.flatMapLatest {
        if (it != null) {
            categoryStore.episodesFromPodcastsInCategory(it.id, 20)
        } else {
            flowOf(emptyList())
        }
    }.map { list: List<EpisodeToPodcast> ->
        EpisodeList(list.map { it.toPlayerEpisode() })
    }

    /**
     * A [StateFlow] that represents the UI state of the Discover screen. It combines four flows:
     *  - [categoryListFlow]: Emits a list of [CategoryInfo].
     *  - [selectedCategoryFlow]: Emits the currently selected [CategoryInfo].
     *  - [podcastInSelectedCategory]: Emits a list of [PodcastInfo] based on the currently selected
     *  category.
     *  - [latestEpisodeFlow]: Emits a list of [EpisodeList] based on the currently selected category.
     *
     * If the [CategoryInfo] from [selectedCategoryFlow] is not null, it creates a
     * [DiscoverScreenUiState.Ready], otherwise it emits [DiscoverScreenUiState.Loading]. In either
     * case it uses [Flow.stateIn] to convert the [Flow] into a [StateFlow] whose arguments are:
     *  - `scope`: The [viewModelScope].
     *  - `started`: [SharingStarted.WhileSubscribed] indicates that the flow should be started when
     *  when there is at least one subscriber, and it will continue to emit values for 5 seconds
     *  after the last subscriber leaves.
     *  - `initialValue`: [DiscoverScreenUiState.Loading].
     */
    val uiState: StateFlow<DiscoverScreenUiState> = combine(
        categoryListFlow,
        selectedCategoryFlow,
        podcastInSelectedCategory,
        latestEpisodeFlow,
    ) { categoryList: List<CategoryInfo>,
        category: CategoryInfo?,
        podcastList: List<PodcastInfo>,
        latestEpisodes: EpisodeList ->
        if (category != null) {
            DiscoverScreenUiState.Ready(
                categoryInfoList = CategoryInfoList(categoryList),
                selectedCategory = category,
                podcastList = podcastList,
                latestEpisodeList = latestEpisodes
            )
        } else {
            DiscoverScreenUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DiscoverScreenUiState.Loading
    )

    /**
     * Initializes the ViewModel by refreshing the data when the instance is created.
     */
    init {
        refresh()
    }

    /**
     * Selects a category and updates the [MutableStateFlow.value] of [_selectedCategory],
     * triggering updates to related flows.
     *
     * This function takes a [CategoryInfo] object as input and sets it as the currently
     * selected category.
     *
     * @param category The [CategoryInfo] object representing the category to select.
     */
    fun selectCategory(category: CategoryInfo) {
        _selectedCategory.value = category
    }

    /**
     * Initiates playback of a given episode.
     *
     * This function delegates the actual playback operation to the `episodePlayer`.
     *
     * @param playerEpisode The [PlayerEpisode] object containing the necessary information
     * to play the episode (e.g., media URL, episode ID, etc.).
     *
     * @see PlayerEpisode
     * @see EpisodePlayer.play
     */
    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode = playerEpisode)
    }

    /**
     * Refreshes the list of podcasts.
     *
     * This function triggers an asynchronous update of the podcasts data.
     * It uses the [podcastsRepository] to fetch the latest podcasts and update the local
     * data source. The `force` parameter is set to `false`, indicating that the
     * update should only occur if necessary (e.g., data is outdated or missing).
     *
     * This operation is performed in a coroutine within the ViewModel's scope, ensuring
     * that it doesn't block the main thread and is automatically cancelled when the
     * ViewModel is cleared.
     *
     * Note that this method does not return any specific result, but instead updates the
     * internal state managed by the `podcastsRepository`. Observers of the repository
     * (e.g. UI components) can react to this state change.
     *
     * @see PodcastsRepository.updatePodcasts
     * @see viewModelScope
     * @see launch
     */
    private fun refresh() {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }
}

/**
 * Represents the UI state of the Discover screen.
 *
 * This sealed interface defines the different states the Discover screen can be in,
 * including loading data and displaying the fetched content.
 */
sealed interface DiscoverScreenUiState {
    /**
     * Represents the loading state of the Discover screen.
     *
     * This object indicates that data is currently being fetched or loaded
     * for the Discover screen, and no content is yet available to display.
     * It is used as a specific state within the `DiscoverScreenUiState`
     * sealed interface/class hierarchy.
     */
    data object Loading : DiscoverScreenUiState

    /**
     * Represents the "Ready" state of the Discover screen.
     *
     * This data class encapsulates all the necessary information required to display
     * the fully loaded content on the Discover screen.  It includes lists of categories,
     * podcasts, and episodes, as well as the currently selected category.
     *
     * @property categoryInfoList A [CategoryInfoList] containing a list of all available
     * categories.
     * @property selectedCategory The [CategoryInfo] that is currently selected by the user.
     * @property podcastList A [PodcastList] containing a list of podcasts to display.
     * @property latestEpisodeList An [EpisodeList] containing a list of the latest episodes to
     * display.
     */
    data class Ready(
        val categoryInfoList: CategoryInfoList,
        val selectedCategory: CategoryInfo,
        val podcastList: PodcastList,
        val latestEpisodeList: EpisodeList,
    ) : DiscoverScreenUiState
}
