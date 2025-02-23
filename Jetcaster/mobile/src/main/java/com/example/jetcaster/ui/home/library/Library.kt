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

package com.example.jetcaster.ui.home.library

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetcaster.R
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.LibraryInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.util.fullWidthItem

/**
 * Composes the UI for displaying a list of library items (episodes) in a [LazyVerticalGrid].
 *
 * This function displays a section header ("Latest Episodes") followed by a list of
 * [EpisodeListItem] components, each representing an episode from the provided [library].
 * It uses a [LazyVerticalGrid] layout for efficient rendering of a potentially large number
 * of items.
 *
 * @param library The [LibraryInfo] object containing the list of episodes and associated
 * podcast information to display.
 * @param navigateToPlayer A lambda function that is invoked when an episode is clicked.
 * It receives the [EpisodeInfo] of the clicked episode and should handle navigation to
 * the player screen.
 * @param onQueueEpisode A lambda function that is invoked when an episode should be added to the
 * queue. It receives the [PlayerEpisode] that will be queued.
 *
 * @see LazyGridScope
 * @see EpisodeListItem
 * @see LibraryInfo
 * @see EpisodeInfo
 * @see PlayerEpisode
 */
fun LazyGridScope.libraryItems(
    library: LibraryInfo,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit
) {
    fullWidthItem {
        Text(
            text = stringResource(id = R.string.latest_episodes),
            modifier = Modifier.padding(
                start = Keyline1,
                top = 16.dp,
            ),
            style = MaterialTheme.typography.headlineLarge,
        )
    }

    items(
        library,
        key = { it.episode.uri }
    ) { (episode: EpisodeInfo, podcast: PodcastInfo) ->
        EpisodeListItem(
            episode = episode,
            podcast = podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
