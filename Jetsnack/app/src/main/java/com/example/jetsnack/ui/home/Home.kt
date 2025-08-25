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

package com.example.jetsnack.ui.home

import android.content.res.Configuration
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.home.search.Search
import com.example.jetsnack.ui.navigation.JetsnackNavController
import com.example.jetsnack.ui.navigation.MainDestinations
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import java.util.Locale

/**
 * This [NavGraphBuilder] extension function adds a Composable [composable] to the [NavGraphBuilder]
 * that is wrapped in a [CompositionLocalProvider] with the key [LocalNavAnimatedVisibilityScope]
 * providing the current [AnimatedVisibilityScope]. It is used for the shared transition between
 * the [MainDestinations.HOME_ROUTE] and the [MainDestinations.SNACK_DETAIL_ROUTE] when a
 * [Snack] is clicked and then back again. Our root Composable is just a call to the [composable]
 * method, passing it all our parameters as its arguments, except for `content` argument which
 * we wrap in a [CompositionLocalProvider] that provides the `current` [AnimatedVisibilityScope]
 * under the key [LocalNavAnimatedVisibilityScope] to our [content] parameter before using it as
 * the `content` argument of the [composable] method.
 *
 * @param route The destination route for the Composable created by the [composable] method.
 * @param arguments A [List] of [NamedNavArgument]s to be passed to the [composable] method to be
 * associated with the destination.
 * @param deepLinks list of deep links to associate with the destinations. Used as the `deepLinks`
 * argument of the [composable] method.
 * @param enterTransition callback to determine the destination's enter transition. Used as the
 * `enterTransition` argument of the [composable] method. Our usages do not pass us any so the
 * default [fadeIn] with an `animationSpec` of [nonSpatialExpressiveSpring] is used.
 * @param exitTransition callback to determine the destination's exit transition. Used as the
 * `exitTransition` argument of the [composable] method. Our usages do not pass us any so the
 * default [fadeIn] with an `animationSpec` of [nonSpatialExpressiveSpring] is used.
 * @param popEnterTransition callback to determine the destination's popEnter transition. Used as
 * the `popEnterTransition` argument of the [composable] method. Our usages do not pass us any so
 * the default of [enterTransition] is used.
 * @param popExitTransition callback to determine the destination's popExit transition. Used as the
 * `popExitTransition` argument of the [composable] method. Our usages do not pass us any so
 * the default of [exitTransition] is used.
 * @param content Composable lambda taking a [NavBackStackEntry]. It is called by the `content`
 * Composable lambda argument of the [CompositionLocalProvider] wrapping it. The
 * [CompositionLocalProvider] provides the [AnimatedVisibilityScope] of the [composable] method
 * under the key [LocalNavAnimatedVisibilityScope].
 */
