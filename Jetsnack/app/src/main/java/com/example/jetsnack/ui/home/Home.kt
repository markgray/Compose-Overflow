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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
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
import androidx.navigation.compose.composable
import com.example.jetsnack.R
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.home.search.Search
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.JetsnackTheme
import java.util.Locale

/**
 * This [NavGraphBuilder] extension function adds a Composable [composable] to the [NavGraphBuilder]
 * that is wrapped in a [CompositionLocalProvider] with the key [LocalNavAnimatedVisibilityScope]
 * providing the current [AnimatedVisibilityScope].
 *
 * @param route The destination route for the Composable created by the [composable] method.
 * @param arguments A [List] of [NamedNavArgument]s to be passed to the [composable] method to be
 * associated with the destination.
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
 *
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
 *
 */
enum class HomeSections(
    /**
     *
     */
    @StringRes val title: Int,
    /**
     *
     */
    val icon: ImageVector,
    /**
     *
     */
    val route: String
) {
    /**
     *
     */
    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),

    /**
     *
     */
    SEARCH(R.string.home_search, Icons.Outlined.Search, "home/search"),

    /**
     *
     */
    CART(R.string.home_cart, Icons.Outlined.ShoppingCart, "home/cart"),

    /**
     *
     */
    PROFILE(R.string.home_profile, Icons.Outlined.AccountCircle, "home/profile")
}

/**
 *
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
                    if (selected) {
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
 *
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
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
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
 *
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
 * Bottom navigation item view
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
    ) { measurables, constraints ->
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
 *
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
 *
 */
private val TextIconSpacing = 2.dp

/**
 *
 */
private val BottomNavHeight = 56.dp

/**
 *
 */
private val BottomNavLabelTransformOrigin = TransformOrigin(
    pivotFractionX = 0f,
    pivotFractionY = 0.5f
)

/**
 *
 */
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)

/**
 *
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
