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

package com.example.jetcaster.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastCategoryEntry
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

/**
 * `Room` DAO for [Episode] related operations.
 */
@Dao
abstract class EpisodesDao : BaseDao<Episode> {
    /**
     * This method returns a [Flow] of [Episode] from the `episodes` table whose [Episode.uri]
     * field is equal to our [String] parameter [uri].
     *
     * @param uri the [Episode.uri] to search for.
     * @return a [Flow] of [Episode] from the `episodes` table whose [Episode.uri] field is equal
     * to [uri].
     */
    @Query(
        """
        SELECT * FROM episodes WHERE uri = :uri
        """
    )
    abstract fun episode(uri: String): Flow<Episode>

    /**
     * This method returns a [Flow] of [EpisodeToPodcast] from the `episodes` and `podcasts` tables
     * whose [Episode.uri] field is equal to our [String] parameter [episodeUri]. The `@Transaction`
     * annotation marks a method in a Dao annotated class as a transaction method, ie. it will be
     * run as a single database transaction. The SQL statment means:
     *  - `SELECT episodes.*` this selects all of the [Episode] entries `FROM` the `episodes` table
     *  - `INNER JOIN` selects entries from both the `episodes` table and the `podcasts` table. `ON`
     *  specifies the join condition which is that the `podcast_uri` field of the `episodes` table
     *  is equal to the `uri` field of the `podcasts` table.
     *  - `WHERE` episodes.uri = :episodeUri: This filters the results further to only include only
     *  rows where the `uri` field of the `episodes` table is equal to our [String] parameter
     *  [episodeUri].
     *
     * @param episodeUri the [Episode.uri] to search for.
     * @return a [Flow] of [EpisodeToPodcast] that is constructed from the [Episode] whose
     * [Episode.uri] is equal to our [String] parameter [episodeUri] and the [Podcast] whose
     * [Podcast.uri] is equal to the [Episode.podcastUri] of that [Episode].
     */
    @Transaction
    @Query(
        """
        SELECT episodes.* FROM episodes
        INNER JOIN podcasts ON episodes.podcast_uri = podcasts.uri
        WHERE episodes.uri = :episodeUri
        """
    )
    abstract fun episodeAndPodcast(episodeUri: String): Flow<EpisodeToPodcast>

    /**
     * The query retrieves all information about episodes associated with a specific podcast, sorts
     * them from newest to oldest based on their publication date and time, and then limits the
     * number of results to a predefined value. The `@Transaction` annotation marks a method in a
     * Dao annotated class as a transaction method, ie. it will be run as a single database
     * transaction. The SQL statment means:
     *  - `SELECT * FROM episodes` this selects all of the [Episode] entries `FROM` the `episodes`
     *  table of the database
     *  - `WHERE podcast_uri = :podcastUri`: This is a filter condition. It selects only those rows
     *  ([Episode]'s) where the value in the `podcast_uri` column matches our [String] parameter
     *  [podcastUri].
     *  - `ORDER BY datetime(published) DESC`: This clause sorts the results in descending order
     *  based on the `DATETIME` of the [OffsetDateTime] `published` column.
     *  - `LIMIT :limit`: This clause limits the number of rows returned by the query to our [Int]
     *  parameter [limit].
     *
     * @param podcastUri the [Episode.podcastUri] to search for.
     * @param limit the maximum number of [Episode] entries to return.
     * @return a [Flow] of [List] of [EpisodeToPodcast] that is constructed from the [Episode]'s
     * whose `podcast_uri` column is our [String] parameter [podcastUri] sorted by the date in the
     * [Episode.published] column in descending order.
     */
    @Transaction
    @Query(
        """
        SELECT * FROM episodes WHERE podcast_uri = :podcastUri
        ORDER BY datetime(published) DESC
        LIMIT :limit
        """
    )
    abstract fun episodesForPodcastUri(
        podcastUri: String,
        limit: Int
    ): Flow<List<EpisodeToPodcast>>

    /**
     * This method finds episodes related to a specific category ([String] parameter [categoryId]),
     * orders them by publication date (newest first), and limits the number of results returned
     * to our [Int] parameter [limit]. The SQL statment means:
     *  - `SELECT episodes.* FROM episodes` this selects all of the [Episode] entries `FROM` the
     *  `episodes` table of the database.
     *  - `INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri`
     *  combines the `episodes` table with the `podcast_category_entries` table based `ON` the
     *  [Episode.podcastUri] being equal to the [PodcastCategoryEntry.podcastUri]
     *  - `WHERE category_id = :categoryId`: This is a filter condition. It selects only those rows
     *  whose `category_id` column matches our [String] parameter [categoryId].
     *  - `ORDER BY datetime(published) DESC`: This clause sorts the results in descending order
     *  based on the `DATETIME` of the [OffsetDateTime] `published` column.
     *  - `LIMIT :limit`: This clause limits the number of rows returned by the query to the value
     *  of our [Int] parameter [limit].
     *
     * @param categoryId the [Category.id] of the [Podcast]'s whose [Episode]'s we are to get.
     * @param limit the maximum number of rows we should return.
     * @return a [Flow] of [List] of [EpisodeToPodcast] constructed from the [Episode]'s whose
     * [Episode.podcastUri] is in the [PodcastCategoryEntry]'s whose [PodcastCategoryEntry.categoryId]
     * is our [Long] parameter [categoryId] sorted by the date in the [Episode.published] column
     * in descending order with the number of rows limited to our [Int] parameter [limit].
     */
    @Transaction
    @Query(
        """
        SELECT episodes.* FROM episodes
        INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri
        WHERE category_id = :categoryId
        ORDER BY datetime(published) DESC
        LIMIT :limit
        """
    )
    abstract fun episodesFromPodcastsInCategory(
        categoryId: Long,
        limit: Int
    ): Flow<List<EpisodeToPodcast>>

    /**
     *
     */
    @Query("SELECT COUNT(*) FROM episodes")
    abstract suspend fun count(): Int

    /**
     *
     */
    @Transaction
    @Query(
        """
        SELECT * FROM episodes WHERE podcast_uri IN (:podcastUris)
        ORDER BY datetime(published) DESC
        LIMIT :limit
        """
    )
    abstract fun episodesForPodcasts(
        podcastUris: List<String>,
        limit: Int
    ): Flow<List<EpisodeToPodcast>>
}
