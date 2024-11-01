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

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.jetsnack.R
import com.example.jetsnack.ui.home.cart.CartItem
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * This composable is used by the [CartItem] composable and the `CartBottomBar` composable to allow
 * the user to view and change the order count of a snack. Our root composable is a [Row] whose
 * `modifier` argument is our [Modifier] parameter [modifier]. In its [RowScope] `content` lambda
 * argument we have a:
 *  - [Text] whose `text` argument is the [String] "Qty", whose [TextStyle] `style` argument is the
 *  [Typography.titleMedium] of our custom [MaterialTheme.typography], whose `color` argument is the
 *  [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors], whose `fontWeight` argument
 *  is [FontWeight.Normal] and whose `modifier` argument is a [Modifier.padding] that adds 16dp to
 *  the `end` of the [Text], to which is chained a [RowScope.align] whose `alignment` argument is
 *  [Alignment.CenterVertically].
 *  - [JetsnackGradientTintedIconButton] whose `imageVector` argument is the [ImageVector] drawn by
 *  [Icons.Filled.Remove] (a "-" sign), whose `onClick` argument is our labmda parameter
 *  [decreaseItemCount], whose `contentDescription` argument is the string "Decrease", and whose
 *  `modifier` argument is a [RowScope.align] whose `alignment` argument is [Alignment.CenterVertically].
 *
 *
 * @param count the current count of the snack.
 * @param decreaseItemCount a lambda we should call when the user wishes to decrease the snack count.
 * (Called when the "-" [JetsnackGradientTintedIconButton] is clicked).
 * @param increaseItemCount a lambda we should call when the user wishes to decrease the snack count.
 * (Called when the "+" [JetsnackGradientTintedIconButton] is clicked).
 * @param modifier a [Modifier] instance our caller can use to modify our appearance and/or behavior.
 * [CartItem] calls us with a [Modifier] that positions us inside its [ConstraintLayout], and
 * `CartBottomBar` does not call us with any so the empty, default, or starter [Modifier] that contains
 * no elements is used.
 */
@Composable
fun QuantitySelector(
    count: Int,
    decreaseItemCount: () -> Unit,
    increaseItemCount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.quantity),
            style = MaterialTheme.typography.titleMedium,
            color = JetsnackTheme.colors.textSecondary,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(end = 18.dp)
                .align(alignment = Alignment.CenterVertically)
        )
        JetsnackGradientTintedIconButton(
            imageVector = Icons.Default.Remove,
            onClick = decreaseItemCount,
            contentDescription = stringResource(R.string.label_decrease),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        Crossfade(
            targetState = count,
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            label = "Count animation"
        ) {
            Text(
                text = "$it",
                style = MaterialTheme.typography.titleSmall,
                fontSize = 18.sp,
                color = JetsnackTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 24.dp)
            )
        }
        JetsnackGradientTintedIconButton(
            imageVector = Icons.Default.Add,
            onClick = increaseItemCount,
            contentDescription = stringResource(R.string.label_increase),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

/**
 * Three different Previews of our [QuantitySelector], one with the default configuration, one with
 * uiMode = [UI_MODE_NIGHT_YES] and one with a "large font"
 */
@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun QuantitySelectorPreview() {
    JetsnackTheme {
        JetsnackSurface {
            QuantitySelector(1, {}, {})
        }
    }
}

/**
 * Right to Left version of our [QuantitySelector].
 */
@Preview("RTL")
@Composable
fun QuantitySelectorPreviewRtl() {
    JetsnackTheme {
        JetsnackSurface {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QuantitySelector(count = 1, decreaseItemCount = {}, increaseItemCount = {})
            }
        }
    }
}
