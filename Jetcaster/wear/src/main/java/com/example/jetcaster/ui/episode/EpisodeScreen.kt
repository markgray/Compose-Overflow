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

package com.example.jetcaster.ui.episode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.jetcaster.R
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.designsystem.component.HtmlTextContainer
import com.example.jetcaster.ui.components.MediumDateFormatter
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

/**
 * Composable function representing the Episode screen.
 *
 * This composable is a high-level representation of the Episode screen, responsible for
 * collecting the UI state from the [EpisodeViewModel] and passing it down to the
 * lower-level stateless [EpisodeScreen] composable. It also handles the interactions with
 * the [EpisodeViewModel] by using function references to methods in the [EpisodeViewModel]
 * as lambda arguments to the lower-level [EpisodeScreen] override.
 *
 * @param onPlayButtonClick Lambda function to be executed when the play button is clicked.
 * @param onDismiss Lambda function to be executed when the screen should be dismissed.
 * @param modifier Modifier for styling and layout adjustments.
 * @param episodeViewModel The ViewModel responsible for managing the episode's data and logic.
 * Defaults to a ViewModel instance provided by Hilt.
 */
@Composable
fun EpisodeScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    episodeViewModel: EpisodeViewModel = hiltViewModel()
) {
    val uiState: EpisodeScreenState by episodeViewModel.uiState.collectAsStateWithLifecycle()

    EpisodeScreen(
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onPlayEpisode = episodeViewModel::onPlayEpisode,
        onAddToQueue = episodeViewModel::addToQueue,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

/**
 * Stateless composable function representing the Episode screen, it is called by the "Stateful"
 * [EpisodeScreen] composable override.
 *
 * @param uiState The current state of the Episode screen, including the loaded episode.
 * @param onPlayButtonClick Lambda function to be executed when the play button is clicked.
 * @param onPlayEpisode Lambda function to be executed when an episode is played.
 * @param onAddToQueue Lambda function to be executed when an episode is added to the queue.
 * @param onDismiss Lambda function to be executed when the screen should be dismissed.
 * @param modifier Modifier for styling and layout adjustments.
 */
@Composable
fun EpisodeScreen(
    uiState: EpisodeScreenState,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onAddToQueue: (PlayerEpisode) -> Unit,
    onDismiss: () -> Unit,
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
            is EpisodeScreenState.Loaded -> {
                val title = uiState.episode.episode.title

                EntityScreen(
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    buttonsContent = {
                        LoadedButtonsContent(
                            episode = uiState.episode,
                            onPlayButtonClick = onPlayButtonClick,
                            onPlayEpisode = onPlayEpisode,
                            onAddToQueue = onAddToQueue
                        )
                    },
                    content = {
                        episodeInfoContent(episode = uiState.episode)
                    }

                )
            }

            EpisodeScreenState.Empty -> {
                AlertDialog(
                    showDialog = true,
                    onDismiss = { onDismiss },
                    message = stringResource(R.string.episode_info_not_available)
                )
            }
            EpisodeScreenState.Loading -> {
                LoadingScreen()
            }
        }
    }
}

