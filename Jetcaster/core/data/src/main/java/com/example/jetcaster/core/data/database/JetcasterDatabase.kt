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

package com.example.jetcaster.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jetcaster.core.data.database.dao.CategoriesDao
import com.example.jetcaster.core.data.database.dao.EpisodesDao
import com.example.jetcaster.core.data.database.dao.PodcastCategoryEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastFollowedEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastsDao
import com.example.jetcaster.core.data.database.dao.TransactionRunner
import com.example.jetcaster.core.data.database.dao.TransactionRunnerDao
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastCategoryEntry
import com.example.jetcaster.core.data.database.model.PodcastFollowedEntry

/**
 * The [RoomDatabase] we use in this app. The meaning of the annotations:
 *  1. `@Database`: This annotation tells Room that the class defines a database.
 *  2. `entities`: This parameter is an array of classes that represent the tables in your database.
 *  Each class listed here (e.g., Podcast::class, Episode::class) should be annotated with `@Entity`
 *  and define the structure of a table.
 *    - `Podcast::class`: Represents the table for podcast information.
 *    - `Episode::class`: Represents the table for individual podcast episodes.
 *    - `PodcastCategoryEntry::class`: a join table to manage the many-to-many relationship between
 *    podcasts and categories.
 *    - `Category::class`: Represents the table for podcast categories.
 *    - `PodcastFollowedEntry::class`: a table to track which podcasts a user is following.
 *  3. `version`: This integer represents the current version of your database schema. When you make
 *  changes to your entities (add columns, change data types, etc.), you must increment this version
 *  number. Room uses this to manage database migrations.
 *  4. `exportSchema`: This boolean flag determines whether Room should export the database schema
 *  to a JSON file.
 *    - `exportSchema = false`: In this case, the schema is not exported. This is fine for development
 *    or if you don't need to track schema changes outside of the database itself.
 *    - `exportSchema = true`: If set to `true`, you should also configure the room.schemaLocation in
 *    your build.gradle file to specify where the schema files should be saved. This is useful for
 *    tracking database schema changes over time and for debugging.
 *  5. `@TypeConverters Annotation`: This annotation tells Room to use the specified type converters
 *  for handling custom data types.
 *    - `DateTimeTypeConverters::class`: The class named [DateTimeTypeConverters] contains methods
 *    annotated with @TypeConverter. These methods are responsible for converting between your custom
 *    date/time types (e.g., LocalDateTime, Instant) and a type that Room understands (e.g., Long for
 *    timestamps).
 *  6. `abstract class JetcasterDatabase : RoomDatabase()`:  defines our database class.
 *    - `abstract class`: It's an abstract class because Room will generate the concrete
 *    implementation for you.
 *    - `JetcasterDatabase`: This is the name of your database class.
 *    - `: RoomDatabase()`: It inherits from [RoomDatabase], which is the base class for all Room
 *    databases.
 *  7. `abstract fun ...Dao(): ...Dao`: These are abstract methods that return Data Access Objects
 *  (DAOs). Each DAO (e.g., PodcastsDao, EpisodesDao) is an interface or abstract class that defines
 *  the methods for interacting with a specific table in your database. Room will generate the
 *  implementation for these methods, allowing you to easily perform database operations
 */
@Database(
    entities = [
        Podcast::class,
        Episode::class,
        PodcastCategoryEntry::class,
        Category::class,
        PodcastFollowedEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class JetcasterDatabase : RoomDatabase() {
    /**
     * Provides access to the "podcasts" table which is defined by the [Podcast] Entity.
     */
    abstract fun podcastsDao(): PodcastsDao

    /**
     * Provides access to the "episodes" table which is defined by the [Episode] Entity.
     */
    abstract fun episodesDao(): EpisodesDao

    /**
     * Provides access to the "categories" table which is defined by the [Category] Entity.
     */
    abstract fun categoriesDao(): CategoriesDao

    /**
     * Provides access to the "podcast_category_entries" table which is defined by the
     * [PodcastCategoryEntry] Entity.
     */
    abstract fun podcastCategoryEntryDao(): PodcastCategoryEntryDao

    /**
     * Provides access to our [TransactionRunner] which is used to run multiple `Room` operations
     * in a single atomic `@Transaction`.
     */
    abstract fun transactionRunnerDao(): TransactionRunnerDao

    /**
     * Provides access to the "podcast_followed_entries" table which is defined by the
     * [PodcastFollowedEntry] Entity.
     */
    abstract fun podcastFollowedEntryDao(): PodcastFollowedEntryDao
}
