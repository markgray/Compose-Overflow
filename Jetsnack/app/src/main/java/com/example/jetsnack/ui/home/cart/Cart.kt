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

package com.example.jetsnack.ui.home.cart

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetsnack.R
import com.example.jetsnack.model.OrderLine
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.home.DestinationBar
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.formatPrice
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

/**
 * Stateful override of our stateless `Cart` Composable. We start by initializing our [State] wrapped
 * [List] of [OrderLine] variable `val orderLines` by using the [StateFlow.collectAsStateWithLifecycle]
 * method of the [StateFlow] wrapped [List] of [OrderLine] property [CartViewModel.orderLines] of our
 * [CartViewModel] parameter [viewModel]. Then we initialize and remember our [SnackCollection] variable
 * `val inspiredByCart` to the [SnackCollection] returned by the [SnackRepo.getInspiredByCart] method.
 * Our root Composable is then the stateless override of [Cart] which we call with the arguments:
 *  - `orderLines` our [State] wrapped [List] of [OrderLine] variable `orderLines`
 *  - `removeSnack` a function reference to the [CartViewModel.removeSnack] method of our
 *  [CartViewModel] parameter [viewModel].
 *  - `increaseItemCount` a function reference to the [CartViewModel.increaseSnackCount] method of
 *  our [CartViewModel] parameter [viewModel].
 *  - `decreaseItemCount`  a function reference to the [CartViewModel.decreaseSnackCount] method of
 *  our [CartViewModel] parameter [viewModel].
 *  - `inspiredByCart` our [SnackCollection] variable `inspiredByCart`.
 *  - `onSnackClick` our lambda taking a [Long] and a [String] parameter [onSnackClick].
 *  - `modifier` our [Modifier] parameter [modifier]
 *
 * @param onSnackClick a lambda taking a [Long] and a [String] that a Composable holding a [Snack]
 * should call with the [Snack.id] of the [Snack] and a [String] identifying the [SnackCollection]
 * it is part of whenever the Composable is clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [modifier] parameter traces back to [MainContainer] where a [Modifier.padding]
 * with the [PaddingValues] that are passed by [JetsnackScaffold] to its `content` lambda is
 * chained to a [Modifier.consumeWindowInsets] that consumes those [PaddingValues] as insets.
 * @param viewModel the [CartViewModel] for the app.
 */
@Composable
fun Cart(
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory())
) {
    val orderLines: List<OrderLine> by viewModel.orderLines.collectAsStateWithLifecycle()
    val inspiredByCart: SnackCollection = remember { SnackRepo.getInspiredByCart() }
    Cart(
        orderLines = orderLines,
        removeSnack = viewModel::removeSnack,
        increaseItemCount = viewModel::increaseSnackCount,
        decreaseItemCount = viewModel::decreaseSnackCount,
        inspiredByCart = inspiredByCart,
        onSnackClick = onSnackClick,
        modifier = modifier
    )
}

/**
 * The stateless override of `Cart` (which is called by the stateful override). Our root Composable
 * is a [JetsnackSurface] whose `modifier` argument chains a [Modifier.fillMaxSize] to our [Modifier]
 * parameter [modifier] (causes it to occupy its entire incoming size constraints). In its `content`
 * Composable lambda argument we have a [Box] whose `modifier` argument is a [Modifier.fillMaxSize],
 * and in its [BoxScope] `content` lambda argument we have three Composables:
 *  - A [CartContent] whose `orderLines` argument is our [List] of [OrderLine] parameter [orderLines],
 *  whose `removeSnack` argument is our lambda taking [Long] parameter [removeSnack], whose
 *  `increaseItemCount` argument is our lambda taking [Long] parameter [increaseItemCount], whose
 *  `decreaseItemCount` argument is our lambda taking [Long] parameter [decreaseItemCount], whose
 *  `inspiredByCart` argument is our [SnackCollection] parameter [inspiredByCart], whose `onSnackClick`
 *  argument is our lambda taking [Long] and [String] parameter [onSnackClick], and whose `modifier`
 *  argument is a [BoxScope.align] whose `alignment` is a [Alignment.TopCenter] that aligns the root
 *  [LazyColumn] Composable of [CartContent] to the top center of the [Box] (note that the first
 *  `item` in the [LazyColumn] contains a [Spacer] which compensates for the fact that the
 *  [DestinationBar] is composed on top of the [CartContent]).
 *  - A [DestinationBar] whose `modifier` argument is a [BoxScope.align] whose `alignment` is a
 *  [Alignment.TopCenter] that aligns it to the top center of the [Box] (on top of the [CartContent]).
 *  - A [CheckoutBar] whose `modifier` argument is a [BoxScope.align] whose `alignment` is a
 *  [Alignment.BottomCenter] that aligns it to the bottom center of the [Box] (overlayed on top of
 *  the [CartContent]).
 *
 * @param orderLines the [List] of [OrderLine] that our [CartContent] Composable should display.
 * @param removeSnack a lambda that should be called with the [Snack.id] when the user indicates that
 * they wish to remove a [Snack] from their order.
 * @param increaseItemCount a lambda that should be called with the [Snack.id] when the user
 * indicates that they want to add 1 more to their order of a [Snack].
 * @param decreaseItemCount a lambda that should be called with the [Snack.id] when the user
 * indicates that they want to subtract 1 from their order of a [Snack].
 * @param inspiredByCart a [SnackCollection] that is "inspired" by the contents of the cart.
 * @param onSnackClick a lambda that should be called with the [Snack.id] of the [Snack] that the
 * user clicks on and a [String] describing the collection it belongs to.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [modifier] parameter traces back to [MainContainer] where a [Modifier.padding]
 * with the [PaddingValues] that are passed by [JetsnackScaffold] to its `content` lambda is
 * chained to a [Modifier.consumeWindowInsets] that consumes those [PaddingValues] as insets.
 */
