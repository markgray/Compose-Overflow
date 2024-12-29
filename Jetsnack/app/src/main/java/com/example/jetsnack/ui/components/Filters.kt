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
    ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class
)

package com.example.jetsnack.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.SharedContentState
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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.material3.Typography
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
 * it is passed as its `filters` argument. Using `with` [SharedTransitionScope] parameter
 * [sharedTransitionScope] as the receiver we compose a [LazyRow] whose `verticalAlignment` argument
 * is [Alignment.CenterVertically] to center its children vertically, whose `horizontalArrangement`
 * argument is [Arrangement.spacedBy] of `8.dp` `space` between the children, whose `contentPadding`
 * is a [PaddingValues] whose `start` is `12.dp` and `end` is `8.dp`, and whose `modifier` argument
 * is a [Modifier.heightIn] whose `min` is `56.dp`. In the [LazyListScope] `content` Composable lambda
 * argument of the [LazyRow] we compose a [LazyListScope.item] which wraps an [IconButton] in an
 * [AnimatedVisibility] whose `visible` argument is `!filterScreenVisible` (the [IconButton] is not
 * displayed when the [FilterScreen] is being displayed). The `onClick` argument of the [IconButton]
 * is our lambda parameter [onShowFilters], and its [Modifier] `modifier` argument is a
 * [SharedTransitionScope.sharedBounds] whose `sharedContentState` argument is a remembered
 * [SharedContentState] whose `key` is [FilterSharedElementKey], whose `animatedVisibilityScope`
 * argument is the [AnimatedVisibilityScope] of the [AnimatedVisibility] wrapping the [IconButton],
 * and whose `resizeMode` is [SharedTransitionScope.ResizeMode.RemeasureToBounds] (remeasures and
 * relayouts its child whenever bounds change during the bounds transform). The `content` Composable
 * lambda argument of the [IconButton] composes an [Icon] whose [ImageVector] `imageVector` argument
 * is the [ImageVector] drawn by [Icons.Rounded.FilterList] (three horizontal lines of decreasing
 * length), whose [Color] `tint` argument is the [JetsnackColors.brand] of our custom
 * [JetsnackTheme.colors], whose `contentDescription` argument is the [String] whose resource ID is
 * `R.string.label_filters` ("Filters"), and whose [Modifier] `modifier` argument is a
 * [Modifier.diagonalGradientBorder] whose [List] of [Color] argument `colors` is the
 * [JetsnackColors.interactiveSecondary] of our custom [JetsnackTheme.colors], and whose [Shape]
 * `shape` argument is [CircleShape].
 *
 * After the [IconButton] we compose a [LazyListScope.items] whose `items` argument is our [List] of
 * [Filter] parameter [filters]. In the [LazyItemScope] `itemContent` Composable lambda argument we
 * capture the [Filter] passed the lambda in our variable `filter` then compose a [FilterChip] for
 * each [Filter] whose `filter` argument is our variable `filter`, and whose [Shape] `shape` argument
 * is the [Shapes.small] of our custom [MaterialTheme.shapes].
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
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
            modifier = Modifier.heightIn(min = 56.dp)
        ) {
            item {
                AnimatedVisibility(visible = !filterScreenVisible) {
                    IconButton(
                        onClick = onShowFilters,
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(FilterSharedElementKey),
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
            items(items = filters) { filter: Filter ->
                FilterChip(filter = filter, shape = MaterialTheme.shapes.small)
            }
        }
    }
}

/**
 * This used by [FilterBar] and [FilterScreen] to display a [Filter]. We start by using a destructuring
 * declaration to initialize our [Boolean] variable `val selected` and lambda taking a [Boolean]
 * argument `val setSelected` to the [MutableState] wrapped [Boolean] field [Filter.enabled] of our
 * [Filter] parameter [filter]. We initialize our [Color] variable `val backgroundColor` to a [State]
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
 * [InteractionSource.collectIsPressedAsState] method of `interactionSource`. It initializes its
 * [Modifier] variable `val backgroundPressed` to a [Modifier.offsetGradientBackground] which is
 * configured to animate in and out the background of the [Box] of our composable using the
 * [JetsnackColors.interactiveSecondary] of our custom [JetsnackTheme.colors] as the [List] of [Color]
 * used by [Brush.linearGradient] when `pressed` is `true` or a [Modifier.background] whose `color`
 * is [Color.Transparent] when it is `false`.
 *
 * The root composable of the [JetsnackSurface] is a [Box] whose `modifier` argument is a
 * [Modifier.toggleable] whose `value` argument is our [Boolean] variable `selected`, whose
 * `onValueChange` argument is our lambda variable `setSelected`, whose `interactionSource` argument
 * is our [MutableInteractionSource] variable `interactionSource`, and whose `indication` argument
 * is `null`, and to this is chained our [Modifier] variable `backgroundPressed`, and our [Modifier]
 * variable `border`.
 *
 * The `content` of the [Box] is a [Text] whose `text` argument is the [Filter.name] of our [Filter]
 * parameter [filter], whose [TextStyle] `style` argument is the [Typography.bodySmall] of our
 * custom [MaterialTheme.typography], whose `maxLines` argument is 1, and whose `modifier` argument
 * is a [Modifier.padding] that adds 20.dp padding to our `horizontal` sides and 6.dp padding to our
 * `vertical` sides.
 *
 * @param filter the [Filter] we are supposed to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [com.example.jetsnack.ui.home.FilterChipSection] calls us with a [Modifier.padding]
 * that adds 4.dp to our `end` and 8.dp to our `bottom` but [FilterBar] does not pass us any so the
 * empty, default, or starter Modifier that contains no elements is used.
 * @param shape the [Shape] we should use for our border and our [JetsnackSurface]. [FilterBar]
 * passes us the [Shapes.small] of our custom [MaterialTheme] (which is also our default?), but
 * [com.example.jetsnack.ui.home.FilterChipSection] passes us none so our default [Shapes.small] of
 * our custom [MaterialTheme.shapes] is used.
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

/**
 * Three Previews of our [FilterChip] with the [Filter.enabled] property `false`
 */
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

/**
 * Three Previews of our [FilterChip] with the [Filter.enabled] property `true`
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterEnabledPreview() {
    JetsnackTheme {
        FilterChip(Filter(name = "Demo", enabled = true))
    }
}
