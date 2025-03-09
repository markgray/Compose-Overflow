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

package com.example.jetcaster.ui.podcast

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.ui.shared.Loading
import com.example.jetcaster.ui.tooling.DevicePreviews
import com.example.jetcaster.util.fullWidthItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Displays the details of a podcast, including its description, episodes, and subscription status.
 *
 * This composable handles the different states of the podcast data (loading, ready) and displays
 * the corresponding UI. It also provides callbacks for user interactions, such as navigating to
 * the player, subscribing/unsubscribing, and navigating back.
 *
 * We start by initializing our [State] wrapped [PodcastUiState] variable `state` with the value
 * that the [StateFlow.collectAsStateWithLifecycle] function of the [StateFlow] of [PodcastUiState]
 * field [PodcastDetailsViewModel.state] of our [PodcastDetailsViewModel] field [viewModel] returns.
 * Then we use a `when` to branch based on the types of `state` that a copy in `s` is:
 *  - [PodcastUiState.Loading] -> We compose a [PodcastDetailsLoadingScreen] whose [Modifier]
 *  `modifier` argument is [Modifier.fillMaxSize].
 *  - [PodcastUiState.Ready] -> We compose a [PodcastDetailsScreen] whose arguments are:
 *      - `podcast` is the [PodcastUiState.podcast] field of `s`.
 *      - `episodes` is the [PodcastUiState.episodes] field of `s`.
 *      - `toggleSubscribe` is the [PodcastDetailsViewModel.toggleSusbcribe] function of our
 *      [PodcastDetailsViewModel] field [viewModel].
 *      - `onQueueEpisode` is the [PodcastDetailsViewModel.onQueueEpisode] function of our
 *      [PodcastDetailsViewModel] field [viewModel].
 *      - `navigateToPlayer` is our lambda function that takes an [EpisodeInfo] parameter
 *      [navigateToPlayer]
 *      - `navigateBack` is our lambda parameter that takes no parameters [navigateBack]
 *      - `showBackButton` is our [Boolean] parameter [showBackButton]
 *      - `modifier` is our [Modifier] parameter [modifier]
 *
 * @param viewModel The [PodcastDetailsViewModel] responsible for providing the podcast data and
 * handling user interactions.
 * @param navigateToPlayer A lambda function that takes an [EpisodeInfo] and navigates to the player
 * screen to play the selected episode.
 * @param navigateBack A lambda function that navigates back to the previous screen.
 * @param showBackButton A boolean indicating whether to display a back button in the UI.
 * @param modifier The [Modifier] to be applied to the root composable. Our caller `HomeScreenReady`
 * does not pass us any so the empty, default, or starter [Modifier] that contains no elements is
 * used.
 */
