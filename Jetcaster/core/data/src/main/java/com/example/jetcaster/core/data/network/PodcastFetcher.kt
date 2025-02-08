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
    private val cacheControl: CacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }

    /**
     * Invokes the fetching and parsing process for a list of podcast RSS feed URLs.
     *
     * This function takes a list of RSS feed URLs, fetches the content of each URL,
     * and parses it to extract podcast information. It uses concurrent fetching
     * and parsing to improve efficiency. If is called from the [PodcastsRepository.updatePodcasts]
     * method.
     *
     * Code explanation:
     *  1. operator fun invoke(feedUrls: [List]<[String]>): [Flow]<[PodcastRssResponse]>
     *    - This defines an invoke operator function. In Kotlin, invoke allows you to call an object
     *    as if it were a function. In this case, it means you can call this function directly on an
     *    instance of the class it's defined in, without explicitly naming the function.
     *    - `feedUrls: List<String>`: This is the input parameter, a list of strings, where each string
     *    is an URL to a podcast's RSS feed.
     *    - [Flow]<[PodcastRssResponse]>: This is the return type. It indicates that the function
     *    returns a [Flow], which is a Kotlin Coroutines concept for asynchronous streams of data.
     *    Each item emitted by this flow will be a [PodcastRssResponse].
     *    - [PodcastRssResponse.Success]: Indicates that the feed was successfully fetched and parsed.
     *    - [PodcastRssResponse.Error]: Indicates that an error occurred during the process.
     *  2. feedUrls.asFlow(): This converts the input [List]<[String]> of feed URLs into a
     *  [Flow]<[String]>. This allows you to use Kotlin [Flow] operators on the list.
     *  3. `flatMapMerge { feedUrl: String -> ... }`: This is the core of the concurrent processing.
     *  flatMapMerge is a powerful operator that does the following:
     *    - It takes each feedUrl emitted by the upstream flow (feedUrls.asFlow()).
     *    - For each feedUrl, it launches a new coroutine to process it.
     *    - It then merges the results from all these coroutines into a single output flow.
     *    - The flatMapMerge operator allows for concurrent execution of the inner flows. The number
     *    of concurrent flows is limited by the concurrency parameter, which defaults to the number
     *    of available processors.
     *    - In essence, it's like saying, "For each feed URL, start a separate task to fetch and
     *    parse it, and then combine all the results into one stream." This is the part that makes
     *    the fetching and parsing concurrent.
     *  4. `flow { ... }`: This creates a new [Flow] within the flatMapMerge lambda. This inner flow
     *  is responsible for fetching and parsing a single feed.
     *    - `emit(fetchPodcast(url = feedUrl))`: This line does the actual work.
     *    - `fetchPodcast(url = feedUrl)`: This is a suspend function that fetches and parses the
     *    content of the RSS feed at the given `feedUrl` and returns a [PodcastRssResponse], either
     *    an [PodcastRssResponse.Success] or an [PodcastRssResponse.Error].
     *  5. `catch { e: Throwable -> ... }`: This is an error-handling operator. If an error occurs
     *  it will be caught here, and the flow will emit a [PodcastRssResponse.Error] with the
     *  exception that occurred.
     *
     * @param feedUrls A list of strings, where each string is a URL pointing to a podcast's RSS feed.
     * @return A [Flow] of [PodcastRssResponse] objects. Each emission represents the result of
     * fetching and parsing a single RSS feed. The flow may emit:
     *  - [PodcastRssResponse.Success]: If the fetch and parsing were successful.
     *  - [PodcastRssResponse.Error]: If an error occurred during fetching or parsing.
     *
     * The flow completes when all feeds have been processed.
     *
     * @throws Exception If any of the provided feedUrls are invalid.
     *
     * @OptIn(ExperimentalCoroutinesApi::class) This function uses experimental coroutines APIs for
     * concurrent flow processing.
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
     * Code explanation:
     *  1. `withContext(context = ioDispatcher) { ... }`: This is a suspending function that
     *  allows you to change the coroutine context for a specific block of code.
     *    - `context = ioDispatcher`: This specifies that the code within the lambda { ... } should
     *    run on the ioDispatcher. This is a dispatcher designed for I/O-bound operations, such as
     *    network requests and file system access.
     *  2. `val request: Request = Request.Builder() ... .build()`: This code uses the OkHttp library
     *  to create an HTTP request.
     *    - `Request.Builder()`: Starts building a new request.
     *    - `.url(url = url)`: Sets the URL of the request to the url parameter passed to the function.
     *    - `.cacheControl(cacheControl = cacheControl)`: Configures the caching behavior of the
     *    request using our [CacheControl] variable [cacheControl]. This allows the function to
     *    potentially use cached responses instead of always making a network request.
     *    - `.build()`: Finalizes the request configuration and creates the [Request] object.
     *  3. `val response: Response = okHttpClient.newCall(request = request).execute()`:
     *    - `okHttpClient`: This is the singleton [OkHttpClient] instance injected by Hilt.
     *    - `.newCall(request = request)`: Creates a new call object for the `Request` variable
     *    `request`.
     *    - `.execute()`: Executes the request synchronously. This means the current coroutine will
     *    be paused until the response is received.
     *  4. `if (!response.isSuccessful) throw HttpException(response = response)`: This checks if the
     *  HTTP response was successful (the status code is in the 2xx range).
     *    - `response.isSuccessful`: A boolean property indicating whether the response was successful.
     *    - `throw HttpException(response = response)`: If the response was not successful, an
     *    HttpException is thrown, providing details about the failed response.
     *  5. `response.body!!.use { body: ResponseBody -> ... }`:
     *    - Accesses the response body. The !! is the not-null assertion operator, which means the
     *    code assumes the response body is not `null`. If it is `null`, an `IllegalStateException`
     *    will be thrown.
     *    - `.use { ... }`: This is a Kotlin standard library function that ensures the `ResponseBody`
     *    is properly closed after it's used, even if exceptions occur. This is important for
     *    releasing resources.
     *    - `body: ResponseBody`: The `ResponseBody` is the content of the HTTP response.
     *  6. `syndFeedInput.build(body.charStream()).toPodcastResponse(feedUrl = url)`:
     *    - `syndFeedInput`: This is the instance of `SyndFeedInput` that is injected by Hilt. It
     *    comes from the Rome library, which is used for parsing RSS and Atom feeds.
     *    - `.build(body.charStream())`: Parses the RSS feed from the response body's character stream.
     *    - .toPodcastResponse(feedUrl = url): This our custom extension function [toPodcastResponse]
     *    that converts the parsed `SyndFeed` object (from Rome) into a `PodcastRssResponse` object.
     *    It also includes the original feed URL in the `PodcastRssResponse`.
     *
     * @param url The URL of the podcast's RSS feed.
     * @return A [PodcastRssResponse] object containing the parsed podcast data.
     * @throws HttpException If the network request fails (e.g., non-2xx response code).
     * @throws Exception If there is an error during the parsing process (e.g., malformed XML).
     * @throws IllegalStateException if the response body is null
     *
     * @see PodcastRssResponse
     * @see OkHttpClient
     * @see CacheControl
     * @see HttpException
     * @see SyndFeed
     * @see SyndFeedInput
     * @see toPodcastResponse
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
 * Represents the response from fetching a podcast's RSS feed. This sealed class encapsulates either
 * a successful response containing podcast data or an error.
 */
