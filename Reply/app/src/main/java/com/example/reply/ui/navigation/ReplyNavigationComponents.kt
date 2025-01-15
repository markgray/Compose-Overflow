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

@file:Suppress("UNUSED_PARAMETER")

package com.example.reply.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.reply.R
import com.example.reply.ui.ReplyApp
import com.example.reply.ui.utils.ReplyNavigationContentPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Convenience method to check if the current window size is compact. Returns `true` if the
 * [WindowWidthSizeClass] is [WindowWidthSizeClass.COMPACT] or [WindowHeightSizeClass] is
 * [WindowHeightSizeClass.COMPACT]
 */
private fun WindowSizeClass.isCompact() =
    windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
        windowHeightSizeClass == WindowHeightSizeClass.COMPACT

/**
 * This class is used to wrap Composables allowing them to access the [NavigationSuiteType] that is
 * being used for the [NavigationSuiteScaffoldLayout] they are being used in.
 */
class ReplyNavSuiteScope(
    /**
     * The [NavigationSuiteType] that is being used for the [NavigationSuiteScaffoldLayout], one of
     * [NavigationSuiteType.NavigationBar] (instructs the [NavigationSuite] to expect a [NavigationBar]
     * that will be displayed at the bottom of the screen), [NavigationSuiteType.NavigationRail]
     * (instructs the [NavigationSuite] to expect a [NavigationRail] that will be displayed at the
     * start of the screen), or [NavigationSuiteType.NavigationDrawer] (instructs the [NavigationSuite]
     * to expect a [PermanentDrawerSheet] that will be displayed at the start of the screen).
     */
    val navSuiteType: NavigationSuiteType
)

/**
 * This is used to wrap the `ReplyNavHost` used by our app's [ReplyApp] top level Composable in a
 * [NavigationSuiteScaffoldLayout] that has been properly configured for the current device's
 * [WindowSizeClass]. We start by initializing our [WindowAdaptiveInfo] variable `val adaptiveInfo`
 * to the current [WindowAdaptiveInfo] returned by the [currentWindowAdaptiveInfo] method. Then we
 * initialize our [DpSize] variable `val windowSize` to the current window size returned by the
 * [currentWindowSize] method for the current [LocalDensity] (by using the [IntSize.toSize] extension
 * method followed by the [Density.toDpSize] extension method).
 *
 * Next we initialize our [NavigationSuiteType] variable `val navLayoutType` to a
 * [NavigationSuiteType] using a when expression:
 *  - when the [WindowAdaptiveInfo.windowPosture] of `adaptiveInfo` is [Posture.isTabletop] we set
 *  it to [NavigationSuiteType.NavigationBar]
 *  - when the [WindowAdaptiveInfo.windowSizeClass] of `adaptiveInfo` is [WindowSizeClass.isCompact]
 *  we set it to [NavigationSuiteType.NavigationBar]
 *  - when the [WindowSizeClass.windowWidthSizeClass] of the [WindowAdaptiveInfo.windowSizeClass] of
 *  `adaptiveInfo` is equal to [WindowWidthSizeClass.EXPANDED] and the [DpSize.width] of `windowSize`
 *  is greater than or equal to `1200.dp` we set it to [NavigationSuiteType.NavigationDrawer].
 *  - otherwise we set it to [NavigationSuiteType.NavigationRail].
 *
 * Next we initialize our [ReplyNavigationContentPosition] variable `val navContentPosition` using
 * a when expression based on the value of the [WindowSizeClass.windowHeightSizeClass] of the
 * [WindowAdaptiveInfo.windowSizeClass] of `adaptiveInfo`:
 *  - [WindowHeightSizeClass.COMPACT] -> [ReplyNavigationContentPosition.TOP]
 *  - [WindowHeightSizeClass.MEDIUM] or [WindowHeightSizeClass.EXPANDED] ->
 *  [ReplyNavigationContentPosition.CENTER]
 *  - otherwise -> [ReplyNavigationContentPosition.TOP]
 *
 * We initialize and remember our [DrawerState] variable `val drawerState` to a new instance with an
 * `initialValue` of [DrawerValue.Closed]. We initialize and remember our [CoroutineScope] variable
 * `val coroutineScope` to a new instance using the [rememberCoroutineScope] method. We initialize
 * our [Boolean] variable `val gesturesEnabled` to `true` if the [DrawerState.isOpen] method of
 * `drawerState` is `true` or if [NavigationSuiteType] variable `navLayoutType` is
 * [NavigationSuiteType.NavigationRail].
 *
 * We compose a [BackHandler] whose `enabled` property is `true` if the [DrawerState.isOpen] method
 * of [DrawerState] variable `drawerState` is `true`. Inside the `onBack` lambda argument of the
 * [BackHandler] we use [CoroutineScope] variable `coroutine` to launch a [CoroutineScope] lambda
 * `block` wherein we call the [DrawerState.close] method of the [DrawerState] variable `drawerState`.
 *
 * Our root composable is a [ModalNavigationDrawer] whose `drawerState` property is our [DrawerState]
 * variable `drawerState`, whose `gesturesEnabled` property is our [Boolean] `gesturesEnabled`
 * variable, and whose `drawerContent` argument is a lambda in which we compose a
 * [ModalNavigationDrawerContent] whose `currentDestination` argument is our [NavDestination]
 * parameter [currentDestination], whose `navigationContentPosition` argument is our
 * [ReplyNavigationContentPosition] variable `navContentPosition`, whose `navigateToTopLevelDestination`
 * lambda taking a [ReplyTopLevelDestination] argument is our [navigateToTopLevelDestination] lambda
 * parameter, and whose `onDrawerClicked` lambda argument is a lambda in which we use the
 * [CoroutineScope.launch] method of our [CoroutineScope] variable `coroutineScope` to launch a
 * coroutine which calls the [DrawerState.close] method of our [DrawerState] variable `drawerState`.
 *
 * In the `content` Composabe lambda argument of the [ModalNavigationDrawer] we compose a
 * [NavigationSuiteScaffoldLayout] whose `layoutType` argument is our [NavigationSuiteType] variable
 * `navLayoutType`, whose `navigationSuite` argument is a lambda in which we use a `when` statement
 * to chose depending on the value of our [NavigationSuiteType] variable `navLayoutType` between:
 *  - [NavigationSuiteType.NavigationBar] we compose a [ReplyBottomNavigationBar] whose
 *  `currentDestination` arguement is our [NavDestination] parameter [currentDestination] and whose
 *  `navigateToTopLevelDestination` argument is our [navigateToTopLevelDestination] lambda parameter.
 *  - [NavigationSuiteType.NavigationRail] we compose a [ReplyNavigationRail] whose `currentDestination`
 *  argument is our [NavDestination] parameter [currentDestination], whose `navigationContentPosition`
 *  argument is our [ReplyNavigationContentPosition] variable `navContentPosition`, whose
 *  `navigateToTopLevelDestination` argument is our [navigateToTopLevelDestination] lambda parameter,
 *  and whose `onDrawerClicked` lambda argument is a lambda in which we use the [CoroutineScope.launch]
 *  method of our [CoroutineScope] variable `coroutineScope` to launch a coroutine which calls the
 *  [DrawerState.open] method of our [DrawerState] variable `drawerState`.
 *  - [NavigationSuiteType.NavigationDrawer] we compose a [PermanentNavigationDrawerContent] whose
 *  `currentDestination` argument is our [NavDestination] parameter [currentDestination], whose
 *  `navigationContentPosition` argument is our [ReplyNavigationContentPosition] variable
 *  `navContentPosition`, and whose `navigateToTopLevelDestination` argument is our lambda parameter
 *  [navigateToTopLevelDestination]
 *
 * In the `content` composable lambda argument of the [NavigationSuiteScaffoldLayout] we construct
 * a [ReplyNavSuiteScope] whose `navSuiteType` argument is our [NavigationSuiteType] variable
 * `navLayoutType` and use it as the receiver to compose our [ReplyNavSuiteScope] composable lambda
 * parameter [content].
 *
 * @param currentDestination The current [NavDestination] of the [ReplyApp] that it has retrieved
 * from the [NavBackStackEntry] of its [NavHostController].
 * @param navigateToTopLevelDestination A callback that is invoked with a [ReplyTopLevelDestination]
 * when the user indicates that he wishes to navigate to a different screen.
 * @param content the [ReplyNavSuiteScope] wrapped content to be displayed in the `content` of our
 * [NavigationSuiteScaffoldLayout].
 */
