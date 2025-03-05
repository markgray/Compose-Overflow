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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.example.jetcaster.R
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.HtmlTextContainer
import com.example.jetcaster.designsystem.component.ImageBackgroundColorScrim
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.tooling.DevicePreviews
import com.example.jetcaster.util.isBookPosture
import com.example.jetcaster.util.isSeparatingPosture
import com.example.jetcaster.util.isTableTopPosture
import com.example.jetcaster.util.verticalGradientScrim
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.VerticalTwoPaneStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration

/**
 * Stateful version of the Podcast player
 *
 * This function displays the user interface for playing audio or video content. It utilizes
 * the provided [PlayerViewModel] to manage the playback state and control actions.
 * It also adapts its layout based on the [windowSizeClass] and [displayFeatures] for optimal
 * presentation across different screen sizes and foldable devices.
 *
 * We start by initializing our [PlayerUiState] variable `uiState` with the current value of the
 * [PlayerViewModel]'s `uiState` property. Then we compose the Stateless version of the Player
 * screen, with its arguments:
 *  - `uiState`: The current state of the player, our [PlayerUiState] variable `uiState`
 *  - `windowSizeClass`: The current window size class representing the current screen size, our
 *  [WindowSizeClass] parameter [windowSizeClass].
 *  - `displayFeatures`: A list of [DisplayFeature]s representing the physical characteristics, of
 *  the display, such as folds or hinges, our [List] of [DisplayFeature] parameter [displayFeatures].
 *  - `onBackPress`: Callback invoked when the user presses the back button, our lambda parameter
 *  [onBackPress].
 *  - `onAddToQueue`: Callback invoked when the user adds an episode to the queue, a function
 *  reference to the [PlayerViewModel.onAddToQueue] method of our [PlayerViewModel] parameter
 *  [viewModel].
 *  - `onStop`: Callback invoked when the user stops the playback, a function reference to the
 *  [PlayerViewModel.onStop] method of our [PlayerViewModel] parameter [viewModel].
 *  - `playerControlActions`: A [PlayerControlActions] object representing the actions available
 *  for the player controls. We pass a new instance of [PlayerControlActions] with its arguments:
 *      - `onPlayPress`: Callback invoked when the user presses the play button, a function
 *      reference to the [PlayerViewModel.onPlay] method of our [PlayerViewModel] parameter
 *      [viewModel].
 *      - `onPausePress`: Callback invoked when the user presses the pause button, a function
 *      reference to the [PlayerViewModel.onPause] method of our [PlayerViewModel] parameter
 *      [viewModel].
 *      - `onAdvanceBy`: Callback invoked when the user advances the playback by a specified
 *      [Duration], a function reference to the [PlayerViewModel.onAdvanceBy] method of our
 *      [PlayerViewModel] parameter [viewModel].
 *      - `onRewindBy`: Callback invoked when the user rewinds the playback by a specified
 *      [Duration], a function reference to the [PlayerViewModel.onRewindBy] method of our
 *      [PlayerViewModel] parameter [viewModel].
 *      - `onSeekingStarted`: Callback invoked when the user starts seeking the playback, a
 *      function reference to the [PlayerViewModel.onSeekingStarted] method of our [PlayerViewModel]
 *      parameter [viewModel].
 *      - `onSeekingFinished`: Callback invoked when the user finishes seeking the playback, a
 *      function reference to the [PlayerViewModel.onSeekingFinished] method of our [PlayerViewModel]
 *      parameter [viewModel].
 *      - `onNext`: Callback invoked when the user presses the next button, a function reference to
 *      the [PlayerViewModel.onNext] method of our [PlayerViewModel] parameter [viewModel].
 *      - `onPrevious`: Callback invoked when the user presses the previous button, a function
 *      reference to the [PlayerViewModel.onPrevious] method of our [PlayerViewModel] parameter
 *      [viewModel].
 *
 * @param windowSizeClass The window size class representing the current screen size. Used to
 * determine the appropriate layout.
 * @param displayFeatures A list of [DisplayFeature]s representing the physical characteristics
 * of the display, such as folds or hinges. Used for adapting the UI for foldable devices.
 * @param onBackPress Callback invoked when the user presses the back button.
 * @param viewModel The [PlayerViewModel] responsible for managing the player state and actions.
 * By default it uses a [hiltViewModel]
 */
@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState: PlayerUiState = viewModel.uiState
    PlayerScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
        onBackPress = onBackPress,
        onAddToQueue = viewModel::onAddToQueue,
        onStop = viewModel::onStop,
        playerControlActions = PlayerControlActions(
            onPlayPress = viewModel::onPlay,
            onPausePress = viewModel::onPause,
            onAdvanceBy = viewModel::onAdvanceBy,
            onRewindBy = viewModel::onRewindBy,
            onSeekingStarted = viewModel::onSeekingStarted,
            onSeekingFinished = viewModel::onSeekingFinished,
            onNext = viewModel::onNext,
            onPrevious = viewModel::onPrevious,
        ),
    )
}

/**
 * Stateless version of the Player screen, displays the UI for playing an episode.
 *
 * It handles the overall layout, including the background, playback controls, and
 * loading state. It also manages side effects such as adding an episode to the queue
 * and cleaning up when the screen is no longer visible.
 *
 * First we compose a [DisposableEffect] whose `key1` argument is `Unit`, and in its
 * [DisposableEffectScope] `effect` lambda we use [DisposableEffectScope.onDispose] to
 * register a call to our lambda parameter [onStop] to be run when the [DisposableEffect]
 * leaves the composition. Next we initialize and remember our [CoroutineScope] variable
 * `coroutineScope` to a new instance of [CoroutineScope] using [rememberCoroutineScope],
 * initialize our [String] variable `snackBarText` to the [String] with resource ID
 * `R.string.episode_added_to_your_queue` ("Episode added to your queue") using [stringResource],
 * and initialize and remember our [SnackbarHostState] variable `snackbarHostState` to a new
 * instance.
 *
 * Our root Composable is a [Scaffold] whose `snackbarHost` argument is a lambda that composes
 * a [SnackbarHost] with the `hostState` argument set to our [SnackbarHostState] variable
 * `snackbarHostState`, and whose `modifier` argument is our [Modifier] parameter [modifier].
 * In the `content` composable lambda argument we accept the [PaddingValues] passed the lambda
 * in our variable `contentPadding`. Then if the [EpisodePlayerState.currentEpisode] of the
 * [PlayerUiState.episodePlayerState] property of our [PlayerUiState] parameter [uiState] is not
 * `null` we compose a [PlayerContentWithBackground] with the arguments:
 *  - `uiState`: The current state of the player, our [PlayerUiState] parameter [uiState].
 *  - `windowSizeClass`: The current window size class representing the current screen size, our
 *  [WindowSizeClass] parameter [windowSizeClass].
 *  - `displayFeatures`: A list of [DisplayFeature]s representing the physical characteristics, of
 *  the display, such as folds or hinges, our [List] of [DisplayFeature] parameter [displayFeatures].
 *  - `onBackPress`: Callback invoked when the user presses the back button, our lambda parameter
 *  [onBackPress].
 *  - `onAddToQueue`: Callback invoked when the user adds an episode to the queue, a lambda that
 *  uses our [CoroutineScope] variable `coroutineScope` to launch a coroutine that shows a snackbar
 *  by calling the [SnackbarHostState.showSnackbar] method of our [SnackbarHostState] variable
 *  `snackbarHostState` with the [String] variable `snackBarText` as its argument, then calls our
 *  lambda parameter [onAddToQueue].
 *  - `playerControlActions`: A [PlayerControlActions] object representing the actions available
 *  to control the player, our [PlayerControlActions] parameter [playerControlActions].
 *  - `contentPadding`: The [PaddingValues] passed to the lambda in our variable `contentPadding`.
 *
 * If the [EpisodePlayerState.currentEpisode] of the [PlayerUiState.episodePlayerState] property
 * is `null` on the otherhand, we compose a [FullScreenLoading] composable.
 *
 * @param uiState The current UI state of the player, including the episode being played, playback
 * queue, playback, playback status (is it playing?), and time elapsed.
 * @param windowSizeClass The window size class that represents the screen size.
 * @param displayFeatures A list of display features, which can be used for handling foldable or
 * dual-screen devices.
 * @param onBackPress A callback to be triggered when the user presses the back button.
 * @param onAddToQueue A callback to be triggered when the user requests to add the current
 * episode to the playback queue.
 * @param onStop A callback to be triggered when the screen is disposed or the playback is stopped.
 * This is typically used for cleanup operations like releasing resources.
 * @param playerControlActions An object containing actions to control the player like play,
 * pause, seek etc.
 * @param modifier [Modifier] for styling and layout of the screen. Our caller, the Stateful version
 * of the Podcast player, does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
private fun PlayerScreen(
    uiState: PlayerUiState,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    onAddToQueue: () -> Unit,
    onStop: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    DisposableEffect(key1 = Unit) {
        onDispose {
            onStop()
        }
    }

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val snackBarText: String = stringResource(id = R.string.episode_added_to_your_queue)
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) { contentPadding: PaddingValues ->
        if (uiState.episodePlayerState.currentEpisode != null) {
            PlayerContentWithBackground(
                uiState = uiState,
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                onBackPress = onBackPress,
                onAddToQueue = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = snackBarText)
                    }
                    onAddToQueue()
                },
                playerControlActions = playerControlActions,
                contentPadding = contentPadding,
            )
        } else {
            FullScreenLoading()
        }
    }
}

/**
 * Displays a background image scrim for the player screen.
 *
 * This composable uses the podcast image URL from the provided [episode] to display a background
 * image. It applies a semi-transparent scrim (90% opaque surface color) over the image to ensure
 * text and other UI elements on top are legible. If no episode or podcast image URL is available,
 * it will default to displaying a solid color background.
 *
 * @param episode The [PlayerEpisode] containing the podcast image URL. If null, a default solid
 * background will be displayed.
 * @param modifier Modifier to be applied to the background image.
 */
