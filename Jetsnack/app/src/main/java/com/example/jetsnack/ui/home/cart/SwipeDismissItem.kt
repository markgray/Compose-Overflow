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
 * each of the [CartItem]'s in the `CartContent` of the [Cart] screen, making it possible to swipe
 * to dismiss them. We initialize and remember our [SwipeToDismissBoxState] variable `val dismissState`
 * to a new instance. We initialize our animated [Boolean] variable `val isDismissed` to `true` when
 * the [SwipeToDismissBoxState.currentValue] is equal to [SwipeToDismissBoxValue.EndToStart] (it can
 * be dismissed by swiping in the reverse of the reading direction, ie right to left - apparently this
 * is believed to be `true` until the [SwipeToDismissBox] has been dismissed?). Our root Composable
 * is an [AnimatedVisibility] holding a [SwipeToDismissBox] in its `content` Composable lambda argument.
 * The `modifier` argument of the [AnimatedVisibility] is our [Modifier] parameter [modifier], its
 * `visible` argument is the inverse of our [Boolean] variable `isDismissed` (defines whether the
 * content should be visible), its `enter` argument is our [EnterTransition] parameter [enter], and
 * its `exit` argument is our [ExitTransition] parameter [exit] (these are the animations used for
 * the appearance and disappearance of its `content` respectively).
 *
 * The arguments of the [SwipeToDismissBox] `content` of the [AnimatedVisibility] are:
 *  - `modifier` we pass our [Modifier] parameter [modifier]
 *  - `state` we pass our [SwipeToDismissBoxState] variable `dismissState` (state of the component)
 *  - `enableDismissFromStartToEnd` we pass `false` ([SwipeToDismissBox] _cannot_ be dismissed from
 *  start to end)
 *  - `backgroundContent` composable that is stacked behind the content and is exposed when the
 *  content is swiped, we pass a lambda which calls our Composable lambda parameter [background]
 *  with the [SwipeToDismissBoxState.progress] of our [SwipeToDismissBoxState] variable `dismissState`
 *  as its [Float] argument `progress`.
 *  - `content` The content that can be dismissed. We pass a lambda that calls our Composable lambda
 *  parameter [content] with our [Boolean] variable `isDismissed` as its `isDismissed` argument.
 *  The lambda passed us by our caller `CartContent` does not appear to use the `isDismissed` argument
 *  which gives me a queasy feeling, but perhaps this is just a relic of a copy/paste the author did.
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
