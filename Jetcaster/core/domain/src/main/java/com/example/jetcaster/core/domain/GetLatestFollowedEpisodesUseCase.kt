/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.core.domain

import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Use case responsible for retrieving the latest episodes from followed podcasts.
 *
 * This use case provides a stream of the most recent episodes from the podcasts that
 * the user is following. It fetches followed podcasts, sorts them by the most recent
 * episode's publication date, and then retrieves the latest episodes for each of
 * these podcasts.
 *
 * The number of episodes fetched per podcast is dynamically adjusted based on the number
 * of followed podcasts, ensuring a relevant and up-to-date stream of episodes.
 *
 * @property episodeStore The data store for accessing episodes.
 * @property podcastStore The data store for accessing podcasts and user's followed podcasts.
 */
class GetLatestFollowedEpisodesUseCase @Inject constructor(
    private val episodeStore: EpisodeStore,
    private val podcastStore: PodcastStore,
) {
    /**
     * Retrieves a flow of lists of episodes associated with followed podcasts, sorted by the most
     * recent episode.
     *
     * This function performs the following operations:
     * 1. Fetches the user's followed podcasts from the [podcastStore].
     * 2. Sorts these followed podcasts by their last published episode (most recent first).
     * 3. For each of the followed podcasts, fetches the most recent episodes from the [episodeStore].
     * 4. The number of episodes fetched per podcast is determined by multiplying the number of
     * followed podcasts by 5.
     * 5. Emits a new list of [EpisodeToPodcast] each time the underlying data changes.
     * 6. Uses `flatMapLatest` to ensure that only the most recent set of episodes is emitted.
     * If new followed podcasts are added or the last episode for any podcast changes, any ongoing
     * episode fetching operation is cancelled, and a new one is started, which will emit the latest
     * results.
     *
     * @return A [Flow] that emits lists of [EpisodeToPodcast]. Each list represents the most recent
     * episodes from the user's followed podcasts.
     *
     * @throws Exception If there's an issue fetching data from either the [podcastStore] or
     * [episodeStore].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<EpisodeToPodcast>> =
        podcastStore.followedPodcastsSortedByLastEpisode()
            .flatMapLatest { followedPodcasts ->
                episodeStore.episodesInPodcasts(
                    followedPodcasts.map { it.podcast.uri },
                    followedPodcasts.size * 5
                )
            }
}
