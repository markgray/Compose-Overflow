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

package com.example.jetcaster.ui.latest_episodes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.components.MediaContent
import com.example.jetcaster.ui.preview.WearPreviewEpisodes
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

/**
 * Composable function representing the screen that displays the latest episodes.
 *
 * This screen fetches and displays the latest episodes using the [LatestEpisodeViewModel].
 * It handles user interactions like playing all episodes or a single episode, and
 * dismissing the screen.
 *
 * @param onPlayButtonClick Callback function to be executed when the play button is clicked
 * (likely for the entire list).
 * @param onDismiss Callback function to be executed when the user dismisses the screen.
 * @param modifier Modifier for styling and layout adjustments of the composable.
 * @param latestEpisodeViewModel The ViewModel responsible for managing the latest episodes
 * data and logic. Defaults to an instance provided by Hilt.
 */
@Composable fun LatestEpisodesScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    latestEpisodeViewModel: LatestEpisodeViewModel = hiltViewModel()
) {
    val uiState: LatestEpisodeScreenState by latestEpisodeViewModel.uiState.collectAsStateWithLifecycle()
    LatestEpisodeScreen(
        modifier = modifier,
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onDismiss = onDismiss,
        onPlayEpisodes = latestEpisodeViewModel::onPlayEpisodes,
        onPlayEpisode = latestEpisodeViewModel::onPlayEpisode
    )
}

/**
 * Composable function representing the screen displaying the latest episodes.
 *
 * This screen handles three different states:
 *  - `Loaded`: Displays a list of the latest episodes.
 *  - `Empty`: Displays an alert dialog indicating that no episodes are available.
 *  - `Loading`: Displays a loading indicator while fetching the latest episodes.
 *
 * @param uiState The current state of the latest episode screen, which can be one of:
 *  - [LatestEpisodeScreenState.Loaded]: Contains a list of [PlayerEpisode] to display.
 *  - [LatestEpisodeScreenState.Empty]: Indicates that there are no episodes to display.
 *  - [LatestEpisodeScreenState.Loading]: Indicates that the episodes are being loaded.
 * @param onPlayButtonClick Callback triggered when the global "play" button is clicked.
 * @param onDismiss Callback triggered when the alert dialog (in the `Empty` state) is dismissed.
 * @param onPlayEpisodes Callback triggered when a request to play a list of episodes is made.
 * It receive a `List<PlayerEpisode>` as param
 * @param onPlayEpisode Callback triggered when a request to play a single episode is made.
 * It receives a [PlayerEpisode] as param
 * @param modifier [Modifier] for styling and layout adjustments of the composable.
 */
@Composable
fun LatestEpisodeScreen(
    uiState: LatestEpisodeScreenState,
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnState: ScalingLazyColumnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier
    ) {
        when (uiState) {
            is LatestEpisodeScreenState.Loaded -> {
                LatestEpisodesScreen(
                    episodeList = uiState.episodeList,
                    onPlayButtonClick = onPlayButtonClick,
                    onPlayEpisode = onPlayEpisode,
                    onPlayEpisodes = onPlayEpisodes,
                    modifier = modifier
                )
            }

            is LatestEpisodeScreenState.Empty -> {
                AlertDialog(
                    showDialog = true,
                    onDismiss = onDismiss,
                    message = stringResource(id = R.string.podcasts_no_episode_podcasts)
                )
            }

            is LatestEpisodeScreenState.Loading -> {
                LatestEpisodesScreenLoading(
                    modifier = modifier
                )
            }
        }
    }
}

/**
 * A composable function that displays a "Play" button as a Chip.
 *
 * This button triggers two actions when clicked:
 *  1. `onPlayButtonClick`: A callback to handle the primary "Play" action.
 *  2. `onPlayEpisodes`: A callback to provide the list of episodes to be played.
 *
 * @param episodes The list of [PlayerEpisode]s to be played.
 * @param onPlayButtonClick A lambda function to be executed when the "Play" button is clicked.
 * This is intended for general play button actions, such as starting playback.
 * @param onPlayEpisodes A lambda function that receives the list of [PlayerEpisode]s.
 * This is used to specify which episodes should be played.
 * @param modifier Modifier for styling and layout adjustments of the Chip.
 */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    modifier: Modifier = Modifier
) {
    Chip(
        label = stringResource(id = R.string.button_play_content_description),
        onClick = {
            onPlayButtonClick()
            onPlayEpisodes(episodes)
        },
        modifier = modifier.padding(bottom = 16.dp),
        icon = Icons.Outlined.PlayArrow.asPaintable(),
    )
}

/**
 * Displays a screen showing a list of the latest episodes.
 *
 * This composable displays a [List] of [PlayerEpisode] objects, allowing the user to
 * interact with them. It includes a header indicating that these are the "Latest Episodes",
 * a list of episodes with their media content, and buttons to control playback.
 *
 * @param episodeList The [List] of [PlayerEpisode] objects to display.
 * @param onPlayButtonClick Callback triggered when a "play" button is clicked, typically for
 * general play actions.
 * @param onPlayEpisode Callback triggered when a specific episode is selected to be played.
 * It provides the [PlayerEpisode] that was clicked.
 * @param onPlayEpisodes Callback triggered when a "play all" or similar action is performed
 * on the entire list of episodes. It is provided the entire [List] of [PlayerEpisode] objects.
 * @param modifier [Modifier] for styling and layout of the screen.
 */
@Composable
fun LatestEpisodesScreen(
    episodeList: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.latest_episodes))
            }
        },
        content = {
            items(count = episodeList.size) { index: Int ->
                MediaContent(
                    episode = episodeList[index],
                    episodeArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue,
                    ),
                    onItemClick = {
                        onPlayButtonClick()
                        onPlayEpisode(episodeList[index])
                    }
                )
            }
        },
        buttonsContent = {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes
            )
        },
    )
}

/**
 * Displays a loading screen for the "Latest Episodes" section.
 *
 * This composable provides a visual representation of the screen while the actual
 * latest episodes are being fetched or loaded. It uses placeholder chips and
 * a header to indicate the loading state.
 *
 * @param modifier Modifier to apply to the outer container of the loading screen. Defaults to
 * [Modifier], the empty, default, or starter Modifier that contains no elements.
 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LatestEpisodesScreenLoading(
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.latest_episodes))
            }
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        },
        buttonsContent = {
            ButtonsContent(
                episodes = emptyList(),
                onPlayButtonClick = { },
                onPlayEpisodes = { },
            )
        },
    )
}

/**
 * A preview of the Latest Episodes Screen.
 */
@Suppress("UnusedVariable")
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LatestEpisodeScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    @OptIn(ExperimentalHorologistApi::class)
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    LatestEpisodesScreen(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisode = { },
        onPlayEpisodes = { }
    )
}

/**
 * A preview of the Latest Episodes Screen Loading.
 */
@Suppress("UnusedVariable")
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LatestEpisodeScreenLoadingPreview() {
    @OptIn(ExperimentalHorologistApi::class)
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    LatestEpisodesScreenLoading()
}
