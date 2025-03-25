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

package com.example.jetcaster.tv.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
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
 * ViewModel for the Library screen, responsible for managing the UI state
 * and interacting with the data layer to fetch and update podcast and episode information.
 *
 * This ViewModel fetches the list of followed podcasts and their latest episodes,
 * combines them into a single UI state, and exposes it to the UI layer. It also
 * handles user actions like playing an episode.
 *
 * @property podcastsRepository Repository for fetching and updating podcast information.
 * @property episodeStore Data store for accessing and managing episode data.
 * @property podcastStore Data store for accessing and managing podcast data, including followed podcasts.
 * @property episodePlayer Component responsible for playing audio episodes.
 */
@HiltViewModel
class LibraryScreenViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val episodeStore: EpisodeStore,
    podcastStore: PodcastStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     * A flow emitting a list of `PodcastInfo` representing the podcasts the user is following.
     * The list is sorted by the last episode's publication date, with the most recent episode first.
     *
     * This flow is derived from the underlying [PodcastStore] property [podcastStore], specifically
     * from the [PodcastStore.followedPodcastsSortedByLastEpisode] method, which provides a list of
     * [PodcastWithExtraInfo]. This internal model is then mapped to the
     * external representation [PodcastInfo].
     *
     * The flow will emit a new list whenever the underlying data in the [PodcastStore] changes,
     * i.e. when a user follows/unfollows a podcast, or when the last episode of a followed podcast
     * is updated.
     *
     * This property is private to encapsulate the data source and transformation logic.
     */
    private val followingPodcastListFlow: Flow<List<PodcastInfo>> =
        podcastStore.followedPodcastsSortedByLastEpisode().map { list: List<PodcastWithExtraInfo> ->
            list.map { it.asExternalModel() }
        }

    /**
     * A flow that emits a list of the latest episodes from followed podcasts.
     *
     * This flow does the following:
     * 1. Gets a list of followed podcasts, sorted by their most recent episode date, from the
     * [PodcastStore] property [podcastStore].
     * 2. If there are followed podcasts:
     *    - For each followed podcast, it fetches the most recent episode (limit of 1) from the
     *    [EpisodeStore] property [episodeStore].
     *    - It then combines the individual flows of latest episodes into a single flow emitting a
     *    list of the latest episodes.
     *    - Each emission in this list contains the single latest episode from each followed podcast.
     * 3. If there are no followed podcasts, it emits an empty list.
     * 4. Finally, it transforms the list of [EpisodeToPodcast] into an [EpisodeList], ready to be
     * used by the player.
     *
     * The flow utilizes [flatMapLatest] to ensure that only the latest list of podcasts is being
     * processed. If the list of followed podcasts changes, any ongoing processing from the previous
     * list is cancelled.
     *
     * This property is annotated with `@OptIn(ExperimentalCoroutinesApi::class)` because it uses
     * the `flatMapLatest` operator, which is considered experimental.
     *
     * @see PodcastStore.followedPodcastsSortedByLastEpisode
     * @see EpisodeStore.episodesInPodcast
     * @see EpisodeToPodcast.toPlayerEpisode
     * @see EpisodeList
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val latestEpisodeListFlow: Flow<EpisodeList> = podcastStore
        .followedPodcastsSortedByLastEpisode()
        .flatMapLatest { podcastList: List<PodcastWithExtraInfo> ->
            if (podcastList.isNotEmpty()) {
                combine(podcastList.map { episodeStore.episodesInPodcast(it.podcast.uri, 1) }) {
                    it.map { episodes ->
                        episodes.first()
                    }
                }
            } else {
                flowOf(value = emptyList())
            }
        }.map { list: List<EpisodeToPodcast> ->
            EpisodeList(member = list.map { it.toPlayerEpisode() })
        }

    /**
     * Represents the UI state of the library screen.
     *
     * This [StateFlow] emits different states based on the combined data from the following podcast
     * list and the latest episode list. It provides the following states:
     * - [LibraryScreenUiState.Loading]: Indicates that the data is being loaded.
     * - [LibraryScreenUiState.NoSubscribedPodcast]: Indicates that the user has not subscribed to
     * any podcasts.
     * - [LibraryScreenUiState.Ready]: Indicates that the data is ready to be displayed, containing
     * both the subscribed podcast list and the latest episode list.
     *
     * The state is derived from combining two flows:
     * - [followingPodcastListFlow]: A flow emitting a list of [PodcastInfo] representing the
     * podcasts the user is following.
     * - [latestEpisodeListFlow]: A flow emitting an [EpisodeList] representing the latest
     * episodes from followed podcasts.
     *
     * The [StateFlow] is started with a [SharingStarted.WhileSubscribed] strategy, meaning it will
     * keep emitting values as long as there are active subscribers, and will stop after 5 seconds
     * of no subscribers. The initial value of the [StateFlow] is [LibraryScreenUiState.Loading].
     */
    val uiState: StateFlow<LibraryScreenUiState> =
        combine(
            flow = followingPodcastListFlow,
            flow2 = latestEpisodeListFlow
        ) { podcastList: List<PodcastInfo>, episodeList: EpisodeList ->
            if (podcastList.isEmpty()) {
                LibraryScreenUiState.NoSubscribedPodcast
            } else {
                LibraryScreenUiState.Ready(
                    subscribedPodcastList = podcastList,
                    latestEpisodeList = episodeList
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryScreenUiState.Loading
        )

    /**
     * Initializes the ViewModel by launching a coroutine to update the podcasts.
     */
    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }

    /**
     * Plays the specified episode using the episode player ([EpisodePlayer] property [episodePlayer]).
     *
     * This function takes a [PlayerEpisode] object as input, which encapsulates the
     * information needed to play a specific episode (e.g., media URL, episode ID, etc.).
     * It then delegates the actual playback to the [EpisodePlayer] property [episodePlayer],
     *  [PlayerEpisode] data.
     *
     * @param playerEpisode The [PlayerEpisode] object representing the episode to be played.
     * Must not be `null`.
     * @throws IllegalArgumentException if `playerEpisode` is null.
     */
    fun playEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode = playerEpisode)
    }
}

/**
 * Represents the UI state of the Library screen.
 * This sealed interface provides a structured way to manage the different states
 * that the Library screen can be in, such as loading, having no subscribed podcasts,
 * or being ready to display the content.
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
     * Represents the UI state when the user has no subscribed podcasts.
     * This state indicates that the library screen should display a message or
     * UI elements indicating that the user needs to subscribe to podcasts to see content here.
     *
     * This object is a concrete implementation of the [LibraryScreenUiState] interface,
     * specifically designed for the scenario where there are no subscribed podcasts.
     */
    data object NoSubscribedPodcast : LibraryScreenUiState

    /**
     * Represents the UI state when the library screen is fully loaded and ready to display content.
     *
     * This state indicates that both the list of subscribed podcasts and the list of latest
     *  have been successfully fetched and are available for rendering.
     *
     * @property subscribedPodcastList The list of podcasts that the user is subscribed to.
     * @property latestEpisodeList The list of the latest episodes from the subscribed podcasts.
     */
    data class Ready(
        val subscribedPodcastList: PodcastList,
        val latestEpisodeList: EpisodeList,
    ) : LibraryScreenUiState
}