@Composable
fun PodcastDetailsScreen(
    viewModel: PodcastDetailsViewModel,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    val state: PodcastUiState by viewModel.state.collectAsStateWithLifecycle()
    when (val s: PodcastUiState = state) {
        is PodcastUiState.Loading -> {
            PodcastDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
        is PodcastUiState.Ready -> {
            PodcastDetailsScreen(
                podcast = s.podcast,
                episodes = s.episodes,
                toggleSubscribe = viewModel::toggleSusbcribe,
                onQueueEpisode = viewModel::onQueueEpisode,
                navigateToPlayer = navigateToPlayer,
                navigateBack = navigateBack,
                showBackButton = showBackButton,
                modifier = modifier,
            )
        }
    }
}

/**
 * Displays a loading screen for the podcast details.
 *
 * This composable function shows our [Loading] composable while the podcast details are being
 * fetched. We just call [Loading] and pass it our [Modifier] parameter [modifier] as its [Modifier]
 * `modifier` argument.
 *
 * @param modifier Modifier to be applied to the loading screen container. Our caller
 * [PodcastDetailsScreen] passes us a [Modifier.fillMaxSize].
 */
@Composable
private fun PodcastDetailsLoadingScreen(
    modifier: Modifier = Modifier
) {
    Loading(modifier = modifier)
}

/**
 * Displays the details of a podcast, including its information and a list of episodes. (This is the
 * stateless overload of the stateful [PodcastDetailsScreen] which calls us).
 *
 * We start by initializing and remembering our [CoroutineScope] variable `val coroutineScope` to
 * the instance returned by [rememberCoroutineScope], initializing and remembering our
 * [SnackbarHostState] variable `val snackbarHostState` to a new instance, and initializing our
 * [String] variable `val snackBarText` to the [String] with resource ID
 * `R.string.episode_added_to_your_queue` ("R.string.episode_added_to_your_queue").
 *
 * Our root composable is a [Scaffold] whose [Modifier] argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.fillMaxSize]. Its `topBar` argument is a lambda which if our [Boolean]
 * parameter [showBackButton] is `true` will compose a [PodcastDetailsTopAppBar] whose `navigateBack`
 * argument is our lambda parameter [navigateBack], and whose [Modifier] `modifier` argument is a
 * [Modifier.fillMaxWidth]. Its `snackbarHost` argument is a lambda which composes a [SnackbarHost]
 * whose `hostState` argument is our [SnackbarHostState] variable `snackbarHostState`.
 *
 * In its `content` Composable lambda argument we accept the [PaddingValues] passed the lambda in
 * our `contentPadding` variable, then compose a [PodcastDetailsContent] whose arguments are:
 *  - `podcast`: is our [PodcastInfo] parameter [podcast].
 *  - `episodes`: is our [List] of [EpisodeInfo] parameter [episodes].
 *  - `toggleSubscribe`: is our lambda parameter [toggleSubscribe].
 *  - `onQueueEpisode`: is a lambda which accepts the [PlayerEpisode] passed the lambda in variable
 *  `newEpisode` then it calls the [CoroutineScope.launch] method of [CoroutineScope] variable
 *  `coroutineScope` to launch a coroutine which calls the [SnackbarHostState.showSnackbar] method
 *  of [SnackbarHostState] variable `snackbarHostState` with the `message` argument our [String]
 *  variable `snackBarText`. It then calls our lambda parameter [onQueueEpisode] with [PlayerEpisode]
 *  variable `newEpisode` to enqueue the episode.
 *  - `navigateToPlayer`: is our lambda parameter [navigateToPlayer].
 *  - `modifier`: is a [Modifier.padding] whose `paddingValues` argument is our [PaddingValues]
 *  variable `contentPadding`.
 *
 * @param podcast The [PodcastInfo] object containing the details of the podcast.
 * @param episodes A list of [EpisodeInfo] objects representing the episodes of the podcast.
 * @param toggleSubscribe A callback function that is invoked when the user wants to subscribe or
 * unsubscribe from the podcast. It takes the [PodcastInfo] as a parameter.
 * @param onQueueEpisode A callback function that is invoked when the user wants to add an episode
 * to the playback queue. It takes a [PlayerEpisode] as a parameter. It also displays a snackbar to
 * inform the user.
 * @param navigateToPlayer A callback function to be invoked when the user wants to navigate to the
 * player screen for a specific episode. It takes an [EpisodeInfo] as a parameter.
 * @param navigateBack A callback function to be invoked when the user wants to navigate back to
 * the previous screen.
 * @param showBackButton A [Boolean] indicating whether to show the back button in the top app bar.
 * @param modifier The [Modifier] to be applied to the layout. Our caller, the stateful overload of
 * [PodcastDetailsScreen], passes us its own [Modifier] parameter which traces back to the empty,
 * default, or starter [Modifier] that contains no elements.
 */
@Composable
fun PodcastDetailsScreen(
    podcast: PodcastInfo,
    episodes: List<EpisodeInfo>,
    toggleSubscribe: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val snackBarText: String = stringResource(id = R.string.episode_added_to_your_queue)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (showBackButton) {
                PodcastDetailsTopAppBar(
                    navigateBack = navigateBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding: PaddingValues ->
        PodcastDetailsContent(
            podcast = podcast,
            episodes = episodes,
            toggleSubscribe = toggleSubscribe,
            onQueueEpisode = { newEpisode: PlayerEpisode ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message = snackBarText)
                }
                onQueueEpisode(newEpisode)
            },
            navigateToPlayer = navigateToPlayer,
            modifier = Modifier.padding(paddingValues = contentPadding)
        )
    }
}

