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

package com.example.jetcaster.tv.ui.podcast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.Screen
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
 * [PodcastDetailsScreenViewModel] is the ViewModel for the Podcast Details screen.
 *
 * It is responsible for:
 *  - Retrieving and providing podcast details based on the provided podcast URI.
 *  - Retrieving and providing the list of episodes for the selected podcast.
 *  - Providing the list of followed podcasts for checking the subscribed status.
 *  - Managing the UI state of the Podcast Details screen.
 *  - Handling user interactions like subscribing/unsubscribing to a podcast,
 *  playing an episode, and adding an episode to the queue.
 *
 * @property handle [SavedStateHandle] to access and observe saved state parameters, such as the
 * podcast URI.
 * @property podcastStore [PodcastStore] to interact with the podcast data layer (e.g., fetching
 * podcasts, managing subscriptions).
 * @property episodeStore [EpisodeStore] to interact with the episode data layer (e.g., fetching
 * episodes for a podcast).
 * @property episodePlayer [EpisodePlayer] to manage the playback of episodes.
 */
@HiltViewModel
class PodcastDetailsScreenViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val podcastStore: PodcastStore,
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     * The URI of the podcast to be displayed.
     *
     * This value is retrieved from the [SavedStateHandle] using the [Screen.Podcast.PARAMETER_NAME]
     * key. It represents the unique identifier or location of the podcast content.
     * It can be `null` if no podcast URI was provided during navigation.
     */
    private val podcastUri: String? = handle.get<String>(Screen.Podcast.PARAMETER_NAME)

    /**
     * A [Flow] that emits the [Podcast] associated with the URI stored in the navigation handle,
     * or null if no URI is present.
     *
     * This flow is derived from the state flow retrieved from [SavedStateHandle] navigation
     * property [handle] using the key [Screen.Podcast.PARAMETER_NAME], which holds a podcast URI.     *
     *  - It first observes the state flow for changes in the podcast URI, identified by the
     *  [Screen.Podcast.PARAMETER_NAME] key.
     *  - When a non-null URI is received, it uses the [podcastStore] to fetch the corresponding
     *  [Podcast].
     *  - If the URI is null, it emits a flow containing a null value, representing the absence
     *  of a selected podcast.
     *  - It utilizes [Flow.flatMapLatest] to ensure that only the latest podcast URI is processed,
     *  canceling any previous ongoing operations.
     *
     * The flow emits `null` initially if no URI is provided in the navigation parameters.
     *
     * @see Screen.Podcast.PARAMETER_NAME
     * @see podcastStore
     * @see Podcast
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val podcastFlow: Flow<Podcast?> =
        handle.getStateFlow<String?>(key = Screen.Podcast.PARAMETER_NAME, initialValue = null)
            .flatMapLatest { podcastUri: String? ->
                if (podcastUri != null) {
                    podcastStore.podcastWithUri(uri = podcastUri)
                } else {
                    flowOf(value = null)
                }
            }

    /**
     * A flow of the list of episodes for the currently selected podcast.
     *
     * This flow emits a new [EpisodeList] whenever the currently selected podcast changes.
     * It retrieves the episodes from the [episodeStore] for the selected podcast's URI.
     * If no podcast is selected, it emits an empty [EpisodeList].
     *
     * The flow is constructed using:
     *   1. [podcastFlow]: A flow emitting the currently selected [Podcast] (or null if none is
     *   selected).
     *   2. [flatMapLatest]:  Ensures that only the latest podcast's episode list is observed. When
     *   a new podcast is emitted by [podcastFlow], any ongoing episode retrieval for the previous
     *   podcast is canceled, and a new one starts for the new podcast. The [Podcast] emitted is
     *   accepted in variable `podcast`, then
     *   3. Condition check: If `podcast` is not null, it fetches episodes from [episodeStore] using
     *   the podcast's `uri`. Otherwise it emits an empty list.
     *   4. `episodeStore.episodesInPodcast`: Retrieves a list of [EpisodeToPodcast] objects for
     *   the given `podcastUri`.
     *   5. [map]: Transforms the list of [EpisodeToPodcast] into an [EpisodeList] containing the
     *   mapped [PlayerEpisode].
     *   6. `toPlayerEpisode()`: A function on [EpisodeToPodcast] used to convert an
     *   [EpisodeToPodcast] to a [PlayerEpisode].
     *
     * The [EpisodeList] is essentially a wrapper around a list of [PlayerEpisode] objects.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodeListFlow: Flow<EpisodeList> = podcastFlow
        .flatMapLatest { podcast: Podcast? ->
            if (podcast != null) {
                episodeStore.episodesInPodcast(podcastUri = podcast.uri)
            } else {
                flowOf(value = emptyList())
            }
        }.map { list: List<EpisodeToPodcast> ->
            EpisodeList(member = list.map { it.toPlayerEpisode() })
        }

    /**
     * A [Flow] that emits a list of [PodcastWithExtraInfo] representing the podcasts
     * the user is currently subscribed to.  The list is sorted by the last published
     * episode date, with the most recently updated podcast appearing first.
     *
     * This flow is derived from the underlying [PodcastStore] property [podcastStore]
     * and provides a reactive stream of subscribed podcasts. Any changes to the user's
     * subscriptions or the latest episode details of followed podcasts will be reflected
     * in subsequent emissions from this flow.
     *
     * The list includes extra information about each podcast as described in
     * [PodcastWithExtraInfo].
     *
     * Emits:
     *  - A `List<PodcastWithExtraInfo>`: A list of subscribed podcasts, ordered by the last episode date.
     *  - An empty list (`listOf()`) is emitted if the user is not subscribed to any podcasts.
     */
    private val subscribedPodcastListFlow: Flow<List<PodcastWithExtraInfo>> =
        podcastStore.followedPodcastsSortedByLastEpisode()

    /**
     * The UI state for the Podcast screen.
     *
     * This [StateFlow] emits different [PodcastScreenUiState] instances based on the combined data
     * from the underlying flows: [podcastFlow], [episodeListFlow], and [subscribedPodcastListFlow].
     *
     * The flow combines these three sources:
     *  - [podcastFlow]: A flow that emits the [Podcast] details. It can be null if the podcast
     *  is not found.
     *  - [episodeListFlow]: A flow that emits the [EpisodeList] associated with the podcast.
     *  - [subscribedPodcastListFlow]: A flow that emits the list of [PodcastWithExtraInfo]
     *  the user is subscribed to.
     *
     * Emitted states:
     *  - [PodcastScreenUiState.Loading]: The initial state, indicating that data is being loaded.
     *  - [PodcastScreenUiState.Ready]: Emitted when the [podcastFlow] emits a non-null [Podcast],
     *  along with its [EpisodeList] and subscription state:
     *      - [PodcastScreenUiState.Ready.podcastInfo]: The [Podcast] details as an external model.
     *      - [PodcastScreenUiState.Ready.episodeList]: The [EpisodeList] of the podcast.
     *      - [PodcastScreenUiState.Ready.isSubscribed]: A boolean indicating if the user is
     *      subscribed to this podcast.
     *  - [PodcastScreenUiState.Error]: Emitted when the [podcastFlow] emits null, indicating that
     *  the podcast was not found.
     *
     * The [StateFlow] is configured with:
     *  - [viewModelScope]: The scope in which the flow is active.
     *  - [SharingStarted.WhileSubscribed]: The flow will start collecting when the first subscriber
     *  appears and will continue to collect while there are active subscribers. It will stop
     *  collecting after 5 seconds of inactivity (no subscribers).
     *  - [PodcastScreenUiState.Loading]: The initial value of the [StateFlow]
     */
    val uiStateFlow: StateFlow<PodcastScreenUiState> = combine(
        flow = podcastFlow,
        flow2 = episodeListFlow,
        flow3 = subscribedPodcastListFlow
    ) { podcast: Podcast?, episodeList: EpisodeList, subscribedPodcastList: List<PodcastWithExtraInfo> ->
        if (podcast != null) {
            val isSubscribed = subscribedPodcastList.any { it.podcast.uri == podcastUri }
            PodcastScreenUiState.Ready(podcast.asExternalModel(), episodeList, isSubscribed)
        } else {
            PodcastScreenUiState.Error
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = PodcastScreenUiState.Loading
    )

    /**
     * Subscribes to a podcast if its not already subscribed to.
     *
     * If `isSubscribed` is `false`, it means the user wants to subscribe to the podcast.
     * This function will then call `togglePodcastFollowed` in the `podcastStore` to update the
     * subscription status persistently.
     *
     * If `isSubscribed` is `true`, this function will do nothing, assuming the user has already subscribed.
     *
     * The subscription toggle operation is launched in the `viewModelScope` to ensure it's
     * lifecycle-aware and runs on a background thread.
     *
     * @param podcastInfo The information about the podcast to subscribe to.
     * @param isSubscribed A boolean indicating whether the user is already subscribed to the
     * podcast. `false` implies the user wants to subscribe. `true` implies the user is already
     * subscribed.
     */
    fun subscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        if (!isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcastUri = podcastInfo.uri)
            }
        }
    }

    /**
     * Unsubscribes from a podcast if the user is currently subscribed.
     *
     * This function takes a [PodcastInfo] object and a boolean indicating the current subscription
     * status. If the user is subscribed (i.e., [isSubscribed] is true), it triggers a coroutine to
     * update the podcast's subscription status in the [podcastStore]. The [podcastStore] will then
     * handle the logic of actually unsubscribing (e.g., removing the podcast from the user's
     * followed list).
     *
     * If the user is not subscribed, this function does nothing.
     *
     * @param podcastInfo The [PodcastInfo] object representing the podcast to unsubscribe from.
     * @param isSubscribed A boolean indicating whether the user is currently subscribed to the
     * podcast. If `true`, the unsubscribe action will be performed. If false, no `action` is taken.
     */
    fun unsubscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        if (isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcastUri = podcastInfo.uri)
            }
        }
    }

    /**
     * Initiates playback of a given episode.
     *
     * This function delegates the actual playback operation to the [EpisodePlayer] property
     * [episodePlayer].
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
     * Adds a [PlayerEpisode] to the playback queue.
     *
     * This function enqueues the provided [PlayerEpisode] to be played after the currently
     * playing episode (if any) or at the beginning of the queue if the queue is empty.
     *
     * @param playerEpisode The [PlayerEpisode] to be added to the queue.
     */
    fun enqueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(episode = playerEpisode)
    }
}

