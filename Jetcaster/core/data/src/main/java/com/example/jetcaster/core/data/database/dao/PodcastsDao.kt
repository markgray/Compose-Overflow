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
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import kotlinx.coroutines.flow.Flow

/**
 * `Room` DAO for [Podcast] related operations.
 */
@Dao
abstract class PodcastsDao : BaseDao<Podcast> {
    /**
     * This method returns a [Flow] of [Podcast] from the `podcasts` table whose [Podcast.uri] is
     * equal to our [String] parameter [uri].
     *
     * @param uri the uri of the podcast to return.
     * @return a [Flow] of [Podcast] from the `podcasts` table whose [Podcast.uri] is equal to our
     * [String] parameter [uri].
     */
    @Query("SELECT * FROM podcasts WHERE uri = :uri")
    abstract fun podcastWithUri(uri: String): Flow<Podcast>

    /**
     * This method returns a [Flow] of [PodcastWithExtraInfo] from the `podcasts` table whose
     * [Podcast.uri] is equal to our [String] parameter [podcastUri]. The meaning of the SQL is:
     *  1. SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
     *    - `SELECT podcasts.*` this selects all or the [Podcast] entries `FROM` the `podcasts` table
     *    - `last_episode_date`: This is an alias for the maximum (most recent) published date of an
     *    episode, which will be calculated in a subquery.
     *    - `followed_entries.podcast_uri IS NOT NULL) AS is_followed`: This is a conditional expression
     *    that checks if a matching entry exists in the podcast_followed_entries table. If a matching
     *    podcast_uri is found in podcast_followed_entries, it means the podcast is followed, and
     *    is_followed will be TRUE. If no match is found, followed_entries.podcast_uri will be NULL,
     *    and is_followed will be FALSE.
     *  2. `FROM podcasts`: This specifies that the primary table for the query is podcasts.
     *  3. `INNER JOIN (...) episodes ON podcasts.uri = episodes.podcast_uri`: This joins the
     *  podcasts table with a subquery aliased as episodes. The Subquery:
     *    - `SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri`:
     *    This subquery finds the most recent episode for each podcast. `MAX(published)`: Finds the
     *    maximum (most recent) published date within each group. `GROUP BY podcast_uri`: Groups the
     *    episodes by their podcast_uri, so MAX(published) is calculated separately for each podcast.
     *    - `INNER JOIN`: This ensures that only podcasts that have at least one episode in the
     *    episodes table are included in the results.
     *    - `ON podcasts.uri = episodes.podcast_uri`: This is the join condition, linking the podcasts
     *    table to the subquery results based on matching podcast_uri values.
     *  4. `LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = podcasts.uri`:
     *  This performs a LEFT JOIN with the podcast_followed_entries table.
     *    - `LEFT JOIN`: This ensures that all podcasts from the podcasts table are included in the
     *    results, even if they don't have a matching entry in podcast_followed_entries.
     *    - `ON followed_entries.podcast_uri = podcasts.uri`: This is the join condition, linking the
     *    podcasts table to podcast_followed_entries based on matching podcast_uri values.
     *  5. `WHERE podcasts.uri = :podcastUri`: This filters the results to include only the podcast
     *  with our [String] parameter [podcastUri].
     *  6. `ORDER BY datetime(last_episode_date) DESC`: This sorts the results in descending order
     *  based on the `last_episode_date`, meaning the most recently updated podcasts will appear
     *  first.
     *
     * @param podcastUri the uri of the podcast to create an [PodcastWithExtraInfo] from.
     * @return a [Flow] of [PodcastWithExtraInfo] constructed to describe the podcast whose
     * [Podcast.uri] is equal to our [String] parameter [podcastUri].
     */
    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = podcasts.uri
        WHERE podcasts.uri = :podcastUri
        ORDER BY datetime(last_episode_date) DESC
        """
    )
    abstract fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo>

    /**
     * This method returns a [Flow] of [PodcastWithExtraInfo] from the `podcasts` table sorted by
     * the most recent episode's [Episode.published] date.
     */
    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun podcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     *
     */
    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT episodes.podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri
            WHERE category_id = :categoryId
            GROUP BY episodes.podcast_uri
        ) inner_query ON podcasts.uri = inner_query.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = inner_query.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun podcastsInCategorySortedByLastEpisode(
        categoryId: Long,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     *
     */
    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun followedPodcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     *
     */
    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri 
        WHERE podcasts.title LIKE '%' || :keyword || '%' 
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun searchPodcastByTitle(keyword: String, limit: Int): Flow<List<PodcastWithExtraInfo>>

    /**
     *
     */
    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT episodes.podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri
            WHERE category_id IN (:categoryIdList)
            GROUP BY episodes.podcast_uri
        ) inner_query ON podcasts.uri = inner_query.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = inner_query.podcast_uri
        WHERE podcasts.title LIKE '%' || :keyword || '%' 
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun searchPodcastByTitleAndCategory(
        keyword: String,
        categoryIdList: List<Long>,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     *
     */
    @Query("SELECT COUNT(*) FROM podcasts")
    abstract suspend fun count(): Int
}
