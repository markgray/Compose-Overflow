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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Text
import com.example.jetcaster.tv.R

/**
 * Displays a message indicating that a particular feature is not yet available.
 *
 * This composable is used to inform the user that a specific feature or functionality
 * is not currently implemented or accessible. It displays a simple text message to convey this.
 *
 * @param modifier The modifier to be applied to the Text composable. Allows for customization
 * of the layout and appearance of the message. Defaults to Modifier.
 * @param message The text message to be displayed. By default, it uses a string resource whose ID
 * is `R.string.message_not_available_feature` ("This feature is not available yet"). This allows
 * for easy localization and customization of the message.
 */
@Composable
internal fun NotAvailableFeature(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.message_not_available_feature)
) {
    Text(text = message, modifier = modifier)
}
