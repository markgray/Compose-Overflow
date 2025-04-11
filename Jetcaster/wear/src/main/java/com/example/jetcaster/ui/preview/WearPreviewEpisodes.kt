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
import com.example.jetcaster.core.domain.testing.PreviewPlayerEpisodes
import com.example.jetcaster.core.player.model.PlayerEpisode

/**
 * [PreviewParameterProvider] for providing a sequence of [PlayerEpisode] objects
 * specifically for Wear OS previews.
 *
 * This class is designed to be used with the `@Preview` annotation in Compose
 * to display example [PlayerEpisode] data within the Android Studio preview panel
 * for Wear OS components. It leverages the fake data in the [PreviewPlayerEpisodes]
 * object to provide a predefined set of episodes.
 *
 * @see PreviewParameterProvider
 * @see androidx.compose.ui.tooling.preview.Preview
 * @see androidx.compose.ui.tooling.preview.PreviewParameter
 * @see PlayerEpisode
 * @see PreviewPlayerEpisodes
 */
class WearPreviewEpisodes : PreviewParameterProvider<PlayerEpisode> {
    override val values: Sequence<PlayerEpisode>
        get() = PreviewPlayerEpisodes.asSequence()
}
