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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * Displays an error state screen with a message and a button to return to the home screen.
 *
 * @param backToHome A lambda function that is invoked when the "Back to Home" button is clicked.
 * This function should handle the navigation back to the home screen.
 * @param modifier The [Modifier] to be applied to the layout.
 * @param focusRequester A [FocusRequester] used to request focus on the "Back to Home" button when
 * the screen is displayed. Defaults to a newly created [FocusRequester].
 */
@Composable
fun ErrorState(
    backToHome: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = stringResource(id = R.string.display_error_state),
                style = MaterialTheme.typography.displayMedium
            )
            Button(
                onClick = backToHome,
                modifier = modifier
                    .padding(top = JetcasterAppDefaults.gap.podcastRow)
                    .focusRequester(focusRequester = focusRequester)
            ) {
                Text(text = stringResource(id = R.string.label_back_to_home))
            }
        }
    }
}
