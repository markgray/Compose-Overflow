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

package com.example.reply.ui

import android.graphics.Rect
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.example.reply.data.Email
import com.example.reply.ui.navigation.ReplyNavSuiteScope
import com.example.reply.ui.navigation.ReplyNavigationActions
import com.example.reply.ui.navigation.ReplyNavigationWrapper
import com.example.reply.ui.navigation.Route
import com.example.reply.ui.utils.DevicePosture
import com.example.reply.ui.utils.ReplyContentType
import com.example.reply.ui.utils.ReplyNavigationType
import com.example.reply.ui.utils.isBookPosture
import com.example.reply.ui.utils.isSeparating
import kotlinx.coroutines.flow.StateFlow

/**
 * This extension function is a convenience function that maps the [NavigationSuiteType] enum to the
 * [ReplyNavigationType] enum
 */
private fun NavigationSuiteType.toReplyNavType() = when (this) {
    NavigationSuiteType.NavigationBar -> ReplyNavigationType.BOTTOM_NAVIGATION
    NavigationSuiteType.NavigationRail -> ReplyNavigationType.NAVIGATION_RAIL
    NavigationSuiteType.NavigationDrawer -> ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER
    else -> ReplyNavigationType.BOTTOM_NAVIGATION
}

/**
 * This is the root composable of our app. First we initialize our [FoldingFeature] variable
 * `val foldingFeature` to the first [FoldingFeature] in our [List] of [DisplayFeature] parameter
 * [displayFeatures] that is an instance of [FoldingFeature] (or to `null` if there are none). Then
 * we initialize our [DevicePosture] variable `val foldingDevicePosture` using a when expression:
 *  - If our [isBookPosture] method returns `true` for [FoldingFeature] variable `foldingFeature` we
 *  set its value to a [DevicePosture.BookPosture] with its `hingePosition` bounding [Rect] the
 *  [FoldingFeature.bounds] property of [FoldingFeature] variable `foldingFeature`.
 *  - If our [isSeparating] method returns `true` for [FoldingFeature] variable `foldingFeature` we
 *  set its value to a [DevicePosture.Separating] with its `hingePosition` bounding [Rect] the
 *  [FoldingFeature.bounds] property of [FoldingFeature] variable `foldingFeature` and its `orientation`
 *  the [FoldingFeature.orientation] property of [FoldingFeature] variable `foldingFeature`.
 *  - If neither method returns `true` we set it to [DevicePosture.NormalPosture].
 *
 * We initialize our [ReplyContentType] variable `val contentType` using a when expression based on
 * the value of the [WindowSizeClass.widthSizeClass] of [WindowSizeClass] parameter [windowSize]:
 *  - [WindowWidthSizeClass.Compact] -> we set it to [ReplyContentType.SINGLE_PANE].
 *  - [WindowWidthSizeClass.Medium] -> we set it to [ReplyContentType.DUAL_PANE] if our [DevicePosture]
 *  variable `foldingDevicePosture` is not equal to [DevicePosture.NormalPosture] or we set it to
 *  [ReplyContentType.SINGLE_PANE] if it is.
 *  - [WindowWidthSizeClass.Expanded] -> we set it to [ReplyContentType.DUAL_PANE].
 *  - Otherwise we set it to [ReplyContentType.SINGLE_PANE].
 *
 * We initialize and remember our [NavHostController] variable `val navController` to the instance
 * returned by [rememberNavController]. We initialize and remember our [ReplyNavigationActions]
 * variable `val navigationActions` to a new instance whose `navController` argument is `navController`.
 * (which is also the `key1` argument of the call to [remember]). We initialize our [State] wrapped
 * [NavBackStackEntry] variable `val navBackStackEntry` to the value returned by the
 * [NavHostController.currentBackStackEntryAsState] method of `navController`. We initialize our
 * [NavDestination] variable `val currentDestination` to the value of the
 * [NavBackStackEntry.destination] property of `navBackStackEntry`.
 *
 * Then our root Composable is a [Surface] whose `content` Composable lambda argument composes a
 * [ReplyNavigationWrapper] whose `currentDestination` argument is [NavDestination] variable
 * `currentDestination` and whose `navigateToTopLevelDestination` argument is a reference to the
 * [ReplyNavigationActions.navigateTo] method of `navigationActions`. The [ReplyNavSuiteScope]
 * `content` Composable lambda argument of the [ReplyNavigationWrapper] is a lambda in which we
 * compose a [ReplyNavHost] whose arguments are:
 *  - `navController` -> our [NavHostController] variable `navController`
 *  - `contentType` -> our [ReplyContentType] variable `contentType`
 *  - `displayFeatures` -> our [List] of [DisplayFeature] parameter [displayFeatures]
 *  - `replyHomeUIState` -> our [State] wrapped [ReplyHomeUIState] parameter [replyHomeUIState]
 *  - `navigationType` -> the [ReplyNavigationType] that corresponds to the [NavigationSuiteType]
 *  that is being used for the [NavigationSuiteScaffoldLayout] in our app.
 *  - `closeDetailScreen` -> our lambda parameter [closeDetailScreen]
 *  - `navigateToDetail` -> our lambda parameter [navigateToDetail]
 *  - `toggleSelectedEmail` -> our lambda parameter [toggleSelectedEmail].
 *
 * @param windowSize The [WindowSizeClass] of the device's window.
 * @param displayFeatures The [List] of [DisplayFeature] of the device's display.
 * @param replyHomeUIState The current state of the Reply app. This is the [State] wrapped
 * [ReplyHomeUIState] returned by the [StateFlow.collectAsStateWithLifecycle] method of the
 * [StateFlow] of [ReplyHomeUIState] field [ReplyHomeViewModel.uiState] of the [ReplyHomeViewModel].
 * @param closeDetailScreen The lambda to be called when the detail screen is to be closed. We are
 * passed a lambda that calls the [ReplyHomeViewModel.closeDetailScreen] method of the
 * [ReplyHomeViewModel].
 * @param navigateToDetail A lambda to be called with the [Email.id] of the [Email] to have its details
 * displayed and the [ReplyContentType] appropriate for the device we are running on.
 * @param toggleSelectedEmail A lambda to be called with the [Email.id] of the [Email] whose selected
 * status the user wants to toggle. We are passed a lambda that calls the
 * [ReplyHomeViewModel.toggleSelectedEmail] method of the [ReplyHomeViewModel]
 */
