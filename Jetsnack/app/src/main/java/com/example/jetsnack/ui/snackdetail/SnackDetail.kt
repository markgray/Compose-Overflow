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

@file:OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.example.jetsnack.ui.snackdetail

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHostController
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope
import com.example.jetsnack.ui.SnackSharedElementKey
import com.example.jetsnack.ui.SnackSharedElementType
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackPreviewWrapper
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.navigation.MainDestinations
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Neutral8
import com.example.jetsnack.ui.utils.formatPrice
import kotlin.math.max
import kotlin.math.min

/**
 * The height of the [CartBottomBar] (the [JetsnackBottomBar] uses `BottomNavHeight` which is equal
 * as it needs to be for the shared transition to work properly).
 */
private val BottomBarHeight = 56.dp

/**
 * The height of the [Title] Composable.
 */
private val TitleHeight = 128.dp

/**
 * The height of a [Spacer] in the [Body] Composable that compensates for part of the Gradient in
 * [Spacer] at the top of the [Header] (I think?)
 */
private val GradientScroll = 180.dp

/**
 * The height of a [Spacer] in the [Body] Composable that compensates for the [Image] (I think?)
 */
private val ImageOverlap = 115.dp

/**
 * The minimum offset from the top of the screen of the [Title] Composable when the
 * [CollapsingImageLayout] is collapsed (I think?)
 */
private val MinTitleOffset = 56.dp

/**
 * The minimum offset from the top of the screen of the [Image] Composable when the
 * [CollapsingImageLayout] is collapsed (I think?)
 */
private val MinImageOffset = 12.dp

/**
 * The maximum offset from the top of the screen of the [Title] Composable when the
 * [CollapsingImageLayout] is expanded (`351.dp`) (I think?)
 */
private val MaxTitleOffset = ImageOverlap + MinTitleOffset + GradientScroll

/**
 * The maximum size of the [Image] Composable when the [CollapsingImageLayout] is expanded.
 */
private val ExpandedImageSize = 300.dp

/**
 * The minimum size of the [Image] Composable when the [CollapsingImageLayout] is collapsed.
 */
private val CollapsedImageSize = 150.dp

/**
 * The [Modifier.padding] for the `horizontal` edges of the many of our composables.
 */
private val HzPadding = Modifier.padding(horizontal = 24.dp)

/**
 * The [FiniteAnimationSpec] that is used for animations throughout the app for the animation of
 * coordinate values. It is a [SpringSpec] whose `dampingRatio` is set to 0.8f, and whose `stiffness`
 * is set to 380f.
 */
fun <T> spatialExpressiveSpring(): SpringSpec<T> = spring(
    dampingRatio = 0.8f,
    stiffness = 380f
)

/**
 * The [FiniteAnimationSpec] that is used for animations throughout the app for the animation of
 * non-spatial values (such as [fadeIn] and [fadeOut]). It is a [SpringSpec] whose `dampingRatio`
 * is set to `1f`, and `stiffness` is set to `1600f`.
 */
fun <T> nonSpatialExpressiveSpring(): SpringSpec<T> = spring(
    dampingRatio = 1f,
    stiffness = 1600f
)