fun NavGraphBuilder.composableWithCompositionLocal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        fadeIn(animationSpec = nonSpatialExpressiveSpring())
    },
    exitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        fadeOut(animationSpec = nonSpatialExpressiveSpring())
    },
    popEnterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    popExitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition
    ) {
        CompositionLocalProvider(
            value = LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}

/**
 * This adds [composable] routes for all the home screens: [Feed], [Search], [Cart], and [Profile].
 * Our content block just consists of 4 calls to the [NavGraphBuilder.composable] method to add
 * the following routes to our [NavGraphBuilder]:
 *  - `route` = `HomeSections.FEED.route` composes a [Feed] whose `onSnackClick` lambda argument
 *  calls our [onSnackSelected] lambda parameter passing it the [Long] and [String] passed the
 *  `onSnackClick` lambda as well as the [NavBackStackEntry] passed the `content` Composable of
 *  the [composable] method, and whose [Modifier] `modifier` argument is our [Modifier] parameter
 *  [modifier].
 *  - `route` = `HomeSections.SEARCH.route` composes a [Search] whose `onSnackClick` lambda argument
 *  calls our [onSnackSelected] lambda parameter passing it the [Long] and [String] passed the
 *  `onSnackClick` lambda as well as the [NavBackStackEntry] passed the content Composable of the
 *  composable method, and whose [Modifier] `modifier` argument is our [Modifier] parameter
 *  [modifier].
 *  - `route` = `HomeSections.CART.route` composes a [Cart] whose `onSnackClick` lambda argument
 *  calls our [onSnackSelected] lambda parameter passing it the [Long] and [String] passed the
 *  `onSnackClick` lambda as well as the [NavBackStackEntry] passed the content Composable of the
 *  composable method, and whose [Modifier] `modifier` argument is our [Modifier] parameter
 *  [modifier].
 *  - `route` = `HomeSections.PROFILE.route` composes a [Profile] whose [Modifier] `modifier`
 *  argument is our [Modifier] parameter [modifier].
 *
 * @param onSnackSelected a lambda that can be called with the [Snack.id] of a [Snack] that is
 * clicked, a [String] identifying the "orgin" and the [NavBackStackEntry] that is passed to the
 * `content` Composable lambda argument.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [MainContainer] passes us a [Modifier.padding] that adds the [PaddingValues]
 * that [JetsnackScaffold] passes its `content` Composable lambda parameter, followed by a chain
 * to [Modifier.consumeWindowInsets] that consumes those same [PaddingValues].
 */
fun NavGraphBuilder.addHomeGraph(
    onSnackSelected: (Long, String, NavBackStackEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    composable(route = HomeSections.FEED.route) { from: NavBackStackEntry ->
        Feed(
            onSnackClick = { id: Long, origin: String -> onSnackSelected(id, origin, from) },
            modifier = modifier
        )
    }
    composable(route = HomeSections.SEARCH.route) { from: NavBackStackEntry ->
        Search(
            onSnackClick = { id: Long, origin: String -> onSnackSelected(id, origin, from) },
            modifier = modifier
        )
    }
    composable(route = HomeSections.CART.route) { from: NavBackStackEntry ->
        Cart(
            onSnackClick = { id: Long, origin: String -> onSnackSelected(id, origin, from) },
            modifier = modifier
        )
    }
    composable(route = HomeSections.PROFILE.route) {
        Profile(modifier = modifier)
    }
}

/**
 * This enum is used to identify and label the different tabs in our [JetsnackBottomBar], as well as
 * holding the `route` [String] in its [HomeSections.route] property to use for a call to
 * [NavHostController.navigate]
 */
enum class HomeSections(
    /**
     * A [String] resource ID representing the title of this tab.
     */
    @param:StringRes val title: Int,
    /**
     * An [ImageVector] to use for the [Icon] of this tab.
     */
    val icon: ImageVector,
    /**
     * The [String] to use to use when calling [NavHostController.navigate] to navigate to the
     * screen that this [HomeSections] represents.
     */
    val route: String
) {
    /**
     * This is [HomeSections] for the [Feed] screen.
     */
    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),

    /**
     * This is [HomeSections] for the [Search] screen.
     */
    SEARCH(R.string.home_search, Icons.Outlined.Search, "home/search"),

    /**
     * This is [HomeSections] for the [Cart] screen.
     */
    CART(R.string.home_cart, Icons.Outlined.ShoppingCart, "home/cart"),

    /**
     * This is [HomeSections] for the [Profile] screen.
     */
    PROFILE(R.string.home_profile, Icons.Outlined.AccountCircle, "home/profile")
}

/**
 * This is used as the `bottomBar` argument of the [JetsnackScaffold] used by [MainContainer]. We
 * start by initializing and remembering our [List] of [String] variable `val routes` to the [List]
 * that [Array.map] returns of all of the [HomeSections.route] in our [Array] of [HomeSections]
 * parameter [tabs], and we initialize our [HomeSections] variable `val currentSection` to the
 * [HomeSections] returned by the [Array.first] method of [tabs] search for the [HomeSections] whose
 * [HomeSections.route] is equal to our [String] parameter [currentRoute].
 *
 * Our root Composable then is a [JetsnackSurface] whose `modifier` argument is our [Modifier]
 * parameter [modifier], whose `color` argument is our [Color] parameter [color] and whose
 * `contentColor` argument is our [Color] parameter [contentColor]. In the `content` Composable
 * lambda argument of the [JetsnackSurface] we initialize our [SpringSpec] of [Float] variable
 * `val springSpec` to a new instance of [spatialExpressiveSpring]. Then we compose a
 * [JetsnackBottomNavLayout] whose `selectedIndex` argument is the [HomeSections.ordinal] of our
 * [HomeSections] variable `currentSection`, whose `itemCount` argument is the [List.size] of our
 * [List] of [String] variable `routes`, whose `indicator` argument is a [BoxScope] Composable
 * lambda that composes our [JetsnackBottomNavIndicator], whose `animSpec` argument is our
 * [SpringSpec] of [Float] variable `springSpec`, and whose `modifier` argument is a
 * [Modifier.navigationBarsPadding] that adds padding to accommodate the navigation bars insets.
 *
 * In the `content` Composable lambda argument of the [JetsnackBottomNavLayout] we initialize our
 * [Configuration] variable `val configuration` to the `current` [LocalConfiguration], and initialize
 * our [Locale] variable `val currentLocale` to the [Locale] that the [ConfigurationCompat.getLocales]
 * method returns for `configuration`, defaulting to [Locale.getDefault] if that is `null`. Then we
 * use the [Array.forEach] method of our [Array] of [HomeSections] parameter [tabs] to loop through
 * all of its members and in its `action` lambda argument we capture each [HomeSections] in the
 * variable `section` then we set [Boolean] variable `val selected` to `true` if this `section` is
 * equal to `currentSection`, and initialize our animated [Color] variable `val tint` to the value
 * that [animateColorAsState] returns when its [Color] `targetValue` argument is when `selected` is
 * `true` the [JetsnackColors.iconInteractive] of our custom [JetsnackTheme.colors], or when it
 * is `false` the [JetsnackColors.iconInteractiveInactive]. Next we initialize our [String]
 * variable `val text` to the string whose resource ID is the [HomeSections.title] of `section`
 * using the [String.uppercase] method to convert it to uppercase for the [Locale] `currentLocale`.
 *
 * Finally we compose a [JetsnackBottomNavigationItem] whose arguments are:
 *  - `icon` a [BoxScope] Composable lambda: we pass an [Icon] whose [ImageVector] `imageVector`
 *  argument is the [ImageVector] drawn by the [HomeSections.icon] of `section`, whose [Color]
 *  `tint` argument is our `tint` variable, and whose [String] `contentDescription` argument is our
 *  `text` variable (which is the [String] with resource ID [HomeSections.title] of `section`).
 *  - `text` a [BoxScope] Composable lambda: we pass a lambda which composes a [Text] whose [String]
 *  `text` argument is our `text` variable, whose [Color] `color` argument is our animated `tint`
 *  variable, whose [TextStyle] `style` argument is the [Typography.labelLarge] of our custom
 *  [MaterialTheme.typography], and whose [Int] `maxLines` argument is `1`.
 *  - `selected` a [Boolean]: we pass our [Boolean] variable `selected`.
 *  - `onSelected` a lambda: we pass a lambda that calls our lambda parameter [navigateToRoute] with
 *  the [HomeSections.route] of `section`.
 *  - `animSpec` an [AnimationSpec] of [Float]: we pass our [AnimationSpec] of [Float] variable
 *  `springSpec` (a [spatialExpressiveSpring] recall).
 *  - `modifier` a [Modifier] instance: we pass our [Modifier] variable [BottomNavigationItemPadding]
 *  with a [Modifier.clip] chained to that that clips to the `shape` [BottomNavIndicatorShape].
 *
 * @param tabs a [List] of [HomeSections] to display in the [JetsnackBottomBar].
 * @param currentRoute a [String] representing the current destination of the [NavHostController].
 * @param navigateToRoute a lambda that can be called to navigate to a [String] destination of the
 * [NavHostController]. Our caller [MainContainer] passes us a reference to the
 * [JetsnackNavController.navigateToBottomBarRoute] method.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [MainContainer] passes us a [SharedTransitionScope.renderInSharedTransitionScopeOverlay]
 * whose `zIndexInOverlay` argument is `1f` (causes us to render on top of the other shared elements),
 * and chained to that is a [AnimatedVisibilityScope.animateEnterExit] whose `enter` is a [fadeIn]
 * plus a [slideInVertically], and whose `exit` is a [fadeOut] plus a [slideOutVertically].
 * @param color the background [Color] to use for our [JetsnackSurface]. Our caller does not pass us
 * any so the default [JetsnackColors.iconPrimary] of our custom [JetsnackTheme.colors] is used.
 * @param contentColor used as the `contentColor` argument of our [JetsnackSurface], it wraps it
 * in a [CompositionLocalProvider] that provides it as the [LocalContentColor]. Our caller does not
 * pass us any so the default [JetsnackColors.iconInteractive] of our custom [JetsnackTheme.colors]
 * is used.
 */
@Composable
fun JetsnackBottomBar(
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = JetsnackTheme.colors.iconPrimary,
    contentColor: Color = JetsnackTheme.colors.iconInteractive
) {
    val routes: List<String> = remember { tabs.map { it.route } }
    val currentSection: HomeSections = tabs.first { it.route == currentRoute }

    JetsnackSurface(
        modifier = modifier,
        color = color,
        contentColor = contentColor
    ) {
        val springSpec: SpringSpec<Float> = spatialExpressiveSpring()
        JetsnackBottomNavLayout(
            selectedIndex = currentSection.ordinal,
            itemCount = routes.size,
            indicator = { JetsnackBottomNavIndicator() },
            animSpec = springSpec,
            modifier = Modifier.navigationBarsPadding()
        ) {
            val configuration: Configuration = LocalConfiguration.current
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()

            tabs.forEach { section: HomeSections ->
                val selected: Boolean = section == currentSection
                val tint: Color by animateColorAsState(
                    targetValue = if (selected) {
                        JetsnackTheme.colors.iconInteractive
                    } else {
                        JetsnackTheme.colors.iconInteractiveInactive
                    },
                    label = "tint"
                )

                val text: String = stringResource(section.title).uppercase(currentLocale)

                JetsnackBottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = section.icon,
                            tint = tint,
                            contentDescription = text
                        )
                    },
                    text = {
                        Text(
                            text = text,
                            color = tint,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { navigateToRoute(section.route) },
                    animSpec = springSpec,
                    modifier = BottomNavigationItemPadding
                        .clip(shape = BottomNavIndicatorShape)
                )
            }
        }
    }
}

