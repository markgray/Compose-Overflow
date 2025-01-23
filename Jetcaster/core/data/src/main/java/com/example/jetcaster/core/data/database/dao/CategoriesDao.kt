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
import com.example.jetcaster.core.data.database.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * `Room` DAO for [Category] related operations.
 */
@Dao
abstract class CategoriesDao : BaseDao<Category> {
    /**
     * This method returns a [Flow] of [List] of [Category] sorted by the number of podcasts in
     * each category. The `@Query` Marks a method in a Dao annotated class as a query method.
     * The value of the annotation is the query that will be run when this method is called. The
     * [Int] parameter [limit] will be bound to the `:limit` parameter in the query. The meaning of
     * the SQL is:
     *  - `SELECT categories.*` this selects all or the [Category] entries `FROM` the `categories`
     *  table of the database.
     *
     * Inside the parenthesis:
     *  - `SELECT category_id, COUNT(podcast_uri) AS podcast_count FROM podcast_category_entries`
     *  selects all of the `category_id` fields and the `COUNT` of the `podcast_uri` field aliased
     *  to `podcast_count` in the `podcast_category_entries` table
     *  - `GROUP BY category_id` groups the output of the `SELECT` by the `category_id` field.
     *
     * and then:
     *  - `INNER JOIN` selects entries from both the `categories` table and the data produced by the
     *  `SELECT` in the parenthesis (`category_id` and `podcast_count`) based `ON` the `category_id`
     *  field of the `podcast_category_entries` table and the `id` field of the `categories`.
     *
     * and then:
     *  - `ORDER BY podcast_count DESC` orders the data produced by the `INNER JOIN` by the
     *  `podcast_count` field in descending order.
     *  - `LIMIT :limit` Specifies the number of records to return in the result set.
     *
     * @param limit the number of records to return in the result set.
     * @return a [Flow] of [List] of [Category] sorted by the number of podcasts in each [Category].
     */
    @Query(
        """
        SELECT categories.* FROM categories
        INNER JOIN (
            SELECT category_id, COUNT(podcast_uri) AS podcast_count FROM podcast_category_entries
            GROUP BY category_id
        ) ON category_id = categories.id
        ORDER BY podcast_count DESC
        LIMIT :limit
        """
    )
    abstract fun categoriesSortedByPodcastCount(
        limit: Int
    ): Flow<List<Category>>

    /**
     * This method returns the [Category] from the `categories` table whose [Category.name] field
     * is equal to our [String] parameter [name] or `null` if there is none.
     */
    @Query("SELECT * FROM categories WHERE name = :name")
    abstract suspend fun getCategoryWithName(name: String): Category?

    /**
     * This method returns a [Flow] of [Category] from the `categories` table whose [Category.name]
     * field is equal to our [String] parameter [name] or `null` if there is none.
     */
    @Query("SELECT * FROM categories WHERE name = :name")
    abstract fun observeCategory(name: String): Flow<Category?>
}