/**
 * This is used as the `boundsTransform` argument of the [Modifier] extension function
 * [SharedTransitionScope.sharedBounds] that is used for shared element transitions for
 * composables throughout the app. It defines the animation spec used to animate from
 * initial bounds to the target bounds to be our global [SpringSpec] property
 * [spatialExpressiveSpring]
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val snackDetailBoundsTransform: BoundsTransform = BoundsTransform { _, _ ->
    spatialExpressiveSpring()
}

/**
 * This screen is navigated to when the user clicks on a [Snack] that is displayed in one of the
 * other screens. Our route is [MainDestinations.SNACK_DETAIL_ROUTE], and the method
 * [com.example.jetsnack.ui.navigation.JetsnackNavController.navigateToSnackDetail] is used to form
 * the URL from the [Snack.id] it is passed in its `snackId` parameter and the `orgin` [String]
 * identifying the shared transition to be used, these then become our [snackId] and [origin]
 * parameters respectively after it calls the [NavHostController.navigate] method with the URL as
 * its `route` argument.
 *
 * We start by initializing and remembering our [Snack] variable `val snack` to the [Snack] that the
 * [SnackRepo.getSnack] method returns when it searches for the [Snack] whose [Snack.id] is equal to
 * our [Long] parameter [snackId] (the `key1` argument of the [remember] is [snackId] which will
 * cause the `snack` variable to be refreshed if [snackId] changes value). Next we initialize and
 * remember our [List] of [SnackCollection] variable `val related` to the [List] of [SnackCollection]
 * that the [SnackRepo.getRelated] method returns for our [Long] parameter [snackId] (also using
 * [snackId] as its `key1` argument).
 *
 * We initialize our [SharedTransitionScope] variable `val sharedTransitionScope` to the `current`
 * [LocalSharedTransitionScope] (or throw [IllegalStateException] if that is `null`), and we
 * initialize our [AnimatedVisibilityScope] variable `val animatedVisibilityScope` to the `current`
 * [LocalNavAnimatedVisibilityScope] (or throw [IllegalStateException] if that is `null`). We use
 * our [AnimatedVisibilityScope] variable `animatedVisibilityScope` to create a [Transition] whose
 * [Transition.animateDp] method we use to initialize our animated [Dp] variable `val roundedCornerAnim`
 * to an instance whose size for the different [EnterExitState] `targetValueByState` are:
 *  - [EnterExitState.PreEnter] -> `20.dp` The initial state of a custom enter animation in
 *  [AnimatedVisibility]
 *  - [EnterExitState.Visible] -> `0.dp` The Visible state is the target state of a custom enter
 *  animation, also the initial state of a custom exit animation in [AnimatedVisibility].
 *  - [EnterExitState.PostExit] -> `20.dp` Target state of a custom exit animation in
 *  [AnimatedVisibility].
 *
 * Then `with` [SharedTransitionScope] variable `sharedTransitionScope` as the receiver we execute
 * a `block` containing a [Box] whose `modifier` argument is a [Modifier.clip] whose `shape` argument
 * is a [RoundedCornerShape] whose `size` argument is our animated [Dp] variable `roundedCornerAnim`,
 * to this is chained a [SharedTransitionScope.sharedBounds] whose `sharedContentState` argument is
 * a remembered [SharedContentState] whose `key` argument is a [SnackSharedElementKey] whose `snackId`
 * argument is our [Long] parameter [snackId] and whose `origin` argument is our [String] parameter
 * [origin], and whose `type` argument is [SnackSharedElementType.Bounds], the `animatedVisibilityScope`
 * argument of the [SharedTransitionScope.sharedBounds] is our [AnimatedVisibilityScope] variable
 * `animatedVisibilityScope`, the `clipInOverlayDuringTransition` argument is an [OverlayClip] whose
 * `clipShape` argument is a [RoundedCornerShape] whose `size` argument is our animated [Dp] variable
 * `roundedCornerAnim`, the `boundsTransform` argument is our [BoundsTransform] property
 * [snackDetailBoundsTransform], its `exit` argument is a [fadeOut] whose `animationSpec` argument
 * is our [FiniteAnimationSpec] property [nonSpatialExpressiveSpring], and its `enter` argument is a
 * [fadeIn] whose `animationSpec` argument is our [FiniteAnimationSpec] property
 * [nonSpatialExpressiveSpring]. Next in the [Modifier] chain is a [Modifier.fillMaxSize] that causes
 * the [Box] to occupy its entire incoming size constraints, with [Modifier.background] at the end
 * of the chain that sets the [Color] `color` of the background to the [JetsnackColors.uiBackground]
 * of our custom [JetsnackTheme.colors].
 *
 * In the [BoxScope] `content` lambda argument of the [Box] we initialize and remember our [ScrollState]
 * variable `val scroll` to a [rememberScrollState] whose `initial` argument is `0` (the initial
 * position to start with), then we compose:
 *  - a [Header] Composable whose `snackId` argument is our [Long] parameter [snackId], and whose
 *  `origin` argument is our [String] parameter [origin].
 *  - a [Body] Composable whose `related` argument is our [List] of [SnackCollection] variable
 *  `related`, and whose `scroll` argument is our [ScrollState] variable `scroll`.
 *  - a [Title] Composable whose `snack` argument is our [Snack] variable `snack`, whose `origin`
 *  argument is our [String] parameter [origin], and whose `scrollProvider` lambda argument is a
 *  lambda that returns the [ScrollState.value] of our [ScrollState] variable `scroll`.
 *  - an [Image] Composable whose `snackId` argument is our [Long] parameter [snackId], whose `origin`
 *  argument is our [String] parameter [origin], whose `imageRes` argument is the [Snack.imageRes]
 *  of our [Snack] variable `snack`, and whose `scrollProvider` lambda argument is a lambda that
 *  returns the [ScrollState.value] of our [ScrollState] variable `scroll`.
 *  - an [Up] Composable whose `upPress` argument is our lambda parameter [upPress] (which traces
 *  back to a lambda that calls the [NavHostController.navigateUp] method of our [NavHostController]).
 *  - a [CartBottomBar] whose `modifier` argument is a [BoxScope.align] whose `alignment` argument
 *  is [Alignment.BottomCenter] to align it to the bottom center of the [Box].
 *
 * @param snackId the [Snack.id] of the [Snack] that should be displayed on the screen.
 * @param origin a [String] that is used to idenify the shared transition that is to be used to
 * transition to this screen.
 * @param upPress a lambda that should be called when the user presses the "Up" button.
 */
