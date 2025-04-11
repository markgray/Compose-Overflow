/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetcaster.ui.preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.PodcastInfo

/**
 * [PreviewParameterProvider] for [PodcastInfo] used to provide sample data for Compose Previews.
 *
 * This class provides a sequence of [PodcastInfo] objects obtained from [PreviewPodcasts]
 * to be used in UI previews within Android Studio. This allows developers to see how their
 * Composable functions will render with various example podcast data without needing to
 * run the application on a device or emulator.
 *
 * The `values` property returns a sequence of [PodcastInfo] instances, which are
 * internally sourced from the [PreviewPodcasts] object.
 *
 * @see PodcastInfo
 * @see PreviewParameterProvider
 * @see PreviewPodcasts
 */
class WearPreviewPodcasts : PreviewParameterProvider<PodcastInfo> {
    override val values: Sequence<PodcastInfo>
        get() = PreviewPodcasts.asSequence()
}