@Composable
fun Cart(
    orderLines: List<OrderLine>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            CartContent(
                orderLines = orderLines,
                removeSnack = removeSnack,
                increaseItemCount = increaseItemCount,
                decreaseItemCount = decreaseItemCount,
                inspiredByCart = inspiredByCart,
                onSnackClick = onSnackClick,
                modifier = Modifier.align(alignment = Alignment.TopCenter)
            )
            DestinationBar(modifier = Modifier.align(alignment = Alignment.TopCenter))
            CheckoutBar(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        }
    }
}

/**
 * This Composable displays the "current contents" of the cart (our [List] of [OrderLine] parameter
 * [orderLines]), a [SummaryItem] that displays the total price of the [Snack]'s in the cart, and
 * a [SnackCollection] displaying the [Snack]'s in our [SnackCollection] parameter [inspiredByCart].
 * We start by initializing our [Resources] variable `val resources` to the [Context.getResources]
 * of the current [LocalContext], then initialize and remember our [String] variable
 * `val snackCountFormattedString` to the [String] formed by using the format string with resource
 * ID `R.plurals.cart_order_count` to format the [List.size] of our [List] of [OrderLine] parameter
 * [orderLines] using the [Resources.getQuantityString] method of `resources`. Next we initialize
 * our [SpringSpec] of [Float] variable `val itemAnimationSpecFade` to a [nonSpatialExpressiveSpring],
 * and initialize our [SpringSpec] of [IntOffset] variable `val itemPlacementSpec` to a
 * [spatialExpressiveSpring].
 *
 * Our root Composable is a [LazyColumn] whose `modifier` argument is our [Modifier] parameter
 * [modifier] and in its [LazyListScope] `content` Composable lambda argument we have:
 *  - an [LazyListScope.item] whose key is the constant string "title", and in its [LazyItemScope]
 *  Composable lambda argument we have:
 *
 *  1. a [Spacer] whose `modifier` argument is a [Modifier.windowInsetsTopHeight] whose `insets`
 *  argument is the [WindowInsets.Companion.statusBars] with an additional [WindowInsets] whose
 *  `top` is 56.dp.
 *
 *  2. a [Text] whose `text` is the [String] that is formatted using the format [String] with resouce
 *  ID `R.string.cart_order_header` ("Order (%1s)") with our [String] variable `snackCountFormattedString`
 *  as the substituted value. The [TextStyle] `style` argument of the [Text] is the [Typography.titleLarge]
 *  of our custom [MaterialTheme.typography], its [Color] `color` argument is the [JetsnackColors.brand]
 *  of our custom [JetsnackTheme.colors], its `maxLines` argument is 1, its `overflow` argument is
 *  [TextOverflow.Ellipsis] (uses an ellipsis to indicate that the text has overflowed), and its
 *  `modifier` argument is a [Modifier.heightIn] whose `min` is 56.dp with a [Modifier.padding] chained
 *  to that which adds 24.dp to each `horizontal` side and 4.dp to each `vertical` side, and at the
 *  end of the chain is a [Modifier.wrapContentHeight] that allows it to measure at its desired height.
 *
 *  - Next in the [LazyColumn] is a [LazyListScope.items] whose `items` argument is our [List] of
 *  [OrderLine] parameter [orderLines], and whose `key` argument is the [Snack.id] of the
 *  [OrderLine.snack] that is currently being composed in its `itemContent` [LazyItemScope] composable
 *  lambda argument. In the `itemContent` [LazyItemScope] composable lambda argument we accept the
 *  [OrderLine] passed the lambda in our `orderLine` variable. Its root Composable is a
 *  [SwipeDismissItem] (Holds the Swipe to dismiss composable, its animation and the current state)
 *  whose `modifier` argument is a [LazyItemScope.animateItem] whose `fadeInSpec` argument is our
 *  [SpringSpec] of [Float] variable `itemAnimationSpecFade`, whose `fadeOutSpec` argument is our
 *  [SpringSpec] of [Float] variable `itemAnimationSpecFade`, and whose `placementSpec` argument is
 *  our [SpringSpec] of [IntOffset] variable `itemPlacementSpec`. The `background` argument is a
 *  lambda which accepts the [Float] it is passed in its `progress` variable then Composes our
 *  [SwipeDismissItemBackground] Composable with its `progress` argument the [Float] passed to the
 *  `progress` variable of the lambda. In the `content` Composable lambda argment of the
 *  [SwipeDismissItem] we have a [CartItem] whose `orderLine` argument is the current [OrderLine] in
 *  the `orderLine` variable passed by [LazyListScope.items] to its `itemContent` lambda argument,
 *  whose `removeSnack` argument is the [removeSnack] lambda parameter of [CartContent], whose
 *  `increaseItemCount` argument is the [increaseItemCount] lambda parameter of [CartContent], whose
 *  `decreaseItemCount` argument is the [decreaseItemCount] lambda parameter of [CartContent], and
 *  whose `onSnackClick` argument is the [onSnackClick] lambda parameter of [CartContent].
 *
 *  - Next in the [LazyColumn] is a [LazyListScope.item] whose `key` argument is the constant [String]
 *  "summary", and in its [LazyItemScope] `content` lambda argument we have a [SummaryItem] whose
 *  [Modifier] `modifier` argument is a [LazyItemScope.animateItem] whose `fadeInSpec` argument is
 *  our [SpringSpec] of [Float] variable `itemAnimationSpecFade`, whose `fadeOutSpec` argument is
 *  our [SpringSpec] of [Float] variable `itemAnimationSpecFade`, and whose `placementSpec` argument
 *  is our [SpringSpec] of [IntOffset] variable `itemPlacementSpec`. The [Long] `subtotal` argument
 *  is the [List.sumOf] the [Snack.price] of the [OrderLine.snack]'s times the [OrderLine.count] of
 *  all of the [OrderLine] in our [List] of [OrderLine] parameter [orderLines], and the `shippingCosts`
 *  argument is the constant 369.
 *
 *  - Last in the [LazyColumn] is a [LazyListScope.item] whose `key` argument is the constant [String]
 *  "inspiredByCart", and in its [LazyItemScope] `content` lambda argument we have:
 *
 *  1. a [SnackCollection] whose `modifier` argument is a [LazyItemScope.animateItem] whose
 *  `fadeInSpec` argument is our [SpringSpec] of [Float] variable `itemAnimationSpecFade`, whose
 *  `fadeOutSpec` argument is our [SpringSpec] of [Float] variable `itemAnimationSpecFade`, and
 *  whose `placementSpec` argument is our [SpringSpec] of [IntOffset] variable `itemPlacementSpec`.
 *  The `snackCollection` argument is the [SnackCollection] parameter [inspiredByCart] of [CartContent],
 *  the `onSnackClick` argument is the [onSnackClick] lambda parameter of [CartContent], and the
 *  `highlight` argument is `false`.
 *
 *  2. Below the [SnackCollection] is a [Spacer] whose `modifier` argument is a [Modifier.height]
 *  that set its `height` to 56.dp.
 *
 * @param orderLines the [List] of [OrderLine] that represents the [Snack]'s the user has "ordered"
 * and the number of them that they have ordered.
 * @param removeSnack a lambda that should be called with the [Snack.id] when the user indicates that
 * they wish to remove a [Snack] from their order.
 * @param increaseItemCount a lambda that should be called with the [Snack.id] when the user
 * indicates that they want to add 1 more to their order of a [Snack].
 * @param decreaseItemCount a lambda that should be called with the [Snack.id] when the user
 * indicates that they want to subtract 1 from their order of a [Snack].
 * @param inspiredByCart a [SnackCollection] that is "inspired" by the contents of the cart.
 * @param onSnackClick a lambda that should be called with the [Snack.id] of the [Snack] that the
 * user clicks on and a [String] describing the collection it belongs to.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [modifier] parameter traces back to [MainContainer] where a [Modifier.padding]
 * with the [PaddingValues] that are passed by [JetsnackScaffold] to its `content` lambda is
 * chained to a [Modifier.consumeWindowInsets] that consumes those [PaddingValues] as insets.
 */
