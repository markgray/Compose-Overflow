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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.component.BackgroundContainer
import com.example.jetcaster.tv.ui.component.EnqueueButton
import com.example.jetcaster.tv.ui.component.EpisodeDetails
import com.example.jetcaster.tv.ui.component.EpisodeRow
import com.example.jetcaster.tv.ui.component.InfoButton
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.NextButton
import com.example.jetcaster.tv.ui.component.PlayPauseButton
import com.example.jetcaster.tv.ui.component.PreviousButton
import com.example.jetcaster.tv.ui.component.RewindButton
import com.example.jetcaster.tv.ui.component.Seekbar
import com.example.jetcaster.tv.ui.component.SkipButton
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The main screen for the player, responsible for displaying the currently playing episode
 * and managing the player controls.
 *
 * This composable handles different UI states of the player, such as:
 * - **Loading:** When the player is still loading data or initializing.
 * - **NoEpisodeInQueue:** When there are no episodes in the player's queue.
 * - **Ready:** When an episode is ready to be played and the player is initialized.
 *
 * It uses a [PlayerScreenViewModel] to manage the player's state and actions.
 *
 * We start by initializing our [State] wrapped [PlayerScreenUiState] variable `uiState` to the
 * value that the [StateFlow.collectAsStateWithLifecycle] method returns for the [StateFlow]
 * of [PlayerScreenUiState] property [PlayerScreenViewModel.uiStateFlow]  of our
 * [PlayerScreenViewModel] property [playScreenViewModel]. Then we copy this to our
 * [PlayerScreenUiState] variable `s` in a `when` statement and branch on the type of s:
 *  - [PlayerScreenUiState.Loading] -> we compose a [Loading] composable whose `modifier` argument
 *  is our [Modifier] parameter [modifier].
 *  - [PlayerScreenUiState.NoEpisodeInQueue] -> we compose a [NoEpisodeInQueue] whose `backToHome`
 *  argument is our [backToHome] lambda parameter, and whose `modifier` argument is our [Modifier]
 *  parameter [modifier].
 *  - [PlayerScreenUiState.Ready] -> we compose a [Player] whose `episodePlayerState` argument is
 *  the value of the [PlayerScreenUiState.Ready] property [PlayerScreenUiState.Ready.playerState]
 *  of `s`, whose `play` argument is the [PlayerScreenViewModel.play] method of our
 *  [PlayerScreenViewModel] parameter [playScreenViewModel], whose `pause` argument is the
 *  [PlayerScreenViewModel.pause] method of our [PlayerScreenViewModel] parameter
 *  [playScreenViewModel], whose `previous` argument is the [PlayerScreenViewModel.previous] method
 *  of our [PlayerScreenViewModel] parameter [playScreenViewModel], whose `next` argument is the
 *  [PlayerScreenViewModel.next] method of our [PlayerScreenViewModel] parameter
 *  [playScreenViewModel], whose `skip` argument is the [PlayerScreenViewModel.skip] method of our
 *  [PlayerScreenViewModel] parameter [playScreenViewModel], whose `rewind` argument is the
 *  [PlayerScreenViewModel.rewind] method of our [PlayerScreenViewModel] parameter
 *  [playScreenViewModel], whose `enqueue` argument is the [PlayerScreenViewModel.enqueue] method
 *  of our [PlayerScreenViewModel] parameter [playScreenViewModel], and whose `showDetails`
 *  argument is our lambda parameter [showDetails].
 *
 * @param backToHome A lambda function that is called to navigate back to the home screen.
 * @param showDetails A lambda function that is called to show the details of a specific
 * [PlayerEpisode]. It receives the [PlayerEpisode] as a parameter.
 * @param modifier Modifier for styling the composable. Our caller, the `Route` method of
 * `JetcasterApp`, passes us a  [Modifier.fillMaxSize] instance.
 * @param playScreenViewModel The [PlayerScreenViewModel] instance. By default it uses
 * hiltViewModel() to get it by dependency injection.
 */