sealed class PodcastRssResponse {

    /**
     * Represents an error state in a Podcast RSS response.
     *
     * This class encapsulates an optional [Throwable] that describes the error
     * that occurred during the processing or retrieval of the Podcast RSS feed.
     *
     * @property throwable The [Throwable] representing the error, or null if no specific error was
     * encountered. This can be used to obtain more detailed information about the cause of the
     * error, such as the exception type, error message, and stack trace.
     */
    data class Error(        
        val throwable: Throwable?,
    ) : PodcastRssResponse()

    /**
     * Represents a successful response containing podcast information, episodes, and categories.
     *
     * This data class encapsulates the successful outcome of a podcast RSS feed request. It holds
     * the main podcast details, a list of associated episodes, and a set of relevant categories.
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
 * First we initialize our [String] variable `val podcastUri` with the [SyndFeed.getUri] value of
 * our [SyndFeed] receiver, or if this is `null`, we use our [String] parameter [feedUrl]. Then
 * we initialize our [List] of [Episode] variable `val episodes` with the results of applying the
 * [List.map] extension function to the [SyndFeed.getEntries] property of our [SyndFeed] receiver,
 * and in the `transform` lambda we call our [SyndEntry.toEpisode] extension function on each
 * [SyndEntry] in the [SyndFeed] feed to convert it to an [Episode]. We initialize our
 * [FeedInformation] variable `val feedInfo` with the value of the [SyndFeed.getModule] property
 * of our [SyndFeed] receiver using our [String] constant [PodcastModuleDtd] as the module URI, and
 * if this is not `null`, we cast it to a [FeedInformation]. We initialize our [Podcast] variable
 * `val podcast` to be a new instance of [Podcast] with the following properties:
 *  - `uri = podcastUri`: The URI of the podcast.
 *  - `title = title`: The title of the podcast.
 *  - `description = feedInfo?.summary ?: description`: The description of the podcast, using the
 *  [FeedInformation.getSummary] property if available, or the [SyndFeed.getDescription] property.
 *  - `author = author`: The author of the podcast.
 *  - `copyright = copyright`: The copyright information of the podcast.
 *  - `imageUrl = feedInfo?.imageUri?.toString()`: The URL of the podcast's image, if available in
 *  the [FeedInformation.getImageUri] property.
 *
 * We initialize our [Set] of [Category] variable `val categories` with the results of applying
 * the [List.map] extension function to the [FeedInformation.getCategories] property of our [SyndFeed]
 * receiver, and in the `transform` lambda we create a new [Category] instance for each [Category.name]
 * in the [List], then use the [List.toSet] extension function to convert the [List] of [Category]
 * to a [Set] of [Category], or if anything in the chain is `null`, we return an empty [Set].
 *
 * Finally, we return a [PodcastRssResponse.Success] instance with the following properties:
 *  - `podcast = podcast`: The [Podcast] instance we initialized earlier.
 *  - `episodes = episodes`: The [List] of [Episode] instances we initialized earlier.
 *  - `categories = categories`: The [Set] of [Category] instances we initialized earlier.
 *
 * @param feedUrl The original URL from which the SyndFeed was fetched. This is used
 * as a fallback for the podcast URI if the feed itself doesn't contain a specific URI.
 * @return A [PodcastRssResponse.Success] object containing the parsed podcast information,
 * including podcast details, episodes, and categories.
 *
 * @throws Exception If there are issues accessing elements within the SyndFeed, or if casting is
 * not possible.
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
 * including the episode's URI, podcast URI, title, author, summary, subtitle, published date, and
 * duration.
 *
 * First we initialize our [EntryInformation]
 *
 * @param podcastUri The URI of the podcast to which this episode belongs. This will be used to
 * associate the episode with its podcast.
 * @return An [Episode] object representing the data extracted from the [SyndEntry].
 * @throws IllegalArgumentException if the published date is null, as it's a required field.
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
 * Most feeds use the following DTD to include extra information related to their podcast. Info such
 * as images, summaries, duration, categories is sometimes only available via this attributes in
 * this DTD.
 */
private const val PodcastModuleDtd = "http://www.itunes.com/dtds/podcast-1.0.dtd"
