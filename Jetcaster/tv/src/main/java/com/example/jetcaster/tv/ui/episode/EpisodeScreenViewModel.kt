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
     * is the [viewModelScope]. The `started` parameter is set to [SharingStarted.WhileSubscribed]
     * with a 5 second `stopTimeoutMillis` value. The `initialValue` parameter is set to `null`.
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

    fun addPlayList(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode = episode)
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode = playerEpisode)
    }

    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }
}

sealed interface EpisodeScreenUiState {
    data object Loading : EpisodeScreenUiState
    data object Error : EpisodeScreenUiState
    data class Ready(val playerEpisode: PlayerEpisode) : EpisodeScreenUiState
}
