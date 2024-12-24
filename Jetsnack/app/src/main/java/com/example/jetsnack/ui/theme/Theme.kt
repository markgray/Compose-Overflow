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

@file:Suppress("UNUSED_PARAMETER", "PropertyName")

package com.example.jetsnack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.jetsnack.model.Snack
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.FilterChip
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackCard
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackGradientTintedIconButton
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.home.FilterScreen
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.home.JetsnackBottomNavigationItem
import com.example.jetsnack.ui.home.MaxCalories
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.home.cart.CartItem
import com.example.jetsnack.ui.home.cart.SwipeDismissItem
import com.example.jetsnack.ui.home.search.Search
import com.example.jetsnack.ui.home.search.SearchCategories
import com.example.jetsnack.ui.snackdetail.SnackDetail

private val LightColorPalette = JetsnackColors(
    brand = Shadow5,
    brandSecondary = Ocean3,
    uiBackground = Neutral0,
    uiBorder = Neutral4,
    uiFloated = FunctionalGrey,
    textSecondary = Neutral7,
    textHelp = Neutral6,
    textInteractive = Neutral0,
    textLink = Ocean11,
    iconSecondary = Neutral7,
    iconInteractive = Neutral0,
    iconInteractiveInactive = Neutral1,
    error = FunctionalRed,
    gradient6_1 = listOf(Shadow4, Ocean3, Shadow2, Ocean3, Shadow4),
    gradient6_2 = listOf(Rose4, Lavender3, Rose2, Lavender3, Rose4),
    gradient3_1 = listOf(Shadow2, Ocean3, Shadow4),
    gradient3_2 = listOf(Rose2, Lavender3, Rose4),
    gradient2_1 = listOf(Shadow4, Shadow11),
    gradient2_2 = listOf(Ocean3, Shadow3),
    gradient2_3 = listOf(Lavender3, Rose2),
    tornado1 = listOf(Shadow4, Ocean3),
    isDark = false
)

private val DarkColorPalette = JetsnackColors(
    brand = Shadow1,
    brandSecondary = Ocean2,
    uiBackground = Neutral8,
    uiBorder = Neutral3,
    uiFloated = FunctionalDarkGrey,
    textPrimary = Shadow1,
    textSecondary = Neutral0,
    textHelp = Neutral1,
    textInteractive = Neutral7,
    textLink = Ocean2,
    iconPrimary = Shadow1,
    iconSecondary = Neutral0,
    iconInteractive = Neutral7,
    iconInteractiveInactive = Neutral6,
    error = FunctionalRedDark,
    gradient6_1 = listOf(Shadow5, Ocean7, Shadow9, Ocean7, Shadow5),
    gradient6_2 = listOf(Rose11, Lavender7, Rose8, Lavender7, Rose11),
    gradient3_1 = listOf(Shadow9, Ocean7, Shadow5),
    gradient3_2 = listOf(Rose8, Lavender7, Rose11),
    gradient2_1 = listOf(Ocean3, Shadow3),
    gradient2_2 = listOf(Ocean4, Shadow2),
    gradient2_3 = listOf(Lavender3, Rose3),
    tornado1 = listOf(Shadow4, Ocean3),
    isDark = true
)

/**
 * This is our custom [MaterialTheme]. The [MaterialTheme] construtctor is wrapped in our
 * [ProvideJetsnackColors] Composable function to provide the [JetsnackColors] from the
 * [LocalJetsnackColors] provides `colors` [CompositionLocalProvider]
 *
 * @param darkTheme Whether we are in dark theme or not.
 * @param content The Composable lambda that we are to provide [MaterialTheme] values to.
 */
