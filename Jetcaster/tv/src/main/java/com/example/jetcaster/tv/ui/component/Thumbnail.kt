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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * Displays a thumbnail image for a podcast, using the podcast's image URL.
 *
 * This composable provides a convenient way to display a podcast thumbnail with
 * customizable appearance options. It delegates the actual image loading and
 * display to the lower-level [Thumbnail] composable, passing in the image URL
 * from the provided [PodcastInfo].
 *
 * @param podcastInfo The [PodcastInfo] object containing the image URL to be displayed.
 * @param modifier Modifier to be applied to the thumbnail image. Our callers do not pass us any so
 * the empty, default, or starter [Modifier] that contains no elements is used.
 * @param shape The shape of the thumbnail image. Defaults to a rounded rectangle.
 * @param size The desired size of the thumbnail. Defaults to a medium-sized square.
 * @param contentScale How the image should be scaled to fit within the given size.
 * Defaults to [ContentScale.Crop].
 */
@Composable
fun Thumbnail(
    podcastInfo: PodcastInfo,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(size = 12.dp),
    size: DpSize = DpSize(
        width = JetcasterAppDefaults.cardWidth.medium,
        height = JetcasterAppDefaults.cardWidth.medium
    ),
    contentScale: ContentScale = ContentScale.Crop
): Unit =
    Thumbnail(
        url = podcastInfo.imageUrl,
        modifier = modifier,
        shape = shape,
        size = size,
        contentScale = contentScale
    )

/**
 * Displays a thumbnail image for a given [PlayerEpisode], fetching the image from the episode's
 * podcast image URL.
 *
 * @param episode The [PlayerEpisode] containing the podcast image URL to display.
 * @param modifier [Modifier] to be applied to the thumbnail image. Our callers do not pass us any
 * so the empty, default, or starter [Modifier] that contains no elements is used.
 * @param shape The shape of the thumbnail. Defaults to a rounded rectangle with 12.dp corner radius.
 * @param size The size of the thumbnail. Defaults to a square with width and height equal to
 * `JetcasterAppDefaults.cardWidth.medium` (`196.dp`).
 * @param contentScale How the image should be scaled to fit the thumbnail's bounds.
 * Defaults to [ContentScale.Crop].
 */
@Composable
fun Thumbnail(
    episode: PlayerEpisode,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(size = 12.dp),
    size: DpSize = DpSize(
        width = JetcasterAppDefaults.cardWidth.medium,
        height = JetcasterAppDefaults.cardWidth.medium
    ),
    contentScale: ContentScale = ContentScale.Crop
): Unit =
    Thumbnail(
        url = episode.podcastImageUrl,
        modifier = modifier,
        shape = shape,
        size = size,
        contentScale = contentScale
    )

/**
 * Displays a thumbnail image from a given URL.
 *
 * This composable provides a convenient way to display a thumbnail image with customizable
 * shape, size, and content scaling. It utilizes the [PodcastImage] composable internally to
 * handle image loading and display.
 *
 * @param url The URL of the thumbnail image.
 * @param modifier Modifier to be applied to the thumbnail.
 * @param shape The shape of the thumbnail. Defaults to a rounded rectangle with 12.dp corner radius.
 * @param size The size of the thumbnail. Defaults to a square with medium card width from
 * [JetcasterAppDefaults].
 * @param contentScale How the image should be scaled to fit the thumbnail bounds. Defaults to
 * [ContentScale.Crop].
 */
@Composable
fun Thumbnail(
    url: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(size = 12.dp),
    size: DpSize = DpSize(
        width = JetcasterAppDefaults.cardWidth.medium,
        height = JetcasterAppDefaults.cardWidth.medium
    ),
    contentScale: ContentScale = ContentScale.Crop
): Unit =
    PodcastImage(
        podcastImageUrl = url,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .clip(shape = shape)
            .size(size = size),
    )
