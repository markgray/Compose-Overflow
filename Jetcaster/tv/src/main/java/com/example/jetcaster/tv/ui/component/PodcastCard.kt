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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CardScale
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * A composable function that displays a card representing a podcast.
 *
 * This card displays the podcast's thumbnail image and title, and is clickable.
 *
 * Our root composable is a [StandardCardContainer] which is an opinionated TV Material Card layout
 * with an image and text content to show information about a subject. The `imageCard` argument of
 * the [StandardCardContainer] is a lambda which accepts the [MutableInteractionSource] passed the
 * lambda in the variable `it` then composes a [Card] whose arguments are:
 *  - `onClick` is our lambda parameter [onClick]
 *  - `interactionSource` is the [MutableInteractionSource] passed the lambda in `it`.
 *  - `scale` is [CardScale.None]
 *  - `shape` is a [CardDefaults.shape] whose `shape` is a [RoundedCornerShape] of `size` `12.dp
 *
 * In the [ColumnScope] `content` composable lambda argument of the [StandardCardContainer] we
 * compose a [Thumbnail] whose `podcastInfo` argument is our [PodcastInfo] parameter [podcastInfo],
 * and whose `size` is `JetcasterAppDefaults.thumbnailSize.podcast` (a [DpSize] of 196.dp by 196.dp.
 *
 * The `title` argument of the [StandardCardContainer] is a lambda that composes a [Text] whose
 * `text` argument is the [PodcastInfo.title] of our [PodcastInfo] parameter [podcastInfo], and
 * whose `modifier` argument is a [Modifier.padding] that adds 12.dp to the top of the [Text], and
 * the `modifier` argument of the [StandardCardContainer] is our [Modifier] parameter [modifier].
 *
 * @param podcastInfo The [PodcastInfo] object containing the data for the podcast to display.
 * @param onClick The callback function to be executed when the card is clicked.
 * @param modifier The modifier to be applied to the card container.
 */
@Composable
internal fun PodcastCard(
    podcastInfo: PodcastInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StandardCardContainer(
        imageCard = {
            Card(
                onClick = onClick,
                interactionSource = it,
                scale = CardScale.None,
                shape = CardDefaults.shape(shape = RoundedCornerShape(size = 12.dp))
            ) {
                Thumbnail(
                    podcastInfo = podcastInfo,
                    size = JetcasterAppDefaults.thumbnailSize.podcast
                )
            }
        },
        title = {
            Text(text = podcastInfo.title, modifier = Modifier.padding(top = 12.dp))
        },
        modifier = modifier,
    )
}
