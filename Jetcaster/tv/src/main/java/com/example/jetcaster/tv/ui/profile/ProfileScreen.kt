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

package com.example.jetcaster.tv.ui.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.jetcaster.tv.ui.component.NotAvailableFeature

/**
 * Represents the Profile screen of the application.
 *
 * Currently, this screen displays the [NotAvailableFeature] placeholder indicating that the
 * profile feature is not yet available. It is called with its `modifier` argument our [Modifier]
 * parameter [modifier].
 *
 * @param modifier [Modifier] to be applied to the layout. Our caller the `Route` method of
 * `JetcasterApp` passes us a [Modifier.fillMaxSize] with a [Modifier.padding] chained to it
 * that adds the [PaddingValues] constant `JetcasterAppDefaults.overScanMargin.default`
 * (top = 40.dp, bottom = 40.dp, start = 80.dp, end = 80.dp) to our padding.
 */
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    NotAvailableFeature(modifier = modifier)
}