/**
 * Displays a row of buttons for interacting with an episode, specifically a play button and an
 * "add to queue" button.
 *
 * This composable uses the following components:
 * - A [Row] to arrange the buttons horizontally.
 * - [Button] for the play and "add to queue" actions.
 * - Icons from [Icons.Outlined] and [Icons.AutoMirrored.Filled].
 * - [stringResource] for localized content descriptions.
 *
 * The buttons are arranged with a spacing of 6.dp and are center-aligned vertically. Each button
 * takes up 30% of the available space. When the user clicks
 * on play button both [onPlayButtonClick]
 * and [onPlayEpisode] are triggered
 *
 * @param episode The episode data to be used when triggering actions. It's an
 * [EpisodeToPodcast] object.
 * @param onPlayButtonClick Callback triggered when the main play button is clicked.
 * @param onPlayEpisode Callback triggered when an episode should start playing.
 * It receives the [PlayerEpisode] object as its parameter.
 * @param onAddToQueue Callback triggered when an episode should be added to the playback queue.
 * It receives the [PlayerEpisode] object as its parameter.
 * @param enabled Controls whether the buttons are enabled or disabled. Defaults to `true`.
 */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun LoadedButtonsContent(
    episode: EpisodeToPodcast,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onAddToQueue: (PlayerEpisode) -> Unit,
    enabled: Boolean = true
) {

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {

        Button(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = stringResource(id = R.string.button_play_content_description),
            onClick = {
                onPlayButtonClick()
                onPlayEpisode(episode.toPlayerEpisode())
            },
            enabled = enabled,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )

        Button(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = stringResource(id = R.string.add_to_queue_content_description),
            onClick = { onAddToQueue(episode.toPlayerEpisode()) },
            enabled = enabled,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )
    }
}
/**
 * Displays a loading screen with a header, placeholder content, and loading buttons.
 *
 * This composable function presents a screen that indicates data is being loaded.
 * It includes a header displaying the text "Loading", placeholder chips to represent
 * loading data, and a section for loading-related buttons.
 *
 * The screen uses the [EntityScreen] composable to structure its layout.
 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LoadingScreen() {
    EntityScreen(
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.loading))
            }
        },
        buttonsContent = {
            LoadingButtonsContent()
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

/**
 * [LoadingButtonsContent] is a composable function that displays a row of two disabled buttons:
 * a "Play" button and an "Add to Queue" button. These buttons are intended to be used in a loading
 * state where the user should not be able to interact with them.
 *
 * The buttons are arranged horizontally with spacing between them and are vertically centered.
 * Both buttons are disabled and use icons to indicate their function.
 *
 * The content descriptions for the buttons are loaded from string resources.
 *
 * This Composable needs [ExperimentalHorologistApi] because it's using
 * the custom Button from the Horologist library.
 *
 * Example Use Case:
 * This function can be used in a UI where content is being loaded, and the user
 * should not be able to interact with media controls or playlist manipulation until
 * the content has finished loading.
 *
 * @see Button
 * @see Icons.Outlined.PlayArrow
 * @see Icons.AutoMirrored.Filled.PlaylistAdd
 */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun LoadingButtonsContent() {
    Row(
        modifier = Modifier
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
            onClick = {},
            enabled = false,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )

        Button(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = stringResource(id = R.string.add_to_queue_content_description),
            onClick = {},
            enabled = false,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )
    }
}

/**
 * Composable function to display episode information within a scaling lazy list.
 *
 * This function displays the author, published date, duration (if available), and
 * summary of an episode. It handles null or empty values for these fields gracefully.
 *
 * @param episode The [EpisodeToPodcast] object containing the episode's information.
 * @see ScalingLazyListScope
 * @see EpisodeToPodcast
 */
private fun ScalingLazyListScope.episodeInfoContent(episode: EpisodeToPodcast) {
    val author = episode.episode.author
    val duration = episode.episode.duration
    val published = episode.episode.published
    val summary = episode.episode.summary

    if (!author.isNullOrEmpty()) {
        item {
            Text(
                text = author,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2
            )
        }
    }

    item {
        Text(
            text = when {
                duration != null -> {
                    // If we have the duration, we combine the date/duration via a
                    // formatted string
                    stringResource(
                        R.string.episode_date_duration,
                        MediumDateFormatter.format(published),
                        duration.toMinutes().toInt()
                    )
                }
                // Otherwise we just use the date
                else -> MediumDateFormatter.format(published)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
    }
    if (summary != null) {
        val summaryInParagraphs = summary.split("\n+".toRegex())
        items(summaryInParagraphs) {
            HtmlTextContainer(text = summary) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2,
                    color = LocalContentColor.current,
                    modifier = Modifier.listTextPadding()
                )
            }
        }
    }
}
