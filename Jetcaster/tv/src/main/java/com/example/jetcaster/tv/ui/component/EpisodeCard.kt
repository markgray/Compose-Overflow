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

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CardScale
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.WideCardContainer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import java.time.Duration

/**
 * A composable function that displays a card representing an episode.
 *
 * This card displays an episode's thumbnail and metadata, including title,
 * publication date, and optionally a play/pause button. It's designed to be
 * a visually consistent element for showing episodes in a list or grid.
 *
 * @param playerEpisode The [PlayerEpisode] data object containing information about the episode to
 * be displayed. This includes the episode's title, description, publication date, and thumbnail image.
 * @param onClick A callback function that is invoked when the user clicks on the episode card.
 * This is typically used to navigate to the episode's detail screen or start playback.
 * @param modifier An optional [Modifier] to customize the layout behavior and appearance of the card.
 * By default, it uses a basic layout configuration.
 * @param cardSize The size of the card's thumbnail. Defaults to `JetcasterAppDefaults.thumbnailSize.episode`.
 * You can adjust this to change the dimensions of the thumbnail image within the card.
 */
@Composable
internal fun EpisodeCard(
    playerEpisode: PlayerEpisode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardSize: DpSize = JetcasterAppDefaults.thumbnailSize.episode,
) {
    WideCardContainer(
        imageCard = {
            EpisodeThumbnail(
                playerEpisode = playerEpisode,
                onClick = onClick,
                modifier = Modifier.size(cardSize)
            )
        },
        title = {
            EpisodeMetaData(
                playerEpisode = playerEpisode,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .width(width = JetcasterAppDefaults.cardWidth.small * 2)
            )
        },
        modifier = modifier
    )
}

/**
 * Displays a thumbnail for an episode.
 *
 * This composable renders a clickable card that displays the thumbnail of a given
 * [PlayerEpisode]. It uses the [Thumbnail] composable to display the actual image.
 *
 * @param playerEpisode The [PlayerEpisode] data containing the thumbnail information.
 * @param onClick The callback to be invoked when the thumbnail is clicked.
 * @param modifier Modifier to be applied to the root card.
 * @param interactionSource The [MutableInteractionSource] representing the stream of Interactions
 * for the card. Defaults to a new remembered [MutableInteractionSource].
 */
@Composable
private fun EpisodeThumbnail(
    playerEpisode: PlayerEpisode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        scale = CardScale.None,
        shape = CardDefaults.shape(shape = RoundedCornerShape(size = 12.dp)),
        modifier = modifier,
    ) {
        Thumbnail(episode = playerEpisode, size = JetcasterAppDefaults.thumbnailSize.episode)
    }
}

/**
 * Displays the metadata for a podcast episode, including the title, podcast name,
 * publish date, and duration (if available).
 *
 * @param playerEpisode The [PlayerEpisode] object containing the metadata.
 * @param modifier Modifier for styling the layout.
 */
@Composable
private fun EpisodeMetaData(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier
) {
    val duration: Duration? = playerEpisode.duration
    Column(modifier = modifier) {
        Text(
            text = playerEpisode.title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = playerEpisode.podcastName, style = MaterialTheme.typography.bodySmall)
        if (duration != null) {
            Spacer(
                modifier = Modifier.height(height = JetcasterAppDefaults.gap.podcastRow * 0.8f)
            )
            EpisodeDataAndDuration(offsetDateTime = playerEpisode.published, duration = duration)
        }
    }
}
