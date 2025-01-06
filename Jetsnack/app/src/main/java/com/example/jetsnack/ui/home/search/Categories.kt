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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.SearchCategory
import com.example.jetsnack.model.SearchCategoryCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.VerticalGrid
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import kotlin.math.max

/**
 * Displays a [List] of [SearchCategoryCollection]. Our first Composable is a [LazyColumn], and in
 * its [LazyListScope] `content` composable lambda argument we have a [LazyListScope.itemsIndexed]
 * whose `items` argument is our [List] of [SearchCategoryCollection]s, and in its `itemContent`
 * [LazyItemScope] Composable lambda argument it passes the index of the current
 * [SearchCategoryCollection] in the variable `index` and the [SearchCategoryCollection] in the
 * variable `collection` to the lambda where we compose a [SearchCategoryCollection] Composable
 * whose`collection` argument is the `collection` variable and `index` argument is the `index`
 * variable. We add a [Spacer] after the [LazyColumn] whose `modifier` argument is a
 * [Modifier.height] of 8.dp.
 *
 * @param categories The [List] of [SearchCategoryCollection] to display.
 */
@Composable
fun SearchCategories(
    categories: List<SearchCategoryCollection>
) {
    LazyColumn {
        itemsIndexed(items = categories) { index: Int, collection: SearchCategoryCollection ->
            SearchCategoryCollection(collection = collection, index = index)
        }
    }
    Spacer(modifier = Modifier.height(height = 8.dp))
}

/**
 * Displays a single [SearchCategoryCollection]. Our root Composable is a [Column] whose `modifier`
 * argument is our [Modifier] parameter [modifier], and in its [ColumnScope] `content` Composable
 * lambda argument we have:
 *  - a [Text] whose `text` argument is the [SearchCategoryCollection.name] of our
 *  [SearchCategoryCollection] parameter [collection], the [TextStyle] `style` argument is the
 *  [Typography.titleLarge] of our custom [MaterialTheme.typography], the [Color] `color` argument
 *  is the [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors], and the [Modifier]
 *  `modifier` argument is a [Modifier.heightIn] whose `min` height is 56.dp, chained to a
 *  [Modifier.padding] that sets the padding on the `horizontal` sides to 24.dp, and the padding on
 *  the `vertical` sides to 4.dp, and that has chained to it a [Modifier.wrapContentHeight] which
 *  allows it to measure at its desired height without regard for the incoming measurement minimum
 *  height constraint).
 *  - a [VerticalGrid] whose `modifier` argument is a [Modifier.padding] that adds 16.dp padding to
 *  each of its `horizontal` sides, and in the `content` Composable lambda argument we start by
 *  assigning the value of our [List] of [Color] variable `val gradient` to the
 *  [JetsnackColors.gradient2_2] of our custom custom [JetsnackTheme.colors] when our [Int] parameter
 *  [index] is even or the [JetsnackColors.gradient2_3] if it is odd. Then we use the [forEach]
 *  method of our [SearchCategoryCollection] parameter [collection] to loop through its contents
 *  passing each [SearchCategory] to its lambda argument in the variable `category` which we use to
 *  compose a [SearchCategory] whose `category` argument is the [SearchCategory] variable `category`,
 *  whose `gradient` argument is our [List] of [Color] variable `gradient`, and whose `modifier`
 *  argument is a [Modifier.padding] that adds 8.dp to all side of the [SearchCategory].
 *  - At the bottom of the [Column] is a [Spacer] whose `modifier` argument is a [Modifier.height]
 *  that sets its `height` to 4.dp.
 *
 * @param collection The [SearchCategoryCollection] to display.
 * @param index The index of the collection in the [List] of [SearchCategoryCollection]s.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behaviour. Our caller does not pass us one, so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
private fun SearchCategoryCollection(
    collection: SearchCategoryCollection,
    index: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = collection.name,
            style = MaterialTheme.typography.titleLarge,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .wrapContentHeight()
        )
        VerticalGrid(modifier = Modifier.padding(horizontal = 16.dp)) {
            val gradient: List<Color> = when (index % 2) {
                0 -> JetsnackTheme.colors.gradient2_2
                else -> JetsnackTheme.colors.gradient2_3
            }
            collection.categories.forEach { category: SearchCategory ->
                SearchCategory(
                    category = category,
                    gradient = gradient,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(height = 4.dp))
    }
}

/**
 * Minimum size for the category image
 */
private val MinImageSize = 134.dp

/**
 * [SearchCategory] uses this as the [Shape] for the [Modifier.shadow] and [Modifier.clip] it applies
 * to its root [Layout] Composable.
 */
private val CategoryShape = RoundedCornerShape(size = 10.dp)

/**
 * The proportion of the [Constraints.maxWidth] used by the [Text] in the [SearchCategory] (the `x`
 * position of the [SnackImage] which follow the [Text])
 */
private const val CategoryTextProportion = 0.55f

