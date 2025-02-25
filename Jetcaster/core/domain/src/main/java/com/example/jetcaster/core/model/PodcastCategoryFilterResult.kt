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

package com.example.jetcaster.core.model

import com.example.jetcaster.core.domain.PodcastCategoryFilterUseCase

/**
 * A model holding top podcasts and matching episodes when filtering based on a category. It is
 * created by [PodcastCategoryFilterUseCase] and used by the UI to display the filtered content.
 *
 * @property topPodcasts A list of [PodcastInfo] representing the top podcasts in the category.
 * Defaults to an empty list if no top podcasts are found.
 * @property episodes A list of [PodcastToEpisodeInfo] representing episodes related to podcasts
 * within the category. Defaults to an empty list if no episodes are found.
 */
data class PodcastCategoryFilterResult(
    val topPodcasts: List<PodcastInfo> = emptyList(),
    val episodes: List<PodcastToEpisodeInfo> = emptyList()
)