@Composable
fun ReplyNavigationWrapper(
    currentDestination: NavDestination?,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,
    content: @Composable ReplyNavSuiteScope.() -> Unit
) {
    val adaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    val windowSize: DpSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }

    val navLayoutType: NavigationSuiteType = when {
        adaptiveInfo.windowPosture.isTabletop -> NavigationSuiteType.NavigationBar
        adaptiveInfo.windowSizeClass.isCompact() -> NavigationSuiteType.NavigationBar
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
            windowSize.width >= 1200.dp -> NavigationSuiteType.NavigationDrawer

        else -> NavigationSuiteType.NavigationRail
    }
    val navContentPosition: ReplyNavigationContentPosition =
        when (adaptiveInfo.windowSizeClass.windowHeightSizeClass) {
            WindowHeightSizeClass.COMPACT -> ReplyNavigationContentPosition.TOP
            WindowHeightSizeClass.MEDIUM,
            WindowHeightSizeClass.EXPANDED -> ReplyNavigationContentPosition.CENTER

            else -> ReplyNavigationContentPosition.TOP
        }

    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    // Avoid opening the modal drawer when there is a permanent drawer or a bottom nav bar,
    // but always allow closing an open drawer.
    val gesturesEnabled: Boolean =
        drawerState.isOpen || navLayoutType == NavigationSuiteType.NavigationRail

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            ModalNavigationDrawerContent(
                currentDestination = currentDestination,
                navigationContentPosition = navContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        },
    ) {
        NavigationSuiteScaffoldLayout(
            layoutType = navLayoutType,
            navigationSuite = {
                when (navLayoutType) {
                    NavigationSuiteType.NavigationBar -> ReplyBottomNavigationBar(
                        currentDestination = currentDestination,
                        navigateToTopLevelDestination = navigateToTopLevelDestination
                    )

                    NavigationSuiteType.NavigationRail -> ReplyNavigationRail(
                        currentDestination = currentDestination,
                        navigationContentPosition = navContentPosition,
                        navigateToTopLevelDestination = navigateToTopLevelDestination,
                        onDrawerClicked = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )

                    NavigationSuiteType.NavigationDrawer -> PermanentNavigationDrawerContent(
                        currentDestination = currentDestination,
                        navigationContentPosition = navContentPosition,
                        navigateToTopLevelDestination = navigateToTopLevelDestination
                    )
                }
            }
        ) {
            ReplyNavSuiteScope(navSuiteType = navLayoutType).content()
        }
    }
}