/**
 * Composable function that displays the content of a podcast details screen.
 *
 * This function uses a [LazyVerticalGrid] to display a list of podcast episodes
 * along with a header containing podcast information.
 *
 * Our root composable is a [LazyVerticalGrid] whose `columns` argument is a [GridCells.Adaptive]
 * whose `minSize` argument is 362.dp, and whose `modifier` argument chains a [Modifier.fillMaxSize]
 * to our [Modifier] parameter [modifier]. In its [LazyGridScope] `content` Composable lambda
 * argument we first compose a [fullWidthItem] in whose [LazyGridItemScope] `content` Composable
 * lambda argument we compose a [PodcastDetailsHeaderItem] whose arguments are:
 *  - `podcast`: is our [PodcastInfo] parameter [podcast].
 *  - `toggleSubscribe`: is our lambda parameter [toggleSubscribe].
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *
 * Then we compose a [LazyGridScope.items], whose `items` argument is our [List] of [EpisodeInfo]
 * parameter [episodes] and its `key` argument is a lambda which returns the [EpisodeInfo.uri] of
 * each [EpisodeInfo] in [episodes]. In its [LazyGridItemScope] `itemContent` Composable lambda
 * argument we accept the [EpisodeInfo] passed the lambda in variable `episode` then we compose a
 * [EpisodeListItem] whose arguments are:
 *  - `episode`: is the [EpisodeInfo] passed the lambda in variable `episode`.
 *  - `podcast`: is our [PodcastInfo] parameter [podcast].
 *  - `onClick`: is our lambda parameter [navigateToPlayer].
 *  - `onQueueEpisode`: is our lambda parameter [onQueueEpisode].
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *  - `showPodcastImage`: is `false`.
 *  - `showSummary`: is `true`.
 *
 * @param podcast The [PodcastInfo] object containing details about the podcast.
 * @param episodes A list of [EpisodeInfo] objects representing the podcast's episodes.
 * @param toggleSubscribe A lambda function to be called when the user wants to subscribe/unsubscribe
 * to the podcast. It takes the [PodcastInfo] as a parameter.
 * @param onQueueEpisode A lambda function to be called when the user wants to queue an episode.
 * It takes a [PlayerEpisode] as a parameter.
 * @param navigateToPlayer A lambda function to be called when the user wants to navigate to the
 * episode player screen. It takes the selected [EpisodeInfo] as a parameter.
 * @param modifier Modifier to be applied to the root of the composable. Our caller
 * [PodcastDetailsScreen] passes us a [Modifier.padding] whose `paddingValues` argument is the
 * [PaddingValues] that the [Scaffold] we are in passes to its `content` Composable lambda argument.
 */
