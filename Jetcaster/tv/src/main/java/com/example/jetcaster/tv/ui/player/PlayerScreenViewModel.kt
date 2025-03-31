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

package com.example.jetcaster.tv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.PlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * [PlayerScreenViewModel] is the [ViewModel] responsible for managing the state and interactions of
 * the [PlayerScreen] player screen. It interacts with the [EpisodePlayer] to control the playback
 * of episodes and exposes the current player state to the UI through a [StateFlow].
 *
 * This ViewModel is annotated with @[HiltViewModel] to enable dependency injection with Hilt.
 *
 * @property episodePlayer The [EpisodePlayer] instance used to manage episode playback injected by
 * Hilt thanks to the @[Inject] annotation on the constructor.
 */
@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     * A [StateFlow] that represents the UI state of the player screen.
     *
     * This flow emits different states based on the underlying [EpisodePlayer.playerState] of our
     * [EpisodePlayer] parameter [episodePlayer].
     * It handles cases where:
     *   - There are no episodes in the queue, emitting a [PlayerScreenUiState.NoEpisodeInQueue].
     *   - The player is ready to play with an episode in the queue, emitting a
     *   [PlayerScreenUiState.Ready] containing the current [EpisodePlayerState].
     *   - The player is still loading and not yet ready, emitting a [PlayerScreenUiState.Loading].
     *
     * The flow is started using [SharingStarted.WhileSubscribed] with a stop timeout of 5 seconds.
     * This means that the flow will only be active and collecting values as long as there are
     * subscribers. If there are no subscribers for 5 seconds, the flow will stop collecting and
     * emitting values.
     *
     * It is initially set to [PlayerScreenUiState.Loading]
     */
    val uiStateFlow: StateFlow<PlayerScreenUiState> = episodePlayer.playerState.map {
        if (it.currentEpisode == null && it.queue.isEmpty()) {
            PlayerScreenUiState.NoEpisodeInQueue
        } else {
            PlayerScreenUiState.Ready(playerState = it)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = PlayerScreenUiState.Loading
    )

    /**
     * The default amount of time to skip forward or backward when skipping.
     *
     * This property defines the [Duration] (in this case, 10 seconds) that the media player will
     * move forward or backward when a "skip" action is performed (e.g., by pressing a skip button).
     *
     * The value is currently set to 10 seconds.
     */
    private val skipAmount: Duration = Duration.ofSeconds(10L)

    /**
     * Plays the current episode or the next episode if no episode is currently loaded.
     *
     * This function checks if there's an episode currently loaded in the [EpisodePlayer] property
     * [episodePlayer].
     * If there isn't (i.e., the [EpisodePlayerState.currentEpisode] is null), it calls the
     * [EpisodePlayer.next] method of the [EpisodePlayer] property [episodePlayer]
     * to load the next episode in the queue. After (potentially) loading a new episode,
     * it calls [EpisodePlayer.play] method of [episodePlayer] to start playback.
     *
     * This ensures that playback starts correctly, whether or not an episode was
     * already loaded.
     */
    fun play() {
        if (episodePlayer.playerState.value.currentEpisode == null) {
            episodePlayer.next()
        }
        episodePlayer.play()
    }

    /**
     * Initiates playback of a given episode.
     *
     * This function delegates the actual playback operation to the [EpisodePlayer.play] method of
     * the [EpisodePlayer] property [episodePlayer].
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
     * Pauses the currently playing episode.
     *
     * This function delegates the pause operation to the [EpisodePlayer.pause] method of our
     * [EpisodePlayer] property [episodePlayer].
     * Calling this function will halt the playback of the current episode until [play] is called.
     *
     * @see play
     */
    fun pause(): Unit = episodePlayer.pause()

    /**
     * Advances the episode player to the next episode.
     *
     * This function delegates the task of moving to the next episode to the [EpisodePlayer.next]
     * method of our [EpisodePlayer] property [episodePlayer]. It does not perform any specific
     * logic beyond that.
     *
     * @see EpisodePlayer.next
     */
    fun next(): Unit = episodePlayer.next()

    /**
     * Navigates the episode player to the previous episode.
     *
     * This function calls the [EpisodePlayer.previous] method of our [EpisodePlayer] property
     * [episodePlayer] effectively instructing it to play or prepare for playback of the episode
     * that precedes the currently active episode in the playlist queue.
     *
     * The specific behavior of "previous" (e.g., wrapping around to the end of the list,
     * ignoring the call if already at the beginning, etc.) is determined by the
     * implementation of the [EpisodePlayer].
     */
    fun previous(): Unit = episodePlayer.previous()

    /**
     * Skips forward in the current episode by a predefined amount.
     *
     * This function utilizes the [EpisodePlayer.advanceBy] method of [EpisodePlayer] property
     * [episodePlayer] to advance the playback position by the [Duration] specified in [Duration]
     * property [skipAmount] (10 seconds).
     */
    fun skip() {
        episodePlayer.advanceBy(duration = skipAmount)
    }

    /**
     * Rewinds the currently playing episode by a predefined skip amount.
     *
     * This function utilizes the [EpisodePlayer.rewindBy] method of [EpisodePlayer] property
     * [episodePlayer] to move the playback position backward. The amount of time skipped is
     * determined by the [Duration] property [skipAmount] (10 seconds)
     *
     * @see EpisodePlayer.rewindBy for details on how the rewinding is performed.
     * @see skipAmount the duration to be skipped when rewinding
     */
    fun rewind() {
        episodePlayer.rewindBy(duration = skipAmount)
    }

    /**
     * Adds a [PlayerEpisode] to the playback queue.
     *
     * This function enqueues the [PlayerEpisode] parameter [playerEpisode] into the playback queue
     * to be played after the currently playing episode (if any) or at the beginning of the queue if
     * the queue is empty. It utilizes the [EpisodePlayer.addToQueue] method of our [EpisodePlayer]
     * property [episodePlayer].
     *
     * @param playerEpisode The [PlayerEpisode] to be added to the queue.
     */
    fun enqueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(episode = playerEpisode)
    }
}

