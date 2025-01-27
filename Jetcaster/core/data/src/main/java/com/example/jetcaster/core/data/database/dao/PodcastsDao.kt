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
     *    - `followed_entries.podcast_uri IS NOT NULL) AS is_followed`: This is a conditional
     *    expression that checks if a matching entry exists in the podcast_followed_entries table.
     *    If a matching podcast_uri is found in podcast_followed_entries, it means the podcast is
     *    followed, and is_followed will be TRUE. If no match is found, followed_entries.podcast_uri
     *    will be NULL, and is_followed will be FALSE.
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
     * This method returns a [Flow] of [PodcastWithExtraInfo] created from the `podcasts` table
     * sorted by the most recent episode of each podcast. The SQL statment means:
     *  1. `SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed`:
     *    - `podcasts.*`: This selects all columns from the podcasts table.
     *    - `last_episode_date`: This is an alias for the maximum (most recent) published date of
     *    episodes for each podcast, which will be calculated in a subquery.
     *    - `(followed_entries.podcast_uri IS NOT NULL) AS is_followed`: This is a conditional
     *    expression. It checks if a `podcast_uri` exists in the `podcast_followed_entries` table.
     *    If it exists (meaning the podcast is followed), it evaluates to `true`; otherwise, it's
     *    `false`. This result is aliased as is_followed.
     *  2. `FROM podcasts`: This specifies that the primary table for the query is podcasts.
     *  3. `INNER JOIN (...) episodes ON podcasts.uri = episodes.podcast_uri`: This is an INNER JOIN
     *  operation, meaning it will only include rows where a match is found in both tables. The
     *  subquery `(SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY
     *  podcast_uri)` is used to find the most recent episode for each podcast.
     *    - `SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri`:
     *    This selects the `podcast_uri` and the maximum published date (aliased as `last_episode_date`).
     *    - `FROM episodes`: This specifies that the subquery is operating on the episodes table.
     *    `GROUP BY podcast_uri`: This groups the episodes by their `podcast_uri`, so `MAX(published)`
     *    will find the latest date for each podcast.
     *    - `ON podcasts.uri = episodes.podcast_uri`: This is the join condition. It links the `podcasts`
     *    table to the results of the subquery based on matching `podcast_uri` values.
     *  4. `LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri`:
     *    - This is a `LEFT JOIN`, which means it will include all rows from the left table (podcasts
     *    joined with the subquery results) and matching rows from the right table
     *    (`podcast_followed_entries`). If there's no match in `podcast_followed_entries`, the columns
     *    from that table will be NULL.
     *    - `podcast_followed_entries AS followed_entries`: This specifies the table to join with and
     *    gives it the alias `followed_entries`.
     *    - `ON followed_entries.podcast_uri = episodes.podcast_uri`: This is the join condition,
     *    linking based on matching `podcast_uri` values.
     *  5. `ORDER BY datetime(last_episode_date) DESC`: This sorts the results in descending order
     *  based on the last_episode_date. datetime() is used to ensure proper date/time comparison.
     *  6. `LIMIT :limit`: This limits the number of rows returned to the value specified by our [Int]
     *  parameter [limit].
     *
     * @param limit the maximum number of [PodcastWithExtraInfo] to return.
     * @return a [Flow] of [List] of [PodcastWithExtraInfo] constructed from the `podcasts` table
     * sorted by the latest episode of each podcast and the number of rows returned limited to our
     * [Int] parameter [limit].
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
     * This method returns a [Flow] of [PodcastWithExtraInfo] created from the podcasts in the
     * `podcasts` table which belong to the category whose [Category.id] is equal to our [Long]
     * [categoryId] sorted by the podcasts' most recent episode date. The SQL statment means:
     *  1. `SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed`
     *    - `podcasts.*`: This selects all columns from the podcasts table.
     *    - `last_episode_date`: This is an alias for the most recent episode's published date for
     *    each podcast. It's calculated in a subquery.
     *    - `(followed_entries.podcast_uri IS NOT NULL) AS is_followed`: This is a conditional
     *    expression that checks if a podcast is followed. If a matching `podcast_uri` exists in the
     *    `podcast_followed_entries table`, it means the podcast is followed, and this expression
     *    evaluates to `true`. Otherwise, it's `false`. `AS is_followed`: This aliases the result of
     *    the conditional expression to `is_followed`, making it a column in the result set.
     *  2. `FROM podcasts`: This specifies that the primary table for the query is podcasts.
     *  3. `INNER JOIN (...) inner_query ON podcasts.uri = inner_query.podcast_uri`: This is an
     *  `INNER JOIN` with a subquery, aliased as `inner_query`. An `INNER JOIN` means that only rows
     *  where a match is found in both the podcasts table and the `inner_query` results will be
     *  included.
     *    -  `SELECT episodes.podcast_uri, MAX(published) AS last_episode_date`: This selects the
     *    `podcast_uri` and the maximum (most recent) published date, aliased as `last_episode_date`.
     *    - `FROM episodes`: This specifies that the subquery is operating on the episodes table.
     *    - `INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri`:
     *    This joins the `episodes` table with the `podcast_category_entries` table based on matching
     *    `podcast_uri`. This is how we filter episodes by category.
     *    - `WHERE category_id = :categoryId`: This filters the episodes to include only those that
     *    belong to the category whose [Category.id] is equal to our [Long] parameter [categoryId].
     *    - `GROUP BY episodes.podcast_uri`: This groups the episodes by their `podcast_uri`, so
     *    `MAX(published)` will find the latest date for each podcast within the specified category.
     *    - `ON podcasts.uri = inner_query.podcast_uri`: This is the join condition, linking the
     *    `podcasts` table to the results of the subquery based on matching `podcast_uri` values.
     *  4. `LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = inner_query.podcast_uri`:
     *  This is a `LEFT JOIN` with the `podcast_followed_entries` table, aliased as `followed_entries`.
     *    - A `LEFT JOIN` means that all rows from the left side (the `podcasts` table joined with
     *    the `inner_query` results) will be included. If there's a matching row in
     *    `podcast_followed_entries`, the columns from that table will be included. If there's no
     *    match, the columns from `podcast_followed_entries` will be NULL.
     *    - `ON followed_entries.podcast_uri = inner_query.podcast_uri`: This is the join condition,
     *    linking based on matching `podcast_uri` values.
     *  5. `ORDER BY datetime(last_episode_date) DESC`: This sorts the results in descending order
     *  based on the `last_episode_date`. datetime() is used to ensure proper date/time comparison.
     *  6. `LIMIT :limit`: This limits the number of rows returned to the value specified by the
     *  by our [Int] parameter [limit].
     *
     * @param categoryId the [Category.id] of the [Category] whose podcasts we are interested in.
     * @param limit the maximum number of rows to return.
     * @return a [Flow] of [List] of [PodcastWithExtraInfo] created from the podcasts whose category
     * has the [Category.id] equal to our [Long] parameter [categoryId] sorted by the latest episode
     * in each podcast with the number of rows returned limited to our [Int] parameter [limit].
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
     * This method returns a [Flow] of [List] of [PodcastWithExtraInfo] created from the podcasts
     * that the user is following sorted by the most recent episode of each podcast, with the number
     * of rows returned limited to our [Int] parameter [limit]. The SQL statment means:
     *  1. `SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed`
     *    - `podcasts.*`: This selects all columns from the `podcasts` table.
     *    - `last_episode_date`: This selects the date of the most recently published episode for
     *    each podcast (calculated in a subquery).
     *    - `(followed_entries.podcast_uri IS NOT NULL) AS is_followed`: This is a crucial part.
     *    It checks if a corresponding entry exists in the `podcast_followed_entries` table for the
     *    current podcast. If an entry exists (meaning `followed_entries.podcast_uri` is not null),
     *    it means the user is following the podcast, and `is_followed` will be true (or 1 in some
     *    SQL implementations). If no entry exists (meaning `followed_entries.podcast_uri` is null),
     *    it means the user is not following the podcast, and `is_followed` will be false (or 0).
     *  2. `FROM podcasts`: This specifies that the primary table for the query is `podcasts`.
     *  3. `INNER JOIN (...) episodes ON podcasts.uri = episodes.podcast_uri`: This is an `INNER JOIN`
     *  It connects the `podcasts` table to the results of the subquery, which is aliased as `episodes`.
     *  `podcasts.uri = episodes.podcast_uri`: This is the join condition. It links a podcast to its
     *  latest episode based on the `podcast_uri` column.
     *    - Subquery: `(SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri)`
     *    This subquery is responsible for finding the latest episode for each podcast.
     *    - `SELECT podcast_uri, MAX(published) AS last_episode_date`: It selects the `podcast_uri`
     *    and the maximum (latest) published date, aliasing it as `last_episode_date`.
     *    - `FROM episodes`: It gets this data from the `episodes` table.
     *    - `GROUP BY podcast_uri`: This is essential. It groups the episodes by `podcast_uri`, so
     *    the `MAX(published)` function finds the latest date for each podcast.
     *  4. `INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri`
     *    - This is another `INNER JOIN`, this time with the `podcast_followed_entries` table.
     *    - `followed_entries.podcast_uri = episodes.podcast_uri`: This condition links the latest
     *    episode (from the `episodes` subquery) to the `podcast_followed_entries` table based on
     *    the podcast_uri.
     *    - Because this is an `INNER JOIN`, only podcasts that have an entry in `podcast_followed_entries`
     *    will be included in the result.
     *  5. `ORDER BY datetime(last_episode_date) DESC`: This sorts the results in descending order
     *  based on the `last_episode_date`. datetime() is used to ensure that the date is treated as
     *  a date/time value for proper sorting.
     *  6. `LIMIT :limit`: This limits the number of results returned to our [Int] parameter [limit].
     *
     * @param limit the maximum number of rows to return.
     * @return a [Flow] of [List] of [PodcastWithExtraInfo] created from the podcasts that the user
     * is following sorted by the latest episode of each podcast with the number of rows returned
     * limited to our [Int] parameter [limit].
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
     * This method searches for podcasts whose title contains our [String] parameter [keyword]
     * considering only those podcasts that the user is following, sorted by the most recent episode
     * in each podcast, with the number of rows returned limited to our [Int] parameter [limit].
     * The SQL statment means:
     *  1. `SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed`
     *    - `podcasts.*`: This selects all columns from the `podcasts` table.
     *    - `last_episode_date`: This is an alias for the most recent episode's published date for
     *    each podcast
     *    - `(followed_entries.podcast_uri IS NOT NULL) AS is_followed`: This is a conditional
     *    expression that checks if a podcast is followed. If a matching `podcast_uri` exists in the
     *    `podcast_followed_entries table`, it means the podcast is followed, and this expression
     *    evaluates to `true`. Otherwise, it's `false`. `AS is_followed`: This aliases the result of
     *    the expression to `is_followed`, making it a column in the result set.
     *  2. `FROM podcasts`: This specifies that the primary table for the query is `podcasts`.
     *  3. `INNER JOIN (...) episodes ON podcasts.uri = episodes.podcast_uri`: This is an `INNER JOIN`
     *  it connects the `podcasts` table to the results of the subquery, which is aliased as `episodes`.
     *    - `podcasts.uri = episodes.podcast_uri`: This is the join condition. It links a podcast to
     *    its latest episode based on the `podcast_uri` column.
     *    - Subquery: `(SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri)`
     *    This subquery is responsible for finding the latest episode for each podcast.
     *    `SELECT podcast_uri, MAX(published) AS last_episode_date`: It selects the `podcast_uri`
     *    and the maximum (latest) published date, aliasing it as `last_episode_date`.
     *    `FROM episodes`: It gets this data from the `episodes` table.
     *    `GROUP BY podcast_uri`: This is essential. It groups the episodes by `podcast_uri`, so
     *    the `MAX(published)` function finds the latest date for each podcast.
     *  4. `INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri`
     *    - This is another `INNER JOIN`, this time with the `podcast_followed_entries` table.
     *    - `followed_entries.podcast_uri = episodes.podcast_uri`: This condition links the latest
     *    episode (from the `episodes` subquery) to the `podcast_followed_entries` table based on the
     *    `podcast_uri`.
     *    - Because this is an `INNER JOIN`, only podcasts that have an entry in `podcast_followed_entries`
     *    will be included in the result.
     *  5. `WHERE podcasts.title LIKE '%' || :keyword || '%'`: This is the filtering condition. It
     *  filters the results to include only podcasts whose titles contain the given `:keyword`.
     *  `LIKE '%' || :keyword || '%'` is a pattern matching expression. The `%` is a wildcard
     *  that matches any sequence of characters (including an empty sequence). So, this condition
     *  will match any title that contains the `:keyword` anywhere within it.
     *  6. `ORDER BY datetime(last_episode_date) DESC`: This sorts the results in descending order
     *   based on the `last_episode_date`. `datetime()` is used to ensure that the date is treated
     *   as a date/time value for proper sorting.
     *  7. `LIMIT :limit`: This limits the number of results returned to the value specified by our
     *   [Int] parameter [limit].
     *
     * @param keyword the keyword to search for in the podcast titles.
     * @param limit the maximum number of rows to return.
     * @return a [Flow] of [List] of [PodcastWithExtraInfo] created from the podcasts that the user
     * is following whose title contains our [String] parameter [keyword], sorted by the most recent
     * episode in each podcast with the number of rows returned limited to our [Int] parameter [limit].
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
     * This method searches for podcasts whose title contains our [String] parameter [keyword] that
     * are in the categories whose [Category.id] is in our [List] of [Long] parameter [categoryIdList],
     * sorted by the most recent episode in each podcast, with the number of rows returned limited
     * to our [Int] parameter [limit]. The SQL statment means:
     *  1. `SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed`
     *    - `podcasts.*`: Selects all columns from the `podcasts` table.
     *    - `last_episode_date`: This is an alias for the most recent episode's published date for
     *    each podcast. It's calculated in a subquery.
     *    - `(followed_entries.podcast_uri IS NOT NULL) AS is_followed`: This is a conditional
     *    expression that checks if a podcast is followed. If a matching `podcast_uri` exists in the
     *    `podcast_followed_entries` table, it means the podcast is followed, and this expression
     *    evaluates to `true`. Otherwise, it's `false`. `AS is_followed`: This aliases the result
     *    of the expression to `is_followed`, making it a column in the result set.
     *  2. `FROM podcasts`: This specifies that the primary table for the query is `podcasts`.
     *  3. `INNER JOIN (...) inner_query ON podcasts.uri = inner_query.podcast_uri`: This is an
     *  `INNER JOIN` with a subquery, aliased as `inner_query`. An `INNER JOIN` means that only rows
     *  where a match is found in both the `podcasts` table and the `inner_query` results will be
     *  included.
     *    - `SELECT episodes.podcast_uri, MAX(published) AS last_episode_date`: This selects the
     *    `podcast_uri` and the maximum (most recent) published date, aliased as `last_episode_date`.
     *    - `FROM episodes`: This specifies that the subquery is operating on the `episodes` table.
     *    - `INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri`:
     *    This joins the `episodes` table with the `podcast_category_entries` table based on matching
     *    `podcast_uri`. This is how we filter episodes by category.
     *    - `WHERE category_id IN (:categoryIdList)`: This filters the episodes to include only those
     *    that belong to the categories whose [Category.id] is in our [List] of [Long] parameter
     *    [categoryIdList].
     *    - `GROUP BY episodes.podcast_uri`: This groups the episodes by their `podcast_uri`, so
     *    `MAX(published)` will find the latest date for each podcast within the specified categories.
     *  4. `LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = inner_query.podcast_uri`:
     *    - This is a `LEFT JOIN` with the `podcast_followed_entries` table, aliased as `followed_entries`.
     *    - A `LEFT JOIN` means that all rows from the left side (the `podcasts` table joined with
     *    the `inner_query` results) will be included. If there's a matching row in `podcast_followed_entries`,
     *    the columns from that table will be included. If there's no match, the columns from
     *    `podcast_followed_entries` will be NULL.
     *    - `ON followed_entries.podcast_uri = inner_query.podcast_uri`: This is the join condition,
     *    linking based on matching `podcast_uri` values.
     *  5. `WHERE podcasts.title LIKE '%' || :keyword || '%'`: This is the filtering condition. It
     *  filters the results to include only podcasts whose titles contain the given :keyword. (Our
     *  [String] parameter [keyword]). `LIKE '%' || :keyword || '%'` is a pattern matching expression.
     *  The % is a wildcard that matches any sequence of characters (including an empty sequence).
     *  So, this condition will match any title that contains the `:keyword` anywhere within it.
     *  6. `ORDER BY datetime(last_episode_date) DESC`: This sorts the results in descending order
     *  based on the last_episode_date. datetime() is used to ensure that the date is treated as a
     *  date/time value for proper sorting.
     *  7. `LIMIT :limit`: This limits the number of results returned to the value specified by
     *  our [Int] parameter [limit].
     *
     * @param keyword the [String] keyword to search for in the podcast titles.
     * @param categoryIdList a [List] of [Category.id]'s of [Category]'s that the podcasts need to
     * be in.
     * @param limit the maximum number of results to return.
     * @return a [Flow] of [List] of [PodcastWithExtraInfo] created from the podcasts whose title
     * contains our [String] parameter [keyword] that are in the categories whose [Category.id]
     * is in our [List] of [Long] parameter [categoryIdList], sorted by the most recent episode
     * in each podcast with the number of rows returned limited to our [Int] parameter [limit].
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
     * This method returns the number of [Podcast] entries in the `podcasts` table.
     *
     * @return the number of [Podcast] entries in the `podcasts` table.
     */
    @Query("SELECT COUNT(*) FROM podcasts")
    abstract suspend fun count(): Int
}
