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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.Typography
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * Displays the details of an episode, including a thumbnail, author, title, and custom content.
 * It also supports optional controls.
 *
 * @param playerEpisode The [PlayerEpisode] object containing the episode's information.
 * @param modifier Modifier for styling and layout adjustments of the episode details container.
 * @param controls An optional composable function that can be used to add controls below the
 * episode content. For example, playback controls or other interactive elements.
 * @param verticalArrangement The vertical arrangement of the content within the details column.
 * Defaults to [Arrangement.spacedBy] using `JetcasterAppDefaults.gap.item`.
 * @param content A composable lambda that allows you to add custom content within the episode
 * details column. This lambda is called within a [ColumnScope], providing access to column-specific
 * layout options.
 */
@Composable
internal fun EpisodeDetails(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    controls: (@Composable () -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(JetcasterAppDefaults.gap.item),
    content: @Composable ColumnScope.() -> Unit
) {
    TwoColumn(
        modifier = modifier,
        first = {
            Thumbnail(
                episode = playerEpisode,
                size = JetcasterAppDefaults.thumbnailSize.episodeDetails
            )
        },
        second = {
            Column(
                modifier = modifier,
                verticalArrangement = verticalArrangement
            ) {
                EpisodeAuthor(playerEpisode = playerEpisode)
                EpisodeTitle(playerEpisode = playerEpisode)
                content()
                if (controls != null) {
                    controls()
                }
            }
        }
    )
}

/**
 * Displays the author of a given [PlayerEpisode].
 *
 * This composable renders the author's name from the provided [playerEpisode] data.
 * It utilizes a [Text] composable for display and allows for customization through
 * the [modifier] and [style] parameters.
 *
 * @param playerEpisode The [PlayerEpisode] containing the author's name to display.
 * @param modifier Modifier to be applied to the [Text] composable. This can be used
 * to adjust the layout, size, and other visual aspects of the text.
 * @param style The [TextStyle] to be applied to the author's name. Defaults to the
 * [Typography.bodySmall] of our custom [MaterialTheme.typography]. This allows for
 * customization  of font, color, size, etc.
 */
@Composable
internal fun EpisodeAuthor(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(text = playerEpisode.author, modifier = modifier, style = style)
}

/**
 * Displays the title of a [PlayerEpisode].
 *
 * This composable function renders the title of a given [PlayerEpisode] using a [Text] composable.
 * It allows customization of the text's appearance through a [Modifier] and [TextStyle].
 *
 * @param playerEpisode The [PlayerEpisode] object containing the title to be displayed.
 * @param modifier Optional [Modifier] to apply to the underlying [Text] composable for layout and
 * styling. Defaults to [Modifier].
 * @param style Optional [TextStyle] to apply to the text. Defaults to the [Typography.headlineLarge]
 * of our custom [MaterialTheme.typography]. This allows for customization of font, color, size, etc.
 *
 * @see PlayerEpisode
 * @see Text
 * @see Modifier
 * @see TextStyle
 * @see MaterialTheme.typography
 */
@Composable
internal fun EpisodeTitle(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge
) {
    Text(text = playerEpisode.title, modifier = modifier, style = style)
}
