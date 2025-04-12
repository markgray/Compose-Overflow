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

package com.example.jetcaster.ui.podcast

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

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.ui.PodcastDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * [ViewModel] that handles the business logic and screen state for the [PodcastDetails] screen.
 *
 * This ViewModel is responsible for:
 *  - Fetching and providing data related to a specific podcast, including its details and episodes.
 *  - Handling user interactions related to playing episodes.
 *  - Managing the state of the [PodcastDetails] screen.
 *
 * @property savedStateHandle [SavedStateHandle] to access the saved state, including the podcast URI.
 * @property episodeStore [EpisodeStore] to access and manage episodes related to a podcast.
 * @property episodePlayer [EpisodePlayer] to control the playback of episodes.
 * @property podcastStore [PodcastStore] to access and manage podcast information.
 */
@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    podcastStore: PodcastStore
) : ViewModel() {

    /**
     * The URI of the podcast.
     *
     * This is retrieved from the [SavedStateHandle] using the key [PodcastDetails.PODCAST_URI].
     * The value is expected to be a URL-encoded string, which is then decoded using [Uri.decode].
     *
     * This URI is used to identify and access the podcast's RSS feed or associated resources.
     */
    private val podcastUri: String =
        savedStateHandle.get<String>(PodcastDetails.PODCAST_URI).let {
            Uri.decode(it)
        }

    /**
     * A [StateFlow] that emits the [PodcastWithExtraInfo] associated with the given [podcastUri].
     *
     * If [podcastUri] is null, this flow emits a single null value.
     *
     * This flow is backed by the [podcastStore] and is converted to a [StateFlow] using [stateIn].
     * It shares the latest value with all subscribers and retains it even when there are no active
     * subscribers, thanks to [SharingStarted.WhileSubscribed].
     *
     * The value is retained for 5 seconds after the last subscriber disappears, which is defined
     * by the 5_000ms `stopTimeoutMillis` argument of [SharingStarted.WhileSubscribed].
     *
     * The initial value of this [StateFlow] is null.
     *
     * This allows for efficient observation of podcast data changes.
     *
     * It is important to note that the flow is started in the [viewModelScope]. This ensures that
     * the coroutine managing this flow is cancelled when the ViewModel is cleared.
     *
     * @see [PodcastStore.podcastWithExtraInfo]
     * @see [SharingStarted.WhileSubscribed]
     * @see [stateIn]
     * @see [viewModelScope]
     */
    @Suppress("SENSELESS_COMPARISON")
    private val podcastFlow: StateFlow<PodcastWithExtraInfo?> = if (podcastUri != null) {
        podcastStore.podcastWithExtraInfo(podcastUri = podcastUri)
    } else {
        flowOf(value = null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = null
    )

    /**
     * A flow emitting a list of [PlayerEpisode]s.
     *
     * This flow is derived from the [podcastFlow] and provides the list of episodes
     * associated with the currently selected podcast.
     *  - It uses [flatMapLatest] to reactively switch to a new episode list whenever a
     *  new podcast is emitted by [podcastFlow].
     *  - If [podcastFlow] emits `null` (meaning no podcast is selected), this flow
     *  emits an empty list.
     *  - Otherwise, it fetches the episodes from the [episodeStore] for the given
     *  podcast's URI.
     *  - Finally, it maps each [EpisodeToPodcast] to a [PlayerEpisode] for use in the player.
     *
     * The flow uses [ExperimentalCoroutinesApi] because it's leveraging `flatMapLatest`.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodeListFlow: Flow<List<PlayerEpisode>> = podcastFlow
        .flatMapLatest { podcast: PodcastWithExtraInfo? ->
            if (podcast != null) {
                episodeStore.episodesInPodcast(podcastUri = podcast.podcast.uri)
            } else {
                flowOf(value = emptyList())
            }
        }.map { list: List<EpisodeToPodcast> ->
            list.map { it.toPlayerEpisode() }
        }

    /**
     * The current UI state of the Podcast Details screen.
     *
     * This [StateFlow] emits the current state based on the combined results of the
     * [podcastFlow] and [episodeListFlow]. It represents one of the following states:
     *  - [PodcastDetailsScreenState.Loading]: The initial state, indicating that data is being
     *  loaded.
     *  - [PodcastDetailsScreenState.Loaded]: The state when both the podcast and its episodes are
     *  loaded. It contains the podcast details (the [PodcastInfo] property `podcast`) and the list
     *  of episodes (the [List] of [PlayerEpisode] property `episodeList`). The podcast details
     *  include a flag `isSubscribed` indicating whether the user is subscribed to it.
     *  - [PodcastDetailsScreenState.Empty]: The state when no podcast data is available (e.g.,
     *  due to an error or no matching podcast found).
     *
     * This [StateFlow] is derived by combining:
     *  - [podcastFlow]: A flow emitting a `PodcastWithExtraInfo` object or null.
     *  - [episodeListFlow]: A flow emitting a list of `PlayerEpisode`.
     *
     * It uses [combine] to merge these two flows and determine the appropriate
     * [PodcastDetailsScreenState].
     * The flow is converted into a [StateFlow] using [stateIn], ensuring that it:
     *  - Shares the latest emitted value.
     *  - Starts emitting when there is at least one subscriber.
     *  - Stops emitting after 5 seconds of no subscribers.
     *  - Has an initial value of [PodcastDetailsScreenState.Loading].
     */
    val uiState: StateFlow<PodcastDetailsScreenState> =
        combine(
            flow = podcastFlow,
            flow2 = episodeListFlow
        ) { podcast: PodcastWithExtraInfo?, episodes: List<PlayerEpisode> ->
            if (podcast != null) {
                PodcastDetailsScreenState.Loaded(
                    podcast = podcast.podcast.asExternalModel()
                        .copy(isSubscribed = podcast.isFollowed),
                    episodeList = episodes,
                )
            } else {
                PodcastDetailsScreenState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = PodcastDetailsScreenState.Loading,
        )

    /**
     * Starts playing a list of episodes.
     *
     * This function takes a [List] of [PlayerEpisode] objects and initiates playback.
     * It sets the first episode in the list as the current episode and then
     * instructs the episode player to start playing the entire list.
     *
     * @param episodes A list of [PlayerEpisode] objects to be played. The first
     * episode in this list will be set as the `currentEpisode`.
     */
    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        episodePlayer.currentEpisode = episodes[0]
        episodePlayer.play(playerEpisodes = episodes)
    }
}