/**
 * This is used by [JetsnackBottomBar] to measure and place all the Composables that it uses. First
 * we initialize and remember our [List] of [Animatable] of `size` [itemCount] variable
 * `val selectionFractions` to a [List] which has an [Animatable] whose `initialValue` argument is
 * `1f` if the list index is equal to our [Int] parameter [selectedIndex], or `0f` if it is not.
 * Then we use the [Iterable.forEachIndexed] method of `selectionFractions` to loop over its members
 * setting [Float] variable `val target` to `1f` if the list index is equal to our [Int] parameter
 * [selectedIndex] or to `0f` if it is not. Then we launch a [LaunchedEffect] for each of its members
 * whose `key1` argument is our `target` variable and whose `key2` argument is our [AnimationSpec]
 * parameter [animSpec] and in that [LaunchedEffect] we call the [Animatable.animateTo] method of
 * the current member of `selectionFractions` with its `targetValue` argument set to our `target`
 * variable and its `animationSpec` argument set to our [AnimationSpec] parameter [animSpec].
 *
 * Next we animate the position of our [indicator] Composable lambda parameter. To do this we
 * initialize and remember our [Animatable] of [Float] variable to a new instance with its
 * `initialValue` set to `0f`, then we initialize our [Float] variable `val targetIndicatorIndex`
 * to the [Float] value of our [Int] parameter [selectedIndex]. Then we launch a [LaunchedEffect]
 * whose `key1` argument is our [Float] variable `targetIndicatorIndex` and in that [LaunchedEffect]
 * we call the [Animatable.animateTo] method of `indicatorIndex` with its `targetValue` argument set
 * to our `targetIndicatorIndex` variable and its `animationSpec` argument set to our [AnimationSpec]
 * parameter [animSpec].
 *
 * Our root Composable is a [Layout] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.height] whose `height` is [BottomNavHeight], and whose `content` Composable
 * lambda argument composes our Composable lambda parameter [content] followed by a [Box] whose
 * `modifier` argument is a [Modifier.layoutId] whose `layoutId` argument is the [String] "indicator",
 * and whose `content` argument is our [BoxScope] Composable lambda parameter [indicator]. In the
 * [MeasurePolicy] `measurePolicy` [MeasureScope] lambda argument we accept the [List] of [Measurable]
 * passed the lambda in our `measurables` variable and the [Constraints] passed the lambda in our
 * variable `constraints` ([Layout] produces the [List] of [Measurable] from the Composables that
 * it finds in its `content` argument, in our case the four [JetsnackBottomNavigationItem] in our
 * [content] composable lambda parameter and our [Box]). In the body of the `measurePolicy` lambda
 * we first [check] that our [Int] parameter [itemCount] is equal to the [List.size] minus `1` of
 * our [List] of [Measurable] variable `measurables` throwing [IllegalStateException] if it is not.
 * If we pass this check we proceed to initialize our [Int] variable `val unselectedWidth` to the
 * [Constraints.maxWidth] of `constraints` divided by out [Int] parameter [itemCount] plus `1` (this
 * divides the width into n+1 slots). We initialize our [Int] variable `val selectedWidth` to `2`
 * times `unselectedWidth` (gives the selected item 2 slots). We initialize our [Measurable] variable
 * `val indicatorMeasurable` to the first [Measurable] in our [List] of [Measurable] variable whose
 * [Measurable.layoutId] is "indicator".
 *
 * Next we initialize our [List] of [Placeable] variable `val itemPlaceables` to a [List] which uses
 * the [Iterable.filterNot] method of `measurables` to filter out the [Measurable] variable
 * `indicatorMeasurable`, and then uses the [Iterable.mapIndexed] method of the remaining [List] of
 * [Placeable] capturing the [Int] index in the `index` variable, and the [Measurable] in the
 * `measurable` variable. In the `action` lambda argument we initialize our [Int] variable `val width`
 * to the [lerp] of `unselectedWidth` and `selectedWidth` linearly interpolated based upon the
 * the value of the `index` entry in the [List] of [Animatable] of [Float] variable `selectionFractions`.
 * Then we measure the [Measurable] variable `measurable` with its `constraints` argument set to a
 * copy of our [Constraints] variable `constraints` with its `minWidth` and `maxWidth` set to our
 * `width` variable. We add the [Placeable] to our [List] of [Placeable] variable `itemPlaceables`,
 * and loop to the next [Measurable] in `measurables`.
 *
 * We initialize our [Placeable] variable `val indicatorPlaceable` to the [Measurable.measure] of
 * [Measurable] variable `indicatorMeasurable` with its `constraints` argument set to a copy of our
 * [Constraints] variable `constraints` with its `minWidth` and `maxWidth` set to `selectedWidth`.
 *
 * Finally we call the [MeasureScope.layout] method with its `width` argument set to the
 * [Constraints.maxWidth] and its `height` argument set to the maximum [Placeable.height] of all of
 * the [Placeable]s in `itemPlaceables` (or `0` if the [List] is empty). In the
 * [Placeable.PlacementScope] `placementBlock` lambda argument we initialize our [Float] variable
 * `val indicatorLeft` to the value of the [Animatable] of [Float] variable `indicatorIndex`
 * multiplied by `unselectedWidth`. Then we use the [Placeable.PlacementScope.placeRelative]
 * extension method of the [Placeable] variable `indicatorPlaceable` to place it at the `x` position
 * of `indicatorLeft` and a `y` position of `0`. Next we initialize our [Int] variable `var x` to `0`.
 * Then we use the [List.forEach] method of `itemPlaceables` to loop over all of its [Placeable]s
 * capturing them in the `placeable` variable and then call the [Placeable.PlacementScope.placeRelative]
 * extension method of the [Placeable] `placeable` to place it at the `x` position of `x` and a `y`
 * position of `0`. Then we add the [Placeable.width] of `placeable` to `x` and loop back for the
 * next [Placeable] in `itemPlaceables`.
 *
 * @param selectedIndex the index of the item that is currently selected. Our caller [JetsnackBottomBar]
 * passes us the [HomeSections.ordinal] of the [HomeSections] currently selected.
 * @param itemCount the number of items in the [JetsnackBottomBar]. Our caller [JetsnackBottomBar]
 * passes us the [List.size] of the [List] of [String]'s it creates from all of the
 * [HomeSections.route] of the [HomeSections] in its [Array] of [HomeSections] parameter `tabs`.
 * @param animSpec the [AnimationSpec] of [Float] used to animate all the animations in our
 * [JetsnackBottomNavLayout]. Our caller [JetsnackBottomBar] passes us a [spatialExpressiveSpring].
 * @param indicator a [BoxScope] Composable lambda that composes a [JetsnackBottomNavIndicator]
 * that we use to highlight the currently selected [JetsnackBottomNavigationItem].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetsnackBottomBar] passes us a [Modifier.navigationBarsPadding] that
 * adds padding to accommodate the navigation bars insets.
 * @param content Composable lambda that our caller [JetsnackBottomBar] passes us that composes all
 * of the [JetsnackBottomNavigationItem]'s in the [JetsnackBottomBar]. It will be used in the
 * `content` argument of our [Layout] composable along with a [Box] holding our [indicator] parameter.
 */
