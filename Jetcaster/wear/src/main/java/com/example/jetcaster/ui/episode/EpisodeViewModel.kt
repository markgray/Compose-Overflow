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

package com.example.jetcaster.ui.episode

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

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.Episode
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel that handles the business logic and screen state of the Episode screen.
 *
 * @param savedStateHandle handle to saved state passed down to [ViewModel]. It is a key-value map
 * that will let you write and retrieve objects to and from the saved state. These values will
 * persist after the process is killed by the system and remain available via the same object.
 * @param episodeStore [EpisodeStore] for managing Episode instances and their relationships with
 * Podcast instances.
 * @param episodePlayer an episode player which defines high-level functions such as queuing
 * episodes, playing an episode, pausing, seeking, etc.
 */
@HiltViewModel
class EpisodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    /**
     * The URI of the episode, decoded from the saved state handle.
     *
     * This property holds the URI of the episode that this ViewModel is associated with.
     * It's retrieved from the [SavedStateHandle] using the key [Episode.EPISODE_URI].
     * The retrieved URI string, if present, is then decoded using [Uri.decode] to handle
     * any URL-encoded characters. If no URI is found in the saved state handle, this property
     * will be `null`.
     *
     * This is used for identifying and accessing the specific episode's data or resources.
     */
    private val episodeUri: String? =
        savedStateHandle.get<String>(Episode.EPISODE_URI).let {
            Uri.decode(it)
        }

    /**
     * A [StateFlow] that emits the [EpisodeToPodcast] associated with the [episodeUri], if provided.
     *
     * This flow is derived from the [EpisodeStore]. If an [episodeUri] is available, it fetches
     * the corresponding episode and podcast data using [EpisodeStore.episodeAndPodcastWithUri].
     * Otherwise, it emits a single null value.
     *
     * The flow is configured to:
     *  - Share its value with multiple collectors.
     *  - Start collecting immediately when the first collector subscribes.
     *  - Keep the shared state active as long as there are subscribers.
     *  - Stop sharing after a 5-second timeout of no subscribers.
     *  - Emit null as the initial value before any data is available.
     *
     * The [Flow.stateIn] operator is used to convert the flow into a [StateFlow].
     *
     * The flow will emit:
     * - [EpisodeToPodcast]: when the [episodeUri] is not null and the data exists in the store.
     * - `null` : when the [episodeUri] is `null` or if the Episode is not found.
     */
    private val episodeFlow: StateFlow<EpisodeToPodcast?> = if (episodeUri != null) {
        episodeStore.episodeAndPodcastWithUri(episodeUri = episodeUri)
    } else {
        flowOf(value = null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = null
    )

    /**
     * Represents the UI state of the episode screen.
     *
     * This [StateFlow] emits updates about the loading, empty, or loaded state of the episode data.
     *
     * It is derived from the [episodeFlow] and transforms it into one of the following states:
     *  - [EpisodeScreenState.Loading]: The initial state, indicating that the episode data
     *  is being fetched.
     *  - [EpisodeScreenState.Empty]: Emitted when the [episodeFlow] is null, meaning no episode
     *  data is available.
     *  - [EpisodeScreenState.Loaded]: Emitted when the [episodeFlow] emits a non-`null` episode,
     *  providing the actual episode data.
     *
     * The [StateFlow] is configured with:
     *  - `scope`: [viewModelScope] to tie its lifecycle to the ViewModel.
     *  - `started`: [SharingStarted.WhileSubscribed] for a `stopTimeoutMillis` of 5_000 to keep
     *  the flow active as long as there are active subscribers, and for 5 seconds after the last
     *  subscriber disappears. This helps in retaining data for short configuration changes.
     *  - `initialValue`: [EpisodeScreenState.Loading] to immediately show a loading state while
     *  the data is being fetched.
     *
     * Use this [StateFlow] in your UI to update the screen based on the current state of
     * the episode data.
     */
    val uiState: StateFlow<EpisodeScreenState> =
        episodeFlow.map {
            if (it != null) {
                EpisodeScreenState.Loaded(it)
            } else {
                EpisodeScreenState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = EpisodeScreenState.Loading,
        )

    /**
     * Initiates playback of a specified episode.
     *
     * This function sets the given [episode] as the currently active episode in the
     * [episodePlayer] and then starts playback.
     *
     * @param episode The [PlayerEpisode] object representing the episode to be played.
     * @throws IllegalStateException if the episodePlayer is not initialized or is in
     * an invalid state.
     * @see PlayerEpisode
     * @see EpisodePlayer
     */
    fun onPlayEpisode(episode: PlayerEpisode) {
        episodePlayer.currentEpisode = episode
        episodePlayer.play()
    }
    
    /**
     * Adds a [PlayerEpisode] to the playback queue.
     *
     * This function takes a [PlayerEpisode] as input and delegates the task of adding it to the
     * underlying player's queue to the [EpisodePlayer] property [episodePlayer]. This allows for
     * queuing up multiple episodes for sequential playback.
     *
     * @param episode The [PlayerEpisode] to be added to the queue.
     */
    fun addToQueue(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode = episode)
    }
}

/**
 * Represents the different states of the Episode screen.
 *
 * This sealed interface defines the possible states for an Episode screen, including:
 * - Loading: When the episode data is being fetched.
 * - Loaded: When the episode data has been successfully loaded.
 * - Empty: When there is no episode data to display.
 *
 * @see EpisodeToPodcast
 */
@ExperimentalHorologistApi
sealed interface EpisodeScreenState {

    /**
     * Represents the loading state of the Episode screen.
     *
     * This object signifies that the Episode screen is currently in a loading state,
     * typically while fetching data from a remote source or performing some initialization process.
     * No episode data is available in this state, and the UI should reflect this by displaying a
     * loading indicator or placeholder content.
     *
     * This is one of the possible states of the [EpisodeScreenState] sealed interface/class.
     */
    data object Loading : EpisodeScreenState

    /**
     * Represents the state of the Episode screen when the data has been successfully loaded.
     *
     * This state holds the [EpisodeToPodcast] object, which contains all the necessary
     * information to display the episode details, such as title, description, audio URL,
     * and podcast information.
     *
     * @property episode The [EpisodeToPodcast] object containing the loaded episode data.
     */
    data class Loaded(
        val episode: EpisodeToPodcast
    ) : EpisodeScreenState

    /**
     * Represents the state of the episode screen when there is no data to display.
     *
     * This state indicates that either:
     *  - The episode data has not yet been loaded.
     *  - The episode data failed to load.
     *  - There are no episodes available to display.
     *
     * It is typically used as a placeholder or an initial state in the UI before
     * actual episode data becomes available. When in this state the UI should generally
     * display a loading indicator or a message informing the user that no episodes
     * are currently available.
     */
    data object Empty : EpisodeScreenState
}