/**
 * This is composed as the `navigationSuite` argument of the [NavigationSuiteScaffoldLayout] when
 * the [NavigationSuiteType] variable `navLayoutType` in [ReplyNavigationWrapper] is
 * [NavigationSuiteType.NavigationRail]. Our root Composable is a [NavigationRail] whose [Modifier]
 * `modifier` argument is a [Modifier.fillMaxHeight] to have is fill its entire incoming height
 * constraint, and whose [Color] `containerColor` argument is [ColorScheme.inverseOnSurface] of our
 * custom [MaterialTheme.colorScheme]. In its [ColumnScope] `content` Composable lambda argument we
 * compose a [Column] whose [Modifier] `modifier` argument is a [Modifier.layoutId] with `layoutId`
 * of [LayoutType.HEADER], whose `horizontalAlignment` argument is [Alignment.CenterHorizontally],
 * and whose `verticalArrangement` argument is [Arrangement.spacedBy] with `space` of `4.dp`. In
 * the [ColumnScope] `content` Composable lambda argument of the [Column] we compose:
 *  - a [NavigationRailItem] whose `selected` argument is `false`, whose `onClick` lambda argument
 *  is our lambda parameter [onDrawerClicked], and whose `icon` Composable lambda argument is a
 *  lambda that composes an [Icon] whose [ImageVector] `imageVector` argument is the [ImageVector]
 *  drawn by [Icons.Filled.Menu].
 *  - a [FloatingActionButton] whose `onClick` lambda argument is a do-nothing lambda, whose
 *  [Modifier] `modifier` argument is a [Modifier.padding] that adds `8.dp` to the `top` and `32.dp`
 *  to the `bottom`, whose [Color] `containerColor` argument is the [ColorScheme.tertiaryContainer]
 *  of our custom [MaterialTheme.colorScheme], and whose [Color] `contentColor` argument is the
 *  [ColorScheme.onTertiaryContainer] of our custom [MaterialTheme.colorScheme]. In the `content`
 *  Composable lambda argument of the [FloatingActionButton] we compose an [Icon] whose [ImageVector]
 *  `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Edit], whose [String]
 *  `contentDescription` argument is the [String] with resource ID `R.string.compose` ("Compose")
 *  and whose [Modifier] `modifier` argument is a [Modifier.size] whose `size` is `18.dp`.
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [Modifier.height] with `height` of `8.dp`
 *  (this is for NavigationRailHeaderPadding)
 *  - a [Spacer] whose [Modifier] `modifier` argument is a [Modifier.height] with `height` of `4.dp`
 *  (this is for NavigationRailVerticalPadding)
 *  - a [Column] whose [Modifier] `modifier` argument is a [Modifier.layoutId] with `layoutId` of
 *  [LayoutType.CONTENT], whose `horizontalAlignment` argument is [Alignment.CenterHorizontally],
 *  and whose `verticalArrangement` argument is [Arrangement.spacedBy] with a `space` of `4.dp`.
 *  In the [ColumnScope] `content` Composable lambda argument of the [Column] we use the [List.forEach]
 *  method of our [List] of [ReplyTopLevelDestination] field [TOP_LEVEL_DESTINATIONS] accepting each
 *  [ReplyTopLevelDestination] passed its `action` lambda argument in variable `replyDestination`,
 *  and then we compose a [NavigationRailItem] whose `selected` argument is `true` if our
 *  [NavDestination] parameter [currentDestination] was generated by the current
 *  [ReplyTopLevelDestination] in variable `replyDestination`, whose `onClick` lambda argument is
 *  a lambda that calls our lambda parameter [navigateToTopLevelDestination] with the current
 *  [ReplyTopLevelDestination] in variable `replyDestination`, and whose `icon` Composable lambda
 *  argument is a lambda that composes an [Icon] whose [ImageVector] `imageVector` argument is the
 *  [ReplyTopLevelDestination.selectedIcon] property of the current [ReplyTopLevelDestination] in
 *  variable `replyDestination`, and whose [String] `contentDescription` argument is the [String]
 *  whose resource ID is the [ReplyTopLevelDestination.iconTextId] property of the current
 *  [ReplyTopLevelDestination] in variable `replyDestination`.
 *
 * @param currentDestination The current [NavDestination] of the [ReplyApp] that it has retrieved
 * from the [NavBackStackEntry] of its [NavHostController].
 * @param navigationContentPosition The current [ReplyNavigationContentPosition] of the [ReplyApp]
 * either [ReplyNavigationContentPosition.TOP] or [ReplyNavigationContentPosition.CENTER] depending
 * on the [WindowHeightSizeClass] of the current [WindowAdaptiveInfo].
 * @param navigateToTopLevelDestination A callback that is invoked with a [ReplyTopLevelDestination]
 * when the user indicates that he wishes to navigate to a different screen.
 * @param onDrawerClicked A callback that should be invoked when the user indicates that he wishes
 * to open the drawer of the [ModalNavigationDrawer].
 */