@Composable
fun SnackDetail(
    snackId: Long,
    origin: String,
    upPress: () -> Unit
) {
    val snack: Snack = remember(key1 = snackId) { SnackRepo.getSnack(snackId = snackId) }
    val related: List<SnackCollection> = remember(key1 = snackId) { SnackRepo.getRelated(snackId) }
    val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No Scope found")
    val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No Scope found")
    val roundedCornerAnim: Dp by animatedVisibilityScope.transition
        .animateDp(label = "rounded corner") { enterExit: EnterExitState ->
            when (enterExit) {
                EnterExitState.PreEnter -> 20.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> 20.dp
            }
        }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(size = roundedCornerAnim))
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = snack.id,
                            origin = origin,
                            type = SnackSharedElementType.Bounds
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(roundedCornerAnim)),
                    boundsTransform = snackDetailBoundsTransform,
                    exit = fadeOut(animationSpec = nonSpatialExpressiveSpring()),
                    enter = fadeIn(animationSpec = nonSpatialExpressiveSpring()),
                )
                .fillMaxSize()
                .background(color = JetsnackTheme.colors.uiBackground)
        ) {
            val scroll: ScrollState = rememberScrollState(initial = 0)
            Header(snackId = snack.id, origin = origin)
            Body(related = related, scroll = scroll)
            Title(snack = snack, origin = origin) { scroll.value }
            Image(snackId = snackId, origin = origin, imageRes = snack.imageRes) { scroll.value }
            Up(upPress = upPress)
            CartBottomBar(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        }
    }
}

