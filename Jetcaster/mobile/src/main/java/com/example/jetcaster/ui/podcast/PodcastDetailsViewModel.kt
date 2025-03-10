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

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Represents the UI state of a podcast screen.
 *
 * This sealed interface defines the two possible states the podcast UI can be in,
 * [Loading] or [Ready] with data.
 */
sealed interface PodcastUiState {
    data object Loading : PodcastUiState
    data class Ready(
        val podcast: PodcastInfo,
        val episodes: List<EpisodeInfo>,
    ) : PodcastUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Podcast details screen.
 *
 * This ViewModel fetches and manages data related to a specific podcast, including its details and
 * episodes. It interacts with [EpisodeStore], [EpisodePlayer], and [PodcastStore] to retrieve and
 * update data.
 *
 * @property episodeStore The data layer responsible for managing episodes.
 * @property episodePlayer The player responsible for managing episode playback.
 * @property podcastStore The data layer responsible for managing podcasts.
 * @property podcastUri The URI of the podcast being displayed. This is passed in via assisted
 * injection.
 */
@HiltViewModel(assistedFactory = PodcastDetailsViewModel.Factory::class)
class PodcastDetailsViewModel @AssistedInject constructor(
    private val episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    private val podcastStore: PodcastStore,
    @Assisted private val podcastUri: String,
) : ViewModel() {

    /**
     * The decoded URI of the podcast.
     *
     * This property holds the podcast URI after it has been decoded using [Uri.decode].
     * Decoding is necessary to handle special characters or encoded parts within the URI,
     * ensuring that the URI is in a standard, usable format.
     *
     * Example:
     * If `podcastUri` is "https%3A%2F%2Fexample.com%2Fpodcast%3Fid%3D123",
     * then `decodedPodcastUri` will be "https://example.com/podcast?id=123".
     */
    private val decodedPodcastUri: String = Uri.decode(podcastUri)

    /**
     * This property is a [StateFlow] that represents the current state of the podcast screen.
     *
     * We use the [combine] function to combine two [Flow]s:
     *  - [PodcastStore.podcastWithExtraInfo] of [PodcastStore] field [podcastStore] for the podcast
     *  whose `podcastUri` is our [String] variable `decodedPodcastUri`. Returns a [Flow] containing
     *  the [PodcastWithExtraInfo] of the podcast.
     *  - [EpisodeStore.episodesInPodcast] of [EpisodeStore] field [episodeStore] for the episodes
     *  whose `podcastUri` is our [String] variable `decodedPodcastUri`. Returns a [Flow] containing
     *  lists of [EpisodeToPodcast] objects.
     *
     * In the `transform` lambda, we accept the latest [PodcastWithExtraInfo] in variable `podcast`,
     * and the latest [List] of [EpisodeToPodcast] objects in variable `episodeToPodcasts`. We then
     * initialize our [List] of [EpisodeInfo] variable `episodes` with the list of [EpisodeInfo] that
     * the [Iterable.map] function of our [List] of [EpisodeToPodcast] variable `episodeToPodcasts`
     * creates when it iterates over each [EpisodeToPodcast] calling its [Episode.asExternalModel]
     * method. The lambda returns a [PodcastUiState.Ready] object with the [PodcastInfo] `podcast`
     * of the [PodcastUiState.Ready] created by calling its [PodcastWithExtraInfo.asExternalModel],
     * making a copy with the [PodcastInfo.isSubscribed] field set to the
     * [PodcastWithExtraInfo.isFollowed] property of the [PodcastWithExtraInfo] variable `podcast`.
     * The [EpisodeInfo] `episodes` of the [PodcastUiState.Ready] object are set to our [List] of
     * [EpisodeInfo] variable `episodes`.
     *
     * We then use the [Flow.stateIn] function to convert the [Flow] of [PodcastUiState.Ready]
     * that is created by the `transform` lambda into a [StateFlow] with its `scope` set to the
     * [viewModelScope], `started` set to [SharingStarted.WhileSubscribed] with a `stopTimeoutMillis`
     * of 5,000, and an `initialValue` of [PodcastUiState.Loading].
     */
    val state: StateFlow<PodcastUiState> =
        combine(
            flow = podcastStore.podcastWithExtraInfo(podcastUri = decodedPodcastUri),
            flow2 = episodeStore.episodesInPodcast(podcastUri = decodedPodcastUri)
        ) { podcast: PodcastWithExtraInfo, episodeToPodcasts: List<EpisodeToPodcast> ->
            val episodes: List<EpisodeInfo> = episodeToPodcasts.map { it.episode.asExternalModel() }
            PodcastUiState.Ready(
                podcast = podcast.podcast.asExternalModel().copy(isSubscribed = podcast.isFollowed),
                episodes = episodes,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = PodcastUiState.Loading
        )

    /**
     * Toggles the podcast subscription status of its [PodcastInfo] parameter [podcast]. We use
     * [viewModelScope] to launch a coroutine that calls the [PodcastStore.togglePodcastFollowed]
     * method of the [PodcastStore] field [podcastStore] with the [PodcastInfo.uri] of the
     * [PodcastInfo] parameter [podcast].
     */
    fun toggleSusbcribe(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastUri = podcast.uri)
        }
    }

    /**
     * Adds a [PlayerEpisode] to the playback queue.
     *
     * This function takes a [PlayerEpisode] object and adds it to the end of the
     * current playback queue managed by the [episodePlayer].
     *
     * We just call the [EpisodePlayer.addToQueue] method of our [EpisodePlayer] field [episodePlayer]
     * with our [PlayerEpisode] parameter [playerEpisode].
     *
     * @param playerEpisode The [PlayerEpisode] to be added to the queue. This object contains the
     * necessary information about the episode to be played, such as its media URL, title, and any
     * other relevant metadata.
     */
    fun onQueueEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(episode = playerEpisode)
    }

    /**
     * Factory for creating instances of [PodcastDetailsViewModel]. The [AssistedFactory] annotation
     * anotates an abstract class or interface used to create an instance of a type via an
     * [AssistedInject] constructor.
     *
     * An [AssistedFactory]-annotated type must obey the following constraints:
     *  - The type must be an abstract class or interface,
     *  - The type must contain exactly one abstract, non-default method whose return type must
     *  exactly match the type of an assisted injection type, and
     *  - parameters must match the exact list of Assisted parameters in the assisted injection
     *  type's constructor (and in the same order).
     */
    @AssistedFactory
    interface Factory {
        fun create(podcastUri: String): PodcastDetailsViewModel
    }
}