@Composable
private fun PlayerBackground(
    episode: PlayerEpisode?,
    modifier: Modifier,
) {
    ImageBackgroundColorScrim(
        url = episode?.podcastImageUrl,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = modifier,
    )
}

/**
 * Displays the player content overlaid on a background derived from the current episode.
 *
 * This composable combines the [PlayerBackground] and [PlayerContent] composables to provide a
 * visually rich player experience. It handles the background display and overlays the interactive
 * player controls and information on top.
 *
 * Our root Composable is a [Box] whose `modifier` argument is our [Modifier] parameter [modifier],
 * and whose `contentAlignment` argument is [Alignment.Center] (which aligns its content to the
 * center of the box). In the [BoxScope] `content` composable lambda argument we first compose a
 * [PlayerBackground] with the arguments:
 *  - `episode`: The current episode being played, the [EpisodePlayerState.currentEpisode] of the
 *  [PlayerUiState.episodePlayerState] property of our [PlayerUiState] parameter [uiState].
 *  - `modifier`: a [Modifier.fillMaxSize] with a  chained [Modifier.padding] of our [PaddingValues]
 *  parameter [contentPadding].
 *
 * On top of the background we compose a [PlayerContent] with the arguments:
 *  - `uiState`: The current state of the player, our [PlayerUiState] parameter [uiState].
 *  - `windowSizeClass`: The current window size class representing the current screen size, our
 *  [WindowSizeClass] parameter [windowSizeClass].
 *  - `displayFeatures`: A list of [DisplayFeature]s representing the physical characteristics of
 *  the display, such as folds or hinges, our [List] of [DisplayFeature] parameter [displayFeatures].
 *  - `onBackPress`: Callback to be invoked when the user presses the back button, our lambda
 *  parameter [onBackPress].
 *  - `onAddToQueue`: Callback to be invoked when the user adds an episode to the queue, our lambda
 *  parameter [onAddToQueue].
 *  - `playerControlActions`: A [PlayerControlActions] object representing the actions available,
 *  our [PlayerControlActions] parameter [playerControlActions].
 *
 * @param uiState The current [PlayerUiState] UI state of the player.
 * @param windowSizeClass The current window size class, used for adaptive layout.
 * @param displayFeatures A list of [DisplayFeature]s, representing foldable/hinge features on the
 * device.
 * @param onBackPress Callback to be invoked when the user requests to navigate back.
 * @param onAddToQueue Callback to be invoked when the user requests to add the current episode to the queue.
 * @param playerControlActions Actions related to the player, such as play/pause, seek, etc.
 * @param modifier [Modifier] to be applied to the root layout. Our caller [PlayerScreen] does not
 * pass us any so the empty, default, or starter [Modifier] that contains no elements is used.
 * @param contentPadding Padding to be applied around the background. Our caller [PlayerScreen]
 * passes us the [PaddingValues] that the [Scaffold] that holds us passes to its `content` lambda
 * parameter.
 */
@Composable
fun PlayerContentWithBackground(
    uiState: PlayerUiState,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    onAddToQueue: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        PlayerBackground(
            episode = uiState.episodePlayerState.currentEpisode,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPadding)
        )
        PlayerContent(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            displayFeatures = displayFeatures,
            onBackPress = onBackPress,
            onAddToQueue = onAddToQueue,
            playerControlActions = playerControlActions,
        )
    }
}

/**
 * Encapsulates all actions that can be triggered by the player controls.
 *
 * This data class provides a structured way to handle various user interactions
 * with the media player's control interface. Each property represents a specific
 * action, allowing for easy binding to UI elements or other event sources.
 *
 * @property onPlayPress Callback invoked when the user presses the "play" button.
 * @property onPausePress Callback invoked when the user presses the "pause" button.
 * @property onAdvanceBy Callback invoked when the user wants to advance the playback by a specified
 * duration. The provided [Duration] indicates the amount of time to advance.
 * @property onRewindBy Callback invoked when the user wants to rewind the playback by a specified
 * duration. The provided [Duration] indicates the amount of time to rewind.
 * @property onNext Callback invoked when the user wants to skip to the next item in the playback
 * queue.
 * @property onPrevious Callback invoked when the user wants to go back to the previous item in the
 * playback queue.
 * @property onSeekingStarted Callback invoked when the user initiates seeking (e.g., dragging a
 * seek bar).
 * @property onSeekingFinished Callback invoked when the user finishes seeking. The `newElapsed`
 * [Duration] represents the new elapsed time in the media after seeking is completed.
 */
data class PlayerControlActions(
    val onPlayPress: () -> Unit,
    val onPausePress: () -> Unit,
    val onAdvanceBy: (Duration) -> Unit,
    val onRewindBy: (Duration) -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    val onSeekingStarted: () -> Unit,
    val onSeekingFinished: (newElapsed: Duration) -> Unit,
)

