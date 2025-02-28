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

package com.example.jetcaster.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.domain.FilterableCategoriesUseCase
import com.example.jetcaster.core.domain.PodcastCategoryFilterUseCase
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.LibraryInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.PodcastToEpisodeInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.model.asPodcastToEpisodeInfo
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.model.PlayerEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlin.enums.EnumEntries

/**
 * [ViewModel] for the Home screen.
 *
 * This ViewModel is responsible for managing the state of the Home screen, including:
 * - Fetching and displaying featured podcasts.
 * - Handling user interactions with the Home screen
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    /**
     * Repository for managing podcasts, episodes, and categories.
     */
    private val podcastsRepository: PodcastsRepository,
    /**
     * The store for managing and accessing podcast data.
     */
    private val podcastStore: PodcastStore,
    /**
     * The store for managing [Episode] instances and their relationships with [Podcast] instances.
     */
    private val episodeStore: EpisodeStore,
    /**
     * Use case responsible for filtering and retrieving top podcasts and matching episodes related
     * to a specific category.
     */
    private val podcastCategoryFilterUseCase: PodcastCategoryFilterUseCase,
    /**
     * Use case responsible for providing a stream of [FilterableCategoriesModel] which represents
     * a list of categories that can be used for filtering, along with the currently selected
     * category.
     */
    private val filterableCategoriesUseCase: FilterableCategoriesUseCase,
    /**
     * Instance of [EpisodePlayer] used for playing episodes.
     */
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {
    /**
     * [MutableStateFlow] that holds the currently selected podcast from the user's library.
     *
     * This property is used to track which podcast the user has selected for actions such as:
     *   - Displaying detailed information about the podcast.
     *   - Playing episodes from the selected podcast.
     *   - Managing the podcast's subscription status.
     *   - Etc.
     *
     * The value is `null` if no podcast is currently selected.
     * When a user selects a podcast from their library, this state should be updated to
     * reflect that selection. When the user deselects the podcast or navigates away from the
     * library view, this state should be set back to `null` or a suitable default.
     *
     * The type of the stored value is [PodcastInfo], which presumably contains the podcast's
     * metadata (title, author, description, etc.).
     */
    private val selectedLibraryPodcast: MutableStateFlow<PodcastInfo?> =
        MutableStateFlow(value = null)

    /**
     * The currently selected category in the home screen.
     *
     * This property holds the [HomeCategory] that the user has selected or is currently viewing in
     * the home screen. It is a [MutableStateFlow] which allows for observing changes to the
     * selected category.
     *
     * The initial value is set to [HomeCategory.Discover], meaning the "Discover" category is
     * selected by default.
     *
     * Changes to this flow will trigger UI updates to display content relevant to the selected
     * category.
     */
    private val selectedHomeCategory: MutableStateFlow<HomeCategory> =
        MutableStateFlow(value = HomeCategory.Discover)

    /**
     * Holds the currently available home categories. Represents the available categories for the
     * "home" section of the app. This is an enum class, providing a type-safe way to handle
     * category options.
     */
    private val homeCategories: MutableStateFlow<EnumEntries<HomeCategory>> =
        MutableStateFlow(value = HomeCategory.entries)

    /**
     * The currently selected category.
     *
     * This is a mutable state flow that emits the [CategoryInfo] object representing the
     * currently selected category. If no category is selected, it emits `null`.
     *
     * Changes to this state flow trigger updates in the UI or other components that
     * observe it.  It is primarily used internally to manage the state of the selected
     * category within the class.
     */
    private val _selectedCategory: MutableStateFlow<CategoryInfo?> =
        MutableStateFlow(value = null)

    /**
     * The internal mutable state flow that holds the UI state for the HomeScreen.
     *
     * This is a private property, meaning it's only accessible within this class.
     * It's used to manage the internal state of the HomeScreen's UI and is exposed
     * externally as a read-only [StateFlow] through the [state] property.
     *
     * Changes to the UI state should be made by updating this `_state` flow.
     *
     * Initial Value:
     * - It starts with a default state of [HomeScreenUiState] which represents the initial
     *   state of the HomeScreen.
     *
     * Thread Safety:
     * - [MutableStateFlow] is thread-safe, so it can be updated from any thread.
     *
     * @see HomeScreenUiState
     * @see state
     */
    private val _state: MutableStateFlow<HomeScreenUiState> =
        MutableStateFlow(value = HomeScreenUiState())

    /**
     * Indicates whether a refresh operation is currently in progress.
     *
     * This property is a [MutableStateFlow] that emits `true` while a refresh is ongoing,
     * and `false` when the refresh is complete or not active.
     *
     * It can be used to:
     *   - Display a loading indicator (e.g., a progress bar) during a refresh.
     *   - Disable user interactions (e.g., button clicks) to prevent conflicts during refresh.
     *   - Trigger UI updates when the refresh state changes.
     *
     * The initial value is `false`, meaning no refresh is active upon creation.
     */
    private val refreshing: MutableStateFlow<Boolean> =
        MutableStateFlow(value = false)

    /**
     * A [SharedFlow] that provides a list of the 10 most recently updated podcasts that the user
     * follows. Here's what happens:
     * 1. The podcastStore fetches and sorts the followed podcasts.
     * 2. The shareIn operator transforms this into a SharedFlow.
     * 3. The viewModelScope ensures that the flow's operations are tied to the ViewModel's lifecycle.
     * 4. SharingStarted.WhileSubscribed() ensures that the upstream flow only runs when there are
     * active collectors, saving resources when no one is listening.
     */
    private val subscribedPodcasts: SharedFlow<List<PodcastWithExtraInfo>> =
        podcastStore.followedPodcastsSortedByLastEpisode(limit = 10)
            .shareIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed())

    /**
     * Represents the current UI state of the Home Screen.
     * It is a [StateFlow] that emits updates whenever the UI state changes.
     *
     * This state flow is used to drive the UI rendering and reflect
     * the current status of data loading, content display, and any potential errors.
     */
    val state: StateFlow<HomeScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            com.example.jetcaster.core.util.combine(
                flow = homeCategories,
                flow2 = selectedHomeCategory,
                flow3 = subscribedPodcasts,
                flow4 = refreshing,
                flow5 = _selectedCategory.flatMapLatest { selectedCategory: CategoryInfo? ->
                    filterableCategoriesUseCase(selectedCategory = selectedCategory)
                },
                flow6 = _selectedCategory.flatMapLatest { categoryInfo: CategoryInfo? ->
                    podcastCategoryFilterUseCase(category = categoryInfo)
                },
                flow7 = subscribedPodcasts.flatMapLatest { podcasts: List<PodcastWithExtraInfo> ->
                    episodeStore.episodesInPodcasts(
                        podcastUris = podcasts.map { it.podcast.uri },
                        limit = 20
                    )
                }
            ) { homeCategories: EnumEntries<HomeCategory>,
                homeCategory: HomeCategory,
                podcasts: List<PodcastWithExtraInfo>,
                refreshing: Boolean,
                filterableCategories: FilterableCategoriesModel,
                podcastCategoryFilterResult: PodcastCategoryFilterResult,
                libraryEpisodes: List<EpisodeToPodcast> ->

                _selectedCategory.value = filterableCategories.selectedCategory

                // Override selected home category to show 'DISCOVER' if there are no
                // featured podcasts
                selectedHomeCategory.value =
                    if (podcasts.isEmpty()) HomeCategory.Discover else homeCategory

                HomeScreenUiState(
                    isLoading = refreshing,
                    homeCategories = homeCategories,
                    selectedHomeCategory = homeCategory,
                    featuredPodcasts = podcasts.map { it.asExternalModel() }.toPersistentList(),
                    filterableCategoriesModel = filterableCategories,
                    podcastCategoryFilterResult = podcastCategoryFilterResult,
                    library = libraryEpisodes.asLibrary()
                )
            }.catch { throwable: Throwable ->
                emit(
                    HomeScreenUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    /**
     * Refreshes the list of podcasts.
     *
     * This function triggers an update of the podcast data, potentially fetching new or updated
     * information from a remote source. It utilizes a coroutine to perform the refresh operation
     * asynchronously and manages a `refreshing` state to indicate when the process is in progress.
     *
     * **Side Effects:**
     *   - Sets the `refreshing` value to `true` before starting the update.
     *   - Sets the `refreshing` value to `false` after the update completes (either successfully or with an error).
     *   - May update the underlying podcast data managed by `podcastsRepository`.
     *   - There's a TODO to handle errors, so currently exceptions might not be properly surfaced to the caller.
     *
     * **Threading:**
     *   - This function must be called from the main thread, as it utilizes `viewModelScope.launch`.
     *   - The `podcastsRepository.updatePodcasts(force)` method will likely be executed on a background thread.
     *
     * @param force Determines whether to force a network refresh or rely on cached data if
     * available. Defaults to `true`, which means a network refresh will be attempted. If set to
     * `false`, the update might use cached data if present and valid, potentially avoiding a
     * network call.
     *
     * @throws Any exceptions that may be thrown by `podcastsRepository.updatePodcasts(force)` will
     * be propagated within the coroutine.
     */
    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                podcastsRepository.updatePodcasts(force = force)
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    /**
     * Handles actions originating from the home screen.
     *
     * This function acts as a dispatcher for various actions that can be triggered
     * within the home screen of the application. It receives a [HomeAction] and
     * delegates the handling to the appropriate function based on the specific
     * type of action.
     *
     * Possible actions and their corresponding handlers:
     *
     * - [HomeAction.CategorySelected]: A category from the main categories has been selected.
     *   - Handler: [onCategorySelected]
     *   - Parameter: The selected [CategoryInfo].
     *
     * - [HomeAction.HomeCategorySelected]: A category from the Home categories (e.g., "Recently Played") has been selected.
     *   - Handler: [onHomeCategorySelected]
     *   - Parameter: The selected [CategoryInfo].
     *
     * - [HomeAction.LibraryPodcastSelected]: A podcast from the user's library has been selected.
     *   - Handler: [onLibraryPodcastSelected]
     *   - Parameter: The selected [PodcastInfo].
     *
     * - [HomeAction.PodcastUnfollowed]: A podcast has been unfollowed by the user.
     *   - Handler: [onPodcastUnfollowed]
     *   - Parameter: The unfollowed [PodcastInfo].
     *
     * - [HomeAction.QueueEpisode]: An episode has been added to the queue.
     *   - Handler: [onQueueEpisode]
     *   - Parameter: The [PlayerEpisode] added to the queue.
     *
     * - [HomeAction.TogglePodcastFollowed]: The user has toggled the follow status of a podcast.
     *   - Handler: [onTogglePodcastFollowed]
     *   - Parameter: The [PodcastInfo] whose follow status has been toggled.
     *
     * @param action The [HomeAction] representing the user's interaction on the home screen.
     */
    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.CategorySelected -> onCategorySelected(category = action.category)
            is HomeAction.HomeCategorySelected -> onHomeCategorySelected(category = action.category)
            is HomeAction.LibraryPodcastSelected -> onLibraryPodcastSelected(podcast = action.podcast)
            is HomeAction.PodcastUnfollowed -> onPodcastUnfollowed(podcast = action.podcast)
            is HomeAction.QueueEpisode -> onQueueEpisode(episode = action.episode)
            is HomeAction.TogglePodcastFollowed -> onTogglePodcastFollowed(podcast = action.podcast)
        }
    }

    /**
     * Updates the currently selected category.
     *
     * This function is responsible for setting the value of the [MutableStateFlow] of
     * [CategoryInfo] property [_selectedCategory]  to the provided [category].
     *
     * @param category The [CategoryInfo] object representing the category that has been selected.
     * This should be a non-null object.
     */
    private fun onCategorySelected(category: CategoryInfo) {
        _selectedCategory.value = category
    }

    /**
     * Updates the currently selected home category.
     *
     * This function is called when a new category is selected from the home screen's
     * category list. It updates the [MutableStateFlow] of [HomeCategory] property
     * [selectedHomeCategory] with the newly selected [category].
     *
     * @param category The [HomeCategory] that was selected.
     */
    private fun onHomeCategorySelected(category: HomeCategory) {
        selectedHomeCategory.value = category
    }

    /**
     * Unfollows a podcast.
     *
     * This function is triggered when a user unfollows a podcast. It performs the following actions:
     *  1. Launches a coroutine within the ViewModel's scope. This ensures that the unfollow operation
     *  is performed asynchronously and does not block the main thread.
     *  2. Calls the [PodcastStore.unfollowPodcast] function of [PodcastStore] property [podcastStore].
     *  to remove the specified podcast from the user's followed podcasts list.
     *
     * @param podcast The [PodcastInfo]` object representing the podcast that the user has unfollowed.
     * It contains information about the podcast, including its unique URI, which is used to identify
     * it in [podcastStore]..
     *
     * @see PodcastInfo
     * @see PodcastStore.unfollowPodcast
     * @see viewModelScope
     */
    private fun onPodcastUnfollowed(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcast.uri)
        }
    }

    /**
     * Toggles the followed state of a podcast.
     *
     * This function is responsible for updating the user's followed podcasts based on
     * whether the provided podcast is currently followed or not. It uses the
     * [PodcastStore] to perform the actual storage operation and runs it within
     * the ViewModel's coroutine scope.
     *
     * @param podcast The [PodcastInfo] object representing the podcast to toggle the followed state
     * for. It primarily uses the `uri` property to identify the podcast.
     *
     * @see PodcastStore.togglePodcastFollowed
     */
    private fun onTogglePodcastFollowed(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcast.uri)
        }
    }

    /**
     * Updates the currently selected podcast in the library.
     *
     * This function is responsible for setting the [MutableStateFlow] of [PodcastInfo] property
     * [selectedLibraryPodcast]  to the [PodcastInfo] parameter [podcast].
     *
     * @param podcast The [PodcastInfo] object representing the podcast that has been selected in
     * the library. Can be `null` if no podcast is selected.
     */
    private fun onLibraryPodcastSelected(podcast: PodcastInfo?) {
        selectedLibraryPodcast.value = podcast
    }

    /**
     * Adds an episode to the player's queue.
     *
     * This function takes a [PlayerEpisode] object representing an episode and adds it to the
     * current playback queue managed by the [EpisodePlayer] field [episodePlayer].
     *
     * @param episode The [PlayerEpisode] object representing the episode to be added to the queue.
     */
    private fun onQueueEpisode(episode: PlayerEpisode) {
        episodePlayer.addToQueue(episode)
    }
}

