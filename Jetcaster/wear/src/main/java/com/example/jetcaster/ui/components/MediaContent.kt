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

package com.example.jetcaster.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ChipDefaults
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.images.coil.CoilPaintable
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Displays the information about a media item (e.g., an episode) as a [Chip].
 *
 * This composable shows the title, date, and optional duration of a media episode,
 * along with an associated artwork (if available). It provides a clickable
 * interface that triggers an action when the item is selected.
 *
 * @param episode The [PlayerEpisode] data to be displayed.
 * @param episodeArtworkPlaceholder A [Painter] to be used as a placeholder for the
 * episode's artwork while it's loading or if no artwork is available.
 * @param onItemClick A lambda function that's invoked when the chip is clicked.
 * It is passed our [PlayerEpisode] parameter [episode] as its argument.
 * @param modifier Modifier for styling and layout adjustments of the chip.
 */
@Composable
fun MediaContent(
    episode: PlayerEpisode,
    episodeArtworkPlaceholder: Painter?,
    onItemClick: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaTitle: String = episode.title
    val duration: Duration? = episode.duration

    val secondaryLabel: String? = when {
        duration != null -> {
            // If we have the duration, we combine the date/duration via a
            // formatted string
            stringResource(
                R.string.episode_date_duration,
                MediumDateFormatter.format(episode.published),
                duration.toMinutes().toInt()
            )
        }
        // Otherwise we just use the date
        else -> MediumDateFormatter.format(episode.published)
    }

    Chip(
        label = mediaTitle,
        onClick = { onItemClick(episode) },
        secondaryLabel = secondaryLabel,
        icon = CoilPaintable(
            model = episode.podcastImageUrl,
            placeholder = episodeArtworkPlaceholder
        ),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
        modifier = modifier
    )
}

/**
 * A lazy-initialized [DateTimeFormatter] that formats dates using the medium style.
 *
 * This formatter uses the locale's default settings for medium-style date formatting.
 * The output format will vary based on the user's locale.
 *
 * **Examples (for US locale):**
 *  - "Jan 1, 2024"
 *  - "Dec 25, 2023"
 *
 * **Examples (for UK locale):**
 *  - "1 Jan 2024"
 *  - "25 Dec 2023"
 *
 * The formatter is created lazily, meaning it is only initialized when it is first accessed.
 * Subsequent accesses will return the same formatter instance.
 *
 * @see DateTimeFormatter.ofLocalizedDate
 * @see FormatStyle.MEDIUM
 */
val MediumDateFormatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
