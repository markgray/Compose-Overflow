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

package com.example.jetcaster.ui.podcasts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.ui.preview.WearPreviewPodcasts
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.DefaultEntityScreenHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable
fun PodcastsScreen(
    podcastsViewModel: PodcastsViewModel = hiltViewModel(),
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    onDismiss: () -> Unit,
) {
    val uiState: PodcastsScreenState by podcastsViewModel.uiState.collectAsStateWithLifecycle()

    val modifiedState: PodcastsScreenState = when (uiState) {
        is PodcastsScreenState.Loaded -> {
            val modifiedPodcast: List<PodcastInfo> = (uiState as PodcastsScreenState.Loaded)
                .podcastList.map { podcast: PodcastInfo ->
                    podcast.takeIf { it.title.isNotEmpty() }
                        ?: podcast.copy(title = stringResource(id = R.string.no_title))
                }
            PodcastsScreenState.Loaded(podcastList = modifiedPodcast)
        }
        PodcastsScreenState.Empty,
        PodcastsScreenState.Loading,
            -> uiState
    }

    PodcastsScreen(
        podcastsScreenState = modifiedState,
        onPodcastsItemClick = onPodcastsItemClick,
        onDismiss = onDismiss
    )
}

@ExperimentalHorologistApi
@Composable
fun PodcastsScreen(
    podcastsScreenState: PodcastsScreenState,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val columnState: ScalingLazyColumnState = rememberResponsiveColumnState()
    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier
    ) {
        when (podcastsScreenState) {
            is PodcastsScreenState.Loaded -> PodcastScreenLoaded(
                podcastList = podcastsScreenState.podcastList,
                onPodcastsItemClick = onPodcastsItemClick
            )

            PodcastsScreenState.Empty ->
                PodcastScreenEmpty(onDismiss)

            PodcastsScreenState.Loading ->
                PodcastScreenLoading()
        }
    }
}

@Composable
fun PodcastScreenLoaded(
    podcastList: List<PodcastInfo>,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.podcasts))
            }
        },
        content = {
            items(count = podcastList.size) { index: Int ->
                MediaContent(
                    podcast = podcastList[index],
                    downloadItemArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue,
                    ),
                    onPodcastsItemClick = onPodcastsItemClick

                )
            }
        }
    )
}

@Composable
fun PodcastScreenEmpty(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        showDialog = true,
        message = stringResource(id = R.string.podcasts_no_podcasts),
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun PodcastScreenLoading(
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            DefaultEntityScreenHeader(
                title = stringResource(id = R.string.podcasts)
            )
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

/**
 * Preview of the [PodcastScreenLoaded] composable.
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenLoadedPreview(
    @PreviewParameter(WearPreviewPodcasts::class) podcasts: PodcastInfo
) {
    @Suppress("UnusedVariable", "unused")
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    PodcastScreenLoaded(
        podcastList = listOf(podcasts),
        onPodcastsItemClick = {}
    )
}

/**
 * Preview of the [PodcastScreenLoading] composable.
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenLoadingPreview() {
    @Suppress("UnusedVariable", "unused")
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    PodcastScreenLoading()
}

/**
 * Preview of the [PodcastScreenEmpty] composable.
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenEmptyPreview() {
    PodcastScreenEmpty(onDismiss = {})
}

/**
 * Displays a media content item (typically a podcast) as a Chip.
 *
 * This composable represents a single item in a list of media content, typically podcasts.
 * It displays the podcast's title and author as the main and secondary labels, respectively,
 * and provides a visual representation using an image loaded via Coil.
 *
 * @param podcast The [PodcastInfo] object containing the details of the podcast to be displayed.
 * It holds information like the podcast's title, author, and image URL.
 * @param downloadItemArtworkPlaceholder An optional [Painter] to be used as a placeholder while
 * the podcast image is being loaded. If null, a default placeholder from Coil will be used.
 * @param onPodcastsItemClick A callback function that is invoked when the user clicks on the
 * podcast item. It receives the [PodcastInfo] of the clicked podcast as a parameter.
 */
@Composable
fun MediaContent(
    podcast: PodcastInfo,
    downloadItemArtworkPlaceholder: Painter?,
    onPodcastsItemClick: (PodcastInfo) -> Unit
) {
    val mediaTitle: String = podcast.title

    val secondaryLabel: String = podcast.author

    Chip(
        label = mediaTitle,
        onClick = { onPodcastsItemClick(podcast) },
        secondaryLabel = secondaryLabel,
        icon = CoilPaintable(model = podcast.imageUrl, placeholder = downloadItemArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}
