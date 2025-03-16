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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ButtonScale
import androidx.tv.material3.Icon
import androidx.tv.material3.Text

/**
 * A composable function that creates a button with an icon and a text label.
 *
 * This button displays an icon on the left and a text label on the right.
 * It's designed for actions that can be visually represented by an icon.
 *
 * @param label The text label to display on the button.
 * @param icon The [ImageVector] to display as an icon on the button.
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier Optional [Modifier] to apply to the button.
 * @param scale Optional [ButtonScale] to control the button's scaling behavior on interaction.
 * Defaults to [ButtonDefaults.scale].
 */
@Composable
internal fun ButtonWithIcon(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    scale: ButtonScale = ButtonDefaults.scale(),
) {
    Button(onClick = onClick, modifier = modifier, scale = scale) {
        Icon(
            imageVector = icon,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(width = 6.dp))
        Text(text = label)
    }
}
