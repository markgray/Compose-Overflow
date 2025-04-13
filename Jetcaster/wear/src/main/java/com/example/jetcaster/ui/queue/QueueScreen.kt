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

package com.example.jetcaster.ui.queue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
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
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.media.ui.screens.entity.DefaultEntityScreenHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen
import kotlinx.coroutines.flow.StateFlow

/**
 * Stateful composable override that displays the Queue screen, managing the list of episodes
 * queued for playback. It calls the Stateless [QueueScreen] composable override to render the UI.
 *
 * This composable provides a UI to interact with the queue, allowing users to:
 *  - View the current queue of episodes.
 *  - Initiate playback of the entire queue.
 *  - Navigate to a specific episode's details.
 *  - Delete episodes from the queue
 *  - Dismiss the queue screen
 *
 * @param onPlayButtonClick Callback triggered when the user clicks the "Play" button for the
 * entire queue.
 * @param onEpisodeItemClick Callback triggered when the user clicks on a specific episode item
 * in the queue. It is provided the clicked [PlayerEpisode] as a parameter.
 * @param onDismiss Callback triggered when the user dismisses the queue screen (e.g., by clicking
 * a close button).
 * @param modifier [Modifier] for styling and layout customization of the [QueueScreen].
 * @param queueViewModel The [QueueViewModel] instance responsible for managing the queue's data
 * and logic. Defaults to a new instance provided by Hilt.
 */
@Composable fun QueueScreen(
    onPlayButtonClick: () -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    /**
     * [State] wrapped [QueueScreenState] representing the current state of the queue collected
     * from the [StateFlow] of [QueueViewModel] property [QueueViewModel.uiState] of our
     * [QueueViewModel] property [queueViewModel].
     */
    val uiState: QueueScreenState by queueViewModel.uiState.collectAsStateWithLifecycle()

    QueueScreen(
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onPlayEpisodes = queueViewModel::onPlayEpisodes,
        modifier = modifier,
        onEpisodeItemClick = onEpisodeItemClick,
        onDeleteQueueEpisodes = queueViewModel::onDeleteQueueEpisodes,
        onDismiss = onDismiss
    )
}

/**
 * Stateless composable override that displays the Queue screen.
 *
 * @param uiState The current state of the queue, represented as a [QueueScreenState].
 * @param onPlayButtonClick Callback to triggered when the user clicks the "Play" button for the
 * entire queue.
 * @param onPlayEpisodes Callback to triggered when the user clicks the "Play" button for the
 * entire queue. It is provided the list of [PlayerEpisode] as a parameter.
 * @param modifier [Modifier] for styling and layout customization of the [QueueScreen].
 * @param onEpisodeItemClick Callback triggered when the user clicks on a specific episode item
 * in the queue. It is provided the clicked [PlayerEpisode] as a parameter.
 * @param onDeleteQueueEpisodes Callback to be triggered when the user deletes all episodes from
 * the queue.
 * @param onDismiss Callback triggered when the user dismisses the queue screen (e.g., by clicking
 * a close button).
 */
@Composable
fun QueueScreen(
    uiState: QueueScreenState,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    modifier: Modifier = Modifier,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
    onDismiss: () -> Unit
) {
    /**
     * [ScalingLazyColumnState] used  by [ScreenScaffold] as the ScrollableState to show in a
     * default PositionIndicator.
     */
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
            is QueueScreenState.Loaded -> QueueScreenLoaded(
                episodeList = uiState.episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                onDeleteQueueEpisodes = onDeleteQueueEpisodes,
                onEpisodeItemClick = onEpisodeItemClick
            )
            QueueScreenState.Loading -> QueueScreenLoading()
            QueueScreenState.Empty -> QueueScreenEmpty(onDismiss)
        }
    }
}

/**
 * Composable function that displays the loaded state of the queue screen.
 *
 * This function renders a list of episodes currently in the playback queue, along with
 * controls to manage the queue. It uses the [EntityScreen] composable as a layout container.
 *
 * @param episodeList The list of [PlayerEpisode] objects to display in the queue.
 * @param onPlayButtonClick Callback function to be executed when the "Play" button is clicked.
 * @param onPlayEpisodes Callback function to be executed when the "Play All" button is clicked,
 * it is provided the list of [PlayerEpisode] to be played as a parameter.
 * @param onDeleteQueueEpisodes Callback function to be executed when the "Delete All" button
 * is clicked.
 * @param onEpisodeItemClick Callback function to be executed when an individual episode item
 * in the list is clicked. It it provided the clicked [PlayerEpisode] as a parameter.
 * @param modifier [Modifier] to be applied to the root layout of the queue screen.
 */
