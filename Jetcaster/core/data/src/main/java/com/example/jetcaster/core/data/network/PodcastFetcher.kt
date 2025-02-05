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

package com.example.jetcaster.core.data.network

import coil.network.HttpException
import com.example.jetcaster.core.data.Dispatcher
import com.example.jetcaster.core.data.JetcasterDispatchers
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.rometools.modules.itunes.EntryInformation
import com.rometools.modules.itunes.FeedInformation
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * A class which fetches some selected podcast RSS feeds. Injected via Hilt to construct a
 * [PodcastsRepository].
 *
 * @param okHttpClient [OkHttpClient] to use for network requests injected by Hilt.
 * @param syndFeedInput [SyndFeedInput] to use for parsing RSS feeds injected by Hilt.
 * @param ioDispatcher [CoroutineDispatcher] to use for running fetch requests injected by Hilt.
 */
class PodcastsFetcher @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val syndFeedInput: SyndFeedInput,
    @Dispatcher(JetcasterDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * It seems that most podcast hosts do not implement HTTP caching appropriately.
     * Instead of fetching data on every app open, we instead allow the use of 'stale'
     * network responses (up to 8 hours).
     */
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }

    /**
     * Returns a [Flow] which fetches each podcast feed and emits it in turn.
     *
     * The feeds are fetched concurrently, meaning that the resulting emission order may not
     * match the order of [feedUrls].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(feedUrls: List<String>): Flow<PodcastRssResponse> {
        // We use flatMapMerge here to achieve concurrent fetching/parsing of the feeds.
        return feedUrls.asFlow()
            .flatMapMerge { feedUrl: String ->
                flow {
                    emit(fetchPodcast(url = feedUrl))
                }.catch { e: Throwable ->
                    // If an exception was caught while fetching the podcast, wrap it in
                    // an Error instance.
                    emit(value = PodcastRssResponse.Error(throwable = e))
                }
            }
    }

    /**
     * Fetches a podcast's RSS feed from a given URL and parses it into a [PodcastRssResponse] object.
     *
     * This function performs a network request to retrieve the RSS feed, handles caching, and parses
     * the XML content into a structured format. It also manages potential network and parsing errors.
     *
     * @param url The URL of the podcast's RSS feed.
     * @return A [PodcastRssResponse] object containing the parsed podcast data.
     * @throws HttpException If the network request fails (e.g., non-2xx response code).
     * @throws Exception If there is an error during the parsing process (e.g., malformed XML).
     * @throws IllegalStateException if the response body is null
     */
    private suspend fun fetchPodcast(url: String): PodcastRssResponse {
        return withContext(context = ioDispatcher) {
            val request: Request = Request.Builder()
                .url(url = url)
                .cacheControl(cacheControl = cacheControl)
                .build()

            val response: Response = okHttpClient.newCall(request = request).execute()

            // If the network request wasn't successful, throw an exception
            if (!response.isSuccessful) throw HttpException(response = response)

            // Otherwise we can parse the response using a Rome SyndFeedInput, then map it
            // to a Podcast instance. We run this on the IO dispatcher since the parser is reading
            // from a stream.
            response.body!!.use { body: ResponseBody ->
                syndFeedInput.build(body.charStream()).toPodcastResponse(feedUrl = url)
            }
        }
    }
}

/**
 * Represents the response from fetching a podcast's RSS feed.
 * This sealed class encapsulates either a successful response containing podcast data or an error.
 */
sealed class PodcastRssResponse {

    /**
     * Represents an error state in a Podcast RSS response.
     *
     * This class encapsulates an optional [Throwable] that describes the error
     * that occurred during the processing or retrieval of the Podcast RSS feed.
     *
     * @property throwable The [Throwable] representing the error, or null if no specific
     *                    error was encountered. This can be used to obtain more detailed
     *                    information about the cause of the error, such as the exception
     *                    type, error message, and stack trace.
     */
    data class Error(        
        val throwable: Throwable?,
    ) : PodcastRssResponse()

