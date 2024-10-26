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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.example.jetsnack.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
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
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.example.jetsnack.ui.theme.JetsnackColors
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
 * This used by [FilterBar] and [FilterScreen] to display a [Filter]. We start by using a destructuring
 * declaration to initialize our [Boolean] variable `val selected` and lambda taking a [Boolean]
 * argument `val setSelected` to the [MutableState] wrapped [Boolean] field [Filter.enabled] of our
 * [Filter] parameter [filter]. We initialize our [Color] variable `val backgroundColor` to an [State]
 * wrapped animated [Color] that animates between [JetsnackColors.brandSecondary] when `selected` is
 * `true` to [JetsnackColors.uiBackground] when it is `false`. We initialize our [Modifier] variable
 * `val border` to our [Modifier.fadeInDiagonalGradientBorder] which is configured to animate in
 * and out the border of the [Box] of our composable using the [JetsnackColors.interactiveSecondary]
 * [List] of [Color] as the [Brush.linearGradient] used based on the inverse of our [Boolean] variable
 * `selected` with our [Shape] argument [shape] as the [Shape] of the border. We initialize our [Color]
 * variable `val textColor` to a [State] wrapped animated [Color] that animates between [Color.Black]
 * when `selected` is `true` to [JetsnackColors.textSecondary] when it is `false`.
 *
 * Our root Composable is a [JetsnackSurface] whose `modifier` argument is our [Modifier] parameter
 * [modifier], whose `color` argument is our [State] wrapped animated [Color] variable `backgroundColor`,
 * whose `contentColor` argument is our [State] wrapped animated [Color] variable `textColor`, whose
 * `shape` argument is our [Shape] parameter [shape], and whose `elevation` argument is 2.dp. The
 * `content` Compable lambda argument of the [JetsnackSurface] starts by initializing and remembering
 * its [MutableInteractionSource] variable `val interactionSource` to a new instance. It initializes
 * its [State] wrapped [Boolean] variable `val pressed` to the value returned by the
 * [InteractionSource.collectIsPressedAsState] method of method of `interactionSource`. It initializes
 * its [Modifier] variable `val backgroundPressed` to a [Modifier.offsetGradientBackground] which is
 * configured to animate in and out the background of the [Box] of our composable using the
 * [JetsnackColors.interactiveSecondary] [List] of [Color] as the [Brush.linearGradient] when `pressed`
 * is `true` or a [Modifier.background] whose `color` is [Color.Transparent].
 *
 * The root composable of the [JetsnackSurface] is a [Box] whose `modifier` argument is a
 * [Modifier.toggleable]
 *
 * @param filter the [Filter] we are supposed to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [com.example.jetsnack.ui.home.FilterChipSection] calls us with a [Modifier.padding]
 * that adds 4.dp to our `end` and 8.dp to our `bottom` but [FilterBar] does not pass us any so the
 * empty, default, or starter Modifier that contains no elements is used.
 * @param shape the [Shape] we should use for our border and our [JetsnackSurface]. [FilterBar]
 * passes us the [Shapes.small] of our custom [MaterialTheme] (which is also our default?), but
 * [com.example.jetsnack.ui.home.FilterChipSection] passes us none so our default [Shapes.small] of
 * our custom [MaterialTheme] is used.
 */
@Composable
fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    val (selected: Boolean, setSelected: (Boolean) -> Unit) = filter.enabled
    val backgroundColor: Color by animateColorAsState(
        targetValue = if (selected) JetsnackTheme.colors.brandSecondary else JetsnackTheme.colors.uiBackground,
        label = "background color"
    )
    val border: Modifier = Modifier.fadeInDiagonalGradientBorder(
        showBorder = !selected,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = shape
    )
    val textColor: Color by animateColorAsState(
        targetValue = if (selected) Color.Black else JetsnackTheme.colors.textSecondary,
        label = "text color"
    )

    JetsnackSurface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

        val pressed: Boolean by interactionSource.collectIsPressedAsState()
        @Suppress("RedundantValueArgument")
        val backgroundPressed: Modifier =
            if (pressed) {
                Modifier.offsetGradientBackground(
                    colors = JetsnackTheme.colors.interactiveSecondary,
                    width = 200f,
                    offset = 0f
                )
            } else {
                Modifier.background(color = Color.Transparent)
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