/**
 * Composable function that displays the content of the player screen, adapting to different
 * screen sizes, window sizes, and folding features.
 *
 * It utilizes different layouts based on the device's characteristics:
 *  - **Regular Layout:** For single-screen devices without folds.
 *  - **Two-Pane Layout (Vertical):** For devices in tabletop posture or with horizontal
 *  separating folds, displaying content in a top-bottom arrangement.
 *  - **Two-Pane Layout (Horizontal):** For devices in book posture or with a sufficiently
 *  large screen, displaying content side-by-side.
 *
 * First we initialize our [FoldingFeature] variable `foldingFeature` with the first [DisplayFeature]
 * in our [List] of [DisplayFeature] parameter [displayFeatures] that is an instance of
 * [FoldingFeature] (or `null` if no such feature is found). Then if the
 * [WindowSizeClass.windowWidthSizeClass] of our [WindowSizeClass] parameter [windowSizeClass] is
 * [WindowWidthSizeClass.EXPANDED], or the [isBookPosture] of our [FoldingFeature] variable is
 * `true`, or the [isTableTopPosture] of our [FoldingFeature] variable is `true`, or the
 * [isSeparatingPosture] of our [FoldingFeature] variable is `true` we initialize our [Boolean]
 * variable `usingVerticalStrategy` to `true` if the [isTableTopPosture] of our [FoldingFeature]
 * variable is `true` or the [isSeparatingPosture] of our [FoldingFeature] variable is `true` and
 * the [FoldingFeature.orientation] of our [FoldingFeature] variable is
 * [FoldingFeature.Orientation.HORIZONTAL]. Then if the [Boolean] variable `usingVerticalStrategy`
 * is `true` we compose a [TwoPane] with the arguments:
 *  - `first`: A lambda that composes the first pane of the two-pane layout, which is a
 *  [PlayerContentTableTopTop] whose `uiState` argument is our [PlayerUiState] parameter [uiState].
 *  - `second`: A lambda that composes the second pane of the two-pane layout, which is a
 *  [PlayerContentTableTopBottom] whose arguments are:
 *      - `uiState`: The current state of the player, our [PlayerUiState] parameter [uiState].
 *      - `onBackPress`: Callback to be invoked when the user presses the back button, our lambda
 *      parameter [onBackPress].
 *      - `onAddToQueue`: Callback to be invoked when the user adds an episode to the queue, our
 *      lambda parameter [onAddToQueue].
 *      - `playerControlActions`: A [PlayerControlActions] object representing the actions available
 *      our [PlayerControlActions] parameter [playerControlActions].
 *  - `strategy`: A [VerticalTwoPaneStrategy] with a `splitFraction` of 0.5f
 *  - `displayFeatures`: A [List] of [DisplayFeature]s representing the physical characteristics
 *  of the display, such as folds or hinges, our [List] of [DisplayFeature] parameter
 *  [displayFeatures].
 *  - `modifier`: Our [Modifier] parameter [modifier].
 *
 * If `usingVerticalStrategy` is `false` we compose a [Column] whose `modifier` argument is our
 * [Modifier] parameter [modifier] chained to a [Modifier.fillMaxSize], chained to a
 * [Modifier.verticalGradientScrim] whose `color` argument is the [ColorScheme.primary] of our custom
 * [MaterialTheme.colorScheme], whose `startYPercentage` argument is 1f, and whose `endYPercentage`
 * is 0f, chained to a [Modifier.systemBarsPadding], chained to a [Modifier.padding] that adds 8.dp
 * to each `horizontal` side. In the [ColumnScope] `content` composable lambda argument we first
 * compose a [TopAppBar] with its `onBackPress` argument set to our lambda parameter [onBackPress],
 * and its `onAddToQueue` argument set to our lambda parameter [onAddToQueue]. Then we compose a
 * [TwoPane] with the arguments:
 *   - `first`: A lambda that composes the first pane of the two-pane layout, which is a
 *   [PlayerContentBookStart] whose `uiState` argument is our [PlayerUiState] parameter [uiState].
 *   - `second`: A lambda that composes the second pane of the two-pane layout, which is a
 *   [PlayerContentBookEnd] whose `uiState` argument is our [PlayerUiState] parameter [uiState], and
 *   whose `playerControlActions` argument is our [PlayerControlActions] parameter
 *   [playerControlActions].
 *   - `strategy`: A [HorizontalTwoPaneStrategy] with a `splitFraction` of 0.5f.
 *   - `displayFeatures`: A [List] of [DisplayFeature]s representing the physical characteristics
 *   of the display, such as folds or hinges, our [List] of [DisplayFeature] parameter
 *   [displayFeatures].
 *
 * On the other hand, if the display is not appropriate for a two-pane layout, we compose a
 * [PlayerContentRegular] with the arguments:
 *  - `uiState`: The current state of the player, our [PlayerUiState] parameter [uiState].
 *  - `onBackPress`: Callback to be invoked when the user presses the back button, our lambda
 *  parameter [onBackPress].
 *  - `onAddToQueue`: Callback to be invoked when the user adds an episode to the queue, our lambda
 *  parameter [onAddToQueue].
 *  - `playerControlActions`: A [PlayerControlActions] object representing the actions available,
 *  our [PlayerControlActions] parameter [playerControlActions].
 *  - `modifier`: Our [Modifier] parameter [modifier].
 *
 * @param uiState The current UI state of the player.
 * @param windowSizeClass The window size class indicating the screen dimensions.
 * @param displayFeatures A list of display features, such as folding features.
 * @param onBackPress Callback invoked when the back button is pressed.
 * @param onAddToQueue Callback invoked when the "add to queue" action is triggered.
 * @param playerControlActions Actions to control the player (play, pause, etc.).
 * @param modifier [Modifier] for styling and layout customization. Our caller
 * [PlayerContentWithBackground] does not pass us any so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 */
@Composable
fun PlayerContent(
    uiState: PlayerUiState,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    onAddToQueue: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    val foldingFeature: FoldingFeature? =
        displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    // Use a two pane layout if there is a fold impacting layout (meaning it is separating
    // or non-flat) or if we have a large enough width to show both.
    if (
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
        isBookPosture(foldingFeature) ||
        isTableTopPosture(foldingFeature) ||
        isSeparatingPosture(foldingFeature)
    ) {
        // Determine if we are going to be using a vertical strategy (as if laying out
        // both sides in a column). We want to do so if we are in a tabletop posture,
        // or we have an impactful horizontal fold. Otherwise, we'll use a horizontal strategy.
        val usingVerticalStrategy: Boolean =
            isTableTopPosture(foldingFeature) ||
                (
                    isSeparatingPosture(foldingFeature) &&
                        foldingFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
                    )

        if (usingVerticalStrategy) {
            TwoPane(
                first = {
                    PlayerContentTableTopTop(
                        uiState = uiState,
                    )
                },
                second = {
                    PlayerContentTableTopBottom(
                        uiState = uiState,
                        onBackPress = onBackPress,
                        onAddToQueue = onAddToQueue,
                        playerControlActions = playerControlActions,
                    )
                },
                strategy = VerticalTwoPaneStrategy(splitFraction = 0.5f),
                displayFeatures = displayFeatures,
                modifier = modifier,
            )
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalGradientScrim(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                        startYPercentage = 1f,
                        endYPercentage = 0f
                    )
                    .systemBarsPadding()
                    .padding(horizontal = 8.dp)
            ) {
                TopAppBar(
                    onBackPress = onBackPress,
                    onAddToQueue = onAddToQueue,
                )
                TwoPane(
                    first = {
                        PlayerContentBookStart(uiState = uiState)
                    },
                    second = {
                        PlayerContentBookEnd(
                            uiState = uiState,
                            playerControlActions = playerControlActions,
                        )
                    },
                    strategy = HorizontalTwoPaneStrategy(splitFraction = 0.5f),
                    displayFeatures = displayFeatures
                )
            }
        }
    } else {
        PlayerContentRegular(
            uiState = uiState,
            onBackPress = onBackPress,
            onAddToQueue = onAddToQueue,
            playerControlActions = playerControlActions,
            modifier = modifier,
        )
    }
}