@Composable
fun PlayerScreen(
    backToHome: () -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    playScreenViewModel: PlayerScreenViewModel = hiltViewModel()
) {
    val uiState: PlayerScreenUiState by playScreenViewModel.uiStateFlow.collectAsStateWithLifecycle()

    when (val s: PlayerScreenUiState = uiState) {
        PlayerScreenUiState.Loading -> Loading(modifier = modifier)
        PlayerScreenUiState.NoEpisodeInQueue -> {
            NoEpisodeInQueue(backToHome = backToHome, modifier = modifier)
        }

        is PlayerScreenUiState.Ready -> {
            Player(
                episodePlayerState = s.playerState,
                play = playScreenViewModel::play,
                pause = playScreenViewModel::pause,
                previous = playScreenViewModel::previous,
                next = playScreenViewModel::next,
                skip = playScreenViewModel::skip,
                rewind = playScreenViewModel::rewind,
                enqueue = playScreenViewModel::enqueue,
                playEpisode = playScreenViewModel::play,
                showDetails = showDetails,
            )
        }
    }
}

/**
 * A composable function that renders the player UI for an audio episode.
 *
 * This function manages the playback state and renders the player controls and information based
 * on the [EpisodePlayerState] parameter [episodePlayerState]. It also handles actions such as play,
 * pause, skip, rewind, and navigation to the next or previous episode.
 *
 * We start by composing a [LaunchedEffect] keyed on our [Boolean] parameter [autoStart] (Compose
 * will re-launch the effect if the [autoStart] parameter changes) and in its [CoroutineScope]
 * `block` we call our [play] lambda parameter if [autoStart] is `true` and the
 * [EpisodePlayerState.isPlaying] property of our [EpisodePlayerState] parameter [episodePlayerState]
 * is `false`.
 *
 * Then we initialize our [PlayerEpisode] variable `currentEpisode` to the value of the
 * [EpisodePlayerState.currentEpisode] property of our [EpisodePlayerState] parameter
 * [episodePlayerState].
 *
 * If `currentEpisode` is not `null` we compose a [EpisodePlayerWithBackground] whose arguments are:
 *  - `playerEpisode` is our [PlayerEpisode] variable `currentEpisode`.
 *  - `queue` is an [EpisodeList] constructed from the [EpisodePlayerState.queue] property of our
 *  [EpisodePlayerState] parameter [episodePlayerState].
 *  - `isPlaying` is the value of the [EpisodePlayerState.isPlaying] property of our
 *  [EpisodePlayerState] parameter [episodePlayerState].
 *  - `timeElapsed` is the value of the [EpisodePlayerState.timeElapsed] property of our
 *  [EpisodePlayerState] parameter [episodePlayerState].
 *  - `play` is our [play] lambda parameter.
 *  - `pause` is our [pause] lambda parameter.
 *  - `previous` is our [previous] lambda parameter.
 *  - `next` is our [next] lambda parameter.
 *  - `skip` is our [skip] lambda parameter.
 *  - `rewind` is our [rewind] lambda parameter.
 *  - `enqueue` is our [enqueue] lambda parameter.
 *  - `showDetails` is our [showDetails] lambda parameter.
 *  - `playEpisode` is our [playEpisode] lambda parameter.
 *  - `modifier` is our [Modifier] parameter [modifier].
 *
 * @param episodePlayerState The current state of the episode player, including the current
 * episode, playback status, queue, and time elapsed.
 * @param play A callback function to start or resume playback.
 * @param pause A callback function to pause playback.
 * @param previous A callback function to play the previous episode in the queue.
 * @param next A callback function to play the next episode in the queue.
 * @param skip A callback function to skip forward in the current episode.
 * @param rewind A callback function to rewind in the current episode.
 * @param enqueue A callback function to add an episode to the playback queue.
 * @param showDetails A callback function to display the detailed information of an episode.
 * @param playEpisode A callback function to start playing a specific episode.
 * @param modifier Modifier for styling and layout customization of the player UI. Our caller,
 * [PlayerScreen], does not pass us any so the empty, default, or starter [Modifier] that contains
 * no elements is used.
 * @param autoStart A boolean indicating whether playback should automatically start when the
 * composable is first rendered. Defaults to `true`.
 */
@Composable
private fun Player(
    episodePlayerState: EpisodePlayerState,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    autoStart: Boolean = true
) {
    LaunchedEffect(key1 = autoStart) {
        if (autoStart && !episodePlayerState.isPlaying) {
            play()
        }
    }

    val currentEpisode: PlayerEpisode? = episodePlayerState.currentEpisode

    if (currentEpisode != null) {
        EpisodePlayerWithBackground(
            playerEpisode = currentEpisode,
            queue = EpisodeList(member = episodePlayerState.queue),
            isPlaying = episodePlayerState.isPlaying,
            timeElapsed = episodePlayerState.timeElapsed,
            play = play,
            pause = pause,
            previous = previous,
            next = next,
            skip = skip,
            rewind = rewind,
            enqueue = enqueue,
            showDetails = showDetails,
            playEpisode = playEpisode,
            modifier = modifier,
        )
    }
}

