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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.EpisodePlayerState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.state.model.TrackPositionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * UI state for the Player screen.
 *
 * Contains information about the current playback state, including the current
 * [EpisodePlayerState] and the [TrackPositionUiModel].
 *
 * @property episodePlayerState The current playback state of the episode. Defaults to an empty
 * [EpisodePlayerState].
 * @property trackPositionUiModel The current track position, represented as a [TrackPositionUiModel].
 * Defaults to [TrackPositionUiModel.Actual.ZERO], indicating the beginning of the track.
 */
@OptIn(ExperimentalHorologistApi::class)
data class PlayerUiState(
    val episodePlayerState: EpisodePlayerState = EpisodePlayerState(),
    var trackPositionUiModel: TrackPositionUiModel = TrackPositionUiModel.Actual.ZERO
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen.
 *
 * @property episodePlayer The [EpisodePlayer] used to manage the playback of episodes.
 */
@HiltViewModel
@OptIn(ExperimentalHorologistApi::class)
class PlayerViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     * Represents the UI state of the Player screen.
     *
     * This property provides a [StateFlow] that emits the current UI state based on the player's
     * internal state. It transforms the `playerState` from the `episodePlayer` into a
     * [PlayerScreenUiState] which can be either:
     *  - [PlayerScreenUiState.Empty]: Emitted when there is no current episode and the playback
     *  queue is empty. This indicates that the player has nothing to play.
     *  - [PlayerScreenUiState.Ready]: Emitted when there is an episode available to play (either
     *  a current episode or items in the queue). It wraps a [PlayerUiState] object that contains
     *  details about the current player state and the playback position.
     *  - [PlayerScreenUiState.Loading]: The initial state, emitted while the player is initializing
     *  or determining its initial state.
     *
     * The [StateFlow] is configured to:
     *  - Share its emissions with multiple collectors using `stateIn`.
     *  - Be scoped to the [viewModelScope], meaning it's lifecycle-aware and automatically cancels
     *  emissions when the [ViewModel] is cleared.
     *  - Use [SharingStarted.WhileSubscribed] to keep the upstream flow active as long as there are
     *  active subscribers, and for up to 5 seconds after the last subscriber unsubscribes.
     *  - Have an initial value of [PlayerScreenUiState.Loading], ensuring the UI starts in a
     *  loading state.
     */
    val uiState: StateFlow<PlayerScreenUiState> = episodePlayer.playerState.map {
        if (it.currentEpisode == null && it.queue.isEmpty()) {
            PlayerScreenUiState.Empty
        } else {
            PlayerScreenUiState.Ready(PlayerUiState(it, buildPositionModel(it)))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlayerScreenUiState.Loading
    )

    /**
     * Builds a [TrackPositionUiModel] representing the current playback position of an episode.
     *
     * This function takes an [EpisodePlayerState] as input and calculates the playback position
     * information, including the percentage of the episode played, the total duration of the
     * episode, and the elapsed time.
     *
     * If there's a current episode, it creates a [TrackPositionUiModel.Actual] instance with:
     *  - `percent`: The percentage of the episode played (elapsed time / total duration).
     *  - `duration`: The total duration of the episode.
     *  - `position`: The elapsed time of the episode.
     *
     * If there's no current episode, it returns [TrackPositionUiModel.Actual.ZERO], representing
     * a default state with zero values.
     *
     * @param state The [EpisodePlayerState] containing information about the current episode and
     * playback time.
     * @return A [TrackPositionUiModel] representing the playback position.
     */
    private fun buildPositionModel(state: EpisodePlayerState): TrackPositionUiModel.Actual =
        if (state.currentEpisode != null) {
            TrackPositionUiModel.Actual(
                percent = state.timeElapsed.toMillis().toFloat() /
                    (
                        state.currentEpisode?.duration?.toMillis()
                            ?.toFloat() ?: 0f
                        ),
                duration = state.currentEpisode?.duration?.toKotlinDuration()
                    ?: Duration.ZERO.toKotlinDuration(),
                position = state.timeElapsed.toKotlinDuration()
            )
        } else {
            TrackPositionUiModel.Actual.ZERO
        }

    /**
     * Initiates playback of the currently loaded episode.
     *
     * This function calls the [EpisodePlayer.play] method of our [EpisodePlayer] property
     * [episodePlayer], which starts or resumes the playback of the audio content.
     */
    fun onPlay() {
        episodePlayer.play()
    }

    /**
     * Pauses the episode playback.
     *
     * This function calls the [EpisodePlayer.pause] method of our [EpisodePlayer] property
     * [episodePlayer], which pauses the audio playback.
     */
    fun onPause() {
        episodePlayer.pause()
    }

    /**
     * Advances the episode playback by 10 seconds.
     *
     * This function calls the [EpisodePlayer.advanceBy] method of our [EpisodePlayer] property
     * [episodePlayer] with a `duration` of 10 seconds which advances the playback by  a fixed
     * duration of 10 seconds. It's designed for quick forward navigation within the episode content.
     * 
     * @see EpisodePlayer.advanceBy
     */
    fun onAdvanceBy() {
        episodePlayer.advanceBy(duration = Duration.ofSeconds(10))
    }

    /**
     * Rewinds the currently playing episode by 10 seconds.
     *
     * This function calls the [EpisodePlayer.rewindBy] method of our [EpisodePlayer] property
     * [episodePlayer] with a `duration` of 10 seconds which rewinds the playback by a fixed
     * duration of 10 seconds. It's designed for quick backward navigation within the episode content.
     *
     * @see episodePlayer
     * @see EpisodePlayer.rewindBy
     */
    fun onRewindBy() {
        episodePlayer.rewindBy(duration = Duration.ofSeconds(10))
    }

    /**
     * Handles changes to the playback speed of the episode player.
     *
     * This function toggles between normal speed (1x) and double speed (2x) playback.
     * If the current playback speed is 2x, it will decrease the speed to 1x (by decreasing by 1 second).
     * Otherwise, it will increase the speed to 2x (or to the next available faster speed).
     *
     * The logic is as follows:
     *  1. Check if the current playback speed, as indicated by the [EpisodePlayerState.playbackSpeed]
     *  property of the [EpisodePlayer] is equal to 2 seconds (representing 2x speed).
     *  2. If the speed is 2x, call the [EpisodePlayer.decreaseSpeed] method of our [EpisodePlayer]
     *  property [episodePlayer] with a `speed` of 1000 milliseconds (1 second) to decrease it by
     *  1 second ( effectively returning it to 1x in this case).
     *  3. If the speed is not 2x (meaning it's 1x or potentially a different speed), call
     *  the [EpisodePlayer.increaseSpeed] method of our [EpisodePlayer] property [episodePlayer]
     *  to increase it to the next faster speed, expected to be 2x in most cases.
     */
    fun onPlaybackSpeedChange() {
        if (episodePlayer.playerState.value.playbackSpeed == Duration.ofSeconds(2)) {
            episodePlayer.decreaseSpeed(speed = Duration.ofMillis(1000))
        } else {
            episodePlayer.increaseSpeed()
        }
    }
}

/**
 * Represents the UI state of the player screen.
 *
 * This sealed class defines the possible states the player screen can be in, allowing for
 * a structured and type-safe way to handle different screen states.
 */
sealed class PlayerScreenUiState {
    /**
     * Represents the loading state of the player screen UI.
     * This state indicates that the necessary data for the player screen
     * is currently being fetched or processed.
     *
     * It's one of the possible states in the [PlayerScreenUiState] sealed class.
     * When the UI is in the `Loading` state, typically a loading indicator
     * or a placeholder view is displayed to the user.
     */
    data object Loading : PlayerScreenUiState()

    /**
     * Represents the UI state when the player is ready to play.
     *
     * This state indicates that the player has been initialized and is prepared
     * to start playback or interact with the media controls. It contains the
     * current [PlayerUiState] which holds the specifics about the player's status
     * and media information.
     *
     * @property playerState The current state of the player, including playback status,
     * media metadata, buffering information, and any errors.
     */
    data class Ready(
        val playerState: PlayerUiState
    ) : PlayerScreenUiState()

    /**
     * Represents the state of the Player Screen UI when there is no data to display.
     * This state indicates that the player screen is empty and no content is available.
     * It's a subclass of [PlayerScreenUiState].
     */
    data object Empty : PlayerScreenUiState()
}
