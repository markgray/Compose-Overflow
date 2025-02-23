/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetcaster.ui.home.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetcaster.core.domain.testing.PreviewEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.PodcastToEpisodeInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.fullWidthItem

/**
 * Composes the UI for displaying a category of podcasts, including top podcasts
 * and a list of episodes.
 *
 * This is an extension function of [LazyGridScope] that efficiently renders a list of items within
 * a [LazyVerticalGrid]. It displays a section for "Top Podcasts" followed by a scrollable list of
 * episodes related to the category.
 *
 * @param podcastCategoryFilterResult The result containing the top podcasts and episodes for the
 * category. It's expected to be an instance of [PodcastCategoryFilterResult], which should have
 * properties:
 *  - `topPodcasts`: A [List] of [PodcastInfo] representing the top podcasts in the category.
 *  - `episodes`: A  [List] of [PodcastToEpisodeInfo] representing episodes belonging to the category.
 * @param navigateToPodcastDetails A lambda function that takes a [PodcastInfo] as input and
 * navigates to the details screen of that podcast.
 * @param navigateToPlayer A lambda function that takes an [EpisodeInfo] as input and navigates to
 * the player screen to play that episode.
 * @param onQueueEpisode A lambda function that takes a [PlayerEpisode] as input and handles the
 * enqueuing of that episode to the player's queue.
 * @param onTogglePodcastFollowed A lambda function that takes a [PodcastInfo] as input and handles
 * toggling the follow/unfollow state of the podcast.
 */
fun LazyGridScope.podcastCategory(
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
) {
    fullWidthItem {
        CategoryPodcasts(
            topPodcasts = podcastCategoryFilterResult.topPodcasts,
            navigateToPodcastDetails = navigateToPodcastDetails,
            onTogglePodcastFollowed = onTogglePodcastFollowed
        )
    }

    val episodes: List<PodcastToEpisodeInfo> = podcastCategoryFilterResult.episodes
    items(episodes, key = { it.episode.uri }) { (episode: EpisodeInfo, podcast: PodcastInfo) ->
        EpisodeListItem(
            episode = episode,
            podcast = podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Displays a row of podcasts within a specific category.
 *
 * This composable function takes a list of [PodcastInfo] objects and displays them in a horizontal
 * row using the [CategoryPodcastRow] composable. It also handles navigation to podcast details
 * and toggling the "followed" status of a podcast.
 *
 * @param topPodcasts A list of [PodcastInfo] objects to display in the row.
 * @param navigateToPodcastDetails A lambda function that takes a [PodcastInfo] object and
 * navigates the user to the details screen for that podcast.
 * @param onTogglePodcastFollowed A lambda function that takes a [PodcastInfo] object and toggles
 * whether the podcast is followed or not.
 */
@Composable
private fun CategoryPodcasts(
    topPodcasts: List<PodcastInfo>,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit
) {
    CategoryPodcastRow(
        podcasts = topPodcasts,
        onTogglePodcastFollowed = onTogglePodcastFollowed,
        navigateToPodcastDetails = navigateToPodcastDetails,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Displays a horizontal row of podcast items within a specific category.
 *
 * This composable renders a horizontally scrollable list of podcasts, each represented by a
 * [TopPodcastRowItem]. It handles the layout, spacing, and user interactions for each item
 * in the row.
 *
 * @param podcasts A list of [PodcastInfo] objects to display. Each object contains information
 * about a single podcast, such as its title, image URL, and whether it is followed.
 * @param onTogglePodcastFollowed A callback function invoked when the user toggles the
 * follow/unfollow state of a podcast. It receives the [PodcastInfo] of the podcast whose
 * followed state changed.
 * @param navigateToPodcastDetails A callback function invoked when the user clicks on a podcast
 * item. It receives the [PodcastInfo] of the selected podcast and is intended to trigger navigation
 * to a detailed view of that podcast.
 * @param modifier [Modifier] for styling and layout customization of the entire row.
 */
@Composable
private fun CategoryPodcastRow(
    podcasts: List<PodcastInfo>,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = Keyline1,
            top = 8.dp,
            end = Keyline1,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(space = 24.dp)
    ) {
        items(
            items = podcasts,
            key = { it.uri }
        ) { podcast: PodcastInfo ->
            TopPodcastRowItem(
                podcastTitle = podcast.title,
                podcastImageUrl = podcast.imageUrl,
                isFollowed = podcast.isSubscribed ?: false,
                onToggleFollowClicked = { onTogglePodcastFollowed(podcast) },
                modifier = Modifier
                    .width(width = 128.dp)
                    .clickable {
                        navigateToPodcastDetails(podcast)
                    }
            )
        }
    }
}

/**
 * A composable function that displays a single row item for a top podcast.
 *
 * This composable shows a podcast's image, title, and a button to toggle following the podcast.
 *
 * @param podcastTitle The title of the podcast.
 * @param podcastImageUrl The URL of the podcast's image.
 * @param isFollowed Whether the podcast is currently followed by the user.
 * @param modifier Modifier to be applied to the root layout.
 * @param onToggleFollowClicked Callback to be invoked when the follow/unfollow button is clicked.
 */
@Composable
private fun TopPodcastRowItem(
    podcastTitle: String,
    podcastImageUrl: String,
    isFollowed: Boolean,
    modifier: Modifier = Modifier,
    onToggleFollowClicked: () -> Unit,
) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            PodcastImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = MaterialTheme.shapes.medium),
                podcastImageUrl = podcastImageUrl,
                contentDescription = podcastTitle
            )

            ToggleFollowPodcastIconButton(
                onClick = onToggleFollowClicked,
                isFollowed = isFollowed,
                modifier = Modifier.align(alignment = Alignment.BottomEnd)
            )
        }

        Text(
            text = podcastTitle,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

/**
 * A preview of the [EpisodeListItem] composable.
 */
@Preview
@Composable
fun PreviewEpisodeListItem() {
    JetcasterTheme {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            podcast = PreviewPodcasts[0],
            onClick = { },
            onQueueEpisode = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
