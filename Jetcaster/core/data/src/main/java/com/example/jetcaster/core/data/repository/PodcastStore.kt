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
     * associated with the given URI and constructing the corresponding [Podcast] instance.
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
     * @see PodcastWithExtraInfo
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
     * @see PodcastWithExtraInfo
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
     * @param categories A [List] of [Category] objects representing the categories to filter by.
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
     * @param podcastUri The URI of the podcast to toggle the followed state for. This should be a
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
     * This function removes the podcast from the user's set of followed podcasts.
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
 * [LocalPodcastStore] is a concrete implementation of [PodcastStore] that
 * interacts directly with the local database to manage [Podcast] data.
 *
 * It provides methods for retrieving, searching, following, and unfollowing podcasts,
 * as well as adding new podcasts to the local store.
 *
 * @property podcastDao The Data Access Object (DAO) for interacting with the "podcast" table 
 * in the database.
 * @property podcastFollowedEntryDao The DAO for managing the relationship between users and
 * followed podcasts in the "podcast_followed_entries" table.
 * @property transactionRunner An utility class to handle multiple atomic database transactions.
 */
class LocalPodcastStore(
    private val podcastDao: PodcastsDao,
    private val podcastFollowedEntryDao: PodcastFollowedEntryDao,
    private val transactionRunner: TransactionRunner
) : PodcastStore {
    /**
     * Retrieves a podcast from the data source by its URI.
     *
     * This function queries the [PodcastsDao] property [podcastDao] for a [Podcast] whose
     * [Podcast.uri] matches the URI in the [String] parameter [uri]. It returns a [Flow] that emits
     * the [Podcast] object if found, or completes without emitting if no podcast with the specified
     * URI exists.
     *
     * We just return the [Flow] of [Podcast] that the [PodcastsDao.podcastWithUri] method of
     * [PodcastsDao] property [podcastDao] returns when called with our [String] parameter [uri].
     *
     * @param uri The URI of the podcast to retrieve.
     * @return A [Flow] that emits the [Podcast] object if found. Emits nothing and completes if no
     * podcast with the specified URI is found.
     * @throws Exception if any error happens during the fetching process from the database.
     * @see [PodcastsDao.podcastWithUri]
     */
    override fun podcastWithUri(uri: String): Flow<Podcast> {
        return podcastDao.podcastWithUri(uri = uri)
    }

    /**
     * Retrieves a [PodcastWithExtraInfo] object from the database for a given podcast URI.
     *
     * This function uses the [PodcastsDao] property [podcastDao] to fetch detailed information about
     * a podcast including the [Podcast] itself, the date of the latest episode, and whether it is
     * followed or not. It returns a [Flow] that emits the [PodcastWithExtraInfo] object for the
     * [Podcast] identified by the URI in its [String] parameter [podcastUri].
     *
     * We just return the [Flow] of [PodcastWithExtraInfo] that the [PodcastsDao.podcastWithExtraInfo]
     * method of [PodcastsDao] property [podcastDao] returns when called with our [String] parameter
     * [podcastUri].
     *
     * @param podcastUri The unique URI string identifying the podcast.
     * @return A [Flow] emitting a single [PodcastWithExtraInfo] object. The Flow will emit the
     * [PodcastWithExtraInfo] if the [Podcast] is found, otherwise the flow will complete. It does
     * not emit errors, if a podcast is not found the flow simply finishes. Subscribers should
     * consider handling empty flow conditions.
     */
    override fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo> =
        podcastDao.podcastWithExtraInfo(podcastUri = podcastUri)

    /**
     * Retrieves a [List] of [PodcastWithExtraInfo] of all of the podcasts sorted by the date of
     * their last episode in descending order.
     *
     * This function queries the underlying data source to fetch [PodcastWithExtraInfo]'s for all of
     * the [Podcast]'s in the database, sorted by the date of their last episode in descending order.
     *
     * We just return the [Flow] of [List] of [PodcastWithExtraInfo] that the
     * [PodcastsDao.podcastsSortedByLastEpisode] method of [PodcastsDao] property [podcastDao]
     * returns when called with our [Int] parameter [limit].
     *
     * @param limit The maximum number of podcasts to return. If there are fewer podcasts than the
     * limit, all available podcasts will be returned.
     * @return A [Flow] emitting a list of [PodcastWithExtraInfo] objects. The list is sorted by the
     * date of the last episode (most recent first) and contains at most [limit] elements. The flow
     * will emit an updated list if the underlying data changes.
     */
    override fun podcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.podcastsSortedByLastEpisode(limit = limit)
    }

    /**
     * Retrieves a list of followed podcasts sorted by the date of their last episode in descending
     * order.
     *
     * This function fetches podcasts that the user is following and sorts them based on the most
     * recent episode released. The result is a list of [PodcastWithExtraInfo] objects created for
     * the followed podcasts, ordered from the podcast with the newest last episode to the podcast
     * with the oldest last episode.
     *
     * We just return the [Flow] of [List] of [PodcastWithExtraInfo] that the
     * [PodcastsDao.followedPodcastsSortedByLastEpisode] method of [PodcastsDao] property
     * [podcastDao] returns when called with our [Int] parameter [limit].
     *
     * @param limit The maximum number of podcasts to return. If the number of followed podcasts is
     * less than this limit, all followed podcasts will be returned. If the number of followed
     * podcasts is more than this limit, only the top [limit] number of podcasts will be returned.
     * @return A [Flow] emitting a [List] of [PodcastWithExtraInfo] objects, sorted by the last
     * episode date in descending order. The list may be empty if no podcasts are followed.
     */
    override fun followedPodcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.followedPodcastsSortedByLastEpisode(limit = limit)
    }

    /**
     * Searches for podcasts in the local database based on a keyword in their titles.
     *
     * This function queries the local podcast database to find podcasts whose titles
     * contain the [String] parameter [keyword]. The search is case-insensitive.
     *
     * We just return the [Flow] of [List] of [PodcastWithExtraInfo] that the
     * [PodcastsDao.searchPodcastByTitle] method of [PodcastsDao] property [podcastDao] returns
     * when called with our [String] parameter [keyword] and [Int] parameter [limit].
     *
     * @param keyword The keyword to search for within podcast titles. It will match if the keyword
     * is a substring of the title. e.g. if keyword is "Tech", it will match "Tech News Daily" or
     * "MyTechCast".
     * @param limit The maximum number of results to return. If there are more matching podcasts
     * than [limit], only the first [limit] number of results will be returned.
     * @return A [Flow] emitting a [List] of [PodcastWithExtraInfo] objects. The list will contain
     * podcasts whose titles contain the [String] parameter [keyword]. If no podcasts are found 
     * the [Flow] will emit an empty list.
     */
    override fun searchPodcastByTitle(
        keyword: String,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.searchPodcastByTitle(keyword = keyword, limit = limit)
    }

    /**
     * Searches for podcasts based on a keyword in the title and a list of categories.
     *
     * This function queries the local database for podcasts that match the given search criteria.
     * It filters podcasts whose titles contain the specified keyword (case-insensitive) and
     * are associated with at least one of the provided categories. The search is limited to the
     * number of results specified by the [Int] parameter [limit].
     *
     * We start by initializing our [List] of [Long] variable `val categoryIdList` using the [map]
     * operator to convert each [Category] object in our [List] of [Category] parameter [categories]
     * to its [Long] property [Category.id]. Then we return the [Flow] of [List] of
     * [PodcastWithExtraInfo] that the [PodcastsDao.searchPodcastByTitleAndCategory] method of
     * [PodcastsDao] property [podcastDao] returns when called with our [String] parameter [keyword],
     * our [List] of [Long] variable `categoryIdList`, and our [Int] parameter [limit].
     *
     * @param keyword The keyword to search for within the podcast titles. The search is case
     * insensitive.
     * @param categories A list of [Category] objects representing the categories to filter by.
     * Only podcasts belonging to at least one of these categories will be included.
     * @param limit The maximum number of podcasts to return in the result.
     * @return A [Flow] emitting a list of [PodcastWithExtraInfo] objects created for podcasts that
     * match the search criteria. The list is ordered by the date of the last episode in each
     * podcast in descending order (most recent first). If no matching podcasts are found, an empty
     * list will be emitted.
     *
     * @see PodcastWithExtraInfo
     * @see Category
     * @see Flow
     * @see PodcastsDao.searchPodcastByTitleAndCategory
     */
    override fun searchPodcastByTitleAndCategories(
        keyword: String,
        categories: List<Category>,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>> {
        val categoryIdList: List<Long> = categories.map { it.id }
        return podcastDao.searchPodcastByTitleAndCategory(
            keyword = keyword,
            categoryIdList = categoryIdList,
            limit = limit
        )
    }

    /**
     * Repository function to follow a podcast. This function inserts a new [PodcastFollowedEntry]
     * into the database, representing that the user is now following the specified podcast.
     *
     * We just call the [PodcastFollowedEntryDao.insert] method of [PodcastFollowedEntryDao] property
     * [podcastFollowedEntryDao] with our [String] parameter [podcastUri] as its `podcastUri` argument.
     *
     * @param podcastUri The URI (Uniform Resource Identifier) of the podcast to follow.
     * @throws Exception if there's an error while inserting the podcast follow entry into the database.
     *
     * @see PodcastFollowedEntry
     * @see podcastFollowedEntryDao
     */
    override suspend fun followPodcast(podcastUri: String) {
        podcastFollowedEntryDao.insert(PodcastFollowedEntry(podcastUri = podcastUri))
    }

    /**
     * Toggles the followed status of a podcast.
     *
     * This function checks if a podcast, identified by our [String] parameter [podcastUri], is
     * currently followed. If it is, the podcast is unfollowed. If it is not, the podcast is followed.
     * These operations are performed within the atomic transaction that is provided by
     * [TransactionRunner] property [transactionRunner] to ensure data consistency.
     *
     * @param podcastUri The URI of the podcast to toggle the followed status for.
     * @throws Exception if any error occurs during the transaction or database operation.
     * @see [unfollowPodcast]
     * @see [followPodcast]
     * @see [transactionRunner]
     * @see [PodcastFollowedEntryDao.isPodcastFollowed]
     */
    override suspend fun togglePodcastFollowed(podcastUri: String): Unit = transactionRunner {
        if (podcastFollowedEntryDao.isPodcastFollowed(podcastUri = podcastUri)) {
            unfollowPodcast(podcastUri = podcastUri)
        } else {
            followPodcast(podcastUri = podcastUri)
        }
    }

    /**
     * Unfollows a podcast by removing its entry from the followed podcasts database.
     *
     * This function deletes the corresponding [PodcastFollowedEntry] from the database
     * based on the provided podcast URI. If no entry with the given URI exists,
     * no action is taken.
     *
     * We just call the [PodcastFollowedEntryDao.deleteWithPodcastUri] method of our
     * [PodcastFollowedEntryDao] property [podcastFollowedEntryDao] with our [String] parameter
     * [podcastUri] as its `podcastUri` argument.
     *
     * @param podcastUri The unique URI of the podcast to unfollow. This is used to identify the
     * specific [PodcastFollowedEntry] in the database.
     */
    override suspend fun unfollowPodcast(podcastUri: String) {
        podcastFollowedEntryDao.deleteWithPodcastUri(podcastUri = podcastUri)
    }

    /**
     * Adds a new podcast to the data source.
     *
     * This function inserts a given [Podcast] object into the underlying data storage.
     * It's a suspend function, meaning it should be called within a coroutine or another
     * suspend function. This allows it to perform potentially long-running database operations
     * without blocking the main thread. Automatically switches to the main thread to maintain
     * thread consistency.
     *
     * We just call the [PodcastsDao.insert] method of [PodcastsDao] property [podcastDao] with
     * our [Podcast] parameter [podcast] as its `entity` argument.
     *
     * @param podcast The [Podcast] object to be added to the data source.
     */
    override suspend fun addPodcast(podcast: Podcast) {
        podcastDao.insert(entity = podcast)
    }

    /**
     * Checks if the "podcasts" table in the database is empty.
     *
     * We just return `true` if the [PodcastsDao.count] method of [PodcastsDao] property [podcastDao]
     * is equal to 0, indicating that the "podcasts" table is empty. Otherwise, we return `false`.
     *
     * @return `true` if the data source contains no records (i.e., the count is 0), `false`
     * otherwise.
     */
    override suspend fun isEmpty(): Boolean = podcastDao.count() == 0
}
