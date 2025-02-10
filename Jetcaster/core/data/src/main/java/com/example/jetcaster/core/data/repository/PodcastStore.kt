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

import com.example.jetcaster.core.data.database.dao.PodcastFollowedEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastsDao
import com.example.jetcaster.core.data.database.dao.TransactionRunner
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastFollowedEntry
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import kotlinx.coroutines.flow.Flow

/**
 * A store for managing and accessing podcast data.
 *
 * This interface defines methods for retrieving, searching, and managing podcasts,
 * including their extra information and follow status. It provides access to
 * podcast data through Flows, allowing for reactive updates as the underlying
 * data changes.
 */
interface PodcastStore {

    /**
     * Retrieves a Podcast object based on its URI.
     *
     * This function takes a URI string as input and returns a [Flow] that emits a single
     * [Podcast] object. The function is responsible for fetching the podcast data
     * associated with the given URI and constructing the corresponding Podcast instance.
     *
     * @param uri The unique URI identifying the podcast to retrieve. This could be a URL, a file
     * path, or any other string representation of a unique resource identifier for the podcast.
     * @return A [Flow] that emits a single [Podcast] object. If an error occurs during retrieval or
     * processing of the podcast data, the flow will emit an error. If the podcast is not found or
     * the URI is invalid, the flow might complete without emitting any value or emit a specific
     * error.
     * @throws IllegalArgumentException if the provided URI is malformed or invalid in some way.
     * @throws Exception if any other error happen like network error.
     */
    fun podcastWithUri(uri: String): Flow<Podcast>

    /**
     * Returns a [Flow] containing the [PodcastWithExtraInfo] of the podcast whose URI is its
     * [String] parameter [podcastUri].
     *
     * @param podcastUri The URI of the podcast whose [PodcastWithExtraInfo] should be retrieved.
     * @return A [Flow] that emits a [PodcastWithExtraInfo] object. If the podcast with the URI
     * [podcastUri] is not found or an error occurs during the retrieval process, the flow will
     * emit an error.
     */
    fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo>