@Composable
private fun CartContent(
    orderLines: List<OrderLine>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources: Resources = LocalContext.current.resources
    val snackCountFormattedString: String = remember(orderLines.size, resources) {
        resources.getQuantityString(
            R.plurals.cart_order_count,
            orderLines.size, orderLines.size
        )
    }
    val itemAnimationSpecFade: SpringSpec<Float> = nonSpatialExpressiveSpring()
    val itemPlacementSpec: SpringSpec<IntOffset> = spatialExpressiveSpring()
    LazyColumn(modifier = modifier) {
        item(key = "title") {
            Spacer(
                modifier = Modifier.windowInsetsTopHeight(
                    insets = WindowInsets.statusBars.add(insets = WindowInsets(top = 56.dp))
                )
            )
            Text(
                text = stringResource(R.string.cart_order_header, snackCountFormattedString),
                style = MaterialTheme.typography.titleLarge,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .wrapContentHeight()
            )
        }
        items(items = orderLines, key = { it.snack.id }) { orderLine: OrderLine ->
            SwipeDismissItem(
                modifier = Modifier.animateItem(
                    fadeInSpec = itemAnimationSpecFade,
                    fadeOutSpec = itemAnimationSpecFade,
                    placementSpec = itemPlacementSpec
                ),
                background = { progress: Float ->
                    SwipeDismissItemBackground(progress = progress)
                },
            ) {
                CartItem(
                    orderLine = orderLine,
                    removeSnack = removeSnack,
                    increaseItemCount = increaseItemCount,
                    decreaseItemCount = decreaseItemCount,
                    onSnackClick = onSnackClick
                )
            }
        }
        item("summary") {
            SummaryItem(
                modifier = Modifier.animateItem(
                    fadeInSpec = itemAnimationSpecFade,
                    fadeOutSpec = itemAnimationSpecFade,
                    placementSpec = itemPlacementSpec
                ),
                subtotal = orderLines.sumOf { it.snack.price * it.count },
                shippingCosts = 369
            )
        }
        item(key = "inspiredByCart") {
            SnackCollection(
                modifier = Modifier.animateItem(
                    fadeInSpec = itemAnimationSpecFade,
                    fadeOutSpec = itemAnimationSpecFade,
                    placementSpec = itemPlacementSpec
                ),
                snackCollection = inspiredByCart,
                onSnackClick = onSnackClick,
                highlight = false
            )
            Spacer(modifier = Modifier.height(height = 56.dp))
        }
    }
}

