/*
 * Copyright 2021 The Android Open Source Project
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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * An alternative to [Snackbar] utilizing [JetsnackColors]. We just call the [Snackbar] Composable
 * with our [SnackbarData] parameter [snackbarData] as its `snackbarData`, with our [Modifier]
 * parameter [modifier] as its `modifier` argument, with our [Boolean] parameter [actionOnNewLine]
 * as its `actionOnNewLine` argument, with our [Shape] parameter [shape] as its `shape`, with our
 * [Color] parameter [backgroundColor] as its `containerColor` argument, with our [Color] parameter
 * [contentColor] as its `contentColor` argument, and with our [Color] parameter [actionColor] as
 * its `actionColor` argument.
 *
 * @param snackbarData Interface to represent the data of one particular Snackbar as a piece of the
 * [SnackbarHostState]. Used as the `snackbarData` argument of our [Snackbar]. Our caller [MainContainer]
 * calls us with the same [SnackbarData] passed the lambda (which is a bit odd).
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param actionOnNewLine whether or not action should be put on a separate line. Our caller does
 * not pass us any so the default of `false` is used.
 * @param shape the [Shape] we should use as the `shape` argument of our [Snackbar]. Our caller does
 * not pass us one so the default [Shapes.small] of our custom [MaterialTheme.shapes] is used.
 * @param backgroundColor the [Color] we should use as the `containerColor` argument of our [Snackbar].
 * Our caller [MainContainer] does not pass us one so the [JetsnackColors.uiBackground] of our custom
 * [JetsnackTheme.colors] is used.
 * @param contentColor the [Color] we should use as the `contentColor` argument of our [Snackbar].
 * Our caller [MainContainer] does not pass us one so the [JetsnackColors.textSecondary] of our custom
 * [JetsnackTheme.colors] is used.
 * @param actionColor the [Color] to use as the `actionColor` argument of our [Snackbar]. Our caller
 * [MainContainer] does not pass us one so the [JetsnackColors.brand] of our custom [JetsnackTheme.colors]
 * is used.
 */
@Composable
fun JetsnackSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textSecondary,
    actionColor: Color = JetsnackTheme.colors.brand
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = backgroundColor,
        contentColor = contentColor,
        actionColor = actionColor
    )
}
