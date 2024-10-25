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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.example.jetsnack.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.FilterSharedElementKey
import com.example.jetsnack.ui.home.FilterScreen
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * Used by `SnackCollectionList` to hold a [LazyRow] of [FilterChip] to display the [List] of [Filter]
 * it is passed as its `filters` argument.
 *
 * @param filters the [List] of [Filter] that we are to display in our [LazyRow] of [FilterChip].
 * `SnackCollectionList` passes us one that traces back to that returned by [SnackRepo.getPriceFilters].
 * @param onShowFilters a lambda that we pass to the [IconButton] at the beginning of our [LazyRow].
 * `SnackCollectionList` passes us its `onFiltersSelected` parameter which traces back to a lambda
 * that sets the `filtersVisible` [MutableState] wrapped [Boolean] variable to `true` which animates
 * the visibility of its [FilterScreen] Composable to visible (this displays a pop-up with more
 * filter possibilities).
 * @param filterScreenVisible is the [FilterScreen] Composable visible? This traces back to
 * `filtersVisible` [MutableState] wrapped [Boolean] variable that is set by the [onShowFilters]
 * lambda parameter.
 * @param sharedTransitionScope this is the [SharedTransitionScope] that handles a shared element
 * transition for the [FilterScreen] Composable.
 */
@Composable
fun FilterBar(
    filters: List<Filter>,
    onShowFilters: () -> Unit,
    filterScreenVisible: Boolean,
    sharedTransitionScope: SharedTransitionScope
) {
    with(sharedTransitionScope) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
            modifier = Modifier.heightIn(min = 56.dp)
        ) {
            item {
                AnimatedVisibility(visible = !filterScreenVisible) {
                    IconButton(
                        onClick = onShowFilters,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(FilterSharedElementKey),
                                animatedVisibilityScope = this@AnimatedVisibility,
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            tint = JetsnackTheme.colors.brand,
                            contentDescription = stringResource(R.string.label_filters),
                            modifier = Modifier.diagonalGradientBorder(
                                colors = JetsnackTheme.colors.interactiveSecondary,
                                shape = CircleShape
                            )
                        )
                    }
                }
            }
            items(filters) { filter ->
                FilterChip(filter = filter, shape = MaterialTheme.shapes.small)
            }
        }
    }
}

/**
 * This used by [FilterBar] and [FilterScreen] to display a [Filter].
 *
 * @param filter the [Filter] we are supposed to display.
 */
@Composable
fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    val (selected, setSelected) = filter.enabled
    val backgroundColor by animateColorAsState(
        if (selected) JetsnackTheme.colors.brandSecondary else JetsnackTheme.colors.uiBackground,
        label = "background color"
    )
    val border = Modifier.fadeInDiagonalGradientBorder(
        showBorder = !selected,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = shape
    )
    val textColor by animateColorAsState(
        if (selected) Color.Black else JetsnackTheme.colors.textSecondary,
        label = "text color"
    )

    JetsnackSurface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        val pressed by interactionSource.collectIsPressedAsState()
        @Suppress("RedundantValueArgument")
        val backgroundPressed =
            if (pressed) {
                Modifier.offsetGradientBackground(
                    colors = JetsnackTheme.colors.interactiveSecondary,
                    width = 200f,
                    offset = 0f
                )
            } else {
                Modifier.background(Color.Transparent)
            }
        Box(
            modifier = Modifier
                .toggleable(
                    value = selected,
                    onValueChange = setSelected,
                    interactionSource = interactionSource,
                    indication = null
                )
                .then(backgroundPressed)
                .then(border),
        ) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 6.dp
                )
            )
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterDisabledPreview() {
    JetsnackTheme {
        @Suppress("RedundantValueArgument")
        FilterChip(
            filter = Filter(name = "Demo", enabled = false),
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterEnabledPreview() {
    JetsnackTheme {
        FilterChip(Filter(name = "Demo", enabled = true))
    }
}