@Composable
private fun JetsnackBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Track how "selected" each item is [0, 1]
    val selectionFractions: List<Animatable<Float, AnimationVector1D>> = remember(itemCount) {
        List(size = itemCount) { i: Int ->
            Animatable(initialValue = if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index: Int, selectionFraction: Animatable<Float, AnimationVector1D> ->
        val target: Float = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(key1 = target, key2 = animSpec) {
            selectionFraction.animateTo(targetValue = target, animationSpec = animSpec)
        }
    }

    // Animate the position of the indicator
    val indicatorIndex: Animatable<Float, AnimationVector1D> =
        remember { Animatable(initialValue = 0f) }
    val targetIndicatorIndex: Float = selectedIndex.toFloat()
    LaunchedEffect(key1 = targetIndicatorIndex) {
        indicatorIndex.animateTo(targetValue = targetIndicatorIndex, animationSpec = animSpec)
    }

    Layout(
        modifier = modifier.height(height = BottomNavHeight),
        content = {
            content()
            Box(modifier = Modifier.layoutId("indicator"), content = indicator)
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->
        check(itemCount == (measurables.size - 1)) // account for indicator

        // Divide the width into n+1 slots and give the selected item 2 slots
        val unselectedWidth: Int = constraints.maxWidth / (itemCount + 1)
        val selectedWidth: Int = 2 * unselectedWidth
        val indicatorMeasurable: Measurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables: List<Placeable> = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index: Int, measurable: Measurable ->
                // Animate item's width based upon the selection amount
                val width: Int =
                    lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints = constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable: Placeable = indicatorMeasurable.measure(
            constraints = constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft: Float = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable: Placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

/**
 * This Composable is used for each of the [HomeSections] tabs in our [JetsnackBottomBar]. We start
 * by initializing and remembering our [Animatable] of [Float] variable `val animationProgress` to a
 * new instance with its `targetValue` set to `1f` if our [Boolean] parameter `selected` is `true`,
 * or to `0f` if it is `false`, its `animationSpec` argument is our [AnimationSpec] parameter
 * [animSpec], and its `label` argument is the [String] "animation progress".
 *
 * Our root Composable is a [JetsnackBottomNavItemLayout] whose `icon` [BoxScope] Composable lambda
 * argument is our [Composable] parameter [icon], whose `text` [BoxScope] Composable lambda argument
 * is our [Composable] lambda parameter [text], whose `animationProgress` is our animated [Float]
 * variable `animationProgress`, and whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.selectable] whose `selected` argument is our [Boolean] parameter [selected],
 * and whose `onClick` argument is a lambda that calls our lambda parameter [onSelected].
 *
 * @param icon a [BoxScope] Composable lambda. Our caller [JetsnackBottomBar] pass us an [Icon] whose
 * [ImageVector] `imageVector` argument is the [ImageVector] drawn by the [HomeSections.icon] of the
 * [HomeSections] we represent.
 * @param text a [BoxScope] Composable lambda. Our caller [JetsnackBottomBar] pass us a [Text] whose
 * `text` argument is the [String] whose resource ID is the [HomeSections.title] of the
 * [HomeSections] we represent.
 * @param selected a [Boolean] if `true` this Composable is selected. Our caller [JetsnackBottomBar]
 * passes us `true` if the [HomeSections] we represent is the currently selected [HomeSections].
 * @param onSelected a lambda that we should call when this Composable is selected. Our caller
 * [JetsnackBottomBar] passes passes us a lambda that calls its lambda parameter `navigateToRoute`
 * with the [HomeSections.route] of the [HomeSections] we represent.
 * @param animSpec the [AnimationSpec] of [Float] used to animate all the animations in our Composable.
 * Our caller [JetsnackBottomBar] passes us a [spatialExpressiveSpring].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetsnackBottomBar] passes us a [BottomNavigationItemPadding] chained to a
 * [Modifier.clip] whose `shape` argument is [BottomNavIndicatorShape].
 */
@Composable
fun JetsnackBottomNavigationItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    // Animate the icon/text positions within the item based on selection
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = animSpec,
        label = "animation progress"
    )
    JetsnackBottomNavItemLayout(
        icon = icon,
        text = text,
        animationProgress = animationProgress,
        modifier = modifier
            .selectable(selected = selected, onClick = onSelected)
            .wrapContentSize()
    )
}

/**
 * Used by [JetsnackBottomNavigationItem] to measure and place our [BoxScope] Composable lambda
 * parameters [icon] and [text]. Our root Composable is a [Layout] whose `modifier` argument is
 * our [Modifier] parameter [modifier], and whose `content` Composable lambda argument composes:
 *
 *  - a [Box] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId` argument is the
 *  [String] "icon", with a [Modifier.padding] that adds [TextIconSpacing] padding to each horizontal
 *  side, and whose `content` argument is our [BoxScope] Composable lambda parameter [icon].
 *
 *  - We initialize our [Float] variable `val scale` to the [lerp] of `0.6f` and `1f` linearly
 *  interpolated by the `fraction` of our [Float] variable `animationProgress`. Then we compose
 *  a [Box] whose `modifier` argument is a [Modifier.layoutId] whose `layoutId` argument is the
 *  [String] "text", with a [Modifier.padding] that adds [TextIconSpacing] padding to each horizontal
 *  side, and this is followed by a [Modifier.graphicsLayer] whose `alpha` is our [Float] parameter
 *  [animationProgress], whose `scaleX` is our [Float] variable `scale`, whose `scaleY` is `scale`,
 *  and whose `transformOrigin` is our [TransformOrigin] field [BottomNavLabelTransformOrigin]. The
 *  [BoxScope] `content` Composable lambda argument is our [BoxScope] Composable lambda parameter
 *  [text].
 *
 * In the [MeasurePolicy] `measurePolicy` [MeasureScope] lambda argument we capture the [List] of
 * [Measurable] passed the lambda in our `measurables` variable and the [Constraints] passed the
 * lambda in our `constraints` variable. Then we initialize our [Placeable] variable `val iconPlaceable`
 * to the [Measurable.measure] of the first [Measurable] in `measurables` whose [Measurable.layoutId]
 * is the [String] "icon", with the `constraints` argument our [Constraints] variable `constraints`.
 * We initialize our [Placeable] variable `val textPlaceable` to the [Measurable.measure] of the
 * first [Measurable] in `measurables` whose [Measurable.layoutId] is the [String] "text", with the
 * `constraints` argument our [Constraints] variable `constraints`. Then we call our
 * [MeasureScope.placeTextAndIcon] extension function with its `textPlaceable` argument set to our
 * [Placeable] variable `textPlaceable`, its `iconPlaceable` argument set to our [Placeable] variable
 * `iconPlaceable`, with its `width` argument set to the [Constraints.maxWidth] of our [Constraints]
 * variable `constraints`, and its `height` argument set to the [Constraints.maxHeight] of our
 * [Constraints] variable `constraints`, and its `animationProgress` argument set to our animated
 * [Float] parameter [animationProgress].
 *
 * @param icon a [BoxScope] Composable lambda. Our caller [JetsnackBottomNavigationItem] passes us
 * its [BoxScope] Composable lambda parameter `icon`, which traces back to an [Icon] whose
 * [ImageVector] `imageVector` argument is the [ImageVector] drawn by the [HomeSections.icon] of the
 * [HomeSections] it represents.
 * @param text a [BoxScope] Composable lambda. Our caller [JetsnackBottomNavigationItem] passes us
 * its [BoxScope] Composable lambda parameter `text`, which traces back to a [Text] whose `text`
 * argument is the [String] whose resource ID is the [HomeSections.title] of the [HomeSections] it
 * represents.
 * @param animationProgress an animated [Float] between `0f` and `1f` that represents the progress
 * of the animation of the selected [JetsnackBottomNavigationItem]. Our caller
 * [JetsnackBottomNavigationItem] passes us an [animateFloatAsState] whose `targetValue` is `1f` if
 * its [Boolean] parameter `selected` is `true` or `0f` if it is `false`.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetsnackBottomNavigationItem] passes us a [Modifier.selectable] whose
 * `selected` argument is `true` if the [JetsnackBottomNavigationItem] is selected, and whose
 * `onClick` argument is a lambda that calls its lambda parameter `onSelected`, with a
 * [Modifier.wrapContentSize] chained to that [Modifier] that allows us to measure at our desired
 * size without regard to the minimum size of the incoming constraints.
 */
@Composable
private fun JetsnackBottomNavItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .layoutId(layoutId = "icon")
                    .padding(horizontal = TextIconSpacing),
                content = icon
            )
            val scale: Float = lerp(start = 0.6f, stop = 1f, fraction = animationProgress)
            Box(
                modifier = Modifier
                    .layoutId(layoutId = "text")
                    .padding(horizontal = TextIconSpacing)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = BottomNavLabelTransformOrigin
                    },
                content = text
            )
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->
        val iconPlaceable: Placeable =
            measurables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable: Placeable =
            measurables.first { it.layoutId == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable = textPlaceable,
            iconPlaceable = iconPlaceable,
            width = constraints.maxWidth,
            height = constraints.maxHeight,
            animationProgress = animationProgress
        )
    }
}