@Composable
fun ReplyNavigationRail(
    currentDestination: NavDestination?,
    navigationContentPosition: ReplyNavigationContentPosition,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            modifier = Modifier.layoutId(layoutId = LayoutType.HEADER),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {
            NavigationRailItem(
                selected = false,
                onClick = onDrawerClicked,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(id = R.string.navigation_drawer)
                    )
                }
            )
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.compose),
                    modifier = Modifier.size(size = 18.dp)
                )
            }
            Spacer(Modifier.height(height = 8.dp)) // NavigationRailHeaderPadding
            Spacer(Modifier.height(height = 4.dp)) // NavigationRailVerticalPadding
        }

        Column(
            modifier = Modifier.layoutId(layoutId = LayoutType.CONTENT),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {
            TOP_LEVEL_DESTINATIONS.forEach { replyDestination: ReplyTopLevelDestination ->
                NavigationRailItem(
                    selected = currentDestination.hasRoute(destination = replyDestination),
                    onClick = { navigateToTopLevelDestination(replyDestination) },
                    icon = {
                        Icon(
                            imageVector = replyDestination.selectedIcon,
                            contentDescription = stringResource(
                                id = replyDestination.iconTextId
                            )
                        )
                    }
                )
            }
        }
    }
}

/**
 * This is used as the `navigationSuite` argument of the [NavigationSuiteScaffoldLayout] when the
 * [NavigationSuiteType] is [NavigationSuiteType.NavigationBar] which happens when the device's
 * [WindowAdaptiveInfo.windowSizeClass] is [WindowWidthSizeClass.COMPACT] or
 * [WindowHeightSizeClass.COMPACT]. Our root composable is a [NavigationBar] whose [Modifier]
 * `modifier` argument is a [Modifier.fillMaxWidth] to have it occupy its entire incoming width
 * constraint. In its [RowScope] `content` Composable lambda argument we use the [List.forEach]
 * method of the [List] of [ReplyTopLevelDestination] field [TOP_LEVEL_DESTINATIONS] accepting each
 * [ReplyTopLevelDestination] passed its `action` lambda argument in variable `replyDestination`
 * then we compose a [NavigationBarItem] whose `selected` argument is `true` if our [NavDestination]
 * parameter [currentDestination] has the same route as the current [ReplyTopLevelDestination] in
 * variable `replyDestination`, whose `onClick` lambda argument is a lambda that calls our lambda
 * parameter [navigateToTopLevelDestination] with the current [ReplyTopLevelDestination] in
 * variable `replyDestination`, and whose `icon` Composable lambda argument is a lambda that
 * composes an [Icon] whose [ImageVector] `imageVector` argument is the
 * [ReplyTopLevelDestination.selectedIcon] of the current [ReplyTopLevelDestination] in variable
 * `replyDestination`, and whose [String] `contentDescription` argument is the [String] whose
 * resource ID is the [ReplyTopLevelDestination.iconTextId] of the current [ReplyTopLevelDestination]
 * in variable `replyDestination`.
 *
 * @param currentDestination the current [NavDestination] of the [ReplyApp] that it has retrieved
 * from the [NavBackStackEntry] of its [NavHostController].
 * @param navigateToTopLevelDestination A callback that is invoked with a [ReplyTopLevelDestination]
 * in order to navigate to a different top level screen.
 */
@Composable
fun ReplyBottomNavigationBar(
    currentDestination: NavDestination?,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TOP_LEVEL_DESTINATIONS.forEach { replyDestination: ReplyTopLevelDestination ->
            NavigationBarItem(
                selected = currentDestination.hasRoute(destination = replyDestination),
                onClick = { navigateToTopLevelDestination(replyDestination) },
                icon = {
                    Icon(
                        imageVector = replyDestination.selectedIcon,
                        contentDescription = stringResource(id = replyDestination.iconTextId)
                    )
                }
            )
        }
    }
}

