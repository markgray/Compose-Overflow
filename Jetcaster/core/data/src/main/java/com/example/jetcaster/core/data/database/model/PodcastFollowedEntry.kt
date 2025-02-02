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

/**
 * This class represents a podcast that the user is following. It is used in the database as an
 * an entry in the "podcast_followed_entries" table.
 *  1. `@Entity`: This annotation marks the [PodcastFollowedEntry] class as a Room entity, meaning
 *  it represents a table in your database.
 *  2. `tableName = "podcast_followed_entries"`: This option specifies the name of the table in the
 *  database. If omitted, Room will use the class name as the table name.
 *  3. `foreignKeys`: Defines foreign key constraints for this table.
 *  4. `ForeignKey`: This specifies a single foreign key relationship.
 *    - `entity = Podcast::class`: The type of the referenced entity ([Podcast]).
 *    - `parentColumns = ["uri"]`: The column in [Podcast] table that is referenced (the primary key
 *    or a unique column).
 *    - `childColumns = ["podcast_uri"]`: The column in the [PodcastFollowedEntry] table that holds
 *    the foreign key value.
 *    - `onUpdate = ForeignKey.CASCADE`: This specifies that if the uri in the `Podcast` table is
 *    updated, the corresponding `podcast_uri` in [PodcastFollowedEntry] should also be updated.
 *    - `onDelete = ForeignKey.CASCADE`: Specifies that if a row in the `Podcast` table is deleted,
 *    the corresponding rows in [PodcastFollowedEntry] should also be deleted.
 *  5. `indices`: Defines indexes for the table.
 *    - `Index("podcast_uri")`: Creates an index on the `podcast_uri` column. This can speed up
 *    queries that filter or sort by this column.
 *  6. `@Immutable`: This annotation from Compose indicates that instances of this class are immutable.
 *  7. `data class PodcastFollowedEntry`: Defines the data class that represents a row in the
 *  `podcast_followed_entries` table.
 */
@Entity(
    tableName = "podcast_followed_entries",
    foreignKeys = [
        ForeignKey(
            entity = Podcast::class,
            parentColumns = ["uri"],
            childColumns = ["podcast_uri"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("podcast_uri")
    ]
)
@Immutable
data class PodcastFollowedEntry(
    /**
     * This is the primary key for the "podcast_followed_entries" table.
     *  1. `@PrimaryKey(autoGenerate = true)`: This annotation marks the "id" field as the primary key
     *    - `autoGenerate = true`: Tells Room to automatically generate unique IDs for new rows.
     *  2. `@ColumnInfo(name = "id")`: Specifies the column name in the database.
     *  3. `val id: Long = 0`: The property itself, a Long that defaults to 0.
     */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    /**
     * This is the uri of the podcast that is being followed.
     *  1. `@ColumnInfo(name = "podcast_uri")`: Specifies the column name in the database.
     *  2. `val podcastUri: String`: The property itself, a String that holds the URI of the
     *  followed podcast.
     */
    @ColumnInfo(name = "podcast_uri") val podcastUri: String
)