/**
 * Represents the UI state of the player screen.
 *
 * This sealed interface defines the different states the player screen can be in,
 * such as loading, ready to play with episode information, or having no episodes in the queue.
 */
sealed interface PlayerScreenUiState {
    /**
     * Represents the "Loading" state of the player screen UI.
     * This state indicates that the necessary data for the player screen
     * is currently being fetched or initialized.  The UI should typically
     * display a loading indicator or placeholder content while in this state.
     *
     * This is a sealed class data object used as a state in UI.
     *
     * This state should be replaced by a different state when the loading
     * process is completed or an error occurs.
     */
    data object Loading : PlayerScreenUiState

    /**
     * Represents the "Ready" state of the player screen UI.
     *
     * This state indicates that the player is prepared to start playback and is
     * currently displaying the relevant information for the episode.  It contains
     * the current state of the episode player.
     *
     * @property playerState The current state of the episode player. This provides details about
     * the playback status, like whether the episode is paused, buffering, or ready to play.
     */
    data class Ready(
        val playerState: EpisodePlayerState
    ) : PlayerScreenUiState

    /**
     * Represents the UI state when there are no episodes in the playback queue.
     * This state indicates that the player is idle because there's nothing to play.
     * It's a specific state within the broader [PlayerScreenUiState] hierarchy.
     *
     * Typically, this state would be displayed when:
     *  - The user has not added any episodes to the playback queue.
     *  - The playback queue was previously populated but has now been cleared.
     *  - The player has finished playing the last episode in the queue and is waiting for more.
     *
     * This state provides a clear signal to the UI to display a message or other
     * relevant content indicating the empty queue status. For instance, the UI can
     * guide the user to add episodes.
     */
    data object NoEpisodeInQueue : PlayerScreenUiState
}
