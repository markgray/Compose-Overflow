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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.jetsnack.ui.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.navigation.MainDestinations
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * The Composable displayed for the route [HomeSections.FEED.route], which is the first destination
 * of the app. We start by initializing and remembering our [List] of [SnackCollection]s variable
 * `val snackCollections` from the [SnackRepo.getSnacks] method, and our [List] of [Filter]s variable
 * `val filters` from the [SnackRepo.getFilters] method. We then compose our [Feed] override with its
 * [List] of [SnackCollection] argument our `snackCollections` variable, its [List] of [Filter]
 * argument our `filters` variable, its `onSnackClick` lambda argument our lambda taking [Long] and
 * [String] parameter [onSnackClick], and its [Modifier] `modifier` argument our [Modifier] parameter
 * [modifier].
 *
 * @param onSnackClick function called with the [Snack.id] of the [Snack] and a [String] when the
 * Composable displaying it is clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us one that traces back to a [Modifier.padding] that adds the
 * [PaddingValues] that the [JetsnackScaffold] in [MainContainer] passes to its `content`, with
 * a [Modifier.consumeWindowInsets] chained to that which consumes the same [PaddingValues].
 */
@Composable
fun Feed(
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackCollections: List<SnackCollection> = remember { SnackRepo.getSnacks() }
    val filters: List<Filter> = remember { SnackRepo.getFilters() }
    Feed(
        snackCollections = snackCollections,
        filters = filters,
        onSnackClick = onSnackClick,
        modifier = modifier
    )
}

/**
 * Private override of the [Feed] Composable that is called by the public [Feed] Composable (since
 * it also contains state I am not sure why it is needed, if it were stateless it would make more
 * sense). Our root Composable is a [JetsnackSurface] whose [Modifier] `modifier` argument uses our
 * [Modifier] parameter [modifier] with a [Modifier.fillMaxSize] chained to that that causess it to
 * occupy its entire incoming size constraints. In its `content` Composable lambda argument we
 * initialize and remember our [MutableState] wrapped [Boolean] variable `var filtersVisible` to
 * an initial value of `false`. Then we Compose a [SharedTransitionLayout] (creates a layout and a
 * SharedTransitionScope for the child layouts in its `content` argument. Any child (direct or
 * indirect) of the SharedTransitionLayout can use the receiver scope SharedTransitionScope to
 * create shared element or shared bounds transitions). In its [SharedTransitionScope] `content`
 * composable lambda we have [Box] in whose [BoxScope] `content` Composable lambda argument we have:
 *  - a [SnackCollectionList] whose [List] of [SnackCollection] argument `snackCollections` is our
 *  [List] of [SnackCollection] parameter [snackCollections], whose [List] of [Filter] `filters`
 *  argument is our [List] of [Filter] parameter [filters], whose [Boolean] `filtersVisible`
 *  argument is our [MutableState] wrapped [Boolean] variable `filtersVisible`, whose
 *  `onFiltersSelected` lambda argument is a lambda that sets `filtersVisible` to `true`,
 *  whose [SharedTransitionScope] `sharedTransitionScope` argument is `this` [SharedTransitionLayout],
 *  and whose `onSnackClick` lambda  argument taking [Long] and [String] is our [onSnackClick]
 *  lambda  parameter taking [Long] and [String].
 *  - a [DestinationBar] (displays over the top of the [SnackCollectionList], which has a [Spacer]
 *  at its top to compensate for this).
 *  - an [AnimatedVisibility] (animates the appearance and disappearance of its [AnimatedVisibilityScope]
 *  `content` Composable lambda argument, as its [Boolean] `visible` argument changes). Its [Boolean]
 *  `visible` argument is our [MutableState] wrapped [Boolean] variable `filtersVisible`, its
 *  [EnterTransition] `enter` argument is a [fadeIn] animation, and its [ExitTransition] `exit`
 *  argument is a [fadeOut] animation. In its [AnimatedVisibilityScope] `content` Composable lambda
 *  we compose a [FilterScreen] whose [SharedTransitionScope] `sharedTransitionScope` argument is
 *  `this` [SharedTransitionLayout], whose `animatedVisibilityScope` argument is `this`
 *  [AnimatedVisibility]. Its `onDismiss` lambda argument is a lambda that sets `filtersVisible` to
 *  `false`.
 *
 * @param snackCollections the [List] of [SnackCollection] that we are supposed to display
 * @param filters the [List] of [Filter] that is displayed by the [FilterBar] in the
 * [SnackCollectionList] Composable.
 * @param onSnackClick a lambda that should be called with the [Snack.id] of the [Snack] and a
 * [String] when a composable displaying a [Snack] is clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller (the other [Feed] override) passes us the [Modifier] passed it in its own
 * `modifier` parameter and it traces back to a [Modifier.padding] that adds the [PaddingValues]
 * that the [JetsnackScaffold] in [MainContainer] passes to its `content`, with a
 * [Modifier.consumeWindowInsets] chained to that which consumes the same [PaddingValues].
 */
