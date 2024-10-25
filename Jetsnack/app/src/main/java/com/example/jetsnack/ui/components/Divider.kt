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

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.home.cart.CartItem
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * Our custom [HorizontalDivider]. It just passes its parameters to the [HorizontalDivider] arguments
 * of the same name.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior, we pass it unchanged to our [HorizontalDivider] root Composable. Our caller [CartItem]
 * passes us a [androidx.constraintlayout.compose.ConstraintLayoutScope.constrainAs] that specifies
 * our position, as does `SearchResult`, `Title` passes an empty, default, or starter [Modifier]
 * that contains no elements and the rest of our caller pass us none so the empty, default, or
 * starter [Modifier] that contains no elements is used.
 * @param color the [Color] to pass to our [HorizontalDivider] root composable as its `color` argument
 * (color of the divider line.) None of our callers pass us one so our default value of a copy of
 * [JetsnackColors.uiBorder] with alpha of `DividerAlpha` (0,12f) is used.
 * @param thickness passed to our [HorizontalDivider] root composable as its `thickness` argument
 * (thickness of the divider line). Only our `SnackCollectionList` caller passes us a value (2.dp)
 * so for all the others our default value of 1.dp is used instead.
 */
@Composable
fun JetsnackDivider(
    modifier: Modifier = Modifier,
    color: Color = JetsnackTheme.colors.uiBorder.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp
) {
    HorizontalDivider(
        modifier = modifier,
        color = color,
        thickness = thickness
    )
}

/**
 * The alpha to use for the `color` of the [HorizontalDivider].
 */
private const val DividerAlpha = 0.12f

/**
 * Two Previews of our [JetsnackDivider] using different configurations.
 */
@Preview("default", showBackground = true)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DividerPreview() {
    JetsnackTheme {
        Box(modifier = Modifier.size(height = 10.dp, width = 100.dp)) {
            JetsnackDivider(modifier = Modifier.align(Alignment.Center))
        }
    }
}
