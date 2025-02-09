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

package com.example.jetcaster.core.data.repository

import com.example.jetcaster.core.data.database.dao.CategoriesDao
import com.example.jetcaster.core.data.database.dao.EpisodesDao
import com.example.jetcaster.core.data.database.dao.PodcastCategoryEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastsDao
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastCategoryEntry
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import dagger.Provides
import kotlinx.coroutines.flow.Flow

/**
 * An interface for managing categories and their relationships with podcasts and episodes.
 * This store provides functionality to retrieve categories, podcasts within categories,
 * and episodes from podcasts within categories, as well as adding new categories and
 * associating podcasts with categories.
 */
interface CategoryStore {

    /**
     * Returns a flow emitting a list of categories sorted by the number of podcasts they contain,
     * in descending order.
     *
     * This function retrieves all categories and then sorts them based on the count of podcasts
     * associated with each category. The sorting is done in descending order, so categories with
     * more podcasts will appear earlier in the list.
     *
     * @param limit The maximum number of categories to return. If not specified, it defaults to
     * [Integer.MAX_VALUE], effectively returning all categories. If the number of categories is
     * less than the limit, only the existing categories will be returned.
     * @return A [Flow] that emits a [List] of [Category] objects. The list is sorted by podcast
     * count (descending).
     */
    fun categoriesSortedByPodcastCount(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Category>>

    /**
     * Retrieves a list of podcasts belonging to a specific category, sorted by the number of
     * podcasts in that category.
     *
     * This function fetches podcasts associated with the given [categoryId] and orders them based
     * on the count of podcasts within that category. The podcasts are then returned as a list of
     * [PodcastWithExtraInfo] objects.
     *
     * @param categoryId The unique identifier of the category for which to retrieve podcasts.
     * @param limit An optional parameter specifying the maximum number of podcasts to return.
     * Defaults to [Int.MAX_VALUE] if not provided, effectively returning all podcasts in the
     * category.
     * @return A [Flow] emitting a [List] of [PodcastWithExtraInfo] objects. The list is sorted in
     * descending order based on the number of podcasts in the specified category. If no podcasts
     * are found for the given `categoryId`, an empty list is emitted. Emits a new list whenever the
     * underlying data changes.
     */
    fun podcastsInCategorySortedByPodcastCount(
        categoryId: Long,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     * Retrieves a list of episodes from podcasts belonging to a specific category.
     *
     * This function fetches episodes from podcasts that are associated with the given category ID.
     * It provides a stream of lists, allowing for reactive updates as the data changes.
     *
     * @param categoryId The unique identifier of the category. Episodes from podcasts in this
     * category will be retrieved.
     * @param limit The maximum number of episodes to retrieve. Defaults to [Integer.MAX_VALUE],
     * meaning no limit. If a limit is specified, the function will return up to that number of
     * episodes. If the number of episodes available is less than the limit, it returns all
     * available episodes.
     * @return A [Flow] of [List] of [EpisodeToPodcast]. Each list represents a batch of episodes and
     * their associated podcast information. The flow will emit a new list whenever the underlying
     * data changes. The list can be empty if there are no episodes found for the specified category
     * or if all fetched episodes were filtered.
     *
     * @see EpisodeToPodcast
     */
    fun episodesFromPodcastsInCategory(
        categoryId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>>

    /**
     * Adds a new category to the data source if it doesn't already exist.
     *
     * This function inserts a [Category] object into the persistent storage. It's a suspend function,
     * meaning it can be called within a coroutine or another suspend function.
     *
     * @param category The [Category] object to be added.
     * @return The row ID of the newly inserted category in the database, or -1 if the insertion
     * failed. The row ID is typically a unique identifier automatically generated by the database.
     *
     * @throws Exception If there is an error during the insertion process, such as a database error.
     * Specific exceptions might include SQLiteConstraintException if there is a constraint violation
     * (e.g., inserting a duplicate category name if the name is set as unique).
     */
    suspend fun addCategory(category: Category): Long

    /**
     * Adds a podcast to a specific category.
     *
     * This function associates a podcast, identified by its URI, with a category, identified by its
     * [Category.id]. This operation is typically used to organize podcasts into different groups or
     * genres.
     *
     * @param podcastUri The URI (Uniform Resource Identifier) of the podcast to be added to the
     * category. This should be a unique identifier for the podcast, often a URL or a content ID.
     * Example: "https://www.example.com/podcasts/my-podcast.rss" or "content://podcasts/123"
     * @param categoryId The [Category.id] of the category to which the podcast will be added.
     * This is a [Long] integer representing a unique identifier for the category.
     * Example: 1, 2, 100
     * @throws Exception If there's an error adding the podcast to the category, an exception might
     * be thrown.
     * Possible reasons include:
     *  - The podcast URI is invalid or not found.
     *  - The category ID is invalid or not found.
     *  - A database or storage error occurred.
     *  - Network issues (if the operation requires network access).
     *
     * It's recommended to handle potential exceptions when calling this function.
     *
     * @return Unit. The function does not return any specific value.
     */
    suspend fun addPodcastToCategory(podcastUri: String, categoryId: Long)


    /**
     * Retrieves a category from the data source based on its name.
     *
     * This function queries the underlying data source (e.g., database, API) for a category
     * with the specified [name]. If a matching category is found, it is emitted as a
     * [Category] object within the [Flow]. If no matching category is found, `null` is
     * emitted.
     *
     * The function returns a [Flow] to allow for asynchronous and potentially reactive
     * handling of the category retrieval process. This allows the caller to observe
     * the result and react to it as it becomes available.
     *
     * @param name The name of the category to retrieve.
     * @return A [Flow] that emits a [Category] object if a category with the given [name] is found,
     * or `null` if no matching category exists.
     */
    fun getCategory(name: String): Flow<Category?>
}

/**
 * [LocalCategoryStore] is an implementation of [CategoryStore] that uses local DAOs to
 * provide access to the categories, podcasts and episodes. It is constructed by Hilt as the
 * [CategoryStore] to inject by the `provideCategoryStore` [Provides] method.
 *
 * This class interacts with the database through the provided DAOs and provides methods for
 * retrieving categories, podcasts within categories, and episodes from podcasts in categories.
 * It also supports adding new categories and associating podcasts with categories.
 *
 * @property categoriesDao DAO for accessing and manipulating [Category] entities in the "categories"
 * table.
 * @property categoryEntryDao DAO for accessing and manipulating [PodcastCategoryEntry] entities
 * in the "podcast_category_entries" table, which links podcasts to categories.
 * @property episodesDao DAO for accessing and manipulating [Episode] entities in the "episodes"
 * table.
 * @property podcastsDao DAO for accessing and manipulating [Podcast] entities in the "podcasts"
 * table.
 */
class LocalCategoryStore(
    private val categoriesDao: CategoriesDao,
    private val categoryEntryDao: PodcastCategoryEntryDao,
    private val episodesDao: EpisodesDao,
    private val podcastsDao: PodcastsDao
) : CategoryStore {
    /**
     * Returns a flow containing a list of categories sorted by the number of podcasts associated
     * with each category. The categories with the highest podcast counts will appear first in the
     * list.
     *
     * @param limit The maximum number of categories to return. If there are fewer categories than
     * the limit, all available categories will be returned.
     * @return A [Flow] emitting a list of [Category] objects. Each [Category] object represents a
     * category, and the list is sorted in descending order based on the number of podcasts associated
     * with each category.
     */
    override fun categoriesSortedByPodcastCount(limit: Int): Flow<List<Category>> {
        return categoriesDao.categoriesSortedByPodcastCount(limit)
    }

    /**
     * Retrieves a list of podcasts belonging to a specific category, sorted by the their last
     * episode date.
     *
     * This function fetches podcasts associated with the given `categoryId` and limits the result to
     * the specified `limit`. The podcasts are sorted by the last episode date (in descending
     * order, with the categories containing the newest episodes at the top).
     *
     * Note: Currently, in this specific implementation, it's sorted by the last episode date, as
     * indicated by the call to [PodcastsDao.podcastsInCategorySortedByLastEpisode]. It needs to be
     * modified if it is to be sorted by the podcast count in the category as the name of the
     * function suggests.
     *
     * @param categoryId The [Category.id] of the [Category] to retrieve podcasts from.
     * @param limit The maximum number of podcasts to return.
     * @return A [Flow] emitting a [List] of [PodcastWithExtraInfo], where each [PodcastWithExtraInfo]
     * represents a podcast in the specified category. The list is sorted as described above. If no
     * podcasts are found or [categoryId] is not valid, it might return an empty [List].
     */
    override fun podcastsInCategorySortedByPodcastCount(
        categoryId: Long,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.podcastsInCategorySortedByLastEpisode(categoryId, limit)
    }

    /**
     * Retrieves a list of episodes from podcasts belonging to a specific category,sorted by the
     * their last episode date.
     *
     * This function uses the [EpisodesDao] field [episodesDao] to query the underlying data source
     * to retrieve episodes that are associated with podcasts within the given category, sorted by
     * the last episode date. The results are limited to a specified number of episodes.
     *
     * @param categoryId The [Category.id] of the category to filter podcasts by.
     * @param limit The maximum number of episodes to retrieve.
     * @return A [Flow] emitting a list of [EpisodeToPodcast] objects. Each [EpisodeToPodcast]
     * contains information about an episode and its associated podcast. The flow will emit the list
     * of episodes as they are retrieved from the data source. The list will contain at most [limit]
     * elements.
     * @throws Exception if there's an issue retrieving the data from the data source.
     */
    override fun episodesFromPodcastsInCategory(
        categoryId: Long,
        limit: Int
    ): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodesFromPodcastsInCategory(categoryId, limit)
    }

    /**
     * Adds a category to the database if it doesn't already exist.
     *
     * This function checks if a category with the same name already exists.
     *  - If a category with the same name exists, it returns the ID of the existing category.
     *  - If a category with the same name does not exist, it inserts the new category into the
     *  database and returns the generated ID.
     *
     * @param category The [Category] object to be added.
     * @return The [Category.id] of the category. If a category with the same name already exists,
     * the [Category.id] of the existing category is returned. Otherwise, the newly generated ID of
     * the inserted category is returned.
     * @throws Exception If any error occurred during the database operation
     */
    override suspend fun addCategory(category: Category): Long {
        return when (val local = categoriesDao.getCategoryWithName(category.name)) {
            null -> categoriesDao.insert(category)
            else -> local.id
        }
    }

    /**
     * Adds a podcast to a specific category.
     *
     * This function inserts a new entry into the database linking a given podcast URI to a
     * category ID. It uses the [PodcastCategoryEntry] data class to represent this relationship.
     *
     * @param podcastUri The URI of the podcast to add to the category. This should be a unique
     * identifier for the podcast.
     * @param categoryId The ID of the category to which the podcast should be added.
     * @throws Exception if the database operation fails.
     */
    override suspend fun addPodcastToCategory(podcastUri: String, categoryId: Long) {
        categoryEntryDao.insert(
            entity = PodcastCategoryEntry(podcastUri = podcastUri, categoryId = categoryId)
        )
    }

    /**
     * Retrieves a category by its [Category.name], observing changes in the database.
     *
     * This function queries the [CategoriesDao] field [categoriesDao] to find a category with the
     * specified [name]. It returns a [Flow] that emits the found [Category] object or `null` if no
     * category with that name exists. The [Flow] will emit whenever the corresponding category in
     * the database is updated.
     *
     * @param name The name of the category to retrieve.
     * @return A [Flow] emitting the [Category] object if found, or `null` otherwise. The Flow will
     * emit on data changes in the underlying database.
     */
    override fun getCategory(name: String): Flow<Category?> =
        categoriesDao.observeCategory(name)
}
