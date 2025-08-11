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

@file:Suppress("UnusedImport")

package com.example.jetsnack.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.jetsnack.model.Snack
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * This button is used in three places: as the "Checkout" button in the `CheckoutBar` of the [Cart]
 * screen, as the "+" "Add to cart" button on the result [Snack] of a search operation and as the
 * "ADD TO CART" button on the [Snack] detail screen. Our root Composable is a [JetsnackSurface]
 * whose `shape` argument is our [Shape] parameter [shape], whose [Color] `color` argument is
 * [Color.Transparent], whose `contentColor` argument is our [Color] parameter [contentColor] if
 * our [enabled] parameter is `true` or our [Color] parameter [disabledContentColor] if it is `false`,
 * whose `border`  argument is our [BorderStroke] parameter [border] (always `null`), whose `modifier`
 * argument chains to our [Modifier] parameter [modifier] a [Modifier.clip] that uses our [Shape]
 * parameter [shape] as its `shape` argument followed by a [Modifier.background] whose `brush` is
 * a [Brush.horizontalGradient] that uses for its `colors` argument our [backgroundGradient] parameter
 * if our [enabled] parameter is `true` or our [disabledBackgroundGradient] if it is `false`, followed
 * by a [Modifier.clickable] whose `onClick` argument is our lambda parameter [onClick], whose `enabled`
 * argument is our [Boolean] parameter [enabled], whose `role` argument is [Role.Button], whose
 * `interactionSource` argument is our [MutableInteractionSource] parameter [interactionSource], and
 * whose `indication` argument is `null`. The `content` Composable lambda argument of the
 * [JetsnackSurface] uses [ProvideTextStyle] to produce a [CompositionLocalProvider] that provides
 * (using the key [LocalTextStyle]) as [TextStyle] the [Typography.labelLarge] of the
 * [MaterialTheme.typography] of our [JetsnackTheme] custom [MaterialTheme] (which is Montserrat`
 * `fontFamily`, `fontSize` = 14.sp, `fontWeight` = [FontWeight.SemiBold], `lineHeight` = 16.sp, and
 * `letterSpacing` = 1.25.sp). This wraps a [Row] whose `modifier` argument is a [Modifier.defaultMinSize]
 * with `minWidth` argument [ButtonDefaults.MinWidth] and `minHeight` argument [ButtonDefaults.MinHeight],
 * and chained to that is a [Modifier.indication] whose `interactionSource` argument is our
 * [MutableInteractionSource] parameter [interactionSource], and chained to that is a [Modifier.padding]
 * whose `paddingValues` argument is our [PaddingValues] parameter [contentPadding]. The
 * `horizontalArrangement` argument of the [Row] is [Arrangement.Center], the `verticalAlignment`
 * argument is [Alignment.CenterVertically] and the `content` argument is our Composable lambda
 * parameter [content].
 *
 * @param onClick a lambda to call when the [JetsnackButton] is clicked. All three uses are no-ops
 * at the moment.
 * @param modifier a [Modifier] instance that the use can use to modify our appearance and/or
 * behavior. The "Checkout" button uses a [Modifier.padding] that adds `12.dp` padding to each
 * `horizontal` side, and `8.dp` to each vertical side with a [RowScope.weight] of `1f` to have it
 * take up all space remaining after its siblings are measured and place, the "ADD TO CART" button
 * uses a [RowScope.weight] of `1f` to have it take up all the space left after its siblings have
 * been measured and placed, and the "+" button uses a [Modifier.size] of 36.dp with a
 * [ConstraintLayout] modifier specifying its positioning.
 * @param enabled determines whether the button is clickable, and changes the colors of the button
 * to indicate whether it is enabled or not. Our callers never override the default value `true`.
 * @param interactionSource is used as the `interactionSource` argument of a [Modifier.indication]
 * and a [Modifier.clickable] that are applied to the Composables making up the [JetsnackButton].
 * Our callers never override the default which is a remembered [MutableInteractionSource].
 * @param shape the [Shape] that the [JetsnackButton] is clipped to. We are called with a
 * [RectangleShape] by `CheckoutBar` and a [CircleShape] by `SearchResult`, with the "ADD TO CART"
 * button defaulting to our [ButtonShape].
 * @param border this is the `border` argument of a [Modifier.border] that is used if it is not the
 * default of `null`. Our callers do not override the default so no border is drawn.
 * @param backgroundGradient this is used as the `colors` of a [Brush.horizontalGradient] that
 * are used as the `brush` argument of a [Modifier.background] if our [Boolean] parameter [enabled]
 * is `true`. Our callers do not override the default so the [List] of [Color] in the
 * [JetsnackColors.interactivePrimary] of our [JetsnackTheme.colors] is used.
 * @param disabledBackgroundGradient this is used as the `colors` of a [Brush.horizontalGradient]
 * that are used as the `brush` argument of a [Modifier.background] if our [Boolean] parameter
 * [enabled] is `false`. Our callers do not override the default so the [List] of [Color] in the
 * [JetsnackColors.interactiveSecondary] of our [JetsnackTheme.colors] is used.
 * @param contentColor this is used as the `contentColor` argument of our [JetsnackSurface] Composable
 * if our [Boolean] parameter [enabled] is `true`. Our callers do not override the default so the
 * [Color] in the [JetsnackColors.textInteractive] of our [JetsnackTheme.colors] is used.
 * @param disabledContentColor this is used as the `contentColor` argument od our [JetsnackSurface]
 * Composable if our [Boolean] parameter [enabled] is `false`. Our callers do not override the
 * default so the [Color] in the [JetsnackColors.textHelp] of our [JetsnackTheme.colors] is used.
 * @param contentPadding this is used as the `paddingValues` argument of the [Modifier.padding]
 * of the [Row] holding our `content` Composable lambda parameter. Our callers do not override the
 * default so [ButtonDefaults.ContentPadding] is used.
 * @param content a Composable lambda that will be used as the `content` argument of our [Row].
 */
@Composable
fun JetsnackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonShape,
    border: BorderStroke? = null,
    backgroundGradient: List<Color> = JetsnackTheme.colors.interactivePrimary,
    disabledBackgroundGradient: List<Color> = JetsnackTheme.colors.interactiveSecondary,
    contentColor: Color = JetsnackTheme.colors.textInteractive,
    disabledContentColor: Color = JetsnackTheme.colors.textHelp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    JetsnackSurface(
        shape = shape,
        color = Color.Transparent,
        contentColor = if (enabled) contentColor else disabledContentColor,
        border = border,
        modifier = modifier
            .clip(shape = shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) backgroundGradient else disabledBackgroundGradient
                )
            )
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelLarge
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = ButtonDefaults.MinWidth,
                        minHeight = ButtonDefaults.MinHeight
                    )
                    .indication(interactionSource = interactionSource, indication = ripple())
                    .padding(paddingValues = contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

/**
 * The default [Shape] of our [JetsnackButton] is a [RoundedCornerShape] whose `percent` argument
 * is 50.
 */
private val ButtonShape: RoundedCornerShape = RoundedCornerShape(percent = 50)

/**
 * Three different Previews of a round [JetsnackButton]
 */
@Preview("default", "round")
@Preview("dark theme", "round", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "round", fontScale = 2f)
@Composable
private fun ButtonPreview() {
    JetsnackTheme {
        JetsnackButton(onClick = {}) {
            Text(text = "Demo")
        }
    }
}

/**
 * Three different Previews of a rectangle shaped [JetsnackButton]
 */
@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
private fun RectangleButtonPreview() {
    JetsnackTheme {
        JetsnackButton(
            onClick = {}, shape = RectangleShape
        ) {
            Text(text = "Demo")
        }
    }
}