/**
 * This is used by [JetsnackBottomNavItemLayout] to place its [Placeable] parameter [textPlaceable]
 * and [iconPlaceable]. We start by initializing our [Int] variable `val iconY` to our [Int] parameter
 * [height] minus the [Placeable.height] of our [Placeable] parameter [iconPlaceable] all divided by
 * `2`, and initializing our [Int] variable `val textY` to our [Int] parameter [height] minus the
 * [Placeable.height] of our [Placeable] parameter [textPlaceable] all divided by `2`. We then
 * initialize our [Int] variable `val textWidth` to the [Placeable.width] of [textPlaceable] times
 * our animated [Float] parameter [animationProgress]. We initialize our [Float] variable `val iconX`
 * to our [Int] parameter [width] minus our [Float] variable `textWidth` minus the [Placeable.width]
 * of [Placeable] parameter [iconPlaceable] all divided by `2`, and we initialize our [Float] variable
 * `val textX` to `iconX` plus the [Placeable.width] of [iconPlaceable].
 *
 * Finally we return the [MeasureResult] that is returned by a call to [MeasureScope.layout] with its
 * `width` argument our [Int] parameter [width], its `height` argument our [Int] parameter [height],
 * and its [Placeable.PlacementScope] `placementBlock` lambda parameter a lambda in which we call
 * the [Placeable.PlacementScope.placeRelative] extension method of the [Placeable] parameter
 * [iconPlaceable] to place it at `x` coordinate `iconX` (converted to [Int]) and `y` coordinate
 * `iconY`. Then if our animated [Float] parameter [animationProgress] is not `0f` we also call
 * [Placeable.PlacementScope.placeRelative] extension method of the [Placeable] parameter
 * [textPlaceable] to place it at `x` coordinate `textX` (converted to [Int]) and `y` coordinate
 * `textY`.
 *
 * @param textPlaceable the [Placeable] created from the [Measurable] of the [Text] displaying the
 * [HomeSections.title] of the [HomeSections] we represent.
 * @param iconPlaceable the [Placeable] created from the [Measurable] of the [Icon] displaying the
 * [HomeSections.icon] of the [HomeSections] we represent.
 * @param width the [Constraints.maxWidth] of the incoming [Constraints].
 * @param height the [Constraints.maxHeight] of the incoming [Constraints].
 * @param animationProgress an animated [Float] between `0f` and `1f` that represents the progress
 * of the animation of the selected [JetsnackBottomNavigationItem].
 */
