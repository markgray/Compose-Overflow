/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * Displays two composable functions side-by-side in a row.
 *
 * This composable provides a simple way to arrange two distinct UI elements horizontally,
 * with control over their horizontal spacing.
 *
 * @param first The first composable function to display. This will be placed on the left.
 * @param second The second composable function to display. This will be placed on the right.
 * @param modifier Optional [Modifier] to apply to the underlying [Row].
 * @param horizontalArrangement The horizontal arrangement of the two composables within the [Row].
 * Defaults to [Arrangement.spacedBy] with a spacing defined by `JetcasterAppDefaults.gap.twoColumn`
 * (`36.dp` in this case).
 */
@Composable
internal fun TwoColumn(
    first: (@Composable RowScope.() -> Unit),
    second: (@Composable RowScope.() -> Unit),
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.twoColumn)
) {
    Row(
        horizontalArrangement = horizontalArrangement,
        modifier = modifier
    ) {
        first()
        second()
    }
}
