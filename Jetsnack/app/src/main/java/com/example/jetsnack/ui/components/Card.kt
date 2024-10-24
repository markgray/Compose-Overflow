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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * Used by `HighlightSnackItem` (private) in [com.example.jetsnack.ui.components.Snacks] it is just
 * a wrapper around a [JetsnackSurface] that supplies custom defaults (many of which our caller
 * overrides). Our root Composable is just a [JetsnackSurface] to which we pass our parameters to
 * its arguments of the same name.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller `HighlightSnackItem` calls us with its `modifier` parameter to which is chains
 * a [Modifier.padding] that adds 16.dp padding, followed by a [SharedTransitionScope.sharedBounds]
 * which defines the values controlling the shared element transition of our contents (too complex
 * to be worth time spent analyzing it IMO), followed by a [Modifier.size] that set our `width` to
 * `HighlightCardWidth` (170.dp) and our `height` to 250.dp, and at the end of the chain is a
 * [Modifier.border] whose `width` is 1.dp, whose `color` is a copy of the [JetsnackColors.uiBorder]
 * from our [JetsnackTheme] custom [MaterialTheme] whose `alpha` is 0.12f, and the `shape` of the
 * [Modifier.border] is a [RoundedCornerShape] whose `size` is a shared element animated size between
 * 0.dp and 20.dp.
 * @param shape the [Shape] to use as the `shape` argument of our [JetsnackSurface]. Our caller passes
 * us a [RoundedCornerShape] whose `size` is a shared element animated size between 0.dp and 20.dp.
 * @param color the [Color] to use as the `color` argument of our [JetsnackSurface]. Our caller does
 * not pass us one, so our default of [JetsnackColors.uiBackground] from our [JetsnackTheme] custom
 * [MaterialTheme] is used instead.
 * @param contentColor the [Color] to use as the `contentColor` argument of our [JetsnackSurface].
 * Our caller does not pass us one, so our default of [JetsnackColors.textPrimary] from our
 * [JetsnackTheme] custom [MaterialTheme] is used instead.
 * @param border the [BorderStroke] to use as the `border` argument of our [JetsnackSurface]. Our
 * caller does not pass us one so the default of `null` is used instead.
 * @param elevation the [Dp] to use as the `elevation` argument of our [JetsnackSurface]. Our caller
 * passes us 0.dp.
 */
@Composable
fun JetsnackCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textPrimary,
    border: BorderStroke? = null,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    JetsnackSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = elevation,
        border = border,
        content = content
    )
}

/**
 * Three Previews of our [JetsnackCard] using different Preview arguments, each displaying a [Text]
 * whose `text` is "Demo"
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun CardPreview() {
    JetsnackTheme {
        JetsnackCard {
            Text(text = "Demo", modifier = Modifier.padding(16.dp))
        }
    }
}
