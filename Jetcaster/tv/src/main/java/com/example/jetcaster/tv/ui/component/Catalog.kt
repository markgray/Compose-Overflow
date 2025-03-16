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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.Typography
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * Displays a catalog of podcasts and the latest episodes.
 *
 * This composable function presents a vertically scrollable list (using [LazyColumn])
 * consisting of two main sections:
 * 1. A list of podcasts, obtained from the [podcastList] parameter.
 * 2. A list of latest episodes, obtained from the [latestEpisodeList] parameter.
 *
 * Optionally, a custom header can be added at the top using the [header] parameter.
 *
 * @param podcastList The list of podcasts to display in the podcast section.
 * @param latestEpisodeList The list of latest episodes to display in the latest episode section.
 * @param onPodcastSelected A callback function that is invoked when a podcast is selected.
 * It receives the [PodcastInfo] of the selected podcast.
 * @param onEpisodeSelected A callback function that is invoked when an episode is selected.
 * It receives the [PlayerEpisode] of the selected episode.
 * @param modifier Modifier for styling and layout customization of the catalog.
 * @param state The state object to be used to control or observe the list's scrolling state.
 * @param header An optional composable function to display as a header above the sections.
 */
@Composable
internal fun Catalog(
    podcastList: PodcastList,
    latestEpisodeList: EpisodeList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    onEpisodeSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    header: (@Composable () -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = JetcasterAppDefaults.overScanMargin.catalog.intoPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.section),
        state = state,
    ) {
        if (header != null) {
            item { header() }
        }
        item {
            PodcastSection(
                podcastList = podcastList,
                onPodcastSelected = onPodcastSelected,
                title = stringResource(id = R.string.label_podcast)
            )
        }
        item {
            LatestEpisodeSection(
                episodeList = latestEpisodeList,
                onEpisodeSelected = onEpisodeSelected,
                title = stringResource(id = R.string.label_latest_episode)
            )
        }
    }
}

/**
 * Displays a section of podcasts, optionally with a title.
 *
 * This composable renders a horizontal row of podcast items within a section.
 * It handles displaying the title (if provided) and presenting the podcasts in a scrollable row.
 *
 * @param podcastList The list of podcasts to display.
 * @param onPodcastSelected A callback function that is invoked when a podcast is selected.
 * It receives the selected [PodcastInfo] as a parameter.
 * @param modifier Modifiers to be applied to the section container.
 * @param title An optional title for the podcast section. If null, no title is displayed.
 */
@Composable
private fun PodcastSection(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Section(
        title = title,
        modifier = modifier
    ) {
        PodcastRow(
            podcastList = podcastList,
            onPodcastSelected = onPodcastSelected,
        )
    }
}

/**
 * Displays a section dedicated to showcasing the latest episodes.
 *
 * This composable presents a row of the most recent episodes from the provided [episodeList].
 * It also allows for an optional title to be displayed above the episode row.
 *
 * @param episodeList The list of episodes to display. This should be an [EpisodeList] containing
 * [PlayerEpisode] objects.
 * @param onEpisodeSelected A callback function triggered when an episode is selected. It receives
 * the selected [PlayerEpisode] as a parameter.
 * @param modifier Modifier to be applied to the root layout of the section.
 * @param title Optional title to display above the episode row. If null, no title is displayed.
 *
 * @see Section
 * @see EpisodeRow
 * @see PlayerEpisode
 * @see EpisodeList
 */
@Composable
private fun LatestEpisodeSection(
    episodeList: EpisodeList,
    onEpisodeSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Section(
        modifier = modifier,
        title = title
    ) {
        EpisodeRow(
            playerEpisodeList = episodeList,
            onSelected = onEpisodeSelected,
        )
    }
}

/**
 * A composable function that renders a section with an optional title and content.
 *
 * This composable provides a structured way to group related UI elements together. It can
 * optionally display a title above the content, making it easy to create visually distinct
 * sections within a larger layout.
 *
 * @param modifier The [Modifier] to be applied to the section's root [Column].
 * @param title The optional title of the section. If null, no title is displayed.
 * @param style The [TextStyle] to be used for the section's title. Defaults to the
 * [Typography.headlineMedium] of our custom [MaterialTheme.typography].
 * @param content The composable content to be displayed within the section.
 */
@Composable
private fun Section(
    modifier: Modifier = Modifier,
    title: String? = null,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        if (title != null) {
            Text(
                text = title,
                style = style,
                modifier = Modifier.padding(paddingValues = JetcasterAppDefaults.padding.sectionTitle)
            )
        }
        content()
    }
}

/**
 * Displays a horizontally scrollable row of [PodcastCard]s.
 *
 * This composable renders a list of podcasts in a row, allowing the user to scroll
 * through them horizontally. It also manages focus for accessibility and keyboard
 * navigation, specifically handling the transition of focus when entering and
 * exiting the row.
 *
 * @param podcastList The list of [PodcastInfo] to display in the row.
 * @param onPodcastSelected Callback invoked when a podcast card is clicked. It provides
 * the selected [PodcastInfo] as a parameter.
 * @param modifier Modifier to be applied to the root [LazyRow].
 * @param contentPadding Padding to be applied to the content of the row. Defaults to
 * `JetcasterAppDefaults.padding.podcastRowContentPadding`.
 * @param horizontalArrangement The horizontal arrangement of the podcast cards within the row.
 * Defaults to [Arrangement.spacedBy] with spacing defined by `JetcasterAppDefaults.gap.podcastRow`.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PodcastRow(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = JetcasterAppDefaults.padding.podcastRowContentPadding,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(space = JetcasterAppDefaults.gap.podcastRow),
) {
    val (focusRequester: FocusRequester, firstItem: FocusRequester) =
        remember(key1 = podcastList) { FocusRequester.createRefs() }

    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier
            .focusRequester(focusRequester = focusRequester)
            .focusProperties {
                exit = {
                    focusRequester.saveFocusedChild()
                    FocusRequester.Default
                }
                enter = {
                    if (focusRequester.restoreFocusedChild()) {
                        FocusRequester.Cancel
                    } else {
                        firstItem
                    }
                }
            },
    ) {
        itemsIndexed(items = podcastList) { index: Int, podcastInfo: PodcastInfo ->
            val cardModifier = if (index == 0) {
                Modifier.focusRequester(focusRequester = firstItem)
            } else {
                Modifier
            }
            PodcastCard(
                podcastInfo = podcastInfo,
                onClick = { onPodcastSelected(podcastInfo) },
                modifier = cardModifier.width(width = JetcasterAppDefaults.cardWidth.medium)
            )
        }
    }
}
