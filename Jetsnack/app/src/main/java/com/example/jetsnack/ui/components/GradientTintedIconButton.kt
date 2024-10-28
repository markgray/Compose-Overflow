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
 * "-" and "+" symbols.
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
    val interactionSource = remember { MutableInteractionSource() }

    // This should use a layer + srcIn but needs investigation
    val border = Modifier.fadeInDiagonalGradientBorder(
        showBorder = true,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = CircleShape
    )
    val pressed by interactionSource.collectIsPressedAsState()
    val background = if (pressed) {
        Modifier.offsetGradientBackground(colors = colors, width = 200f, offset = 0f)
    } else {
        Modifier.background(JetsnackTheme.colors.uiBackground)
    }
    val blendMode = if (JetsnackTheme.colors.isDark) BlendMode.Darken else BlendMode.Plus
    val modifierColor = if (pressed) {
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
