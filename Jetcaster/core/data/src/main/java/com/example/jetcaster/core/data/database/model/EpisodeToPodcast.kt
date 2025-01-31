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

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.util.Objects

/**
 * This class is used by several methods to return an [Episode] and the [Podcast]`s that are related
 * to it. In actual use the [Episode] probably only has one [Podcast] related to it, the one that
 * broadcasts the [Episode], but there must of been some reason to allow for more than one [Podcast]
 * to be related to the same [Episode].
 */
class EpisodeToPodcast {
    /**
     * This is the [Episode] being returned.
     *
     * `@Embedded`: We're using `@Embedded` to include the Episode entity directly within the
     * [EpisodeToPodcast] class. This is a good approach if you want to treat [Episode]'s fields
     * as if they were part of [EpisodeToPodcast].
     */
    @Embedded
    lateinit var episode: Episode

    /**
     * This is the [List] of [Podcast] being returned.
     *
     * `@Relation`: This is the core of your relationship definition.
     *  - `parentColumn = "podcast_uri"`: This indicates that the `podcast_uri` column in the
     *  [Episode] entity (which is embedded) is the foreign key.
     *  - `entityColumn = "uri"`: This indicates that the uri column in the [Podcast] entity is the
     *  primary key that podcast_uri references.
     *  - `_podcasts: List<Podcast>`: This is the [List] of [Podcast] entities that are related to
     *  the [Episode].
     */
    @Suppress("PropertyName")
    @Relation(parentColumn = "podcast_uri", entityColumn = "uri")
    lateinit var _podcasts: List<Podcast>

    /**
     * This is a convenience property that returns the first [Podcast] in the [_podcasts] list.
     * This assumes there's always at least one [Podcast] related to an [Episode].
     *
     * `@get:Ignore`: We're using @Ignore to prevent Room from treating [podcast] as a database
     * column. This is correct since it's a derived property.
     *  - `podcast: Podcast`: This is a convenience property that returns the first [Podcast] in the
     *  `_podcasts` list. This assumes there's always at least one [Podcast] related to an [Episode].
     */
    @get:Ignore
    val podcast: Podcast
        get() = _podcasts[0]

    /**
     * Allow consumers to destructure this class, returns the [episode] as the first element.
     */
    operator fun component1(): Episode = episode
    /**
     * Allow consumers to destructure this class, returns the [podcast] as the second element.
     */
    operator fun component2(): Podcast = podcast

    /**
     * Allow consumers to use == to compare instances.
     */
    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is EpisodeToPodcast -> episode == other.episode && _podcasts == other._podcasts
        else -> false
    }

    /**
     * Allow consumers to use the hashcode method.
     */
    override fun hashCode(): Int = Objects.hash(episode, _podcasts)
}
