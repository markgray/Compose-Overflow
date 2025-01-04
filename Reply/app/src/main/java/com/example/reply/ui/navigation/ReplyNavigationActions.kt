/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.reply.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import com.example.reply.R
import com.example.reply.ui.EmptyComingSoon
import com.example.reply.ui.ReplyApp
import com.example.reply.ui.ReplyInboxScreen
import kotlinx.serialization.Serializable

/**
 * Subclasses of the sealed interface [Route] represent the possible destinations in the app.
 */
sealed interface Route {
    /**
     * This is the [Route] for the [ReplyInboxScreen] Composable, which is the `startDestination`.
`     */
    @Serializable data object Inbox : Route
    /**
     * This is the [Route] for the (as yet unimplemented) "Articles" Composable (displays the
     * [EmptyComingSoon] Composable)
     */
    @Serializable data object Articles : Route
    /**
     * This is the [Route] for the (as yet unimplemented) "DirectMessages" Composable (displays the
     * [EmptyComingSoon] Composable)
     */
    @Serializable data object DirectMessages : Route
    /**
     * This is the [Route] for the (as yet unimplemented) "Groups" Composable (displays the
     * [EmptyComingSoon] Composable)
     */
    @Serializable data object Groups : Route
}

/**
 * This class contains all the information needed to manage the top level destinations in the app.
 */
data class ReplyTopLevelDestination(
    /**
     * This is the [Route] for the destination. Used in calls to [NavHostController.navigate].
     */
    val route: Route,
    /**
     * This [ImageVector] is displayed in an [Icon] that the user can click to navigate to [route]
     */
    val selectedIcon: ImageVector,
    /**
     * This [ImageVector] is the same as [selectedIcon] for all of our top level destinations, and
     * is unused. Apparently the decision was made to use the background color to indicate the
     * selected [Icon] instead of using a different [ImageVector].
     */
    val unselectedIcon: ImageVector,
    /**
     * Resource ID of a [String] that identifies this destination. This is used as the
     * `contentDescription` of the [Icon] that is displayed, and when the buttons for
     * destinations are wide enough to include a [Text] it is used as its `text` argument.
     */
    val iconTextId: Int
)

/**
 * This class holds the [NavHostController] used to navigate between top level destinations in the
 * app, and provides a convenience method to call [NavHostController.navigate] with the backstack
 * properly handled.
 *
 * @param navController the [NavHostController] used to navigate between top level destinations in
 * the app. [ReplyApp] passes us a remembered [NavHostController] that we use to navigate, and it
 * is populated with routes to all four of our [ReplyTopLevelDestination] in [ReplyApp] using
 * [NavGraphBuilder] in its private `ReplyNavHost` Composable.
 */
class ReplyNavigationActions(private val navController: NavHostController) {

    /**
     * This calls the [NavHostController.navigate] method of our [NavHostController] field
     * [navController] to navigate to the [ReplyTopLevelDestination.route] of its
     * [ReplyTopLevelDestination] parameter [destination].
     *
     * We call the [NavHostController.navigate] method of our [NavHostController] field
     * [navController] with its `route` argument the [ReplyTopLevelDestination.route] of our
     * [ReplyTopLevelDestination] parameter [destination]. In the [NavOptionsBuilder] `builder`
     * Composable lambda argument we:
     *  - call the [NavOptionsBuilder.popUpTo] method with the [NavDestination.id] of the
     *  [NavDestination] returned by the [NavGraph.findStartDestination] method of the
     *  [NavHostController.graph] of our [NavHostController] field [navController] and in the
     *  [PopUpToBuilder] `popUpToBuilder` Composable lambda argument we set the
     *  [PopUpToBuilder.saveState] property to `true` (the back stack and the state of all
     *  destinations between the current destination and the [NavOptionsBuilder.popUpTo] ID
     *  should be saved for later restoration via NavOptionsBuilder)
     *  - set the [NavOptionsBuilder.launchSingleTop] property to `true` (Avoid multiple copies of
     *  the same destination when reselecting the same item)
     *  - set the [NavOptionsBuilder.restoreState] property to `true` (Restore state when reselecting
     *  a previously selected item).
     *
     * @param destination the [ReplyTopLevelDestination] to navigate to.
     */
    fun navigateTo(destination: ReplyTopLevelDestination) {
        navController.navigate(route = destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

/**
 * These are the four [ReplyTopLevelDestination]s in the app.
 *  - [Route.Inbox] navigates to the [ReplyInboxScreen] screen
 *  - [Route.Articles] navigates to the [EmptyComingSoon] screen
 *  - [Route.DirectMessages] navigates to the [EmptyComingSoon] screen
 *  - [Route.Groups] navigates to the [EmptyComingSoon] screen
 */
val TOP_LEVEL_DESTINATIONS: List<ReplyTopLevelDestination> = listOf(
    ReplyTopLevelDestination(
        route = Route.Inbox,
        selectedIcon = Icons.Default.Inbox,
        unselectedIcon = Icons.Default.Inbox,
        iconTextId = R.string.tab_inbox
    ),
    ReplyTopLevelDestination(
        route = Route.Articles,
        selectedIcon = Icons.AutoMirrored.Filled.Article,
        unselectedIcon = Icons.AutoMirrored.Filled.Article,
        iconTextId = R.string.tab_article
    ),
    ReplyTopLevelDestination(
        route = Route.DirectMessages,
        selectedIcon = Icons.Outlined.ChatBubbleOutline,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        iconTextId = R.string.tab_inbox
    ),
    ReplyTopLevelDestination(
        route = Route.Groups,
        selectedIcon = Icons.Default.People,
        unselectedIcon = Icons.Default.People,
        iconTextId = R.string.tab_article
    )

)