/**
 * Displays the episode player with a background, overlayed with a queue of episodes.
 *
 * This composable combines the [BackgroundContainer] and [EpisodePlayer] to create a
 * full-screen player experience. It also includes a [PlayerQueueOverlay] to display
 * and interact with the current episode queue.
 *
 * We start by initializing and remembering our [FocusRequester] variable `episodePlayer` to a new
 * instance (this will be used by our [EpisodePlayer] composable to request focus). Then we compose
 * a [LaunchedEffect] keyed on `Unit` (means this effect will only run once when this composable is
 * first composed) and in its [CoroutineScope] `block` argument we call the
 * [FocusRequester.requestFocus] of our [FocusRequester] variable `episodePlayer` to immediately
 * request focus on our [EpisodePlayer].
 *
 * Our root composable is a [BackgroundContainer] whose arguments are:
 *  - `playerEpisode` is our [PlayerEpisode] parameter [playerEpisode].
 *  - `modifier` is our [Modifier] parameter [modifier].
 *  - `contentAlignment` is [Alignment.Center].
 *
 * In the [BoxScope] `content` composable lambe argument of the [BackgroundContainer] we compose an
 * [EpisodePlayer] whose arguments are:
 *  - `playerEpisode` is our [PlayerEpisode] parameter [playerEpisode].
 *  - `isPlaying` is our [Boolean] parameter [isPlaying].
 *  - `timeElapsed` is our [Duration] parameter [timeElapsed].
 *  - `play` is our [play] lambda parameter.
 *  - `pause` is our [pause] lambda parameter.
 *  - `previous` is our [previous] lambda parameter.
 *  - `next` is our [next] lambda parameter.
 *  - `skip` is our [skip] lambda parameter.
 *  - `rewind` is our [rewind] lambda parameter.
 *  - `enqueue` is our [enqueue] lambda parameter.
 *  - `showDetails` is our [showDetails] lambda parameter.
 *  - `focusRequester` is our [FocusRequester] variable `episodePlayer`.
 *  - `modifier` is a [Modifier.padding] whose `paddingValues` argument is the constant
 *  `JetcasterAppDefaults.overScanMargin.player` converted to [PaddingValues] (top = 40.dp,
 *  bottom = 40.dp, start = 80.dp, end = 80.dp).
 *
 * On top of the [EpisodePlayer] we compose a [PlayerQueueOverlay] whose arguments are;
 *  - `playerEpisodeList` is our [EpisodeList] parameter [queue].
 *  - `onSelected` is our [playEpisode] lambda parameter.
 *  - `modifier` is a [Modifier.fillMaxSize].
 *  - `contentPadding` is the a copy of the constant `JetcasterAppDefaults.overScanMargin.player`
 *  converted to [PaddingValues] with its `top` overridden to be 0.dp (top = 0.dp, bottom = 40.dp,
 *  start = 80.dp, end = 80.dp).
 *
 * @param playerEpisode The currently playing episode.
 * @param queue The list of episodes in the current queue.
 * @param isPlaying Whether the episode is currently playing.
 * @param timeElapsed The elapsed playback time of the current episode.
 * @param play Callback to start or resume playback.
 * @param pause Callback to pause playback.
 * @param previous Callback to skip to the previous episode in the queue.
 * @param next Callback to skip to the next episode in the queue.
 * @param skip Callback to skip forward in the current episode.
 * @param rewind Callback to rewind in the current episode.
 * @param enqueue Callback to add an episode to the queue.
 * @param showDetails Callback to display more details about the given episode.
 * @param playEpisode Callback to start playing the given episode.
 * @param modifier Modifier for styling and layout customization. Our caller, [Player], passes us
 * its own [Modifier] parameter which is the empty, default, or starter [Modifier] that contains no
 * elements.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EpisodePlayerWithBackground(
    playerEpisode: PlayerEpisode,
    queue: EpisodeList,
    isPlaying: Boolean,
    timeElapsed: Duration,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    val episodePlayer: FocusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        episodePlayer.requestFocus()
    }

    BackgroundContainer(
        playerEpisode = playerEpisode,
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        EpisodePlayer(
            playerEpisode = playerEpisode,
            isPlaying = isPlaying,
            timeElapsed = timeElapsed,
            play = play,
            pause = pause,
            previous = previous,
            next = next,
            skip = skip,
            rewind = rewind,
            enqueue = enqueue,
            showDetails = showDetails,
            focusRequester = episodePlayer,
            modifier = Modifier
                .padding(paddingValues = JetcasterAppDefaults.overScanMargin.player.intoPaddingValues())
        )

        PlayerQueueOverlay(
            playerEpisodeList = queue,
            onSelected = playEpisode,
            modifier = Modifier.fillMaxSize(),
            contentPadding = JetcasterAppDefaults.overScanMargin.player.copy(top = 0.dp)
                .intoPaddingValues(),
            offset = DpOffset(x = 0.dp, y = 136.dp),
        )
    }
}

/**
 * Displays the episode player UI, including episode details and playback controls.
 *
 * This composable provides a UI for playing an episode, displaying its details,
 * and interacting with playback controls. It handles focus management to ensure the
 * player is visible when focused.
 *
 * Our root composable is a [Column] whose arguments are:
 *  - `verticalArrangement` is a [Arrangement.spacedBy] whose `space` argument is the constant
 *  `JetcasterAppDefaults.gap.section` (40.dp)
 *  - `modifier` is a [Modifier.bringIntoViewRequester] whose `bringIntoViewRequester` argument is
 *  our [BringIntoViewRequester] parameter [bringIntoViewRequester], to which is chained a
 *  [Modifier.onFocusChanged] whose `onFocused` lambda argument checks whether the
 *  [FocusState.isFocused] property of the [FocusState] passed the lambda argument is `true` and
 *  if it is `true` it calls the [CoroutineScope.launch] method of our [CoroutineScope] parameter
 *  [coroutineScope] and in its [CoroutineScope] `block` it calls the
 *  [BringIntoViewRequester.bringIntoView] method of our [BringIntoViewRequester] parameter
 *  [bringIntoViewRequester] to bring this item into bounds by making all the scrollable parents
 *  scroll appropriately. and at the end of the [Modifier] chain we use `then` to chain our [Modifier]
 *  parameter [modifier] into the chain.
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we compose a
 * [EpisodeDetails] whose arguments are:
 *  - `playerEpisode` is our [PlayerEpisode] parameter [playerEpisode].
 *  - `content` is an empty lambda.
 *  - `controls` is a lambda whose content is a [EpisodeControl] whose `showDetails` argument is
 *  a lambda that calls our [showDetails] lambda parameter with our [PlayerEpisode] parameter
 *  [playerEpisode], and whose `enqueue` argument is a lambda that calls our [enqueue] lambda
 *  parameter with our [PlayerEpisode] parameter [playerEpisode].
 *
 * Below the [EpisodeDetails] we compose a [PlayerControl] whose arguments are:
 *  - `isPlaying` is our [Boolean] parameter [isPlaying].
 *  - `timeElapsed` is our [Duration] parameter [timeElapsed].
 *  - `length` is the value of the [Duration] property [PlayerEpisode.duration] of our
 *  [PlayerEpisode] parameter [playerEpisode].
 *  - `play` is our [play] lambda parameter.
 *  - `pause` is our [pause] lambda parameter.
 *  - `previous` is our [previous] lambda parameter.
 *  - `next` is our [next] lambda parameter.
 *  - `skip` is our [skip] lambda parameter.
 *  - `rewind` is our [rewind] lambda parameter.
 *  - `focusRequester` is our [FocusRequester] parameter [focusRequester].
 *
 * @param playerEpisode The [PlayerEpisode] object containing the details of the episode to be played.
 * @param isPlaying A boolean indicating whether the episode is currently playing.
 * @param timeElapsed The [Duration] representing the amount of time that has elapsed in the current
 * episode during playback.
 * @param play A callback function to start or resume playback.
 * @param pause A callback function to pause playback.
 * @param previous A callback function to go to the previous episode.
 * @param next A callback function to go to the next episode.
 * @param skip A callback function to skip forward in the episode.
 * @param rewind A callback function to rewind the episode.
 * @param enqueue A callback function to enqueue an episode for later playback.
 * @param showDetails A callback function to show the detailed information of the episode.
 * @param modifier The [Modifier] to be applied to the layout. Our caller, [EpisodePlayerWithBackground],
 * passes us a [Modifier.padding] whose `paddingValues` argument is the constant
 * `JetcasterAppDefaults.overScanMargin.player` converted to [PaddingValues] (top = 40.dp,
 * bottom = 40.dp, start = 80.dp, end = 80.dp).
 * @param bringIntoViewRequester The [BringIntoViewRequester] used to bring the player into view
 * when focused.
 * @param coroutineScope The [CoroutineScope] used for launching coroutines.
 * @param focusRequester The [FocusRequester] used to request focus on the player controls.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EpisodePlayer(
    playerEpisode: PlayerEpisode,
    isPlaying: Boolean,
    timeElapsed: Duration,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    bringIntoViewRequester: BringIntoViewRequester = remember { BringIntoViewRequester() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.section),
        modifier = Modifier
            .bringIntoViewRequester(bringIntoViewRequester = bringIntoViewRequester)
            .onFocusChanged { focusState: FocusState ->
                if (focusState.hasFocus) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
            .then(other = modifier)
    ) {
        EpisodeDetails(
            playerEpisode = playerEpisode,
            content = {},
            controls = {
                EpisodeControl(
                    showDetails = { showDetails(playerEpisode) },
                    enqueue = { enqueue(playerEpisode) }
                )
            },
        )
        PlayerControl(
            isPlaying = isPlaying,
            timeElapsed = timeElapsed,
            length = playerEpisode.duration,
            play = play,
            pause = pause,
            previous = previous,
            next = next,
            skip = skip,
            rewind = rewind,
            focusRequester = focusRequester
        )
    }
}

/**
 * A composable function that provides controls for managing an episode.
 *
 * This function renders a horizontal row containing buttons for enqueuing an episode
 * and viewing episode details.
 *
 * Our root composable is a [Row] whose arguments are:
 *  - `modifier` is our [Modifier] parameter [modifier].
 *  - `horizontalArrangement` is an [Arrangement.spacedBy] whose `space` argument is the constant
 *  `JetcasterAppDefaults.gap.item` (16.dp)
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] we first compose an
 * [EnqueueButton] whose arguments are:
 *  - `onClick` is our [enqueue] lambda parameter.
 *  - `modifier` is a [Modifier.size] whose `size` argument is the constant
 *  `JetcasterAppDefaults.iconButtonSize.default` converted to [DpSize] (28.dp by 28.dp).
 *
 * Next in the [Row] we compose an [InfoButton] whose arguments are:
 *  - `onClick` is our [showDetails] lambda parameter.
 *  - `modifier` is a [Modifier.size] whose `size` argument is the constant
 *  `JetcasterAppDefaults.iconButtonSize.default` converted to [DpSize] (28.dp by 28.dp).
 *
 * @param showDetails A lambda function to be invoked when the info button is clicked.
 * This should typically navigate to a screen displaying the episode's details.
 * @param enqueue A lambda function to be invoked when the enqueue button is clicked.
 * This should typically add the episode to a playback queue.
 * @param modifier Optional [Modifier] to apply to the row containing the control buttons. Our caller,
 * [EpisodePlayer] does not pass us any so the empty, default, or starter [Modifier] that contains
 * no elements is used.
 */