/**
 * Composable function that displays the player content for a phone screen.
 *
 * This function is responsible for rendering the main UI components of the player screen,
 * including the top app bar, podcast image, description, slider, and control buttons when
 * the device we are running on is a phone.
 *
 * We start by initializing our [EpisodePlayerState] variable `playerEpisode` to the
 * [PlayerUiState.episodePlayerState] property of our [PlayerUiState] parameter [uiState], and
 * initializing our [PlayerEpisode] variable `currentEpisode` to the
 * [EpisodePlayerState.currentEpisode] of `playerEpisode`, returning without doing anything more
 * if that is `null`. Our root composable is a [Column] whose [Modifier] `modifier` argument chains
 * a [Modifier.fillMaxSize] to our [Modifier] parameter [modifier], to which it chains a
 * [Modifier.verticalGradientScrim] whose `color` is a copy of the [ColorScheme.primary] of our
 * custom [MaterialTheme.colorScheme] with its `alpha` set to 0.50f, whose `startYPercentage` is 1f,
 * and whose `endYPercentage` is 0f, to which is chained a [Modifier.systemBarsPadding], and a
 * [Modifier.padding] that adds 8.dp to each `horizontal` side.
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we first compose a
 * [TopAppBar] whose `onBackPress` argument is our lambda parameter [onBackPress] and whose
 * `onAddToQueue` argument is our [onAddToQueue] lambda parameter. Below the [TopAppBar] we compose
 * another [Column] whose `horizontalAlignment` argument is [Alignment.CenterHorizontally] and whose
 * [Modifier] `modifier` argument is a [Modifier.padding] that adds 8.dp to each `horizontal` side.
 * In its [ColumnScope] `content` composable lambda argument we compose:
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [ColumnScope.weight] whose `weight` is 1f.
 *  - a [PlayerImage] whose `podcastImageUrl` argument is the [PlayerEpisode.podcastImageUrl] property
 *  of our [PlayerEpisode] variable `currentEpisode`, and whose [Modifier] `modifier` argument is a
 *  [ColumnScope.weight] whose `weight` is `10f`.
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [Modifier.height] whose `height` is 32.dp
 *  - a [PodcastDescription] whose `title` argument is the [PlayerEpisode.title] property of our
 *  [PlayerEpisode] variable `currentEpisode`, and whose `podcastName` is the
 *  [PlayerEpisode.podcastName] property of our [PlayerEpisode] variable `currentEpisode`.
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [Modifier.height] whose `height` is 32.dp
 *
 * Underneath the above in the [Column] is yet another [Column] whose `horizontalAlignment` is
 * [Alignment.CenterHorizontally], and whose [Modifier] `modifier` argument is a [ColumnScope.weight]
 * whose `weight` is `10f`. In its [ColumnScope] `content` composable lambda argument we compose:
 *  - a [PlayerSlider] whose `timeElapsed` argument is the [EpisodePlayerState.timeElapsed] property
 *  of our [EpisodePlayerState] variable `playerEpisode`, whose `episodeDuration` is the
 *  [PlayerEpisode.duration] of our [PlayerEpisode] variable `currentEpisode`, whose
 *  `onSeekingStarted` argument is the [PlayerControlActions.onSeekingStarted] property of our
 *  [PlayerControlActions] parameter [playerControlActions], and whose `onSeekingFinished` argument
 *  is the [PlayerControlActions.onSeekingFinished] property of our [PlayerControlActions] parameter
 *  [playerControlActions].
 *  - a [PlayerButtons] whose `hasNext` argument is `true` if the [EpisodePlayerState.queue] field
 *  of our [EpisodePlayerState] variable `playerEpisode` is not empty, whose `isPlaying` argument is
 *  the [EpisodePlayerState.isPlaying] property of our [EpisodePlayerState] variable `playerEpisode`,
 *  whose `onPlayPress` argument is the [PlayerControlActions.onPlayPress] property of our
 *  [PlayerControlActions] parameter [playerControlActions], whose `onPausePress` argument is the
 *  [PlayerControlActions.onPausePress] property of our [PlayerControlActions] parameter
 *  [playerControlActions], whose `onAdvanceBy` argument is the [PlayerControlActions.onAdvanceBy]
 *  property of our [PlayerControlActions] parameter [playerControlActions], whose `onRewindBy`
 *  argument is the [PlayerControlActions.onRewindBy] property of our [PlayerControlActions] parameter
 *  [playerControlActions], whose `onNext` argument is the [PlayerControlActions.onNext] property of
 *  our [PlayerControlActions] parameter [playerControlActions], whose `onPrevious` argument is the
 *  [PlayerControlActions.onPrevious] property of our [PlayerControlActions] parameter
 *  [playerControlActions], and whose [Modifier] `modifier` argument is a [Modifier.padding] that
 *  adds 8.dp to each vertical side.
 *
 * At the very bottom of the outermost [Column] is a [Spacer] whose [Modifier] `modifier` argument
 * is a [ColumnScope.weight] whose `weight` is `1f`.
 *
 * @param uiState The current UI state of the player, including the current episode and playback
 * status.
 * @param onBackPress Callback to be triggered when the back button is pressed.
 * @param onAddToQueue Callback to be triggered when the add to queue button is pressed.
 * @param playerControlActions Actions that can be performed on the player (play, pause, seek, etc.).
 * @param modifier [Modifier] for styling and layout customization. Our caller [PlayerContent] passes
 * us its own [Modifier] parameter which is the empty, default, or starter [Modifier] that contains
 * no elements since its own caller does not pass it a [Modifier].
 */
@Composable
private fun PlayerContentRegular(
    uiState: PlayerUiState,
    onBackPress: () -> Unit,
    onAddToQueue: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    val playerEpisode: EpisodePlayerState = uiState.episodePlayerState
    val currentEpisode: PlayerEpisode = playerEpisode.currentEpisode ?: return
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalGradientScrim(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                startYPercentage = 1f,
                endYPercentage = 0f
            )
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        TopAppBar(
            onBackPress = onBackPress,
            onAddToQueue = onAddToQueue,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.weight(weight = 1f))
            PlayerImage(
                podcastImageUrl = currentEpisode.podcastImageUrl,
                modifier = Modifier.weight(weight = 10f)
            )
            Spacer(modifier = Modifier.height(height = 32.dp))
            PodcastDescription(
                title = currentEpisode.title,
                podcastName = currentEpisode.podcastName
            )
            Spacer(modifier = Modifier.height(height = 32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(weight = 10f)
            ) {
                PlayerSlider(
                    timeElapsed = playerEpisode.timeElapsed,
                    episodeDuration = currentEpisode.duration,
                    onSeekingStarted = playerControlActions.onSeekingStarted,
                    onSeekingFinished = playerControlActions.onSeekingFinished
                )
                PlayerButtons(
                    hasNext = playerEpisode.queue.isNotEmpty(),
                    isPlaying = playerEpisode.isPlaying,
                    onPlayPress = playerControlActions.onPlayPress,
                    onPausePress = playerControlActions.onPausePress,
                    onAdvanceBy = playerControlActions.onAdvanceBy,
                    onRewindBy = playerControlActions.onRewindBy,
                    onNext = playerControlActions.onNext,
                    onPrevious = playerControlActions.onPrevious,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(weight = 1f))
        }
    }
}

