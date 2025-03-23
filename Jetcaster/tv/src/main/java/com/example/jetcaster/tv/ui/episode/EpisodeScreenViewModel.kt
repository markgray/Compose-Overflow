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

package com.example.jetcaster.tv.ui.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.tv.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Episode Screen.
 *
 * [HiltViewModel]: This annotation from the Hilt library indicates that this class is a [ViewModel]
 * and that Hilt should handle its dependency injection.
 *
 * [Inject] constructor(...): This indicates that the constructor is used for dependency injection.
 * Hilt will automatically provide instances of the dependencies listed in the constructor.
 *
 * @property handle: This is an instance of [SavedStateHandle], a key-value map that holds saved
 * state data. It's particularly useful for persisting UI-related data across process death and
 * configuration changes (e.g., screen rotation). This is a best practice when dealing with
 * ViewModels.
 * @property podcastsRepository: An instance of [PodcastsRepository], a repository class that is
 * responsible for fetching and managing podcast data.
 * @property episodeStore: An instance of [EpisodeStore] for managing [Episode] instances and their
 * relationships with [Podcast] instances.
 * @property episodePlayer: An instance of [EpisodePlayer], a class responsible for playing episodes.
 */
@HiltViewModel
class EpisodeScreenViewModel @Inject constructor(
    handle: SavedStateHandle,
    podcastsRepository: PodcastsRepository,
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     * A [StateFlow] of [String] that holds the URI of the episode currently being displayed. The
     * [SavedStateHandle.getStateFlow] function is used to create a [StateFlow] that will emit the
     * currently active value associated with the key [Screen.Episode.PARAMETER_NAME] with an
     * initial value of `null`.
     */
    private val episodeUriFlow: StateFlow<String?> =
        handle.getStateFlow<String?>(key = Screen.Episode.PARAMETER_NAME, initialValue = null)

    /**
     * A [StateFlow] of [EpisodeToPodcast] that is derived from [episodeUriFlow]. It uses the
     * [Flow.flatMapLatest] method of [episodeUriFlow] to transform the episodeUriFlow, ensuring
     * that only the latest value is processed and any ongoing [Flow] is canceled. In the `transform`
     * lambda argument it accepts the [String] passed the lambda in variable `it` and branches on
     * the value of `it`:
     *  - If `it` is `null`, it emits a [Flow] of `null`.
     *  - If `it` is not `null`, it calls the [EpisodeStore.episodeAndPodcastWithUri] method of
     *  [EpisodeStore] property [episodeStore] with the value of `it` as an argument and returns
     *  the [Flow] of [EpisodeToPodcast] returned by the method to the `transform` lambda.
     *
     * This is chained to a call to the [Flow.stateIn] method to create a [StateFlow] whose `scope`
     * is the [viewModelScope], its `started` parameter is set to [SharingStarted.WhileSubscribed]
     * with a 5 second `stopTimeoutMillis` value, and the `initialValue` parameter is set to `null`.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodeToPodcastFlow: StateFlow<EpisodeToPodcast?> = episodeUriFlow.flatMapLatest {
        if (it != null) {
            episodeStore.episodeAndPodcastWithUri(episodeUri = it)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = null
    )

    /**
     * A [StateFlow] of [EpisodeScreenUiState] that is derived from [episodeToPodcastFlow]. It uses
     * the [Flow.map] method of [episodeToPodcastFlow] to transform the [EpisodeToPodcast] returned
     * ensuring that only the latest value is processed and any ongoing [Flow] is canceled when it
     * changes value. In the `map` lambda argument it accepts the [EpisodeToPodcast] instance passed
     * the lambda in variable `it` and branches on the value of `it`:
     *  - If `it` is `null`, it emits a [EpisodeScreenUiState.Error] instance.
     *  - If `it` is not `null`, it calls the [EpisodeToPodcast.toPlayerEpisode] method of `it` for
     *  conversion to a [PlayerEpisode] and returns a [EpisodeScreenUiState.Ready] instance with the
     *  `playerEpisode` parameter set to the converted [PlayerEpisode].
     *
     * This is chained to a call to the [Flow.stateIn] method to create a [StateFlow] whose `scope`
     * is the [viewModelScope], its `started` parameter is set to [SharingStarted.WhileSubscribed]
     * with a 5 second `stopTimeoutMillis` value, and the `initialValue` parameter is set to
     * [EpisodeScreenUiState.Loading].
     */
    val uiStateFlow: StateFlow<EpisodeScreenUiState> = episodeToPodcastFlow.map {
        if (it != null) {
            EpisodeScreenUiState.Ready(playerEpisode = it.toPlayerEpisode())
        } else {
            EpisodeScreenUiState.Error
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = EpisodeScreenUiState.Loading
    )

    /**
     * Adds a new episode to the playback queue.
     *
     * This function takes a [PlayerEpisode] object and appends it to the end of the queue managed
     * by our [EpisodePlayer] property [episodePlayer]. Subsequent playback will include this added
     * episode.
     *
     * @param episode The [PlayerEpisode] object to be added to the queue.
     */
    fun addPlayList(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode = episode)
    }

    /**
     * Initiates playback of a given episode.
     *
     * This function delegates the actual playback operation to our [EpisodePlayer] property
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
     * Initializes the ViewModel by updating podcasts using the [PodcastsRepository.updatePodcasts]
     * method of our [PodcastsRepository] property [podcastsRepository].
     */
    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }
}

/**
 * Represents the UI state of the episode screen.
 *
 * This sealed interface defines the possible states the episode screen can be in,
 * including loading, error, and ready with the episode data.
 */
sealed interface EpisodeScreenUiState {
    /**
     * Represents the loading state of the episode screen.
     *
     * This object indicates that data for the episode screen is currently being fetched
     * and is not yet available to be displayed. It is one of the possible states
     * of the [EpisodeScreenUiState] sealed interface.
     */
    data object Loading : EpisodeScreenUiState

    /**
     * Represents an error state in the Episode Screen UI.
     * This state indicates that an error occurred while fetching or processing
     * episode data. It can be used to display an error message or a retry button.
     *
     * This is a sealed data object, part of the [EpisodeScreenUiState] sealed interface,
     * meaning it represents one of the possible states the UI can be in.
     */
    data object Error : EpisodeScreenUiState

    /**
     * Represents the UI state when the player is ready to start playing an episode.
     *
     * This state indicates that all necessary data for playing the episode has been loaded
     * and the player is prepared to begin playback. It contains information about the
     * specific episode that is ready to be played.
     *
     * @property playerEpisode The [PlayerEpisode] instance representing the episode that is
     * ready to play.
     */
    data class Ready(val playerEpisode: PlayerEpisode) : EpisodeScreenUiState
}