@Composable
private fun EpisodeControl(
    showDetails: () -> Unit,
    enqueue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.item)
    ) {
        EnqueueButton(
            onClick = enqueue,
            modifier = Modifier.size(size = JetcasterAppDefaults.iconButtonSize.default.intoDpSize())
        )
        InfoButton(
            onClick = showDetails,
            modifier = Modifier.size(size = JetcasterAppDefaults.iconButtonSize.default.intoDpSize())
        )
    }
}

/**
 * A Composable function that provides controls for a media player.
 *
 * This composable displays buttons for play/pause, skip, rewind, previous, and next,
 * along with an elapsed time indicator if the media length is provided.
 *
 * We start by initializing and remembering our [FocusRequester] variable `playPauseButton` to a new
 * instance (this will be used by our [PlayPauseButton] composable to request focus).
 *
 * Our root composable is a [Column] whose arguments are:
 *  - `verticalArrangement` is a [Arrangement.spacedBy] whose `space` argument is the constant
 *  `JetcasterAppDefaults.gap.item` (16.dp)
 *  - `modifier` is our [Modifier] parameter [modifier].
 *
 * At the top of the [ColumnScope] `content` lambda argument of the [Column] we compose a [Row] whose
 * arguments are:
 *  - `horizontalArrangement` is an [Arrangement.spacedBy] whose `space` argument is the constant
 * `JetcasterAppDefaults.gap.default` (16.dp), and whose `alignment` argument is
 * [Alignment.CenterHorizontally].
 *  - `verticalAlignment` is [Alignment.CenterVertically].
 *  - `modifier` is a [Modifier.fillMaxWidth] to which is chained a [Modifier.focusRequester] whose
 *  `focusRequester` argument is our [FocusRequester] parameter [focusRequester], chained to a
 *  [Modifier.onFocusChanged] whose `onFocused` lambda argument checks whether the
 *  [FocusState.isFocused] property of the [FocusState] passed the lambda argument is `true` and if
 *  it is `true` it calls the [FocusRequester.requestFocus] method of our [FocusRequester] variable
 *  `playPauseButton` to request focus on our [PlayPauseButton]. At the end of the [Modifier] chain
 *  is a [Modifier.focusable].
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] we compose a [PreviousButton],
 * whose `onClick` argument is our [previous] lambda parameter, and whose `modifier` argument is a
 * [Modifier.size] whose `size` argument is the constant `JetcasterAppDefaults.iconButtonSize.medium`
 * converted to [DpSize] (40.dp by 40.dp).
 *
 * Next in the [Row] we compose a [RewindButton], whose `onClick` argument is our [rewind] lambda
 * parameter, and whose `modifier` argument is a [Modifier.size] whose `size` argument is the
 * constant `JetcasterAppDefaults.iconButtonSize.medium` converted to [DpSize] (40.dp by 40.dp).
 *
 * Next in the [Row] we compose a [PlayPauseButton], whose arguments are:
 *  - `isPlaying` is our [Boolean] parameter [isPlaying].
 *  - `onClick` is a lambda that checks whether the [Boolean] parameter [isPlaying] is `true` and
 *  if it is `true` it calls our [pause] lambda parameter, otherwise it calls our [play] lambda
 *  parameter.
 *  - `modifier` is a [Modifier.size] whose `size` argument is the constant
 *  `JetcasterAppDefaults.iconButtonSize.large` converted to [DpSize] (56.dp by 56.dp), to which is
 *  chained a [Modifier.focusRequester] whose `focusRequester` argument is our [FocusRequester]
 *  variable `playPauseButton`.
 *
 * Next in the [Row] we compose a [SkipButton], whose `onClick` argument is our [skip] lambda
 * parameter, and whose `modifier` argument is a [Modifier.size] whose `size` argument is the
 * constant `JetcasterAppDefaults.iconButtonSize.medium` converted to [DpSize] (40.dp by 40.dp).
 *
 * Finally in the [Row] we compose a [NextButton], whose `onClick` argument is our [next] lambda
 * parameter, and whose `modifier` argument is a [Modifier.size] whose `size` argument is the
 * constant `JetcasterAppDefaults.iconButtonSize.medium` converted to [DpSize] (40.dp by 40.dp).
 *
 * Below the [Row] in the [Column] if our [Duration] parameter [length] is not `null` we compose an
 * [ElapsedTimeIndicator] whose arguments are:
 *  - `timeElapsed` is our [Duration] parameter [timeElapsed].
 *  - `length` is our [Duration] parameter [length].
 *  - `skip` is our [skip] lambda parameter.
 *  - `rewind` is our [rewind] lambda parameter.
 *
 * @param isPlaying Boolean indicating whether the media is currently playing.
 * @param timeElapsed [Duration] representing the elapsed time of the media playback.
 * @param length Optional [Duration] representing the total length of the media. If `null`, the
 * elapsed time indicator is not shown.
 * @param play Callback function invoked when the play button is clicked.
 * @param pause Callback function invoked when the pause button is clicked.
 * @param previous Callback function invoked when the previous track button is clicked.
 * @param next Callback function invoked when the next track button is clicked.
 * @param skip Callback function invoked when the skip button is clicked.
 * @param rewind Callback function invoked when the rewind button is clicked.
 * @param modifier Modifier to be applied to the container of the controls. Our caller, [EpisodePlayer],
 * does not pass us any so the empty, default, or starter [Modifier] that contains no elements is used.
 * @param focusRequester [FocusRequester] used to control focus within the player controls.
 * Defaults to a new [FocusRequester].
 */