/**
 * This is used as the `background` argument of the [SwipeDismissItem] used by [CartContent], which
 * uses it as the `backgroundContent` composable lambda argument of the [SwipeToDismissBox] it uses
 * (composable that is stacked behind the content and is exposed when the content is swiped). Our
 * root Composable is a [Column] whose `modifier` argument is a [Modifier.background] whose `color`
 * [Color] argument is the [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors] (draws
 * a default [RectangleShape] in the [Color] `color` behind the `content`), with a
 * [Modifier.fillMaxWidth], and a [Modifier.fillMaxHeight] chained to that (which causes it to
 * occupy its entire incoming size constraints). The `horizontalAlignment` argument of the [Column]
 * is [Alignment.End] (aligns its children to its end), and the `verticalArrangement` is
 * [Arrangement.Center] (the children are vertically centered in their allotted spaces). In the
 * [ColumnScope] `content` Composable lambda argument we start by initializing our animated [Dp]
 * variable `val padding` using [animateDpAsState] with the [Dp] `targetValue` 4.dp for our [Float]
 * parameter [progress] less than 0.5f, and 0.dp if it is greater than or equal to 0.5f. The root
 * Composable is a [BoxWithConstraints] whose `modifier` argument is a [Modifier.fillMaxWidth] whose
 * `fraction` argument is our [Float] parameter [progress] (sets the minimum width and the maximum
 * width to be equal to the incoming maximum width constraint multiplied by [progress]). In the
 * [BoxWithConstraintsScope] `content` Composable lambda argument we have a [Surface] whose `modifier`
 * argument is a [Modifier.padding] that adds our animated [Dp] variable `padding` to `all` or its
 * sides, with a [Modifier.fillMaxWidth] chained to that which causes it to occupy its entire incoming
 * width constraint, with a [Modifier.height] chained to that which sets its `height` to the
 * [BoxWithConstraintsScope.maxWidth] of the [BoxWithConstraints] (for some reason?), and with a
 * [BoxWithConstraintsScope.align] chained to that which aligns the [Surface] to the `alignment`
 * [Alignment.Center]. The `shape` argument of the [Surface] is a [RoundedCornerShape] whose `percent`
 * (Size in percents to apply) is 1 minus our [Float] parameter [progress] times 100 rounded to an
 * [Int], and its [Color] `color` argument is the [JetsnackColors.error] of our custom
 * [JetsnackTheme.colors].
 *
 * The `content` Composable lambda argument of the [Surface] holds a [Box] whose `modifier` argument
 * is a [Modifier.fillMaxSize] that causes it to occupy its entire incoming size constraint, and its
 * `contentAlignment` argument is an [Alignment.Center] that centers its children to the center of the
 * [Box]. In its [BoxScope] `content` Composable lambda argument we check if our [Float] parameter
 * [progress] is in the width range of `[0.125 .. 0.475]` and if it is we initialize our [State]
 * wrapped animated [Float] variable `val iconAlpha` to the value that the [animateFloatAsState]
 * method returns for a `targetValue` argument which is 0.5f for [progress] greater than 0.4f or
 * else 1f, when we Compose an [Icon] whose `imageVector` argument is the [ImageVector] drawn by
 * [Icons.Filled.DeleteForever], whose `modifier` argument is a [Modifier.size] that sets its `size`
 * to 32.dp, with a [Modifier.graphicsLayer] chained to that that sets its `alpha` to our [State]
 * wrapped animated [Float] variable `iconAlpha`. The [Color] `tint` argument of the [Icon] is the
 * [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors], and the `contentDescription`
 * argment is `null`. Next we initialize our [State] wrapped animated [Float] variable `val textAlpha`
 * to the value that the [animateFloatAsState] method returns for `targetValue` argument which is
 * 1f for [progress] greater than 0.5f or else 0.5f. Then if [progress] is greater than 0.5f we
 * compose a [Text] whose `text` is the [String] with resource `id` `R.string.remove_item`
 * ("Remove Item"), whose [TextStyle] `style` argument is the [Typography.titleMedium] of our custom
 * [MaterialTheme.typography], whose [Color] `color` argument is the [JetsnackColors.uiBackground]
 * of our custom [JetsnackTheme.colors], its `textAlign` argument is [TextAlign.Center] (Aligns the
 * text in the center of the container), and its `modifier` argument is a [Modifier.graphicsLayer]
 * the sets its `alpha` to our [State] wrapped animated [Float] variable `textAlpha`.
 *
 * @param progress this is the [SwipeToDismissBoxState.progress] of the [SwipeToDismissBox] that is
 * being dismissed.
 */
