/*
 * Copyright 2021 The Android Open Source Project
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

@file:OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.example.jetsnack.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.FilterSharedElementKey
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.FilterChip
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * This is a pop-up which is launched when the "Filters" [IconButton] in the [FilterBar] Composable
 * is clicked. Its appearance and disappearance is animated using a shared transition that also
 * animates the synchronized disappearance and appearance of the [IconButton]. We start by initializing
 * and remembering our [MutableState] wrapped [String] variable `val sortState` with the value returned
 * by the [SnackRepo.getSortDefault] method, and our [MutableFloatState] wrapped [Float] variable
 * `var maxCalories` with the value of `0f`. We initialize our [String] variable `val defaultFilter`
 * with the [String] returned by the [SnackRepo.getSortDefault] method.
 *
 * Our root Composable is a [Box] whose [Modifier] `modifier` argument is a [Modifier.fillMaxSize],
 * to which it chains a [Modifier.clickable] whose `indication` argument is `null`, and whose
 * `interactionSource` argument is a [remember] of a [MutableInteractionSource], and its `onClick`
 * lambda argument is a do-nothing lambda. In the [BoxScope] `content` Composable lambda argument of
 * the [Box] we initialize and remember our [List] of [Filter] variable `val priceFilters` to the
 * [List] of [Filter] returned by the [SnackRepo.getPriceFilters] method, our [List] of [Filter]
 * variable `val categoryFilters` to the [List] of [Filter] returned by the [SnackRepo.getCategoryFilters],
 * and our [List] of [Filter] variable `val lifeStyleFilters` to the [List] of [Filter] returned by
 * the [SnackRepo.getLifeStyleFilters] method. Then we compose a [Spacer] whose `modifier` argument
 * is a [Modifier.fillMaxSize] to have it fill the entire [Box], with [Modifier.background] whose
 * [Color] `color` argument is a copy of [Color.Black] with its `alpha` argument set to `0.5f`, and
 * chained to that is a [Modifier.clickable] whose `indication` argument is `null`, and whose
 * `interactionSource` argument is a [remember] of a [MutableInteractionSource]. In its `onClick`
 * lambda argument it calls our lambda parameter [onDismiss] (rather neat, it occupies the background
 * behind the actual filters displayed when they are animated in turning the background behind them
 * gray and allowing you to dismiss by clicking on the [Spacer]).
 *
 * Composed on top of the [Spacer] in the [Box] we use `with` to set the receiver of a block to our
 * [SharedTransitionScope] parameter [sharedTransitionScope]. In its [SharedTransitionScope] `block`
 * we compose a [Column] (it will be animated in and our using the [SharedTransitionScope]) whose
 * [Modifier] argument `modifier` is a [Modifier.padding] that sets the padding on `all` sides to
 * 16.dp, with a [BoxScope.align] whose `alignment` argument is [Alignment.Center] chained to that,
 * followed by a [Modifier.clip] whose `shape` argument is the [Shapes.medium] of our custom
 * [MaterialTheme.shapes], with a [SharedTransitionScope.sharedBounds] chained to that whose
 * `sharedContentState` argument is a [SharedTransitionScope.rememberSharedContentState] whose `key`
 * argument is [FilterSharedElementKey], and whose `animatedVisibilityScope` argument is our
 * [AnimatedVisibilityScope]`parameter [animatedVisibilityScope], whose `resizeMode` argument is
 * [SharedTransitionScope.ResizeMode.RemeasureToBounds] (remeasures and relayouts its child whenever
 * bounds change during the bounds transform), and whose `clipInOverlayDuringTransition` argument is
 * an [SharedTransitionScope.OverlayClip] whose `shape` argument is the [Shapes.medium] of our custom
 * [MaterialTheme.shapes]. This is followed by [Modifier.wrapContentSize] to have it measure at its
 * desired size without regard to incoming minimum size constraints, with a [Modifier.heightIn] whose
 * maximum is 450.dp, followed by [Modifier.verticalScroll] whose `state` argument is a [rememberScrollState],
 * followed by a [Modifier.clickable] whose `indication` argument is `null`, and whose
 * `interactionSource` argument is a [remember] of a [MutableInteractionSource], its `onClick`
 * lambda argument is a do-nothing lambda. This is followed by a [Modifier.background] whose [Color]
 * `color` argument is the [JetsnackColors.uiFloated] of our custom [JetsnackTheme.colors], followed
 * by a [Modifier.padding] that sets the padding on `horizontal` sides to 24.dp, and the padding on
 * vertical sides to 16.dp, with a [SharedTransitionScope.skipToLookaheadSize] at the end of the chain
 * which enables a layout to measure its children with the lookahead constraints, therefore laying
 * out the children as if the transition has finished.
 *
 * In the [ColumnScope] `content` Composable lambda argument of the [Column] we compose:
 *  - a [Row] whose `modifier` argument is a [Modifier.height] whose `intrinsicSize` argument is
 * [IntrinsicSize.Min]. In the [RowScope] `content` Composable lambda argument of the [Row] we
 * have an [IconButton] whose `onClick` lambda argument is our lambda parameter [onDismiss], and
 * whose `content` is an [Icon] whose [ImageVector] `imageVector` argument is the [ImageVector]
 * drawn by [Icons.Filled.Close] (an "X" character), and whose [String] `contentDescription` argument
 * is the [String] with resource ID `R.string.close` ("Close"). Next in the [Row] is a [Text] whose
 * `text` argument is the [String] with resource ID `R.string.label_filters` ("Filters"), and whose
 * [Modifier] `modifier` argument is a [Modifier.fillMaxWidth] with a [Modifier.fillMaxHeight] chained
 * to that followed by a [Modifier.padding] that sets the padding of its `top` to 8.dp and the padding
 * of its `end` to 48.dp. The [TextAlign] `textAlign` argument of the [Text] is [TextAlign.Center],
 * and its [TextStyle] `style` argument is the [Typography.titleLarge] of our custom
 * [MaterialTheme.typography].
 *
 * @param sharedTransitionScope the [SharedTransitionScope] that controls the animation we share with
 * the "Filters" [IconButton] in the [FilterBar] Composable.
 * @param animatedVisibilityScope the [AnimatedVisibilityScope] that controls our enter/exit
 * animations.
 * @param onDismiss a lambda we should call when the user indicates we should be dismissed.
 */