@Composable
private fun Feed(
    snackCollections: List<SnackCollection>,
    filters: List<Filter>,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        var filtersVisible: Boolean by remember {
            mutableStateOf(value = false)
        }
        SharedTransitionLayout {
            Box {
                SnackCollectionList(
                    snackCollections = snackCollections,
                    filters = filters,
                    filtersVisible = filtersVisible,
                    onFiltersSelected = {
                        filtersVisible = true
                    },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onSnackClick = onSnackClick
                )
                DestinationBar()
                AnimatedVisibility(visible = filtersVisible, enter = fadeIn(), exit = fadeOut()) {
                    FilterScreen(
                        animatedVisibilityScope = this@AnimatedVisibility,
                        sharedTransitionScope = this@SharedTransitionLayout
                    ) { filtersVisible = false }
                }
            }
        }
    }
}

/**
 * Composable that is called by the [Feed] Composable to display its [List] of [SnackCollection]
 * parameter. Our root Composable is a [LazyColumn] whose [Modifier] `modifier` argument is our
 * [Modifier] parameter [modifier]. In the [LazyListScope] `content` Composable lambda argument we
 * have:
 *  - a [LazyListScope.item] whose [LazyItemScope] `content` Composable lambda argument holds a
 *  [Spacer] whose `modifier` argument is a [Modifier.windowInsetsTopHeight] which adds a
 *  [WindowInsets] whose `top` is 56.dp to the [WindowInsets.Companion.statusBars] (this compensates
 *  for both the status bar and the [DestinationBar] that is composed on top of us). Next in the
 *  [LazyListScope.item] is a [FilterBar] whose `filters` argument is our [List] of [Filter] parameter
 *  [filters], whose `sharedTransitionScope` argument is our [SharedTransitionScope] parameter
 *  [sharedTransitionScope], whose `filterScreenVisible` argument is our [Boolean] parameter
 *  [filtersVisible], and whose `onShowFilters` lambda argument our lambda parameter [onFiltersSelected].
 *  - a [LazyListScope.itemsIndexed] whose `items` argument is our [List] of [SnackCollection] parameter
 *  [snackCollections]. In its [LazyItemScope] `itemContent` Composable lambda it passes the index of
 *  the current [SnackCollection] in [Int] variable `index` and the  [SnackCollection] in the variable
 *  `snackCollection`. Then for each element in the [List] of [SnackCollection] parameter [snackCollections]
 *  if its `index` is greater than 0 we compose a [JetsnackDivider] whose `thickness` is 2.dp, then
 *  for all values of `index` we compose a [SnackCollection] whose `snackCollection` argument is the
 *  variable `snackCollection`, whose `onSnackClick` argument is our lambda parameter [onSnackClick],
 *  and whose `index` argument is our `[Int] variable `index`.
 *
 * @param snackCollections the [List] of [SnackCollection] that we are supposed to display.
 * @param filters the [List] of [Filter] that is displayed by our [FilterBar] Composable
 * @param filtersVisible a [Boolean] that is `true` when the [FilterScreen] Composable is visible.
 * @param onFiltersSelected a lambda that is called by the [FilterBar] Composable when the "Filters"
 * [IconButton] at the beginning of its Composable is clicked. Our caller [Feed] passes us a lambda
 * that sets its [MutableState] wrapped [Boolean] variable `filtersVisible` to `true` which animates
 * the visibilty of a [FilterScreen] Composable to visible (this displays a pop-up with more filter
 * options).
 * @param onSnackClick a lambda that should be called with the [Snack.id] and a [String] when a
 * composable displaying a [Snack] is clicked. Our caller [Feed] passes us a lambda that traces back
 * to a call to [com.example.jetsnack.ui.navigation.JetsnackNavController.navigateToSnackDetail]
 * which navigates to the route [MainDestinations.SNACK_DETAIL_ROUTE] with the [Snack.id] as its
 * arugment which composes a [SnackDetail] Composable displaying the [Snack].
 * @param sharedTransitionScope a [SharedTransitionScope] that our caller [Feed] passes us. It is
 * used by [FilterBar] to animate the disappearance and appearance of its `filters` [IconButton]
 * Composable in concert with the appearance and disappearance of the [FilterScreen] Composable.
 * (a very neat effect, but you have to be looking for it to notice).
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
private fun SnackCollectionList(
    snackCollections: List<SnackCollection>,
    filters: List<Filter>,
    filtersVisible: Boolean,
    onFiltersSelected: () -> Unit,
    onSnackClick: (Long, String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Spacer(
                modifier = Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(insets = WindowInsets(top = 56.dp))
                )
            )
            FilterBar(
                filters = filters,
                sharedTransitionScope = sharedTransitionScope,
                filterScreenVisible = filtersVisible,
                onShowFilters = onFiltersSelected
            )
        }
        itemsIndexed(items = snackCollections) { index: Int, snackCollection: SnackCollection ->
            if (index > 0) {
                JetsnackDivider(thickness = 2.dp)
            }

            SnackCollection(
                snackCollection = snackCollection,
                onSnackClick = onSnackClick,
                index = index
            )
        }
    }
}

/**
 * Three previews of the [Feed] screen with different device configurations.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun HomePreview() {
    JetsnackTheme {
        Feed(onSnackClick = { _, _ -> })
    }
}
