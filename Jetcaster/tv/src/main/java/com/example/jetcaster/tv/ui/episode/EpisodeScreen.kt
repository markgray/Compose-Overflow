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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.ui.component.BackgroundContainer
import com.example.jetcaster.tv.ui.component.EnqueueButton
import com.example.jetcaster.tv.ui.component.EpisodeDataAndDuration
import com.example.jetcaster.tv.ui.component.ErrorState
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.PlayButton
import com.example.jetcaster.tv.ui.component.Thumbnail
import com.example.jetcaster.tv.ui.component.TwoColumn
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

/**
 * Composable function representing the Episode Screen.
 *
 * This screen displays the details of a selected episode and allows the user to interact with it.
 * It handles different UI states such as loading, error, and ready, and delegates actions
 * like playing the episode and adding to a playlist to the provided callbacks and ViewModel.
 *
 * First we initialize our [State] wrapped [EpisodeScreenUiState] variable `uiState` by using the
 * [StateFlow.collectAsState] method of the [EpisodeScreenViewModel.uiStateFlow] property of our
 * [EpisodeScreenViewModel] parameter [episodeScreenViewModel] to collect its latest value as a
 * [State]. Then we initialize our [Modifier] variable `screenModifier` by chaining a
 * [Modifier.fillMaxSize] to our [Modifier] parameter [modifier].
 *
 * We copy our [State] wrapped [EpisodeScreenUiState] variable `uiState` to our [EpisodeScreenUiState]
 * variable `s` to initialize it then use a `when` statement to branch on the type of `s`:
 *  - [EpisodeScreenUiState.Loading] -> we compose a [Loading] composable with its `modifier`
 *  argument our [Modifier] variable `screenModifier` (the UI is in a loading state, so we display a
 *  circular indeterminate progress screen).
 *  - [EpisodeScreenUiState.Error] -> we compose an [ErrorState] composable whose `backToHome`
 *  argument is our [backToHome] lambda parameter, and whose `modifier` argument us our [Modifier]
 *  variable `screenModifier`.
 *  - [EpisodeScreenUiState.Ready] -> we compose an [EpisodeDetailsWithBackground] composable whose
 *  whose `playerEpisode` is the [EpisodeScreenUiState.Ready.playerEpisode] of `s`, whose
 *  `playEpisode` argument is a lambda that calls the [EpisodeScreenViewModel.play] method of
 *  [EpisodeScreenViewModel] parameter [episodeScreenViewModel] with the [PlayerEpisode] passed the
 *  lambda and calls our [playEpisode] lambda parameter. The `addPlayList` argument of the
 *  [EpisodeDetailsWithBackground] is a function reference to the [EpisodeScreenViewModel.addPlayList]
 *  method of our [EpisodeScreenViewModel] parameter [episodeScreenViewModel], and its `modifer`
 *  argument is our [Modifier] variable `screenModifier`.
 *
 * @param playEpisode Callback function to be executed when the user initiates playing the episode.
 * This function is typically responsible for starting the audio/video player.
 * @param backToHome Callback function to be executed when the user wants to navigate back to the
 * home screen.
 * @param modifier [Modifier] for styling and layout of the EpisodeScreen. Defaults to [Modifier].
 * @param episodeScreenViewModel [ViewModel] instance responsible for managing the state and business
 * logic of the [EpisodeScreen]. It is injected using Hilt. Defaults to a new instance provided by
 * [hiltViewModel].
 *
 * @see EpisodeScreenUiState for the possible UI states of the screen.
 * @see EpisodeScreenViewModel for the business logic and state management.
 * @see EpisodeDetailsWithBackground for the composable used to display the episode details.
 * @see Loading for the composable to display when the episode is loading.
 * @see ErrorState for the composable to display when there's an error fetching episode data.
 */
@Composable
fun EpisodeScreen(
    playEpisode: () -> Unit,
    backToHome: () -> Unit,
    modifier: Modifier = Modifier,
    episodeScreenViewModel: EpisodeScreenViewModel = hiltViewModel()
) {

    /**
     * Collects the current UI state from the [StateFlow] of [EpisodeScreenUiState] emitted by the
     * [EpisodeScreenViewModel.uiStateFlow] of [episodeScreenViewModel] and converts it to a [State]
     * wrapped [EpisodeScreenUiState] which it assigns it to [uiState].
     */
    val uiState: EpisodeScreenUiState by episodeScreenViewModel.uiStateFlow.collectAsState()

    /**
     * [Modifier] for styling and layout of the various screens in the [EpisodeScreen], an
     * [Modifier.fillMaxSize] is used to fill the entire screen.
     */
    val screenModifier: Modifier = modifier.fillMaxSize()

    when (val s: EpisodeScreenUiState = uiState) {
        EpisodeScreenUiState.Loading -> Loading(modifier = screenModifier)
        EpisodeScreenUiState.Error -> ErrorState(backToHome = backToHome, modifier = screenModifier)
        is EpisodeScreenUiState.Ready -> EpisodeDetailsWithBackground(
            playerEpisode = s.playerEpisode,
            playEpisode = {
                episodeScreenViewModel.play(playerEpisode = it)
                playEpisode()
            },
            addPlayList = episodeScreenViewModel::addPlayList,
            modifier = screenModifier
        )
    }
}