/**
 * This is used as the `navigationSuite` argument of the [NavigationSuiteScaffoldLayout] when the
 * [NavigationSuiteType] is [NavigationSuiteType.NavigationDrawer] which happens when the device's
 * [WindowAdaptiveInfo.windowSizeClass] is [WindowWidthSizeClass.EXPANDED] and the device's width
 * is greater than 1200.dp. Our root composable is a [PermanentDrawerSheet] whose [Modifier] `modifier`
 * argument is a [Modifier.sizeIn] whose `minWidth` argument is `200.dp` and whose `maxWidth` argument
 * is `300.dp`. In its [ColumnScope] `content` Composable lambda argument we compose a [Layout]
 * whose [Modifier] `modifier` argument is a [Modifier.background] whose [Color] `color` argument is
 * the [ColorScheme.surfaceContainerHigh] of our custom [MaterialTheme.colorScheme], with a
 * [Modifier.padding] chained to it that adds `16.dp` to all sides. In the `content` Composable lambda
 * argument we compose:
 *  - a [Column] whose [Modifier] `modifier` argument is a [Modifier.layoutId] with `layoutId` of
 *  [LayoutType.HEADER], whose `horizontalAlignment` argument is [Alignment.Start], and whose
 *  `verticalArrangement` argument is [Arrangement.spacedBy] with `space` of `4.dp`. In the
 *  [ColumnScope] `content` Composable lambda argument of the [Column] we compose:
 *  1. a [Text] whose [Modifier] `modifier` argument is a [Modifier.padding] that adds `16.dp`
 *  padding to all sides, whose [String] `text` argument is the [String] with resource ID
 *  `R.string.app_name` ("Reply") in uppercase, whose [TextStyle] `style` argument is the
 *  [Typography.titleMedium] of our custom [MaterialTheme.typography], and whose [Color] `color`
 *  argument is the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme].
 *  2. an [ExtendedFloatingActionButton] whose `onClick` lambda argument is a do-nothing lambda,
 *  whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth], with a [Modifier.padding]
 *  chained to that which adds `8.dp` to the `top` and `40.dp` to the `bottom`, whose [Color]
 *  `containerColor` argument is the [ColorScheme.tertiaryContainer] of our custom
 *  [MaterialTheme.colorScheme], and whose [Color] `contentColor` argument is the
 *  [ColorScheme.onTertiaryContainer] of our custom [MaterialTheme.colorScheme]. In the [RowScope]
 *  `content` Composable lambda argument of the [ExtendedFloatingActionButton] we compose an [Icon]
 *  whose [ImageVector] `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Edit],
 *  whose [String] `contentDescription` argument is the [String] with resource ID `R.string.compose`
 *  ("Compose"), and whose [Modifier] `modifier` argument is a [Modifier.size] whose `size` is `24.dp`.
 *  Next to that in the [RowScope] `content` Composable lambda argument of the
 *  [ExtendedFloatingActionButton] is a [Text] whose [String] `text` argument is the [String] with
 *  resource ID `R.string.compose` ("Compose"), whose [Modifier] `modifier` argument is a
 *  [RowScope.weight] of `weight` of `1f` (causes it to take up all the incoming width constraint
 *  once its sidlings are measured and placed), and whose [TextAlign] `textAlign` argument is
 *  [TextAlign.Center].
 *  - a second [Column] whose [Modifier] `modifier` argument is a [Modifier.layoutId] with `layoutId`
 *  of [LayoutType.CONTENT], with a [Modifier.verticalScroll] chained to that whose [ScrollState]
 *  `state` argument is a remembered [ScrollState] returned by [rememberScrollState] (this makes the
 *  [Column] scrollable), and whose `horizontalAlignment` argument is [Alignment.CenterHorizontally]
 *  to center its children horizontally.
 *  1. In the [ColumnScope] `content` Composable lambda argument of the [Column] we use the
 *  [List.forEach] method of the [List] of [ReplyTopLevelDestination] field [TOP_LEVEL_DESTINATIONS]
 *  accepting each [ReplyTopLevelDestination] passed its `action` lambda argument in variable
 *  `replyDestination` then we compose a [NavigationDrawerItem] whose `selected` argument is `true`
 *  if our [NavDestination] parameter [currentDestination] has the same route as the current
 *  [ReplyTopLevelDestination] in variable `replyDestination`, whose `label` Composable lambda
 *  argument is a lambda that composes a [Text] whose [String] `text` argument is the [String]
 *  whose resource ID is the [ReplyTopLevelDestination.iconTextId] of the current
 *  [ReplyTopLevelDestination] in variable `replyDestination`, and whose [Modifier] `modifier`
 *  argument is a [Modifier.padding] that adds `16.dp` to both horizontal sides. The `icon`
 *  Composable lambda argument is a lambda that composes an [Icon] whose [ImageVector] `imageVector`
 *  argument is the [ReplyTopLevelDestination.selectedIcon] of the current [ReplyTopLevelDestination]
 *  in variable `replyDestination`, and whose [String] `contentDescription` argument is the [String]
 *  with resrource ID [ReplyTopLevelDestination.iconTextId] of the current [ReplyTopLevelDestination]
 *  in variable `replyDestination`. The [NavigationDrawerItemColors] `colors` argument of the
 *  [NavigationDrawerItem] is the [NavigationDrawerItemDefaults.colors] with the
 *  `unselectedContainerColor` overridden to be [Color.Transparent] (the color to use for the icon
 *  when the item is unselected). The `onClick` lambda argument is a lambda that calls our lambda
 *  parameter [navigateToTopLevelDestination] with the current [ReplyTopLevelDestination] in variable
 *  `replyDestination`.
 *  - the [MeasurePolicy] `measurePolicy` argument is a call to [navigationMeasurePolicy] with its
 *  [ReplyNavigationContentPosition] `navigationContentPosition` argument our
 *  [ReplyNavigationContentPosition] parameter [navigationContentPosition].
 *
 * @param currentDestination the current [NavDestination] of the [ReplyApp] that it has retrieved
 * from the [NavBackStackEntry] of its [NavHostController].
 * @param navigationContentPosition The [ReplyNavigationContentPosition] to use when positioning the
 * [PermanentNavigationDrawerContent], one of [ReplyNavigationContentPosition.TOP] or
 * [ReplyNavigationContentPosition.CENTER].
 * @param navigateToTopLevelDestination A callback that is invoked with a [ReplyTopLevelDestination]
 * in order to navigate to a different top level screen.
 */
