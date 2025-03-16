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
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.Typography
import com.example.jetcaster.tv.R
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * A lazy-initialized [DateTimeFormatter] that formats dates using the MEDIUM style.
 *
 * This formatter will produce a date string that is typically more compact than
 * LONG or FULL styles, but more detailed than SHORT. The exact format depends on
 * the locale. For example, in the US locale, it might output "Jan 1, 2024".
 *
 * @see DateTimeFormatter.ofLocalizedDate
 * @see FormatStyle.MEDIUM
 */
private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}

/**
 * Displays episode data including the formatted date and duration.
 *
 * This composable function displays a formatted string containing the episode's date and its
 * duration in minutes. It uses a predefined string resource to format the output and applies
 * the provided text style.
 *
 * @param offsetDateTime The date and time of the episode. This will be formatted using
 * [MediumDateFormatter].
 * @param duration The duration of the episode. This will be converted to minutes and
 * displayed as an integer.
 * @param modifier Modifier for styling and layout adjustments. Defaults to `Modifier`.
 * @param style The text style to be applied to the text. Defaults to the [Typography.bodySmall]
 * of our custom [MaterialTheme.typography].
 */
@Composable
internal fun EpisodeDataAndDuration(
    offsetDateTime: OffsetDateTime,
    duration: Duration,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Text(
        text = stringResource(
            R.string.episode_date_duration,
            MediumDateFormatter.format(offsetDateTime),
            duration.toMinutes().toInt()
        ),
        style = style,
        modifier = modifier
    )
}