@Composable
private fun PlayerControl(
    isPlaying: Boolean,
    timeElapsed: Duration,
    length: Duration?,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val playPauseButton: FocusRequester = remember { FocusRequester() }

    Column(
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.item),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = JetcasterAppDefaults.gap.default,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged { focusState: FocusState ->
                    if (focusState.isFocused) {
                        playPauseButton.requestFocus()
                    }
                }
                .focusable(),
        ) {
            PreviousButton(
                onClick = previous,
                modifier = Modifier.size(size = JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
            RewindButton(
                onClick = rewind,
                modifier = Modifier.size(size = JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
            PlayPauseButton(
                isPlaying = isPlaying,
                onClick = {
                    if (isPlaying) {
                        pause()
                    } else {
                        play()
                    }
                },
                modifier = Modifier
                    .size(size = JetcasterAppDefaults.iconButtonSize.large.intoDpSize())
                    .focusRequester(focusRequester = playPauseButton)
            )
            SkipButton(
                onClick = skip,
                modifier = Modifier.size(size = JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
            NextButton(
                onClick = next,
                modifier = Modifier.size(size = JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
        }
        if (length != null) {
            ElapsedTimeIndicator(
                timeElapsed = timeElapsed,
                length = length,
                skip = skip,
                rewind = rewind
            )
        }
    }
}

@Composable
private fun ElapsedTimeIndicator(
    timeElapsed: Duration,
    length: Duration,
    skip: () -> Unit,
    rewind: () -> Unit,
    modifier: Modifier = Modifier,
    knobSize: Dp = 8.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.tiny)
    ) {
        ElapsedTime(timeElapsed = timeElapsed, length = length)
        Seekbar(
            timeElapsed = timeElapsed,
            length = length,
            knobSize = knobSize,
            onMoveLeft = rewind,
            onMoveRight = skip,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ElapsedTime(
    timeElapsed: Duration,
    length: Duration,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    val elapsed: String =
        stringResource(
            R.string.minutes_seconds,
            timeElapsed.toMinutes(),
            timeElapsed.toSeconds() % 60
        )
    val l: String =
        stringResource(R.string.minutes_seconds, length.toMinutes(), length.toSeconds() % 60)
    Text(
        text = stringResource(R.string.elapsed_time, elapsed, l),
        style = style,
        modifier = modifier
    )
}

@Composable
private fun NoEpisodeInQueue(
    backToHome: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Column {
            Text(
                text = stringResource(id = R.string.display_nothing_in_queue),
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(height = JetcasterAppDefaults.gap.paragraph))
            Text(text = stringResource(id = R.string.message_nothing_in_queue))
            Button(
                onClick = backToHome,
                modifier = Modifier.focusRequester(focusRequester = focusRequester)
            ) {
                Text(text = stringResource(id = R.string.label_back_to_home))
            }
        }
    }
}

@Composable
private fun PlayerQueueOverlay(
    playerEpisodeList: EpisodeList,
    onSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(space = JetcasterAppDefaults.gap.item),
    contentPadding: PaddingValues = PaddingValues(),
    contentAlignment: Alignment = Alignment.BottomStart,
    scrim: DrawScope.() -> Unit = {
        val brush: Brush = Brush.verticalGradient(
            listOf(Color.Transparent, Color.Black),
        )
        drawRect(brush = brush, blendMode = BlendMode.Multiply)
    },
    offset: DpOffset = DpOffset.Zero,
) {
    var hasFocus: Boolean by remember { mutableStateOf(false) }
    val actualOffset: DpOffset = if (hasFocus) {
        DpOffset.Zero
    } else {
        offset
    }
    Box(
        modifier = modifier.drawWithCache {
            onDrawBehind {
                if (hasFocus) {
                    scrim()
                }
            }
        },
        contentAlignment = contentAlignment,
    ) {
        EpisodeRow(
            playerEpisodeList = playerEpisodeList,
            onSelected = onSelected,
            horizontalArrangement = horizontalArrangement,
            contentPadding = contentPadding,
            modifier = Modifier
                .offset(x = actualOffset.x, y = actualOffset.y)
                .onFocusChanged { hasFocus = it.hasFocus }
        )
    }
}
