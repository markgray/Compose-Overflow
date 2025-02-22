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

package com.example.jetcaster.ui.home.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetcaster.R
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.home.category.podcastCategory
import com.example.jetcaster.util.fullWidthItem

/**
 * Composes the "Discover" items section within a [LazyVerticalGrid].
 *
 * This function renders the discoverable content, including category tabs and a list of podcasts
 * filtered by the selected category. It handles the layout and interactions for this section.
 *
 * @param filterableCategoriesModel The model containing the available categories for filtering.
 * @param podcastCategoryFilterResult The result of filtering podcasts based on the selected category.
 * @param navigateToPodcastDetails Callback to navigate to the details screen of a selected podcast.
 * @param navigateToPlayer Callback to navigate to the player screen for a selected episode.
 * @param onCategorySelected Callback invoked when a category tab is selected.
 * @param onTogglePodcastFollowed Callback to toggle the followed state of a podcast.
 * @param onQueueEpisode Callback to add an episode to the player queue.
 */
fun LazyGridScope.discoverItems(
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
) {
    if (filterableCategoriesModel.isEmpty) {
        // TODO: empty state
        return
    }

    fullWidthItem {
        Spacer(modifier = Modifier.height(height = 8.dp))

        PodcastCategoryTabs(
            filterableCategoriesModel = filterableCategoriesModel,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(height = 8.dp))
    }

    podcastCategory(
        podcastCategoryFilterResult = podcastCategoryFilterResult,
        navigateToPodcastDetails = navigateToPodcastDetails,
        navigateToPlayer = navigateToPlayer,
        onTogglePodcastFollowed = onTogglePodcastFollowed,
        onQueueEpisode = onQueueEpisode,
    )
}

/**
 * Displays a horizontal row of clickable tabs representing podcast categories.
 *
 * This composable renders a set of [ChoiceChipContent] components within a [LazyRow],
 * allowing the user to select a specific podcast category. The currently selected
 * category is highlighted.
 *
 * @param filterableCategoriesModel A [FilterableCategoriesModel] that holds the list of available
 * categories and the currently selected category.
 * @param onCategorySelected A callback function invoked when a category tab is clicked.
 * It receives the selected [CategoryInfo] as a parameter.
 * @param modifier The [Modifier] to be applied to the root layout of this composable.
 *
 * @see FilterableCategoriesModel
 * @see CategoryInfo
 * @see ChoiceChipContent
 */
@Composable
private fun PodcastCategoryTabs(
    filterableCategoriesModel: FilterableCategoriesModel,
    onCategorySelected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex: Int = filterableCategoriesModel.categories.indexOf(
        filterableCategoriesModel.selectedCategory
    )
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Keyline1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(
            items = filterableCategoriesModel.categories,
            key = { _, (id, _) -> id }
        ) { index: Int, category: CategoryInfo ->
            ChoiceChipContent(
                text = category.name,
                selected = index == selectedIndex,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

/**
 * A composable function that renders a custom Choice Chip (Filter Chip).
 *
 * This function creates a chip that can be selected or unselected, displaying
 * a checkmark icon when selected. It is commonly used for filtering options
 * or selecting multiple categories.
 *
 * @param text The text to display on the chip's label.
 * @param selected A boolean indicating whether the chip is currently selected.
 * @param onClick The callback function to be executed when the chip is clicked.
 * @param modifier Modifiers to be applied to the chip. Defaults to [Modifier].
 */
@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.cd_selected_category),
                    modifier = Modifier.height(18.dp)
                )
            }
        },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        shape = MaterialTheme.shapes.medium,
        border = null,
        modifier = modifier,
    )
}