/**
 * Converts a list of [EpisodeToPodcast] objects to a [LibraryInfo] object.
 *
 * This function takes a list of [EpisodeToPodcast] instances, each representing an association
 * between an episode and a podcast, and transforms them into a [LibraryInfo] object. The
 * [LibraryInfo] contains a list of [PodcastToEpisodeInfo] which represents the same episode-podcast
 * relationship but in a format suitable for library representation.
 *
 * @receiver A [List] of [EpisodeToPodcast] objects.
 * @return A [LibraryInfo] object containing the transformed data.
 * @see EpisodeToPodcast
 * @see LibraryInfo
 * @see PodcastToEpisodeInfo
 * @see asPodcastToEpisodeInfo
 */
private fun List<EpisodeToPodcast>.asLibrary(): LibraryInfo =
    LibraryInfo(
        episodes = this.map { it.asPodcastToEpisodeInfo() }
    )

/**
 * Represents the different categories available on the home screen.
 *
 * This enum defines the distinct sections or tabs that a user can navigate
 * to within the home section of the application.
 */
enum class HomeCategory {
    /**
     *
     */
    Library,

    /**
     *
     */
    Discover
}

/**
 * Represents actions that can be performed within the Home screen or related components.
 *
 * These actions are typically triggered by user interactions or events and are
 * then handled by the HomeViewModel or a similar component to update the UI or
 * perform other operations.
 */