/**
 * The UI for the top pane of a tabletop layout.
 *
 * This function renders the top part of the player UI, including a gradient scrim, window insets
 * padding, and the podcast image associated with the currently playing episode. It fetches the
 * necessary data from its [PlayerUiState] parameter [uiState].
 *
 * We start by initializing our [PlayerEpisode] variable `currentEpisode` to the
 * [EpisodePlayerState.currentEpisode] of the [PlayerUiState.episodePlayerState] property of our
 * parameter [uiState], returning without doing anything more if that is `null`.
 *
 * Our root composable is a [Column] whose [Modifier] `modifier` argument chains a
 * [Modifier.fillMaxWidth] to our [Modifier] parameter [modifier], to which it chains a
 * [Modifier.verticalGradientScrim] whose `color` is a copy of the [ColorScheme.primary] of the
 * [ColorScheme.primary] of our custom [MaterialTheme.colorScheme] with its `alpha` set to 0.50f,
 * whose `startYPercentage` is 1f, and whose `endYPercentage` is 0f, to which is chained a
 * [Modifier.windowInsetsPadding] whose `insets` is the [WindowInsets.Companion.systemBars] with
 * `only` the [WindowInsetsSides.Companion.Horizontal] and [WindowInsetsSides.Companion.Top] sides,
 * and at the end of the chain is a [Modifier.padding] that adds 32.dp to each `all` sides. The
 * `horizontalAlignment` argument of the [Column] is [Alignment.CenterHorizontally]. In the
 * [ColumnScope] `content` composable lambda argument we compose a [PlayerImage] whose
 * `podcastImageUrl` argument is the [PlayerEpisode.podcastImageUrl] of our [PlayerEpisode] variable
 * `episode`.
 *
 * @param uiState The current UI state of the player, containing information about the currently
 * playing episode.
 * @param modifier Modifier to be applied to the root layout of this composable. Our caller
 * [PlayerContent] does not pass us any so the empty, default, or starter [Modifier] that contains
 * no elements is used.
 */
@Composable
private fun PlayerContentTableTopTop(
    uiState: PlayerUiState,
    modifier: Modifier = Modifier
) {
    // Content for the top part of the screen
    val episode: PlayerEpisode = uiState.episodePlayerState.currentEpisode ?: return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalGradientScrim(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                startYPercentage = 1f,
                endYPercentage = 0f
            )
            .windowInsetsPadding(
                insets = WindowInsets.systemBars.only(
                    sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                )
            )
            .padding(all = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerImage(podcastImageUrl = episode.podcastImageUrl)
    }
}

/**
 * The UI for the bottom pane of a tabletop layout.
 *
 * This composable is responsible for rendering:
 *  - The top app bar with back and add-to-queue actions.
 *  - The podcast episode title and podcast name.
 *  - The player control buttons (play/pause, next, previous, rewind, forward).
 *  - The player progress slider.
 *
 * It arranges these elements vertically, with the app bar and episode information at the top,
 * followed by a spacer and then the control buttons and slider at the bottom.
 *
 * We start by initializing our [EpisodePlayerState] variable `episodePlayerState` to the
 * [PlayerUiState.episodePlayerState] property of our [PlayerUiState] parameter [uiState], and
 * initializing our [PlayerEpisode] variable `episode` to the [EpisodePlayerState.currentEpisode]
 * of `playerEpisode`, returning without doing anything more if that is `null`.
 *
 * Our root composable is a [Column] whose [Modifier] `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.windowInsetsPadding] whose `insets` argument is the
 * [WindowInsets.Companion.systemBars] with `only` the [WindowInsetsSides.Companion.Horizontal] and
 * [WindowInsetsSides.Companion.Bottom] sides, and at the end of the chain is a [Modifier.padding]
 * that adds 32.dp to each `horizontal` side, and 8.dp to each `vertical` side, and the
 * `horizontalAlignment` argument of the [Column] is [Alignment.CenterHorizontally]. In the
 * [ColumnScope] `content` composable lambda argument we compose:
 *  - a [TopAppBar] whose `onBackPress` argument is our lambda parameter [onBackPress] and whose
 *  `onAddToQueue` argument is our [onAddToQueue] lambda parameter.
 *  - a [PodcastDescription] whose `title` argument is the [PlayerEpisode.title] of our
 *  [PlayerEpisode] variable `episode`, whose `podcastName` argument is the [PlayerEpisode.podcastName]
 *  of our [PlayerEpisode] variable `episode`, and whose [TextStyle] `titleTextStyle` argument is the
 *  [Typography.titleLarge] of our custom [MaterialTheme.typography].
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [ColumnScope.weight] whose `weight`
 *  is `0.5f`.
 *  - a [Column] whose `horizontalAlignment` argument is [Alignment.CenterHorizontally], and whose
 *  [Modifier] `modifier` argument is a [ColumnScope.weight] whose `weight` is `10f`.
 *
 * In the [ColumnScope] `content` composable lambda argument of the inner [Column] we compose a
 * [PlayerButtons] and a [PlayerSlider]. The arguments of the [PlayerButtons] are:
 *  - `hasNext`: `true` if the [EpisodePlayerState.queue] field of our [EpisodePlayerState] variable
 *  `episodePlayerState` is not empty.
 *  - `isPlaying`: The [EpisodePlayerState.isPlaying] property of our [EpisodePlayerState] variable
 *  `episodePlayerState`.
 *  - `onPlayPress`: The [PlayerControlActions.onPlayPress] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `onPausePress`: The [PlayerControlActions.onPausePress] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `playerButtonSize` is 92.dp.
 *  - `onAdvanceBy`: The [PlayerControlActions.onAdvanceBy] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `onRewindBy`: The [PlayerControlActions.onRewindBy] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `onNext`: The [PlayerControlActions.onNext] property of our [PlayerControlActions] parameter
 *  parameter [playerControlActions].
 *  - `onPrevious`: The [PlayerControlActions.onPrevious] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `modifier`: A [Modifier.padding] that adds 8.dp to the `top` side.
 *
 * The arguments of the [PlayerSlider] are:
 *  - `timeElapsed`: The [EpisodePlayerState.timeElapsed] property of our [EpisodePlayerState]
 *  variable `episodePlayerState`.
 *  - `episodeDuration`: The [PlayerEpisode.duration] property of our [PlayerEpisode] variable
 *  `episode`.
 *  - `onSeekingStarted`: The [PlayerControlActions.onSeekingStarted] property of our
 *  [PlayerControlActions] parameter [playerControlActions].
 *  - `onSeekingFinished`: The [PlayerControlActions.onSeekingFinished] property of our
 *  [PlayerControlActions] parameter [playerControlActions].
 *
 * @param uiState The [PlayerUiState] containing the current state of the player, including the
 * current episode and playback state.
 * @param onBackPress Callback function to be invoked when the back button in the top app bar
 * is pressed.
 * @param onAddToQueue Callback function to be invoked when the add-to-queue button in the
 * top app bar is pressed.
 * @param playerControlActions An object containing callback functions for controlling the player
 * (play, pause, next, previous, seek).
 * @param modifier [Modifier] for styling and layout customization of the container [Column]. Our
 * caller [PlayerContent] does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
private fun PlayerContentTableTopBottom(
    uiState: PlayerUiState,
    onBackPress: () -> Unit,
    onAddToQueue: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    val episodePlayerState: EpisodePlayerState = uiState.episodePlayerState
    val episode: PlayerEpisode = uiState.episodePlayerState.currentEpisode ?: return
    // Content for the table part of the screen
    Column(
        modifier = modifier
            .windowInsetsPadding(
                insets = WindowInsets.systemBars.only(
                    sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            onBackPress = onBackPress,
            onAddToQueue = onAddToQueue,
        )
        PodcastDescription(
            title = episode.title,
            podcastName = episode.podcastName,
            titleTextStyle = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.weight(weight = 0.5f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(weight = 10f)
        ) {
            PlayerButtons(
                hasNext = episodePlayerState.queue.isNotEmpty(),
                isPlaying = episodePlayerState.isPlaying,
                onPlayPress = playerControlActions.onPlayPress,
                onPausePress = playerControlActions.onPausePress,
                playerButtonSize = 92.dp,
                onAdvanceBy = playerControlActions.onAdvanceBy,
                onRewindBy = playerControlActions.onRewindBy,
                onNext = playerControlActions.onNext,
                onPrevious = playerControlActions.onPrevious,
                modifier = Modifier.padding(top = 8.dp)
            )
            PlayerSlider(
                timeElapsed = episodePlayerState.timeElapsed,
                episodeDuration = episode.duration,
                onSeekingStarted = playerControlActions.onSeekingStarted,
                onSeekingFinished = playerControlActions.onSeekingFinished
            )
        }
    }
}

/**
 * The UI for the start pane of a book layout.
 *
 * This composable shows the podcast information for the currently playing episode. It includes the
 * episode title, podcast name, and episode summary. If no episode is currently playing, it will
 * not render any content.
 *
 * We start by initializing our [PlayerEpisode] variable `episode` to the
 * [EpisodePlayerState.currentEpisode] property of the [PlayerUiState.episodePlayerState] property
 * of our [PlayerUiState] parameter [uiState], returning without doing anything more if that is
 * `null`.
 *
 * Then our root composable is a [Column] whose [Modifier] `modifier` argument chains to our
 * [Modifier] parameter [modifier] a [Modifier.fillMaxSize], to which it chains a
 * [Modifier.verticalScroll] whose `state` is a [rememberScrollState], and at the end of the
 * chain is a [Modifier.padding] that adds 40.dp to each `vertical` side, and 16.dp to each
 * `horizontal` side, and the `horizontalAlignment` argument of the [Column] is
 * [Alignment.CenterHorizontally].
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we compose a
 * [PodcastInformation] whose arguments are:
 *  - `title`: The [PlayerEpisode.title] property of our [PlayerEpisode] variable `episode`.
 *  - `name`: The [PlayerEpisode.podcastName] property of our [PlayerEpisode] variable `episode`.
 *  - `summary`: The [PlayerEpisode.summary] property of our [PlayerEpisode] variable `episode`.
 *
 * @param uiState The current UI state of the player, providing access to the episode data.
 * @param modifier [Modifier] to be applied to the layout. Our caller [PlayerContent] does not pass
 * us any so the empty, default, or starter [Modifier] that contains no elements is used.
 */
