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

import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.model.asPodcastToEpisodeInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 * Use case responsible for filtering and retrieving top podcasts and matching episodes related to
 * a specific [Category].
 *
 * This class encapsulates the logic for fetching podcasts and episodes that belong to a given
 * category. It leverages the [CategoryStore] to interact with the underlying data source and
 * provides a clean, business-oriented interface for retrieving category-specific content.
 *
 * @property categoryStore The data store responsible for providing category-related information.
 */
class PodcastCategoryFilterUseCase @Inject constructor(
    private val categoryStore: CategoryStore
) {
    /**
     * Invokes the logic to filter and retrieve podcasts and episodes related to a specific category.
     *
     * This function retrieves the most recent podcasts and episodes associated with the given
     * `category`. It fetches a limited number of both (10 podcasts and 20 episodes) and
     * combines them into a `PodcastCategoryFilterResult`.
     *
     * @param category The `CategoryInfo` object representing the category to filter by. If `null`,
     * an empty `PodcastCategoryFilterResult` is returned, indicating no category-specific data.
     * @return A `Flow` emitting `PodcastCategoryFilterResult` objects. The `PodcastCategoryFilterResult`
     * contains the top podcasts and episodes related to the category.
     * - `topPodcasts`: A list of the most recent podcasts (up to 10) in the category.
     * - `episodes`: A list of recent episodes (up to 20) from podcasts within the category.
     *
     * @see CategoryInfo
     * @see PodcastCategoryFilterResult
     * @see CategoryStore.podcastsInCategorySortedByPodcastCount
     * @see CategoryStore.episodesFromPodcastsInCategory
     * @see asExternalModel
     * @see asPodcastToEpisodeInfo
     */
    operator fun invoke(category: CategoryInfo?): Flow<PodcastCategoryFilterResult> {
        if (category == null) {
            return flowOf(PodcastCategoryFilterResult())
        }

        val recentPodcastsFlow = categoryStore.podcastsInCategorySortedByPodcastCount(
            category.id,
            limit = 10
        )

        val episodesFlow = categoryStore.episodesFromPodcastsInCategory(
            category.id,
            limit = 20
        )

        // Combine our flows and collect them into the view state StateFlow
        return combine(recentPodcastsFlow, episodesFlow) { topPodcasts, episodes ->
            PodcastCategoryFilterResult(
                topPodcasts = topPodcasts.map { it.asExternalModel() },
                episodes = episodes.map { it.asPodcastToEpisodeInfo() }
            )
        }
    }
}
