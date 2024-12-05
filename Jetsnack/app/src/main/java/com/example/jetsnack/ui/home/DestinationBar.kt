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

package com.example.jetsnack.ui.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetsnack.R
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackPreviewWrapper
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * Displayed at the top of the [Feed] and [Cart] screens, it just shows a [TopAppBar] containing
 * a [Text] displaying the constant [String] "Delivery to 1600 Amphitheater Way". We start by
 * initializing our [SharedTransitionScope] variable `val sharedElementScope` with the current
 * [LocalSharedTransitionScope] (or throw [IllegalStateException] if none is available) and initialize
 * our [AnimatedVisibilityScope] variable `val navAnimatedScope` with the current
 * [LocalNavAnimatedVisibilityScope] (or throw [IllegalStateException] if none is available).
 * Then `with` our [SharedTransitionScope] variable `sharedElementScope` we compose a [Column]
 * whose [Modifier] `modifier` argument chains to our [Modifier] parameter [modifier] a
 * [SharedTransitionScope.renderInSharedTransitionScopeOverlay] to have it render its content in the
 * [SharedTransitionScope]'s overlay, and then a [AnimatedVisibilityScope.animateEnterExit] that
 * uses as its `enter` and `exit` parameters [slideInVertically] and [slideOutVertically] with the
 * `animationSpec` parameter set to [spatialExpressiveSpring] and its `initialOffsetY` parameter
 * a lambda that multiplies the `fullHeight` [Int] passed the lambda by `-it * 2`. In the [ColumnScope]
 * `content` Composable lambda we compose a [TopAppBar] whose `windowInsets` parameter is a [WindowInsets]
 * whose `left` = 0, `top` = 0, `right` = 0, and `bottom` = 0, the `title` parameter is a [Row] in whose
 * [RowScope] `content` Composable lambda we compose:
 *  - a [Text] whose `text` parameter is "Delivery to 1600 Amphitheater Way", its [TextStyle] `style`
 *  argument is the [Typography.titleMedium] of our custom [MaterialTheme.typography], its [Color]
 *  `color` argument is the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors], its
 *  [TextAlign] `textAlign` argument is [TextAlign.Center] to center the text, its `maxLines` argument
 *  is `1`, its [TextOverflow] `overflow` argument is [TextOverflow.Ellipsis] to Use an ellipsis to
 *  indicate that the text has overflowed, and its [Modifier] `modifier` argument is a [RowScope.weight]
 *  whose `weight` argument of `1f` causes it to occupy all of the [Row]'s incoming width constraint
 *  after its unweighted siblings have been measured and placed, and chained to that is a [RowScope.align]
 *  whose `alignment` argument is [Alignment.CenterVertically] to align the [Text] to the vertical
 *  center of the [Row].
 *  - an [IconButton] whose `onClick` argument is a do-nothing lambda, and its [Modifier] `modifier`
 *  argument is a [RowScope.align] whose `alignment` argument is [Alignment.CenterVertically] to
 *  align the [IconButton] to the vertical center of the [Row]. In the [IconButton] `content`
 *  Composable lambda argument is an [Icon] whose [ImageVector] `imageVector` argument is
 *  [Icons.Outlined.ExpandMore] (a downward pointing caret), its [Color] `tint` argument is the
 *  [JetsnackColors.brand] of our custom [JetsnackTheme.colors], and its `contentDescription`
 *  argument is the [String] with resource ID `R.string.label_select_delivery` ("Select delivery
 *  address").
 *
 * The [List] of [Color] `colors` argument of the [TopAppBar] is a copy of the
 * [TopAppBarDefaults.topAppBarColors] with the [Color] `containerColor` value set to a copy of the
 * [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors] with the `alpha` value set to
 * [AlphaNearOpaque], and the [Color] `titleContentColor` argument is [JetsnackColors.textSecondary]
 * of our custom [JetsnackTheme.colors].
 *
 * Below the [TopAppBar] in the [Column] we compose a [JetsnackDivider].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [Cart] passes us a [BoxScope.align] whose `alignment` parameter is [Alignment.TopCenter]
 * that aligns us to the top center of the screen, but [Feed] passes none so the empty, default, or
 * starter [Modifier] that contains no elements is used.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationBar(modifier: Modifier = Modifier) {
    val sharedElementScope: SharedTransitionScope =
        LocalSharedTransitionScope.current ?: throw IllegalStateException("No shared element scope")
    val navAnimatedScope: AnimatedVisibilityScope =
        LocalNavAnimatedVisibilityScope.current ?: throw IllegalStateException("No nav scope")
    with(sharedElementScope) {
        with(navAnimatedScope) {
            Column(
                modifier = modifier
                    .renderInSharedTransitionScopeOverlay()
                    .animateEnterExit(
                        enter = slideInVertically(spatialExpressiveSpring()) { -it * 2 },
                        exit = slideOutVertically(spatialExpressiveSpring()) { -it * 2 }
                    )
            ) {
                TopAppBar(
                    windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
                    title = {
                        Row {
                            Text(
                                text = "Delivery to 1600 Amphitheater Way",
                                style = MaterialTheme.typography.titleMedium,
                                color = JetsnackTheme.colors.textSecondary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 1f)
                                    .align(alignment = Alignment.CenterVertically)
                            )
                            IconButton(
                                onClick = { /* todo */ },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ExpandMore,
                                    tint = JetsnackTheme.colors.brand,
                                    contentDescription = stringResource(R.string.label_select_delivery)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = JetsnackTheme.colors.uiBackground
                            .copy(alpha = AlphaNearOpaque),
                        titleContentColor = JetsnackTheme.colors.textSecondary
                    ),
                )
                JetsnackDivider()
            }
        }
    }
}

/**
 * Three previews of our [DestinationBar] using different device settings.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun PreviewDestinationBar() {
    JetsnackPreviewWrapper {
        DestinationBar()
    }
}
