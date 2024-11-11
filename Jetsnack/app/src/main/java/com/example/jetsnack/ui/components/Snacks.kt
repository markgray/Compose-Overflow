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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.jetsnack.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetsnack.R
import com.example.jetsnack.model.CollectionType
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope
import com.example.jetsnack.ui.SnackSharedElementKey
import com.example.jetsnack.ui.SnackSharedElementType
import com.example.jetsnack.ui.home.Feed
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.snackDetailBoundsTransform
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * Width of a [JetsnackCard]
 */
private val HighlightCardWidth = 170.dp

/**
 * Padding of a [JetsnackCard]
 */
private val HighlightCardPadding = 16.dp

/**
 * Convenience extension property to obtain the sum of the width and padding of a [JetsnackCard]
 * from the `current` [LocalDensity].
 */
private val Density.cardWidthWithPaddingPx
    get() = (HighlightCardWidth + HighlightCardPadding).toPx()

/**
 * Displays its [SnackCollection] parameter [snackCollection] and all its [SnackCollection.snacks].
 * Our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In its [ColumnScope] `content` composable lambda argument it has:
 *  - a [Row] whose `verticalAlignment` argument is [Alignment.CenterVertically], and whose `modifier`
 *  argument is a [Modifier.heightIn] that constrains its minimum height to 56.dp to which is chained
 *  a [Modifier.padding] that adds 24.dp padding to the start of the [Row]. In its [RowScope] `content`
 *  composable lambda argument it holds:
 *
 *  - a [Text] whose `text` argument is the [SnackCollection.name] of our [SnackCollection] parameter
 *  [snackCollection], whose [TextStyle] `style` argument is the [Typography.titleLarge] of our custom
 *  [MaterialTheme.typography], whose [Color] `color` argument is the [JetsnackColors.brand] or our
 *  custom [JetsnackTheme.colors], whose `maxLines` argument is 1, whose [TextOverflow] `overflow`
 *  argument is [TextOverflow.Ellipsis] causing it to use an ellipsis to indicate that the text has
 *  overflowed the 1 line allowed it, and whose [Modifier] `modifier` argument is a [RowScope.weight]
 *  of 1f causing it to take up all the space left after its siblings have been measured and placed
 *  to which is chained a [Modifier.wrapContentWidth] whose `align` argument of [Alignment.Start]
 *  that allows it to occupy its desired width without regard to the incoming minimum width with it
 *  alignment at the start of the [Row],
 *  - an [IconButton] whose `onClick` argument is a do-nothing lambda, and whose [Modifier] `modifier`
 *  argument is a [RowScope.align] whose `alignment` argument of [Alignment.CenterVertically] causes
 *  it to be be vertically centered in the [Row]. The `content` composable lambda argument of the
 *  [IconButton] is an [Icon] whose [ImageVector] `imageVector` argument is that drawn by
 *  [Icons.AutoMirrored.Outlined.ArrowBack] (a backwards pointing arrow), whose [Color] `tint` argument
 *  is the [JetsnackColors.brand] or our custom [JetsnackTheme.colors], and whose `contentDescription`
 *  argument is `null`.
 *
 *   - Next in the [Column] if our [Boolean] parameter [highlight] is `true` and the
 *   [SnackCollection.type] of our [SnackCollection] parameter [snackCollection] is equal to
 *   [CollectionType.Highlight] is a [HighlightedSnacks] composable whose `snackCollectionId` argument
 *   is the [SnackCollection.id] of our [SnackCollection] parameter [snackCollection], whose `index`
 *   argument is our [Int] parameter [index], whose [List] of [Snack] argument `snacks` is the
 *   [SnackCollection.snacks] property of our [SnackCollection] parameter [snackCollection], and
 *   whose `onSnackClick` argument is the lambda taking [Long] and [String] parameter [onSnackClick].
 *   - Otherwise the next in the [Column] is a [Snacks] Composable whose `snackCollectionId` argument
 *   is the [SnackCollection.id] of our [SnackCollection] parameter [snackCollection], whose [List]
 *   of [Snack] argument `snacks` is the [SnackCollection.snacks] property of our [SnackCollection]
 *   parameter [snackCollection], and whose `onSnackClick` argument is the lambda taking [Long] and
 *   [String] parameter [onSnackClick].
 *
 * @param snackCollection the [SnackCollection] whose [List] of [Snack] field [SnackCollection.snacks]
 * we should display.
 * @param onSnackClick the lambda that each of the [JetsnackCard] displaying a [Snack] from our
 * [SnackCollection] should call with the [Snack.id] of the [Snack] and the [String] version of the
 * [SnackCollection.id] of our [SnackCollection] parameter [snackCollection].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. The `CartContent` composable calls us with a fancy [LazyItemScope.animateItem] that
 * animates the item placement within its Lazy list which we use as the `modifier` argument of our
 * root [Column] Composable, our other two callers pass us none so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 * @param index the position of this [SnackCollection] in a [List] of [SnackCollection] that are
 * being displayed. We use it to select the [List] of [Color] to use as the gradient of any
 * [HighlightSnackItem] from our [SnackCollection] parameter [snackCollection] that we display,
 * either [JetsnackColors.gradient6_1] for even values, or [JetsnackColors.gradient6_2] for odd.
 * @param highlight if `true` and the [SnackCollection.type] of our [SnackCollection] paramter
 * [snackCollection] is [CollectionType.Highlight] we use a [HighlightedSnacks] to display the
 * [List] of [Snack] in [SnackCollection.snacks] otherwise we use a [Snacks]. `CartContent` calls
 * us with `false`, as does the `Body` composable used by [SnackDetail], but the `SnackCollectionList`
 * composable used by [Feed] passes us none so the default of `true` is used.
 */
