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

package com.example.jetcaster.tv.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Library screen, responsible for managing the UI state
 * and interacting with the data layer to fetch and update podcast and episode information.
 *
 * This ViewModel fetches the list of followed podcasts and their latest episodes,
 * combines them into a single UI state, and exposes it to the UI layer. It also
 * handles user actions like playing an episode.
 *
 * @property podcastsRepository Repository for fetching and updating podcast information.
 * @property episodeStore Data store for accessing and managing episode data.
 * @property podcastStore Data store for accessing and managing podcast data, including followed podcasts.
 * @property episodePlayer Component responsible for playing audio episodes.
 */
@HiltViewModel
class LibraryScreenViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val episodeStore: EpisodeStore,
    podcastStore: PodcastStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val followingPodcastListFlow: Flow<List<PodcastInfo>> =
        podcastStore.followedPodcastsSortedByLastEpisode().map { list: List<PodcastWithExtraInfo> ->
            list.map { it.asExternalModel() }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val latestEpisodeListFlow: Flow<EpisodeList> = podcastStore
        .followedPodcastsSortedByLastEpisode()
        .flatMapLatest { podcastList: List<PodcastWithExtraInfo> ->
            if (podcastList.isNotEmpty()) {
                combine(podcastList.map { episodeStore.episodesInPodcast(it.podcast.uri, 1) }) {
                    it.map { episodes ->
                        episodes.first()
                    }
                }
            } else {
                flowOf(value = emptyList())
            }
        }.map { list: List<EpisodeToPodcast> ->
            EpisodeList(member = list.map { it.toPlayerEpisode() })
        }

    val uiState: StateFlow<LibraryScreenUiState> =
        combine(
            flow = followingPodcastListFlow,
            flow2 = latestEpisodeListFlow
        ) { podcastList: List<PodcastInfo>, episodeList: EpisodeList ->
            if (podcastList.isEmpty()) {
                LibraryScreenUiState.NoSubscribedPodcast
            } else {
                LibraryScreenUiState.Ready(
                    subscribedPodcastList = podcastList,
                    latestEpisodeList = episodeList
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryScreenUiState.Loading
        )

    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }

    fun playEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode = playerEpisode)
    }
}

sealed interface LibraryScreenUiState {
    data object Loading : LibraryScreenUiState
    data object NoSubscribedPodcast : LibraryScreenUiState
    data class Ready(
        val subscribedPodcastList: PodcastList,
        val latestEpisodeList: EpisodeList,
    ) : LibraryScreenUiState
}