@Composable
private fun PlayerContentBookStart(
    uiState: PlayerUiState,
    modifier: Modifier = Modifier
) {
    val episode: PlayerEpisode = uiState.episodePlayerState.currentEpisode ?: return
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(
                vertical = 40.dp,
                horizontal = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PodcastInformation(
            title = episode.title,
            name = episode.podcastName,
            summary = episode.summary,
        )
    }
}

/**
 * The UI for the end pane of a book layout.
 *
 * This composable shows the player's main controls, including the podcast image, a slider for
 * seeking, and buttons for play/pause, rewind/fast-forward, and navigating to the previous/next
 * episodes. It is displayed when an episode is selected and ready to be played.
 *
 * We start by initializing our [EpisodePlayerState] variable `episodePlayerState` to the
 * [PlayerUiState.episodePlayerState] property of our [PlayerUiState] parameter [uiState], and
 * initializing our [PlayerEpisode] variable `episode` to the [EpisodePlayerState.currentEpisode]
 * of `episodePlayerState` returning without doing anything more if that is `null`.
 *
 * Then our root composable is a [Column] whose [Modifier] `modifier` argument chains to our
 * [Modifier] parameter [modifier] a [Modifier.fillMaxSize], to which it chains a [Modifier.padding]
 * that adds 8.dp to `all` sides. The `horizontalAlignment` argument of the [Column] is
 * [Alignment.CenterHorizontally], and the `verticalArrangement` argument of the [Column] is
 * [Arrangement.SpaceAround]. In the [ColumnScope] `content` composable lambda argument of the
 * [Column] we compose a [PlayerImage], a [PlayerSlider], and a [PlayerButtons]. The arguments of
 * the [PlayerImage] are:
 *  - `podcastImageUrl`: The [PlayerEpisode.podcastImageUrl] property of our [PlayerEpisode] variable
 *  `episode`.
 *  - `modifier`: A [Modifier.padding] that adds 16.dp to each `vertical` side, with a
 *  [ColumnScope.weight] whose `weight` is 1f chained to that.
 *
 * The arguments of the [PlayerSlider] are:
 *  - `timeElapsed`: The [EpisodePlayerState.timeElapsed] property of our [EpisodePlayerState]
 *  variable `episodePlayerState`.
 *  - `episodeDuration`: The [PlayerEpisode.duration] property of our [PlayerEpisode] variable
 *  `episode`.
 *  - `onSeekingStarted`: The [PlayerControlActions.onSeekingStarted] property of our
 *  [PlayerControlActions] parameter [playerControlActions].
 *  - `onSeekingFinished`: The [PlayerControlActions.onSeekingFinished] property of our
 *  [PlayerControlActions] parameter [playerControlActions].
 *
 * The arguments of the [PlayerButtons] are:
 *  - `hasNext`: `true` if the [EpisodePlayerState.queue] field of our [EpisodePlayerState]
 *  variable `episodePlayerState` is not empty.
 *  - `isPlaying`: The [EpisodePlayerState.isPlaying] property of our [EpisodePlayerState]
 *  variable `episodePlayerState`.
 *  - `onPlayPress`: The [PlayerControlActions.onPlayPress] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `onPausePress`: The [PlayerControlActions.onPausePress] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `onAdvanceBy`: The [PlayerControlActions.onAdvanceBy] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `onRewindBy`: The [PlayerControlActions.onRewindBy] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `onNext`: The [PlayerControlActions.onNext] property of our [PlayerControlActions] parameter
 *  [playerControlActions].
 *  - `onPrevious`: The [PlayerControlActions.onPrevious] property of our [PlayerControlActions]
 *  parameter [playerControlActions].
 *  - `modifier`: A [Modifier.padding] that adds 8.dp to each `vertical` side.
 *
 * @param uiState The current UI state of the player, containing information about the episode being
 * played, the playback position, and the state of the queue.
 * @param playerControlActions An object containing actions that can be performed on the player,
 * such as play, pause, seek, and navigate to the next/previous episodes.
 * @param modifier [Modifier] for styling and layout customization. Our caller [PlayerContent] does
 * not pass us any so the empty, default, or starter [Modifier] that contains no elements is used.
 */