/**
 * This Composable appears at the very top of the [SnackDetail] screen and consists of just a [Spacer]
 * whose background is a colorful [Brush.linearGradient]. The other composables in the screen will
 * scroll over us and be resized when the user scrolls the screen.
 *
 * We initialize our [SharedTransitionScope] variable `val sharedTransitionScope` to the `current`
 * [LocalSharedTransitionScope] (or throw [IllegalStateException] if that is `null`), and we
 * initialize our [AnimatedVisibilityScope] variable `val animatedVisibilityScope` to the `current`
 * [LocalNavAnimatedVisibilityScope] (or throw [IllegalStateException] if that is `null`).
 *
 * Then `with` [SharedTransitionScope] variable `sharedTransitionScope` as the receiver we execute
 * a `block` in which we initialize our [List] of [Color] variable `val brushColors` to the
 * [JetsnackColors.tornado1] of our custom [JetsnackTheme.colors] and then initialize our
 * [InfiniteTransition] variable `val infiniteTransition` to a [rememberInfiniteTransition] whose
 * `label` argument is "background". We initialize our [Float] variable `val targetOffset` to the
 * `current` [LocalDensity] pixel value of `1000.dp` then we initialize our animated [Float] variable
 * `val offset` to the [InfiniteTransition.animateFloat] of our [InfiniteTransition] variable
 * `infiniteTransition` whose `initialValue` is `0f`, whose `targetValue` is our [Float] variable
 * `targetOffset`, whose `animationSpec` is a [InfiniteRepeatableSpec] whose `animation` argument
 * is a [tween] whose `durationMillis` argument is `50,000`, and whose `easing` argument is a
 * [LinearEasing]. The `repeatMode` argument of the [InfiniteRepeatableSpec] is a [RepeatMode.Reverse].
 *
 * Our root Composable is a [Spacer] whose `modifier` argument is a [SharedTransitionScope.sharedBounds]
 * whose `sharedContentState` argument is the remembered [SharedContentState] returned by a call to
 * the [SharedTransitionScope.rememberSharedContentState] method with the `key` argument a
 * [SnackSharedElementKey] constructed from our [Long] parameter [snackId] as its `snackId` argument
 * our [String] parameter [origin] as its `origin` argument, and whose `type` argument is
 * [SnackSharedElementType.Background]. The `animatedVisibilityScope` argument of the
 * [SharedTransitionScope.sharedBounds] is our [AnimatedVisibilityScope] variable `animatedVisibilityScope`,
 * the `boundsTransform` argument is our [BoundsTransform] property [snackDetailBoundsTransform],
 * the `enter` argument is a [fadeIn] whose `animationSpec` argument is our [FiniteAnimationSpec]
 * property [nonSpatialExpressiveSpring], the `exit` argument is a [fadeOut] whose `animationSpec`
 * is our [FiniteAnimationSpec] property [nonSpatialExpressiveSpring], and the `resizeMode` argument
 * is [SharedTransitionScope.ResizeMode.ScaleToBounds] (will measure the child layout with lookahead
 * constraints to obtain the size of the stable layout. This stable layout is the post-animation
 * layout of the child. The default [ContentScale] of [ContentScale.FillWidth] will be used to
 * calculate a scale for both width and height. The resulting effect is that the child layout does
 * not re-layout during the bounds transform, contrary to RemeasureToBounds mode. Instead, it will
 * scale the stable layout based on the animated size of the sharedBounds). Next in the [Modifier]
 * chain is a [Modifier.height] whose `height` is `280.dp`, followed by a [Modifier.fillMaxWidth]
 * to have it occupy its entire incoming width constraint, followed by a [Modifier.blur] whose
 * `radius` argument is `40.dp` to blur the background, followed by a [Modifier.drawWithCache]
 * in whose [CacheDrawScope] `onBuildDrawCache` lambda argument we initialize our [Float] variable
 * `val brushSize` to `400f`, initialize our [Brush] variable `val brush` to a [Brush.linearGradient]
 * whose `colors` argument is our [List] of [Color] variable `brushColors`, whose `start` argument
 * is an [Offset] whose `x` coordinate is our animated [Float] variable `offset`, whose `y` argument
 * is our animated [Float] variable `offset`. The `end` argument of the [Brush.linearGradient] is an
 * [Offset] whose `x` coordinate is our animated [Float] variable `offset` plus our [Float] variable
 * `brushSize`, whose `y` argument is our animated [Float] variable `offset` plus our [Float] variable
 * `brushSize`. The `tileMode` argument of the [Brush.linearGradient] is a [TileMode.Mirror].
 * Then we call the [CacheDrawScope.onDrawBehind] method with its [DrawScope] `block` lambda argument
 * a lambda that calls the [DrawScope.drawRect] method with its `brush` argument our [Brush] variable
 * `brush`.
 *
 * @param snackId the [Snack.id] of the [Snack] that our [SnackDetail] is displaying. It is used just
 * to construct a [SnackSharedElementKey] linking our shared transition to the originating screen.
 * @param origin a [String] that is used to identify the shared transition we are transitioning with.
 */