@Composable
fun PermanentNavigationDrawerContent(
    currentDestination: NavDestination?,
    navigationContentPosition: ReplyNavigationContentPosition,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,
) {
    PermanentDrawerSheet(
        modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        // TODO remove custom nav drawer content positioning when NavDrawer component supports it. ticket : b/232495216
        Layout(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(all = 16.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(layoutId = LayoutType.HEADER),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(all = 16.dp),
                        text = stringResource(id = R.string.app_name).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    ExtendedFloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.compose),
                            modifier = Modifier.size(size = 24.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.compose),
                            modifier = Modifier.weight(weight = 1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .layoutId(layoutId = LayoutType.CONTENT)
                        .verticalScroll(state = rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TOP_LEVEL_DESTINATIONS.forEach { replyDestination: ReplyTopLevelDestination ->
                        NavigationDrawerItem(
                            selected = currentDestination.hasRoute(destination = replyDestination),
                            label = {
                                Text(
                                    text = stringResource(id = replyDestination.iconTextId),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = replyDestination.selectedIcon,
                                    contentDescription = stringResource(
                                        id = replyDestination.iconTextId
                                    )
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            ),
                            onClick = { navigateToTopLevelDestination(replyDestination) }
                        )
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(
                navigationContentPosition = navigationContentPosition
            )
        )
    }
}

/**
 * This is used as the `drawerContent` argument of the [ModalNavigationDrawer] that is used by
 * [ReplyNavigationWrapper]. Our root composable is a [ModalDrawerSheet]. In its [ColumnScope]
 * `content` Composable lambda argument we compose a [Layout] whose [Modifier] `modifier` argument
 * is a [Modifier.background] whose [Color] `color` argument is the [ColorScheme.inverseOnSurface]
 * of our custom [MaterialTheme.colorScheme], with a [Modifier.padding] chained to that that adds
 * `16.dp` to all sides. In the `content` Composable lambda argument we compose:
 *
 *  - a [Column] whose [Modifier] `modifier` argument is a [Modifier.layoutId] with `layoutId` of
 *  [LayoutType.HEADER], whose `horizontalAlignment` argument is [Alignment.CenterHorizontally],
 *  and whose `verticalArrangement` argument is [Arrangement.spacedBy] with `space` of `4.dp`. In
 *  its [ColumnScope] `content` Composable lambda argument we compose:
 *  1. a [Row] whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth] that has it take up
 *  all of its incoming width constraint, with a [Modifier.padding] chained to that which adds `16.dp`
 *  padding to all sides, whose `horizontalArrangement` argument is [Arrangement.SpaceBetween], and
 *  whose `verticalAlignment` argument is [Alignment.CenterVertically]. In the [RowScope] `content`
 *  Composable lambda argument we compose a [Text] whose `text` argument is the [String] with resource
 *  ID `R.string.app_name` ("Reply"), whose [TextStyle] `style` argument is the [Typography.titleMedium]
 *  of our custom [MaterialTheme.typography], and whose [Color] `color` argument is the
 *  [ColorScheme.primary] of our custom [MaterialTheme.colorScheme]. Next in the [Row] is an
 *  [IconButton] whose `onClick` lambda argument is a lambda that calls our lambda parameter
 *  [onDrawerClicked], and whose `content` Composable lambda argument is a lambda that composes an
 *  [Icon] whose [ImageVector] `imageVector` argument is the [ImageVector] drawn by
 *  [Icons.AutoMirrored.Filled.MenuOpen], and whose [String] `contentDescription` argument is the
 *  [String] with resource ID `R.string.close_drawer` ("Close drawer").
 *  2. Next in the [Column] is an [ExtendedFloatingActionButton] whose `onClick` lambda argument is
 *  a do-nothing lambda, whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth], with a
 *  [Modifier.padding] chained to that which adds `8.dp` to its `top`, and `4.dp` to its `bottom`,
 *  whose [Color] `containerColor` argument is the [ColorScheme.tertiaryContainer] of our custom
 *  [MaterialTheme.colorScheme], and whose [Color] `contentColor` argument is the
 *  [ColorScheme.onTertiaryContainer] of our custom [MaterialTheme.colorScheme]. In the [RowScope]
 *  `content` Composable lambda argument of the [IconButton] we compose an [Icon] whose [ImageVector]
 *  `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Edit], whose [String]
 *  `contentDescription` argument is the [String] with resource ID `R.string.compose` ("Compose"),
 *  and whose [Modifier] `modifier` argument is a [Modifier.size] whose `size` is `18.dp`. Next to
 *  that in the [RowScope] `content` Composable lambda argument of the [ExtendedFloatingActionButton]
 *  we have a [Text] whose [String] `text` argument is the [String] with resource ID `R.string.compose`
 *  ("Compose"), whose [Modifier] `modifier` argument is a [RowScope.weight] of `weight` of `1f` to
 *  have it take up all of the incoming width constraint once its sidlings are measured and placed,
 *  and whose [TextAlign] `textAlign` argument is [TextAlign.Center] to align its `text` at its
 *  center.
 *
 *  - Below the [Column] is another [Column] whose [Modifier] `modifier` argument is a
 *  [Modifier.layoutId] whose `layoutId` is [LayoutType.CONTENT], with a [Modifier.verticalScroll]
 *  chained to that which allows the [Column] to be scrollable, and whose `horizontalAlignment`
 *  argument is [Alignment.CenterHorizontally]. In the [ColumnScope] `content` Composable lambda
 *  argument we use the [List.forEach] method of the [List] of [ReplyTopLevelDestination] field
 *  [TOP_LEVEL_DESTINATIONS] capturing each [ReplyTopLevelDestination] in variable `replyDestination`
 *  and in the `action` lambda argument we compose a [NavigationDrawerItem] whose `selected` argument
 *  is `true` if our [NavDestination] parameter [currentDestination] has the same route as the current
 *  [ReplyTopLevelDestination] in variable `replyDestination`, whose `label` Composable lambda
 *  argument is a lambda that composes a [Text] whose `text` argument is the [String] with resource
 *  ID [ReplyTopLevelDestination.iconTextId] of the current [ReplyTopLevelDestination] in variable
 *  `replyDestination`, and whose [Modifier] `modifier` argument is a [Modifier.padding] that adds
 *  `16.dp` to each `horizontal` side, whose `icon` Composable lambda argument is a lambda that
 *  composes an [Icon] whose [ImageVector] `imageVector` argument is the
 *  [ReplyTopLevelDestination.selectedIcon] of the current [ReplyTopLevelDestination] in variable
 *  `replyDestination`, and whose [String] `contentDescription` argument is the [String] with
 *  resource ID [ReplyTopLevelDestination.iconTextId] of the current [ReplyTopLevelDestination] in
 *  variable `replyDestination`. The [NavigationDrawerItemColors] `colors` argument of the
 *  [NavigationDrawerItem] is [NavigationDrawerItemDefaults.colors] with the
 *  `unselectedContainerColor` overridden to be [Color.Transparent] (the color to use for the icon
 *  when the item is unselected). The `onClick` lambda argument is a lambda that calls our lambda
 *  parameter [navigateToTopLevelDestination] with the current [ReplyTopLevelDestination] in variable
 *  `replyDestination`.
 *
 * The [MeasurePolicy] `measurePolicy` argument of the [Layout] is a call to [navigationMeasurePolicy]
 * with its [ReplyNavigationContentPosition] `navigationContentPosition` argument our
 * [ReplyNavigationContentPosition] parameter [navigationContentPosition].
 *
 * @param currentDestination the current [NavDestination] of the [ReplyApp] that it has retrieved
 * from the [NavBackStackEntry] of its [NavHostController].
 * @param navigationContentPosition The [ReplyNavigationContentPosition] to use when positioning the
 * nav drawer, one of [ReplyNavigationContentPosition.TOP] or [ReplyNavigationContentPosition.CENTER].
 * It is passed as the `navigationContentPosition` argument of the [navigationMeasurePolicy] method
 * that creates the [MeasurePolicy] of the [ModalDrawerSheet].
 * @param navigateToTopLevelDestination A callback that is invoked with a [ReplyTopLevelDestination]
 * when the user indicates that they wish to navigate to a different [ReplyTopLevelDestination].
 * @param onDrawerClicked a lambda that should be called when the user indicates that they wish to
 * close the [ModalNavigationDrawer].
 */
@Composable
fun ModalNavigationDrawerContent(
    currentDestination: NavDestination?,
    navigationContentPosition: ReplyNavigationContentPosition,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    ModalDrawerSheet {
        // TODO remove custom nav drawer content positioning when NavDrawer component supports it. ticket : b/232495216
        Layout(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                .padding(all = 16.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(layoutId = LayoutType.HEADER),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = onDrawerClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                                contentDescription = stringResource(id = R.string.close_drawer)
                            )
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.compose),
                            modifier = Modifier.size(size = 18.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.compose),
                            modifier = Modifier.weight(weight = 1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .layoutId(layoutId = LayoutType.CONTENT)
                        .verticalScroll(state = rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TOP_LEVEL_DESTINATIONS.forEach { replyDestination: ReplyTopLevelDestination ->
                        NavigationDrawerItem(
                            selected = currentDestination.hasRoute(destination = replyDestination),
                            label = {
                                Text(
                                    text = stringResource(id = replyDestination.iconTextId),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = replyDestination.selectedIcon,
                                    contentDescription = stringResource(
                                        id = replyDestination.iconTextId
                                    )
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            ),
                            onClick = { navigateToTopLevelDestination(replyDestination) }
                        )
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(
                navigationContentPosition = navigationContentPosition
            )
        )
    }
}

/**
 * This is used as the `measurePolicy` argument of the [Layout] that is used by the [ModalDrawerSheet]
 * that is used by [ModalNavigationDrawerContent] and by the [PermanentDrawerSheet] that is used by
 * [PermanentNavigationDrawerContent]. The [Layout] that we are the `measurePolicy` argument of passes
 * the [MeasureScope] Composable lambda argument of [MeasurePolicy] a [List]] of [Measurable] which
 * we accept in variable `measurables`, and [Constraints] which we accept in variable `constraints`.
 * These are derived from the Composables in its `content` Composable lambda argument. Each [Measurable]
 * is created from one of the Composables and the [Constraints] are the incoming constraints for the
 * [Layout] Composable.
 *
 * In the [MeasureScope] lambda of the [MeasurePolicy] we first declare our lateinit [Measurable]
 * variable `var headerMeasurable`, and our lateinit [Measurable] variable `var contentMeasurable`.
 * We then use the [List.forEach] method of the [List] of [Measurable] in variable `measurables`
 * to loop throught the [List] and when the [Measurable.layoutId] is [LayoutType.HEADER] we set
 * `headerMeasurable` to the current [Measurable], and when the [Measurable.layoutId] is
 * [LayoutType.CONTENT] we set `contentMeasurable` to the current [Measurable]. We initialize our
 * [Placeable] variables `val headerPlaceable` to the [Placeable] returned by the [Measurable.measure]
 * method of `headerMeasurable` with its [Constraints] `constraints` argument our [Constraints]
 * variable `constraints`, and our [Placeable] variable `val contentPlaceable` to the [Placeable]
 * returned by the [Measurable.measure] method of `contentMeasurable` with its [Constraints]
 * `constraints` argument the [Constraints] returned by the [Constraints.offset] method of our
 * [Constraints] variable `constraints` for the `vertical` offset argument of minus the
 * [Placeable.height] of `headerPlaceable`.
 *
 * Then we call the [MeasureScope.layout] method with its `width` argument the [Constraints.maxWidth]
 * of `constraints`, and its `height` argument the [Constraints.maxHeight] of `constraints`. In the
 * [Placeable.PlacementScope] `placementBlock` lambda argument we place the [Placeable] variable
 * `headerPlaceable` at `x` = 0, `y` = 0 using the [Placeable.PlacementScope.placeRelative] extension
 * function of `headerPlaceable`.
 *
 * We initialize our [Int] variable `val nonContentVerticalSpace` to the [Constraints.maxHeight] of
 * `constraints` minus the [Placeable.height] of `contentPlaceable`. We initialize our [Int] variable
 * `val contentPlaceableY` based on the value of our [ReplyNavigationContentPosition] parameter
 * [navigationContentPosition]:
 *  - [ReplyNavigationContentPosition.TOP] -> `0`
 *  - [ReplyNavigationContentPosition.CENTER] -> `nonContentVerticalSpace` / `2`
 * then use the [Int.coerceAtLeast] method to coerce its `minimumValue` to be at least the
 * [Placeable.height] of `headerPlaceable`.
 *
 * Finally we call the [Placeable.PlacementScope.placeRelative] extension method of `contentPlaceable`
 * to place it at `x` = `0`, `y` = `contentPlaceableY` and return the [MeasureResult] returned by
 * [MeasurePolicy] to the caller of [navigationMeasurePolicy]. (at least i think that's how it works).
 *
 * @param navigationContentPosition The [ReplyNavigationContentPosition] to use when positioning the
 * [Measurable] in the [List] of [Measurable] whose [Measurable.layoutId] is [LayoutType.CONTENT],
 * one of [ReplyNavigationContentPosition.TOP] or [ReplyNavigationContentPosition.CENTER].
 */
fun navigationMeasurePolicy(
    navigationContentPosition: ReplyNavigationContentPosition,
): MeasurePolicy {
    return MeasurePolicy { measurables: List<Measurable>, constraints: Constraints ->
        lateinit var headerMeasurable: Measurable
        lateinit var contentMeasurable: Measurable
        measurables.forEach {
            when (it.layoutId) {
                LayoutType.HEADER -> headerMeasurable = it
                LayoutType.CONTENT -> contentMeasurable = it
                else -> error("Unknown layoutId encountered!")
            }
        }

        val headerPlaceable: Placeable = headerMeasurable.measure(constraints = constraints)
        val contentPlaceable: Placeable = contentMeasurable.measure(
            constraints = constraints.offset(vertical = -headerPlaceable.height)
        )
        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            // Place the header, this goes at the top
            headerPlaceable.placeRelative(x = 0, y = 0)

            // Determine how much space is not taken up by the content
            val nonContentVerticalSpace: Int = constraints.maxHeight - contentPlaceable.height

            val contentPlaceableY: Int = when (navigationContentPosition) {
                // Figure out the place we want to place the content, with respect to the
                // parent (ignoring the header for now)
                ReplyNavigationContentPosition.TOP -> 0
                ReplyNavigationContentPosition.CENTER -> nonContentVerticalSpace / 2
            }
                // And finally, make sure we don't overlap with the header.
                .coerceAtLeast(minimumValue = headerPlaceable.height)

            contentPlaceable.placeRelative(x = 0, y = contentPlaceableY)
        }
    }
}

/**
 * This enum is used to determine which [Measurable] corresponds to the header Composable and which
 * corresponds to the content Composable of the [Layout].
 */
enum class LayoutType {
    /**
     * The header Composable of the [Layout].
     */
    HEADER,

    /**
     * The content Composable of the [Layout].
     */
    CONTENT
}

/**
 * Convenience function to determine if the [ReplyTopLevelDestination] parameter [destination] has
 * the same route as the receiver [NavDestination].
 */
fun NavDestination?.hasRoute(destination: ReplyTopLevelDestination): Boolean =
    this?.hasRoute(destination.route::class) ?: false