@Composable
fun SnackCollection(
    snackCollection: SnackCollection,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    highlight: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(start = 24.dp)
        ) {
            Text(
                text = snackCollection.name,
                style = MaterialTheme.typography.titleLarge,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 1f)
                    .wrapContentWidth(align = Alignment.Start)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    tint = JetsnackTheme.colors.brand,
                    contentDescription = null
                )
            }
        }
        if (highlight && snackCollection.type == CollectionType.Highlight) {
            HighlightedSnacks(
                snackCollectionId = snackCollection.id,
                index = index,
                snacks = snackCollection.snacks,
                onSnackClick = onSnackClick
            )
        } else {
            Snacks(
                snackCollectionId = snackCollection.id,
                snacks = snackCollection.snacks,
                onSnackClick = onSnackClick
            )
        }
    }
}

/**
 * This [Composable] is used to display the [List] of [Snack] in [SnackCollection.snacks] if the
 * [SnackCollection] Composable is called with its [Boolean] parameter `highlight` `true` and the
 * [SnackCollection.type] of the [SnackCollection] is [CollectionType.Highlight]. We start by
 * initializing and remembering our [LazyListState] variable `val rowState` to a new instance. We
 * initialize our [Float] variable `val cardWidthWithPaddingPx` to the value returned by the
 * [Density.cardWidthWithPaddingPx] extension property for the current [LocalDensity]. We initialize
 * our lambda returning [Float] variable `val scrollProvider` to a function that returns the result
 * of performing a simple calculation of scroll distance for homogenous item types with the same width.
 * We initialize our [List] of [Color] variable `val gradient` to the [JetsnackColors.gradient6_1]
 * of our custom [JetsnackTheme.colors] if our [Int] parameter [index] is even or to the
 * [JetsnackColors.gradient6_2] if it is odd.
 *
 * Our root composable is a [LazyRow] whose `state` argument is our [LazyListState] variable
 * `rowState`, its `modifier` argument is our [Modifier] parameter [modifier], whose
 * `horizontalArrangement` argument is a [Arrangement.spacedBy] that places its children spaced by
 * 16.dp, and whose `contentPadding` argument is a [PaddingValues] whose `start` is 24.dp and whose
 * `end` is 24.dp.
 *
 * In the [LazyListScope] `content` composable lambda of the [LazyRow] we have an
 * [LazyListScope.itemsIndexed] whose `items` argument is our [List] of [Snack] parameter [snacks],
 * and in its [LazyItemScope] `itemContent` composable lambda we accept the index of the [Snack] in
 * the [List] of [Snack] parameter [snacks] in the [Int] variable `index` and the [Snack] in the
 * [Snack] variable `snack`, then for each of these [Snack] we call the [HighlightSnackItem]
 * composable with its [Long] `snackCollectionId` argument our [Long] parameter [snackCollectionId],
 * its [Snack] `snack` argument our current [Snack] variable `snack`, its `onSnackClick` argument our
 * lambda parameter [onSnackClick], its `index` argument our [Int] parameter [index], its `gradient`
 * argument our [List] of [Color] variable `gradient`, and its `scrollProvider` argument our lambda
 * variable `scrollProvider`.
 *
 * @param snackCollectionId the [SnackCollection.id] of the [SnackCollection] being displayed.
 * @param index the index of the [SnackCollection] in the [LazyColumn] that our [SnackCollection]
 * Composable is being displayed in if it matters to its caller or the default of 0. We use it to
 * select different [List] of [Color] to be used as the `gradient` argument of all of our
 * [HighlightSnackItem]'s depending on whether [index] is even or odd.
 * @param snacks the [List] of [Snack] that we are to display.
 * @param onSnackClick the lambda that each [HighlightSnackItem] should call with the [Snack.id]
 * of the [Snack] it is displaying and the [String] version of the [SnackCollection.id] of the
 * [SnackCollection] being displayed.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
private fun HighlightedSnacks(
    snackCollectionId: Long,
    index: Int,
    snacks: List<Snack>,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowState: LazyListState = rememberLazyListState()
    val cardWidthWithPaddingPx: Float = with(LocalDensity.current) { cardWidthWithPaddingPx }

    val scrollProvider: () -> Float = {
        // Simple calculation of scroll distance for homogenous item types with the same width.
        val offsetFromStart = cardWidthWithPaddingPx * rowState.firstVisibleItemIndex
        offsetFromStart + rowState.firstVisibleItemScrollOffset
    }

    val gradient: List<Color> = when ((index / 2) % 2) {
        0 -> JetsnackTheme.colors.gradient6_1
        else -> JetsnackTheme.colors.gradient6_2
    }

    LazyRow(
        state = rowState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        itemsIndexed(items = snacks) { index: Int, snack: Snack ->
            HighlightSnackItem(
                snackCollectionId = snackCollectionId,
                snack = snack,
                onSnackClick = onSnackClick,
                index = index,
                gradient = gradient,
                scrollProvider = scrollProvider
            )
        }
    }
}

/**
 * This [Composable] is used to display the [List] of [Snack] in [SnackCollection.snacks] if the
 * [SnackCollection] Composable is called with its [Boolean] parameter `highlight` `false` or if
 * the [SnackCollection.type] of the [SnackCollection] is _not_ [CollectionType.Highlight].
 * Our root composable is a [LazyRow] whose `modifier` argument is our [Modifier] parameter
 * [modifier], and whose `contentPadding` argument is a [PaddingValues] that adds 12.dp padding
 * to the `start` and 12.dp to the `end`. In the [LazyListScope] `content` composable lambda of the
 * [LazyRow] we have an [LazyListScope.items] whose `items` argument is our [List] of [Snack]
 * parameter [snacks], and in its [LazyItemScope] `itemContent` composable lambda we accept the
 * [Snack] passed the lambda in our `snack` variable then for each of the [Snack] passed it the
 * lambda composes a [SnackItem] whose [Snack] `snack` argument is our `snack` variable, whose
 * `snackCollectionId` argument is [Snacks] parameter [snackCollectionId], and whose `onSnackClick`
 * argument is [Snacks] lambda parameter [onSnackClick].
 *
 * @param snackCollectionId the [SnackCollection.id] of the [SnackCollection] being displayed.
 * @param snacks the [List] of [Snack] that we are to display.
 * @param onSnackClick the lambda that each [SnackItem] should call with the [Snack.id] of the
 * [Snack] it is displaying and the [String] version of the [SnackCollection.id] of the
 * [SnackCollection] being displayed.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
private fun Snacks(
    snackCollectionId: Long,
    snacks: List<Snack>,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
    ) {
        items(items = snacks) { snack: Snack ->
            SnackItem(
                snack = snack,
                snackCollectionId = snackCollectionId,
                onSnackClick = onSnackClick
            )
        }
    }
}

/**
 * This is used by [Snacks] to display each of the [Snack] in its [List] of [Snack] parameter. Our
 * root Composable is a [JetsnackSurface] whose `shape` argument is the [Shapes.medium] of our custom
 * [MaterialTheme.shapes] (which is a [RoundedCornerShape] whose `size` is 20.dp), and whose `modifier`
 * argument chains a [Modifier.padding] to our [Modifier] parameter [modifier] that adds 4.dp to the
 * `start`, 4.dp to the `end` and 8.dp to the `bottom`. In its `content` composable lambda argument
 * we initialize our [SharedTransitionScope] variable `val sharedTransitionScope` to the current
 * [LocalSharedTransitionScope] or throw an [IllegalStateException] ("No sharedTransitionScope found").
 * We initialize our [AnimatedVisibilityScope] variable `val animatedVisibilityScope` to the current
 * [LocalNavAnimatedVisibilityScope] or throw an [IllegalStateException] ("No animatedVisibilityScope found")
 *
 * Then `with` the `receiver` [SharedTransitionScope] variable `sharedTransitionScope` we Compose a
 * [Column] whose `horizontalAlignment` argument is [Alignment.CenterHorizontally], and whose `modifier`
 * argument is a [Modifier.clickable] whose `onClick` argument is a lambda that calls our lambda parameter
 * [onSnackClick] with the [Snack.id] of our [Snack] parameter [snack], and the [String] version of our
 * [Long] parameter [snackCollectionId], and to this is chained a [Modifier.padding] that adds 8.dp
 * padding to all sides of the [Column]. In the [ColumnScope] `content` lambda argument of the [Column]
 * we have:
 *  - a [SnackImage] whose [Int] `imageRes` argument is the [Snack.imageRes] property of our [Snack]
 *  parameter [snack] (the resource ID of the jpg to display), whose `elevation` argument is 1.dp,
 *  whose `contentDescription` argument is `null`, and whose `modifier` argument is a [Modifier.size]
 *  that sets its size to 120.dp, to which is chained a [SharedTransitionScope.sharedBounds] whose
 *  `sharedContentState` argument is a remembered [SharedContentState] whose `key` is a
 *  [SnackSharedElementKey] constructed with its `snackId` argument the [Snack.id] of [snack], whose
 *  `origin` argument is the [String] version of [snackCollectionId], and whose `type` argument is
 *  [SnackSharedElementType.Image], the `animatedVisibilityScope` argument of the
 *  [SharedTransitionScope.sharedBounds] is our [AnimatedVisibilityScope] variable `animatedVisibilityScope`,
 *  and its `boundsTransform` argument is our global [BoundsTransform] property  [snackDetailBoundsTransform].
 *  - a [Text] whose `text` argument is the [Snack.name] of our [Snack] parameter [snack], whose [TextStyle]
 *  `style` argument is the [Typography.titleMedium] of our custom [MaterialTheme.typography], whose
 *  [Color] argument `color` is the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors],
 *  and whose `modifier` argument is a [Modifier.padding] that adds 8.dp padding to the `top` of the
 *  [Text], to which is chained a [Modifier.wrapContentWidth] that allows it to occupy its desired
 *  width without regard to the incoming minimum width constraint, and finally in the [Modifier] chain
 *  is a [SharedTransitionScope.sharedBounds] whose `sharedContentState` argument is a remembered
 *  [SharedContentState] whose `key` is a [SnackSharedElementKey] constructed with its `snackId`
 *  argument the [Snack.id] of [snack], whose `origin` argument is the [String] version of
 *  [snackCollectionId], and whose `type` argument is [SnackSharedElementType.Title], the
 *  `animatedVisibilityScope` argument of the [SharedTransitionScope.sharedBounds] is our
 *  [AnimatedVisibilityScope] variable `animatedVisibilityScope`, its [EnterTransition] argument `enter`
 *  is a [fadeIn] whose `animationSpec` is our [nonSpatialExpressiveSpring] spring, its [ExitTransition]
 *  argument `exit` is a [fadeOut] whose `animationSpec` is our [nonSpatialExpressiveSpring] spring,
 *  its [ResizeMode] argument `resizeMode` is a [ResizeMode.ScaleToBounds] (will scale the stable
 *  layout based on the animated size), and its `boundsTransform` argument is our global
 *  [BoundsTransform] property  [snackDetailBoundsTransform].
 *
 * @param snack the [Snack] we are to display.
 * @param snackCollectionId the [SnackCollection.id] of the [SnackCollection] being displayed by [Snacks]
 * @param onSnackClick the lambda that the Composable we compose should call with the [Snack.id] of the
 * [Snack] it is displaying and the [String] version of our [snackCollectionId] parameter.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
fun SnackItem(
    snack: Snack,
    snackCollectionId: Long,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(
            start = 4.dp,
            end = 4.dp,
            bottom = 8.dp
        )

    ) {
        val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No sharedTransitionScope found")
        val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No animatedVisibilityScope found")

        with(receiver = sharedTransitionScope) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(onClick = {
                        onSnackClick(snack.id, snackCollectionId.toString())
                    })
                    .padding(all = 8.dp)
            ) {
                SnackImage(
                    imageRes = snack.imageRes,
                    elevation = 1.dp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = 120.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = snack.id,
                                    origin = snackCollectionId.toString(),
                                    type = SnackSharedElementType.Image
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = snackDetailBoundsTransform
                        )
                )
                Text(
                    text = snack.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = JetsnackTheme.colors.textSecondary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .wrapContentWidth()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = snack.id,
                                    origin = snackCollectionId.toString(),
                                    type = SnackSharedElementType.Title
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            enter = fadeIn(animationSpec = nonSpatialExpressiveSpring()),
                            exit = fadeOut(animationSpec = nonSpatialExpressiveSpring()),
                            resizeMode = ResizeMode.ScaleToBounds(),
                            boundsTransform = snackDetailBoundsTransform
                        )
                )
            }
        }
    }
}

/**
 * This is used by [HighlightedSnacks] to display each of the [Snack] in its [List] of [Snack]
 * parameter. We start by initialize our [SharedTransitionScope] variable `val sharedTransitionScope`
 * to the current [LocalSharedTransitionScope] or throw [IllegalStateException] ("No Scope found")
 * if it is `null`. We initialize our [AnimatedVisibilityScope] variable `val animatedVisibilityScope`
 * to the current [LocalNavAnimatedVisibilityScope] or throw [IllegalStateException] ("No Scope found")
 * if it is `null`. Then `with` [SharedTransitionScope] variable `sharedTransitionScope` as the
 * receiver we execute a lambda `block` wherein we:
 *  - Initialize our animated [Dp] variable `val roundedCornerAnimation` to a [Transition.animateDp]
 *  of `animatedVisibilityScope` whose `label` is "rounded corner" and whose [EnterExitState] fed
 *  to the `targetValueByState` lambda argument choses 0.dp for [EnterExitState.PreEnter], 20.dp
 *  for [EnterExitState.Visible] and 20.dp for [EnterExitState.PostExit].
 *
 * Then our root Composable is a [JetsnackCard] whose arguments are:
 *  - `elevation` 0.dp (the [Dp] to use as the `elevation` argument of its [JetsnackSurface])
 *  - `shape` is a [RoundedCornerShape] whose `size` is our animated [Dp] variable
 *  `roundedCornerAnimation` (the [Shape] to use as the `shape` argument of its [JetsnackSurface])
 *  - `modifier` we chain a [Modifier.padding] that adds 16.dp padding to its `bottom` to our
 *  [Modifier] parameter [modifier], followed by a [SharedTransitionScope.sharedBounds] whose
 *  `sharedContentState` argument is a remembered [SharedContentState] whose `key` is a
 *  [SnackSharedElementKey] constructed with its `snackId` argument the [Snack.id] of [snack], whose
 *  `origin` argument is the [String] version of [snackCollectionId], and whose `type` argument is
 *  [SnackSharedElementType.Bounds], the `animatedVisibilityScope` argument of the
 *  [SharedTransitionScope.sharedBounds] is our [AnimatedVisibilityScope] variable `animatedVisibilityScope`,
 *  its `boundsTransform` argument is our global [BoundsTransform] property  [snackDetailBoundsTransform],
 *  its `clipInOverlayDuringTransition` argument is an [OverlayClip] whose `clipShape` is a
 *  [RoundedCornerShape] whose `size` is our animated [Dp] variable `roundedCornerAnimation` (used
 *  to specify the clipping for when the shared element is going through an active transition towards
 *  a new target bounds), the `enter` argument of the [SharedTransitionScope.sharedBounds] is [fadeIn]
 *  and its `exit` argument is [fadeOut]. And to this is chained a [Modifier.size] whose `width` is
 *  our constant [HighlightCardWidth] (170.dp), and whose `height` is 256.dp, and at the end of the
 *  chain is a [Modifier.border] whose `width` argument is 1.dp, whose `color` argument is a copy
 *  of the [JetsnackColors.uiBorder] of our custom [JetsnackTheme.colors] whoe `alpha` is 0.12f, and
 *  whose `shape` argument is a [RoundedCornerShape] whose `size` is our animated [Dp] variable
 *  `roundedCornerAnimation`.
 *
 * In the `content` Composable lambda argument of the [JetsnackCard]
 *
 * @param snackCollectionId the [SnackCollection.id] of the [SnackCollection] being displayed by
 * [HighlightedSnacks].
 * @param snack the [Snack] we are to display.
 * @param onSnackClick the lambda that the Composable we compose should call with the [Snack.id] of
 * the [Snack] it is displaying and the [String] version of our [snackCollectionId] parameter.
 * @param index the index of our position in the [List] of [Snack] that [HighlightedSnacks] is
 * reading our [Snack] parameter [snack] from.
 * @param gradient the [List] of [Color] we should use as the `colors` argument of our
 * [Modifier.offsetGradientBackground] used to draw a backgound behind our [SnackImage].
 * @param scrollProvider lambda that provides a [Float] that is a simple calculation of scroll
 * distance for homogenous item types with the same width.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
private fun HighlightSnackItem(
    snackCollectionId: Long,
    snack: Snack,
    onSnackClick: (Long, String) -> Unit,
    index: Int,
    gradient: List<Color>,
    scrollProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No Scope found")
    val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No Scope found")
    with(sharedTransitionScope) {
        val roundedCornerAnimation: Dp by animatedVisibilityScope.transition
            .animateDp(label = "rounded corner") { enterExit: EnterExitState ->
                when (enterExit) {
                    EnterExitState.PreEnter -> 0.dp
                    EnterExitState.Visible -> 20.dp
                    EnterExitState.PostExit -> 20.dp
                }
            }
        JetsnackCard(
            elevation = 0.dp,
            shape = RoundedCornerShape(size = roundedCornerAnimation),
            modifier = modifier
                .padding(bottom = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = snack.id,
                            origin = snackCollectionId.toString(),
                            type = SnackSharedElementType.Bounds
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = snackDetailBoundsTransform,
                    clipInOverlayDuringTransition = OverlayClip(
                        clipShape = RoundedCornerShape(
                            roundedCornerAnimation
                        )
                    ),
                    enter = fadeIn(),
                    exit = fadeOut()
                )
                .size(
                    width = HighlightCardWidth,
                    height = 250.dp
                )
                .border(
                    width = 1.dp,
                    color = JetsnackTheme.colors.uiBorder.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(size = roundedCornerAnimation)
                )

        ) {
            Column(
                modifier = Modifier
                    .clickable(onClick = {
                        onSnackClick(
                            snack.id,
                            snackCollectionId.toString()
                        )
                    })
                    .fillMaxSize()

            ) {
                Box(
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(
                                    key = SnackSharedElementKey(
                                        snackId = snack.id,
                                        origin = snackCollectionId.toString(),
                                        type = SnackSharedElementType.Background
                                    )
                                ),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = snackDetailBoundsTransform,
                                enter = fadeIn(nonSpatialExpressiveSpring()),
                                exit = fadeOut(nonSpatialExpressiveSpring()),
                                resizeMode = ResizeMode.ScaleToBounds()
                            )
                            .height(100.dp)
                            .fillMaxWidth()
                            .offsetGradientBackground(
                                colors = gradient,
                                width = {
                                    // The Cards show a gradient which spans 6 cards and
                                    // scrolls with parallax.
                                    6 * cardWidthWithPaddingPx
                                },
                                offset = {
                                    val left = index * cardWidthWithPaddingPx
                                    val gradientOffset = left - (scrollProvider() / 3f)
                                    gradientOffset
                                }
                            )
                    )

                    SnackImage(
                        imageRes = snack.imageRes,
                        contentDescription = null,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(
                                    key = SnackSharedElementKey(
                                        snackId = snack.id,
                                        origin = snackCollectionId.toString(),
                                        type = SnackSharedElementType.Image
                                    )
                                ),
                                animatedVisibilityScope = animatedVisibilityScope,
                                exit = fadeOut(nonSpatialExpressiveSpring()),
                                enter = fadeIn(nonSpatialExpressiveSpring()),
                                boundsTransform = snackDetailBoundsTransform
                            )
                            .align(Alignment.BottomCenter)
                            .size(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = snack.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    color = JetsnackTheme.colors.textSecondary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = snack.id,
                                    origin = snackCollectionId.toString(),
                                    type = SnackSharedElementType.Title
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            enter = fadeIn(nonSpatialExpressiveSpring()),
                            exit = fadeOut(nonSpatialExpressiveSpring()),
                            boundsTransform = snackDetailBoundsTransform,
                            resizeMode = ResizeMode.ScaleToBounds()
                        )
                        .wrapContentWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = snack.tagline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = JetsnackTheme.colors.textHelp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = snack.id,
                                    origin = snackCollectionId.toString(),
                                    type = SnackSharedElementType.Tagline
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            enter = fadeIn(nonSpatialExpressiveSpring()),
                            exit = fadeOut(nonSpatialExpressiveSpring()),
                            boundsTransform = snackDetailBoundsTransform,
                            resizeMode = ResizeMode.ScaleToBounds()
                        )
                        .wrapContentWidth()
                )
            }
        }
    }
}

/**
 *
 */
@Composable
fun
    debugPlaceholder(@DrawableRes debugPreview: Int): Painter? =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else {
        null
    }

/**
 *
 */
@Composable
fun SnackImage(
    @DrawableRes
    imageRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    JetsnackSurface(
        elevation = elevation,
        shape = CircleShape,
        modifier = modifier
    ) {

        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(data = imageRes)
                .crossfade(enable = true)
                .build(),
            placeholder = debugPlaceholder(debugPreview = R.drawable.placeholder),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 *
 */
@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun SnackCardPreview() {
    val snack = snacks.first()
    JetsnackPreviewWrapper {
        HighlightSnackItem(
            snackCollectionId = 1,
            snack = snack,
            onSnackClick = { _, _ -> },
            index = 0,
            gradient = JetsnackTheme.colors.gradient6_1,
            scrollProvider = { 0f }
        )
    }
}

/**
 *
 */
@Composable
fun JetsnackPreviewWrapper(content: @Composable () -> Unit) {
    JetsnackTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedVisibilityScope provides this
                ) {
                    content()
                }
            }
        }
    }
}