    /**
     * Represents a successful response containing podcast information, episodes, and categories.
     *
     * This data class encapsulates the successful outcome of a podcast RSS feed request.
     * It holds the main podcast details, a list of associated episodes, and a set of relevant categories.
     *
     * @property podcast The main [Podcast] information.
     * @property episodes A list of [Episode] objects associated with the podcast.
     * @property categories A set of [Category] objects representing the podcast's categories.
     */
    data class Success(
        val podcast: Podcast,
        val episodes: List<Episode>,
        val categories: Set<Category>
    ) : PodcastRssResponse()
}


/**
 * Converts a [SyndFeed] object (from Rome library) to a [PodcastRssResponse].
 *
 * This function parses a [SyndFeed] representing an RSS podcast feed and extracts
 * relevant information to create a structured response object ([PodcastRssResponse]).
 * It handles details like podcast metadata, episodes, and categories.
 *
 * @param feedUrl The original URL from which the SyndFeed was fetched. This is used
 *                as a fallback for the podcast URI if the feed itself doesn't
 *                contain a specific URI.
 * @return A [PodcastRssResponse.Success] object containing the parsed podcast information,
 *         including podcast details, episodes, and categories.
 *
 * @throws Exception If there are issues accessing elements within the SyndFeed, or if casting is not possible.
 *
 * @see SyndFeed
 * @see PodcastRssResponse
 * @see Episode
 * @see Podcast
 * @see Category
 * @see FeedInformation
 */
private fun SyndFeed.toPodcastResponse(feedUrl: String): PodcastRssResponse {
    val podcastUri: String = uri ?: feedUrl
    val episodes: List<Episode> = entries.map { it.toEpisode(podcastUri = podcastUri) }

    val feedInfo: FeedInformation? = getModule(PodcastModuleDtd) as? FeedInformation
    val podcast = Podcast(
        uri = podcastUri,
        title = title,
        description = feedInfo?.summary ?: description,
        author = author,
        copyright = copyright,
        imageUrl = feedInfo?.imageUri?.toString()
    )

    val categories: Set<Category> = feedInfo?.categories
        ?.map { Category(name = it.name) }
        ?.toSet() ?: emptySet()

    return PodcastRssResponse.Success(
        podcast = podcast,
        episodes = episodes,
        categories = categories
    )
}


/**
 * Converts a [SyndEntry] object from a podcast feed into an [Episode] object.
 *
 * This function extracts relevant information from a [SyndEntry] and constructs an [Episode] object,
 * including the episode's URI, podcast URI, title, author, summary, subtitle, published date, and duration.
 *
 * @param podcastUri The URI of the podcast to which this episode belongs.
 *                   This will be used to associate the episode with its podcast.
 * @return An [Episode] object representing the data extracted from the [SyndEntry].
 * @throws IllegalArgumentException if the published date is null, as it's a required field.
 *
 * Example:
 * ```kotlin
 *  val syndEntry: SyndEntry = // ... get SyndEntry from feed
 *  val podcastUri = "https://example.com/podcast"
 *  val episode = syndEntry.toEpisode(podcastUri)
 *  println(episode)
 * ```
 */
private fun SyndEntry.toEpisode(podcastUri: String): Episode {
    val entryInformation = getModule(PodcastModuleDtd) as? EntryInformation
    return Episode(
        uri = uri,
        podcastUri = podcastUri,
        title = title,
        author = author,
        summary = entryInformation?.summary ?: description?.value,
        subtitle = entryInformation?.subtitle,
        published = Instant.ofEpochMilli(publishedDate.time).atOffset(ZoneOffset.UTC),
        duration = entryInformation?.duration?.milliseconds?.let { Duration.ofMillis(it) }
    )
}

/**
 * Most feeds use the following DTD to include extra information related to
 * their podcast. Info such as images, summaries, duration, categories is sometimes only available
 * via this attributes in this DTD.
 */
private const val PodcastModuleDtd = "http://www.itunes.com/dtds/podcast-1.0.dtd"
