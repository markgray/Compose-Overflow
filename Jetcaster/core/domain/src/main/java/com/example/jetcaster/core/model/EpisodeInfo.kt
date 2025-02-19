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
import java.time.Duration
import java.time.OffsetDateTime

/**
 * External data layer representation of a single episode in a podcast or similar media stream.
 *
 * @property uri The unique resource identifier (URI) of the episode. This could be a URL to the
 * episode's media file or a unique identifier within a podcast service. Defaults to an empty string.
 * @property title The title of the episode. Defaults to an empty string.
 * @property subTitle An optional subtitle for the episode, providing further context to the title.
 * Defaults to an empty string.
 * @property summary A short description or summary of the episode's content. Defaults to an
 * empty string.
 * @property author The author or creator of the episode. Defaults to an empty string.
 * @property published The date and time when the episode was published, represented as an
 * [OffsetDateTime]. Defaults to [OffsetDateTime.MIN] if the published date is unknown.
 * @property duration The duration of the episode, represented as a [Duration]. Can be `null` if the
 * duration is unknown or unavailable.
 */
data class EpisodeInfo(
    val uri: String = "",
    val title: String = "",
    val subTitle: String = "",
    val summary: String = "",
    val author: String = "",
    val published: OffsetDateTime = OffsetDateTime.MIN,
    val duration: Duration? = null,
)

/**
 * Converts an [Episode] domain model to an [EpisodeInfo] external model.
 *
 * This function maps the properties of an [Episode] object to the corresponding
 * properties of an [EpisodeInfo] object, suitable for use outside the domain layer.
 * It handles potential null values in the source object, providing default
 * empty strings for [Episode.subtitle], [Episode.summary], and [Episode.author] if they are `null`.
 *
 * @return An [EpisodeInfo] object containing the mapped data from the [Episode].
 */
fun Episode.asExternalModel(): EpisodeInfo =
    EpisodeInfo(
        uri = uri,
        title = title,
        subTitle = subtitle ?: "",
        summary = summary ?: "",
        author = author ?: "",
        published = published,
        duration = duration,
    )
