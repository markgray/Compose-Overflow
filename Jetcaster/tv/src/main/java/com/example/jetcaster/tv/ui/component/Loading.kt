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

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.Typography
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max

/**
 * A composable function that displays a loading indicator with an optional message.
 *
 * This function provides a visually consistent loading UI, consisting of a circular
 * progress indicator and a text message. It's highly customizable through its parameters.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param message The text message to display alongside the loading indicator. Defaults to the
 * string with resource ID `R.string.message_loading` ("Loading").
 * @param contentAlignment The alignment of the content within the Box.
 * Defaults to [Alignment.Center].
 * @param style The text style to apply to the message. Defaults to the [Typography.displaySmall]
 * of our custom [MaterialTheme.typography].
 */
@Composable
fun Loading(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.message_loading),
    contentAlignment: Alignment = Alignment.Center,
    style: TextStyle = MaterialTheme.typography.displaySmall,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.default)
        ) {
            CircularProgressIndicator()
            Text(text = message, style = style)
        }
    }
}

/**
 * CircularProgressIndicator displays a circular animation that indicates progress or loading.
 *
 * This composable provides a customizable, animated circular progress indicator with a
 * moving head and tail, simulating an indeterminate loading state.
 *
 * @param modifier The modifier to be applied to the indicator. Our caller [Loading] does not pass
 * us any so the empty, default, or starter [Modifier] that contains no elements is used.
 * @param color The color of the animated indicator arc. Defaults to the primary color
 * from the MaterialTheme.
 * @param strokeWidth The width of the indicator arc's stroke. Defaults to 4.dp.
 */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp,
    trackColor: Color = MaterialTheme.colorScheme.surface,
    strokeCap: StrokeCap = StrokeCap.Round,
) {
    val transition: InfiniteTransition = rememberInfiniteTransition(label = "loading")

    val stroke: Stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = strokeCap)
    }

    val currentRotation: State<Int> = transition.animateValue(
        initialValue = 0,
        targetValue = RotationsPerCycle,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = RotationDuration * RotationsPerCycle,
                easing = LinearEasing
            )
        ),
        label = "loading_current_rotation"
    )
    // How far forward (degrees) the base point should be from the start point
    val baseRotation: State<Float> = transition.animateFloat(
        initialValue = 0f,
        targetValue = BaseRotationAngle,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = RotationDuration,
                easing = LinearEasing
            )
        ),
        label = "loading_base_rotation_angle"
    )
    // How far forward (degrees) both the head and tail should be from the base point
    val endAngle: State<Float> = transition.animateFloat(
        initialValue = 0f,
        targetValue = JumpRotationAngle,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = HeadAndTailAnimationDuration + HeadAndTailDelayDuration
                0f at 0 using CircularEasing
                JumpRotationAngle at HeadAndTailAnimationDuration
            }
        ),
        label = "loading_end_rotation_angle"
    )
    val startAngle: State<Float> = transition.animateFloat(
        initialValue = 0f,
        targetValue = JumpRotationAngle,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = HeadAndTailAnimationDuration + HeadAndTailDelayDuration
                0f at HeadAndTailDelayDuration using CircularEasing
                JumpRotationAngle at durationMillis
            }
        ),
        label = "loading_start_angle"
    )

    Canvas(
        modifier = modifier
            .progressSemantics()
            .size(size = CircularIndicatorDiameter)
    ) {
        drawCircularIndicatorTrack(color = trackColor, stroke = stroke)

        val currentRotationAngleOffset: Float = (currentRotation.value * RotationAngleOffset) % 360f

        // How long a line to draw using the start angle as a reference point
        val sweep: Float = abs(endAngle.value - startAngle.value)

        // Offset by the constant offset and the per rotation offset
        val offset: Float = StartAngleOffset + currentRotationAngleOffset + baseRotation.value
        drawIndeterminateCircularIndicator(
            startAngle = startAngle.value + offset,
            strokeWidth = strokeWidth,
            sweep = sweep,
            color = color,
            stroke = stroke
        )
    }
}

