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
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.asExternalModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case responsible for providing a stream of [FilterableCategoriesModel]
 * which represents a list of categories that can be used for filtering, along with
 * the currently selected category.
 *
 * This use case retrieves categories from the [CategoryStore], transforms them
 * into external models, and manages the selection state. It emits updates
 * whenever the underlying category data changes.
 *
 * @property categoryStore The data source for categories.
 */
class FilterableCategoriesUseCase @Inject constructor(
    private val categoryStore: CategoryStore
) {
    /**
     * Creates a [Flow] of [FilterableCategoriesModel] from the list of categories in [categoryStore],
     * and the currently selected category.
     *
     * We use the [CategoryStore.categoriesSortedByPodcastCount] method to get the [Flow] of [List]
     * list of [Category] it returns, then we use the [Flow.map] extension function to apply a
     * transform to the [List] of [Category] producing a [Flow] of [FilterableCategoriesModel] with
     * its `categories` argument the result of applying the `asExternalModel` function to each
     * [Category], and its `selectedCategory` argument the result of applying the `asExternalModel`
     * function to our [selectedCategory] argument if it is not `null`, otherwise the result of
     * applying the `asExternalModel` function to the first [Category] in the [List] of [Category]
     * returned by the [List.firstOrNull] function of the [List] of [Category] (or `null` if the
     * [List] of [Category] is empty).
     *
     * @param selectedCategory the currently selected category. If `null`, the first category
     * returned by the backing category list will be selected in the returned
     * [FilterableCategoriesModel]
     */
    operator fun invoke(selectedCategory: CategoryInfo?): Flow<FilterableCategoriesModel> =
        categoryStore.categoriesSortedByPodcastCount()
            .map { categories: List<Category> ->
                FilterableCategoriesModel(
                    categories = categories.map { it.asExternalModel() },
                    selectedCategory = selectedCategory
                        ?: categories.firstOrNull()?.asExternalModel()
                )
            }
}