@Composable
fun PodcastDetailsContent(
    podcast: PodcastInfo,
    episodes: List<EpisodeInfo>,
    toggleSubscribe: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 362.dp),
        modifier = modifier.fillMaxSize()
    ) {
        fullWidthItem {
            PodcastDetailsHeaderItem(
                podcast = podcast,
                toggleSubscribe = toggleSubscribe,
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(items = episodes, key = { it.uri }) { episode: EpisodeInfo ->
            EpisodeListItem(
                episode = episode,
                podcast = podcast,
                onClick = navigateToPlayer,
                onQueueEpisode = onQueueEpisode,
                modifier = Modifier.fillMaxWidth(),
                showPodcastImage = false,
                showSummary = true
            )
        }
    }
}

/**
 * Displays the header section of the podcast details screen.
 *
 * This composable displays the podcast's image, title, and subscription status,
 * along with a button to toggle the subscription. It also displays the podcast's
 * description.
 *
 * Our root composable is a [BoxWithConstraints] whose `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.padding] that adds [Keyline1] padding to `all` sides. In the
 * [BoxWithConstraintsScope] `content` Composable lambda argument we first initialize our [Dp]
 * variable `val maxImageSize` to one half of the [BoxWithConstraintsScope.maxWidth] of our
 * [BoxWithConstraints] and initialize our [Dp] variable `val imageSize` to the minimum of
 * `maxImageSize` and 148.dp. Then we compose a [Column] in whose [ColumnScope] `content` Composable
 * lambda argument we first compose a [Row] whose `verticalAlignment` argument is [Alignment.Bottom]
 * and whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth]. In its [RowScope] `content`
 * Composable lambda argument we first compose a [PodcastImage] whose arguments are:
 *  - `modifier`: is a [Modifier.size] whose `size` argument is our [Dp] variable `imageSize`, to
 *  which is chained a [Modifier.clip] whose `shape` argument is the [Shapes.large] of our custom
 *  [MaterialTheme.shapes].
 *  - `podcastImageUrl`: is the [PodcastInfo.imageUrl] of our [PodcastInfo] parameter [podcast].
 *  - `contentDescription`: is the [PodcastInfo.title] of our [PodcastInfo] parameter [podcast].
 *
 * Next in the [RowScope] `content` Composable lambda argument we compose a [Column] whose `modifier`
 * argument is a [Modifier.padding] that adds 16.dp padding to the `start` side. In its [ColumnScope]
 * `content` Composable lambda argument we first compose a [Text] whose arguments are:
 *  - `text`: is the [PodcastInfo.title] of our [PodcastInfo] parameter [podcast].
 *  - `maxLines`: is `2`.
 *  - `overflow`: is [TextOverflow.Ellipsis].
 *  - `style`: is the [TextStyle] of [Typography.headlineMedium] of our custom 
 *  [MaterialTheme.typography].
 *
 * Below that in the [ColumnScope] `content` Composable lambda argument we compose a
 * [PodcastDetailsHeaderItemButtons] whose arguments are:
 *  - `isSubscribed`: is `true` if the [PodcastInfo.isSubscribed] of our [PodcastInfo] parameter
 *  [podcast] is `true`, and `false` otherwise.
 *  - `onClick`: is our lambda parameter [toggleSubscribe] called with our [PodcastInfo] parameter
 *  [podcast].
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *
 * Next in the [RowScope] `content` Composable lambda argument of the [Row] we compose a
 * [PodcastDetailsDescription] whose arguments are:
 *  - `podcast`: is our [PodcastInfo] parameter [podcast].
 *  - `modifier`: is a [Modifier.fillMaxWidth] and a [Modifier.padding] that adds 16.dp padding to
 *  each vertical side.
 *
 * @param podcast The [PodcastInfo] object containing the podcast's details.
 * @param toggleSubscribe A lambda function that takes a [PodcastInfo] and is called when the
 * subscription button is clicked. This function should toggle the subscription status of the
 * podcast.
 * @param modifier [Modifier] to be applied to the layout. Our caller [PodcastDetailsContent] passes
 * us a [Modifier.fillMaxWidth].
 */
@Composable
fun PodcastDetailsHeaderItem(
    podcast: PodcastInfo,
    toggleSubscribe: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(all = Keyline1)
    ) {
        val maxImageSize: Dp = this.maxWidth / 2
        val imageSize: Dp = min(maxImageSize, 148.dp)
        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                PodcastImage(
                    modifier = Modifier
                        .size(size = imageSize)
                        .clip(shape = MaterialTheme.shapes.large),
                    podcastImageUrl = podcast.imageUrl,
                    contentDescription = podcast.title
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = podcast.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    PodcastDetailsHeaderItemButtons(
                        isSubscribed = podcast.isSubscribed == true,
                        onClick = {
                            toggleSubscribe(podcast)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            PodcastDetailsDescription(
                podcast = podcast,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}

/**
 * Displays the description of a podcast, allowing the user to expand and collapse
 * the text if it exceeds a certain number of lines.
 *
 * We start by initializing and remembering our [MutableState] wrapped [Boolean] variable
 * `var isExpanded` to an initial value of `false`, and initializing and remembering our
 * [MutableState] wrapped [Boolean] variable `var showSeeMore` to an initial value of `false`.
 *
 * Our root composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.clickable] whose `onClick` lambda argument is a lambda which toggles the
 * value of our [MutableState] wrapped [Boolean] variable `isExpanded`. In the [BoxScope] `content`
 * Composable lambda argument we compose a [Text] whose arguments are:
 *  - `text`: is the [PodcastInfo.description] of our [PodcastInfo] parameter [podcast].
 *  - `style`: is the [TextStyle] of [Typography.bodyMedium] of our custom [MaterialTheme.typography].
 *  - `maxLines`: `if` the value of our [MutableState] wrapped [Boolean] variable `isExpanded` is
 *  `true` then [Int.MAX_VALUE] otherwise `3`.
 *  - `overflow`: is [TextOverflow.Ellipsis].
 *  - `onTextLayout`: is a lambda which accepts a [TextLayoutResult] passed the lambda in variable
 *  `result` then it sets the value of our [MutableState] wrapped [Boolean] variable `showSeeMore`
 *  to the value of the [TextLayoutResult.hasVisualOverflow] property of `result`.
 *  - `modifier`: is a [Modifier.animateContentSize] whose `animationSpec` argument is a [tween]
 *  whose `durationMillis` argument is `200` and whose `easing` argument is [EaseOutExpo].
 *
 * If the value of our [MutableState] wrapped [Boolean] variable `showSeeMore` is `true` then we
 * also compose a [Box] whose `modifier` argument is a [BoxScope.align] whose `alignment` argument
 * is [Alignment.BottomEnd] to which is chained a [Modifier.background] whose `color` argument is
 * the [Color] of [ColorScheme.surface] of our custom [MaterialTheme.colorScheme]. In the [BoxScope]
 * `content` Composable lambda argument we compose a [Text] whose arguments are:
 *  - `text`: is the string resource with resource ID `R.string.see_more` ("see more")
 *  - `style`: is the [TextStyle] of a copy of [Typography.bodyMedium] of our custom
 *  [MaterialTheme.typography] with its [TextStyle.textDecoration] property set to
 *  [TextDecoration.Underline], and its [TextStyle.fontWeight] property set to [FontWeight.Bold].
 *  - `modifier`: is a [Modifier.padding] whose `start` argument is 16.dp.
 *
 * @param podcast The [PodcastInfo] object containing the podcast's details, including the description.
 * @param modifier The [Modifier] to be applied to the container of the description. Our caller
 * [PodcastDetailsHeaderItem] passes us a [Modifier.fillMaxWidth] and a [Modifier.padding] that adds
 * 16.dp padding to each vertical side.
 */
@Composable
fun PodcastDetailsDescription(
    podcast: PodcastInfo,
    modifier: Modifier
) {
    var isExpanded: Boolean by remember { mutableStateOf(false) }
    var showSeeMore: Boolean by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.clickable { isExpanded = !isExpanded }
    ) {
        Text(
            text = podcast.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result: TextLayoutResult ->
                showSeeMore = result.hasVisualOverflow
            },
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = EaseOutExpo
                )
            )
        )
        if (showSeeMore) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                // TODO: Add gradient effect
                Text(
                    text = stringResource(id = R.string.see_more),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

/**
 * Composable function that displays the header item buttons for a podcast details screen.
 *
 * This composable displays two buttons:
 * - A "Subscribe" or "Subscribed" button, depending on the [isSubscribed] state.
 * - A "More Options" button, represented by a vertical ellipsis icon.
 *
 * Our root composable is a [Row] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.padding] whose `top` argument is 16.dp. In the [RowScope] `content`
 * Composable lambda argument we first compose a [Button] whose arguments are:
 *  - `onClick`: is our lambda parameter [onClick].
 *  - `colors`: is a [ButtonDefaults.buttonColors] whose `containerColor` argument is the
 *  [Color] of [ColorScheme.tertiary] if the [isSubscribed] parameter is `true`, and the
 *  [Color] of [ColorScheme.secondary] if it is `false`.
 *  - `modifier`: is a [Modifier.semantics] whose `mergeDescendants` argument is `true`.
 *
 * In the [RowScope] `content` Composable lambda argument of the [Button] we compose an [Icon] whose
 * arguments are:
 *  - `imageVector`: is an [Icons.Filled.Check] if the [isSubscribed] parameter is `true`, and
 *  [Icons.Filled.Add] otherwise.
 *  - `contentDescription`: is `null`.
 *
 * Next in the [RowScope] `content` Composable lambda argument of the [Button] we compose a [Text]
 * whose arguments are:
 *  - `text`: is the string resource with resource ID `R.string.subscribed` ("Subscribed") if our
 *  [Boolean] parameter [isSubscribed] is `true`, and the string resource with resource ID
 *  `R.string.subscribe` ("Subscribe") otherwise.
 *  - `modifier`: is a [Modifier.padding] whose `start` argument is 8.dp.
 *
 * Next in the [RowScope] `content` Composable lambda argument of the [Row] root composabe we
 * compose a [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` argument is
 * `1f`. And then we compose a [IconButton] whose arguments are:
 *  - `onClick`: is a do nothing lambda.
 *  - `modifier`: is a [Modifier.padding] whose `start` argument is 8.dp.
 *
 * In the [IconButton] `content` Composable lambda argument we compose an [Icon] whose arguments
 * are:
 *  - `imageVector`: is [Icons.Filled.MoreVert].
 *  - `contentDescription`: is the string resource with resource ID `R.string.cd_more` ("More").
 *
 * @param isSubscribed [Boolean] indicating whether the user is subscribed to the podcast.
 * @param onClick Lambda function to be invoked when the "Subscribe" or "Subscribed" button is clicked.
 * @param modifier [Modifier] to be applied to the Row containing the buttons. Our caller
 * [PodcastDetailsHeaderItem] passes us a [Modifier.fillMaxWidth].
 */
@Composable
fun PodcastDetailsHeaderItemButtons(
    isSubscribed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(top = 16.dp)) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSubscribed)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = if (isSubscribed)
                    Icons.Default.Check
                else
                    Icons.Default.Add,
                contentDescription = null
            )
            Text(
                text = if (isSubscribed)
                    stringResource(id = R.string.subscribed)
                else
                    stringResource(id = R.string.subscribe),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(weight = 1f))

        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(id = R.string.cd_more)
            )
        }
    }
}

