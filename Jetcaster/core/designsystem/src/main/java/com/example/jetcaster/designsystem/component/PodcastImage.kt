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

package com.example.jetcaster.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.jetcaster.core.designsystem.R

/**
 * Displays an image for a podcast, handling loading, error, and placeholder states.
 *
 * This composable function fetches and displays an image from a given URL. It provides visual
 * feedback during the image loading process and handles potential errors gracefully. It also uses
 * a placeholder image when the image is loading or fails to load, and a placeholder brush for a
 * smooth transition.
 *
 * If the `current` [LocalInspectionMode] is `true` (when the composition is rendered in a preview,
 * LocalInspectionMode.current evaluates to true) we just compose a [Box] whose `modifier` argument
 * chains to our [Modifier] parameter [modifier] a [Modifier.background] whose `color` argument is
 * the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme] and then return
 *
 * Otherwise we initialize and remember our [MutableState] wrapped [AsyncImagePainter.State] variable
 * `var imagePainterState` to the initial value [AsyncImagePainter.State.Empty] (the request has not
 * been started). Then we initialize and remember our [AsyncImagePainter] using the
 * [rememberAsyncImagePainter] method with its `model` argument the [ImageRequest] that is built
 * using an [ImageRequest.Builder] for the `context` argument of the `current` [LocalContext], to
 * which is chained a [ImageRequest.Builder.data] whose `data` argument is our [String] parameter
 * [podcastImageUrl], to which is chained a [ImageRequest.Builder.crossfade] whose `enable` argument
 * is `true`, and which the [ImageRequest.Builder.build] method then builds into an [ImageRequest].
 * The `contentScale` argument of the [rememberAsyncImagePainter] method is our [ContentScale]
 * parameter [contentScale], and the `onState` argument is a lambda that sets our [MutableState]
 * wrapped [AsyncImagePainter.State] variable `imagePainterState` to the [AsyncImagePainter.State]
 * passed the lambda.
 *
 * Then our root composable is a [Box] whose `modifier` argument is our [Modifier] parameter [modifier]
 * and whose `contentAlignment` argument is [Alignment.Center]. In the [BoxScope] `content` Composable
 * lambda argument of the [Box] we first branch on the value of [AsyncImagePainter.State] variable
 * `imagePainterState`:
 *  - [AsyncImagePainter.State.Loading] or [AsyncImagePainter.State.Error] we compose an [Image]
 *  whose `painter` argument is the [Painter] created from the drawable with the resource `id`
 *  `R.drawable.img_empty`. The `contentDescription` argument of the [Image] is `null` and the
 *  [Modifier] `modifier` argument is [Modifier.fillMaxSize].
 *  - `else` we compose a [Box] whose [Modifier] `modifier` argument is a [Modifier.background]
 *  whose `brush` argument is our [Brush] parameter [placeholderBrush], to which is chained a
 *  [Modifier.fillMaxSize].
 *
 * The next composable is an [Image] whose [Painter] `painter` argument is our [AsyncImagePainter]
 * variable `imageLoader`, whose `contentDescription` argument is our [String] parameter
 * [contentDescription], whose `contentScale` argument is our [ContentScale] parameter [contentScale],
 * and whose [Modifier] `modifier` argument is our [Modifier] parameter [modifier].
 *
 * @param podcastImageUrl The URL of the podcast image to load.
 * @param contentDescription A text description of the podcast image for accessibility.
 * @param modifier Modifier to apply to the image container.
 * @param contentScale How the image should be scaled to fit the container. Defaults to
 * [ContentScale.Crop].
 * @param placeholderBrush The brush to use as a background placeholder while the image is loading
 * or in case of error. Defaults to [thumbnailPlaceholderDefaultBrush].
 */
@Composable
fun PodcastImage(
    podcastImageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderBrush: Brush = thumbnailPlaceholderDefaultBrush(),
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(color = MaterialTheme.colorScheme.primary))
        return
    }

    var imagePainterState: AsyncImagePainter.State by remember {
        mutableStateOf(value = AsyncImagePainter.State.Empty)
    }

    val imageLoader: AsyncImagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(data = podcastImageUrl)
            .crossfade(enable = true)
            .build(),
        contentScale = contentScale,
        onState = { state: AsyncImagePainter.State -> imagePainterState = state }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (imagePainterState) {
            is AsyncImagePainter.State.Loading,
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(id = R.drawable.img_empty),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .background(brush = placeholderBrush)
                        .fillMaxSize()
                )
            }
        }

        Image(
            painter = imageLoader,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
        )
    }
}
