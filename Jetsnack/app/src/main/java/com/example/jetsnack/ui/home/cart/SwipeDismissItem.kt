/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetsnack.ui.home.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.example.jetsnack.model.OrderLine

/**
 * Holds the Swipe to dismiss composable, its animation and the current state. It is used to hold
 * all of the [CartItem]'s in the `CartContent` of the [Cart] screen.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller `CartContent` passes us a [LazyItemScope.animateItem] whose `fadeInSpec`
 * argument is custom [SpringSpec] of [Float] and whose `fadeOutSpec` argument is the same
 * [SpringSpec], and whose `placementSpec` argument is a custom [SpringSpec] of [IntOffset].
 * @param enter the [EnterTransition] to use as the `enter` argument of the [AnimatedVisibility]
 * wrapping our [SwipeToDismissBox]. Our caller does not pass us one so the default [expandVertically]
 * is used instead.
 * @param exit the [ExitTransition] to use as the `exit` argument of the [AnimatedVisibility] wrapping
 * our [SwipeToDismissBox]. Our caller does not pass us one so the default [shrinkVertically] is used
 * instead.
 * @param background a Composable lambda to use as the `backgroundContent` argument of our
 * [SwipeToDismissBox] (composable that is stacked behind its `content` and is exposed when the
 * `content` is swiped). Our caller passes us a `SwipeDismissItemBackground` which does a fancy
 * animation based on the value of the [Float] parameter `progress` passed it. We pass it the
 * [SwipeToDismissBoxState.progress] of the [SwipeToDismissBoxState] of the [SwipeToDismissBox].
 * @param content the Composable lambda that is used as the `content` argument of our
 * [SwipeToDismissBox]. Our caller passes us a [CartItem] for each of the [OrderLine] in the Cart.
 */
@Composable
fun SwipeDismissItem(
    modifier: Modifier = Modifier,
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(),
    background: @Composable (progress: Float) -> Unit,
    content: @Composable (isDismissed: Boolean) -> Unit,
) {
    // Hold the current state from the Swipe to Dismiss composable
    val dismissState: SwipeToDismissBoxState = rememberSwipeToDismissBoxState()
    // Boolean value used for hiding the item if the current state is dismissed
    val isDismissed: Boolean = dismissState.currentValue == SwipeToDismissBoxValue.EndToStart

    AnimatedVisibility(
        modifier = modifier,
        visible = !isDismissed,
        enter = enter,
        exit = exit
    ) {
        SwipeToDismissBox(
            modifier = modifier,
            state = dismissState,
            enableDismissFromStartToEnd = false,
            backgroundContent = { background(dismissState.progress) },
            content = { content(isDismissed) }
        )
    }
}
