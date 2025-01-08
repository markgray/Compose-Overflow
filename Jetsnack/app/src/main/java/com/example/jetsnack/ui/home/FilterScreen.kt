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
import androidx.compose.foundation.layout.FlowRowScope
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
import androidx.compose.material3.SliderColors
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.FilterSharedElementKey
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.FilterChip
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * This is a pop-up which is launched when the "Filters" [IconButton] in the [FilterBar] Composable
 * is clicked. Its appearance and disappearance is animated using a shared transition that also
 * animates the synchronized disappearance and appearance of the [IconButton]. We start by
 * initializing and remembering our [MutableState] wrapped [String] variable `val sortState` with
 * the value returned by the [SnackRepo.getSortDefault] method, and our [MutableFloatState] wrapped
 * [Float] variable `var maxCalories` with the value of `0f`. We initialize our [String] variable
 * `val defaultFilter` with the [String] returned by the [SnackRepo.getSortDefault] method.
 *
 * Our root Composable is a [Box] whose [Modifier] `modifier` argument is a [Modifier.fillMaxSize],
 * to which it chains a [Modifier.clickable] whose `indication` argument is `null`, and whose
 * `interactionSource` argument is a [remember] of a [MutableInteractionSource], and its `onClick`
 * lambda argument is a do-nothing lambda. In the [BoxScope] `content` Composable lambda argument of
 * the [Box] we initialize and remember our [List] of [Filter] variable `val priceFilters` to the
 * [List] of [Filter] returned by the [SnackRepo.getPriceFilters] method, our [List] of [Filter]
 * variable `val categoryFilters` to the [List] of [Filter] returned by [SnackRepo.getCategoryFilters],
 * and our [List] of [Filter] variable `val lifeStyleFilters` to the [List] of [Filter] returned by
 * [SnackRepo.getLifeStyleFilters]. Then we compose a [Spacer] whose `modifier` argument is a
 * [Modifier.fillMaxSize] to have it fill the entire [Box], followed by a chain to a
 * [Modifier.background] whose [Color] `color` argument is a copy of [Color.Black] with its `alpha`
 * argument set to `0.5f`, and chained to that is a [Modifier.clickable] whose `indication` argument
 * is `null`, and whose `interactionSource` argument is a [remember] of a [MutableInteractionSource].
 * In its `onClick` lambda argument it calls our lambda parameter [onDismiss] (rather neat, it
 * occupies the background behind the actual filters displayed when they are animated in turning the
 * background behind them a translucent gray (the content underneath still visible) and allowing you
 * to dismiss by clicking on the [Spacer]).
 *
 * Composed on top of the [Spacer] in the [Box] we use `with` to set the `receiver` of a block to our
 * [SharedTransitionScope] parameter [sharedTransitionScope]. In its [SharedTransitionScope] `block`
 * we compose a [Column] (it will be animated in and out using the [SharedTransitionScope]) whose
 * [Modifier] argument `modifier` is a [Modifier.padding] that sets the padding on `all` sides to
 * 16.dp, with a [BoxScope.align] whose `alignment` argument is [Alignment.Center] chained to that,
 * followed by a [Modifier.clip] whose `shape` argument is the [Shapes.medium] of our custom
 * [MaterialTheme.shapes], with a [SharedTransitionScope.sharedBounds] chained to that whose
 * `sharedContentState` argument is a [SharedTransitionScope.rememberSharedContentState] whose `key`
 * argument is [FilterSharedElementKey], whose `animatedVisibilityScope` argument is our
 * [AnimatedVisibilityScope]`parameter [animatedVisibilityScope], whose `resizeMode` argument is
 * [SharedTransitionScope.ResizeMode.RemeasureToBounds] (remeasures and relayouts its child whenever
 * bounds change during the bounds transform), and whose `clipInOverlayDuringTransition` argument is
 * an [SharedTransitionScope.OverlayClip] whose [Shape] `shape` argument is the [Shapes.medium] of
 * our custom [MaterialTheme.shapes]. This is followed by [Modifier.wrapContentSize] to have it
 * measure at its desired size without regard to incoming minimum size constraints, with a
 * [Modifier.heightIn] whose maximum is 450.dp, followed by [Modifier.verticalScroll] whose `state`
 * argument is a [rememberScrollState], followed by a [Modifier.clickable] whose `indication` argument
 * is `null`, and whose `interactionSource` argument is a [remember] of a [MutableInteractionSource],
 * its `onClick` lambda argument is a do-nothing lambda. This is followed by a [Modifier.background]
 * whose [Color] `color` argument is the [JetsnackColors.uiFloated] of our custom
 * [JetsnackTheme.colors], followed by a [Modifier.padding] that sets the padding on `horizontal`
 * sides to `24.dp`, and the padding on vertical sides to `16.dp`, with a
 * [SharedTransitionScope.skipToLookaheadSize] at the end of the chain which enables a layout to
 * measure its children with the lookahead constraints, therefore laying out the children as if the
 * transition has finished.
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
 * [MaterialTheme.typography]. Next in the [Row] we initialize our [Boolean] variable
 * `val resetEnabled` to `true` if our [String] variable `sortState` is not equal to our [String]
 * variable `defaultFilter`. Then we compose an [IconButton] whose `onClick` argument is a do-nothing
 * lambda, and whose [Boolean] `enabled` argument is our [Boolean] variable `resetEnabled`. In the
 * `content` Composable lambda argument we initialize [FontWeight] variable to [FontWeight.Bold] if
 * [Boolean] variable `resetEnabled` is `true` or to [FontWeight.Normal] if it is `false`. Then we
 * compose a [Text] as the label whose `text` argument is the [String] with resource ID
 * `R.string.reset` ("Reset"), whose [TextStyle] `style` argument is the [Typography.bodyMedium] of
 * our custom [MaterialTheme.typography], whose [FontWeight] `fontWeight` argument is our [FontWeight]
 * variable `fontWeight` and whose [Color] `color` argument is a copy of the
 * [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors] with the `alpha` set to `0.38f`
 * if `resetEnabled` is `false` or to `1f` if it is `true`.
 *
 *  - Below the [Row] in the [Column] is a [SortFiltersSection] whose `sortState` argument is our
 *  [MutableState] wrapped [String] variable `sortState`, and whose lambda argument `onFilterChange`
 *  is a lambda which accepts the [Filter] passed the lambda in variable `filter` and sets `sortState`
 *  to the [Filter.name] of `filter`.
 *  - Next in the [Column] is a [FilterChipSection] whose `title` argument is the [String] with
 *  resource ID `R.string.price` ("Price"), and whose `filters` argument is our [List] of [Filter]
 *  variable `priceFilters`.
 *  - Next in the [Column] is a [FilterChipSection] whose `title` argument is the [String] with
 *  resource ID `R.string.category` ("Category"), and whose `filters` argument is our [List] of
 *  [Filter] variable `categoryFilters`.
 *  - Next in the [Column] is a [MaxCalories] whose `sliderPosition` argument is our [MutableState]
 *  wrapped [Float] variable `maxCalories`, and whose lambda argument `onValueChanged` which accepts
 *  the [Float] passed the lambda in variable `newValue` and sets `maxCalories` to `newValue`
 *  - Next in the [Column] is a [FilterChipSection] whose `title` argument is the [String] with
 *  resource ID `R.string.lifestyle` ("LifeStyle"), and whose `filters` argument is our [List] of
 *  [Filter] variable `lifeStyleFilters`.
 *
 * @param sharedTransitionScope the [SharedTransitionScope] that controls the animation we share with
 * the "Filters" [IconButton] in the [FilterBar] Composable.
 * @param animatedVisibilityScope the [AnimatedVisibilityScope] that controls our enter/exit
 * animations.
 * @param onDismiss a lambda we should call when the user indicates we should be dismissed.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
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
 * This is used by the [FilterScreen] Composable to display each of the different [List] of [Filter]
 * that it displays. First we compose a [FilterTitle] whose `text` argument is our [String] parameter
 * [title]. Then we compose a [FlowRow] whose `modifier` argument is a [Modifier.fillMaxWidth], with
 * a [Modifier.padding] chained to that that adds 12.dp padding to its `top`, and 16.dp to its
 * `bottom`, and another [Modifier.padding] is chained to that adds 4.dp padding to each of its
 * `horizontal` sides. In the [FlowRowScope] `content` Composable lambda argument of the [FlowRow]
 * we use the [forEach] extension function of the [List] of [Filter] parameter [filters] to loop
 * through each [Filter] in the [List] capturing the [Filter] in variable `filter`. In the `action`
 * lambda argument of the [forEach] we compose a [FilterChip] whose `filter` argument is our [Filter]
 * variable `filter`, and whose `modifier` argument is a [Modifier.padding] that adds 4.dp padding
 * to its `end` and 8.dp padding to its `bottom`.
 *
 * @param title the [String] title of the [FilterChipSection] to be displayed by a [FilterTitle]
 * @param filters the [List] of [Filter] to be displayed, each in a [FilterChip]
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
        filters.forEach { filter: Filter ->
            FilterChip(
                filter = filter,
                modifier = Modifier.padding(end = 4.dp, bottom = 8.dp)
            )
        }
    }
}

/**
 * This is used by the [FilterScreen] Composable to display a [FilterTitle] for [SortFilters] and
 * supply a [ColumnScope] for [SortFilters] to compose its [SortOption]'s into. First we compose a
 * [FilterTitle] whose `text` argument is the [String] with resource ID `R.string.sort` ("Sort").
 * Then we compose a [Column] whose `modifier` argument is a [Modifier.padding] that adds 24.dp to
 * the `bottom` of the [Column]. In the [ColumnScope] `content` Composable lambda argument of the
 * [Column] we compose a [SortFilters] whose `sortState` argument is our [MutableState] wrapped
 * [String] parameter [sortState], and whose `onFilterChange` lambda argument is our lambda parameter
 * [onFilterChange].
 *
 * @param sortState the [Filter.name] of the currently selected sort [Filter].
 * @param onFilterChange a lambda which can be called with a [Filter] to change the currently
 * selected sort [Filter]. Our caller [FilterScreen] passes us a lambda which accepts a [Filter]
 * in its `filter` variable and sets `sortState` to the [Filter.name] of `filter`.
 */
@Composable
fun SortFiltersSection(sortState: String, onFilterChange: (Filter) -> Unit) {
    FilterTitle(text = stringResource(id = R.string.sort))
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        SortFilters(
            sortState = sortState,
            onChanged = onFilterChange
        )
    }
}

/**
 * This is used by the [SortFiltersSection] Composable to display the different [Filter]s that are
 * available from the [SnackRepo.getSortFilters] method. We use the [forEach] extension function of
 * [List] to loop through each [Filter] in the [List] capturing the [Filter] in variable `filter`.
 * Then in the `action` lambda argument of the [forEach] we compose a [SortOption] whose `text`
 * argument is the [Filter.name] of `filter`, whose `icon` argument is the [Filter.icon] of `filter`,
 * whose `selected` argument is `true` if `sortState` is equal to the [Filter.name] of `filter`, and
 * whose `onClickOption` lambda argument is a lambda that calls our lambda parameter [onChanged] with
 * [Filter] variable `filter`.
 *
 * @param sortFilters the [List] of [Filter] to be displayed, each [Filter] in a [SortOption]. Our
 * caller [SortFiltersSection] doesn't pass us any so we use the default which is the [List] of
 * [Filter] returned by the [SnackRepo.getSortFilters] method.
 * @param sortState the [Filter.name] of the currently selected sort [Filter].
 * @param onChanged a lambda which can be called with a [Filter] to change the currently selected
 * sort [Filter]. Our caller [SortFiltersSection] passes us a lambda which accepts a [Filter] in
 * its `filter` variable and sets [sortState] to the [Filter.name] of `filter`.
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
 * This is used by [FilterScreen] to allow the user to set the maximum calories they want in their
 * [Snack]. Our first Composable is a [FlowRow] in whose [FlowRowScope] `content` Composable lambda
 * we compose a [FilterTitle] whose `text` argument is the [String] with resource ID
 * `R.string.max_calories` ("Max Calories"), then we compose a [Text] whose `text` argument is the
 * [String] with resource ID `R.string.per_serving` ("per serving"), whose [TextStyle] `style`
 * argument is the [Typography.bodyMedium] of our custom [MaterialTheme.typography], whose [Color]
 * `color` argument is the [JetsnackColors.brand] of our custom [JetsnackTheme.colors], and whose
 * `modifier` argument is a [Modifier.padding] that adds 5.dp to its `top` and 10.dp to its `start`.
 * The second composable is a [Slider] whose [Float] `value` argument is our [Float] parameter
 * [sliderPosition], whose `onValueChange` lambda argument is a lambda which accepts the [Float]
 * passed the lambda in variable `newValue` then calls our lambda taking [Float] parameter
 * [onValueChanged] with `newValue`, whose [ClosedFloatingPointRange] `valueRange` is the closed
 * range of 0f..300f, whose [Int] `steps` argument is 5, whose `modifier` argument is a
 * [Modifier.fillMaxWidth], and whose [SliderColors] `colors` argument is [SliderDefaults.colors]
 * with its [SliderColors.thumbColor] the [JetsnackColors.brand] of our custom [JetsnackTheme.colors],
 * with its [SliderColors.activeTrackColor] the [JetsnackColors.brand] of our custom
 * [JetsnackTheme.colors], and the [SliderColors.inactiveTrackColor] the
 * [JetsnackColors.iconInteractive] of our custom [JetsnackTheme.colors].
 *
 * @param sliderPosition the current [Float] position of the slider.
 * @param onValueChanged a lambda which can be called with a [Float] to change the current
 * [Float] position of the slider.
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
        onValueChange = { newValue: Float ->
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
 * Displays a "Title" for each of the [Filter] groupings. Our sole Composable is a [Text] whose
 * `text` argument is our [String] parameter [text], whose [TextStyle] `style` argument is the
 * [Typography.titleLarge] of our custom [MaterialTheme.typography], whose [Color] `color` argument
 * is the [JetsnackColors.brand] of our custom [JetsnackTheme.colors], and whose [Modifier] `modifier`
 * argument is a [Modifier.padding] that adds 8.dp to the `bottom`.
 *
 * @param text the [String] to use as the "Title".
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
 * Used by the [SortFilters] Composable to display each of the sort [Filter]'s in the [List] of
 * [Filter] it retrieves from the [SnackRepo.getSortFilters] method. Our root Composable is a [Row]
 * whose `modifier` argument is a [Modifier.padding] that adds 14.dp to the `top`, with a
 * [Modifier.selectable] chained to that whose `selected` argument our [Boolean] parameter [selected],
 * and the `onClick` lambda argument lambda that calls our lambda parameter [onClickOption]. In the
 * [RowScope] `content` Composable lambda argument of the [Row] if our [ImageVector] parameter [icon]
 * is not `null` we compose an [Icon] whose `imageVector` argument is our [ImageVector] parameter
 * [icon], and whose `contentDescription` argument is `null`. Next in the [Row] we compose a [Text]
 * whose `text` argument is our [String] parameter [text], whose [TextStyle] `style` argument is the
 * [Typography.titleMedium] of our custom [MaterialTheme.typography], and whose [Modifier] `modifier`
 * argument is a [Modifier.padding] that adds 10.dp to the `start`, with a [RowScope.weight] chained
 * to that with a `weight` of `1f` to have it fill the remaining space after its unweighted siblings
 * are measured and placed. If our [Boolean] parameter [selected] is `true` we compose an [Icon] whose
 * [ImageVector] `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Done] (a "checkmark"),
 * whose `contentDescription` argument is `null`, and whose [Color] `tint` argument is the
 * [JetsnackColors.brand] of our custom [JetsnackTheme.colors].
 *
 * @param text the [Filter.name] of the [Filter]
 * @param icon the [Filter.icon] of the [Filter]
 * @param onClickOption a lambda to be called when this [SortOption] is clicked.
 * @param selected `true` if this [SortOption] is the currently selected one.
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
