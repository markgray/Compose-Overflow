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

package com.example.jetcaster.ui.podcasts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * [PodcastsViewModel] is the ViewModel responsible for managing the state and data related to
 * the [PodcastsScreen] screen. It interacts with the [PodcastStore] to retrieve podcast data
 * and exposes the UI state through a [StateFlow].
 *
 * This [ViewModel] fetches a list of followed podcasts, sorts them by their last episode's date,
 * and then transforms them into a format suitable for the UI. It handles different states such as
 * loading, empty, and loaded, emitting the appropriate state through the [uiState] flow.
 *
 * @property podcastStore The data source for podcast information, injected via Hilt.
 */
@HiltViewModel
class PodcastsViewModel @Inject constructor(
    podcastStore: PodcastStore,
) : ViewModel() {

    /**
     * Represents the UI state of the Podcasts screen.
     *
     * This [StateFlow] emits different states based on the loading and availability of followed
     * podcasts. It initially emits [PodcastsScreenState.Loading], then transitions to either:
     *   - [PodcastsScreenState.Loaded]: If followed podcasts are found, containing a list of
     *   [PodcastInfo] objects.
     *   - [PodcastsScreenState.Empty]: If no followed podcasts are found or an error occurs
     *   during loading.
     *
     * The flow is derived from the [Flow] of [List] of [PodcastWithExtraInfo] returned by
     * [PodcastStore.followedPodcastsSortedByLastEpisode] for a `limit` of 10)`, which provides
     * a list of followed podcasts, sorted by the last episode date. The `map` operator transforms
     * the list of [PodcastWithExtraInfo] to a [List] of [PodcastInfo] using [PodcastMapper].
     * The `catch` operator handles potential errors during the data stream, emitting a
     * [PodcastsScreenState.Empty] if one occurs.
     *
     * The [StateFlow] is started eagerly using [SharingStarted.Eagerly] within the [viewModelScope].
     * This ensures that the flow is active and collecting data as soon as the ViewModel is created.
     * The initial value of the flow is set to [PodcastsScreenState.Loading].
     */
    val uiState: StateFlow<PodcastsScreenState> =
        podcastStore.followedPodcastsSortedByLastEpisode(limit = 10)
            .map { podcastList: List<PodcastWithExtraInfo> ->
                if (podcastList.isNotEmpty()) {
                    PodcastsScreenState.Loaded(podcastList = podcastList.map(PodcastMapper::map))
                } else {
                    PodcastsScreenState.Empty
                }
            }.catch {
                emit(value = PodcastsScreenState.Empty)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = PodcastsScreenState.Loading,
            )
}

/**
 * Object responsible for mapping between internal podcast representations ([PodcastWithExtraInfo])
 * and external podcast representations ([PodcastInfo]).
 *
 * This mapper provides a centralized location for data transformation logic related to podcasts,
 * ensuring consistency and maintainability across the application.
 */
object PodcastMapper {

    /**
     * Maps from [PodcastWithExtraInfo] to [PodcastInfo].
     */
    fun map(
        podcastWithExtraInfo: PodcastWithExtraInfo,
    ): PodcastInfo =
        podcastWithExtraInfo.asExternalModel()
}

/**
 * Represents the different states of the [PodcastsScreen] Screen.
 */
@ExperimentalHorologistApi
sealed interface PodcastsScreenState {

    /**
     * Represents the loading state of the Podcasts screen.
     *
     * This state indicates that the application is currently fetching or loading
     * podcast data from a data source (e.g., network, local database).
     * When the screen is in this state, a loading indicator or placeholder
     * will be displayed to inform the user that data is being retrieved.
     *
     * Inherits from [PodcastsScreenState].
     */
    data object Loading : PodcastsScreenState

    /**
     * Represents the state of the Podcasts screen when the podcast list has been successfully
     * loaded.
     *
     * This state contains the [List] of [PodcastInfo] objects retrieved from the data source in
     * its [podcastList].property
     *
     * @property podcastList The [List] of [PodcastInfo] objects that were successfully loaded.
     * This list represents the podcasts to be displayed on the screen.
     */
    data class Loaded(
        val podcastList: List<PodcastInfo>,
    ) : PodcastsScreenState

    /**
     * Represents the state of the Podcasts screen when there are no podcasts to display.
     *
     * This state indicates that either:
     *   - The initial load has occurred and no podcasts were found matching the current criteria.
     *   - A search or filter operation resulted in no matching podcasts.
     *   - There are no podcasts available in the data source.
     */
    data object Empty : PodcastsScreenState
}
