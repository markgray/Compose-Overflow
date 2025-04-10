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

package com.example.jetcaster.ui.library

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.jetcaster.R
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

/**
 * Composable function for displaying the Library Screen.
 *
 * This screen is responsible for showing the user's library, which includes
 * their subscribed podcasts, up next queue, and other related content.
 *
 * It handles different states of the library data, including:
 *  - Loading: When the library data is being fetched.
 *  - NoSubscribedPodcast: When the user hasn't subscribed to any podcasts yet.
 *  - Ready: When the library data is available and ready to be displayed.
 *
 * @param onLatestEpisodeClick Callback triggered when the "Latest Episode" item is clicked.
 * @param onYourPodcastClick Callback triggered when the "Your Podcast" item is clicked.
 * @param onUpNextClick Callback triggered when the "Up Next" item is clicked.
 * @param modifier [Modifier] for styling and layout customization.
 * @param libraryScreenViewModel ViewModel responsible for managing the library screen's
 * state and logic. Defaults to an instance provided by Hilt.
 */
@Composable
fun LibraryScreen(
    onLatestEpisodeClick: () -> Unit,
    onYourPodcastClick: () -> Unit,
    onUpNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    libraryScreenViewModel: LibraryViewModel = hiltViewModel()
) {
    /**
     * The current UI state of the library screen.
     */
    val uiState: LibraryScreenUiState by libraryScreenViewModel.uiState.collectAsState()

    /**
     * The state for the library screen's various columns.
     */
    val columnState: ScalingLazyColumnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )

    when (val s: LibraryScreenUiState = uiState) {
        is LibraryScreenUiState.Loading ->
            LoadingScreen(
                modifier = modifier
            )
        is LibraryScreenUiState.NoSubscribedPodcast ->
            NoSubscribedPodcastScreen(
                columnState = columnState,
                modifier = modifier,
                topPodcasts = s.topPodcasts,
                onTogglePodcastFollowed = libraryScreenViewModel::onTogglePodcastFollowed
            )

        is LibraryScreenUiState.Ready ->
            LibraryScreen(
                columnState = columnState,
                modifier = modifier,
                onLatestEpisodeClick = onLatestEpisodeClick,
                onYourPodcastClick = onYourPodcastClick,
                onUpNextClick = onUpNextClick,
                queue = s.queue
            )
    }
}

/**
 * Displays a loading screen with a header and placeholder chips.
 *
 * This composable function creates a loading screen commonly used to indicate that data
 * is being fetched or processed. It features a header displaying the "Loading" text
 * and placeholder chips to visually represent content that is not yet available.
 *
 * @param modifier Modifier to be applied to the root layout of the loading screen.
 * This allows customization of the appearance and layout.
 * @see EntityScreen
 * @see ResponsiveListHeader
 * @see PlaceholderChip
 * @see ChipDefaults
 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LoadingScreen(
    modifier: Modifier,
) {
    EntityScreen(
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.loading))
            }
        },
        modifier = modifier,
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

/**
 * Displays a screen indicating that the user has not subscribed to any podcasts yet.
 * It also shows a list of top podcasts that the user can follow.
 *
 * @param columnState The state of the scaling lazy column for controlling scrolling.
 * @param modifier Modifier for styling the screen.
 * @param topPodcasts A list of [PodcastInfo] representing the top podcasts to display.
 * @param onTogglePodcastFollowed Callback function invoked when the user wants to
 * follow/unfollow a podcast. It receives the podcast's URI as a parameter.
 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun NoSubscribedPodcastScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier,
    topPodcasts: List<PodcastInfo>,
    onTogglePodcastFollowed: (uri: String) -> Unit
) {
    ScreenScaffold(scrollState = columnState, modifier = modifier) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(
                    modifier = modifier.listTextPadding(),
                    contentColor = MaterialTheme.colors.onSurface
                ) {
                    Text(text = stringResource(id = R.string.entity_no_featured_podcasts))
                }
            }
            if (topPodcasts.isNotEmpty()) {
                items(items = topPodcasts.take(3)) { podcast: PodcastInfo ->
                    PodcastContent(
                        podcast = podcast,
                        downloadItemArtworkPlaceholder = rememberVectorPainter(
                            image = Icons.Default.MusicNote,
                            tintColor = Color.Blue,
                        ),
                        onClick = {
                            onTogglePodcastFollowed(podcast.uri)
                        },
                    )
                }
            } else {
                item {
                    PlaceholderChip(
                        contentDescription = "",
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }
    }
}

/**
 * Displays a chip representing a podcast.
 *
 * This composable displays a clickable chip with the podcast's title and artwork.
 *
 * @param podcast The [PodcastInfo] object containing the podcast's details.
 * @param downloadItemArtworkPlaceholder An optional [Painter] to be used as a placeholder while
 * the podcast's artwork is loading. If null, a default placeholder might be used by the Coil library.
 * @param onClick The callback to be invoked when the chip is clicked.
 * @param modifier Optional [Modifier] to be applied to the chip.
 */
@Composable
private fun PodcastContent(
    podcast: PodcastInfo,
    downloadItemArtworkPlaceholder: Painter?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mediaTitle: String = podcast.title

    Chip(
        label = mediaTitle,
        onClick = onClick,
        modifier = modifier,
        icon = CoilPaintable(model = podcast.imageUrl, placeholder = downloadItemArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}

/**
 * Composable function that displays the Library screen, showing options for Latest Episodes,
 * Your Podcasts, and Up Next.
 *
 * @param columnState The state of the scrolling column. Used for managing the scrolling position.
 * @param modifier Modifier for styling the layout.
 * @param onLatestEpisodeClick Callback to be invoked when the "Latest Episodes" chip is clicked.
 * @param onYourPodcastClick Callback to be invoked when the "Your Podcasts" chip is clicked.
 * @param onUpNextClick Callback to be invoked when the "Up Next" chip is clicked.
 * @param queue The list of episodes in the queue. Determines whether to show the "Up Next" chip
 * or the "Queue Empty" message.
 */
@Composable
fun LibraryScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier,
    onLatestEpisodeClick: () -> Unit,
    onYourPodcastClick: () -> Unit,
    onUpNextClick: () -> Unit,
    queue: List<PlayerEpisode>
) {
    ScreenScaffold(scrollState = columnState, modifier = modifier) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(modifier = Modifier.listTextPadding()) {
                    Text(text = stringResource(id = R.string.home_library))
                }
            }
            item {
                Chip(
                    label = stringResource(id = R.string.latest_episodes),
                    onClick = onLatestEpisodeClick,
                    icon = DrawableResPaintable(id = R.drawable.new_releases),
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.podcasts),
                    onClick = onYourPodcastClick,
                    icon = DrawableResPaintable(id = R.drawable.podcast),
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
            item {
                ResponsiveListHeader(modifier = Modifier.listTextPadding()) {
                    Text(text = stringResource(id = R.string.queue))
                }
            }
            item {
                if (queue.isEmpty()) {
                    QueueEmpty()
                } else {
                    Chip(
                        label = stringResource(id = R.string.up_next),
                        onClick = onUpNextClick,
                        icon = DrawableResPaintable(id = R.drawable.up_next),
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }
    }
}

/**
 * Displays a message indicating that the queue is empty.
 *
 * This composable is used to inform the user that there are currently no
 * episodes in the queue. It displays a centered text message prompting
 * the user to add episodes.
 */
@Composable
private fun QueueEmpty() {
    Text(
        text = stringResource(id = R.string.add_episode_to_queue),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body2,
    )
}
