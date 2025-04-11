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

package com.example.jetcaster.ui.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.volumeRotaryBehavior
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.components.display.TextMediaDisplay
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import kotlinx.coroutines.flow.StateFlow

/**
 * A composable function that represents the main Player screen.
 * It manages the state related to the volume and interacts with
 * the [PlayerViewModel] and [VolumeViewModel] and then calls the
 * stateless [PlayerScreen] override to render the UI.
 *
 * @param volumeViewModel The [VolumeViewModel] responsible for managing the volume state.
 * @param onVolumeClick A callback function invoked when the volume control is clicked.
 * @param modifier [Modifier] for styling and layout customization.
 * @param playerScreenViewModel The [PlayerViewModel] responsible for managing the player state.
 * Default is created using [hiltViewModel].
 */
@Composable
fun PlayerScreen(
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    playerScreenViewModel: PlayerViewModel = hiltViewModel(),
) {
    /**
     * The current state of the volume. A [State] wrapped [VolumeUiState] created from the
     * [StateFlow] of [VolumeUiState] provided by the [VolumeViewModel.volumeUiState] property
     * of our [VolumeViewModel] property [volumeViewModel].
     */
    val volumeUiState: VolumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()

    PlayerScreen(
        playerScreenViewModel = playerScreenViewModel,
        volumeUiState = volumeUiState,
        onVolumeClick = onVolumeClick,
        onUpdateVolume = { newVolume -> volumeViewModel.setVolume(newVolume) },
        modifier = modifier
    )
}

/**
 * Displays the player screen, showing media information, playback controls, and settings.
 *
 * This composable handles different states of the player, including loading, empty (nothing playing),
 * and ready (media is playing). It uses a [PlayerViewModel] to manage the player's state and actions.
 *
 * @param playerScreenViewModel The [PlayerViewModel] that provides the player's state and
 * handles actions.
 * @param volumeUiState The current state of the volume UI, provided as a [VolumeUiState].
 * @param onVolumeClick Callback to be executed when the volume button is clicked.
 * @param onUpdateVolume Callback to be executed when the volume is changed via rotary input.
 * @param modifier [Modifier] for styling and layout customization.
 */
@OptIn(ExperimentalWearFoundationApi::class, ExperimentalWearMaterialApi::class)
@Composable
private fun PlayerScreen(
    playerScreenViewModel: PlayerViewModel,
    volumeUiState: VolumeUiState,
    onVolumeClick: () -> Unit,
    onUpdateVolume: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * The current state of the player screen. A [State] wrapped [PlayerScreenUiState] created
     * from the [StateFlow] of [PlayerScreenUiState] provided by the [PlayerViewModel.uiState]
     * property of our [PlayerViewModel] property [playerScreenViewModel].
     */
    val uiState: PlayerScreenUiState by playerScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val state: PlayerScreenUiState = uiState) {
        PlayerScreenUiState.Loading -> LoadingMediaDisplay(modifier = modifier)
        PlayerScreenUiState.Empty -> {
            PlayerScreen(
                mediaDisplay = {
                    TextMediaDisplay(
                        title = stringResource(id = R.string.nothing_playing),
                        subtitle = ""
                    )
                },
                controlButtons = {
                    PodcastControlButtons(
                        onPlayButtonClick = playerScreenViewModel::onPlay,
                        onPauseButtonClick = playerScreenViewModel::onPause,
                        playPauseButtonEnabled = false,
                        playing = false,
                        onSeekBackButtonClick = playerScreenViewModel::onRewindBy,
                        seekBackButtonEnabled = false,
                        onSeekForwardButtonClick = playerScreenViewModel::onAdvanceBy,
                        seekForwardButtonEnabled = false
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeUiState = volumeUiState,
                        onVolumeClick = onVolumeClick,
                        playerUiState = PlayerUiState(),
                        onPlaybackSpeedChange = playerScreenViewModel::onPlaybackSpeedChange,
                        enabled = false,
                    )
                },
            )
        }

        is PlayerScreenUiState.Ready -> {
            // When screen is ready, episode is always not null, however EpisodePlayerState may
            // return a null episode
            val episode: PlayerEpisode? = state.playerState.episodePlayerState.currentEpisode

            PlayerScreen(
                mediaDisplay = {
                    if (episode != null && episode.title.isNotEmpty()) {
                        TextMediaDisplay(
                            title = episode.podcastName,
                            subtitle = episode.title
                        )
                    } else {
                        TextMediaDisplay(
                            title = stringResource(id = R.string.nothing_playing),
                            subtitle = ""
                        )
                    }
                },

                controlButtons = {
                    PodcastControlButtons(
                        onPlayButtonClick = playerScreenViewModel::onPlay,
                        onPauseButtonClick = playerScreenViewModel::onPause,
                        playPauseButtonEnabled = true,
                        playing = state.playerState.episodePlayerState.isPlaying,
                        onSeekBackButtonClick = playerScreenViewModel::onRewindBy,
                        seekBackButtonEnabled = true,
                        onSeekForwardButtonClick = playerScreenViewModel::onAdvanceBy,
                        seekForwardButtonEnabled = true,
                        seekBackButtonIncrement = SeekButtonIncrement.Ten,
                        seekForwardButtonIncrement = SeekButtonIncrement.Ten,
                        trackPositionUiModel = state.playerState.trackPositionUiModel
                    )
                },
                buttons = {
                    @Suppress("RedundantValueArgument")
                    SettingsButtons(
                        volumeUiState = volumeUiState,
                        onVolumeClick = onVolumeClick,
                        playerUiState = state.playerState,
                        onPlaybackSpeedChange = playerScreenViewModel::onPlaybackSpeedChange,
                        enabled = true,
                    )
                },
                modifier = modifier
                    .rotaryScrollable(
                        volumeRotaryBehavior(
                            volumeUiStateProvider = { volumeUiState },
                            onRotaryVolumeInput = { onUpdateVolume },
                        ),
                        focusRequester = rememberActiveFocusRequester(),
                    ),
                background = {
                    ArtworkColorBackground(
                        paintable = episode?.let { CoilPaintable(model = episode.podcastImageUrl) },
                        defaultColor = MaterialTheme.colors.primary,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            )
        }
    }
}
