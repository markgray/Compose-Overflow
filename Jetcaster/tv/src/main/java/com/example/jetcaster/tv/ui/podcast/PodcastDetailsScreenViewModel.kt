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

    private val subscribedPodcastListFlow: Flow<List<PodcastWithExtraInfo>> =
        podcastStore.followedPodcastsSortedByLastEpisode()

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

    fun subscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        if (!isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcastUri = podcastInfo.uri)
            }
        }
    }

    fun unsubscribe(podcastInfo: PodcastInfo, isSubscribed: Boolean) {
        if (isSubscribed) {
            viewModelScope.launch {
                podcastStore.togglePodcastFollowed(podcastUri = podcastInfo.uri)
            }
        }
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode = playerEpisode)
    }

    fun enqueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(episode = playerEpisode)
    }
}

sealed interface PodcastScreenUiState {
    data object Loading : PodcastScreenUiState
    data object Error : PodcastScreenUiState
    data class Ready(
        val podcastInfo: PodcastInfo,
        val episodeList: EpisodeList,
        val isSubscribed: Boolean
    ) : PodcastScreenUiState
}
