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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
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
 * Then we use a `when` to branch based on the types of `state` copy `s`:
 *  - [PodcastUiState.Loading] -> We compose a [PodcastDetailsLoadingScreen] whose [Modifier]
 *  `modifier` argument is [Modifier.fillMaxSize].
 *  - [PodcastUiState.Ready] -> We compose a [PodcastDetailsScreen] whose arguments are:
 *      - `podcast` is the [PodcastUiState.podcast] field of [s].
 *      - `episodes` is the [PodcastUiState.episodes] field of [s].
 *      - `toggleSubscribe` is the [PodcastDetailsViewModel.toggleSusbcribe] function of our
 *      [PodcastDetailsViewModel] field [viewModel].
 *      - `onQueueEpisode` is the [PodcastDetailsViewModel.onQueueEpisode] function of our
 *      [PodcastDetailsViewModel] field [viewModel].
 *      - `navigateToPlayer` is our lambda function that takes an [EpisodeInfo] parameter
 *      [navigateToPlayer]
 *      - `navigateBack` is our lambda function that takes no parameters [navigateBack]
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
    ) { contentPadding ->
        PodcastDetailsContent(
            podcast = podcast,
            episodes = episodes,
            toggleSubscribe = toggleSubscribe,
            onQueueEpisode = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(snackBarText)
                }
                onQueueEpisode(it)
            },
            navigateToPlayer = navigateToPlayer,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

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
        items(episodes, key = { it.uri }) { episode: EpisodeInfo ->
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
