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

/**
 * Represents information about a library, specifically a collection of podcast episodes.
 *
 * This class holds a [List] of [PodcastToEpisodeInfo] objects, representing the episodes
 * available in the library. It also implements the [List] interface by delegation,
 * meaning it directly exposes the functionality of the underlying [episodes] list.
 *
 * @property episodes The list of [PodcastToEpisodeInfo] objects representing the episodes in the
 * library. Defaults to an empty list if no episodes are provided.
 */
data class LibraryInfo(
    val episodes: List<PodcastToEpisodeInfo> = emptyList()
) : List<PodcastToEpisodeInfo> by episodes
