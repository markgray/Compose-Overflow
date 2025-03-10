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

package com.example.jetcaster.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen

/**
 * A composable function that displays a full-screen loading indicator.
 *
 * This function shows a [CircularProgressIndicator] centered within a [Surface] that
 * fills the available space.
 *
 * Our root composable is a [Surface] whose [Modifier] `modifier` is our [Modifier] parameter
 * [modifier]. Its `content` Composable lambda argument is a [Box] whose `modifier` argument
 * is a [Modifier.fillMaxSize]. In its [BoxScope] `content` Composable lambda argument we compose
 * a [CircularProgressIndicator] whose [Modifier] `modifier` argument is a [BoxScope.align] whose
 * `alignment` argument is [Alignment.Center].
 *
 * @param modifier The [Modifier] to be applied to the [Surface]. Our caller
 * `PodcastDetailsLoadingScreen` passes us its own [Modifier] parameter [modifier], and its
 * caller [PodcastDetailsScreen] passes it a [Modifier.fillMaxSize].
 */
@Composable
fun Loading(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    }
}
