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

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.OffsetDateTime

/**
 * This is the data class that defines our `episodes` table in the database, it is created from the
 * RSS feed. The meanings of the anotations are:
 *  1. `@Entity`: This annotation marks the [Episode] class as a Room entity, which represents a
 *  table within the database.
 *  2. `tableName = "episodes"`: This parameter specifies the name of the table in the database.
 *  3. `indices = [Index("uri", unique = true)]`: This parameter is used to define indices for the
 *  table.
 *    - `Index("uri", unique = true)`: This creates a unique index on the "uri" column. This means
 *    that each "uri" value in the table must be unique
 *    - `Index("podcast_uri")`: This creates a non-unique index on the "podcast_uri" column. This
 *    will speed up queries that filter or sort by the podcast URI.
 *  4. `foreignKeys = [ ... ]`: This parameter is used to define foreign key constraints for the
 *  table. Foreign keys are used to establish relationships between tables.
 *  5. `ForeignKey( ... )`: This is a foreign key constraint.
 *    - `entity = Podcast::class`: This parameter specifies entity that this foreign key references
 *    the `podcasts` table.
 *    - `parentColumns = ["uri"]`: This specifies that the "uri" column in the `podcasts` table is
 *    the parent column (the column being referenced).
 *    - `childColumns = ["podcast_uri"]`: This specifies that the "podcast_uri" column in the
 *    `episodes` table is the child column (the column containing the foreign key).
 *    - `onUpdate = ForeignKey.CASCADE`: This defines the behavior when the parent column ("uri" in
 *    the `podcasts` table) is updated. CASCADE means that if the parent URI is updated, the
 *    corresponding "podcast_uri" values in the "episodes" table will also be updated to match.
 *    - `onDelete = ForeignKey.CASCADE`: This defines the behavior when a row in the parent table
 *    (`podcasts`) is deleted. CASCADE means that if a podcast is deleted, all episodes associated
 *    with that podcast (where "podcast_uri" matches the deleted podcast's URI) will also be deleted.
 */
@Entity(
    tableName = "episodes",
    indices = [
        Index("uri", unique = true),
        Index("podcast_uri")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Podcast::class,
            parentColumns = ["uri"],
            childColumns = ["podcast_uri"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Immutable
data class Episode(
    /**
     * This is the Primary Key for the table. It is the http URI of the episode, or some other way
     * to play the episode (since this app does not actually play episodes I have no idea how one
     * should use the non-http [String] to play an episode).
     */
    @PrimaryKey @ColumnInfo(name = "uri") val uri: String,
    /**
     * This is the URI of the podcast that the episode belongs to.
     */
    @ColumnInfo(name = "podcast_uri") val podcastUri: String,
    /**
     * This is the title of the episode.
     */
    @ColumnInfo(name = "title") val title: String,
    /**
     * This is the subtitle of the episode.
     */
    @ColumnInfo(name = "subtitle") val subtitle: String? = null,
    /**
     * This is the description of the episode.
     */
    @ColumnInfo(name = "summary") val summary: String? = null,
    /**
     * This is the author of the episode.
     */
    @ColumnInfo(name = "author") val author: String? = null,
    /**
     * This is the [OffsetDateTime] that the episode was published.
     */
    @ColumnInfo(name = "published") val published: OffsetDateTime,
    /**
     * This is the [Duration] of the episode.
     */
    @ColumnInfo(name = "duration") val duration: Duration? = null
)