/**
 * Draws a circular arc indicator within the [DrawScope].
 *
 * This function draws a portion of a circle (an arc) using the specified parameters.
 * It calculates the correct dimensions and position for the arc to ensure it is drawn
 * with the given stroke width and within the available drawing area.
 *
 * @param startAngle The starting angle of the arc, in degrees. 0 degrees corresponds to the
 * positive x-axis, and angles increase clockwise.
 * @param sweep The sweep angle of the arc, in degrees. A positive sweep angle draws the arc
 * clockwise from the start angle.
 * @param color The color to use when drawing the arc.
 * @param stroke The stroke style to use when drawing the arc, specifying the line width and
 * cap/join styles.
 */
private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset: Float = stroke.width / 2
    val arcDimen: Float = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(x = diameterOffset, y = diameterOffset),
        size = Size(width = arcDimen, height = arcDimen),
        style = stroke
    )
}

/**
 * Draws a circular indicator track.
 *
 * This function draws a full circle representing the background or track of a circular
 * indicator (e.g., a progress circle).  It uses the provided [color] and [stroke] to define
 * its appearance. The circle is drawn from 0 degrees to 360 degrees, completing a full
 * rotation.
 *
 * @param color The color to use for drawing the track.
 * @param stroke The stroke style to use for drawing the track. This defines the thickness
 * and style (e.g., cap type) of the line.
 */
private fun DrawScope.drawCircularIndicatorTrack(
    color: Color,
    stroke: Stroke
) = drawCircularIndicator(startAngle = 0f, sweep = 360f, color = color, stroke = stroke)

/**
 * Draws an indeterminate circular indicator arc.
 *
 * This function draws a segment of a circle (an arc) that represents the progress of an
 * indeterminate operation. It takes into account the stroke width, color, start angle, sweep angle,
 * and stroke cap style to accurately render the arc. It also handles edge cases where the start and
 * end angles are close together to ensure a minimum arc length is always drawn.
 *
 * @param startAngle The starting angle of the arc in degrees.
 * @param strokeWidth The width of the arc's stroke in [Dp].
 * @param sweep The sweep angle of the arc in degrees. This defines how many degrees the arc covers.
 * @param color The color of the arc.
 * @param stroke The stroke style to use for drawing the arc. This includes the cap style
 * (e.g., Butt, Round, Square).
 */
private fun DrawScope.drawIndeterminateCircularIndicator(
    startAngle: Float,
    strokeWidth: Dp,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    val strokeCapOffset: Float = if (stroke.cap == StrokeCap.Butt) {
        0f
    } else {
        // Length of arc is angle * radius
        // Angle (radians) is length / radius
        // The length should be the same as the stroke width for calculating the min angle
        (180.0 / PI).toFloat() * (strokeWidth / (CircularIndicatorDiameter / 2)) / 2f
    }

    // Adding a stroke cap draws half the stroke width behind the start point, so we want to
    // move it forward by that amount so the arc visually appears in the correct place
    val adjustedStartAngle: Float = startAngle + strokeCapOffset

    // When the start and end angles are in the same place, we still want to draw a small sweep, so
    // the stroke caps get added on both ends and we draw the correct minimum length arc
    val adjustedSweep: Float = max(sweep, 0.1f)

    drawCircularIndicator(
        startAngle = adjustedStartAngle,
        sweep = adjustedSweep,
        color = color,
        stroke = stroke
    )
}

/**
 * The diameter of the circular indicator in the component.
 *
 * This value determines the size of the circular visual element used to
 * represent loading or progress.  It's set to a specific dimension
 * (38 density-independent pixels) for consistency and visual appeal.
 */
private val CircularIndicatorDiameter = 38.dp

