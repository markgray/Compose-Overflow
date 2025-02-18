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

package com.example.jetcaster.core.player.model

import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import java.time.Duration
import java.time.OffsetDateTime

/**
 * Episode data with necessary information to be used within a player.
 *
 * This data class encapsulates all the necessary information about a single
 * episode, including its metadata, source URI, and associated podcast details.
 *
 * @property uri The unique resource identifier (URI) of the episode's audio file.
 * @property title The title of the episode.
 * @property subTitle A short, descriptive subtitle for the episode.
 * @property published The date and time when the episode was published.
 * @property duration The duration of the episode's audio content. Can be null if unknown.
 * @property podcastName The name of the podcast to which this episode belongs.
 * @property author The author or creator of the episode.
 * @property summary A brief summary or description of the episode's content.
 * @property podcastImageUrl The URL of the image associated with the podcast.
 */
data class PlayerEpisode(
    val uri: String = "",
    val title: String = "",
    val subTitle: String = "",
    val published: OffsetDateTime = OffsetDateTime.MIN,
    val duration: Duration? = null,
    val podcastName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = "",
) {
    /**
     * Creates a [PlayerEpisode] instance by combining a [PodcastInfo] and an [EpisodeInfo].
     *
     * @param podcastInfo Information related to the podcast of the current episode.
     * @param episodeInfo Information related to the current episode.
     */
    constructor(podcastInfo: PodcastInfo, episodeInfo: EpisodeInfo) : this(
        title = episodeInfo.title,
        subTitle = episodeInfo.subTitle,
        published = episodeInfo.published,
        duration = episodeInfo.duration,
        podcastName = podcastInfo.title,
        author = episodeInfo.author,
        summary = episodeInfo.summary,
        podcastImageUrl = podcastInfo.imageUrl,
        uri = episodeInfo.uri
    )
}

/**
 * Converts an [EpisodeToPodcast] object to a [PlayerEpisode] object.
 *
 * This function maps the data from an [EpisodeToPodcast] instance, which represents a combination
 * of episode and podcast information fetched from a data source, into a [PlayerEpisode] instance,
 * suitable for playback or display in a player UI.
 *
 * @receiver The [EpisodeToPodcast] instance to convert.
 * @return A [PlayerEpisode] instance containing the relevant episode information.
 *
 * The mapping process involves:
 * - Directly copying the `uri`, `title`, `published`, and `duration` from the `episode` part.
 * - Handling optional fields:
 *   1. `subtitle`: If `episode.subtitle` is null, an empty string is used.
 *   2. `author`: If `episode.author` is null, `podcast.author` is used as a fallback. If both are
 *   `null` an empty string is used.
 *   3. `summary`: If `episode.summary` is null, an empty string is used.
 *   4. `podcastImageUrl`: If `podcast.imageUrl` is null, an empty string is used.
 * - Using the `podcast.title` as the `podcastName`.
 */
fun EpisodeToPodcast.toPlayerEpisode(): PlayerEpisode =
    PlayerEpisode(
        uri = episode.uri,
        title = episode.title,
        subTitle = episode.subtitle ?: "",
        published = episode.published,
        duration = episode.duration,
        podcastName = podcast.title,
        author = episode.author ?: podcast.author ?: "",
        summary = episode.summary ?: "",
        podcastImageUrl = podcast.imageUrl ?: "",
    )
