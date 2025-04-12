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

package com.example.jetcaster.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel that handles the business logic and screen state of the Queue screen.
 *
 * @param episodePlayer The [EpisodePlayer] instance responsible for managing the playback of
 * episodes.
 */
@HiltViewModel
class QueueViewModel @Inject constructor(
    private val episodePlayer: EpisodePlayer,

) : ViewModel() {

    /**
     * Represents the UI state of the queue screen.
     *
     * This property provides a [StateFlow] that emits different states based on the current
     * state of the episode player's queue. It reflects whether the queue is loading, empty,
     * or loaded with episodes.
     *
     * The state is derived from the [EpisodePlayer.playerState] and transformed into a
     * [QueueScreenState] using a mapping function.
     *
     * - **Loading:** Initially, the state is [QueueScreenState.Loading] until the first
     *   emission from the [EpisodePlayer.playerState].
     * - **Empty:** If the queue within the [EpisodePlayer.playerState] is empty, the state
     *   becomes [QueueScreenState.Empty].
     * - **Loaded:** If the queue within the [EpisodePlayer.playerState] is not empty, the state
     *   becomes [QueueScreenState.Loaded] containing the queue of episodes.
     *
     * The [StateFlow] is configured to:
     *  - Be scoped to the [viewModelScope], ensuring it's cancelled when the ViewModel is cleared.
     *  - Use [SharingStarted.WhileSubscribed] which means it will keep collecting the
     *  upstream flow as long as there are subscribers and will stop collecting if there are
     *  no subscribers for 5 seconds.
     *  - Start with an initial state of [QueueScreenState.Loading].
     *
     * Clients can collect this flow to update the UI based on the current state of the queue.
     */
    val uiState: StateFlow<QueueScreenState> = episodePlayer.playerState.map {
        if (it.queue.isNotEmpty()) {
            QueueScreenState.Loaded(it.queue)
        } else {
            QueueScreenState.Empty
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        QueueScreenState.Loading,
    )

    /**
     * Initiates playback of a specified episode.
     *
     * This function sets the provided [episode] as the currently playing episode
     * within the [EpisodePlayer] property [episodePlayer] and then starts the playback.
     *
     * @param episode The [PlayerEpisode] to be played. This object contains the
     * information needed for the player to load and play the episode.
     */
    @Suppress("unused")
    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }

    /**
     * Starts playing a list of episodes.
     *
     * This function takes a list of [PlayerEpisode] objects and initiates playback.
     * It sets the first episode in the list as the current episode and then
     * instructs the episode player to start playing the entire list.
     *
     * @param episodes A list of [PlayerEpisode] objects to be played. The first
     * episode in this list will be set as the [EpisodePlayer.currentEpisode].
     */
    fun onPlayEpisodes(episodes: List<PlayerEpisode>) {
        episodePlayer.currentEpisode = episodes[0]
        episodePlayer.play(episodes)
    }

    /**
     * Removes all episodes from the playback queue.
     *
     * This function clears the current playback queue, effectively removing all episodes
     * that were scheduled to be played.  After calling this function, the playback
     * queue will be empty.
     *
     * This is used when the user wants to completely clear their playback queue, such as
     * when they're starting fresh with a new set of episodes or no longer wish to continue
     * with their current queue.
     */
    fun onDeleteQueueEpisodes() {
        episodePlayer.removeAllFromQueue()
    }
}

/**
 * Represents the state of the queue screen.
 * This sealed interface encapsulates the different possible states of the queue:
 *  - Loading: Indicates that the queue is currently being loaded.
 *  - Loaded: Indicates that the queue has been successfully loaded and contains a list of [PlayerEpisode].
 *  - Empty: Indicates that the queue is empty.
 */
@ExperimentalHorologistApi
sealed interface QueueScreenState {

    /**
     * Represents the loading state of the queue screen.
     *
     * This object indicates that the queue screen is currently in the process of loading
     * data, such as the list of items or other necessary information.  It signifies that
     * the screen is not yet ready to display content and is actively fetching or preparing it.
     *
     * This is a concrete implementation of the [QueueScreenState] sealed interface.
     * When the `Loading` state is active, UI elements like progress indicators or loading
     * animations should be displayed to inform the user.
     *
     * @see QueueScreenState
     */
    data object Loading : QueueScreenState

    /**
     * Represents the state of the queue screen when the episode list has been successfully loaded.
     *
     * This state indicates that the data representing the episodes that can be played has been
     * retrieved and is ready to be displayed or used by the queue screen.
     *
     * @property episodeList The list of [PlayerEpisode] objects representing the episodes
     * loaded for the queue. This list should contain all the details necessary to displa
     * y and control each episode in the queue.
     */
    data class Loaded(
        val episodeList: List<PlayerEpisode>
    ) : QueueScreenState

    /**
     * Represents the state of the queue screen when the queue is empty.
     *
     * This state indicates that there are no items currently in the queue to be displayed.
     * It is a concrete implementation of the [QueueScreenState] sealed interface.
     */
    data object Empty : QueueScreenState
}
