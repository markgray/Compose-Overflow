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

package com.example.jetcaster.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * This class is designed to encapsulate information about a podcast along with some additional
 * metadata that might not be directly stored within the core Podcast entity. Specifically, it includes:
 *  1. `podcast`: An instance of the [Podcast] class itself, likely representing the core details of
 *  a podcast (e.g., title, description, feed URL).
 *  2. `lastEpisodeDate`: An [OffsetDateTime] representing the date of the most recent episode of the
 *  podcast. This is nullable, indicating that a podcast might not have any episodes yet.
 *  3. `isFollowed`: A boolean indicating whether the user is currently following this podcast.
 */
class PodcastWithExtraInfo {
    /**
     * The core information about the podcast. The `@Embedded` annotation from Room indicates that
     * the [podcast] property should be treated as if its fields were directly part of the
     * [PodcastWithExtraInfo] instance. This is a way to represent a one-to-one relationship between
     * [PodcastWithExtraInfo] and the [Podcast] from the "podcasts" table of the database.
     */
    @Embedded
    lateinit var podcast: Podcast

    /**
     * This is the date of the most recent episode of the podcast.
     */
    @ColumnInfo(name = "last_episode_date")
    var lastEpisodeDate: OffsetDateTime? = null

    /**
     * If `true`, the user is currently following this podcast.
     */
    @ColumnInfo(name = "is_followed")
    var isFollowed: Boolean = false

    /**
     * This returns the [Podcast] field [podcast] as the first element in a destructuring declaration.
     */
    operator fun component1(): Podcast = podcast

    /**
     * This returns the [OffsetDateTime] field [lastEpisodeDate] as the second element in a
     * destructuring declaration.
     */
    operator fun component2(): OffsetDateTime? = lastEpisodeDate

    /**
     * This returns the [Boolean] field [isFollowed] as the third element in a destructuring
     * declaration.
     */
    operator fun component3(): Boolean = isFollowed

    /**
     * This function overrides the default equals method to provide a custom equality check for
     * [PodcastWithExtraInfo] objects. Two PodcastWithExtraInfo objects are considered equal if and
     * only if their [podcast], [lastEpisodeDate], and [isFollowed] properties are all equal.
     * The `other === this` check is an optimization to quickly return `true` if the two references
     * point to the exact same object in memory.
     */
    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is PodcastWithExtraInfo -> {
            podcast == other.podcast &&
                lastEpisodeDate == other.lastEpisodeDate &&
                isFollowed == other.isFollowed
        }
        else -> false
    }

    /**
     * This function overrides the default hashCode method. It's crucial to override [hashCode]
     * whenever you override equals. The contract between equals and hashCode is that if two objects
     * are equal according to equals, they must have the same hash code. The Objects.hash() function
     * is a convenient way to generate a hash code based on multiple properties.
     */
    override fun hashCode(): Int = Objects.hash(podcast, lastEpisodeDate, isFollowed)
}