@Composable
private fun Header(snackId: Long, origin: String) {
    val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalArgumentException("No Scope found")
    val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")

    with(sharedTransitionScope) {
        val brushColors: List<Color> = JetsnackTheme.colors.tornado1

        val infiniteTransition: InfiniteTransition = rememberInfiniteTransition(label = "background")
        val targetOffset: Float = with(LocalDensity.current) {
            1000.dp.toPx()
        }
        val offset: Float by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = targetOffset,
            animationSpec = infiniteRepeatable(
                tween(50000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "offset"
        )
        Spacer(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = snackId,
                            origin = origin,
                            type = SnackSharedElementType.Background
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = snackDetailBoundsTransform,
                    enter = fadeIn(nonSpatialExpressiveSpring()),
                    exit = fadeOut(nonSpatialExpressiveSpring()),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .height(height = 280.dp)
                .fillMaxWidth()
                .blur(radius = 40.dp)
                .drawWithCache {
                    val brushSize = 400f
                    val brush: Brush = Brush.linearGradient(
                        colors = brushColors,
                        start = Offset(x = offset, y = offset),
                        end = Offset(x = offset + brushSize, y = offset + brushSize),
                        tileMode = TileMode.Mirror
                    )
                    onDrawBehind {
                        drawRect(brush = brush)
                    }
                }
        )
    }
}

/**
 * This Composable appears at the very top left of the [SnackDetail] screen and consists of just an
 * [IconButton] whose `onClick` lambda argument is a lambda that calls our [upPress] lambda
 * parameter.
 *
 * @param upPress a lambda to call when our [IconButton] is clicked.
 */
@Composable
private fun SharedTransitionScope.Up(upPress: () -> Unit) {
    val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")
    with(animatedVisibilityScope) {
        IconButton(
            onClick = upPress,
            modifier = Modifier
                .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 3f)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .size(36.dp)
                .animateEnterExit(
                    enter = scaleIn(animationSpec = tween(durationMillis = 300, delayMillis = 300)),
                    exit = scaleOut(animationSpec = tween(durationMillis = 20))
                )
                .background(
                    color = Neutral8.copy(alpha = 0.32f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                tint = JetsnackTheme.colors.iconInteractive,
                contentDescription = stringResource(R.string.label_back),
            )
        }
    }
}

@Composable
private fun Body(
    related: List<SnackCollection>,
    scroll: ScrollState
) {
    val sharedTransitionScope: SharedTransitionScope =
        LocalSharedTransitionScope.current ?: throw IllegalStateException("No scope found")
    with(sharedTransitionScope) {
        Column(modifier = Modifier.skipToLookaheadSize()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(height = MinTitleOffset)
            )

            Column(
                modifier = Modifier.verticalScroll(state = scroll)
            ) {
                Spacer(modifier = Modifier.height(height = GradientScroll))
                Spacer(modifier = Modifier.height(height = ImageOverlap))
                JetsnackSurface(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(height = TitleHeight))
                        Text(
                            text = stringResource(R.string.detail_header),
                            style = MaterialTheme.typography.labelSmall,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding
                        )
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        var seeMore: Boolean by remember { mutableStateOf(value = true) }
                        with(sharedTransitionScope) {
                            Text(
                                text = stringResource(R.string.detail_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = JetsnackTheme.colors.textHelp,
                                maxLines = if (seeMore) 5 else Int.MAX_VALUE,
                                overflow = TextOverflow.Ellipsis,
                                modifier = HzPadding.skipToLookaheadSize()

                            )
                        }
                        val textButton = if (seeMore) {
                            stringResource(id = R.string.see_more)
                        } else {
                            stringResource(id = R.string.see_less)
                        }

                        Text(
                            text = textButton,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            color = JetsnackTheme.colors.textLink,
                            modifier = Modifier
                                .heightIn(min = 20.dp)
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                                .clickable {
                                    seeMore = !seeMore
                                }
                                .skipToLookaheadSize()
                        )

                        Spacer(modifier = Modifier.height(height = 40.dp))
                        Text(
                            text = stringResource(R.string.ingredients),
                            style = MaterialTheme.typography.labelSmall,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding
                        )
                        Spacer(modifier = Modifier.height(height = 4.dp))
                        Text(
                            text = stringResource(R.string.ingredients_list),
                            style = MaterialTheme.typography.bodyLarge,
                            color = JetsnackTheme.colors.textHelp,
                            modifier = HzPadding
                        )

                        Spacer(modifier = Modifier.height(height = 16.dp))
                        JetsnackDivider()

                        related.forEach { snackCollection: SnackCollection ->
                            key(snackCollection.id) {
                                SnackCollection(
                                    snackCollection = snackCollection,
                                    onSnackClick = { _, _ -> },
                                    highlight = false
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .padding(bottom = BottomBarHeight)
                                .navigationBarsPadding()
                                .height(height = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 *
 */
@Composable
private fun Title(snack: Snack, origin: String, scrollProvider: () -> Int) {
    val maxOffset: Float = with(LocalDensity.current) { MaxTitleOffset.toPx() }
    val minOffset: Float = with(LocalDensity.current) { MinTitleOffset.toPx() }
    val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalArgumentException("No Scope found")
    val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")

    with(sharedTransitionScope) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TitleHeight)
                .statusBarsPadding()
                .offset {
                    val scroll = scrollProvider()
                    val offset: Float = (maxOffset - scroll).coerceAtLeast(minOffset)
                    IntOffset(x = 0, y = offset.toInt())
                }
                .background(color = JetsnackTheme.colors.uiBackground)
        ) {
            Spacer(modifier = Modifier.height(height = 16.dp))
            Text(
                text = snack.name,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.headlineMedium,
                color = JetsnackTheme.colors.textSecondary,
                modifier = HzPadding
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SnackSharedElementKey(
                                snackId = snack.id,
                                origin = origin,
                                type = SnackSharedElementType.Title
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = snackDetailBoundsTransform
                    )
                    .wrapContentWidth()
            )
            Text(
                text = snack.tagline,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 20.sp,
                color = JetsnackTheme.colors.textHelp,
                modifier = HzPadding
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SnackSharedElementKey(
                                snackId = snack.id,
                                origin = origin,
                                type = SnackSharedElementType.Tagline
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = snackDetailBoundsTransform
                    )
                    .wrapContentWidth()
            )
            Spacer(modifier = Modifier.height(height = 4.dp))
            with(animatedVisibilityScope) {
                Text(
                    text = formatPrice(price = snack.price),
                    style = MaterialTheme.typography.titleLarge,
                    color = JetsnackTheme.colors.textPrimary,
                    modifier = HzPadding
                        .animateEnterExit(
                            enter = fadeIn() + slideInVertically { -it / 3 },
                            exit = fadeOut() + slideOutVertically { -it / 3 }
                        )
                        .skipToLookaheadSize()
                )
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
            JetsnackDivider(modifier = Modifier)
        }
    }
}

/**
 *
 */
@Composable
private fun Image(
    snackId: Long,
    origin: String,
    @DrawableRes
    imageRes: Int,
    scrollProvider: () -> Int
) {
    val collapseRange: Float = with(LocalDensity.current) { (MaxTitleOffset - MinTitleOffset).toPx() }
    val collapseFractionProvider: () -> Float = {
        (scrollProvider() / collapseRange).coerceIn(0f, 1f)
    }

    CollapsingImageLayout(
        collapseFractionProvider = collapseFractionProvider,
        modifier = HzPadding.statusBarsPadding()
    ) {
        val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No sharedTransitionScope found")
        val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No animatedVisibilityScope found")

        with(sharedTransitionScope) {
            SnackImage(
                imageRes = imageRes,
                contentDescription = null,
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SnackSharedElementKey(
                                snackId = snackId,
                                origin = origin,
                                type = SnackSharedElementType.Image
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        exit = fadeOut(),
                        enter = fadeIn(),
                        boundsTransform = snackDetailBoundsTransform
                    )
                    .fillMaxSize()

            )
        }
    }
}

/**
 *
 */
@Composable
private fun CollapsingImageLayout(
    collapseFractionProvider: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->
        check(measurables.size == 1)

        val collapseFraction: Float = collapseFractionProvider()

        val imageMaxSize: Int = min(ExpandedImageSize.roundToPx(), constraints.maxWidth)
        val imageMinSize: Int = max(CollapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth: Int = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable: Placeable = measurables[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY: Int = lerp(
            start = MinTitleOffset,
            stop = MinImageOffset,
            fraction = collapseFraction
        ).roundToPx()
        val imageX: Int = lerp(
            start = (constraints.maxWidth - imageWidth) / 2, // centered when expanded
            stop = constraints.maxWidth - imageWidth, // right aligned when collapsed
            fraction = collapseFraction
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.placeRelative(x = imageX, y = imageY)
        }
    }
}

/**
 *
 */
@Composable
private fun CartBottomBar(modifier: Modifier = Modifier) {
    val (count: Int, updateCount: (Int) -> Unit) = remember { mutableIntStateOf(value = 1) }
    val sharedTransitionScope: SharedTransitionScope =
        LocalSharedTransitionScope.current ?: throw IllegalStateException("No Shared scope")
    val animatedVisibilityScope: AnimatedVisibilityScope =
        LocalNavAnimatedVisibilityScope.current ?: throw IllegalStateException("No Shared scope")
    with(sharedTransitionScope) {
        with(animatedVisibilityScope) {
            JetsnackSurface(
                modifier = modifier
                    .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 4f)
                    .animateEnterExit(
                        enter = slideInVertically(
                            tween(
                                durationMillis = 300,
                                delayMillis = 300
                            )
                        ) { it } + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 300)),
                        exit = slideOutVertically(animationSpec = tween(durationMillis = 50)) { it } +
                            fadeOut(animationSpec = tween(durationMillis = 50))
                    )
            ) {
                Column {
                    JetsnackDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .then(HzPadding)
                            .heightIn(min = BottomBarHeight)
                    ) {
                        QuantitySelector(
                            count = count,
                            decreaseItemCount = { if (count > 0) updateCount(count - 1) },
                            increaseItemCount = { updateCount(count + 1) }
                        )
                        Spacer(modifier = Modifier.width(width = 16.dp))
                        JetsnackButton(
                            onClick = { /* todo */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.add_to_cart),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Three Previews of our [SnackDetail] using different device configurations.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SnackDetailPreview() {
    JetsnackPreviewWrapper {
        SnackDetail(
            snackId = 1L,
            origin = "details",
            upPress = { }
        )
    }
}