@Immutable
sealed interface HomeAction {
    /**
     * Represents an action where a category has been selected by the user.
     *
     * This data class encapsulates the information about the selected category,
     * including its details. It is used as a type of `HomeAction` to signal
     * that a user has interacted with a category in the home screen or similar
     * context.
     *
     * @property category The [CategoryInfo] object representing the details of the
     * selected category.
     * @see CategoryInfo
     * @see HomeAction
     */
    data class CategorySelected(val category: CategoryInfo) : HomeAction

    /**
     * Represents an action where a specific category has been selected on the home screen.
     *
     * This data class encapsulates the selected [HomeCategory] and implements the [HomeAction]
     * interface, indicating that it is a type of action that can occur within the home screen's
     * context.
     *
     * @property category The [HomeCategory] that was selected.
     * @see HomeCategory
     * @see HomeAction
     */
    data class HomeCategorySelected(val category: HomeCategory) : HomeAction

    /**
     * Represents the action of a user unfollowing a podcast.
     *
     * This data class encapsulates the information about a specific podcast that a user has
     * unfollowed. It implements the [HomeAction] interface, indicating that this action is
     * relevant to the home screen or feed.
     *
     * @property podcast The [PodcastInfo] object representing the podcast that was unfollowed.
     */
    data class PodcastUnfollowed(val podcast: PodcastInfo) : HomeAction

