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

package com.example.jetcaster.ui.shared

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.HtmlTextContainer
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.ui.theme.JetcasterTheme
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * A composable function that displays a single episode in a list.
 *
 * This function renders a card-like item that represents an episode, including
 * its title, podcast information, and actions. It uses [EpisodeListItemHeader]
 * and [EpisodeListItemFooter] for rendering subcomponents of the item.
 *
 * Our root composable is a [Box] whose [Modifier] `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.padding] that adds 8.dp to the `vertical` sides and 16.dp to the
 * to the `horizontal` sides. In the [BoxScope] `content` Comosable lambda argument of the [Box]
 * we compose a [Surface] whose arguments are:
 *  - `shape` the [Shapes.large] of our custom [MaterialTheme.shapes].
 *  - `color` the [ColorScheme.surfaceContainer] of our custom [MaterialTheme.colorScheme].
 *  - `onClick` is a lambda that calls our lambda parameter [onClick] with our [EpisodeInfo] parameter
 *  [episode].
 *
 * In the `content` Composable lambda argument of the [Surface] we compose a [Column] whose [Modifier]
 * `modifier` argument is a [Modifier.padding] that adds 16.dp to the `horizontal` sides and 8.dp to
 * the `vertical` sides. In the [ColumnScope] `content` Composable lambda argument of the [Column]
 * we compose our [EpisodeListItemHeader] and [EpisodeListItemFooter].
 *
 * The arguments of the [EpisodeListItemHeader] are:
 *  - `episode` our [EpisodeInfo] parameter [episode].
 *  - `podcast` our [PodcastInfo] parameter [podcast].
 *  - `showPodcastImage` our [Boolean] parameter [showPodcastImage].
 *  - `showSummary` our [Boolean] parameter [showSummary].
 *  - `modifier` a [Modifier.padding] that adds 8.dp to the `bottom` side.
 *
 * The arguments of the [EpisodeListItemFooter] are:
 *  - `episode` our [EpisodeInfo] parameter [episode].
 *  - `podcast` our [PodcastInfo] parameter [podcast].
 *  - `onQueueEpisode` our lambda parameter [onQueueEpisode].
 *
 * @param episode The [EpisodeInfo] object containing details about the episode.
 * @param podcast The [PodcastInfo] object containing details about the podcast.
 * @param onClick A callback function that is invoked when the user clicks on the episode item.
 * It receives the [EpisodeInfo] as a parameter.
 * @param onQueueEpisode A callback function that is invoked when the user wants to queue the
 * episode for playback. It receives the [PlayerEpisode] as parameter.
 * @param modifier Modifier to be applied to the root layout element. Our three callers all pass
 * us a [Modifier.fillMaxWidth].
 * @param showPodcastImage [Boolean] flag to control whether the podcast image is displayed.
 * Defaults to `true`.
 * @param showSummary [Boolean] flag to control whether the episode summary is displayed in the
 * header. Defaults to `false`.
 */
@Composable
fun EpisodeListItem(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onClick: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    showPodcastImage: Boolean = true,
    showSummary: Boolean = false,
) {
    Box(modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(episode) }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Part
                EpisodeListItemHeader(
                    episode = episode,
                    podcast = podcast,
                    showPodcastImage = showPodcastImage,
                    showSummary = showSummary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Bottom Part
                EpisodeListItemFooter(
                    episode = episode,
                    podcast = podcast,
                    onQueueEpisode = onQueueEpisode,
                )
            }
        }
    }
}

/**
 * Displays the footer of an episode list item.
 *
 * This composable displays a row containing:
 * - A play button to initiate playback (currently a placeholder).
 * - Episode duration and/or published date.
 * - An "Add to Queue" button.
 * - A "More" options button (currently a placeholder).
 *
 * @param episode The [EpisodeInfo] containing data about the episode.
 * @param podcast The [PodcastInfo] containing data about the podcast to which the episode belongs.
 */
@Composable
private fun EpisodeListItemFooter(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            imageVector = Icons.Rounded.PlayCircleFilled,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp)
                ) { /* TODO */ }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button }
        )

        val duration = episode.duration
        Text(
            text = when {
                duration != null -> {
                    // If we have the duration, we combine the date/duration via a
                    // formatted string
                    stringResource(
                        R.string.episode_date_duration,
                        MediumDateFormatter.format(episode.published),
                        duration.toMinutes().toInt()
                    )
                }
                // Otherwise we just use the date
                else -> MediumDateFormatter.format(episode.published)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )

        IconButton(
            onClick = {
                onQueueEpisode(
                    PlayerEpisode(
                        podcastInfo = podcast,
                        episodeInfo = episode
                    )
                )
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = stringResource(R.string.cd_add),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = { /* TODO */ },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EpisodeListItemHeader(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    showPodcastImage: Boolean,
    showSummary: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(
            modifier =
            Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = episode.title,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            if (showSummary) {
                HtmlTextContainer(text = episode.summary) {
                    Text(
                        text = it,
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            } else {
                Text(
                    text = podcast.title,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        if (showPodcastImage) {
            EpisodeListItemImage(
                podcast = podcast,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        }
    }
}

@Composable
private fun EpisodeListItemImage(
    podcast: PodcastInfo,
    modifier: Modifier = Modifier
) {
    PodcastImage(
        podcastImageUrl = podcast.imageUrl,
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun EpisodeListItemPreview() {
    JetcasterTheme {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            podcast = PreviewPodcasts[0],
            onClick = {},
            onQueueEpisode = {},
            showSummary = true
        )
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
