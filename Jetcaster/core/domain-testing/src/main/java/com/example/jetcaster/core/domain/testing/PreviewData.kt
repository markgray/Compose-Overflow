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

package com.example.jetcaster.core.domain.testing

import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.PodcastToEpisodeInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * A list of predefined categories used for previewing or testing purposes.
 *
 * This list contains a set of [CategoryInfo] objects representing different categories.
 * It's primarily used in previews, mock data, or testing scenarios where a predefined set of
 * categories is needed.  These are not necessarily the exhaustive set of categories in the system,
 * just a subset for common use cases.
 */
val PreviewCategories: List<CategoryInfo> = listOf(
    CategoryInfo(id = 1, name = "Crime"),
    CategoryInfo(id = 2, name = "News"),
    CategoryInfo(id = 3, name = "Comedy")
)

/**
 * A list of sample [PodcastInfo] objects used for previewing UI elements.
 * These podcasts are not real and are intended for design and testing purposes only.
 * They provide a quick way to visualize how a list of podcasts might appear in the application.
 */
val PreviewPodcasts: List<PodcastInfo> = listOf(
    PodcastInfo(
        uri = "fakeUri://podcast/1",
        title = "Android Developers Backstage",
        author = "Android Developers",
        isSubscribed = true,
        lastEpisodeDate = OffsetDateTime.now()
    ),
    PodcastInfo(
        uri = "fakeUri://podcast/2",
        title = "Google Developers podcast",
        author = "Google Developers",
        lastEpisodeDate = OffsetDateTime.now()
    )
)

/**
 * A list of EpisodeInfo objects used for previewing episode content.
 *
 * This list contains example [EpisodeInfo] instances that can be used
 * to display placeholder or sample episode data within the UI, especially
 * during development or in preview contexts (like in Android Studio's preview pane).
 *
 * It includes a single episode for now, but more can be added to provide
 * a richer preview experience. The data here should be representative
 * of the actual episode data that will be loaded at runtime.
 */
val PreviewEpisodes: List<EpisodeInfo> = listOf(
    EpisodeInfo(
        uri = "fakeUri://episode/1",
        title = "Episode 140: Lorem ipsum dolor",
        summary = "In this episode, Romain, Chet and Tor talked with Mady Melor and Artur " +
            "Tsurkan from the System UI team about... Bubbles!",
        published = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        )
    )
)

/**
 * A list of [PlayerEpisode] objects used for previewing the player UI.
 *
 * This list contains sample data representing a single episode from a single podcast.
 * It's primarily intended for use in previews and design tools to visualize how
 * the player might look with actual episode data.
 *
 * The first element in this list combines the first podcast ([PreviewPodcasts[0]])
 * with the first episode ([PreviewEpisodes[0]]) to create a realistic example.
 */
val PreviewPlayerEpisodes: List<PlayerEpisode> = listOf(
    PlayerEpisode(
        PreviewPodcasts[0],
        PreviewEpisodes[0]
    )
)

/**
 * A list of [PodcastToEpisodeInfo] used for previewing purposes. It contains a single entry,
 * linking the first podcast in [PreviewPodcasts] to the first episode in [PreviewEpisodes].
 * This is helpful for displaying sample data in UI previews or during development.
 */
val PreviewPodcastEpisodes: List<PodcastToEpisodeInfo> = listOf(
    PodcastToEpisodeInfo(
        podcast = PreviewPodcasts[0],
        episode = PreviewEpisodes[0],
    )
)
