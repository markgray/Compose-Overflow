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

import com.example.jetcaster.core.data.database.dao.EpisodesDao
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import kotlinx.coroutines.flow.Flow

/**
 * An interface for managing [Episode] instances and their relationships with [Podcast] instances.
 */
interface EpisodeStore {

    /**
     * Retrieves the [Episode] object associated with the URI [episodeUri] from the "episodes" table.
     *
     * This function fetches episode details based on a unique identifier (URI). It is
     * designed to handle asynchronous data retrieval and returns a [Flow] that emits
     * the [Episode] when available.
     *
     * @param episodeUri The unique identifier (URI) of the episode to retrieve. This URI is
     * expected to be a valid identifier for an episode within the data source.
     * @return A [Flow] that emits the [Episode] object associated with the provided [episodeUri].
     * If no episode is found for the given URI or if an error occurs during the retrieval process,
     * the Flow may emit nothing or throw an exception, depending on the implementation. The flow
     * will emit exactly one value.
     * @throws Exception if any error occurs during fetching the data.
     */
    fun episodeWithUri(episodeUri: String): Flow<Episode>

    /**
     * Retrieves an [EpisodeToPodcast] object containing an episode and its associated podcast,
     * based on the provided episode URI.
     *
     * This function uses the provided URI in its [String] parameter [episodeUri] to query the
     * underlying data source (e.g., a database, a content provider) and retrieve the corresponding
     *  podcast details. It then combines these details into an [EpisodeToPodcast] object.
     *
     * The function returns a [Flow] that emits a single [EpisodeToPodcast] object. This allows
     * for asynchronous and reactive handling of the data.
     *
     * If no episode is found matching the provided URI or if the episode does not have an associated
`    * podcast, the flow will emit a default/empty [EpisodeToPodcast] object or potentially throw an
`    * exception depending on the implementation.
     *
     * @param episodeUri The URI of the episode to retrieve. This is a unique identifier for the
     * episode.
     * @return A [Flow] that emits a single [EpisodeToPodcast] object containing the episode and its
     * associated podcast.
     *
     * @see EpisodeToPodcast
     */
    fun episodeAndPodcastWithUri(episodeUri: String): Flow<EpisodeToPodcast>