@Composable
private fun SwipeDismissItemBackground(progress: Float) {
    Column(
        modifier = Modifier
            .background(color = JetsnackTheme.colors.uiBackground)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        // Set 4.dp padding only if progress is less than halfway
        val padding: Dp by animateDpAsState(
            if (progress < 0.5f) 4.dp else 0.dp, label = "padding"
        )
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(fraction = progress)
        ) {
            Surface(
                modifier = Modifier
                    .padding(all = padding)
                    .fillMaxWidth()
                    .height(height = maxWidth)
                    .align(alignment = Alignment.Center),
                shape = RoundedCornerShape(percent = ((1 - progress) * 100).roundToInt()),
                color = JetsnackTheme.colors.error
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Icon must be visible while in this width range
                    if (progress in 0.125f..0.475f) {
                        // Icon alpha decreases as it is about to disappear
                        val iconAlpha: Float by animateFloatAsState(
                            if (progress > 0.4f) 0.5f else 1f, label = "icon alpha"
                        )

                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            modifier = Modifier
                                .size(size = 32.dp)
                                .graphicsLayer(alpha = iconAlpha),
                            tint = JetsnackTheme.colors.uiBackground,
                            contentDescription = null,
                        )
                    }
                    /*Text opacity increases as the text is supposed to appear in
                                    the screen*/
                    val textAlpha: Float by animateFloatAsState(
                        if (progress > 0.5f) 1f else 0.5f, label = "text alpha"
                    )
                    if (progress > 0.5f) {
                        Text(
                            text = stringResource(id = R.string.remove_item),
                            style = MaterialTheme.typography.titleMedium,
                            color = JetsnackTheme.colors.uiBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .graphicsLayer(
                                    alpha = textAlpha
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays the information stored in its [OrderLine] parameter [orderLine]. We start by initializing
 * our [Snack] variable `val snack` to the [OrderLine.snack] property of our [OrderLine] parameter
 * [orderLine]. The our root Composable is a [ConstraintLayout] whose `modifier` chains a
 * [Modifier.fillMaxWidth] to our [Modifier] parameter [modifier], followed by a [Modifier.clickable]
 * whose `onClick` lambda argument is a lambda that calls our [onSnackClick] lambda parameter with
 * the [Snack.id] of our [Snack] variable `snack` and the constant [String] "cart", this is followed
 * by a [Modifier.background] that sets the background [Color] of the [ConstraintLayout] to the
 * [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors], and that is followed by a
 * [Modifier.padding] that sets the padding of the `horizontal` sides to 24.dp.
 *
 * In the [ConstraintLayoutScope] `content` lambda argument of the [ConstraintLayout] we start by
 * initializing our [ConstrainedLayoutReference] variables `divider`, `image`, `name`, `tag`,
 * `priceSpacer`, `price`, `remove`, and `quantity` to the values returned by the
 * [ConstraintLayoutScope.createRef] method. Then we use the [ConstraintLayoutScope.createVerticalChain]
 * method to create a vertical chain from `name`, `tag`, `priceSpacer`, and `price` using the
 * `chainStyle` argument [ChainStyle.Packed] (chain style where the contained layouts are packed
 * together and placed to the center of the available space).
 *
 * The first child of the [ConstraintLayout] is a [SnackImage] whose `imageRes` argument is the
 * [Snack.imageRes] property of our [Snack] variable `snack`, whose `contentDescription` argument is
 * `null`, and whose `modifier` argument is a [Modifier.size] that sets its size to 100.dp, with a
 * [ConstraintLayoutScope.constrainAs] chained to that which constrains the [SnackImage] as
 * [ConstrainedLayoutReference] `ref` argument `image`, and in its [ConstrainScope] `constrainBlock`
 * it constrains its `top` `linkTo` to the `anchor` of its `parent.top` with a `margin` of 16.dp,
 * constrains its `bottom` `linkTo` to the `anchor` of its `parent.bottom` with a `margin` of 16.dp,
 * and  constrains its `start` `linkTo` to the `anchor` of its `parent.start`.
 *
 * The second child of the [ConstraintLayout] is a [Text] whose `text` argument is the [Snack.name]
 * property of our [Snack] variable `snack`, whose [TextStyle] `style` argument is the
 * [Typography.titleMedium] of our custom [MaterialTheme.typography], whose [Color] `color` argument
 * is the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors], and its `modifier`
 * argument is a [ConstraintLayoutScope.constrainAs] which constrains the [Text] as
 * [ConstrainedLayoutReference] `ref` argument `name`, and in its [ConstrainScope] `constrainBlock`
 * it uses the [ConstrainScope.linkTo] method to constain its `start` to the `image.end` of the
 * [SnackImage] with a `startMargin` of 16.dp, to constain its `end` to the `remove.start` (`remove`
 * is used for the third child, an [IconButton]) with an `endMargin` of 16.dp, and the `bias`
 * argument of the [ConstrainScope.linkTo] is 0f (this is the "hRtlBias" horizontal bias of the
 * Constraint).
 *
 * The third child of the [ConstraintLayout] is an [IconButton] whose `onClick` lambda argument is
 * a lambda that calls our [removeSnack] lambda parameter with the [Snack.id] of our `snack` variable,
 * and whose `modifier` argument is a [ConstraintLayoutScope.constrainAs] which constrains the
 * [IconButton] as [ConstrainedLayoutReference] `ref` argument `remove`, and in its [ConstrainScope]
 * `constrainBlock` it constrains its `top` `linkTo` to the `anchor` of its `parent.top`, and its
 * `end` `linkTo` to the `anchor` of its `parent.end`, and to that [Modifier] is chained a
 * [Modifier.padding] that adds 12.dp to its `top` margin. In its `content` Composable lambda argument
 * is an [Icon] whose `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Close] (an
 * "X" character), whose `tint` argument is the [JetsnackColors.iconSecondary] of our custom
 * [JetsnackTheme.colors], and its `contentDescription` argument is the [String] with resource ID
 * `R.string.label_remove` ("Remove item").
 *
 * The fourth child of the [ConstraintLayout] is a [Text] whose `text` argument is the [Snack.tagline]
 * property of our [Snack] variable `snack`, whose [TextStyle] `style` argument is the
 * [Typography.bodyLarge] of our custom [MaterialTheme.typography], whose [Color] `color` argument
 * is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors], and its `modifier` argument
 * is a [ConstraintLayoutScope.constrainAs] which constrains the [Text] as [ConstrainedLayoutReference]
 * `ref` argument `tag`, and in its [ConstrainScope] `constrainBlock` it uses the [ConstrainScope.linkTo]
 * method to constain its `start` to the `image.end` of the [SnackImage] with a `startMargin` of 16.dp,
 * to constain its `end` to its `parent.end`, with an `endMargin` of 16.dp, and the `bias` argument
 * of the [ConstrainScope.linkTo] is 0f (this is the "hRtlBias" horizontal bias of the Constraint).
 *
 * The fifth child of the [ConstraintLayout] is a [Spacer] whose `modifier` argument is a [Modifier.height]
 * that sets its `height` to 8.dp, with a [ConstraintLayoutScope.constrainAs] which constrains the [Spacer]
 * as [ConstrainedLayoutReference] `ref` argument `priceSpacer`, and in its [ConstrainScope] `constrainBlock`
 * it uses the [ConstrainScope.linkTo] to constrain its `top` to the `tag.bottom` of our, and its `bottom`
 * to the `price.top`.
 *
 * The sixth child of the [ConstraintLayout] is a [Text] whose `text` argument is the [String] returned
 * by the [formatPrice] method when its argument is the [Snack.price] of our [Snack] variable `snack`,
 * whose [TextStyle] `style` argument is the [Typography.titleMedium] of our custom [MaterialTheme.typography],
 * whose [Color] `color` argument is the [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors],
 * and whose `modifier` argument is a [ConstraintLayoutScope.constrainAs] which constrains the [Text] as
 * [ConstrainedLayoutReference] `ref` argument `price`, and in its [ConstrainScope] `constrainBlock` it uses
 * the [ConstrainScope.linkTo] method to constain its `start` to the `image.end` of the [SnackImage],
 * to constrain its `end` to the `quantity.start`, with a `startMargin` of 16.dp and an `endMargin`
 * of 16.dp, and the `bias` argument of the [ConstrainScope.linkTo] is 0f (this is the "hRtlBias"
 * horizontal bias of the Constraint).
 *
 * The seventh child of the [ConstraintLayout] is a [QuantitySelector] whose `count` argument is the
 * [OrderLine.count] of our [OrderLine] parameter [orderLine], the `decreaseItemCount` argument is a
 * lambda that calls our lambda parameter [decreaseItemCount] with the [Snack.id] of our [Snack]
 * variable `snack`, the `increaseItemCount` argument is a lambda that calls our lambda parameter
 * [increaseItemCount] with the [Snack.id] of our [Snack], and the `modifier` argument is a
 * [ConstraintLayoutScope.constrainAs] which constrains the [QuantitySelector] as [ConstrainedLayoutReference]
 * `ref` argument `quantity`, and in its [ConstrainScope] `constrainBlock` it constrains its `baseline.linkTo`
 * to the `anchor` of the `price.baseline`, and its constrains its `end.linkTo` to the `anchor` of
 * its `parent.end``ref` argument `divider`, and in its [ConstrainScope] `constrainBlock`
 *
 * The eighth child of the [ConstraintLayout] is a [JetsnackDivider] whose `modifier` argument is a
 * [ConstraintLayoutScope.constrainAs] which constrains the [JetsnackDivider] as [ConstrainedLayoutReference]
 * it uses the [ConstrainScope.linkTo] method to constain its `start` to the `parent.start` and its
 * `end` to the `parent.end`, then constrains its `top.linkTo` to the `anchor` of its `parent.bottom`.
 *
 * @param orderLine the [OrderLine] whose information we are to display.
 * @param removeSnack a lambda that should be called with the [Snack.id] of the [OrderLine.snack]
 * of [orderLine] when the user indicates that they wish to remove the [Snack] from their order.
 * @param increaseItemCount a lambda that should be called with the [Snack.id] of the [OrderLine.snack]
 * of [orderLine] when the user indicates that they want to add 1 more to their order of a [Snack].
 * @param decreaseItemCount a lambda that should be called with the [Snack.id] of the [OrderLine.snack]
 * of [orderLine] when the user indicates that they want to subtract 1 from their order of a [Snack].
 * @param onSnackClick a lambda that should be called with the [Snack.id] of the [OrderLine.snack]
 * of [orderLine] that the user clicks on and a [String] describing the collection it belongs to.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
fun CartItem(
    orderLine: OrderLine,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snack: Snack = orderLine.snack
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id, "cart") }
            .background(color = JetsnackTheme.colors.uiBackground)
            .padding(horizontal = 24.dp)

    ) {
        val (divider: ConstrainedLayoutReference,
            image: ConstrainedLayoutReference,
            name: ConstrainedLayoutReference,
            tag: ConstrainedLayoutReference,
            priceSpacer: ConstrainedLayoutReference,
            price: ConstrainedLayoutReference,
            remove: ConstrainedLayoutReference,
            quantity: ConstrainedLayoutReference) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        SnackImage(
            imageRes = snack.imageRes,
            contentDescription = null,
            modifier = Modifier
                .size(size = 100.dp)
                .constrainAs(ref = image) {
                    top.linkTo(anchor = parent.top, margin = 16.dp)
                    bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                    start.linkTo(anchor = parent.start)
                }
        )
        Text(
            text = snack.name,
            style = MaterialTheme.typography.titleMedium,
            color = JetsnackTheme.colors.textSecondary,
            modifier = Modifier.constrainAs(ref = name) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = remove.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        IconButton(
            onClick = { removeSnack(snack.id) },
            modifier = Modifier
                .constrainAs(ref = remove) {
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                }
                .padding(top = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                tint = JetsnackTheme.colors.iconSecondary,
                contentDescription = stringResource(id = R.string.label_remove)
            )
        }
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.bodyLarge,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(ref = tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = parent.end,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Spacer(
            modifier = Modifier
                .height(height = 8.dp)
                .constrainAs(ref = priceSpacer) {
                    linkTo(top = tag.bottom, bottom = price.top)
                }
        )
        Text(
            text = formatPrice(price = snack.price),
            style = MaterialTheme.typography.titleMedium,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.constrainAs(ref = price) {
                linkTo(
                    start = image.end,
                    end = quantity.start,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        QuantitySelector(
            count = orderLine.count,
            decreaseItemCount = { decreaseItemCount(snack.id) },
            increaseItemCount = { increaseItemCount(snack.id) },
            modifier = Modifier.constrainAs(ref = quantity) {
                baseline.linkTo(anchor = price.baseline)
                end.linkTo(anchor = parent.end)
            }
        )
        JetsnackDivider(
            modifier = Modifier.constrainAs(ref = divider) {
                linkTo(start = parent.start, end = parent.end)
                top.linkTo(anchor = parent.bottom)
            }
        )
    }
}

/**
 * This Composable is used to display the total price of the [Snack]'s in the cart. Our root Composable
 * is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier]. In its [ColumnScope]
 * `content` Composable lambda argument we have:
 *
 *  - a [Text] whose `text` argument is the [String] with resource ID `R.string.cart_summary_header`
 *  ("Summary"), whose [TextStyle] `style` argument is the [Typography.titleLarge] of our custom
 *  [MaterialTheme.typography], whose [Color] `color` argument is the [JetsnackColors.brand] of our
 *  custom [JetsnackTheme.colors], whose `maxLines` argument is 1, whose `overflow` argument is
 *  [TextOverflow.Ellipsis], and whose `modifier` argument is a [Modifier.padding] that adds 24.dp
 *  to each `horizontal` size of the [Text], chained to a [Modifier.heightIn] whose `min` is 56.dp,
 *  and at the end of the chain is a [Modifier.wrapContentHeight] (allows it to measure at its desired
 *  height without regard for the incoming measurement minimum height).
 *
 *  - a [Row] whose `modifier` argument is a [Modifier.padding] that adds 24.dp to each `horizontal`
 *  side of the [Row]. In its [RowScope] `content` lambda argument we have:
 *  - a [Text] whose `text` argument is the [String] with resource ID `R.string.cart_subtotal_label`
 *  ("Subtotal") whose [TextStyle] `style` argument is the [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography], and whose `modifier` argument is a [RowScope.weight] with its `weight`
 *  argument 1f (causes the [Text] to occupy all remaining incoming width constraint after its siblings
 *  are measured and placed), chained to a [Modifier.wrapContentWidth] whose `align` argument is
 *  [Alignment.Start] causing it to align to the start, and with an [RowScope.alignBy] whose
 *  `alignmentLine` argument is [LastBaseline] (Positions the element vertically such that its
 *  alignmentLine aligns with sibling elements aligned to the the baseline of their last line).
 *  - a [Text] whose `text` argument is the [String] returned by our [formatPrice] method for the
 *  `price` argument of our [Long] parameter [subtotal], whose [TextStyle] `style` argument is the
 *  [Typography.bodyLarge] of our custom [MaterialTheme.typography], and whose `modifier` argument
 *  is a [RowScope.alignBy] whose `alignmentLine` argument is [LastBaseline] (Positions the element
 *  vertically such that its alignmentLine aligns with sibling elements aligned to the the baseline
 *  of their last line).
 *
 *  - a [Row] whose `modifier` argument is a [Modifier.padding] that adds 24.dp to each `horizontal`
 *  and 8.dp to each `vertical` side. In its [RowScope] `content` lambda argument we have:
 *  - a [Text] whose `text` argument is the [String] with resource ID `R.string.cart_shipping_label`
 *  ("Shipping &amp; Handling") whose [TextStyle] `style` argument is the [Typography.bodyLarge] of
 *  our custom [MaterialTheme.typography], and whose `modifier` argument is a [RowScope.weight] with
 *  its `weight` argument 1f (causes the [Text] to occupy all remaining incoming width constraint
 *  after its siblings are measured and placed), chained to a [Modifier.wrapContentWidth] whose
 *  `align` argument is [Alignment.Start] causing it to align to the start, and with an [RowScope.alignBy]
 *  whose `alignmentLine` argument is [LastBaseline] (Positions the element vertically such that its
 *  alignmentLine aligns with sibling elements aligned to the the baseline of their last line).
 *  - a [Text] whose `text` argument is the [String] returned by our [formatPrice] method for the
 *  `price` argument of our [Long] parameter [shippingCosts], whose [TextStyle] `style` argument is
 *  the [Typography.bodyLarge] of our custom [MaterialTheme.typography], and whose `modifier` argument
 *  is a [RowScope.alignBy] whose `alignmentLine` argument is [LastBaseline] (Positions the element
 *  vertically such that its alignmentLine aligns with sibling elements aligned to the the baseline
 *  of their last line).
 *
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] that sets its `height` to 8.dp
 *
 *  - a [JetsnackDivider] (Our custom colored `HorizontalDivider`)
 *
 *  - a [Row] whose `modifier` argument is a [Modifier.padding] that adds 24.dp to each `horizontal`
 *  and 8.dp to each `vertical` side. In its [RowScope] `content` lambda argument we have:
 *  - a [Text] whose `text` argument is the [String] with resource ID `R.string.cart_total_label`
 *  ("Total") whose [TextStyle] `style` argument is the [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography], and whose `modifier` argument is a [RowScope.weight] with its `weight`
 *  argument 1f (causes the [Text] to occupy all remaining incoming width constraint after its
 *  siblings are measured and placed), chained to a [Modifier.padding] that adds 16.dp padding to its
 *  `end`, chained to a [Modifier.wrapContentWidth] whose `align` argument is [Alignment.End] causing
 *  it to align to the end, and with an [RowScope.alignBy] whose `alignmentLine` argument is
 *  [LastBaseline] (Positions the element vertically such that its alignmentLine aligns with sibling
 *  elements aligned to the the baseline of their last line).
 *  - a [Text] whose `text` argument is the [String] returned by our [formatPrice] method for the
 *  `price` argument of our [Long] parameter [subtotal] plus our [Long] parameter [shippingCosts],
 *  whose [TextStyle] `style` argument is the [Typography.titleMedium] of our custom
 *  [MaterialTheme.typography], and whose `modifier` argument is a [RowScope.alignBy] whose
 *  `alignmentLine` argument is [LastBaseline] (Positions the element vertically such that its
 *  alignmentLine aligns with sibling elements aligned to the the baseline of their last line).
 *
 *  - and at the bottom of the [Column] a [JetsnackDivider] (Our custom colored `HorizontalDivider`)
 *
 * @param subtotal the total of the [Snack.price] of the [OrderLine.snack] times its [OrderLine.count]
 * of all the [OrderLine] in our cart.
 * @param shippingCosts the shipping cost. Our caller passes us the constant 369L
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [CartContent] passes us a [LazyItemScope.animateItem] whose `fadeInSpec` and
 * `fadeOutSpec` arguments a [SpringSpec] of [Float], and whose `placementSpec` argument is a
 * [SpringSpec] of [IntOffset].
 */
@Composable
fun SummaryItem(
    subtotal: Long,
    shippingCosts: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.cart_summary_header),
            style = MaterialTheme.typography.titleLarge,
            color = JetsnackTheme.colors.brand,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .heightIn(min = 56.dp)
                .wrapContentHeight()
        )
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(id = R.string.cart_subtotal_label),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(weight = 1f)
                    .wrapContentWidth(align = Alignment.Start)
                    .alignBy(alignmentLine = LastBaseline)
            )
            Text(
                text = formatPrice(price = subtotal),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.alignBy(alignmentLine = LastBaseline)
            )
        }
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(id = R.string.cart_shipping_label),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(weight = 1f)
                    .wrapContentWidth(align = Alignment.Start)
                    .alignBy(alignmentLine = LastBaseline)
            )
            Text(
                text = formatPrice(price = shippingCosts),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.alignBy(alignmentLine = LastBaseline)
            )
        }
        Spacer(modifier = Modifier.height(height = 8.dp))
        JetsnackDivider()
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(id = R.string.cart_total_label),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(end = 16.dp)
                    .wrapContentWidth(align = Alignment.End)
                    .alignBy(alignmentLine = LastBaseline)
            )
            Text(
                text = formatPrice(price = subtotal + shippingCosts),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alignBy(alignmentLine = LastBaseline)
            )
        }
        JetsnackDivider()
    }
}

/**
 * This Composable holds a "Checkout" [JetsnackButton] which is displayed at the bottom of the [Cart]
 * (currently does nothing). Our root Composable is a [Column] whose `modifier` argument is a
 * [Modifier.background] whose [Color] `color` argument is a copy of the [JetsnackColors.uiBackground]
 * with its `alpha` set to [AlphaNearOpaque] (0.95f). In the `content` [ColumnScope] Composable lambda
 * of the [Column] we have:
 *
 *  - a [JetsnackDivider] (our custom [HorizontalDivider])
 *
 *  - a [Row] which has in its `content` [RowScope] Composable lambda argument:
 *  - a [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` argument of 1f causes
 *  it to take up all remaining width of the incoming constraints after its unweighted siblings are
 *  measured and placed (since its [JetsnackButton] sibling has a `weight` of 1f also, they share
 *  the [Row] width equally).
 *  - a [JetsnackButton] whose `onClick` argument is a do-nothing lambda, whose `shape` argument is
 *  a [RectangleShape] and whose `modifier` argument is a [Modifier.padding] that adds 12.dp padding
 *  to each of its `horizontal` sides and 8.dp to its `vertical` sides, chained to a [RowScope.weight]
 *  of `weight` 1f that will cause it to share the horizontal size of the [Row] with the [Spacer].
 *  In the `content` [RowScope] Composable lambda argument of the [JetsnackButton] is a [Text] whose
 *  `text` argument is the [String] with resource `id` `R.string.cart_checkout` ("Checkout"), whose
 *  `modifier` argument is a [Modifier.fillMaxWidth] that causes it occupy its entire incoming width
 *  constraint, its `textAlign` argument is [TextAlign.Left] (aligning its text to the left), and its
 *  `maxLines` argument is 1.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [CartContent] passes us a a [BoxScope.align] whose `alignment` is a
 * [Alignment.BottomCenter] that aligns is to the bottom center of its [Box] (overlayed on top of
 * the [CartContent]).
 */
@Composable
private fun CheckoutBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(
            color = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    ) {

        JetsnackDivider()
        Row {
            Spacer(modifier = Modifier.weight(weight = 1f))
            JetsnackButton(
                onClick = { /* todo */ },
                shape = RectangleShape,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.cart_checkout),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Three previews of our [Cart] Composable.
 */
@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun CartPreview() {
    JetsnackTheme {
        Cart(
            orderLines = SnackRepo.getCart(),
            removeSnack = {},
            increaseItemCount = {},
            decreaseItemCount = {},
            inspiredByCart = SnackRepo.getInspiredByCart(),
            onSnackClick = { _, _ -> }
        )
    }
}
