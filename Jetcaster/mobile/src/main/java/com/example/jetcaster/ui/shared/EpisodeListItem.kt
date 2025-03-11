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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material3.Typography
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
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
import java.time.Duration
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
 * Our root composable is a [Row] whose `verticalAlignment` argument is [Alignment.CenterVertically],
 * and whose [Modifier] `modifier` argument is our [Modifier] parameter [modifier]. In the [RowScope]
 * `content` Composable lambda argument we compose an [Image] whose arguments are:
 *  - `imageVector` is the [ImageVector] drawn by [Icons.Rounded.PlayCircleFilled].
 *  - `contentDescription` is the string with resource ID "R.string.cd_play" ("Play").
 *  - `contentScale` is [ContentScale.Fit].
 *  - `colorFilter` is a [ColorFilter.tint] that uses the [ColorScheme.primary] of our custom
 *  [MaterialTheme.colorScheme] as its `color` argument.
 *  - `modifier` is a [Modifier.clickable] whose `interactionSource` is a remembered instance of
 *  [MutableInteractionSource], whose `indication` is a [ripple] whose `bounded` argument is `false`,
 *  and whose `radius` argument is 24.dp, and the `onClick` lambda argument is an empty lambda.
 *  Chained to that is a [Modifier.size] whose `size` argument is 48.dp, a [Modifier.padding] that
 *  adds 6.dp to the `all` sides, and a [Modifier.semantics] that sets the [Role] of the [Image]
 *  to [Role.Button].
 *
 * Next in the [RowScope] `content` Composable lambda argument we initialize our [Duration] variable
 * `duration` to the [EpisodeInfo.duration] of our [EpisodeInfo] parameter [episode]. Then we compose
 * a [Text] whose arguments are:
 *  - `text` is if our [Duration] variable `duration` is not `null` the formatted string created
 *  by our [String] format with resource ID `R.string.episode_date_duration` from the
 *  [EpisodeInfo.published] property of our [EpisodeInfo] parameter [episode] and the
 *  [Duration.toMinutes] of our [Duration] variable `duration`, and if `duration` is `null the
 *  [String] formatted from the [EpisodeInfo.published] property of [EpisodeInfo] parameter [episode]
 *  using our [MediumDateFormatter].
 *  - `maxLines` is 1.
 *  - `overflow` is [TextOverflow.Ellipsis].
 *  - `style` is the [TextStyle] of [Typography.bodySmall] from our custom [MaterialTheme.typography].
 *  - `modifier` is a [Modifier.padding] that adds 8.dp to the `horizontal` sides, with a
 *  [RowScope.weight] whose `weight` argument is `1f` chained to that.
 *
 * Next in the [RowScope] `content` Composable lambda argument we compose an [IconButton] whose
 * `onClick` lambda argument is a lambda that calls our lambda parameter [onQueueEpisode] with a
 * [PlayerEpisode] whose `podcastInfo` argument is our [PodcastInfo] parameter [podcast] and whose
 * `episodeInfo` argument is our [EpisodeInfo] parameter [episode]. In the `content` Composable
 * lambda argument of the [IconButton] we compose an [Icon] whose arguments are:
 *  - `imageVector` is the [ImageVector] drawn by [Icons.AutoMirrored.Filled.PlaylistAdd].
 *  - `contentDescription` is the string with resource ID `R.string.cd_add` ("Add").
 *  - `tint` is the [ColorScheme.onSurfaceVariant] of our custom [MaterialTheme.colorScheme].
 *
 * Finally in the [RowScope] `content` Composable lambda argument we compose an [IconButton] whose
 * `onClick` lambda argument is an empty lambda. In the `content` Composable lambda argument of the
 * [IconButton] we compose an [Icon] whose arguments are:
 *  - `imageVector` is the [ImageVector] drawn by [Icons.Filled.MoreVert].
 *  - `contentDescription` is the string with resource ID `R.string.cd_more` ("More").
 *  - `tint` is the [ColorScheme.onSurfaceVariant] of our custom [MaterialTheme.colorScheme].
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
            contentDescription = stringResource(id = R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp)
                ) { /* TODO */ }
                .size(size = 48.dp)
                .padding(all = 6.dp)
                .semantics { role = Role.Button }
        )

        val duration: Duration? = episode.duration
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
                .weight(weight = 1f)
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

/**
 * Displays the header section of an episode list item.
 *
 * This composable shows the episode title and either the episode summary or the podcast title,
 * along with an optional podcast image.
 *
 * Our root composable is a [Row] whose [Modifier] `modifier` argument is our [Modifier] parameter
 * [modifier]. In the [RowScope] `content` Composable lambda argument we compose a [Column] whose
 * [Modifier] `modifier` argument is a [RowScope.weight] whose `weight` argument is `1f` chained to
 * a [Modifier.padding] that adds 16.dp to the `end` side. In the [ColumnScope] `content` Composable
 * lambda argument we compose a [Text] whose arguments are:
 *  - `text` is the [EpisodeInfo.title] of our [EpisodeInfo] parameter [episode].
 *  - `maxLines` is 2.
 *  - `minLines` is 1.
 *  - `overflow` is [TextOverflow.Ellipsis].
 *  - `style` is the [TextStyle] of [Typography.titleMedium] from our custom [MaterialTheme.typography].
 *  - `modifier` is a [Modifier.padding] that adds 2.dp to the `vertical` sides.
 *
 * Next in the [ColumnScope] `content` Composable lambda argument if our [Boolean] parameter
 * [showSummary] is `true` we compose an [HtmlTextContainer] whose `text` argument is the
 * [EpisodeInfo.summary] of our [EpisodeInfo] parameter [episode], and in the `content` Composable
 * lambda argument of the [HtmlTextContainer] we accept the [AnnotatedString] passed the lambda in
 * `it`, then compose a [Text] whose arguments are:
 *  - `text` is the [AnnotatedString] passed the lambda in `it`.
 *  - `maxLines` is 2.
 *  - `minLines` is 1.
 *  - `overflow` is [TextOverflow.Ellipsis].
 *  - `style` is the [TextStyle] of [Typography.titleSmall] from our custom [MaterialTheme.typography].
 *
 * And if [showSummary] is `false` we compose a [Text] whose arguments are:
 *  - `text` is the [PodcastInfo.title] of our [PodcastInfo] parameter [podcast].
 *  - `maxLines` is 2.
 *  - `minLines` is 1.
 *  - `overflow` is [TextOverflow.Ellipsis].
 *  - `style` is the [TextStyle] of [Typography.titleSmall] from our custom [MaterialTheme.typography].
 *
 * Next in the [ColumnScope] `content` Composable lambda argument if our [Boolean] parameter
 * [showPodcastImage] is `true` we compose an [EpisodeListItemImage] whose arguments are:
 *  - `podcast` is our [PodcastInfo] parameter [podcast].
 *  - `modifier` is a [Modifier.size] whose `size` argument is 56.dp, with a [Modifier.clip] chained
 *  to that whose `shape` argument is the [Shapes.medium] of our custom [MaterialTheme.shapes].
 *
 * @param episode The [EpisodeInfo] containing details about the episode.
 * @param podcast The [PodcastInfo] containing details about the podcast.
 * @param showPodcastImage [Boolean] indicating whether to display the podcast image.
 * @param showSummary [Boolean] indicating whether to display the episode summary instead of the
 * podcast title.
 * @param modifier [Modifier] for styling and layout adjustments. Our caller [EpisodeListItem] passes
 * us a [Modifier.padding] that adds 8.dp to the `bottom` side.
 */
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
            modifier = Modifier
                .weight(weight = 1f)
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
                    .size(size = 56.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
            )
        }
    }
}

/**
 * Displays the image associated with an episode list item, using the podcast's image URL.
 *
 * This composable is a wrapper around [PodcastImage] that simplifies the display of a podcast's
 * image in the context of an episode list. It automatically passes the podcast's image URL and
 * sets the content description to `null`, since the image is primarily decorative in this context.
 *
 * Our root composable is a [PodcastImage] whose arguments are:
 *  - `podcastImageUrl` is the [PodcastInfo.imageUrl] of our [PodcastInfo] parameter [podcast].
 *  - `contentDescription` is `null`.
 *  - `modifier` is our [Modifier] parameter [modifier].
 *
 * @param podcast The [PodcastInfo] object containing the image URL to display.
 * @param modifier [Modifier] to be applied to the image. Our caller [EpisodeListItemHeader] passes
 * us a [Modifier.size] whose `size` argument is 56.dp, with a [Modifier.clip] chained to that
 * whose `shape` argument is the [Shapes.medium] of our custom [MaterialTheme.shapes].
 */
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

/**
 * Previews for [EpisodeListItem].
 */
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

/**
 * A [DateTimeFormatter] for displaying dates in a medium format that is used to format the
 * [EpisodeInfo.published] date in the [EpisodeListItemHeader] Composable.
 */
private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
