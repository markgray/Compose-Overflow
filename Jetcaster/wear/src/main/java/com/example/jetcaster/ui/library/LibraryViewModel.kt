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

    /**
     * A flow of the current playback queue.
     *
     * This property provides a reactive stream of lists, each representing the
     * queue of episodes that are currently scheduled for playback in the player.
     * The queue is derived from the `episodePlayer.playerState` and reflects any
     * changes to the queue as they occur.
     *
     * The list within each emission represents the current queue state, with the
     * order of episodes indicating the intended playback order.
     *
     * This flow will emit a new list of `PlayerEpisode` whenever the underlying
     * `episodePlayer.playerState.queue` property changes.
     *
     * @see EpisodePlayerState.queue The underlying source of the queue data.
     * @see EpisodePlayerState The state object that contains the queue.
     * @see PlayerEpisode Represents an episode in the player queue.
     */
    private val queue: Flow<List<PlayerEpisode>> =
        episodePlayer.playerState.map { episodeState: EpisodePlayerState ->
            episodeState.queue
        }

    /**
     * A flow that emits a list of the latest episodes from followed podcasts.
     *
     * This flow does the following:
     *  1. Gets a list of followed podcasts, sorted by their last episode date, from the
     *  [podcastStore].
     *  2. For each followed podcast, it retrieves the most recent episode (limit of 1) from the
     *  [episodeStore].
     *  3. Combines the most recent episodes from each podcast into a single list.
     *  4. If no podcasts are followed, it emits an empty list.
     *  5. Converts each `EpisodeToPodcast` object in the list to a `PlayerEpisode` object.
     *
     * This flow is recomposed whenever:
     *  - The list of followed podcasts changes.
     *  - The latest episode of a followed podcast changes.
     *
     * The flow uses `flatMapLatest` to ensure that only the most recent list of podcasts is being
     * processed. When the list of followed podcasts changes, any ongoing processing of previous
     * lists is cancelled and restarted.
     *
     * The flow uses `combine` operator to wait for all inner flows (one for each podcast) to emit
     * before emitting a combined result.
     *
     * The `episodesInPodcast` method is called with a limit of 1 to efficiently only retrieve the
     * most recent episode.
     *
     * @see PodcastStore.followedPodcastsSortedByLastEpisode
     * @see EpisodeStore.episodesInPodcast
     * @see EpisodeToPodcast.toPlayerEpisode
     */
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

    /**
     * [uiState] represents the current state of the Library screen UI.
     *
     * It's a [StateFlow] that combines data from multiple sources:
     *  - [topPodcastsFlow]: A flow of [PodcastCategoryFilterResult] representing top podcasts.
     *  - [followingPodcastListFlow]: A flow of [List] of [PodcastWithExtraInfo] representing
     *  podcasts the user is following.
     *  - [latestEpisodeListFlow]: A flow of [List] of [PlayerEpisode] representing the latest
     *  episodes.
     *  - [queue]: A flow of [List] of [PlayerEpisode] representing the current playback queue.
     *
     * The [uiState] emits one of the following states:
     *  - [LibraryScreenUiState.Loading]: Indicates that the data is still being loaded. This is
     *  the initial state.
     *  - [LibraryScreenUiState.NoSubscribedPodcast]: Indicates that the user is not subscribed to
     *  any podcasts. It is also provided with the [List] of [PodcastInfo] property
     *  [PodcastCategoryFilterResult.topPodcasts] that contains the top podcasts available
     *  for the user to explore.
     *  - [LibraryScreenUiState.Ready]: Indicates that the data is loaded and ready to be
     *  displayed. It contains the [List] of [PodcastWithExtraInfo] of the user's followed podcasts,
     *  the [List] of [PlayerEpisode] of the latest episodes, and the [List] of [PlayerEpisode] of
     *  the current playback queue.
     *
     * The flow is started with [SharingStarted.WhileSubscribed], meaning it will keep collecting
     * data as long as there is at least one subscriber, and it will wait 5 seconds after the last
     * subscriber disappears before stopping.
     *
     * This property is meant to be observed by the UI to update its content based on the
     * current state.
     */
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

    /**
     * Initializes the library screen by updating the podcasts data using the
     * [PodcastsRepository.updatePodcasts] method of [PodcastsRepository]
     * property [podcastsRepository].
     */
    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }

    /**
     * Plays the specified episode using the episode player.
     *
     * This function takes a [PlayerEpisode] object as input, which encapsulates
     * all the necessary information about the episode to be played. It then
     * delegates the actual playback operation to the underlying [episodePlayer] instance.
     *
     * @param playerEpisode The [PlayerEpisode] object representing the episode to be played.
     * This object should contain details such as the episode's media URL, title, and any other
     * relevant metadata required by the player.
     *
     * @see PlayerEpisode
     * @see episodePlayer
     */
    @Suppress("unused")
    fun playEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode = playerEpisode)
    }

    /**
     * Toggles the followed state of a podcast.
     *
     * This function initiates the process of toggling whether a user is following a specific podcast.
     * It does so by calling the [PodcastStore.togglePodcastFollowed] method of the [PodcastStore]
     * property [podcastStore], which is responsible for handling the underlying data storage and
     * manipulation of same.
     *
     * The operation is performed asynchronously within the viewModel's scope using a coroutine.
     * This ensures that the UI thread is not blocked during the operation.
     *
     * @param podcastUri The URI (Unique Resource Identifier) of the podcast to be toggled. This is
     * a string that uniquely identifies the podcast within the application. It is used to find and
     * update the corresponding podcast's followed state.
     */
    fun onTogglePodcastFollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastUri = podcastUri)
        }
    }
}

/**
 * Represents the UI state of the Library screen.
 *
 * This sealed interface defines the different states the Library screen can be in,
 * such as loading, showing no subscribed podcasts, or showing the library content.
 */
sealed interface LibraryScreenUiState {
    /**
     * Represents the loading state of a library screen.
     *
     * This object signifies that data is currently being fetched or loaded
     * for the library screen, and no other specific content is available.
     * It's used as a state in the UI to indicate this waiting period.
     *
     * This state should be displayed to the user with a loading indicator.
     */
    data object Loading : LibraryScreenUiState
    
    /**
     * Represents the UI state of the library screen when the user has no subscribed podcasts.
     *
     * This data class signifies that the user has not yet subscribed to any podcasts.
     * It provides a list of `topPodcasts`, which can be displayed to the user as
     * suggestions or recommendations for podcasts they might be interested in.
     *
     * @property topPodcasts A list of [PodcastInfo] representing the top or recommended podcasts
     * that can be shown to the user when they have no subscriptions.
     *
     * @see LibraryScreenUiState
     * @see PodcastInfo
     */
    data class NoSubscribedPodcast(
        val topPodcasts: List<PodcastInfo>
    ) : LibraryScreenUiState

    /**
     * Represents the ready state of the Library screen.
     *
     * This data class holds the information needed to display the main content of the
     * Library screen when the data is successfully loaded. It contains lists of subscribed
     * podcasts, the latest episodes, and the current playback queue.
     *
     * @property subscribedPodcastList A list of [PodcastWithExtraInfo] representing the podcasts
     * the user is subscribed to.
     * @property latestEpisodeList A list of [PlayerEpisode] representing the latest episodes
     * from the subscribed podcasts.
     * @property queue A list of [PlayerEpisode] representing the current playback queue.
     */
    data class Ready(
        val subscribedPodcastList: List<PodcastWithExtraInfo>,
        val latestEpisodeList: List<PlayerEpisode>,
        val queue: List<PlayerEpisode>
    ) : LibraryScreenUiState
}
