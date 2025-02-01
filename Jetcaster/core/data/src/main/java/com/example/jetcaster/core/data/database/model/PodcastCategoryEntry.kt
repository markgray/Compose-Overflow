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
 * This is the data class that defines the `podcast_category_entries` table in the database.
 *  1. `@Entity(...)`: This is a Room annotation that tells Room that this class represents a
 *  table in our database.
 *  2. `tableName = "podcast_category_entries"`: This specifies the name of the table that Room will
 *  create in the database to store instances of this class. It's good practice to use plural names
 *  for tables.
 *  3. `foreignKeys`: This array defines the foreign key relationships with other tables.
 *  4. `ForeignKey(entity = Category::class, ...)`: This defines a foreign key relationship with the
 *  `Category` table.
 *    - `parentColumns = ["id"]`: The primary key column in the Category table.
 *    - `childColumns = ["category_id"]`: The column in the PodcastCategoryEntry table that references
 *    the Category's id.
 *    - `onUpdate = ForeignKey.CASCADE`: If the id in the Category table is updated, the corresponding
 *    `category_id` in this table will also be updated.
 *    - `onDelete = ForeignKey.CASCADE`: If a row in the Category table is deleted, all corresponding
 *    rows in this table will also be deleted.
 *  5. `ForeignKey(entity = Podcast::class, ...)`: This defines a foreign key relationship with the
 *  Podcast table.
 *    - `parentColumns = ["uri"]`: The primary key column in the Podcast table.
 *    - `childColumns = ["podcast_uri"]`: The column in the PodcastCategoryEntry table that references
 *    the Podcast's uri.
 *    - `onUpdate = ForeignKey.CASCADE`: If the uri in the Podcast table is updated, the corresponding
 *    podcast_uri in this table will also be updated.
 *    - `onDelete = ForeignKey.CASCADE`: If a row in the Podcast table is deleted, all corresponding
 *    rows in this table will also be deleted.
 *  6. `indices`: This array defines indexes for the table. Indexes speed up data retrieval.
 *    - `Index("podcast_uri", "category_id", unique = true)`: This creates a unique index on the
 *    combination of podcast_uri and category_id. This ensures that there can only be one entry for
 *    a specific podcast-category pair. This is a good way to enforce that a podcast can only belong
 *    to a category once.
 *    - `Index("category_id")`: This creates an index on the category_id column, which will speed up
 *    queries that filter by category.
 *    - `Index("podcast_uri")`: This creates an index on the podcast_uri column, which will speed up
 *    queries that filter by podcast.
 *  7. `@Immutable`: This annotation indicates that the PodcastCategoryEntry class is immutable. This
 *  is a good practice for data classes, especially when used with Room. It means that once an instance
 *  is created, its properties cannot be changed.
 *  8. `data class PodcastCategoryEntry(...)`: This defines the data class itself. Data classes in
 *  Kotlin automatically generate useful methods like equals(), hashCode(), toString(), and copy().
 */
@Entity(
    tableName = "podcast_category_entries",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Podcast::class,
            parentColumns = ["uri"],
            childColumns = ["podcast_uri"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("podcast_uri", "category_id", unique = true),
        Index("category_id"),
        Index("podcast_uri")
    ]
)
@Immutable
data class PodcastCategoryEntry(
    /**
     * This is the primary key of the table.
     *  1. `@PrimaryKey(autoGenerate = true)`: This annotation indicates that the id property is the
     *  primary key for the table.
     *    - `autoGenerate = true`: This tells Room to automatically generate unique IDs for each new
     *    entry.
     *  2. `@ColumnInfo(name = "id")`: This specifies the column name in the database.
     *  3. `val id: Long = 0`: This is the primary key property, a Long type, and it defaults to 0
     *  if not explicitly set.
     */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    /**
     * This is the column in the database that stores the uri of the associated podcast.
     *  1. `@ColumnInfo(name = "podcast_uri")`: This specifies the column name in the database.
     *  2. `val podcastUri: String`: This is the property that stores the uri of the associated podcast.
     */
    @ColumnInfo(name = "podcast_uri") val podcastUri: String,
    /**
     * This is the column in the database that stores the id of the associated category.
     *  1. `@ColumnInfo(name = "category_id")`: This specifies the column name in the database.
     *  2. `val categoryId: Long`: This is the property that stores the id of the associated category.
     */
    @ColumnInfo(name = "category_id") val categoryId: Long
)
