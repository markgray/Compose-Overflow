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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
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
import androidx.tv.material3.Typography
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

/**
 * Displays an indicator showing the elapsed time of a media item and a seek bar
 * to control the playback position.
 *
 * Our root composable is a [Column] whose arguments are:
 *  - `modifier` is our [Modifier] parameter [modifier].
 *  - `verticalArrangement` is an [Arrangement.spacedBy] whose `space` argument is the constant
 *  `JetcasterAppDefaults.gap.tiny` (4.dp)
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we compose an
 * [ElapsedTime] whose arguments are:
 *  - `timeElapsed` is our [Duration] parameter [timeElapsed].
 *  - `length` is our [Duration] parameter [length].
 *
 * Next in the [Column] we compose a [Seekbar] whose arguments are:
 *  - `timeElapsed` is our [Duration] parameter [timeElapsed].
 *  - `length` is our [Duration] parameter [length].
 *  - `knobSize` is our [Dp] parameter [knobSize].
 *  - `onMoveLeft` is our lambda parameter [rewind].
 *  - `onMoveRight` is our lambda parameter [skip].
 *  - `modifier` is a [Modifier.fillMaxWidth].
 *
 * @param timeElapsed The [Duration] of time that has elapsed in the media item.
 * @param length The total [Duration] of the media item.
 * @param skip A lambda function to be called when the user wants to skip forward.
 * @param rewind A lambda function to be called when the user wants to rewind.
 * @param modifier [Modifier] to be applied to the outer Column layout. Our caller, [PlayerControl],
 * does not pass us any so the empty, default, or starter [Modifier] that contains no elements is
 * used.
 * @param knobSize The size of the seek bar's knob.
 */
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

/**
 * Displays the elapsed time and the total length of a process, formatted as "MM:SS".
 *
 * This composable function takes the elapsed time and the total length as [Duration] objects
 * and displays them in a user-friendly format: "MM:SS•MM:SS". It uses string resources
 * for localization.
 *
 * We start by initializing our [String] variable `elapsed` with a formatted string created using
 * the format [String] with resource Id `R.string.minutes_seconds` ("%1$02d:%2$02d") and the arguments
 * [Duration.toMinutes] of [Duration] parameter [timeElapsed] and [Duration.toSeconds] modulo 60 of
 * [Duration] parameter [timeElapsed], and initializing our [String] variable `l` with a formatted
 * string created using the same format as `elapsed` but with the arguments [Duration.toMinutes]
 * of [Duration] parameter [length] and [Duration.toSeconds] modulo 60 of [Duration] parameter
 * [length].
 *
 * Then our root composable is a [Text] whose arguments are:
 *  - `text` is our [String] variable `elapsed` and [String] variable `l` formatted using the [String]
 *  format with ID `R.string.elapsed_time` ("%1$s•%2$s").
 *  - `style` is our [TextStyle] parameter [style].
 *  - `modifier` is our [Modifier] parameter [modifier].
 *
 * @param timeElapsed The elapsed time as a [Duration].
 * @param length The total length of the process as a [Duration].
 * @param modifier [Modifier] to apply to the Text composable. Our caller, [ElapsedTimeIndicator],
 * does not pass us any so the empty, default, or starter [Modifier] that contains no elements is
 * used.
 * @param style The text style to apply to the displayed text. Defaults to the [Typography.bodySmall]
 * of our custom [MaterialTheme.typography].
 */
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
        stringResource(
            R.string.minutes_seconds,
            length.toMinutes(),
            length.toSeconds() % 60
        )
    Text(
        text = stringResource(R.string.elapsed_time, elapsed, l),
        style = style,
        modifier = modifier
    )
}