private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY: Int = (height - iconPlaceable.height) / 2
    val textY: Int = (height - textPlaceable.height) / 2

    val textWidth: Float = textPlaceable.width * animationProgress
    val iconX: Float = (width - textWidth - iconPlaceable.width) / 2
    val textX: Float = iconX + iconPlaceable.width

    return layout(width = width, height = height) {
        iconPlaceable.placeRelative(x = iconX.toInt(), y = iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(x = textX.toInt(), y = textY)
        }
    }
}

/**
 * This is used to indicate which [JetsnackBottomNavigationItem] is currently selected. Our root
 * Composable is just a [Spacer] whose [Modifier] is a [Modifier.fillMaxSize] to have it take up
 * the entire incoming size constraints, with a [BottomNavigationItemPadding] chained to that (it is
 * a [Modifier.padding] that adds `16.dp` to its horizontal sides and `8.dp` to its vertical sides),
 * and this is followed by a [Modifier.border] whose `width` argument is our [Dp] parameter
 * `strokeWidth`, whose `color` argument is our [Color] parameter `color`, and whose [Shape] argument
 * is our [Shape] parameter [shape] (which is [BottomNavIndicatorShape] by default).
 *
 * @param strokeWidth the [Dp] of the stroke width of the [Modifier.border] that is drawn around our
 * [Spacer]. Our caller [JetsnackBottomBar] does not pass us any so the default of `2.dp` is used.
 * @param color the [Color] of the [Modifier.border] that is drawn around our [Spacer]. Our caller
 * does not pass us any so the default of [JetsnackColors.iconInteractive] of our custom
 * [JetsnackTheme.colors] is used.
 * @param shape the [Shape] of the [Modifier.border] that is drawn around our [Spacer]. Our caller
 * does not pass us any so the default of [BottomNavIndicatorShape] is used (a [RoundedCornerShape]
 * with a `percent` rounding of `50%`).
 */