/**
 * Displays episode details with a background.
 *
 * This composable combines a [BackgroundContainer] with [EpisodeDetails] to
 * display information about a [PlayerEpisode] with a visually appealing background.
 * It also provides actions to play the episode and add it to a playlist.
 *
 * @param playerEpisode The [PlayerEpisode] containing the details to display.
 * @param playEpisode A lambda function to be invoked when the user wants to play the episode.
 * It takes the [PlayerEpisode] as a parameter.
 * @param addPlayList A lambda function to be invoked when the user wants to add the episode
 * to a playlist. It takes the [PlayerEpisode] as a parameter.
 * @param modifier [Modifier] to apply to the outer container. Our caller [EpisodeScreen] passes us
 * a [Modifier.fillMaxSize].
 */
@Composable
private fun EpisodeDetailsWithBackground(
    playerEpisode: PlayerEpisode,
    playEpisode: (PlayerEpisode) -> Unit,
    addPlayList: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackgroundContainer(
        playerEpisode = playerEpisode,
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        EpisodeDetails(
            playerEpisode = playerEpisode,
            playEpisode = playEpisode,
            addPlayList = addPlayList,
            modifier = Modifier
                .padding(paddingValues = JetcasterAppDefaults.overScanMargin.episode.intoPaddingValues())
        )
    }
}

/**
 * Displays the details of an episode, including a thumbnail and episode information.
 *
 * This composable presents a two-column layout:
 * - The first column displays a thumbnail representing the episode.
 * - The second column shows detailed information about the episode,
 *   including the title, description, and actions to play the episode or add it to a playlist.
 *
 * @param playerEpisode The [PlayerEpisode] object containing the episode's data.
 * @param playEpisode A lambda function to be called when the user wants to play the episode.
 * It receives the [PlayerEpisode] to be played as a parameter.
 * @param addPlayList A lambda function to be called when the user wants to add the episode to a
 * playlist. It receives the [PlayerEpisode] to be added as a parameter.
 * @param modifier [Modifier] to be applied to the layout. Our caller [EpisodeDetailsWithBackground]
 * passes us [Modifier.fillMaxSize] to which it chains a [Modifier.padding] with a constant
 * [PaddingValues] `JetcasterAppDefaults.overScanMargin.episode.intoPaddingValues` (which adds 80.dp
 * to our `start` and 80.dp to our `end`).
 */
@Composable
private fun EpisodeDetails(
    playerEpisode: PlayerEpisode,
    playEpisode: (PlayerEpisode) -> Unit,
    addPlayList: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    TwoColumn(
        first = {
            Thumbnail(
                episode = playerEpisode,
                size = JetcasterAppDefaults.thumbnailSize.episodeDetails
            )
        },
        second = {
            EpisodeInfo(
                playerEpisode = playerEpisode,
                playEpisode = { playEpisode(playerEpisode) },
                addPlayList = { addPlayList(playerEpisode) },
                modifier = Modifier.weight(weight = 1f)
            )
        },
        modifier = modifier,
    )
}

/**
 * Displays detailed information about an episode, including its author, title,
 * publication date, duration, summary, and playback controls.
 *
 * @param playerEpisode The [PlayerEpisode] object containing the episode's details.
 * @param playEpisode A lambda function to be executed when the "play" action is triggered.
 * @param addPlayList A lambda function to be executed when the "add to playlist" action is triggered.
 * @param modifier [Modifier] to be applied to the outer Column. Our caller [EpisodeDetails] passes
 * us a [RowScope.weight] with a `weight` of 1f which causes us to take up the remaining space after
 * our sibling [Thumbnail] is measured and placed.
 */
@Composable
private fun EpisodeInfo(
    playerEpisode: PlayerEpisode,
    playEpisode: () -> Unit,
    addPlayList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val duration: Duration? = playerEpisode.duration

    Column(modifier = modifier) {
        Text(text = playerEpisode.author, style = MaterialTheme.typography.bodySmall)
        Text(text = playerEpisode.title, style = MaterialTheme.typography.headlineLarge)
        if (duration != null) {
            EpisodeDataAndDuration(offsetDateTime = playerEpisode.published, duration = duration)
        }
        Spacer(modifier = Modifier.height(height = JetcasterAppDefaults.gap.paragraph))
        Text(
            text = playerEpisode.summary,
            softWrap = true,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(height = JetcasterAppDefaults.gap.paragraph))
        Controls(playEpisode = playEpisode, addPlayList = addPlayList)
    }
}

/**
 * Displays a row of control buttons for an episode, specifically a play button and an enqueue button.
 *
 * @param playEpisode A callback function invoked when the play button is clicked. It is expected to
 * handle the logic for starting playback of the episode.
 * @param addPlayList A callback function invoked when the enqueue button is clicked. It is expected
 * to handle the logic for adding the episode to a playlist.
 * @param modifier The modifier to be applied to the Row layout containing the controls. Our caller
 * [EpisodeInfo] does not pass us any so we use the empty, default, or starter [Modifier] that
 * contains no elements.
 */
@Composable
private fun Controls(
    playEpisode: () -> Unit,
    addPlayList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.item),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PlayButton(onClick = playEpisode)
        EnqueueButton(onClick = addPlayList)
    }
}