/**
 * Displays a screen indicating that there are no episodes in the queue.
 *
 * This composable displays a message informing the user that their queue is empty.
 * It provides a button that, when clicked, navigates the user back to the home screen.
 *
 * We start by composing a [LaunchedEffect] keyed on the [Unit] object. This ensures that the
 * [LaunchedEffect] block is executed only once, during the initial composition of the composable.
 * In its [CoroutineScope] `block` lambda argument we call the [FocusRequester.requestFocus] method
 * of our [FocusRequester] parameter [focusRequester] to request focus on the "Back to Home" button.
 *
 * Our root composable is a [Box] whose arguments are:
 *  - `contentAlignment` is [Alignment.Center] to center the content within the box both vertically
 *  and horizontally.
 *  - `modifier` is our [Modifier] parameter [modifier].
 *
 * In the [BoxScope] `content` composable lambda argument of the [Box] we compose a [Column], and in
 * its [ColumnScope] `content` composable lambda argument we compose a [Text] whose arguments are:
 *  - `text` is a string resource with ID `R.string.display_nothing_in_queue` ("No episode in the queue").
 *  - `style` is the [TextStyle] of [Typography.displayMedium] of our custom [MaterialTheme.typography].
 *
 * Next in the [Column] we compose a [Spacer] whose `modifier` argument a is a [Modifier.height] whose
 * `height` argument is the constant `JetcasterAppDefaults.gap.paragraph` (16.dp).
 *
 * Next in the [Column] we compose a [Text] whose `text` argument is a string resource with ID
 * `R.string.message_nothing_in_queue` ("Discover the Podcast you want to listen to").
 *
 * Finally in the [Column] we compose a [Button] whose arguments are:
 *  - `onClick` is our [backToHome] lambda parameter.
 *  - `modifier` is a [Modifier.focusRequester] whose `focusRequester` argument is our [FocusRequester]
 *  parameter [focusRequester].
 *  - `content` composable lambda argument of the [Button] is a [Text] whose `text` argument is the
 *  string resource with ID `R.string.label_back_to_home` ("Back to Home").
 *
 * @param backToHome A lambda function that is called when the "Back to Home" button is clicked.
 * This function should handle the navigation back to the home screen.
 * @param modifier [Modifier] to be applied to the layout. Our caller [PlayerScreen] passes us its
 * own [Modifier] parameter which traces back to a [Modifier.fillMaxSize] instance.
 * @param focusRequester [FocusRequester] instance to manage focus. Defaults to a new instance.
 * This is used to automatically focus the "Back to Home" button when the composable is displayed.
 */
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

/**
 * An overlay that displays the current player's queue of episodes.
 *
 * This composable displays a list of episodes in a row, typically at the bottom of the screen,
 * representing the queue of episodes that will be played. It also supports a scrim effect
 * when the overlay is focused, enhancing visual clarity.
 *
 * We start by initializing and remembering our [MutableState] wrapped [Boolean] variable `hasFocus`
 * with `false`. Then we initialize our [DpOffset] variable `actualOffset` to [DpOffset.Zero]
 * if our [Boolean] variable `hasFocus` is `true`, or to our [DpOffset] parameter [offset] if it is
 * `false`.
 *
 * Our root composable is a [Box] whose arguments are:
 *  - `modifier` chains to our [Modifier] parameter [modifier] a [Modifier.drawWithCache] in whose
 *  [CacheDrawScope] `onBuildDrawCache` lambda argument we call the [CacheDrawScope.onDrawBehind]
 *  method and in its [DrawScope] `block` lambda argument we check whether our [Boolean] variable
 *  `hasFocus` is `true` and if it is `true` we call our lambda parameter [scrim] (this draws
 *  [scrim] before the layout content of the [Box] is drawn).
 *  - `contentAlignment` is our [Alignment] parameter [contentAlignment].
 *
 * In the [BoxScope] `content` composable lambda argument of the [Box] we compose a [EpisodeRow]
 * whose arguments are:
 *  - `playerEpisodeList` is our [EpisodeList] parameter [playerEpisodeList].
 *  - `onSelected` is our [onSelected] lambda parameter.
 *  - `horizontalArrangement` is our [Arrangement.Horizontal] parameter [horizontalArrangement].
 *  - `contentPadding` is our [PaddingValues] parameter [contentPadding].
 *  - `modifier` is a [Modifier.offset] whose `x` argument is our [DpOffset.x] of our [DpOffset]
 *  variable `actualOffset` and whose `y` argument is our [DpOffset.y] of our [DpOffset] variable
 *  `actualOffset`, to which is chained a [Modifier.onFocusChanged] in whose `onFocused` lambda
 *  argument we update our [MutableState] variable `hasFocus` with the [FocusState.hasFocus] property
 *  of the [FocusState] passed to the lambda.
 *
 * @param playerEpisodeList The list of episodes to display in the queue.
 * @param onSelected A callback invoked when an episode in the queue is selected. It is provided
 * the selected [PlayerEpisode] as its argument.
 * @param modifier [Modifier] to be applied to the overlay. Our caller [EpisodePlayerWithBackground]
 * passes us a [Modifier.fillMaxSize].
 * @param horizontalArrangement The horizontal arrangement of the episodes within the row.
 * Defaults to spaced evenly with the `space` constant `JetcasterAppDefaults.gap.item` (16.dp)
 * @param contentPadding Padding around the content of the episode row. Defaults to no padding.
 * @param contentAlignment The alignment of the content within the overlay's bounds.
 * Defaults to [Alignment.BottomStart].
 * @param scrim A lambda that defines how the scrim is to be drawn. Defaults to a vertical gradient
 * (a visual effect that colors from [Color.Transparent] to [Color.Black] evenly dispersed).
 * @param offset The offset to apply to the overlay. Defaults to [DpOffset.Zero].
 */
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
                .onFocusChanged { focusState: FocusState -> hasFocus = focusState.hasFocus }
        )
    }
}
