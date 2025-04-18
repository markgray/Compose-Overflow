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

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ColorScheme
import androidx.tv.material3.MaterialTheme
import java.time.Duration

/**
 * A custom Seekbar composable that displays a line with a movable knob representing a time elapsed
 * within a total duration. It allows for keyboard navigation using the left and right arrow keys.
 *
 * Our root composable is a [Box], but all the work is done in a [Modifier.drawWithCache] in its
 * [CacheDrawScope] `onBuildDrawCache` lambda argument in the [DrawScope] `block` lambda argument of
 * a [CacheDrawScope.onDrawBehind] call (which issues drawing commands to be executed before the
 * layout content is drawn).
 *
 * @param timeElapsed The duration of time that has elapsed.
 * @param length The total duration of the content.
 * @param modifier Modifier for styling and layout adjustments of the Seekbar. Our caller
 * `ElapsedTimeIndicator` passes us a [Modifier.fillMaxWidth]
 * @param onMoveLeft Callback function invoked when the left arrow key is pressed.
 * Typically used to move the knob to the left.
 * @param onMoveRight Callback function invoked when the right arrow key is pressed.
 * Typically used to move the knob to the right.
 * @param knobSize The size of the draggable knob in Dp. Defaults to 8.dp.
 * @param interactionSource The [MutableInteractionSource] representing the stream of Interactions
 * for this Seekbar. Can be used to detect if the seekbar is focused.
 * @param color The color of the seekbar line and knob. Defaults to the [ColorScheme.onSurface] of
 * our custom [MaterialTheme.colorScheme].
 */
@Composable
internal fun Seekbar(
    timeElapsed: Duration,
    length: Duration,
    modifier: Modifier = Modifier,
    onMoveLeft: () -> Unit = {},
    onMoveRight: () -> Unit = {},
    knobSize: Dp = 8.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val brush = SolidColor(value = color)
    val isFocused: Boolean by interactionSource.collectIsFocusedAsState()
    val outlineSize: Dp = knobSize * 1.5f
    Box(
        modifier = modifier
            .drawWithCache {
                onDrawBehind {
                    val knobRadius: Float = knobSize.toPx() / 2
                    val start: Offset = Offset.Zero.copy(y = knobRadius)
                    val end: Offset = start.copy(x = size.width)
                    val knobCenter: Offset = start.copy(
                        x = timeElapsed.seconds.toFloat() / length.seconds.toFloat() * size.width
                    )
                    drawLine(
                        brush = brush, start = start, end = end,
                    )
                    if (isFocused) {
                        val outlineColor: Color = color.copy(alpha = 0.6f)
                        drawCircle(
                            color = outlineColor,
                            radius = outlineSize.toPx() / 2,
                            center = knobCenter
                        )
                    }
                    drawCircle(brush = brush, radius = knobRadius, center = knobCenter)
                }
            }
            .height(height = outlineSize)
            .focusable(enabled = true, interactionSource = interactionSource)
            .onKeyEvent {
                when {
                    it.type == KeyEventType.KeyUp && it.key == Key.DirectionLeft -> {
                        onMoveLeft()
                        true
                    }

                    it.type == KeyEventType.KeyUp && it.key == Key.DirectionRight -> {
                        onMoveRight()
                        true
                    }

                    else -> false
                }
            }
    )
}
