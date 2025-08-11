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

package com.example.jetcaster.tv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.tv.model.CategoryInfoList
import com.example.jetcaster.tv.model.CategorySelection
import com.example.jetcaster.tv.model.CategorySelectionList
import com.example.jetcaster.tv.model.PodcastList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [SearchScreenViewModel] is a ViewModel class responsible for managing the UI state and
 * business logic of the search screen.
 *
 * It interacts with [PodcastsRepository], [PodcastStore], and [CategoryStore] to fetch, filter,
 * and provide search results for podcasts based on user input (keyword and category selection).
 *
 * @property podcastsRepository Repository for managing podcasts, episodes, and categories.
 * @property podcastStore Store for managing and accessing podcast data.
 * @property categoryStore Store for managing categories and their relationships with podcasts
 * and episodes.
 */
@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val podcastStore: PodcastStore,
    categoryStore: CategoryStore,
) : ViewModel() {

    /**
     * A [MutableStateFlow] of [String] representing the current keyword entered by the user in the
     * search field.
     */
    private val keywordFlow: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * A [MutableStateFlow] of [List] of [CategoryInfo] representing the selected categories.
     */
    private val selectedCategoryListFlow: MutableStateFlow<List<CategoryInfo>> =
        MutableStateFlow(emptyList())

    /**
     * A [Flow] of [CategoryInfoList] representing the list of all categories sorted by the number of
     * podcasts in each category.
     */
    private val categoryInfoListFlow: Flow<CategoryInfoList> =
        categoryStore.categoriesSortedByPodcastCount().map(CategoryInfoList::from)

    /**
     * A [Flow] of [SearchCondition] representing the current search conditions. It uses the [combine]
     * function to combine the [keywordFlow], [selectedCategoryListFlow], and [categoryInfoListFlow]
     * flows accepting the [String] from [keywordFlow] in variable `keyword`, the [List] of
     * [CategoryInfo] from [selectedCategoryListFlow] in variable `selectedCategories`, and the
     * [CategoryInfoList] from [categoryInfoListFlow] in variable `categories` respectively. It
     * then initializes its [List] of [CategoryInfo] variable `selected` to `selectedCategories`
     * if it is not empty, otherwise to `categories`. Finally it emits a [SearchCondition] object
     * whose `keyword` argument is `keyword`, and whose `categoryInfoList` argument is `selected`.
     */
    private val searchConditionFlow: Flow<SearchCondition> =
        combine(
            flow = keywordFlow,
            flow2 = selectedCategoryListFlow,
            flow3 = categoryInfoListFlow
        ) { keyword: String, selectedCategories: List<CategoryInfo>, categories: CategoryInfoList ->
            val selected: List<CategoryInfo> = selectedCategories.ifEmpty {
                categories
            }
            SearchCondition(keyword = keyword, categoryInfoList = selected)
        }

    /**
     * A [StateFlow] of [List] of [PodcastWithExtraInfo] representing the search results. It uses
     * the [Flow.flatMapLatest] method of [searchConditionFlow] accepting the [SearchCondition]
     * object from [searchConditionFlow] in variable `condition` then emits the [List] of
     * [PodcastWithExtraInfo] from the [PodcastStore.searchPodcastByTitleAndCategories]
     * method of [podcastStore] whose `keyword` argument is `condition.keyword`, and whose
     * `categories` argument is `condition.selectedCategories.intoCategoryList()`. It then uses the
     * [Flow.stateIn] method of the [Flow] it emits to convert the [Flow] to a [StateFlow] using
     * the `scope` argument [viewModelScope], the `started` argument [SharingStarted.WhileSubscribed]
     * with a `stopTimeoutMillis` of 5_000, and the `initialValue` argument [emptyList].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResultFlow: StateFlow<List<PodcastWithExtraInfo>> =
        searchConditionFlow.flatMapLatest { condition: SearchCondition ->
            podcastStore.searchPodcastByTitleAndCategories(
                keyword = condition.keyword,
                categories = condition.selectedCategories.intoCategoryList()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = emptyList()
        )

    /**
     * A [Flow] of [CategorySelectionList] representing the list of categories with their
     * corresponding selection status. It uses the [combine] function to combine the
     * [categoryInfoListFlow] and [selectedCategoryListFlow] flows accepting the [CategoryInfoList]
     * from [categoryInfoListFlow] in variable `categoryList`, and the [List] of [CategoryInfo]
     * from [selectedCategoryListFlow] in variable `selectedCategories` respectively. Then it
     * initializes its [List] of [CategorySelection] variable `list` to a [List] of
     * [CategorySelection] that it creates by using the [Iterable.map] method of `categoryList`
     * accepting the [CategoryInfo] from `categoryList` in variable `category` then creates a
     * [CategorySelection] object whose `categoryInfo` argument is `category`, and whose
     * `isSelected` argument is the result of checking if `selectedCategories` contains `category`.
     * Finally it emits a [CategorySelectionList] object whose `member` argument is `list`.
     */
    private val categorySelectionFlow: Flow<CategorySelectionList> =
        combine(
            flow = categoryInfoListFlow,
            flow2 = selectedCategoryListFlow
        ) { categoryList: CategoryInfoList, selectedCategories: List<CategoryInfo> ->
            val list: List<CategorySelection> = categoryList.map { category: CategoryInfo ->
                CategorySelection(
                    categoryInfo = category,
                    isSelected = selectedCategories.contains(category)
                )
            }
            CategorySelectionList(member = list)
        }

    /**
     * A [StateFlow] of [SearchScreenUiState] representing the current UI state of the search
     * screen. We use the [combine] function to combine the [keywordFlow],
     * [categorySelectionFlow], and [searchResultFlow] flows accepting the [String] from
     * [keywordFlow] in variable `keyword`, the [CategorySelectionList] from
     * [categorySelectionFlow] in variable `categorySelection`, and the [List] of [PodcastInfo]
     * from [searchResultFlow] in variable `result` respectively. Then in the `transform` lambda
     * argument of [combine] we initialize our [List] of [PodcastInfo] variable `podcastList` to
     * the result of using the [Iterable.map] method of `result` accepting the
     * [PodcastWithExtraInfo] from `result` in variable `podcast` then call the
     * [PodcastWithExtraInfo.asExternalModel] method of `podcast` to convert it to a
     * [PodcastInfo] object. Finally we use a `when` statement to branch on the result of
     * checking if `result` is empty. If it is empty we emit a [SearchScreenUiState.Ready]
     * object whose `keyword` argument is `keyword`, and whose `categorySelectionList` argument
     * is `categorySelection`. Otherwise we emit a [SearchScreenUiState.HasResult] object whose
     * `keyword` argument is `keyword`, whose `categorySelectionList` argument is
     * `categorySelection`, and whose `result` argument is `podcastList`. We then use the
     * [Flow.stateIn] method of the [Flow] emitted to convert the [Flow] to a [StateFlow] using
     * the `scope` argument [viewModelScope], the `started` argument
     * [SharingStarted.WhileSubscribed] with a `stopTimeoutMillis` of 5_000, and the
     * `initialValue` argument of [SearchScreenUiState.Loading].
     */
    val uiStateFlow: StateFlow<SearchScreenUiState> =
        combine(
            flow = keywordFlow,
            flow2 = categorySelectionFlow,
            flow3 = searchResultFlow
        ) { keyword: String,
            categorySelection: CategorySelectionList,
            result: List<PodcastWithExtraInfo> ->

            val podcastList: List<PodcastInfo> = result
                .map { podcast: PodcastWithExtraInfo -> podcast.asExternalModel() }
            when {
                result.isEmpty() -> SearchScreenUiState.Ready(
                    keyword = keyword,
                    categorySelectionList = categorySelection
                )

                else -> SearchScreenUiState.HasResult(
                    keyword = keyword,
                    categorySelectionList = categorySelection,
                    result = podcastList
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SearchScreenUiState.Loading,
        )

    /**
     * Sets the current keyword used for filtering or searching.
     *
     * This function updates the value of the underlying [keywordFlow] with the [String] parameter
     * [keyword]. This will trigger any collectors of [keywordFlow] to receive the new keyword.
     *
     * @param keyword The new keyword to be set. This string will be used for
     * filtering or searching purposes. It can be an empty string.
     *
     * @see keywordFlow
     */
    fun setKeyword(keyword: String) {
        keywordFlow.value = keyword
    }

    /**
     * Adds a category to the list of selected categories if it's not already present.
     *
     * This function takes a [CategoryInfo] object and adds it to the [selectedCategoryListFlow]
     * if the category is not already present in the list. It uses an immutable list approach,
     * creating a new list with the added category instead of modifying the existing one.
     * This ensures that any subscribers to the flow receive a new list instance, triggering updates.
     *
     * @param category The [CategoryInfo] object to be added to the selected category list.
     * It must not be `null`.
     * @throws IllegalArgumentException if the passed category is null.
     * @see MutableStateFlow
     * @see CategoryInfo
     */
    fun addCategoryToSelectedCategoryList(category: CategoryInfo) {
        val list: List<CategoryInfo> = selectedCategoryListFlow.value
        if (!list.contains(category)) {
            selectedCategoryListFlow.value = list + listOf(category)
        }
    }

    /**
     * Removes a category from the list of selected categories if it's present.
     *
     * We start by initializing our [List] of [CategoryInfo] variable `list` to the value of
     * [selectedCategoryListFlow]. We then check if `list` contains the [CategoryInfo] parameter
     * [category]. If it does, we create a new [MutableList] of [CategoryInfo] variable `mutable`
     * from [selectedCategoryListFlow] then use the [MutableList.remove] method to remove [category]
     * from `list`. Finally we update the value of [selectedCategoryListFlow] to `mutable`.
     *
     * @param category The [CategoryInfo] object to be removed from the selected category list.
     */
    fun removeCategoryFromSelectedCategoryList(category: CategoryInfo) {
        val list: List<CategoryInfo> = selectedCategoryListFlow.value
        if (list.contains(category)) {
            val mutable: MutableList<CategoryInfo> = list.toMutableList()
            mutable.remove(category)
            selectedCategoryListFlow.value = mutable.toList()
        }
    }

    /**
     * Updates the list of podcasts in the [PodcastsRepository] property [podcastsRepository].
     */
    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(force = false)
        }
    }
}