/**
 * A custom TopAppBar for the Podcast Details screen.
 *
 * This composable provides a [TopAppBar] with a back navigation button.
 * It is designed to be used at the top of the Podcast Details screen to allow
 * the user to navigate back to the previous screen.
 *
 * Our root composable is a [TopAppBar] whose `title` argument is an empty lambda, whose
 * `navigationIcon` argument is a [IconButton] whose `onClick` lambda argument is our [navigateBack]
 * lambda parameter. In the [IconButton] `content` Composable lambda argument we compose an [Icon]
 * whose arguments are:
 *  - `imageVector`: is [Icons.AutoMirrored.Filled.ArrowBack].
 *  - `contentDescription`: is the string resource with resource ID `R.string.cd_back` ("Back").
 *
 * The [Modifier] `modifier` argument of the [TopAppBar] is our [Modifier] parameter [modifier].
 *
 *
 * @param navigateBack A callback function that is invoked when the back button is clicked.
 * This should handle the navigation back to the previous screen.
 * @param modifier [Modifier] to be applied to the [TopAppBar]. Our caller [PodcastDetailsScreen]
 * passes us a [Modifier.fillMaxWidth].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastDetailsTopAppBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.cd_back)
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Preview of [PodcastDetailsHeaderItem].
 */
@Preview
@Composable
fun PodcastDetailsHeaderItemPreview() {
    PodcastDetailsHeaderItem(
        podcast = PreviewPodcasts[0],
        toggleSubscribe = { },
    )
}

/**
 * * Previews of [PodcastDetailsScreen].
 */
@DevicePreviews
@Composable
fun PodcastDetailsScreenPreview() {
    PodcastDetailsScreen(
        podcast = PreviewPodcasts[0],
        episodes = PreviewEpisodes,
        toggleSubscribe = { },
        onQueueEpisode = { },
        navigateToPlayer = { },
        navigateBack = { },
        showBackButton = true,
    )
}
