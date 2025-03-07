/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetcaster.ui.player

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Represents the UI state of the player.
 *
 * This class holds the current state of the player UI, including the state of the episode player.
 * It encapsulates all the information needed to render the player UI components correctly.
 *
 * @property episodePlayerState The state of the episode player, providing information about the
 * currently playing episode, playback position, buffering status, etc. Defaults to an empty
 * [EpisodePlayerState] if no episode is being played or no state has been set.
 */
data class PlayerUiState(
    val episodePlayerState: EpisodePlayerState = EpisodePlayerState()
)

/**
 * [PlayerViewModel] is the ViewModel that handles the business logic and screen state of the
 * Player screen.
 *
 * It interacts with the [EpisodeStore] to retrieve episode and podcast data, and the
 * [EpisodePlayer] to control the playback of episodes.
 *
 * The [PlayerViewModel] handles the following:
 * - Retrieving episode data based on a provided URI.
 * - Managing the playback state (playing, paused, stopped).
 * - Handling user interactions like play, pause, stop, next, previous, seek, etc.
 * - Maintaining and updating the [PlayerUiState] which reflects the current state of the player.
 * - Adding the current episode to the queue.
 *
 * @property episodeStore The data store for retrieving episode and podcast information.
 * @property episodePlayer The player responsible for controlling episode playback.
 * @property savedStateHandle The SavedStateHandle for retrieving arguments passed to the screen,
 * specifically the episode URI.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * The URI of the episode being displayed.
     *
     * This value is retrieved from the saved state handle using the key [Screen.ARG_EPISODE_URI].
     * It's expected to be a string representing a URI, which is then decoded using [Uri.decode].
     * 
     * [episodeUri] should always be present in the PlayerViewModel.
     * If that's not the case, fail crashing the app!
     *
     * @see Screen.ARG_EPISODE_URI
     * @see Uri.decode
     */
    private val episodeUri: String =
        Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)

    /**
     * The current state of the player's UI.
     *
     * This state object holds all the necessary information to render the player's UI, such as
     * whether the player is playing, the current playback position, the duration of the media,
     * and any other UI-related states.
     *
     * The UI recomposes whenever a property inside the [PlayerUiState] changes.
     *
     *  @see PlayerUiState for the details of the UI-related state properties.
     */
    var uiState: PlayerUiState by mutableStateOf(value = PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            episodeStore.episodeAndPodcastWithUri(episodeUri).flatMapConcat {
                episodePlayer.currentEpisode = it.toPlayerEpisode()
                episodePlayer.playerState
            }.map {
                PlayerUiState(episodePlayerState = it)
            }.collect {
                uiState = it
            }
        }
    }

    /**
     * Initiates playback of the currently loaded episode.
     *
     * This function calls the [EpisodePlayer.play] method of our [EpisodePlayer] field
     * [episodePlayer] which starts or resumes the playback of the audio content.
     */
    fun onPlay() {
        episodePlayer.play()
    }

    /**
     * Pauses the episode playback.
     *
     * This function calls the [EpisodePlayer.pause] method of our [EpisodePlayer] field
     * [episodePlayer] which pauses the playback of the audio content.
     */
    fun onPause() {
        episodePlayer.pause()
    }

    /**
     * Stops the episode player.
     *
     * This function calls the [EpisodePlayer.stop] method of our [EpisodePlayer] field
     * [episodePlayer] which stops the playback of the audio content.
     */
    fun onStop() {
        episodePlayer.stop()
    }

    /**
     * Navigates to the previous episode in the playback queue.
     *
     * This function calls the [EpisodePlayer.previous] method of our [EpisodePlayer] field
     * [episodePlayer] which will play the previous episode in the queue, or if an episode is
     * currently playing this will start the episode from the beginning.
     *
     * @see EpisodePlayer.previous
     */
    fun onPrevious() {
        episodePlayer.previous()
    }

    /**
     * Advances the episode player to the next episode in the playlist.
     *
     * This function calls the [EpisodePlayer.next] method of our [EpisodePlayer] field
     * [episodePlayer] which will play the next episode in the queue (if available).
     */
    fun onNext() {
        episodePlayer.next()
    }

    /**
     * Advances the episode player's playback position by the [Duration] parameter [duration].
     *
     * This function calls the [EpisodePlayer.advanceBy] method of our [EpisodePlayer] field
     * [episodePlayer] which will advance the playback position of the currently played episode
     * by the time interval specified by [Duration] 
     *
     * @param duration The [Duration] by which to advance the playback position. This can be a
     * positive or negative duration. A positive duration will advance the playback forward,
     * while a negative duration will rewind the playback.
     */
    fun onAdvanceBy(duration: Duration) {
        episodePlayer.advanceBy(duration = duration)
    }

    /**
     * Rewinds the currently playing episode by the [Duration] parameter [duration].
     *
     * This function calls the [EpisodePlayer.rewindBy] method of our [EpisodePlayer] field
     * [episodePlayer] which will rewind the playback position of the currently played episode
     * by the time interval specified by [Duration] argument [duration].
     *
     * @param duration The [Duration] to rewind by.
     */
    fun onRewindBy(duration: Duration) {
        episodePlayer.rewindBy(duration = duration)
    }

    /**
     * Called when the user starts seeking within the audio playback.
     *
     * This function calls the [EpisodePlayer.onSeekingStarted] method of our [EpisodePlayer] field
     * [episodePlayer] when the user starts seeking within the audio playback.
     *
     * This event can be useful for:
     * - Pausing any visualizations or animations that should be synchronized with the audio.
     * - Showing a loading indicator or other feedback to the user that the position is changing.
     * - Updating the UI to reflect the seeking state.
     *
     * @see EpisodePlayer.onSeekingStarted
     */
    fun onSeekingStarted() {
        episodePlayer.onSeekingStarted()
    }

    /**
     * Called when the user has finished seeking to a new position in the episode.
     *
     * This function is a delegate that forwards the seeking finished event and the new
     * duration to the [EpisodePlayer.onSeekingFinished] method of our [EpisodePlayer] field
     * [episodePlayer]. It should be invoked after the user interaction related to seeking
     * (e.g., dragging a seek bar, skipping forward/backward) has completed, and the playback
     * is expected to resume from the newly selected position.
     *
     * @param duration The new playback position that the user has seeked to.
     */
    fun onSeekingFinished(duration: Duration) {
        episodePlayer.onSeekingFinished(duration = duration)
    }

    /**
     * Adds the currently selected episode to the playback queue.
     *
     * If the [EpisodePlayerState.currentEpisode] of our [PlayerUiState] is not `null`, we use the
     * [let] extension function to add it to the playback queue using the [EpisodePlayer.addToQueue]
     * method of our [EpisodePlayer] field [episodePlayer].
     */
    fun onAddToQueue() {
        uiState.episodePlayerState.currentEpisode?.let { newEpisode: PlayerEpisode ->
            episodePlayer.addToQueue(episode = newEpisode)
        }
    }
}