    /**
     * Returns a flow of a list of podcasts sorted by the date of their last episode in descending
     * order.
     *
     * This function retrieves all podcasts along with extra information (like the latest episode)
     * and orders them based on the publication date of their most recent episode. Podcasts with
     * more recent episodes will appear earlier in the list. If a podcast has no episodes, it will
     * be considered to have the earliest possible date and will appear towards the end of the list.
     *
     * @param limit The maximum number of podcasts to return. If not specified, defaults to
     * [Int.MAX_VALUE], effectively returning all available podcasts.
     * @return A [Flow] emitting a list of [PodcastWithExtraInfo] objects, sorted by the last
     * episode's publication date of each podcast in descending order.
     */
    fun podcastsSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     * Returns a flow of a list of followed podcasts, sorted by the date of their last episode.
     *
     * This function retrieves all podcasts that the user is following and sorts them based on the
     * most recent episode published. The podcast with the newest episode will be at the beginning
     * of the list.  If a podcast has no episodes, it will be placed at the end of the list.
     *
     * @param limit The maximum number of podcasts to return. Defaults to [Int.MAX_VALUE] (no limit).
     * If the number of followed podcasts is less than the limit, all followed podcasts will be
     * returned. If the limit is zero or negative, an empty list will be returned.
     * @return A [Flow] emitting a [List] of [PodcastWithExtraInfo] objects, sorted by the date of
     * the last episode in descending order (newest first). Returns an empty list if the user is not
     * following any podcasts or if the limit is zero or negative.
     */
    fun followedPodcastsSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     * Searches for podcasts by their title such that its name partially matches the [String]
     * parameter [keyword].
     *
     * This function queries a data source (e.g., a database or API) to find podcasts
     * whose titles contain the specified keyword. The search is case-insensitive.
     *
     * @param keyword The keyword to search for within podcast titles. Must not be empty.
     * @param limit The maximum number of podcasts to return. Defaults to [Int.MAX_VALUE], meaning
     * no limit.
     * @return A [Flow] emitting a [List] of [PodcastWithExtraInfo] objects that match the search
     * criteria. The flow may emit multiple lists as the data source is updated, or if the search
     * results are paginated. If no matching podcasts are found, an empty list will be emitted.
     * @throws IllegalArgumentException if the provided keyword is empty.
     *
     * @see PodcastWithExtraInfo
     */
    fun searchPodcastByTitle(
        keyword: String,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     * Returns a [Flow] of a [List] of podcasts such that they belong to the any of categories
     * specified in the [List] of [Category] parameter [categories] and whose names partially match
     * [String] parameter [keyword].
     *
     * This function performs a search operation to find podcasts that match the given criteria.
     * It searches for podcasts whose title contains the specified keyword (case-insensitive)
     * AND are associated with at least one of the specified categories.
     * The search results are returned as a Flow of lists, allowing for reactive handling
     * of the search results as they become available.
     *
     * @param keyword The keyword to search for within the podcast titles. The search is case
     * insensitive. If the keyword is empty or blank, it will match any podcast (regarding the title
     * criteria).
     * @param categories A list of [Category] objects representing the categories to filter by.
     * If the list is empty, it will match any podcast (regarding the categories criteria).
     * @param limit The maximum number of podcasts to return. Defaults to [Int.MAX_VALUE] (no limit).
     * If a negative value is provided, it is treated as [Int.MAX_VALUE] (no limit).
     * @return A [Flow] emitting a list of [PodcastWithExtraInfo] objects. Each [PodcastWithExtraInfo]
     * object represents a podcast that matches the search criteria. The list will be empty if no
     * podcasts match the criteria.
     */
    fun searchPodcastByTitleAndCategories(
        keyword: String,
        categories: List<Category>,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>>

    /**
     * Toggles the followed state of a podcast.
     *
     * This function asynchronously handles the logic of following or unfollowing a podcast
     * identified by its URI. If the podcast is currently followed, it will be unfollowed, and
     * vice-versa.
     *
     * @param podcastUri The URI of the podcast to toggle the followed state for.  This should be a
     * valid, parseable URI representing the podcast. It could be a local file URI, a content
     * provider URI, or a remote URL.
     * @throws IllegalArgumentException if the provided podcastUri is an invalid or unparsable URI.
     * @throws Exception if an error occurs during the process of toggling the followed state, such
     * as database errors, network issues, or other unexpected problems. Specific Exception types
     * should be documented if they are expected (e.g., `IOException`, `SQLiteException`).
     */
    suspend fun togglePodcastFollowed(podcastUri: String)

    /**
     * Follows a podcast identified by its URI.
     *
     * This function initiates the process of following a podcast. Following a podcast typically
     * means adding it to a user's library or subscriptions, allowing them to receive updates or
     * new episodes.
     *
     * This is a suspend function, meaning it must be called within a coroutine or another suspend
     * function. This is because the operation may involve network requests or other potentially
     * long-running tasks that should not block the main thread.
     *
     * @param podcastUri The unique identifier (e.g., a URL or a custom URI) of the podcast to follow.
     * This should be a string that uniquely represents the target podcast.
     * @throws Exception If any error occurs during the following process (e.g., network issues,
     * invalid URI, server errors). Specific exceptions might be thrown depending on the
     * implementation.
     */
    suspend fun followPodcast(podcastUri: String)

    /**
     * Unfollows a podcast identified by its URI.
     *
     * This function removes the podcast from the user's list of followed podcasts.
     * It performs a network operation to update the user's followed podcast list
     * on the server.
     *
     * @param podcastUri The unique URI identifying the podcast to unfollow. This URI should
     * correspond to a podcast that the user is currently following.
     * @throws IllegalArgumentException If the provided `podcastUri` is null or empty.
     * @throws IllegalStateException if internal state is invalid and the operation cannot be
     * performed
     * @throws Exception In case of any other unexpected errors.
     */
    suspend fun unfollowPodcast(podcastUri: String)

    /**
     * Adds a new [Podcast] to the data source. This automatically switches to the main thread to
     * maintain thread consistency.
     *
     * This function is a suspending function, meaning it can be paused and resumed
     * to allow for asynchronous operations, such as network requests or database writes,
     * without blocking the main thread.
     *
     * @param podcast The [Podcast] object representing the podcast to be added. This object should
     * contain all the necessary information about the podcast, such as its title, author, RSS feed
     * URL, etc.
     *
     * @throws Exception if there is an error during the process of adding the podcast, e.g., network
     * connectivity issues, database errors, or invalid podcast data. The specific type of exception
     * thrown will depend on the underlying implementation.
     */
    suspend fun addPodcast(podcast: Podcast)

    /**
     * Checks if the [PodcastStore] is empty.
     *
     * This function determines whether [PodcastStore] contains any elements. It returns `true` if
     * it is empty (i.e., has no elements), and `false` otherwise.
     *
     * This is a suspending function, meaning it can potentially suspend the execution
     * of the coroutine it is called from. This could happen if the underlying data
     * structure needs to perform some asynchronous operation to determine its emptiness.
     *
     * @return `true` if the [PodcastStore] is empty, `false` otherwise.
     */
    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Podcast] instances.
 */
class LocalPodcastStore(
    private val podcastDao: PodcastsDao,
    private val podcastFollowedEntryDao: PodcastFollowedEntryDao,
    private val transactionRunner: TransactionRunner
) : PodcastStore {
    /**
     * Return a flow containing the [Podcast] with the given [uri].
     */
    override fun podcastWithUri(uri: String): Flow<Podcast> {
        return podcastDao.podcastWithUri(uri)
    }

    /**
     * Return a flow containing the [PodcastWithExtraInfo] with the given [podcastUri].
     */
    override fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo> =
        podcastDao.podcastWithExtraInfo(podcastUri)

    /**
     * Returns a flow containing the entire collection of podcasts, sorted by the last episode
     * publish date for each podcast.
     */
    override fun podcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.podcastsSortedByLastEpisode(limit)
    }

    /**
     * Returns a flow containing a list of all followed podcasts, sorted by the their last
     * episode date.
     */
    override fun followedPodcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.followedPodcastsSortedByLastEpisode(limit)
    }

    override fun searchPodcastByTitle(
        keyword: String,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.searchPodcastByTitle(keyword, limit)
    }

    override fun searchPodcastByTitleAndCategories(
        keyword: String,
        categories: List<Category>,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        val categoryIdList = categories.map { it.id }
        return podcastDao.searchPodcastByTitleAndCategory(keyword, categoryIdList, limit)
    }

    override suspend fun followPodcast(podcastUri: String) {
        podcastFollowedEntryDao.insert(PodcastFollowedEntry(podcastUri = podcastUri))
    }

    override suspend fun togglePodcastFollowed(podcastUri: String): Unit = transactionRunner {
        if (podcastFollowedEntryDao.isPodcastFollowed(podcastUri)) {
            unfollowPodcast(podcastUri)
        } else {
            followPodcast(podcastUri)
        }
    }

    override suspend fun unfollowPodcast(podcastUri: String) {
        podcastFollowedEntryDao.deleteWithPodcastUri(podcastUri)
    }

    /**
     * Add a new [Podcast] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addPodcast(podcast: Podcast) {
        podcastDao.insert(podcast)
    }

    override suspend fun isEmpty(): Boolean = podcastDao.count() == 0
}
