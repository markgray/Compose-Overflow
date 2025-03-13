/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetcaster.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetcaster.R

/**
 * A composable function that displays a button to toggle the follow/unfollow state of a podcast.
 *
 * This button visually represents whether the user is following a podcast or not.
 * When clicked, it triggers the provided [onClick] lambda function to handle the state change.
 *
 * We start by initializing our [String] variable `clickLabel` to the [String] with resource ID
 * `R.string.cd_unfollow` ("Unfollow") if the podcast is currently followed, or `R.string.cd_follow`
 * ("Follow") if it's not. Then our root composable is an [IconButton] whose arguments are:
 *  - `onClick`: Is our lambda parameter [onClick].
 *  - `modifier`: Chains to our [Modifier] parameter [modifier] a [Modifier.semantics] in whose
 *  [SemanticsPropertyReceiver] `properties` lambda argument we compose a
 *  [SemanticsPropertyReceiver.onClick] whose `label` argument is our [String] variable `clickLabel`
 *  and whose `action` argument is `null`.
 *
 * In the `content` composable lambda argument of the [IconButton] we compose an [Icon] whose
 * arguments are:
 *  - `imageVector` is the [ImageVector] drawn by  [Icons.Filled.Check] if the our [Boolean] parameter
 *  [isFollowed] is `true`, or [Icons.Filled.Add] if it's `false`.
 *  - `contentDescription` is the [String] with resource ID `R.string.cd_following` ("Following") if
 *  [isFollowed] is `true`, or `R.string.cd_not_following` ("Not following") if it's `false`.
 *  - `tint` is the animated [State] wrapped [Color] returned by [animateColorAsState] whose initial
 *  value is the [ColorScheme.onPrimary] of our custom [MaterialTheme.colorScheme] if [isFollowed]
 *  is `true`, or [ColorScheme.primary] if it's `false`.
 *  - `modifier` is a [Modifier.shadow] whose `elevation` is the animated [State] wrapped [Dp]
 *  returned by` [animateDpAsState] whose initial value is `0.dp` if [isFollowed] is `true`, or
 *  `1.dp` if it's `false`, and whose `shape` is the [Shapes.small] of our custom
 *  [MaterialTheme.shapes]. To this is chained a [Modifier.background] whose `color` argument is the
 *  animated [State] wrapped [Color] returned by [animateColorAsState] whose initial value is
 *  The [ColorScheme.primary] of our custom [MaterialTheme.colorScheme] if [isFollowed] is `true`,
 *  or [ColorScheme.surfaceContainerHighest] if it's `false`, and whose [Shape] `shape` argument is
 *  [CircleShape]. At the end of the [Modifier] chain is a [Modifier.padding] that adds `4.dp`
 *  padding to `all` sides.
 *
 * @param isFollowed A boolean indicating whether the podcast is currently followed (true) or not
 * (false). This determines the icon, text, and color displayed.
 * @param onClick A lambda function that is invoked when the button is clicked.
 * This function should handle the logic for toggling the follow state.
 * @param modifier Optional [Modifier] to be applied to the [IconButton]. Allows for customization
 * of layout, styling, and behavior. Our callers pass us a [BoxScope.align] which aligns us to the
 * `Alignment.BottomEnd` of the [Box] we are composed in.
 */
@Composable
fun ToggleFollowPodcastIconButton(
    isFollowed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel: String = stringResource(if (isFollowed) R.string.cd_unfollow else R.string.cd_follow)
    IconButton(
        onClick = onClick,
        modifier = modifier.semantics {
            onClick(label = clickLabel, action = null)
        }
    ) {
        Icon(
            // TODO: think about animating these icons
            imageVector = when {
                isFollowed -> Icons.Default.Check
                else -> Icons.Default.Add
            },
            contentDescription = when {
                isFollowed -> stringResource(R.string.cd_following)
                else -> stringResource(R.string.cd_not_following)
            },
            tint = animateColorAsState(
                when {
                    isFollowed -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.primary
                }
            ).value,
            modifier = Modifier
                .shadow(
                    elevation = animateDpAsState(if (isFollowed) 0.dp else 1.dp).value,
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    color = animateColorAsState(
                        when {
                            isFollowed -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceContainerHighest
                        }
                    ).value,
                    shape = CircleShape
                )
                .padding(all = 4.dp)
        )
    }
}
