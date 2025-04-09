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

package com.example.jetcaster.ui.library

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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.domain.PodcastCategoryFilterUseCase
import com.example.jetcaster.core.model.CategoryTechnology
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Library screen.
 *
 * This ViewModel manages the data and state for the Library screen, which displays
 * the user's followed podcasts, latest episodes, and the current queue.
 *
 * @property podcastsRepository Repository for interacting with podcast data.
 * @property episodeStore Store for accessing and managing episode data.
 * @property podcastStore Store for accessing and managing podcast data.
 * @property episodePlayer Player for controlling episode playback.
 * @property categoryStore Store for accessing and managing category data.
 * @property podcastCategoryFilterUseCase Use case for filtering podcasts by category.
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val episodeStore: EpisodeStore,
    private val podcastStore: PodcastStore,
    private val episodePlayer: EpisodePlayer,
    private val categoryStore: CategoryStore,
    private val podcastCategoryFilterUseCase: PodcastCategoryFilterUseCase
) : ViewModel() {

    /**
     * The default category used when no specific category is selected or available.
     *
     * This flow emits the category with the name [CategoryTechnology] if it exists in the
     * [categoryStore]. If no such category is found, it emits `null`.
     *
     * This property can be used to provide a fallback or initial category in UIs or other logic
     * where a category is expected. It is a Flow, allowing for reactive updates if the category
     * data changes in the underlying storage.
     *
     * @see CategoryStore.getCategory
     */
    private val defaultCategory: Flow<Category?> =
        categoryStore.getCategory(name = CategoryTechnology)

    /**
     * A [Flow] that emits a list of top podcasts based on the currently selected category.
     *
     * This flow is derived from the [defaultCategory] flow. Each time a new category is emitted
     * by [defaultCategory], this flow will:
     *  1. Cancel any ongoing emission from the previous category.
     *  2. Invoke the [podcastCategoryFilterUseCase] with the new category.
     *  3. Emit the list of podcasts returned by [podcastCategoryFilterUseCase].
     *
     * If [defaultCategory] emits `null`, it will be treated as an unfiltered category and emit
     * podcasts from all categories.
     *
     * The [Flow.flatMapLatest] operator is used to ensure that only the results from the latest
     * category are emitted. This prevents stale or out-of-order results from previous categories
     * from being delivered after a new category has been selected.
     *
     * The `@OptIn(ExperimentalCoroutinesApi::class)` annotation is used because `flatMapLatest`
     * is considered an experimental API.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val topPodcastsFlow: Flow<PodcastCategoryFilterResult> =
        defaultCategory.flatMapLatest { category: Category? ->
            podcastCategoryFilterUseCase(category?.asExternalModel())
        }

    /**
     * A [Flow] emitting a list of podcasts that the user is following, sorted by the date of their
     * last published episode.
     *
     * This flow provides a reactive stream of [PodcastWithExtraInfo] objects, representing the
     * user's followed podcasts. The list is sorted in descending order, with the podcasts having
     * the most recently published episodes appearing first.
     *
     * The flow will emit a new list whenever:
     *  - The set of followed podcasts changes (e.g., a user follows or unfollows a podcast).
     *  - A new episode is published for a followed podcast, potentially changing the sorting order.
     *  - The underlying data source (podcastStore) updates.
     *
     * Subscribers to this flow will receive updates automatically as these changes occur.
     *
     * Note: The [PodcastWithExtraInfo] includes additional information about each podcast, beyond
     * the basic podcast data.
     *
     * The flow is backed by the [PodcastStore.followedPodcastsSortedByLastEpisode] method of our
     * [PodcastStore] property [podcastStore] which is responsible for fetching and sorting the
     * followed podcasts.
     *
     * @see PodcastWithExtraInfo
     * @see PodcastStore.followedPodcastsSortedByLastEpisode
     */
    private val followingPodcastListFlow: Flow<List<PodcastWithExtraInfo>> =
        podcastStore.followedPodcastsSortedByLastEpisode()

    private val queue: Flow<List<PlayerEpisode>> =
        episodePlayer.playerState.map { episodeState: EpisodePlayerState ->
            episodeState.queue
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val latestEpisodeListFlow: Flow<List<PlayerEpisode>> = podcastStore
        .followedPodcastsSortedByLastEpisode()
        .flatMapLatest { podcastList: List<PodcastWithExtraInfo> ->
            if (podcastList.isNotEmpty()) {
                combine(podcastList.map { episodeStore.episodesInPodcast(it.podcast.uri, 1) }) {
                    it.map { episodes: List<EpisodeToPodcast> ->
                        episodes.first()
                    }
                }
            } else {
                flowOf(emptyList())
            }
        }.map { list: List<EpisodeToPodcast> ->
            (list.map { it.toPlayerEpisode() })
        }

    val uiState: StateFlow<LibraryScreenUiState> =
        combine(
            flow = topPodcastsFlow,
            flow2 = followingPodcastListFlow,
            flow3 = latestEpisodeListFlow,
            flow4 = queue
        ) { topPodcasts: PodcastCategoryFilterResult,
            podcastList: List<PodcastWithExtraInfo>,
            episodeList: List<PlayerEpisode>,
            queue: List<PlayerEpisode> ->
            if (podcastList.isEmpty()) {
                LibraryScreenUiState.NoSubscribedPodcast(topPodcasts = topPodcasts.topPodcasts)
            } else {
                LibraryScreenUiState.Ready(
                    subscribedPodcastList = podcastList,
                    latestEpisodeList = episodeList,
                    queue = queue
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryScreenUiState.Loading
        )

    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }

    @Suppress("unused")
    fun playEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode = playerEpisode)
    }

    fun onTogglePodcastFollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastUri = podcastUri)
        }
    }
}

sealed interface LibraryScreenUiState {
    data object Loading : LibraryScreenUiState
    data class NoSubscribedPodcast(
        val topPodcasts: List<PodcastInfo>
    ) : LibraryScreenUiState

    data class Ready(
        val subscribedPodcastList: List<PodcastWithExtraInfo>,
        val latestEpisodeList: List<PlayerEpisode>,
        val queue: List<PlayerEpisode>
    ) : LibraryScreenUiState
}
