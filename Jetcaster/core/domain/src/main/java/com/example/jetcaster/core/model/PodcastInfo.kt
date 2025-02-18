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

import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of a [Podcast].
 *
 * @property uri The unique resource identifier (URI) of the podcast. This is typically a URL or a
 * similar identifier that can be used to access the podcast's feed or other resources. Defaults to
 * an empty string.
 * @property title The title of the podcast. Defaults to an empty string.
 * @property author The author or publisher of the podcast. Defaults to an empty string.
 * @property imageUrl The URL of the podcast's artwork or cover image. Defaults to an empty string.
 * @property description A brief description of the podcast's content. Defaults to an empty string.
 * @property isSubscribed Indicates whether the user is currently subscribed to this podcast.
 * Can be `null` if subscription status is unknown or not applicable.
 * @property lastEpisodeDate The date and time of the most recently published episode, if available.
 * Uses [OffsetDateTime] to include timezone information. Can be `null` if no episodes have been
 * published or if the date is unknown.
 */
data class PodcastInfo(
    val uri: String = "",
    val title: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val isSubscribed: Boolean? = null,
    val lastEpisodeDate: OffsetDateTime? = null,
)

/**
 * Converts a [Podcast] internal data model to an external [PodcastInfo] data model.
 *
 * This function takes a [Podcast] object, and transforms it into a [PodcastInfo] object.
 * The [PodcastInfo] is intended for use outside of the core data layer, such as
 * in the UI or when interacting with other modules.
 *
 * The conversion process includes:
 * - Mapping the `uri`, `title` directly.
 * - Providing a default empty string if `author`, `imageUrl`, or `description` are null.
 *
 * This ensures that the external model always contains valid string values, even if
 * the internal data model has optional (nullable) fields.
 *
 * @receiver The [Podcast] object to convert.
 * @return A [PodcastInfo] object representing the same podcast data in an external format.
 *
 * @throws NullPointerException if the `uri` or `title` in the [Podcast] is null.
 * These are assumed to be required and should be present.
 */
fun Podcast.asExternalModel(): PodcastInfo =
    PodcastInfo(
        uri = this.uri,
        title = this.title,
        author = this.author ?: "",
        imageUrl = this.imageUrl ?: "",
        description = this.description ?: "",
    )

/**
 * Converts a [PodcastWithExtraInfo] (internal model) to a [PodcastInfo] (external model).
 *
 * This function takes a [PodcastWithExtraInfo] object, which represents a podcast along with
 * additional information relevant to the user (e.g., subscription status, last episode date),
 * and transforms it into a [PodcastInfo] object. The [PodcastInfo] is suitable for
 * presentation to the user in the UI or for use in external systems.
 *
 * The transformation includes:
 * - Converting the core [Podcast] data to its external representation using [Podcast.asExternalModel].
 * - Setting the subscription status based on the [PodcastWithExtraInfo.isFollowed] flag in
 * [PodcastWithExtraInfo].
 * - Including the [PodcastWithExtraInfo.lastEpisodeDate] from the [PodcastWithExtraInfo]
 *
 * @receiver The [PodcastWithExtraInfo] instance to be converted.
 * @return A [PodcastInfo] instance representing the external model of the podcast.
 */
fun PodcastWithExtraInfo.asExternalModel(): PodcastInfo =
    this.podcast.asExternalModel().copy(
        isSubscribed = isFollowed,
        lastEpisodeDate = lastEpisodeDate,
    )
