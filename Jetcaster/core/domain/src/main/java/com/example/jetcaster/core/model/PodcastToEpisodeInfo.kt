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

package com.example.jetcaster.core.model

import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast

/**
 * Data class representing the relationship between an episode and its corresponding podcast.
 *
 * This class encapsulates the [EpisodeInfo] and [PodcastInfo] related to a single episode.
 * It provides a convenient way to access both the episode's details and the details of the podcast
 * to which the episode belongs.
 *
 * @property episode The [EpisodeInfo] object containing details about the episode.
 * @property podcast The [PodcastInfo] object containing details about the podcast to which the
 * episode belongs.
 */
data class PodcastToEpisodeInfo(
    val episode: EpisodeInfo,
    val podcast: PodcastInfo,
)

/**
 * Converts an [EpisodeToPodcast] object to a [PodcastToEpisodeInfo] object.
 *
 * This function takes an [EpisodeToPodcast] which contains internal models for an [Episode] and a
 * [Podcast], and transforms it into a [PodcastToEpisodeInfo]. This output contains external models
 * of the same [Episode] and [Podcast].
 *
 * The conversion process involves mapping the internal [Episode] and [Podcast] models to their
 * respective external representations ([EpisodeInfo] and [PodcastInfo]) using the `asExternalModel()`
 * functions from the [EpisodeInfo] and [PodcastInfo] data classes.
 *
 * @receiver The [EpisodeToPodcast] instance to be converted.
 * @return A [PodcastToEpisodeInfo] object containing the external representations of the [Episode]
 * and [Podcast] in the [EpisodeToPodcast].
 */
fun EpisodeToPodcast.asPodcastToEpisodeInfo(): PodcastToEpisodeInfo =
    PodcastToEpisodeInfo(
        episode = episode.asExternalModel(),
        podcast = podcast.asExternalModel(),
    )
