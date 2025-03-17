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

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ButtonScale
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.example.jetcaster.tv.R

/**
 * A composable function that creates a Play button with an icon and label.
 *
 * This button displays a "Play" icon and the label "Play". When clicked, it triggers the
 * provided `onClick` lambda.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier Modifier to be applied to the button.
 * @param scale The scaling configuration for the button. Defaults to [ButtonDefaults.scale].
 */
@Composable
internal fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    scale: ButtonScale = ButtonDefaults.scale(),
) =
    ButtonWithIcon(
        icon = Icons.Outlined.PlayArrow,
        label = stringResource(id = R.string.label_play),
        onClick = onClick,
        modifier = modifier,
        scale = scale
    )

/**
 * A composable function that creates a button for enqueueing items to a playlist.
 *
 * This button displays a playlist add icon and triggers the provided [onClick] action when clicked.
 *
 * @param onClick The callback function to be invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 */
@Composable
internal fun EnqueueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = stringResource(id = R.string.label_add_playlist),
        )
    }
}

/**
 * A composable function that displays an information button.
 *
 * This button, when clicked, triggers the provided `onClick` lambda. It visually
 * represents the information icon, making it suitable for scenarios where users need
 * access to more details or contextual help.
 *
 * @param onClick The callback that will be executed when the button is clicked.
 * @param modifier Modifiers to be applied to the underlying [IconButton].
 */
@Composable
internal fun InfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(id = R.string.label_info),
        )
    }
}

/**
 * A composable function that creates a button to navigate to the previous episode.
 *
 * This button displays a "Skip Previous" icon and triggers the provided [onClick]
 * lambda when clicked. It's typically used within a media player or episode list
 * UI to allow users to quickly jump to the previous item.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier Modifier to apply to this button.
 */
@Composable
internal fun PreviousButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = stringResource(id = R.string.label_previous_episode)
        )
    }
}

/**
 * A composable function that creates a button with a "Next" icon.
 *
 * This button is typically used to navigate to the next item in a sequence,
 * such as the next episode in a series.
 *
 * @param onClick The callback that is triggered when the button is clicked.
 * @param modifier Modifier to be applied to the button.
 */
@Composable
internal fun NextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = stringResource(id = R.string.label_next_episode)
        )
    }
}

/**
 * A composable button that toggles between play and pause states.
 *
 * This button displays either a play arrow or a pause icon, depending on the [isPlaying] state.
 * Clicking the button triggers the [onClick] lambda.
 *
 * @param isPlaying A boolean indicating whether the media is currently playing.
 *                  If `true`, the pause icon will be displayed.
 *                  If `false`, the play arrow icon will be displayed.
 * @param onClick A lambda function to be invoked when the button is clicked.
 *                This is typically used to start or pause the media playback.
 * @param modifier Optional [Modifier] to be applied to the button. Our caller `PlayerControl` passes
 * us a [Modifier.size] to size us, with a `Modifier.focusRequester` chained to that to allow us to
 * request changes of focus.
 */
@Composable
internal fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon: ImageVector, description: String) = if (isPlaying) {
        Icons.Default.Pause to stringResource(id = R.string.label_pause)
    } else {
        Icons.Default.PlayArrow to stringResource(id = R.string.label_play)
    }
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(imageVector = icon, contentDescription = description, modifier = Modifier.size(48.dp))
    }
}

/**
 * A composable function that renders a rewind button.
 *
 * This button, when clicked, triggers the provided [onClick] callback. It displays a rewind icon
 * (specifically, the "Replay10" icon) and includes a content description for accessibility.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier [Modifier] to be applied to the IconButton. This can be used to adjust layout
 * properties such as padding, size, or alignment. Our caller `PlayerControl` passes us a
 * [Modifier.size] to size us.
 */
@Composable
internal fun RewindButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Replay10,
            contentDescription = stringResource(id = R.string.label_rewind)
        )
    }
}

/**
 * A composable function that renders a skip button.
 *
 * This button is typically used to skip forward in a media player or similar context.
 * It displays a forward arrow icon and has a configurable click action.
 *
 * @param onClick The callback to be invoked when the button is clicked. This should trigger the
 * skipping action.
 * @param modifier [Modifier] to be applied to the button.  Our caller `PlayerControl` passes us a
 * [Modifier.size] to size us.
 */
@Composable
internal fun SkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Forward10,
            contentDescription = stringResource(id = R.string.label_skip)
        )
    }
}