@Composable
fun JetsnackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    ProvideJetsnackColors(colors = colors) {
        MaterialTheme(
            colorScheme = debugColors(darkTheme),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * This singleton object is used to retrieve the `current` [LocalJetsnackColors]
 */
object JetsnackTheme {
    /**
     * Used to retrieve the `current` [LocalJetsnackColors]
     */
    val colors: JetsnackColors
        @Composable
        get() = LocalJetsnackColors.current
}

/**
 * Jetsnack custom Color Palette
 */
@Immutable
data class JetsnackColors(
    /**
     * Used as the `gradient` of even numbered [Snack] items in the `HighlightedSnacks` composable
     * used by [SnackCollection]
     */
    val gradient6_1: List<Color>,
    /**
     * Used as the `gradient` of odd numbered [Snack] items in the `HighlightedSnacks` composable
     * used by [SnackCollection]
     */
    val gradient6_2: List<Color>,
    /**
     * Unused
     */
    val gradient3_1: List<Color>,
    /**
     * Used only in the `Preview` of the `SearchCategory` composable as the `gradient` argument.
     */
    val gradient3_2: List<Color>,
    /**
     * Used as the [JetsnackColors.interactivePrimary]
     */
    val gradient2_1: List<Color>,
    /**
     * Used for even numbered `SearchCategory` items in the [SearchCategories] composable, and as
     * the [interactiveSecondary].
     */
    val gradient2_2: List<Color>,
    /**
     * Used for odd numbered `SearchCategory` items in the [SearchCategories] composable.
     */
    val gradient2_3: List<Color>,
    /**
     * Used as the app `brand` color in lots of places. It is [Shadow1] for the `DarkColorPalette`
     * and [Shadow5] for the `LightColorPalette`.
     */
    val brand: Color,
    /**
     * It is used as the `targetValue` of the animated background color of `selected` [FilterChip]s
     * in the [FilterBar] composable.
     */
    val brandSecondary: Color,
    /**
     * Used as the background color in many places.
     */
    val uiBackground: Color,
    /**
     * A copy of [uiBorder] is used by [JetsnackDivider] as the `color` of its [HorizontalDivider]
     * and also as the `border` of the [JetsnackCard] composable in `HighlightSnackItem` of [Snack]
     */
    val uiBorder: Color,
    /**
     * This is used as the `background` of the [FilterScreen] composable, and as the `background` of
     * the [JetsnackSurface] in the `SearchBar` composable of [Search].
     */
    val uiFloated: Color,
    /**
     * This is used as the `backgroundGradient` of [JetsnackButton]
     */
    val interactivePrimary: List<Color> = gradient2_1,
    /**
     * This is used as the `disabledBackgroundGradient` of [JetsnackButton] and in several other places.
     */
    val interactiveSecondary: List<Color> = gradient2_2,
    /**
     * Unused
     */
    val interactiveMask: List<Color> = gradient6_1,
    /**
     * This is used as the `color` of the `text` of [Text] in several places.
     */
    val textPrimary: Color = brand,
    /**
     * This is used as the `color` of the `text` of [Text] in several places.
     */
    val textSecondary: Color,
    /**
     * This is used as the `color` of the `text` of [Text] in several places.
     */
    val textHelp: Color,
    /**
     * This is used as the `contentColor` of [JetsnackButton].
     */
    val textInteractive: Color,
    /**
     * This is used as the `color` of the `text` of the [Text] used by the "SEE MORE" button in
     * the [SnackDetail] composable. It is [Ocean2] in the `DarkPallete` and [Ocean11] in the
     * `LightPalette`.
     */
    val textLink: Color,
    /**
     * This is used as the brush colors of the [Brush.linearGradient] of the `Header` composable
     * used by the [SnackDetail] composable.
     */
    val tornado1: List<Color>,
    /**
     * Used as the `color` of the [JetsnackBottomBar], as the `tint` of the [Icon] of the [IconButton]
     * in the `SearchBar` of [Search], and as the `color` of the [CircularProgressIndicator] in the
     * `SearchBar` of [Search].
     */
    val iconPrimary: Color = brand,
    /**
     * This is used as the `tint` of the "Remove item" [Icon] in the [CartItem] of the [Cart] screen.
     */
    val iconSecondary: Color,
    /**
     * This is used as the `inactiveTrackColor` in the [Slider] in the [MaxCalories] composabe of
     * the [FilterScreen] screen, the `contentColor` of the [JetsnackBottomBar] that is used as
     * the `bottomBar` argument of the [JetsnackScaffold] of the [MainContainer] screen, as the
     * `color` of the `text` in the [Text] and the `tint` of the [Icon] of the selected
     * [JetsnackBottomNavigationItem] in the [JetsnackBottomBar], and in the `tint` of the
     * [Icon] of the "Back" [IconButton] of the [SnackDetail] screen.
     */
    val iconInteractive: Color,
    /**
     * This is used as the `color` of the `text` in the [Text] and the `tint` of the [Icon] of the
     * unselected [JetsnackBottomNavigationItem] in the [JetsnackBottomBar]
     */
    val iconInteractiveInactive: Color,
    /**
     * This is used as the [notificationBadge], and as the `color` of the [Surface] behind the
     * `SwipeDismissItemBackground` used as the background behind the  "Swipe to dismiss"
     * [SwipeDismissItem]s of the [Cart] screen.
     */
    val error: Color,
    /**
     * Unused
     */
    val notificationBadge: Color = error,
    /**
     * If `true` we are using the [DarkColorPalette], if `false` we are using the [LightColorPalette].
     * It is used to choose between [BlendMode.Darken] (if `true`) and [BlendMode.Plus] (if `false`)
     * to use as the `blendMode` argument of the `.diagonalGradientTint` [Modifier] extension function
     * used for the [JetsnackGradientTintedIconButton]s of the [QuantitySelector] composable.
     */
    val isDark: Boolean
)

/**
 * This wraps its [content] in a [CompositionLocalProvider] that provides the [JetsnackColors] when
 * its [content] requests them.
 *
 * @param colors the [JetsnackColors] we should use, either [DarkColorPalette] or [LightColorPalette]
 * depending on whether the "dark theme" of "light theme" is in use.
 * @param content the Composable lambda we should provide [colors] thru the [LocalJetsnackColors]
 * custom [CompositionLocalProvider]
 */
@Composable
fun ProvideJetsnackColors(
    colors: JetsnackColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalJetsnackColors provides colors, content = content)
}

/**
 * This is to prevent use of [LocalJetsnackColors] without being wrapped by [ProvideJetsnackColors].
 */
private val LocalJetsnackColors = staticCompositionLocalOf<JetsnackColors> {
    error("No JetsnackColorPalette provided")
}

/**
 * A Material [ColorScheme] implementation which sets all colors to [debugColor] to discourage usage
 * of [MaterialTheme.colorScheme] in preference to [JetsnackTheme.colors].
 *
 * @param darkTheme if `true` the system is in "dark theme" mode.
 * @param debugColor the debug color to apply to all the [ColorScheme].
 */
fun debugColors(
    darkTheme: Boolean,
    debugColor: Color = Color.Magenta
): ColorScheme = ColorScheme(
    primary = debugColor,
    onPrimary = debugColor,
    primaryContainer = debugColor,
    onPrimaryContainer = debugColor,
    inversePrimary = debugColor,
    secondary = debugColor,
    onSecondary = debugColor,
    secondaryContainer = debugColor,
    onSecondaryContainer = debugColor,
    tertiary = debugColor,
    onTertiary = debugColor,
    tertiaryContainer = debugColor,
    onTertiaryContainer = debugColor,
    background = debugColor,
    onBackground = debugColor,
    surface = debugColor,
    onSurface = debugColor,
    surfaceVariant = debugColor,
    onSurfaceVariant = debugColor,
    surfaceTint = debugColor,
    inverseSurface = debugColor,
    inverseOnSurface = debugColor,
    error = debugColor,
    onError = debugColor,
    errorContainer = debugColor,
    onErrorContainer = debugColor,
    outline = debugColor,
    outlineVariant = debugColor,
    scrim = debugColor,
    surfaceBright = debugColor,
    surfaceDim = debugColor,
    surfaceContainer = debugColor,
    surfaceContainerHigh = debugColor,
    surfaceContainerHighest = debugColor,
    surfaceContainerLow = debugColor,
    surfaceContainerLowest = debugColor,
)