@Composable
private fun PlayerContentBookEnd(
    uiState: PlayerUiState,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    val episodePlayerState: EpisodePlayerState = uiState.episodePlayerState
    val episode: PlayerEpisode = episodePlayerState.currentEpisode ?: return
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        PlayerImage(
            podcastImageUrl = episode.podcastImageUrl,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .weight(weight = 1f)
        )
        PlayerSlider(
            timeElapsed = episodePlayerState.timeElapsed,
            episodeDuration = episode.duration,
            onSeekingStarted = playerControlActions.onSeekingStarted,
            onSeekingFinished = playerControlActions.onSeekingFinished,
        )
        PlayerButtons(
            hasNext = episodePlayerState.queue.isNotEmpty(),
            isPlaying = episodePlayerState.isPlaying,
            onPlayPress = playerControlActions.onPlayPress,
            onPausePress = playerControlActions.onPausePress,
            onAdvanceBy = playerControlActions.onAdvanceBy,
            onRewindBy = playerControlActions.onRewindBy,
            onNext = playerControlActions.onNext,
            onPrevious = playerControlActions.onPrevious,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

/**
 * A custom top app bar composable.
 *
 * This composable displays a top app bar with a back button, an "add to queue" button,
 * and a more options button.
 *
 * Our root composable is a [Row] whose [Modifier] `modifier` is a [Modifier.fillMaxWidth]. In its
 * [RowScope] `content` composable lambda argument we compose:
 *  - an [IconButton] whose `onClick` argument is our [onBackPress] lambda parameter, and in whose
 *  `content` composable lambda argument we compose an [Icon] whose `imageVector` argument is
 *  [Icons.AutoMirrored.Filled.ArrowBack], and whose `contentDescription` argument is the string
 *  resource with ID [R.string.cd_back] ("Back").
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [RowScope.weight] whose `weight` is 1f.
 *  - an [IconButton] whose `onClick` argument is our [onAddToQueue] lambda parameter, and in whose
 *  `content` composable lambda argument we compose an [Icon] whose `imageVector` argument is
 *  [Icons.AutoMirrored.Filled.PlaylistAdd], and whose `contentDescription` argument is the string
 *  resource with ID [R.string.cd_add] ("Add").
 *  - an [IconButton] whose `onClick` argument is an empty lambda, and in whose `content` composable
 *  lambda argument we compose an [Icon] whose `imageVector` argument is [Icons.Filled.MoreVert], and
 *  whose `contentDescription` argument is the string resource with ID [R.string.cd_more] ("More").
 *
 * @param onBackPress Callback to be invoked when the back button is clicked.
 * @param onAddToQueue Callback to be invoked when the "add to queue" button is clicked.
 */
@Composable
private fun TopAppBar(
    onBackPress: () -> Unit,
    onAddToQueue: () -> Unit,
) {
    Row(Modifier.fillMaxWidth()) {
        IconButton(onClick = onBackPress) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.cd_back)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onAddToQueue) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = stringResource(id = R.string.cd_add)
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(id = R.string.cd_more)
            )
        }
    }
}

/**
 * Displays an image representing the player's current podcast.
 *
 * This composable shows the podcast's image using the provided URL. It applies
 * specific styling to the image, such as a maximum size, aspect ratio, and rounded corners.
 *
 * Our root composable is a [PodcastImage] whose arguments are:
 *  - `podcastImageUrl`: The URL of the podcast's image, our [String] parameter [podcastImageUrl].
 *  - `contentDescription`: A description of the podcast's image is `null`.
 *  - `contentScale`: The content scale of the image is [ContentScale.Crop].
 *  - `modifier`: The [Modifier] for styling and layout customization, chains to our [Modifier]
 *  parameter [modifier] a [Modifier.sizeIn] that sets the maximum width and height of the
 *  [PodcastImage] to 500.dp, with a [Modifier.aspectRatio] chained to that that sets the aspect
 *  ratio of the [PodcastImage] to 1f, and at the end of the chain is a [Modifier.clip] that applies
 *  the [Shapes.medium] shape of our custom [MaterialTheme.shapes] to the [PodcastImage]. 
 *
 * @param podcastImageUrl The URL of the podcast's image.
 * @param modifier Modifier for styling the image container. Defaults to an empty Modifier.
 *
 * @see PodcastImage
 */
@Composable
private fun PlayerImage(
    podcastImageUrl: String,
    modifier: Modifier = Modifier
) {
    PodcastImage(
        podcastImageUrl = podcastImageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = MaterialTheme.shapes.medium)
    )
}

/**
 * Composable function that displays the title and podcast name.
 *
 * This function renders two [Text] composables: one for the title and another for the podcast name.
 * The title is displayed using a marquee effect for horizontal scrolling if it's too long.
 *
 * The arguments of the first [Text] composable are:
 *  - `text`: The title of the podcast episode, our [String] parameter [title].
 *  - `style`: The [TextStyle] style to be applied to the title, our [TextStyle] parameter
 *  [titleTextStyle].
 *  - `maxLines`: The maximum number of lines to display in the title is `1`.
 *  - `color`: The color of the title text is the [ColorScheme.onSurface] of our custom
 *  [MaterialTheme.colorScheme].
 *  - `modifier`: The [Modifier] for styling and layout customization is a [Modifier.basicMarquee].
 *
 * The arguments of the second [Text] composable are:
 *  - `text`: The name of the podcast, our [String] parameter [podcastName].
 *  - `style`: The [TextStyle] style to be applied to the podcast name, the [Typography.bodyMedium]
 *  of our custom [MaterialTheme.typography].
 *  - `color`: The color of the podcast name text is the [ColorScheme.onSurface] of our custom
 *  [MaterialTheme.colorScheme].
 *  - `maxLines`: The maximum number of lines to display in the podcast name is `1`.
 *
 * @param title The title of the podcast episode.
 * @param podcastName The name of the podcast.
 * @param titleTextStyle The text style to be applied to the title. Defaults to the
 * [Typography.headlineSmall] of our custom [MaterialTheme.typography].
 */
@Composable
private fun PodcastDescription(
    title: String,
    podcastName: String,
    titleTextStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Text(
        text = title,
        style = titleTextStyle,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.basicMarquee()
    )
    Text(
        text = podcastName,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
    )
}

/**
 * Displays the information about a podcast, including its name, title, and summary.
 *
 * This composable function arranges the podcast's name, title, and summary in a vertical column,
 * ensuring they are aligned to the center horizontally. The name and title are displayed with
 * ellipsis if they overflow, and the summary is rendered using the our custom [HtmlTextContainer].
 *
 * Our root composable is a [Column] whose [Modifier] `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.padding] that adds 8.dp to each `horizontal`. Its
 * `verticalArrangement` argument is [Arrangement.spacedBy] whose `space` argument is 32.dp, and its
 * `horizontalAlignment` argument is [Alignment.CenterHorizontally]. In the [ColumnScope] `content`
 * composable lambda argument we compose two [Text]'s and a [HtmlTextContainer].
 *
 * The arguments of the first [Text] composable are:
 *  - `text`: The name of the podcast, our [String] parameter [name].
 *  - `style`: The [TextStyle] style to be applied to the name, our [TextStyle] parameter
 *  [nameTextStyle].
 *  - `maxLines`: The maximum number of lines to display in the name is `1`.
 *  - `overflow`: The overflow behavior of the text is [TextOverflow.Ellipsis].
 *
 * The arguments of the second [Text] composable are:
 *  - `text`: The title of the podcast, our [String] parameter [title].
 *  - `style`: The [TextStyle] style to be applied to the title, our [TextStyle] parameter
 *  [titleTextStyle].
 *  - `maxLines`: The maximum number of lines to display in the title is `1`.
 *  - `overflow`: The overflow behavior of the text is [TextOverflow.Ellipsis].
 *
 * The arguments of the [HtmlTextContainer] composable are:
 *  - `text`: The summary of the podcast, our [String] parameter [summary].
 *
 * In the [HtmlTextContainer]'s `content` composable lambda argument we compose a [Text] whose
 * arguments are:
 *  - `text`: The summary of the podcast, our [String] parameter [summary] which is passed as the
 *  argument to the lambda.
 *  - `style`: The [TextStyle] style to be applied to the text, our the [Typography.bodyMedium] of
 *  our custom [MaterialTheme.typography].
 *  - `color`: The color of the text is the `current` [LocalContentColor].
 *
 * @param title The title of the podcast.
 * @param name The name of the podcast host or creator.
 * @param summary A brief summary or description of the podcast.
 * @param modifier [Modifier] to be applied to the layout. Our caller [PlayerContentBookStart] does
 * not pass us any so the empty, default, or starter [Modifier] that contains no elements is used.
 * @param titleTextStyle The [TextStyle] to be applied to the title text. Defaults to the
 * [Typography.headlineSmall] of our custom [MaterialTheme.typography].
 * @param nameTextStyle The [TextStyle] to be applied to the name text. Defaults to the
 * [Typography.displaySmall] of our custom [MaterialTheme.typography].
 */
