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
import androidx.compose.foundation.lazy.itemsIndexed
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
 *  `visible` argument is our [MutableState] wrapped [Boolean] variable `filtersVisible`
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
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
            FilterBar(
                filters = filters,
                sharedTransitionScope = sharedTransitionScope,
                filterScreenVisible = filtersVisible,
                onShowFilters = onFiltersSelected
            )
        }
        itemsIndexed(snackCollections) { index: Int, snackCollection: SnackCollection ->
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