@Composable
private fun JetsnackBottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = JetsnackTheme.colors.iconInteractive,
    shape: Shape = BottomNavIndicatorShape
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(other = BottomNavigationItemPadding)
            .border(width = strokeWidth, color = color, shape = shape)
    )
}

/**
 * The padding used on each horizonal side of the [Box]'s holding the [Icon] and [Text] composables
 * used by [JetsnackBottomNavItemLayout].
 */
private val TextIconSpacing = 2.dp

/**
 * The height of the [Layout] used by [JetsnackBottomNavLayout] and thus of [JetsnackBottomBar].
 */
private val BottomNavHeight = 56.dp

/**
 * Offset percentage along the `x` and `y` axis for which contents are rotated and scaled by the
 * [Modifier.graphicsLayer] used to animate the [Text] in the [JetsnackBottomNavItemLayout].
 */
private val BottomNavLabelTransformOrigin = TransformOrigin(
    pivotFractionX = 0f,
    pivotFractionY = 0.5f
)

/**
 * [Shape] used to draw the border around the [JetsnackBottomNavIndicator], and to clip the
 * [JetsnackBottomNavigationItem]
 */
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)

/**
 * The [Modifier.padding] used for the [JetsnackBottomNavigationItem] and for the
 * [JetsnackBottomNavIndicator].
 */
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

/**
 * Preview of our [JetsnackBottomBar].
 */
@Preview
@Composable
private fun JetsnackBottomNavPreview() {
    JetsnackTheme {
        JetsnackBottomBar(
            tabs = HomeSections.entries.toTypedArray(),
            currentRoute = "home/feed",
            navigateToRoute = { }
        )
    }
}