/**
 * The number of full rotations a component completes within a single cycle.
 *
 * This constant defines how many times a rotating component (like a wheel or a gear)
 * spins around its axis during one complete cycle of its operation.
 *
 * For example, if a cycle represents a certain time interval, then this value indicates
 * how many complete revolutions occur within that interval.
 */
private const val RotationsPerCycle = 5

/**
 * The duration, in milliseconds, for a full rotation animation.
 * This constant is used to define the length of time it takes for
 * a visual element to complete a 360-degree rotation.
 *
 * Value: 1332 milliseconds (approximately 1.332 seconds)
 */
private const val RotationDuration = 1332

/**
 * The base rotation angle used for certain UI elements or animations.
 * This value, 286 degrees, likely represents a significant or visually
 * distinct rotation amount in the application's design.  It might be
 * used as a default or starting point for rotational animations or
 * to visually emphasize a specific state or change.
 */
private const val BaseRotationAngle = 286f

/**
 * The angle (in degrees) that the jumping object should rotate towards
 * during a jump animation. This value represents a specific rotation
 * that visually indicates the object is in a "jump" or "launch" state.
 *
 * A value of 290 degrees typically suggests a slight backward lean or
 * tilt, which can enhance the perception of upward momentum in the jump.
 */
private const val JumpRotationAngle = 290f

/**
 * The duration of the head and tail animation in milliseconds.
 *
 * This constant defines the time it takes for the head and tail portions of an animated
 * element to complete their respective animations (e.g., fading in/out, scaling).
 * It is calculated as half of the [RotationDuration]. This ensures that the head and tail
 * animations are synchronized and visually coherent with the overall rotation animation.
 *
 * Value : [RotationDuration] * 0.5
 */
private const val HeadAndTailAnimationDuration = (RotationDuration * 0.5).toInt()

/**
 * The delay duration applied to both the head and tail animations.
 *
 * This constant represents the amount of time, in milliseconds, that the head and tail
 * animations should be delayed before they start animating. It uses the same duration
 * as the overall animation duration [HeadAndTailAnimationDuration] ensuring that
 * the head and tail animations are delayed by a consistent and meaningful amount of time.
 */
private const val HeadAndTailDelayDuration = HeadAndTailAnimationDuration

/**
 * A pre-defined easing curve that provides a circular motion effect.
 *
 * This easing function starts slowly and accelerates towards the end,
 * giving a smooth, rounded feel to the animation. It is based on a
 * cubic Bézier curve with control points (0.4, 0) and (0.2, 1).
 *
 * This type of easing is often used when you want a natural,
 * organic feel to the motion, like something rolling or sliding.
 *
 * The animation starts with a gentle acceleration and ends with a
 * slightly faster deceleration.
 */
private val CircularEasing = CubicBezierEasing(a = 0.4f, b = 0f, c = 0.2f, d = 1f)

/**
 * The starting angle offset for the chart, in degrees.
 *
 * This offset determines the initial position of the chart's first data point.
 * A value of -90f (or -90 degrees) means the chart will start at the top (12 o'clock)
 * and proceed clockwise. Other common values include:
 *
 * - 0f: Starts at the right (3 o'clock) and proceeds clockwise.
 * - 90f: Starts at the bottom (6 o'clock) and proceeds clockwise.
 * - 180f: Starts at the left (9 o'clock) and proceeds clockwise.
 *
 * By adjusting this value, you can customize the visual orientation of the chart.
 */
private const val StartAngleOffset = -90f

/**
 * The offset angle applied to the rotation, calculated by summing the [BaseRotationAngle]
 * and [JumpRotationAngle] and then taking the modulo 360 to ensure the angle remains
 * within the range of 0 to 360 degrees. This is used to determine the final rotation angle.
 *
 * @see BaseRotationAngle
 * @see JumpRotationAngle
 */
private const val RotationAngleOffset = (BaseRotationAngle + JumpRotationAngle) % 360f
