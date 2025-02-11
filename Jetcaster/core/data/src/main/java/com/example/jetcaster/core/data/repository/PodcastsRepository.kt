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

import com.example.jetcaster.core.data.Dispatcher
import com.example.jetcaster.core.data.JetcasterDispatchers
import com.example.jetcaster.core.data.database.dao.TransactionRunner
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.network.PodcastRssResponse
import com.example.jetcaster.core.data.network.PodcastsFetcher
import com.example.jetcaster.core.data.network.SampleFeeds
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Repository for managing podcasts, episodes, and categories.
 *
 * This class handles fetching, storing, and updating podcast data from external sources. It uses a
 * combination of local storage (via [PodcastStore], [EpisodeStore], and [CategoryStore]) and network
 * fetching (via [PodcastsFetcher]) to provide a unified interface for accessing podcast-related
 * information.
 *
 * @property podcastsFetcher The component responsible for fetching podcast data from external sources.
 * @property podcastStore The local data store for managing podcast entities.
 * @property episodeStore The local data store for managing episode entities.
 * @property categoryStore The local data store for managing category entities and their relationships
 * with podcasts.
 * @property transactionRunner A utility for running multiple database transactions atomically.
 * @property mainDispatcher The main coroutine dispatcher for running operations on the main thread.
 */
class PodcastsRepository @Inject constructor(
    private val podcastsFetcher: PodcastsFetcher,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val categoryStore: CategoryStore,
    private val transactionRunner: TransactionRunner,
    @Dispatcher(JetcasterDispatchers.Main)
    private val mainDispatcher: CoroutineDispatcher
) {
    /**
     * A [Job] representing the current refresh operation.
     *
     * This property holds a reference to the coroutine job that is performing a refresh.
     * If a refresh is in progress, this job will be active.
     * If no refresh is in progress, this job will be null.
     *
     * This job can be used to:
     *  - Check if a refresh operation is currently running (by checking if it's not null and active).
     *  - Cancel an ongoing refresh operation (by calling `refreshingJob?.cancel()`).
     *  - Wait for the completion of the refresh operation (by calling `refreshingJob?.join()`).
     *
     * When a new refresh operation is started, this property should be updated to hold the
     * new [Job]. When the refresh operation completes (either successfully or with an error),
     * this property should be set to null.
     */
    private var refreshingJob: Job? = null

    /**
     * A [CoroutineScope] tied to the main thread.
     *
     * This scope is used for performing UI-related operations and any other tasks
     * that should be executed on the main thread.  It uses the [mainDispatcher] as its
     * [CoroutineDispatcher].
     *
     * The scope's lifecycle is tied to the object it is a member of. When the parent object
     * is no longer needed the coroutines launched within this scope will be cancelled.
     */
    private val scope = CoroutineScope(context = mainDispatcher)

    /**
     * Updates the list of podcasts in the local store.
     *
     * This function fetches podcasts from a remote source, parses the RSS feeds, and adds
     * the extracted podcast, episode, and category data to the respective stores.
     *
     * The function performs the following actions:
     *  1. **Checks for existing refresh job:** If a refresh job is already active
     *  (`refreshingJob?.isActive == true`), it waits for the current job to complete using
     *  `refreshingJob?.join()`. This ensures that only one refresh operation happens at a time.
     *  2. **Determines if a refresh is needed:** If [force] is `true` or if the podcast store is
     *  empty (`podcastStore.isEmpty()`), it initiates a new refresh operation.
     *  3. **Launches a new coroutine:** It launches a new coroutine within the [CoroutineScope]
     *  field [scope] to perform the podcast fetching and updating in the background.
     *  4. **Fetches and processes podcasts:** It uses `podcastsFetcher` to retrieve podcasts from
     *  the [List] of [String] `feedUrls` field [SampleFeeds]. It filters out any non-successful
     *  responses and maps the successful responses to their contents.
     *  5. **Adds data to stores:** For each successful podcast response, it executes the following
     *  within a transaction:
     *    - Adds the podcast to the `PodcastStore` property `podcastStore`.
     *    - Adds the associated episodes to the `EpisodeStore` property `episodeStore`.
     *    - Adds each category to the `CategoryStore` property `categoryStore` and associates the
     *    podcast with the category.
     *  6. **Waits for completion:** After launching the refresh job, it waits for the job to
     *  complete using `job.join()`. This ensures that the function doesn't return before the
     *  podcast update is finished.
     *
     * Note: This is a suspend function so should be used inside a coroutine. If there is a
     * `refreshingJob` running it will wait for it to finish.
     *
     * @param force If `true`, forces a refresh of the podcasts even if the store is not empty. If
     * `false`, the function only updates the podcasts if the store is empty and no other refresh
     * is already in progress.
     */
    suspend fun updatePodcasts(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || podcastStore.isEmpty()) {
            val job = scope.launch {
                // Now fetch the podcasts, and add each to each store
                podcastsFetcher(feedUrls = SampleFeeds)
                    .filter { it is PodcastRssResponse.Success }
                    .map { it as PodcastRssResponse.Success }
                    .collect { (podcast: Podcast, episodes: List<Episode>, categories: Set<Category>) ->
                        transactionRunner {
                            podcastStore.addPodcast(podcast = podcast)
                            episodeStore.addEpisodes(episodes = episodes)

                            categories.forEach { category: Category ->
                                // First insert the category
                                val categoryId: Long = categoryStore.addCategory(category = category)
                                // Now we can add the podcast to the category
                                categoryStore.addPodcastToCategory(
                                    podcastUri = podcast.uri,
                                    categoryId = categoryId
                                )
                            }
                        }
                    }
            }
            refreshingJob = job
            // We need to wait here for the job to finish, otherwise the coroutine completes ~immediatelly
            job.join()
        }
    }
}