@Composable
fun ReplyApp(
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit = {},
    navigateToDetail: (Long, ReplyContentType) -> Unit = { _, _ -> },
    toggleSelectedEmail: (Long) -> Unit = { }
) {
    /**
     * We are using display's folding features to map the device postures a fold is in.
     * In the state of folding device If it's half fold in BookPosture we want to avoid content
     * at the crease/hinge
     */
    val foldingFeature: FoldingFeature? =
        displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture: DevicePosture = when {
        isBookPosture(foldFeature = foldingFeature) ->
            DevicePosture.BookPosture(hingePosition = foldingFeature.bounds)

        isSeparating(foldFeature = foldingFeature) ->
            DevicePosture.Separating(
                hingePosition = foldingFeature.bounds,
                orientation = foldingFeature.orientation
            )

        else -> DevicePosture.NormalPosture
    }

    val contentType: ReplyContentType = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> ReplyContentType.SINGLE_PANE
        WindowWidthSizeClass.Medium -> if (foldingDevicePosture != DevicePosture.NormalPosture) {
            ReplyContentType.DUAL_PANE
        } else {
            ReplyContentType.SINGLE_PANE
        }
        WindowWidthSizeClass.Expanded -> ReplyContentType.DUAL_PANE
        else -> ReplyContentType.SINGLE_PANE
    }

    val navController: NavHostController = rememberNavController()
    val navigationActions: ReplyNavigationActions = remember(key1 = navController) {
        ReplyNavigationActions(navController = navController)
    }
    val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    Surface {
        ReplyNavigationWrapper(
            currentDestination = currentDestination,
            navigateToTopLevelDestination = navigationActions::navigateTo
        ) {
            ReplyNavHost(
                navController = navController,
                contentType = contentType,
                displayFeatures = displayFeatures,
                replyHomeUIState = replyHomeUIState,
                navigationType = navSuiteType.toReplyNavType(),
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail,
            )
        }
    }
}

