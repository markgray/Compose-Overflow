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
import androidx.compose.animation.AnimatedContentScope
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.jetsnack.model.Snack
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
 * to `this` [SharedTransitionScope]. Inside the `content` Composable lambda argument of the
 * [CompositionLocalProvider] we compose a [NavHost] whose `navController` argument is the
 * [JetsnackNavController.navController] of our `jetsnackNavController` variable, and whose
 * `startDestination` argument is [MainDestinations.HOME_ROUTE].
 *
 * Inside the [NavGraphBuilder] `builder` Composable lambda argument of the [NavHost] we first call
 * our [NavGraphBuilder.composableWithCompositionLocal] method with its `route` argument
 * [MainDestinations.HOME_ROUTE] to add a [MainContainer] Composable whose `onSnackSelected`
 * argument is the [JetsnackNavController.navigateToSnackDetail] method of our [JetsnackNavController]
 * variable `jetsnackNavController` to the [NavGraphBuilder].
 *
 * Next we call our [NavGraphBuilder.composableWithCompositionLocal] method with its `route` argument
 * an url created by combining the [String] destination [MainDestinations.SNACK_DETAIL_ROUTE] to the
 * character "/" followed by [String] key [MainDestinations.SNACK_ID_KEY], followed by the [String]
 * "?origin=" followed by the [String] key [MainDestinations.ORIGIN]. Its `arguments` argument is
 * a [List] of [navArgument] whose one entry is a [navArgument] whose `name` is
 * [MainDestinations.SNACK_ID_KEY] and in whose [NavArgumentBuilder] `builder` lambda argument
 * we set the [NavArgumentBuilder.type] to [NavType.LongType]. In the [AnimatedContentScope]
 * `content` Composable lambda argument of the [NavGraphBuilder.composableWithCompositionLocal]
 * we accept the [NavBackStackEntry] passed the lambda in our `backStackEntry` variable, then we
 * initialize our [Bundle] variable `val arguments` to the [NavBackStackEntry.arguments] of our
 * variable `backStackEntry` thowing [IllegalArgumentException] if that is `null`. We initialize our
 * [Long] variable `val snackId` to the [Long] stored in [Bundle] `arguments` under the key
 * [MainDestinations.SNACK_ID_KEY], and initialize our [String] variable `val origin` to the [String]
 * stored in `arguments` under the key [MainDestinations.ORIGIN]. Finally we compose a [SnackDetail]
 * whose `snackId` argument is our `snackId` variable, whose `origin` argument is our `origin`
 * variable (or the empty [String] if that is `null`), and whose `upPress` argument is the
 * [JetsnackNavController.upPress] method of our `jetsnackNavController` variable.
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
                        )
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
 * This is the screen that is used for the destination [MainDestinations.HOME_ROUTE], and it is the
 * `startDestination` of the [NavHost] of the app. We start by initializing and remembering our
 * [JetsnackScaffoldState] variable `val jetsnackScaffoldState` to the instance returned by the
 * [rememberJetsnackScaffoldState] method. We initialize and remember our [JetsnackNavController]
 * variable `val nestedNavController` to the instance returned by the [rememberJetsnackNavController]
 * method. We initialize our [NavBackStackEntry] variable `val navBackStackEntry` to the
 * [MutableState] wrapped [NavBackStackEntry] returned by the [NavController.currentBackStackEntryAsState]
 * method of the [NavHostController] returned by the [JetsnackNavController.navController] property
 * of our [JetsnackNavController] variable `nestedNavController`. We initialize our [String] variable
 * `val currentRoute` to the [NavDestination.route] of the [NavBackStackEntry.destination] of our
 * [NavBackStackEntry] variable `navBackStackEntry`. We initialize our [SharedTransitionScope]
 * variable `val sharedTransitionScope` to the `current` [LocalSharedTransitionScope] or throw
 * [IllegalStateException] if it is `null`. We initialize our [AnimatedVisibilityScope] variable
 * `val animatedVisibilityScope` to the `current` [LocalNavAnimatedVisibilityScope] or throw
 * [IllegalStateException] if it is `null`.
 *
 * We compose a [JetsnackScaffold] whose `bottomBar` argument is a Composable lambda that uses the
 * `with` extension function to wrap its contents in our [AnimatedVisibilityScope] receiver variable
 * `animatedVisibilityScope` and another `with` to wrap its contents in our [SharedTransitionScope]
 * receiver variable `sharedTransitionScope`. Then it composes a [JetsnackBottomBar] whose arguments
 * are:
 *  - `tabs` is the [Array] of [HomeSections] of all the [HomeSections.entries].
 *  - `curentRoute` is our [String] variable `currentRoute` or if that is `null` it is the
 *  [HomeSections.route] of [HomeSections.FEED].
 *  - `navigateToRoute` is the [JetsnackNavController.navigateToBottomBarRoute] method of our
 *  [JetsnackNavController] variable `nestedNavController`.
 *  - `modifier` is a [SharedTransitionScope.renderInSharedTransitionScopeOverlay] whose `zIndexInOverlay`
 *  argument is `1f` causing it to render above other composables in the shared transition. To this
 *  is chained a [AnimatedVisibilityScope.animateEnterExit] whose `enter` argument is a [fadeIn] whose
 *  `animationSpec` is an [nonSpatialExpressiveSpring] plus a [slideInVertically] whose `animationSpec`
 *  is a [spatialExpressiveSpring], its `exit` argument is a [fadeOut] whose `animationSpec` is an
 *  [nonSpatialExpressiveSpring] plus a [slideOutVertically] whose `animationSpec` is a
 *  [spatialExpressiveSpring].
 *
 * The `modifier` argument of the [JetsnackScaffold] is our [Modifier] parameter [modifier], its
 * `snackBarHost` argument is a lambda composing a [SnackbarHost] whose `hostState` argument is the
 * [SnackbarHostState] that is passed to the lambda (as `it`) by [JetsnackScaffold] when it composes
 * its [Scaffold], its `modifier` argument is a [Modifier.systemBarsPadding] to add padding to
 * accommodate the system bars insets, and its `snackbar` argument is a lambda which accepts the
 * [SnackbarData] passed it in the variable `snackbarData` and composes a [JetsnackSnackbar] whose
 * `snackbarData` argument is that [SnackbarData] variable `snackbarData`. The `snackbarHostState`
 * argument is the [JetsnackScaffoldState.snackBarHostState] of our [JetsnackScaffoldState] variable
 * `snackBarHostState` (this is passed to the `snackBarHost` lambda by [JetsnackScaffold] when it
 * composes its [Scaffold]).
 *
 * In the `content` Composable lambda argument of the [JetsnackScaffold] we accept the [PaddingValues]
 * passed the lambda in our [PaddingValues] variable `padding`, and then we we compose a [NavHost]
 * whose `navController` argument is the [JetsnackNavController.navController] of our
 * [JetsnackNavController] variable `nestedNavController`, and whose `startDestination` argument is
 * the [HomeSections.route] of [HomeSections.FEED]. In the [NavGraphBuilder] `builder` Composable
 * lambda argument we call our [NavGraphBuilder.addHomeGraph] extension method with its `onSnackSelected`
 * argument our [onSnackSelected] lambda parameter (this will use the [composable] method to add routes
 * for all the [HomeSections]). The `modifier` argument of [NavGraphBuilder.addHomeGraph] is a
 * [Modifier.padding] whose `paddingValues` argument is our [PaddingValues] variable `padding`, to
 * which is chained a [Modifier.consumeWindowInsets] whose `paddingValues` argument is that same
 * [PaddingValues] variable `padding`.
 *
 * @param modifier  a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param onSnackSelected a lambda that can be called when the user wants to view the [SnackDetail]
 * of one of the [Snack]s. It should be passed the [Long] value of the [Snack.id] of the [Snack],
 * the [String] `orgin` identifying the shared transition to be used, and the [NavBackStackEntry]
 * that the [composable] method call that created the route we are coming from passed to its
 * [AnimatedContentScope] `content` lambda argument (this is added by the [composable]'s `content`
 * lambda argument, so the `onSnackClick` lambda passed the screen we are coming from had only a
 * [Long] and a [String] parameter to worry about).
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
 * This is defined to be `null` to make sure that when it is used the Composable using it is wrapped
 * in a [CompositionLocalProvider] that `provides` a `value` for [AnimatedVisibilityScope].
 */
val LocalNavAnimatedVisibilityScope: ProvidableCompositionLocal<AnimatedVisibilityScope?> =
    compositionLocalOf { null }

/**
 * This is defined to be `null` to make sure that when it is used the Composable using it is wrapped
 * in a [CompositionLocalProvider] that `provides` a `value` for [SharedTransitionScope].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope?> =
    compositionLocalOf { null }