    /**
     * Represents an action to toggle the followed state of a podcast.
     *
     * This action is dispatched when a user interacts with a podcast (e.g., clicking a
     * follow/unfollow button) to change whether they are following that podcast or not.
     * The specific logic for updating the followed status (e.g., in a database or network
     * request) will typically be handled by a reducer or side effect tied to this action.
     *
     * @property podcast The [PodcastInfo] representing the podcast whose followed state is
     * to be toggled.
     */
    data class TogglePodcastFollowed(val podcast: PodcastInfo) : HomeAction

    /**
     * Represents an action where a specific podcast has been selected from the library.
     *
     * This data class encapsulates the selected podcast's information. It's used within the
     * `HomeAction` hierarchy to signal that a user has chosen a podcast from their library.
     *
     * @property podcast The `PodcastInfo` object representing the selected podcast. It can be
     * `null` if, for example, the podcast was in a state where it's temporarily unavailable or
     * has been removed from the library. In such cases, the UI could reflect this state to the
     * user (e.g., by showing a placeholder or an error message).
     *
     * @see PodcastInfo
     * @see HomeAction
     */
    data class LibraryPodcastSelected(val podcast: PodcastInfo?) : HomeAction

    /**
     * Represents an action to queue an episode for playback.
     *
     * This data class encapsulates a [PlayerEpisode] which is intended to be added to a playback
     * queue. It also implements the [HomeAction] interface, indicating it's an action relevant to
     * the "home" or main section of the application.
     *
     * @property episode The [PlayerEpisode] to be queued. This contains details about the episode,
     * such as its media source, title, and other relevant metadata.
     */
    data class QueueEpisode(val episode: PlayerEpisode) : HomeAction
}

