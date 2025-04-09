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

package com.example.jetcaster.ui.latest_episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.domain.GetLatestFollowedEpisodesUseCase
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Latest Episode screen.
 *
 * This ViewModel is responsible for fetching and providing the latest episodes
 * from the user's followed podcasts, and managing playback of those episodes.
 *
 * @property episodesFromFavouritePodcasts A use case for retrieving the latest episodes from
 * followed podcasts.
 * @property episodePlayer An interface for interacting with the episode player.
 */
@HiltViewModel
class LatestEpisodeViewModel @Inject constructor(
    episodesFromFavouritePodcasts: GetLatestFollowedEpisodesUseCase,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     *  The UI state for the Latest Episode screen.
     *
     *  This [StateFlow] emits a [LatestEpisodeScreenState] representing the current state
     *  of the UI, which can be one of:
     *  - [LatestEpisodeScreenState.Loading]: Indicates that the latest episodes are being loaded.
     *  - [LatestEpisodeScreenState.Loaded]: Indicates that the latest episodes have been successfully loaded.
     *    It contains a list of [PlayerEpisode] objects.
     *  - [LatestEpisodeScreenState.Empty]: Indicates that no latest episodes from favourite podcasts are available.
     *
     *  The state is derived from the [episodesFromFavouritePodcasts] flow, which emits a list of
     *  [EpisodeToPodcast] objects.  This list is then transformed into a [LatestEpisodeScreenState]
     *  based on whether the list is empty or not.
     *
     *  The flow is shared and cached using [stateIn] with the following configurations:
     *   - [viewModelScope]: The scope used for sharing the flow.
     *   - [SharingStarted.WhileSubscribed]: The sharing policy that keeps the flow active as long
     *   as there are active subscribers, and stops collecting data after 5 seconds of no subscribers.
     *   - [LatestEpisodeScreenState.Loading]: The initial value of the state flow, representing
     *   the initial loading state.
     */
    val uiState: StateFlow<LatestEpisodeScreenState> =
        episodesFromFavouritePodcasts.invoke().map { episodeToPodcastList: List<EpisodeToPodcast> ->
            if (episodeToPodcastList.isNotEmpty()) {
                LatestEpisodeScreenState.Loaded(
                    episodeToPodcastList.map {
                        it.toPlayerEpisode()
                    }
                )
            } else {
                LatestEpisodeScreenState.Empty
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LatestEpisodeScreenState.Loading,
        )

    /**
     * Starts playing a list of episodes.
     *
     * This function takes a list of [PlayerEpisode] objects and initiates playback.
     * It sets the first episode in the list as the [EpisodePlayer.currentEpisode] of
     * our [EpisodePlayer] property [episodePlayer].  Then, it calls [EpisodePlayer.play]
     * with the entire list of episodes to start playing the episodes.
     *
     * @param episodes A list of [PlayerEpisode] objects to be played. The first
     * episode in this list will be set as the [EpisodePlayer.currentEpisode] of
     * our [EpisodePlayer] property [episodePlayer].
     */
    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        episodePlayer.currentEpisode = episodes[0]
        episodePlayer.play(playerEpisodes = episodes)
    }

    /**
     * Initiates playback of a specified episode.
     *
     * This function sets the given [episode] as the currently active episode in the
     * [episodePlayer] and then starts playback.
     *
     * @param episode The [PlayerEpisode] object representing the episode to be played.
     */
    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }
}

/**
 * Represents the different states of the Latest Episode screen.
 * This sealed interface allows us to define a set of possible states
 * the screen can be in, ensuring all cases are handled in UI logic.
 */
sealed interface LatestEpisodeScreenState {

    /**
     * Represents the loading state of the latest episode screen.
     *
     * This state indicates that the application is currently fetching or processing data
     * required to display the latest episodes. It's a concrete implementation of the
     * [LatestEpisodeScreenState] sealed interface, representing one of the possible states
     * the screen can be in.
     *
     * When the screen is in this state, a loading indicator (e.g., a spinner or progress bar)
     * will be displayed to the user to signal that data is being loaded. No actual episode
     * content is available in this state.
     */
    data object Loading : LatestEpisodeScreenState

    /**
     * Represents the state of the Latest Episode screen when the episode list has been
     * successfully loaded.
     *
     * This state holds the list of [PlayerEpisode] objects that have been retrieved and are ready
     * to be displayed.
     *
     * @property episodeList The list of [PlayerEpisode] objects that have been loaded. This list r
     * epresents the episodes that should be shown to the user.
     */
    data class Loaded(
        val episodeList: List<PlayerEpisode>
    ) : LatestEpisodeScreenState

    /**
     * Represents the state of the [LatestEpisodeScreen] when there are no latest episodes to display.
     * This is a sealed object within the LatestEpisodeScreenState hierarchy, indicating an empty state.
     */
    data object Empty : LatestEpisodeScreenState
}