/**
 * Represents the UI state of the Podcast screen.
 *
 * This sealed interface defines the possible states the Podcast screen can be in,
 * including loading, error, and ready states with relevant data.
 */
sealed interface PodcastScreenUiState {
    /**
     * Represents the loading state of the podcast screen.
     *
     * This object signifies that the podcast screen is currently in a loading state,
     * fetching or processing data required for display.  No actual podcast or error
     * information is available during this state.
     *
     * It implements the [PodcastScreenUiState] interface, providing a concrete
     * implementation for the loading scenario within the screen's UI state management.
     */
    data object Loading : PodcastScreenUiState

    /**
     * Represents an error state in the Podcast Screen UI.
     * This state indicates that an error occurred while fetching or processing
     * podcast data. It can be used to display an error message to the user,
     * allowing them to retry or take other appropriate actions.
     *
     * This object is a concrete implementation of the [PodcastScreenUiState] sealed
     * class/interface.
     */
    data object Error : PodcastScreenUiState

    /**
     * Represents the UI state when the podcast information, episode list, and subscription status
     * are successfully loaded and ready to be displayed.
     *
     * @property podcastInfo Details about the podcast, such as title, author, and description.
     * @property episodeList A list of episodes associated with the podcast.
     * @property isSubscribed Indicates whether the user is currently subscribed to the podcast.
     */
    data class Ready(
        val podcastInfo: PodcastInfo,
        val episodeList: EpisodeList,
        val isSubscribed: Boolean
    ) : PodcastScreenUiState
}
