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

package com.example.jetsnack.ui.home.search

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.HorizontalAnchorable
import androidx.constraintlayout.compose.VerticalAnchorable
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.formatPrice

/**
 * Called by the [Search] Composable to display the search results in its [List] of [Snack] parameter
 * [searchResults]. Our root Composable is a [Column]. In its [ColumnScope] `content` Composable
 * lambda we have:
 *  - a [Text] whose `text` argument is the [String] formed by the format [String] with the resource
 *  ID `R.string.search_count` and the [Int] parameter of the [List.size] of our [List] of [Snack]
 *  parameter [searchResults], its [TextStyle] `style` argument is the [Typography.titleLarge] of
 *  our custom [MaterialTheme.typography], its [Color] `color` argument is the
 *  [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors], and the [Modifier] `modifier`
 *  argument is a [Modifier.padding] that adds 24.dp of padding on the `horizontal` sides and 4.dp
 *  of padding on the `vertical` sides.
 *  - a [LazyColumn] in whose [LazyListScope] `content` Composable lambda argument we have a
 *  [LazyListScope.itemsIndexed] whose `items` argument is our [List] of [Snack] parameter
 *  [searchResults],  and the [LazyItemScope] `itemContent` Composable lambda argument is passed
 *  the index of the [Snack] in the [Int] variable `index` and the [Snack] in the `snack` variable
 *  where we compose a [SearchResult] whose `snack` argument is the `snack` variable, whose
 *  `onSnackClick` argument our lambda parameter [onSnackClick], and whose `showDivider` argument
 *  is `true` if our [Int] variable `index` is not zero.
 *
 * @param searchResults the [List] of [Snack]s to display.
 * @param onSnackClick a lambda that is called with the [Snack.id] and the [String] "search" when a
 * [SearchResult] of a [Snack] is clicked.
 */
@Composable
fun SearchResults(
    searchResults: List<Snack>,
    onSnackClick: (Long, String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.search_count, searchResults.size),
            style = MaterialTheme.typography.titleLarge,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        LazyColumn {
            itemsIndexed(items = searchResults) { index: Int, snack: Snack ->
                SearchResult(snack = snack, onSnackClick = onSnackClick, showDivider = index != 0)
            }
        }
    }
}