/**
 * This is the top level [NavHost] of the app. Our root Composable is a [NavHost] whose `modifier`
 * argument is our [Modifier] parameter [modifier] and whose `navController` argument is our
 * [NavHostController] parameter [navController], and the `startDestination` argument is [Route.Inbox]
 * (which is the route for the [ReplyInboxScreen] Composable). In the [NavGraphBuilder] `builder`
 * Composable lambda argument we have:
 *  - [NavGraphBuilder.composable] for the [Route.Inbox] route in whose [AnimatedContentScope] `content`
 *  Composable lambda argument we compose a [ReplyInboxScreen] whose [ReplyContentType] `contentType`
 *  argument is our [ReplyContentType] parameter [contentType], whose [ReplyHomeUIState] argument is
 *  our [State] wrapped [ReplyHomeUIState] parameter [replyHomeUIState], whose [ReplyNavigationType]
 *  argument `navigationType` is our [ReplyNavigationType] parameter [navigationType], whose [List]
 *  of [DisplayFeature] argument `displayFeatures` is our [List] of [DisplayFeature] parameter
 *  [displayFeatures], whose `closeDetailScreen` lambda argument is our lambda parameter
 *  [closeDetailScreen], whose `navigateToDetail` lambda argument is our lambda parameter
 *  [navigateToDetail], and whose `toggleSelectedEmail` lambda argument is our lambda parameter
 *  [toggleSelectedEmail].
 *  - [NavGraphBuilder.composable] for the [Route.DirectMessages] route in whose [AnimatedContentScope]
 *  `content` Composable lambda argument we compose an [EmptyComingSoon].
 *  - [NavGraphBuilder.composable] for the [Route.Articles] route in whose [AnimatedContentScope]
 *  `content` Composable lambda argument we compose an [EmptyComingSoon].
 *  - [NavGraphBuilder.composable] for the [Route.Groups] route in whose [AnimatedContentScope]
 *  `content` Composable lambda argument we compose an [EmptyComingSoon].
 *
 * @param navController The [NavHostController] of the app.
 * @param contentType The [ReplyContentType] to use for the device we are running on, either
 * [ReplyContentType.SINGLE_PANE] or [ReplyContentType.DUAL_PANE].
 * @param displayFeatures The [List] of [DisplayFeature] of the device's display.
 * @param replyHomeUIState The current [State] wrapped [ReplyHomeUIState] state of the Reply app
 * returned by the [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of
 * [ReplyHomeUIState] field [ReplyHomeViewModel.uiState] of the [ReplyHomeViewModel].
 * @param navigationType The [ReplyNavigationType] that corresponds to the [NavigationSuiteType]
 * that is being used for the [NavigationSuiteScaffoldLayout] in our app.
 * @param closeDetailScreen The lambda to be called when the detail screen is to be closed.
 * @param navigateToDetail A lambda to be called with the [Email.id] of the [Email] to have its details
 * displayed and the [ReplyContentType] appropriate for the device we are running on.
 * @param toggleSelectedEmail A lambda to be called with the [Email.id] of the [Email] whose selected
 * status the user wants to toggle.
 * @param modifier A [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ReplyApp] does not pass us one so the empty, default, or starter [Modifier]
 * that contains no elements if used.
 */
@Composable
private fun ReplyNavHost(
    navController: NavHostController,
    contentType: ReplyContentType,
    displayFeatures: List<DisplayFeature>,
    replyHomeUIState: ReplyHomeUIState,
    navigationType: ReplyNavigationType,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
    toggleSelectedEmail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Inbox,
    ) {
        composable<Route.Inbox> {
            ReplyInboxScreen(
                contentType = contentType,
                replyHomeUIState = replyHomeUIState,
                navigationType = navigationType,
                displayFeatures = displayFeatures,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail
            )
        }
        composable<Route.DirectMessages> {
            EmptyComingSoon()
        }
        composable<Route.Articles> {
            EmptyComingSoon()
        }
        composable<Route.Groups> {
            EmptyComingSoon()
        }
    }
}
