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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.ImageBackgroundRadialGradientScrim

/**
 * Composable function that provides a background container with an image derived from a
 * [PlayerEpisode].
 *
 * This function is a convenience wrapper around the other `BackgroundContainer` function,
 * simplifying the call when the background image URL is derived from a `PlayerEpisode` object.
 *
 * @param playerEpisode The [PlayerEpisode] object containing the podcast image URL to be used as the background.
 * @param modifier Modifier to be applied to the container.
 * @param contentAlignment The alignment of the content within the container. Defaults to
 * [Alignment.Center].
 * @param content The composable content to be placed within the container, allowing access
 * to [BoxScope].
 */
@Composable
internal fun BackgroundContainer(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) =
    BackgroundContainer(
        imageUrl = playerEpisode.podcastImageUrl,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content
    )

/**
 * Composable function that provides a background container with an image derived from [PodcastInfo]
 * parameter [podcastInfo].
 *
 * This function acts as a convenience wrapper around the more general `BackgroundContainer`
 * function that takes an image URL directly. It simplifies the use case where you have a
 * `PodcastInfo` object and want to display its image as a background.
 *
 * @param podcastInfo The [PodcastInfo] object containing the image URL to be used as the background.
 * @param modifier Modifier to be applied to the background container.
 * @param contentAlignment The alignment of the content within the container.
 * Defaults to [Alignment.Center].
 * @param content The composable content to be placed within the container.
 */
@Composable
internal fun BackgroundContainer(
    podcastInfo: PodcastInfo,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) =
    BackgroundContainer(
        imageUrl = podcastInfo.imageUrl,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content
    )

/**
 * A composable function that creates a container with a background image and allows content to be
 * placed on top.
 *
 * This function provides a `Box` layout with an image set as the background and then overlays
 * the provided content on top of it. The background image will fill the entire container.
 *
 * @param imageUrl The URL of the background image to display.
 * @param modifier Modifiers to be applied to the container. This allows customization of the
 * size, padding, and other layout properties of the container.
 * @param contentAlignment The alignment of the content within the container. Defaults to
 * `Alignment.Center`. This determines where the content will be placed relative to the
 * container's bounds.
 * @param content The composable content to be placed on top of the background image. This
 * lambda function provides access to the `BoxScope`, allowing the use of `Modifier.align`
 * and other `BoxScope` modifiers.
 *
 * @see Background
 * @see Box
 * @see Modifier
 * @see Alignment
 * @see BoxScope
 */
@Composable
internal fun BackgroundContainer(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        Background(imageUrl = imageUrl, modifier = Modifier.fillMaxSize())
        content()
    }
}

/**
 * Composable function that displays a background image with a radial gradient scrim.
 *
 * This function utilizes the [ImageBackgroundRadialGradientScrim] composable to display an
 * image fetched from the provided URL. A radial gradient is applied on top of the image,
 * transitioning from black at the center to transparent towards the edges. This creates a
 * visually appealing effect that helps to highlight content placed over the background.
 *
 * @param imageUrl The URL of the image to be displayed as the background.
 * @param modifier Optional [Modifier] to apply to the background image. This can be used
 * to customize the layout, size, and appearance of the background.
 */
@Composable
private fun Background(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    ImageBackgroundRadialGradientScrim(
        url = imageUrl,
        colors = listOf(Color.Black, Color.Transparent),
        modifier = modifier,
    )
}
