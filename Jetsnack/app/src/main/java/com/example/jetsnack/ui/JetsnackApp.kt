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

@file:OptIn(
    ExperimentalSharedTransitionApi::class
)
@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.example.jetsnack.ui

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackScaffoldState
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.components.rememberJetsnackScaffoldState
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.home.addHomeGraph
import com.example.jetsnack.ui.home.composableWithCompositionLocal
import com.example.jetsnack.ui.navigation.JetsnackNavController
import com.example.jetsnack.ui.navigation.MainDestinations
import com.example.jetsnack.ui.navigation.rememberJetsnackNavController
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * This is the root view of our app. It starts by wrapping all of its content in [JetsnackTheme] to
 * have it supply all of the [MaterialTheme.colorScheme] and [MaterialTheme.typography] values to
 * the Composable hierarchy. We initialize and remember our [JetsnackNavController] variable
 * `val jetsnackNavController` to the instance returned by [rememberJetsnackNavController]. Then our
 * root composable is a [SharedTransitionLayout] in whose [SharedTransitionScope] `content` Composable
 * lambda argument we compose a [CompositionLocalProvider] to bind [LocalSharedTransitionScope] key
 * to `this` [SharedTransitionScope]. Inside the `content` Composable lambda argument we of the
 * [CompositionLocalProvider] we compose a [NavHost] whose `navController` argument is the
 * [JetsnackNavController.navController] of our `jetsnackNavController` variable, and whose
 * `startDestination` argument is [MainDestinations.HOME_ROUTE]. Inside [NavGraphBuilder] `builder`
 * Composable lambda argument of the [NavHost] we call our [NavGraphBuilder.composableWithCompositionLocal]
 * method with its `route` argument [MainDestinations.HOME_ROUTE] to add a [MainContainer] Composable
 * whose `onSnackSelected` argument is the [JetsnackNavController.navigateToSnackDetail] method of
 * our [JetsnackNavController] variable `jetsnackNavController` to the [NavGraphBuilder].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun JetsnackApp() {
    JetsnackTheme {
        val jetsnackNavController: JetsnackNavController = rememberJetsnackNavController()
        SharedTransitionLayout {
            CompositionLocalProvider(
                value = LocalSharedTransitionScope provides this
            ) {
                NavHost(
                    navController = jetsnackNavController.navController,
                    startDestination = MainDestinations.HOME_ROUTE
                ) {
                    composableWithCompositionLocal(
                        route = MainDestinations.HOME_ROUTE
                    ) { backStackEntry: NavBackStackEntry ->
                        MainContainer(
                            onSnackSelected = jetsnackNavController::navigateToSnackDetail
                        )
                    }

                    composableWithCompositionLocal(
                        route = "${MainDestinations.SNACK_DETAIL_ROUTE}/" +
                            "{${MainDestinations.SNACK_ID_KEY}}" +
                            "?origin={${MainDestinations.ORIGIN}}",
                        arguments = listOf(
                            navArgument(name = MainDestinations.SNACK_ID_KEY) {
                                type = NavType.LongType
                            }
                        ),

                        ) { backStackEntry: NavBackStackEntry ->
                        val arguments: Bundle = requireNotNull(backStackEntry.arguments)
                        val snackId: Long = arguments.getLong(MainDestinations.SNACK_ID_KEY)
                        val origin: String? = arguments.getString(MainDestinations.ORIGIN)
                        SnackDetail(
                            snackId = snackId,
                            origin = origin ?: "",
                            upPress = jetsnackNavController::upPress
                        )
                    }
                }
            }
        }
    }
}

/**
 *
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainContainer(
    modifier: Modifier = Modifier,
    onSnackSelected: (Long, String, NavBackStackEntry) -> Unit
) {
    val jetsnackScaffoldState: JetsnackScaffoldState = rememberJetsnackScaffoldState()
    val nestedNavController: JetsnackNavController = rememberJetsnackNavController()
    val navBackStackEntry: NavBackStackEntry? by nestedNavController
        .navController
        .currentBackStackEntryAsState()
    val currentRoute: String? = navBackStackEntry?.destination?.route
    val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    JetsnackScaffold(
        bottomBar = {
            with(animatedVisibilityScope) {
                with(sharedTransitionScope) {
                    JetsnackBottomBar(
                        tabs = HomeSections.entries.toTypedArray(),
                        currentRoute = currentRoute ?: HomeSections.FEED.route,
                        navigateToRoute = nestedNavController::navigateToBottomBarRoute,
                        modifier = Modifier
                            .renderInSharedTransitionScopeOverlay(
                                zIndexInOverlay = 1f,
                            )
                            .animateEnterExit(
                                enter = fadeIn(animationSpec = nonSpatialExpressiveSpring()) +
                                    slideInVertically(
                                        animationSpec = spatialExpressiveSpring()
                                    ) {
                                        it
                                    },
                                exit = fadeOut(animationSpec = nonSpatialExpressiveSpring()) +
                                    slideOutVertically(
                                        animationSpec = spatialExpressiveSpring()
                                    ) {
                                        it
                                    }
                            )
                    )
                }
            }
        },
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = it,
                modifier = Modifier.systemBarsPadding(),
                snackbar = { snackbarData: SnackbarData -> JetsnackSnackbar(snackbarData = snackbarData) }
            )
        },
        snackBarHostState = jetsnackScaffoldState.snackBarHostState,
    ) { padding: PaddingValues ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = HomeSections.FEED.route
        ) {
            addHomeGraph(
                onSnackSelected = onSnackSelected,
                modifier = Modifier
                    .padding(paddingValues = padding)
                    .consumeWindowInsets(paddingValues = padding)
            )
        }
    }
}

/**
 *
 */
val LocalNavAnimatedVisibilityScope: ProvidableCompositionLocal<AnimatedVisibilityScope?> =
    compositionLocalOf { null }

/**
 *
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope?> =
    compositionLocalOf { null }
