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
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewPodcastEpisodes
import com.example.jetcaster.core.model.PodcastInfo
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
import kotlinx.coroutines.flow.StateFlow

/**
 * Stateful Composable function that renders the Podcast Details screen.
 *
 * This screen displays detailed information about a selected podcast, including
 * its title, description, and a list of episodes. It also handles interactions
 * like playing an episode or navigating back.
 *
 * @param onPlayButtonClick Callback triggered when the main play button is clicked.
 * This is typically used to start playing the entire podcast.
 * @param onEpisodeItemClick Callback triggered when an episode item in the list is clicked.
 * It is provided the [PlayerEpisode] representing the selected episode.
 * @param onDismiss Callback triggered when the screen should be dismissed, typically when
 * the user navigates back or closes the screen.
 * @param modifier [Modifier] for styling and layout customization of the screen.
 * @param podcastDetailsViewModel `ViewModel` responsible for managing the state and logic
 * of the Podcast Details screen. By default, it uses a Hilt-injected [PodcastDetailsViewModel].
 */
@Composable
fun PodcastDetailsScreen(
    onPlayButtonClick: () -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    podcastDetailsViewModel: PodcastDetailsViewModel = hiltViewModel()
) {
    /**
     * [State] wrapped [PodcastDetailsScreenState] collected as state from the [StateFlow]
     * of [PodcastDetailsScreenState] property [PodcastDetailsViewModel.uiState] of our
     * [PodcastDetailsViewModel] property [podcastDetailsViewModel].
     */
    @OptIn(ExperimentalHorologistApi::class)
    val uiState: PodcastDetailsScreenState by podcastDetailsViewModel.uiState.collectAsStateWithLifecycle()


    @OptIn(ExperimentalHorologistApi::class)
    PodcastDetailsScreen(
        uiState = uiState,
        onEpisodeItemClick = onEpisodeItemClick,
        onPlayEpisode = podcastDetailsViewModel::onPlayEpisodes,
        onDismiss = onDismiss,
        onPlayButtonClick = onPlayButtonClick,
        modifier = modifier,
    )
}

/**
 * Stateless Composable function that renders the Podcast Details screen.
 *
 * This screen displays detailed information about a selected podcast, including
 * its title, description, and a list of episodes. It also handles interactions
 * like playing an episode or navigating back.
 *
 * @param uiState The current state of the Podcast Details screen.
 * This can be either [PodcastDetailsScreenState.Loaded], [PodcastDetailsScreenState.Loading],
 * or [PodcastDetailsScreenState.Empty].
 * @param onPlayButtonClick Callback triggered when the main play button is clicked.
 * This is typically used to start playing the entire podcast.
 * @param modifier [Modifier] for styling and layout customization of the screen.
 * @param onEpisodeItemClick Callback triggered when an episode item in the list is clicked.
 * It is called with the [PlayerEpisode] representing the selected episode.
 * @param onPlayEpisode Callback triggered when the main play button is clicked.
 * This is typically used to start playing the entire podcast.
 * @param onDismiss Callback triggered when the screen should be dismissed, typically when
 * the user navigates back or closes the screen.
 */
@OptIn(ExperimentalWearMaterialApi::class, ExperimentalHorologistApi::class)
@Composable
fun PodcastDetailsScreen(
    uiState: PodcastDetailsScreenState,
    onPlayButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onPlayEpisode: (List<PlayerEpisode>) -> Unit,
    onDismiss: () -> Unit
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
            is PodcastDetailsScreenState.Loaded -> {
                EntityScreen(
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(text = uiState.podcast.title)
                        }
                    },
                    buttonsContent = {
                        ButtonsContent(
                            episodes = uiState.episodeList,
                            onPlayButtonClick = onPlayButtonClick,
                            onPlayEpisode = onPlayEpisode
                        )
                    },
                    content = {
                        items(uiState.episodeList) { episode: PlayerEpisode ->
                            MediaContent(
                                episode = episode,
                                episodeArtworkPlaceholder = rememberVectorPainter(
                                    image = Icons.Default.MusicNote,
                                    tintColor = Color.Blue,
                                ),
                                onEpisodeItemClick
                            )
                        }
                    }
                )
            }

            PodcastDetailsScreenState.Empty -> {
                AlertDialog(
                    showDialog = true,
                    onDismiss = { onDismiss },
                    message = stringResource(id = R.string.podcasts_no_episode_podcasts)
                )
            }

            PodcastDetailsScreenState.Loading -> {
                EntityScreen(
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(text = stringResource(id = R.string.loading))
                        }
                    },
                    buttonsContent = {
                        ButtonsContent(
                            episodes = emptyList(),
                            onPlayButtonClick = { },
                            onPlayEpisode = { }
                        )
                    },
                    content = {
                        items(count = 2) {
                            PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
                        }
                    }
                )
            }
        }
    }
}