@Composable
fun QueueScreenLoaded(
    episodeList: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.queue))
            }
        },
        buttonsContent = {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                onDeleteQueueEpisodes = onDeleteQueueEpisodes
            )
        },
        content = {
            items(items = episodeList) { episode: PlayerEpisode ->
                MediaContent(
                    episode = episode,
                    episodeArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue,
                    ),
                    onItemClick = onEpisodeItemClick
                )
            }
        }
    )
}

/**
 * Displays a loading screen for the queue, indicating that data is being fetched or processed.
 *
 * This composable provides a visual representation of the queue screen in a loading state.
 * It uses placeholder chips to simulate the appearance of queue items while the actual content
 * is being loaded. The header and buttons are also displayed in their default state but disabled.
 *
 * @param modifier [Modifier] for styling and layout adjustments of the root composable.
 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun QueueScreenLoading(
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            DefaultEntityScreenHeader(
                title = stringResource(id = R.string.queue)
            )
        },
        buttonsContent = {
            ButtonsContent(
                episodes = emptyList(),
                onPlayButtonClick = {},
                onPlayEpisodes = {},
                onDeleteQueueEpisodes = { },
                enabled = false
            )
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

/**
 * Displays an empty state alert dialog for the Queue screen.
 *
 * This composable function shows an alert dialog to inform the user that
 * the queue is empty and there are no episodes to display.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed, either by clicking outside
 * the dialog or pressing the back button. Used to close the dialog or navigate back.
 * @param modifier [Modifier] to be applied to the dialog.
 */
@Composable
fun QueueScreenEmpty(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        showDialog = true,
        onDismiss = onDismiss,
        title = stringResource(id = R.string.display_nothing_in_queue),
        message = stringResource(id = R.string.no_episodes_from_queue),
        modifier = modifier
    )
}

/**
 * Displays a row of buttons for controlling the playback queue.
 *
 * This composable provides two buttons: a "Play" button and a "Delete Queue" button.
 * The "Play" button triggers playback of the provided list of episodes,
 * and the "Delete Queue" button clears the current playback queue.
 *
 * @param episodes The list of [PlayerEpisode] to be played when the "Play" button is clicked.
 * @param onPlayButtonClick A callback function invoked when the "Play" button is clicked. This
 * callback is used to update UI state or perform other actions related to play button interaction.
 * @param onPlayEpisodes A callback function invoked when the "Play" button is clicked.
 * This callback is called with the list of episodes to be played.
 * @param onDeleteQueueEpisodes A callback function invoked when the "Delete Queue" button is
 * clicked. This callback is used to clear the playback queue.
 * @param modifier [Modifier] to apply to the row of buttons.
 * @param enabled [Boolean] indicating whether the buttons should be enabled or disabled.
 * Defaults to `true`.
 */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    Row(
        modifier = modifier
            .padding(bottom = 16.dp)
            .height(height = 52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 6.dp,
            alignment = Alignment.CenterHorizontally
        ),
    ) {
        Button(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = stringResource(id = R.string.button_play_content_description),
            onClick = {
                onPlayButtonClick()
                onPlayEpisodes(episodes)
            },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
            enabled = enabled
        )
        Button(
            imageVector = Icons.Outlined.Delete,
            contentDescription =
            stringResource(id = R.string.button_delete_queue_content_description),
            onClick = onDeleteQueueEpisodes,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
            enabled = enabled
        )
    }
}

/**
 * Preview of the [QueueScreenLoaded] composable.
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenLoadedPreview(
    @PreviewParameter(provider = WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    @Suppress("UnusedVariable", "unused")
    val columnState: ScalingLazyColumnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    QueueScreenLoaded(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisodes = { },
        onDeleteQueueEpisodes = { },
        onEpisodeItemClick = { }
    )
}

/**
 * Preview of the [QueueScreenLoading] composable.
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenLoadingPreview() {
    @Suppress("UnusedVariable", "unused")
    val columnState: ScalingLazyColumnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    QueueScreenLoading()
}

/**
 * Preview of the [QueueScreenEmpty] composable.
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenEmptyPreview() {
    QueueScreenEmpty(onDismiss = {})
}