/**
 * Displays a single search category. Our root Composable is a [Layout] whose `modifier` argument
 * chains to our [Modifier] parameter [modifier] a [Modifier.aspectRatio] whose `ratio` argument is
 * 1.45f, with a [Modifier.shadow] chained to that whose `elevation` argument is 3.dp and whose
 * `shape` argument is [CategoryShape], with a [Modifier.clip] chained to that whose `shape` argument
 * is [CategoryShape], with a [Modifier.background] chained to that whose `brush` argument is a
 * [Brush.horizontalGradient] whose `colors` argument is our [List] of [Color] parameter [gradient],
 * with a [Modifier.clickable] chained to that whose `onClick` lambda argument is a do-nothing lambda.
 * In its `content` Composable lambda argument we have:
 *  - a [Text] whose `text` argument is the [SearchCategory.name] of our [SearchCategory] parameter
 *  [category], whose [TextStyle] `style` argument is the [Typography.titleMedium] of our custom
 *  [MaterialTheme.typography], whose [Color] `color` argument is the [JetsnackColors.textSecondary]
 *  of our custom [JetsnackTheme.colors], and whose [Modifier] `modifier` argument is a
 *  [Modifier.padding] that adds 4.dp to all sides, with a [Modifier.padding] that adds 8.dp to the
 *  `start`.
 *  - a [SnackImage] whose `imageRes` argument is the resource ID [SearchCategory.imageRes] of our
 *  [SearchCategory] parameter [category], whose `content` description argument is `null`, and whose
 *  `modifier` argument is a [Modifier.fillMaxSize] that allows it to measure at its desired size
 *  without regard to the incoming measurement minimum size constraint.
 *
 * The arguments passed to the [MeasureScope] lambda argument of the [Layout] Composable are a
 * [List] of [Measurable] passed in the variable `measurables` and a [Constraints] passed in the
 * variable `constraints`. In that lambda we initialize our [Int] variable `val textWidth` to the
 * `maxWidth` of our [Constraints] times [CategoryTextProportion]. We initialize our [Placeable]
 * variable `val textPlaceable` by using the [Measurable.measure] of the 0 index [Measurable] in the
 * `measurables` list to measure the [Text] using our [Int] variable `textWidth` as the
 * [Constraints.fixedWidth] of the [Placeable]. We initialize our [Int] variable `imageSize` to the
 * [max] of [MinImageSize] and the [Constraints.maxHeight] of the [Constraints] variable `constraints`.
 * We initialize our [Placeable] variable `val imagePlaceable` by using the [Measurable.measure] of
 * the index `1` [Measurable] in the [List] of [Measurable] variable `measurables` to measure the
 * [SnackImage] using our [Int] variable `imageSize` as the [Constraints.fixed] size of the [Placeable]
 * produced. We then call the [MeasureScope.layout] method with its `width` argument set to the
 * [Constraints.maxWidth] of the [Constraints] variable `constraints`, and its `height` argument set
 * to the [Constraints.minHeight] of the [Constraints] variable `constraints`. In the `placementBlock`
 * lambda argument we have call:
 *  - the [Placeable.PlacementScope.placeRelative] extension method of [Placeable] variable
 *  `textPlaceable` with its `x` argument set to 0, and its `y` argument set to the
 *  [Constraints.maxHeight] of the [Constraints] variable `constraints` minus the [Placeable.height]
 *  of the [Placeable] variable `textPlaceable` all divided by 2 (to center it vertically).
 *  - the [Placeable.PlacementScope.placeRelative] extension method of [Placeable] variable
 *  `imagePlaceable` with its `x` argument our [Int] variable `textWidth`, and its `y` argument set
 *  to the [Constraints.maxHeight] minus the [Placeable.height] of `imagePlaceable` all divided by 2
 *  (to center it vertically).
 *
 * @param category The [SearchCategory] to display.
 * @param gradient The [List] of [Color] to use as the gradient for the background.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behaviour. Our caller [SearchCategoryCollection] passes us a [Modifier.padding] that adds 8.dp to
 * all sides.
 */
@Composable
private fun SearchCategory(
    category: SearchCategory,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier = modifier
            .aspectRatio(ratio = 1.45f)
            .shadow(elevation = 3.dp, shape = CategoryShape)
            .clip(shape = CategoryShape)
            .background(brush = Brush.horizontalGradient(colors = gradient))
            .clickable { /* todo */ },
        content = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier
                    .padding(all = 4.dp)
                    .padding(start = 8.dp)
            )
            SnackImage(
                imageRes = category.imageRes,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->
        // Text given a set proportion of width (which is determined by the aspect ratio)
        val textWidth: Int = (constraints.maxWidth * CategoryTextProportion).toInt()
        val textPlaceable: Placeable = measurables[0].measure(Constraints.fixedWidth(textWidth))

        // Image is sized to the larger of height of item, or a minimum value
        // i.e. may appear larger than item (but clipped to the item bounds)
        val imageSize: Int = max(MinImageSize.roundToPx(), constraints.maxHeight)
        val imagePlaceable: Placeable = measurables[1].measure(Constraints.fixed(imageSize, imageSize))
        layout(
            width = constraints.maxWidth,
            height = constraints.minHeight
        ) {
            textPlaceable.placeRelative(
                x = 0,
                y = (constraints.maxHeight - textPlaceable.height) / 2 // centered
            )
            imagePlaceable.placeRelative(
                // image is placed to end of text i.e. will overflow to the end (but be clipped)
                x = textWidth,
                y = (constraints.maxHeight - imagePlaceable.height) / 2 // centered
            )
        }
    }
}

/**
 * Three previews of the [SearchCategory] composable.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchCategoryPreview() {
    JetsnackTheme {
        SearchCategory(
            category = SearchCategory(
                name = "Desserts",
                imageRes = R.drawable.desserts
            ),
            gradient = JetsnackTheme.colors.gradient3_2
        )
    }
}