/**
 * A composable function that displays a play button ([Chip]) for a list of episodes.
 *
 * When the button is clicked, it calls both [onPlayButtonClick] and [onPlayEpisode] lambda
 * parameters.
 *
 * @param episodes The list of [PlayerEpisode] to be played.
 * @param onPlayButtonClick A callback function invoked when the play button is clicked.
 * This is intended for actions that need to happen before the episode list is passed to player.
 * For example, to inform the UI that the button is clicked.
 * @param onPlayEpisode A callback function invoked when the play button is clicked,
 * passing the list of [PlayerEpisode] to be played. This is intended for the actual
 * action of the player to start playing the episodes.
 */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (List<PlayerEpisode>) -> Unit,
) {

    Chip(
        label = stringResource(id = R.string.button_play_content_description),
        onClick = {
            onPlayButtonClick()
            onPlayEpisode(episodes)
        },
        modifier = Modifier.padding(bottom = 16.dp),
        icon = Icons.Outlined.PlayArrow.asPaintable(),
    )
}

/**
 * Represents the different states of the Podcast Details screen.
 * This sealed class allows for a clear and concise way to manage the UI state
 * of the Podcast Details screen, including loading, loaded, and empty states.
 *
 * @see PodcastInfo
 * @see PlayerEpisode
 */
@ExperimentalHorologistApi
sealed class PodcastDetailsScreenState {

    /**
     * Represents the loading state of the Podcast Details screen.
     * This state indicates that the application is currently fetching
     * or processing data required to display the podcast details.
     *
     * This is one of the possible states for the [PodcastDetailsScreenState] sealed class.
     * When the screen is in this state, a loading indicator (e.g., a progress bar)
     * should be displayed to the user to signify that data is being retrieved.
     */
    data object Loading : PodcastDetailsScreenState()

    /**
     * Represents the state of the Podcast Details screen when the data has been successfully loaded.
     *
     * This state indicates that both the list of episodes and the podcast information have been
     * fetched and are ready to be displayed.
     *
     * @property episodeList The list of episodes associated with the podcast. Each episode is
     * represented by a [PlayerEpisode] object, containing details like title, description,
     * audio URL, etc.
     * @property podcast The [PodcastInfo] object containing metadata about the podcast itself,
     * such as its title, author, description, artwork URL, etc.
     */
    data class Loaded(
        val episodeList: List<PlayerEpisode>,
        val podcast: PodcastInfo,
    ) : PodcastDetailsScreenState()

    /**
     * Represents the state of the Podcast Details Screen when there is no data to display.
     * This state indicates that the podcast details are empty or have not yet been loaded.
     * It's typically used as an initial state or when an error occurs that prevents data retrieval.
     */
    data object Empty : PodcastDetailsScreenState()
}

/**
 * Podcast Details Screen Loaded Preview
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastDetailsScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    @OptIn(ExperimentalHorologistApi::class)
    PodcastDetailsScreen(
        uiState = PodcastDetailsScreenState.Loaded(
            episodeList = listOf(episode),
            podcast = PreviewPodcastEpisodes.first().podcast
        ),
        onPlayButtonClick = { },
        onEpisodeItemClick = {},
        onPlayEpisode = {},
        onDismiss = {}
    )
}

/**
 * Podcast Details Screen Loading Preview
 */
@Suppress("unused")
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastDetailsScreenLoadingPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    @OptIn(ExperimentalHorologistApi::class)
    PodcastDetailsScreen(
        uiState = PodcastDetailsScreenState.Loading,
        onPlayButtonClick = { },
        onEpisodeItemClick = {},
        onPlayEpisode = {},
        onDismiss = {}
    )
}