/**
 * This is used by the [SearchResults] Composable to display each [Snack] in its [List] of [Snack]
 * parameter `searchResults`. Our root composable is a [ConstraintLayout] whose `modifier` argument
 * is a [Modifier.fillMaxWidth] that makes it take up its entire incoming width constraint, with a
 * [Modifier.clickable] whose `onClick` lambda argument is a lambda that calls our [onSnackClick]
 * lambda parameter with the [Snack.id] of [Snack] parameter [snack] and the [String] "search", and
 * chained to this is a [Modifier.padding] that adds 24.dp to each `horizontal` side. In its
 * [ConstraintLayoutScope] `content` Composable lambda argument we start by initializing our
 * [ConstrainedLayoutReference] variables `divider`, `image`, `name`, `tag`, `priceSpacer`, `price`,
 * and `add` by using destructuring on the [ConstraintLayoutScope.ConstrainedLayoutReferences]
 * returned by the [ConstraintLayoutScope.createRefs] method. Then we call the
 * [ConstraintLayoutScope.createVerticalChain] method to create a vertical chain whose `chainStyle`
 * is [ChainStyle.Packed] out of `name`, `tag`, `priceSpacer`, and `price` (chain style where the
 * contained layouts are packed together and placed to the center of the available space). If
 * our [Boolean] parameter [showDivider] is `true` we compose a [JetsnackDivider] whose `modifier`
 * argument is a [ConstraintLayoutScope.constrainAs] whose `ref` argument is our
 * [ConstrainedLayoutReference] variable `divider` and in its [ConstrainScope] `constrainBlock`
 * lambda argument we use the [ConstrainScope.linkTo] method to link its `start` to the
 * [ConstrainedLayoutReference.top] of its [ConstrainScope.parent] and its `end` to the
 * [ConstrainedLayoutReference.end] of its [ConstrainScope.parent]. Then we call the
 * [HorizontalAnchorable.linkTo] method of its [ConstrainedLayoutReference.top] to link to the
 * `anchor` of the [ConstrainedLayoutReference.top] of its [ConstrainScope.parent].
 *
 * Our next Composable is a [SnackImage] whose `imageRes` argument is the [Snack.imageRes] of our
 * [Snack] parameter [snack] (the resource ID of a jpeg showing an example of the [Snack]), its
 * `contentDescription` argument is `null`, and its `modifier` argument is a [Modifier.size] that
 * sets its `size` to 100.dp with a [ConstraintLayoutScope.constrainAs] chained to that to constrain
 * the [SnackImage] using [ConstrainedLayoutReference] variable `image` and in its [ConstrainScope]
 * `constrainBlock` lambda argument we use the [ConstrainScope.linkTo] method to link its `top` to
 * the [ConstrainedLayoutReference.top] of its [ConstrainScope.parent] with a `topMargin` of 16.dp,
 * and to link its `bottom` to [ConstrainedLayoutReference.bottom] of its [ConstrainScope.parent]
 * with a `bottomMargin` of 16.dp. Then we call the [VerticalAnchorable.linkTo] of its
 * [ConstrainedLayoutReference.start] to link to the `anchor` of the [ConstrainedLayoutReference.start]
 * of its [ConstrainScope.parent].
 *
 * Our next Composable is a [Text] whose `text` argument is the [Snack.name] of our [Snack] parameter
 * [snack], its [TextStyle] `style` argument is the [Typography.titleMedium] of our custom
 * [MaterialTheme.typography], its [Color] `color` argument is the [JetsnackColors.textSecondary]
 * of our custom [JetsnackTheme.colors], and its `modifier` argument is a
 * [ConstraintLayoutScope.constrainAs] to constrain the [Text] using [ConstrainedLayoutReference]
 * variable `name`, and in its [ConstrainScope] `constrainBlock` lambda argument we use the
 * [ConstrainScope.linkTo] method to link its `start` to the [ConstrainedLayoutReference.end] of the
 * [ConstrainedLayoutReference] variable `image` with a `startMargin` of 16.dp, and to link its `end`
 * to the [ConstrainedLayoutReference.start] of the [ConstrainedLayoutReference] variable `add` with
 * an `endMargin` of 16.dp, and a `bias` to `0f`.
 *
 * The next Composable is a [Text] whose `text` argument is the [Snack.tagline] of our [Snack]
 * parameter [snack], whose [TextStyle] `style` argument is the [Typography.bodyLarge] of our custom
 * [MaterialTheme.typography], whose [Color] `color` argument is the [JetsnackColors.textHelp] of our
 * custom [JetsnackTheme.colors], and whose `modifier` argument is a [ConstraintLayoutScope.constrainAs]
 * to constrain the [Text] using [ConstrainedLayoutReference] variable `tag`, and in its constrainBlock`
 * lambda argument we use the [ConstrainScope.linkTo] method to link its `start` to the
 * [ConstrainedLayoutReference.end] of [ConstrainedLayoutReference] variable `image` with a
 * `startMargin` of 16.dp, and to link its `end` to the [ConstrainedLayoutReference.start] of
 * [ConstrainedLayoutReference] variable `add` with an `endMargin` of 16.dp, and a `bias` to `0f`.
 *
 * Next we have a [Spacer] whose `modifier` argument is a [Modifier.height] to set its `height` to
 * 8.dp, with a [ConstraintLayoutScope.constrainAs] chained to that to constrain the [Spacer] using
 * [ConstrainedLayoutReference] variable `priceSpacer`, and in its [ConstrainScope] `constrainBlock`
 * we use the [ConstrainScope.linkTo] method to link its `top` to the
 * [ConstrainedLayoutReference.bottom] of [ConstrainedLayoutReference] variable `tag` and its
 * `bottom` to the [ConstrainedLayoutReference.top] of [ConstrainedLayoutReference] variable `price`.
 *
 * Next we have a [Text] whose `text` argument is the [String] returned by the method [formatPrice]
 * for the [Snack.price] of our [Snack] parameter [snack], whose [TextStyle] `style` argument is the
 * [Typography.titleMedium] of our custom [MaterialTheme.typography], whose [Color] `color` argument
 * is the [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors], and whose `modifier`
 * argument is a [ConstraintLayoutScope.constrainAs] to contrain the [Text] using
 * [ConstrainedLayoutReference] variable `price`, and in its [ConstrainScope] `constrainBlock`
 * lambda argument we use the [ConstrainScope.linkTo] method to link its `start` to the
 * [ConstrainedLayoutReference.end] of the [ConstrainedLayoutReference] variable `image` with a
 * `startMargin` of 16.dp, and to link its `end` to to the [ConstrainedLayoutReference.start] of
 * [ConstrainedLayoutReference] variable `add` with an `endMargin` of 16.dp and a `bias` of `Of`.
 *
 * At the bottom of our layout we have a [JetsnackButton] whose `onClick` argument is a do-nothing
 * lambda, whose [Shape] `shape` argument is [CircleShape], whose `contentPadding` argument is a
 * [PaddingValues] that adds 0.dp to all sides, and whose `modifier` argument is a [Modifier.size]
 * that sets its `size` to 36.dp with a [ConstraintLayoutScope.constrainAs] chained to that to
 * constrain the [JetsnackButton] using [ConstrainedLayoutReference] variable `add` and in its
 * [ConstrainScope] `constrainBlock` lambda argument we use the [ConstrainScope.linkTo] method to
 * link its `top` to the [ConstrainedLayoutReference.top] of its [ConstrainScope.parent] and its
 * `bottom` to the [ConstrainedLayoutReference.bottom] of its [ConstrainScope.parent], then we call
 * the [VerticalAnchorable.linkTo] of its [ConstrainedLayoutReference.end] to link it to the
 * [ConstrainedLayoutReference.end] of its [ConstrainScope.parent]. In the [RowScope] `content`
 * Composable lambda argument of the [JetsnackButton] we have an [Icon] whose [ImageVector]
 * `imageVector` argument is the [ImageVector] drawn by [Icons.Outlined.Add] (a "Plus" sign), and
 * whose `contentDescription` argument is the [String] with resource ID `R.string.label_add`
 * ("Add to cart").
 *
 * @param snack the [Snack] that we are to display.
 * @param onSnackClick a lambda that should be called with the [Snack.id] of [snack] and the [String]
 * "search" whenever this [SearchResult] is clicked.
 * @param showDivider when `true` we will display a [JetsnackDivider] at the top of our layout.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [SearchResults] does not pass us one so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 */