/**
 * Represents the search conditions used to filter search results.
 *
 * This data class holds the keyword to search for and the list of selected categories to filter by.
 *
 * @property keyword The keyword to search for. This can be an empty string if no keyword search
 * is needed.
 * @property selectedCategories The list of selected categories used to filter the search results.
 * It's an instance of [CategoryInfoList].
 *
 * @constructor Creates a [SearchCondition] instance with the given keyword and a [CategoryInfoList].
 *
 * @constructor Creates a [SearchCondition] instance with the given keyword and a list of
 * [CategoryInfo]. This constructor converts its list of [CategoryInfo] parameter `categoryInfoList`
 * to [CategoryInfoList].
 */
private data class SearchCondition(val keyword: String, val selectedCategories: CategoryInfoList) {
    constructor(keyword: String, categoryInfoList: List<CategoryInfo>) : this(
        keyword = keyword,
        selectedCategories = CategoryInfoList(categoryInfoList)
    )
}

/**
 * Represents the UI state of the Search Screen.
 *
 * This sealed interface defines the different states the Search Screen can be in,
 * including loading, ready for input, and having search results.
 */
sealed interface SearchScreenUiState {
    /**
     * Represents the UI state before a search has been started.
     */
    data object Loading : SearchScreenUiState

    /**
     * Represents the UI state when the search screen is ready to display search results.
     *
     * This state indicates that the search screen has received the necessary data
     * (a keyword and a list of category selections) to perform a search and display
     * relevant results.
     *
     * @property keyword The keyword used for the search.
     * @property categorySelectionList The list of category selections that can be used to filter
     * the search results.
     */
    data class Ready(
        val keyword: String,
        val categorySelectionList: CategorySelectionList
    ) : SearchScreenUiState

    /**
     * Represents the UI state of the search screen when a search has yielded results.
     *
     * This class encapsulates the data associated with a successful search, including:
     *  - The keyword that was used for the search.
     *  - The list of categories that were selected for the search.
     *  - The list of podcasts that match the search criteria.
     *
     * @property keyword The keyword used for the search.
     * @property categorySelectionList The list of selected categories for the search.
     * @property result The list of podcasts that match the search criteria.
     */
    data class HasResult(
        val keyword: String,
        val categorySelectionList: CategorySelectionList,
        val result: PodcastList
    ) : SearchScreenUiState
}
