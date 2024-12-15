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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.jetsnack.model.Snack
import com.example.jetsnack.ui.JetsnackApp
import com.example.jetsnack.ui.MainContainer
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
     *
     */
    val navController: NavHostController,
) {

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    /**
     *
     */
    fun upPress() {
        navController.navigateUp()
    }

    /**
     *
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
     *
     */
    fun navigateToSnackDetail(snackId: Long, origin: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.SNACK_DETAIL_ROUTE}/$snackId?origin=$origin")
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