@Composable
private fun PodcastInformation(
    title: String,
    name: String,
    summary: String,
    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    nameTextStyle: TextStyle = MaterialTheme.typography.displaySmall,
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(space = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = name,
            style = nameTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = title,
            style = titleTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        HtmlTextContainer(text = summary) {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current
            )
        }
    }
}

/**
 * Formats its [Duration] receiver into a string representation in the format "MM:SS".
 *
 * This function takes a [Duration] and converts it into a [String] showing the minutes and seconds
 * in a digital clock format. It ensures that both minutes and seconds are always represented
 * with two digits (e.g., "05:03" instead of "5:3").
 *
 * @receiver Duration The duration to format.
 * @return A [String] representing the duration in "MM:SS" format.
 */
fun Duration.formatString(): String {
    val minutes = this.toMinutes().toString().padStart(length = 2, padChar = '0')
    val secondsLeft = (this.toSeconds() % 60).toString().padStart(length = 2, padChar = '0')
    return "$minutes:$secondsLeft"
}

/**
 * A composable function that displays a slider for controlling the playback position of an audio
 * or video episode.
 *
 * Our root composable is a [Column] whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth]
 * with a [Modifier.padding] that adds 16.dp to each `horizontal` side chained to that. In the
 * [ColumnScope] `content` composable lambda argument we first initialize and remember keyed on
 * the value or our [Duration] parameter [timeElapsed] our [MutableState] wrapped [Duration]
 * variable `sliderValue` to an initial value of our [Duration] parameter [timeElapsed]. We then
 * initialize our [Float] variable `maxRange` to the seconds value of our [Duration] parameter
 * [episodeDuration]. We then compose a [Row] whose [Modifier] `modifier` argument is a
 * [Modifier.fillMaxWidth]. In its [RowScope] `content` composable lambda argument we compose a
 * [Text] whose arguments are:
 *  - `text`: The current elapsed time of the episode, the formatted [String] of our [MutableState]
 *  wrapped [Duration] variable `sliderValue`.
 *  - `style`: The [TextStyle] style to be applied to the text, the [Typography.bodyMedium] of our
 *  custom [MaterialTheme.typography].
 *  - `color`: The color of the text is the [ColorScheme.onSurfaceVariant] of our custom
 *  [MaterialTheme.colorScheme].
 *
 * Below that in the [ColumnScope] `content` composable lambda argument we compose a [Slider] whose
 * arguments are:
 *  - `value`: The current position of the slider, the [Float] value of our [MutableState] wrapped
 *  variable `sliderValue`.
 *  - `valueRange`: The range of values the slider can be set to, from 0f to our [Float] variable
 *  `maxRange`.
 *  - `onValueChange`: A callback function that is invoked when the value of the slider is changed,
 *  a lambda that takes a [Float] as its parameter in `it`, calls our lambda parameter
 *  [onSeekingStarted], then sets the value of our [MutableState] wrapped [Duration] variable
 *  `sliderValue` to the [Duration] value of `it`.
 *  - `onValueChangeFinished`: A callback function that is invoked when the user finishes interacting
 *  with the slider, a lambda that takes no parameters and calls our lambda parameter
 *  [onSeekingFinished] with the current value of our [MutableState] wrapped [Duration] variable
 *  `sliderValue`.
 *
 * @param timeElapsed The current elapsed time of the episode.
 * @param episodeDuration The total duration of the episode. If null, the slider will be disabled
 * and set to 0.
 * @param onSeekingStarted A callback function to be invoked when the user starts interacting with
 * the slider. This is useful for pausing playback or showing a loading indicator.
 * @param onSeekingFinished A callback function to be invoked when the user finishes interacting
 * with the slider. It receives the new elapsed time as a [Duration] which should be applied to the
 * media player.
 */
@Composable
private fun PlayerSlider(
    timeElapsed: Duration,
    episodeDuration: Duration?,
    onSeekingStarted: () -> Unit,
    onSeekingFinished: (newElapsed: Duration) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        var sliderValue: Duration by remember(timeElapsed) { mutableStateOf(timeElapsed) }
        val maxRange: Float = (episodeDuration?.toSeconds() ?: 0).toFloat()

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${sliderValue.formatString()} • ${episodeDuration?.formatString()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Slider(
            value = sliderValue.seconds.toFloat(),
            valueRange = 0f..maxRange,
            onValueChange = {
                onSeekingStarted()
                sliderValue = Duration.ofSeconds(it.toLong())
            },
            onValueChangeFinished = { onSeekingFinished(sliderValue) }
        )
    }
}

@Composable
private fun PlayerButtons(
    hasNext: Boolean,
    isPlaying: Boolean,
    onPlayPress: () -> Unit,
    onPausePress: () -> Unit,
    onAdvanceBy: (Duration) -> Unit,
    onRewindBy: (Duration) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val sideButtonsModifier = Modifier
            .size(sideButtonSize)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        val primaryButtonModifier = Modifier
            .size(playerButtonSize)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = stringResource(R.string.cd_skip_previous),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = sideButtonsModifier
                .clickable(enabled = isPlaying, onClick = onPrevious)
                .alpha(if (isPlaying) 1f else 0.25f)
        )
        Image(
            imageVector = Icons.Filled.Replay10,
            contentDescription = stringResource(R.string.cd_replay10),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = sideButtonsModifier
                .clickable {
                    onRewindBy(Duration.ofSeconds(10))
                }
        )
        if (isPlaying) {
            Image(
                imageVector = Icons.Outlined.Pause,
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable {
                        onPausePress()
                    }
            )
        } else {
            Image(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = stringResource(R.string.cd_play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable {
                        onPlayPress()
                    }
            )
        }
        Image(
            imageVector = Icons.Filled.Forward10,
            contentDescription = stringResource(R.string.cd_forward10),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = sideButtonsModifier
                .clickable {
                    onAdvanceBy(Duration.ofSeconds(10))
                }
        )
        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = stringResource(R.string.cd_skip_next),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = sideButtonsModifier
                .clickable(enabled = hasNext, onClick = onNext)
                .alpha(if (hasNext) 1f else 0.25f)
        )
    }
}

/**
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

/**
 *
 */
@Preview
@Composable
fun TopAppBarPreview() {
    JetcasterTheme {
        TopAppBar(
            onBackPress = {},
            onAddToQueue = {},
        )
    }
}

/**
 *
 */
@Preview
@Composable
fun PlayerButtonsPreview() {
    JetcasterTheme {
        PlayerButtons(
            hasNext = false,
            isPlaying = true,
            onPlayPress = {},
            onPausePress = {},
            onAdvanceBy = {},
            onRewindBy = {},
            onNext = {},
            onPrevious = {},
        )
    }
}

/**
 *
 */
@Suppress("RedundantValueArgument")
@DevicePreviews
@Composable
fun PlayerScreenPreview() {
    JetcasterTheme {
        BoxWithConstraints {
            PlayerScreen(
                PlayerUiState(
                    episodePlayerState = EpisodePlayerState(
                        currentEpisode = PlayerEpisode(
                            title = "Title",
                            duration = Duration.ofHours(2),
                            podcastName = "Podcast",
                        ),
                        isPlaying = false,
                        queue = listOf(
                            PlayerEpisode(),
                            PlayerEpisode(),
                            PlayerEpisode(),
                        )
                    ),
                ),
                displayFeatures = emptyList(),
                windowSizeClass = WindowSizeClass.compute(maxWidth.value, maxHeight.value),
                onBackPress = { },
                onAddToQueue = {},
                onStop = {},
                playerControlActions = PlayerControlActions(
                    onPlayPress = {},
                    onPausePress = {},
                    onAdvanceBy = {},
                    onRewindBy = {},
                    onSeekingStarted = {},
                    onSeekingFinished = {},
                    onNext = {},
                    onPrevious = {},
                )
            )
        }
    }
}