@Composable
fun FilterScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onDismiss: () -> Unit
) {
    var sortState: String by remember { mutableStateOf(SnackRepo.getSortDefault()) }
    var maxCalories: Float by remember { mutableFloatStateOf(value = 0f) }
    val defaultFilter: String = SnackRepo.getSortDefault()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // capture click
            }
    ) {
        val priceFilters: List<Filter> = remember { SnackRepo.getPriceFilters() }
        val categoryFilters: List<Filter> = remember { SnackRepo.getCategoryFilters() }
        val lifeStyleFilters: List<Filter> = remember { SnackRepo.getLifeStyleFilters() }
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismiss()
                }
        )
        with(receiver = sharedTransitionScope) {
            Column(
                Modifier
                    .padding(all = 16.dp)
                    .align(alignment = Alignment.Center)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = FilterSharedElementKey),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium)
                    )
                    .wrapContentSize()
                    .heightIn(max = 450.dp)
                    .verticalScroll(state = rememberScrollState())
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { }
                    .background(color = JetsnackTheme.colors.uiFloated)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .skipToLookaheadSize(),
            ) {
                Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.close)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.label_filters),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(top = 8.dp, end = 48.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                    val resetEnabled: Boolean = sortState != defaultFilter

                    IconButton(
                        onClick = { /* TODO: Open search */ },
                        enabled = resetEnabled
                    ) {
                        val fontWeight: FontWeight = if (resetEnabled) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }

                        Text(
                            text = stringResource(id = R.string.reset),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = fontWeight,
                            color = JetsnackTheme.colors.uiBackground
                                .copy(alpha = if (!resetEnabled) 0.38f else 1f)
                        )
                    }
                }

                SortFiltersSection(
                    sortState = sortState,
                    onFilterChange = { filter: Filter ->
                        sortState = filter.name
                    }
                )
                FilterChipSection(
                    title = stringResource(id = R.string.price),
                    filters = priceFilters
                )
                FilterChipSection(
                    title = stringResource(id = R.string.category),
                    filters = categoryFilters
                )

                MaxCalories(
                    sliderPosition = maxCalories,
                    onValueChanged = { newValue: Float ->
                        maxCalories = newValue
                    }
                )
                FilterChipSection(
                    title = stringResource(id = R.string.lifestyle),
                    filters = lifeStyleFilters
                )
            }
        }
    }
}

/**
 *
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipSection(title: String, filters: List<Filter>) {
    FilterTitle(text = title)
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp)
            .padding(horizontal = 4.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                filter = filter,
                modifier = Modifier.padding(end = 4.dp, bottom = 8.dp)
            )
        }
    }
}

/**
 *
 */
@Composable
fun SortFiltersSection(sortState: String, onFilterChange: (Filter) -> Unit) {
    FilterTitle(text = stringResource(id = R.string.sort))
    Column(Modifier.padding(bottom = 24.dp)) {
        SortFilters(
            sortState = sortState,
            onChanged = onFilterChange
        )
    }
}

/**
 *
 */
@Composable
fun SortFilters(
    sortFilters: List<Filter> = SnackRepo.getSortFilters(),
    sortState: String,
    onChanged: (Filter) -> Unit
) {

    sortFilters.forEach { filter: Filter ->
        SortOption(
            text = filter.name,
            icon = filter.icon,
            selected = sortState == filter.name,
            onClickOption = {
                onChanged(filter)
            }
        )
    }
}

/**
 *
 */
@Composable
fun MaxCalories(sliderPosition: Float, onValueChanged: (Float) -> Unit) {
    FlowRow {
        FilterTitle(text = stringResource(id = R.string.max_calories))
        Text(
            text = stringResource(id = R.string.per_serving),
            style = MaterialTheme.typography.bodyMedium,
            color = JetsnackTheme.colors.brand,
            modifier = Modifier.padding(top = 5.dp, start = 10.dp)
        )
    }
    Slider(
        value = sliderPosition,
        onValueChange = { newValue ->
            onValueChanged(newValue)
        },
        valueRange = 0f..300f,
        steps = 5,
        modifier = Modifier
            .fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = JetsnackTheme.colors.brand,
            activeTrackColor = JetsnackTheme.colors.brand,
            inactiveTrackColor = JetsnackTheme.colors.iconInteractive
        )
    )
}

/**
 *
 */
@Composable
fun FilterTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = JetsnackTheme.colors.brand,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

/**
 *
 */
@Composable
fun SortOption(
    text: String,
    icon: ImageVector?,
    onClickOption: () -> Unit,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .padding(top = 14.dp)
            .selectable(selected = selected) { onClickOption() }
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(weight = 1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                tint = JetsnackTheme.colors.brand
            )
        }
    }
}

/**
 * A preview of our [FilterScreen].
 */
@Preview("filter screen")
@Composable
fun FilterScreenPreview() {
    JetsnackTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                FilterScreen(
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onDismiss = {}
                )
            }
        }
    }
}