    /**
     * Retrieves a flow of lists of episodes associated with a specific podcast.
     *
     * This function fetches episodes that belong to a podcast identified by its URI.
     * It allows you to limit the number of episodes retrieved.
     *
     * @param podcastUri The unique URI of the podcast. This is used to identify the podcast for
     * which episodes should be retrieved.
     * @param limit The maximum number of episodes to retrieve. Defaults to [Integer.MAX_VALUE],
     * effectively retrieving all episodes if not specified. If the number of episodes associated
     * with the podcast is less than the limit, only those available episodes are returned.
     * @return A [Flow] that emits lists of [EpisodeToPodcast] objects. Each list represents a batch
     * of episodes retrieved. The frequency and size of these batches are implementation-dependent.
     * If no episode is associated with the [podcastUri], an empty list will be emitted. If an error
     * occurs during the fetching, an [Exception] will be propagated through the [Flow].
     *
     * @throws Exception If there is an issue during retrieving the data. The specific Exception
     * type will depend of the internal implementation.
     */
    fun episodesInPodcast(
        podcastUri: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>>

    /**
     * Retrieves a flow of lists of episodes associated with the given podcast URIs sorted by the
     * publication date in descending order (most recently published to least recently published).
     *
     * This function fetches episodes from multiple podcasts specified by their URIs. It limits the
     * total number of episodes retrieved across all podcasts to the specified limit. The results
     * are provided as a flow of lists, where each list contains [EpisodeToPodcast] objects.
     *
     * @param podcastUris A list of URIs representing the podcasts to fetch episodes from. Must not
     * be empty. If a URI is invalid or a podcast cannot be found, episodes from that podcast will
     * be skipped, but it will not stop the flow.
     * @param limit The maximum number of episodes to retrieve across all podcasts. Defaults to
     * [Integer.MAX_VALUE], meaning no limit. Must be non-negative. If limit is 0, it will emit an
     * empty list. If limit is less than the total number of episodes available, only up to the
     * limit will be returned.
     * @return A [Flow] emitting lists of [EpisodeToPodcast] objects. Each emitted list represents a
     * chunk of retrieved episodes. The flow will complete when all available episodes (up to the
     * limit) have been emitted. The flow will emit at least one empty list if there are no episodes
     * available and limit is not equal to zero.
     * @throws IllegalArgumentException if podcastUris is empty or limit is negative.
     */
    fun episodesInPodcasts(
        podcastUris: List<String>,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>>

    /**
     * Adds a collection of episodes to the data store. This automatically switches to the main
     * thread to maintain thread consistency.
     *
     * This function persists the provided [episodes] to the underlying data storage.
     * It's designed to handle bulk insertions efficiently. If an episode with the same
     * identifier already exists, the behavior depends on the underlying implementation
     * (it might be overwritten, ignored, or result in an error).
     *
     * This is a suspend function, meaning it can be paused and resumed, typically
     * used for operations that might involve I/O or other potentially long-running
     * processes. It should be called from within a coroutine or another suspend function.
     *
     * @param episodes The collection of [Episode] objects to be added. Must not be empty.
     * @throws IllegalArgumentException If the [episodes] collection is empty.
     * @throws Exception If an error occurs during the addition of episodes to the data store.
     */
    suspend fun addEpisodes(episodes: Collection<Episode>)

    /**
     * Checks if the collection or data structure represented by the receiver is empty.
     *
     * This function determines whether the associated collection or data structure contains
     * any elements. It returns `true` if the collection is empty (i.e., has no elements),
     * and `false` otherwise.
     *
     * This is a suspending function, meaning it can potentially suspend the execution
     * of the coroutine it is called from. This could happen if the underlying data
     * structure needs to perform some asynchronous operation to determine its emptiness.
     *
     * @return `true` if the collection or data structure is empty, `false` otherwise.
     */
    suspend fun isEmpty(): Boolean
}

/**
 * [EpisodeStore] implementation that uses a local database via [EpisodesDao].
 *
 * This class provides methods to retrieve and manage episodes stored locally.
 * It interacts with the [EpisodesDao] to perform database operations.
 *
 * @property episodesDao The data access object responsible for interacting with the episode database.
 */
class LocalEpisodeStore(
    private val episodesDao: EpisodesDao
) : EpisodeStore {

    /**
     * Retrieves the [Episode] from the "episodes" table of the database corresponding to the [String]
     * parameter [episodeUri].
     *
     * This function queries the underlying data access object ([episodesDao]) to
     * fetch an episode based on its unique URI. It returns a [Flow] that emits
     * the matching [Episode] or completes without emitting if no episode with
     * the specified URI is found.
     *
     * We just return the [Flow] of [Episode] returned by the [EpisodesDao.episode] method of our
     * [EpisodesDao] field [episodesDao] when called with its `uri` argument our [String] parameter
     * [episodeUri].
     *
     * @param episodeUri The unique URI of the episode to retrieve.
     * @return A [Flow] that emits the [Episode] associated with the given URI. If no episode with
     * the specified URI exists, the flow completes without emitting.
     */
    override fun episodeWithUri(episodeUri: String): Flow<Episode> {
        return episodesDao.episode(uri = episodeUri)
    }

    /**
     * Retrieves an [EpisodeToPodcast] object, which contains an [Episode] and its associated
     * [Podcast], based on the provided episode URI.
     *
     * This function queries the database for an episode that matches the given URI and then
     * returns the episode along with its associated podcast information. It leverages Room's
     * relationship mapping to fetch both entities in a single query.
     *
     * We just return the [Flow] of [EpisodeToPodcast] returned by the [EpisodesDao.episodeAndPodcast]
     * method of our [EpisodesDao] field [episodesDao] when called with its `episodeUri` argument our
     * [String] parameter [episodeUri].
     *
     * @param episodeUri The URI of the episode to retrieve. This should be a unique identifier for
     * the episode within the data source.
     * @return A [Flow] emitting an [EpisodeToPodcast] object. This allows for observing changes to
     * the underlying data. The [Flow] will emit a single [EpisodeToPodcast] object if a matching
     * episode is found, otherwise, the [Flow] will complete without emitting any values. If multiple
     * Episodes match the uri, the [Flow] will only emit the first.
     * @throws Exception if there is an error querying the database. (e.g. Database is closed)
     */
    override fun episodeAndPodcastWithUri(episodeUri: String): Flow<EpisodeToPodcast> =
        episodesDao.episodeAndPodcast(episodeUri = episodeUri)

    /**
     * Retrieves a list of episodes associated with a specific podcast URI, limited by the specified
     * [Int] parameter [limit].
     *
     * This function fetches episodes from the data source that belong to the podcast identified by
     * [podcastUri]. The results are returned as a [Flow] of [List] of [EpisodeToPodcast] objects
     * allowing for reactive data handling.
     *
     * We just return the [Flow] of [List] of [EpisodeToPodcast] returned by the
     * [EpisodesDao.episodesForPodcastUri] method of our [EpisodesDao] field [episodesDao] when
     * called with its `podcastUri` argument our [String] parameter [podcastUri] and its `limit`
     * argument our [Int] parameter [limit].
     *
     * @param podcastUri The URI (Uniform Resource Identifier) of the podcast for which to retrieve
     * episodes. This string should uniquely identify a podcast within the data source.
     * @param limit The maximum number of episodes to retrieve. If the podcast has fewer episodes
     * than this limit, all available episodes will be returned.
     * @return A [Flow] emitting a [List] of [EpisodeToPodcast] objects. Each [EpisodeToPodcast]
     * object represents an episode associated with the specified podcast. The [Flow] allows for
     * asynchronous and potentially continuous updates to the list of episodes.
     */
    override fun episodesInPodcast(
        podcastUri: String,
        limit: Int
    ): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodesForPodcastUri(podcastUri = podcastUri, limit = limit)
    }

    /**
     * Retrieves a list of episodes associated with a given [List] of podcast URIs, the number
     * returned limited by our [Int] parameter [limit].
     *
     * This function queries the underlying data source to fetch episodes. It provides a reactive
     * [Flow] of results, allowing the caller to observe changes over time.
     *
     * We just return the [Flow] of [List] of [EpisodeToPodcast] returned by the
     * [EpisodesDao.episodesForPodcasts] method of our [EpisodesDao] field [episodesDao] when
     * called with its `podcastUris` argument our [List] of [String] parameter [podcastUris] and its
     * `limit` argument our [Int] parameter [limit].
     *
     * @param podcastUris A list of podcast URIs (e.g., URLs, unique identifiers) to filter episodes
     * by. Episodes will only be included if they are associated with at least one of these URIs.
     * Must not be empty.
     * @param limit The maximum number of episodes to return. If there are more episodes associated
     * with the provided podcasts than the limit, only the [limit] most recent episodes will be
     * returned. Must be a positive integer.
     * @return A [Flow] emitting a list of [EpisodeToPodcast] objects. Each [EpisodeToPodcast]
     * represents an episode and its associated [Podcast]. The emitted list is ordered by the
     * episode's publication date in descending order (most recent first), up to the specified
     * [limit]. The Flow will emit a new list whenever the underlying data changes. If no episodes
     * match the criteria an empty list is emitted.
     * @throws IllegalArgumentException if `podcastUris` is empty or `limit` is not positive.
     */
    override fun episodesInPodcasts(
        podcastUris: List<String>,
        limit: Int
    ): Flow<List<EpisodeToPodcast>> =
        episodesDao.episodesForPodcasts(podcastUris = podcastUris, limit = limit)

    /**
     * Adds a collection of [Episode] objects to the database. This automatically switches to the
     * main thread to maintain thread consistency.
     *
     * This function inserts the provided episodes into the database using the underlying
     * [episodesDao]. It is a suspend function, meaning it can be safely called from a coroutine.
     *
     * We just call the [EpisodesDao.insertAll] method of our [EpisodesDao] field [episodesDao] with
     * its `entities` argument ouf [Collection] of [Episode] parameter [episodes].
     *
     * @param episodes The collection of [Episode] objects to be added to the database.
     */
    override suspend fun addEpisodes(episodes: Collection<Episode>): Unit =
        episodesDao.insertAll(entities = episodes)

    /**
     * Checks if the underlying data source of episodes is empty.
     *
     * This function queries the [episodesDao] to determine the total count of episodes. If the
     * count is zero, it signifies that the data source is empty, and the function returns `true`.
     * Otherwise, if the count is greater than zero, it indicates the presence of episodes, and the
     * function returns `false`.
     *
     * @return `true` if the data source is empty (no episodes), `false` otherwise.
     */
    override suspend fun isEmpty(): Boolean = episodesDao.count() == 0
}
