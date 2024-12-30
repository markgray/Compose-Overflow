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

@file:Suppress("RedundantValueArgument")

package com.example.jetsnack.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * This Composable displays its [ImageVector] parameter [imageVector] in an [Icon] which is clipped
 * by a [CircleShape] and whose background, border and tint use a gradient created from its [List]
 * of [Color] parameter [colors]. It is used by the [QuantitySelector] Composable to draw its
 * "-" and "+" symbols. We start by initializing and remembering our [MutableInteractionSource]
 * variable `val interactionSource` to a new instance. We initialize our [Modifier] variable
 * `val border` to a [Modifier.fadeInDiagonalGradientBorder] whose `showBorder` argument is `true`,
 * whose `colors` argument is the [JetsnackColors.interactiveSecondary] of our custom
 * [JetsnackTheme.colors], and whose `shape` argument is [CircleShape]. We initialize our [State]
 * wrapped [Boolean] variable `val pressed` to the value collected by the
 * [MutableInteractionSource.collectIsPressedAsState] method of our [MutableInteractionSource]
 * variable `interactionSource`. If `pressed` is `true` we initialize our [Modifier] variable
 * `val background` to a [Modifier.offsetGradientBackground] whose `colors` argument is our
 * [List] of [Color] parameter [colors], whose `width` is 200f, and whose `offset` is 0f, or if
 * `pressed` is `false` to a  [Modifier.background] whose `color` is the [JetsnackColors.uiBackground]
 * of our custom [JetsnackTheme.colors]. If the [JetsnackColors.isDark] method of our custom
 * [JetsnackTheme.colors] returns `true` we initialize our [BlendMode] variable `val blendMode`
 * to [BlendMode.Darken] (Composites the source and destination image by choosing the lowest value
 * from each color channel. The opacity of the output image is computed in the same way as for SrcOver),
 * and if it is `false` we initialize it to [BlendMode.Plus] (Sums the components of the source and
 * destination images). If `pressed` is `true` we initialize our [Modifier] variable
 * `val modifierColor` to a [Modifier.diagonalGradientTint] whose `colors` is a [List] of [Color]
 * composed of two copies of the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors],
 * and its `blendMode` argument is our [BlendMode] variable `blendMode`, and if `pressed` is `false`
 * we initialize it to a [Modifier.diagonalGradientTint] whose `colors` argument is our [List] of
 * [Color] parameter [colors], and whose `blendMode` argument is our [BlendMode] variable `blendMode`.
 *
 * Our root Composable is a [Surface] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.clickable] whose `onClick` argument is our lambda parameter [onClick],
 * whose `interactionSource` is our [MutableInteractionSource] variable `interactionSource` and
 * whose `indication` argument is `null`, followed by a [Modifier.clip] whose `shape` argument if
 * [CircleShape], followed by our [Modifier] variable `border`, and followed by our [Modifier]
 * variable `background`. The `color` argument of the [Surface] is [Color.Transparent].
 *
 * The `content` lambda argument of the [Surface] is an [Icon] whose `imageVector` argument is our
 * [ImageVector] parameter [imageVector], whose `contentDescription` argument is our [String]
 * parameter [contentDescription], and whose `modifier` argument is our [Modifier] variable
 * `modifierColor`.
 *
 * @param imageVector the [ImageVector] we should draw in our [Icon]. One of our calls passes us the
 * [androidx.compose.material.icons.filled.Remove] ("-" symbol) and the other passes us the
 * [androidx.compose.material.icons.filled.Add] ("+" symbol).
 * @param onClick the lambda we should call when our [Surface] is clicked.
 * @param contentDescription the [String] that the accessibility services should use to describe
 * what our [Icon] represents. "Decrease" for the "-" icon and "Increase" for the "+" icon.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Both our callers pass us a [RowScope.align] whose `alignment` argument is
 * [Alignment.CenterVertically] to have use align our [Surface] centered vertically within the [Row]
 * we are composed in.
 * @param colors the [List] of [Color] that we should use for the [Modifier.offsetGradientBackground]
 * used as the background of our [Surface] and for the [Modifier.diagonalGradientTint] of our [Icon]
 * when the [Icon] is not pressed. Our callers do not pass us one so our default [List] of [Color],
 * the [JetsnackColors.interactiveSecondary] of our [JetsnackTheme.colors], is used.
 */
@Composable
fun JetsnackGradientTintedIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    colors: List<Color> = JetsnackTheme.colors.interactiveSecondary
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    // This should use a layer + srcIn but needs investigation
    val border: Modifier = Modifier.fadeInDiagonalGradientBorder(
        showBorder = true,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = CircleShape
    )
    val pressed: Boolean by interactionSource.collectIsPressedAsState()
    val background: Modifier = if (pressed) {
        Modifier.offsetGradientBackground(colors = colors, width = 200f, offset = 0f)
    } else {
        Modifier.background(color = JetsnackTheme.colors.uiBackground)
    }
    val blendMode: BlendMode = if (JetsnackTheme.colors.isDark) BlendMode.Darken else BlendMode.Plus
    val modifierColor: Modifier = if (pressed) {
        Modifier.diagonalGradientTint(
            colors = listOf(
                JetsnackTheme.colors.textSecondary,
                JetsnackTheme.colors.textSecondary
            ),
            blendMode = blendMode
        )
    } else {
        Modifier.diagonalGradientTint(
            colors = colors,
            blendMode = blendMode
        )
    }
    Surface(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .clip(shape = CircleShape)
            .then(other = border)
            .then(other = background),
        color = Color.Transparent
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifierColor
        )
    }
}

/**
 * Two Previews of our [JetsnackGradientTintedIconButton], one with the default `uiMode` and the
 * other with [Configuration.UI_MODE_NIGHT_YES].
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GradientTintedIconButtonPreview() {
    JetsnackTheme {
        JetsnackGradientTintedIconButton(
            imageVector = Icons.Default.Add,
            onClick = {},
            contentDescription = "Demo",
            modifier = Modifier.padding(all = 4.dp)
        )
    }
}
