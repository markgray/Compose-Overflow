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

package com.example.jetsnack.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.JetsnackColors
import kotlin.math.ln

/**
 * An alternative to [androidx.compose.material3.Surface] utilizing [JetsnackColors]. Our root
 * Composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter [modifier] a
 * [Modifier.shadow] whose `elevation` argument is our [Dp] parameter [elevation], whose `shape` is
 * our [Shape] parameter [shape], and whose `clip` argument is `false`. To that is chained a
 * [Modifier.zIndex] whose `zIndex` argument is the [Dp.value] of our [Dp] parameter [elevation].
 * Then if our [BorderStroke] parameter is not `null` we chain a [Modifier.border] whose `border`
 * argument is our [BorderStroke] parameter [border], and whose `shape` argument is our [Shape]
 * parameter [shape], and if it is `null` we chain an empty [Modifier]. Then we chain a
 * [Modifier.background] whose `color` argument is the [Color] returned by our method
 * [getBackgroundColorForElevation] when called with its `color` argument our [Color] parameter
 * [Color] and its `elevation` argument our [Dp] parameter [elevation], and whose `shape` argument
 * is our [Shape] parameter [shape]. Finally we chain a [Modifier.clip] whose `shape` argument is
 * our [Shape] parameter [shape]. In the [BoxScope] `content` Composable lambda argument of the [Box]
 * we wrap our [content] Composable lambda in a [CompositionLocalProvider] that provides our [Color]
 * parameter [contentColor] as the [LocalContentColor] of our Composable lambda parameter [content].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior.
 * @param shape the [Shape] we should use, default is [RectangleShape].
 * @param color the [Color] we should use for our background, default is the [JetsnackColors.uiBackground]
 * of our custom [JetsnackTheme.colors].
 * @param contentColor the [Color] we should use for the [LocalContentColor] that [CompositionLocalProvider]
 * provides to our [content] Composable lambda parameter, default is the [JetsnackColors.textSecondary]
 * of our custom [JetsnackTheme.colors].
 * @param border the [BorderStroke] we should use in the [Modifier.border] we apply to our root [Box]
 * Composable (if it is not `null`), default is `null`.
 * @param elevation the [Dp] value we use as the elevation of our root [Box] Composable, default is 0.dp
 * @param content the Composable lambda that we wrap in a [CompositionLocalProvider] that provides
 * our [Color] parameter [contentColor] as the [LocalContentColor].
 */
@Composable
fun JetsnackSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textSecondary,
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, clip = false)
            .zIndex(zIndex = elevation.value)
            .then(if (border != null) Modifier.border(border = border, shape = shape) else Modifier)
            .background(
                color = getBackgroundColorForElevation(color = color, elevation = elevation),
                shape = shape
            )
            .clip(shape = shape)
    ) {
        CompositionLocalProvider(value = LocalContentColor provides contentColor, content = content)
    }
}

/**
 * Convenience function that Calculates and returns the [Color.withElevation] of our [Color] parameter
 * [color] for our [Dp] parameter [elevation] if [elevation] is greater than 0.dp or returns our
 * [Color] parameter [color] if it is less than or equal to 0.dp.
 *
 * @param color the [Color] whose [Color.withElevation] we should calculate and return.
 * @param elevation the elevation of our [Color] parameter [color]
 * @return the [Color.withElevation] of our [Color] parameter [color] for our [Dp] parameter
 * [elevation] if [elevation] is greater than 0.dp, or [Color] parameter [color] if it is less than
 * or equal to 0.dp.
 */
@Composable
private fun getBackgroundColorForElevation(color: Color, elevation: Dp): Color {
    return if (elevation > 0.dp // && https://issuetracker.google.com/issues/161429530
        // JetsnackTheme.colors.isDark //&&
        // color == JetsnackTheme.colors.uiBackground
    ) {
        color.withElevation(elevation = elevation)
    } else {
        color
    }
}

/**
 * Applies a [Color.White] overlay to this color based on the [elevation]. This increases visibility
 * of elevation for surfaces in a dark theme. We initialize our [Color] variable `val foreground` to
 * the alpha-modified [Color.White] that our [calculateForeground] method returns when its `elevation`
 * argument is our [Dp] parametr [elevation]. Then we return the [Color] that the [Color.compositeOver]
 * method of `foreground` returns when passed our receiver [Color] as its `background` argument.
 *
 * @param elevation the [Dp] value of the elevation.
 * @return the [Color.White] overlay to this color based on the [elevation].
 *
 * TODO: Remove when public https://issuetracker.google.com/155181601
 */
private fun Color.withElevation(elevation: Dp): Color {
    val foreground: Color = calculateForeground(elevation = elevation)
    return foreground.compositeOver(background = this)
}

/**
 * Calculate an alpha-modified [Color.White] that can be overlaid on top of a surface color to produce
 * a better elevation appearance when the [Color] is used in a dark theme. We calculate our [Float]
 * variable `val alpha` to be proportional to the natural log of the [Dp.value] of our [Dp] parameter
 * [elevation] plus 1 then return a copy of [Color.White] with its `alpha` set to that `alpha` value.
 *
 * @param elevation the [Dp] value of the elevation.
 * @return the alpha-modified [Color.White] to overlay on top of the surface color to produce
 * the resultant color.
 */
private fun calculateForeground(elevation: Dp): Color {
    val alpha: Float = ((4.5f * ln(x = elevation.value + 1)) + 2f) / 100f
    return Color.White.copy(alpha = alpha)
}
