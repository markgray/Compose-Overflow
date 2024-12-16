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

package com.example.jetsnack.ui.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.jetsnack.model.Snack
import com.example.jetsnack.ui.JetsnackApp
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.home.addHomeGraph
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.home.JetsnackBottomNavigationItem
import com.example.jetsnack.ui.snackdetail.SnackDetail

/**
 * Destinations used in the [JetsnackApp] Composable.
 */
object MainDestinations {
    /**
     * This route opens the [MainContainer] screen, which is also the `startDestination` of the
     * [NavHost] used by [JetsnackApp].
     */
    const val HOME_ROUTE: String = "home"

    /**
     * This route opens the [SnackDetail] screen to display details about a [Snack] that the user
     * has selected.
     */
    const val SNACK_DETAIL_ROUTE: String = "snack"

    /**
     * The key under which the [Snack.id] of the [Snack] that [SnackDetail] is to display is stored
     * in the [Bundle] passed as arguments when the [NavHost] navigates to [SnackDetail].
     */
    const val SNACK_ID_KEY: String = "snackId"

    /**
     * The key under which a [String] identifying the orgin of a Shared element transition is stored
     * in the [Bundle] passed as arguments when the [NavHost] navigates to [SnackDetail].
     * (I think?) TODO: Figure out how this works for sure
     */
    const val ORIGIN: String = "origin"
}

/**
 * Remembers and creates an instance of [JetsnackNavController]. Called by [MainContainer].
 *
 * @param navController the [NavHostController] that the [NavHost] of [JetsnackNavController]
 * should use.
 */
@Composable
fun rememberJetsnackNavController(
    navController: NavHostController = rememberNavController()
): JetsnackNavController = remember(key1 = navController) {
    JetsnackNavController(navController = navController)
}

/**
 * Responsible for holding UI Navigation logic.
 */
@Stable
class JetsnackNavController(
    /**
     * the [NavHostController] that our [NavHost] should use.
     */
    val navController: NavHostController,
) {

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    /**
     * This is called from [SnackDetail] to navigate back to the [MainContainer]. We just call the
     * [NavHostController.navigateUp] method of our [NavHostController] field [navController] to
     * have it navigate up in the navigation hierarchy.
     */
    fun upPress() {
        navController.navigateUp()
    }

    /**
     * This is called in [JetsnackBottomBar] to navigate to the [HomeSections.route] of one of the
     * [HomeSections] whose [JetsnackBottomNavigationItem] has been clicked. First we check to make
     * sure that our [String] parameter [route] is not the same as the current [NavDestination.route]
     * doing nothing if they are the same. If they are not the same we call [NavHostController.navigate]
     * method of our [NavHostController] field [navController] to navigate to the [route] parameter.
     * In its `builder` [NavOptionsBuilder] lambda argument we set the [NavOptionsBuilder.launchSingleTop]
     * property to `true` and its [NavOptionsBuilder.restoreState] property to `true`, then call the
     * [NavOptionsBuilder.popUpTo] method of the [NavOptionsBuilder] receiver to pop up the backstack
     * to the first destination of the graph and save state (this makes us go back to the start
     * destination when the user presses back in any other bottom tab instead of to this one).
     *
     * @param route the [String] route of the [HomeSections] whose [JetsnackBottomNavigationItem]
     * has been clicked.
     */
    fun navigateToBottomBarRoute(route: String) {
        if (route != navController.currentDestination?.route) {
            navController.navigate(route = route) {
                launchSingleTop = true
                restoreState = true
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(id = findStartDestination(graph = navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    /**
     * This is called when a [Snack] is clicked to navigate to the [SnackDetail] screen to display
     * that [Snack]. First we call our [NavBackStackEntry.lifecycleIsResumed] extension property to
     * make sure that the [NavBackStackEntry.lifecycle] of our [NavBackStackEntry] parameter [from]
     * is [Lifecycle.State.RESUMED] doing nothing if it is not (if the lifecycle is not resumed it
     * means this NavBackStackEntry already processed a nav event, so we ignore the call to avoid
     * duplicated navigation events). If it is [Lifecycle.State.RESUMED] we call the
     * [NavHostController.navigate] method of our [NavHostController] field [navController] with its
     * `route` argument the [String] formed by concatenating [MainDestinations.SNACK_DETAIL_ROUTE]
     * with a "/" followed by the [String] value of our [Long] parameter [snackId] followed by the
     * [String] "?orgin=" followed by our [String] parameter [origin]
     *
     * @param snackId the [Snack.id] of the [Snack] that was clicked.
     * @param origin a [String] identifying the Shared element transition that should be used.
     * @param from the [NavBackStackEntry] that the `composable` extension function of
     * [NavGraphBuilder] passes to its `content` lambda argument. (See our [addHomeGraph] method).
     */
    fun navigateToSnackDetail(snackId: Long, origin: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(route = "${MainDestinations.SNACK_DETAIL_ROUTE}/$snackId?origin=$origin")
        }
    }
}

/**
 * Convenience extension property to check that the `currentState` of the [NavBackStackEntry.lifecycle]
 * of our receiver is [Lifecycle.State.RESUMED]. If the lifecycle is not resumed it means this
 * [NavBackStackEntry] already processed a nav event. This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

/**
 * Convenience extension property to find the starting [NavDestination] for this [NavGraph]. To do
 * this it just returns the value returned by [NavGraph.findNode] for the `resId` argument
 * [NavGraph.startDestinationId] (starting destination id for this NavGraph).
 */
private val NavGraph.startDestination: NavDestination?
    get() = findNode(resId = startDestinationId)

/**
 * Copied from similar function in `NavigationUI.kt`. It is used by the method
 * [JetsnackNavController.navigateToBottomBarRoute] to find the [NavDestination] that
 * is the start destination of its [NavDestination] parameter [graph] in order to use
 * its [NavDestination.id] in a call to [NavOptionsBuilder.popUpTo]. We mark this function
 * with `tailrec` to mark as tail-recursive, allowing the compiler to replace recursion with
 * iteration. Then if our [NavDestination] parameter [graph] is a [NavGraph] we return the
 * result of calling ourselves with the [NavGraph.startDestination] of [graph], otherwise we
 * are done iterating so we return [graph].
 *
 * @param graph the [NavDestination] of the [NavGraph] whose start [NavDestination] we are to find
 * (or a [NavDestination] in the [NavGraph]).
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