@Composable
private fun SearchResult(
    snack: Snack,
    onSnackClick: (Long, String) -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id, "search") }
            .padding(horizontal = 24.dp)
    ) {
        val (divider: ConstrainedLayoutReference,
            image: ConstrainedLayoutReference,
            name: ConstrainedLayoutReference,
            tag: ConstrainedLayoutReference,
            priceSpacer: ConstrainedLayoutReference,
            price: ConstrainedLayoutReference,
            add: ConstrainedLayoutReference) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        if (showDivider) {
            JetsnackDivider(
                modifier = Modifier.constrainAs(ref = divider) {
                    linkTo(start = parent.start, end = parent.end)
                    top.linkTo(anchor = parent.top)
                }
            )
        }
        SnackImage(
            imageRes = snack.imageRes,
            contentDescription = null,
            modifier = Modifier
                .size(size = 100.dp)
                .constrainAs(ref = image) {
                    linkTo(
                        top = parent.top,
                        topMargin = 16.dp,
                        bottom = parent.bottom,
                        bottomMargin = 16.dp
                    )
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
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.bodyLarge,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(ref = tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Spacer(
            Modifier
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
                    startMargin = 16.dp,
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        JetsnackButton(
            onClick = { /* todo */ },
            shape = CircleShape,
            contentPadding = PaddingValues(all = 0.dp),
            modifier = Modifier
                .size(size = 36.dp)
                .constrainAs(ref = add) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    end.linkTo(anchor = parent.end)
                }
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = stringResource(R.string.label_add)
            )
        }
    }
}

/**
 * This is composed by [Search] when there are no [Snack]s that match the [String] parameter [query].
 * Our root Composable is a [Column] whose `horizontalAlignment` argument is [Alignment.CenterHorizontally]
 * to center its children horizontally, and whose `modifier` argument is a [Modifier.fillMaxSize] that
 * causes it to occupy its entire incoming size constraint, with a [Modifier.wrapContentSize] chained
 * to that that allows it to measure at its desired size, and chained to that is a [Modifier.padding]
 * that adds 24.dp to `all` sides. In its [ColumnScope] `content` Composable lambda argument we have:
 *  - an [Image] whose `painter` argument is a [painterResource] whose `id` argument is the resource ID
 *  `R.drawable.empty_state_search` (a stylized "Flashight shining on a "android" head with a question
 *  mark on it"), and whose `contentDescription` argument is `null`.
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] that sets its `height` to 24.dp
 *  - a [Text] whose `text` argument is the [String] formed by the format [String] with the resource
 *  ID `R.string.search_no_matches` (No matches for “%1s”) from our [String] parameter [query], whose
 *  [TextStyle] `style` argument is the [Typography.titleMedium] of our custom [MaterialTheme.typography],
 *  whose `textAlign` argument is [TextAlign.Center] (centers the text), and whose `modifier` argument
 *  is a [Modifier.fillMaxWidth] that causes it to occupy its entire incoming width constraint.
 *
 * @param query the [String] that the user entered for the [Search] Composable to search for.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [Search] does not pass us one so the empty, default, or starter [Modifier]
 * that contains no elements is used.
 */
@Composable
fun NoResults(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(all = 24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_state_search),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = stringResource(R.string.search_no_matches, query),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(height = 16.dp))
        Text(
            text = stringResource(R.string.search_no_matches_retry),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Three previews of a [SearchResult] are provided here to show different device settings.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchResultPreview() {
    JetsnackTheme {
        JetsnackSurface {
            SearchResult(
                snack = snacks[0],
                onSnackClick = { _, _ -> },
                showDivider = false
            )
        }
    }
}