/**
 * Represents the UI state for the Home screen.
 *
 * This data class encapsulates all the necessary information to render the Home screen,
 * including loading state, error messages, featured podcasts, selected category,
 * available categories, filtering data, and library information.
 *
 * @property isLoading Indicates whether the screen is currently loading data. Defaults to `true`.
 * @property errorMessage An optional error message to display if an error occurred. `null` if
 * no error.
 * @property featuredPodcasts A list of featured podcasts to display. Defaults to an empty
 * persistent list.
 * @property selectedHomeCategory The currently selected category in the Home screen. Defaults to
 * [HomeCategory.Discover].
 * @property homeCategories The list of available categories to choose from on the Home screen.
 * Defaults to an empty list.
 * @property filterableCategoriesModel Model containing the data necessary for category filtering
 * @property podcastCategoryFilterResult The results of a podcast category filter, including the
 * filtered list of podcasts and the applied filter.
 * @property library Information about the user's library (e.g., subscriptions, downloaded episodes).
 * Defaults to an empty `LibraryInfo` object.
 */
@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val featuredPodcasts: PersistentList<PodcastInfo> = persistentListOf(),
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val filterableCategoriesModel: FilterableCategoriesModel = FilterableCategoriesModel(),
    val podcastCategoryFilterResult: PodcastCategoryFilterResult = PodcastCategoryFilterResult(),
    val library: LibraryInfo = LibraryInfo(),
)
