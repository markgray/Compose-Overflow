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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.model.SearchRepo
import com.example.jetsnack.model.SearchSuggestionGroup
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * A vertically scrolling list of [SearchSuggestionGroup]s that is displayed by the [Search]
 * Composable when the [SearchDisplay] of the [SearchState.searchDisplay] of the current
 * [SearchState] is [SearchDisplay.Suggestions] (this happens when the user has clicked the search
 * bar but has not entered any text yet). Our root Composable is a [LazyColumn] and in its
 * [LazyListScope] `content` Composable lambda argument we use the [List.forEach] method of [List]
 * of [SearchSuggestionGroup] parameter [suggestions] to loop through each [SearchSuggestionGroup]
 * capturing the [SearchSuggestionGroup] passed the lambda in variable `suggestionGroup`. For each
 * [SearchSuggestionGroup] in `suggestionGroup` we:
 *  - use a [LazyListScope.item] in whose [LazyItemScope] Composable lambda argument we compose a
 *  [SuggestionHeader] passing the [SearchSuggestionGroup.name] of the current [SearchSuggestionGroup]
 *  captured in the `suggestionGroup` variable as its `name` argument.
 *  - use a [LazyListScope.items] whose `items` argument is the [SearchSuggestionGroup.suggestions]
 *  of the current [SearchSuggestionGroup] captured in the `suggestionGroup` feeding each [String]
 *  in the [SearchSuggestionGroup.suggestions] of the current [SearchSuggestionGroup] captured in the
 *  variable `suggestionGroup` to Compose a [Suggestion] whose `suggestion` argument is the current
 *  [String] captured in the `suggestion` variable and whose `onSuggestionSelect` argument is our
 *  lambda parameter [onSuggestionSelect], and whose [Modifier] `modifier` argument is a
 *  [LazyItemScope.fillParentMaxWidth] to have the [Suggestion] fill the width of its parent.
 *  - use a [LazyListScope.item] in whose [LazyItemScope] Composable lambda argument we compose a
 *  [Spacer] whose `modifier` argument is a [Modifier.height] of 4.dp.
 *
 * @param suggestions the list of [SearchSuggestionGroup]s to display.
 * @param onSuggestionSelect called when a [Suggestion] is selected.
 */
@Suppress("Destructure")
@Composable
fun SearchSuggestions(
    suggestions: List<SearchSuggestionGroup>,
    onSuggestionSelect: (String) -> Unit
) {
    LazyColumn {
        suggestions.forEach { suggestionGroup: SearchSuggestionGroup ->
            item {
                SuggestionHeader(name = suggestionGroup.name)
            }
            items(items = suggestionGroup.suggestions) { suggestion: String ->
                Suggestion(
                    suggestion = suggestion,
                    onSuggestionSelect = onSuggestionSelect,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(height = 4.dp))
            }
        }
    }
}

/**
 * A header to display at the top of the [LazyColumn] of [SearchSuggestions]. Our root Composable is
 * a [Text] whose `text` argument is our [String] parameter [name], whose [TextStyle] `style` argument
 * is the [Typography.titleLarge] of our custom [MaterialTheme.typography], whose [Color] `color`
 * argument is the [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors], and whose
 * [Modifier] `modifier` argument chains to our [Modifier] parameter [modifier] a [Modifier.heightIn]
 * whose `min` is 56.dp (constrains the height of the content to be at least 56.dp), then chains a
 * [Modifier.padding] that adds 24.dp to each `horizontal` side and 4.dp to each `vertical` side,
 * and ends with chain of [Modifier.wrapContentHeight] (allows the content to measure at its desired
 * height without regard for the incoming minimum height constraint).
 *
 * @param name the header title to display.
 * @param modifier a [Modifier] instance that our caller can use to modify the header's appearance
 * and/or behaviour. Our caller [SearchSuggestions] passes us none so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 */
@Composable
private fun SuggestionHeader(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge,
        color = JetsnackTheme.colors.textPrimary,
        modifier = modifier
            .heightIn(min = 56.dp)
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .wrapContentHeight()
    )
}

/**
 * Used by [SearchSuggestions] to display each [String] parameter [suggestion] that it has found
 * in the [List] of [String] field [SearchSuggestionGroup.suggestions] of the [SearchSuggestionGroup]
 * it is displaying. Our root Composable is a [Text] whose `text` argument is our [String] parameter
 * [suggestion], whose [TextStyle] `style` argument is the [Typography.titleMedium] of our custom
 * [MaterialTheme.typography], and whose [Modifier] argument `modifier` chains to our [Modifier]
 * parameter [modifier] a [Modifier.heightIn] whose `min` is 48.dp (constrains the height of the
 * content to be at least 48.dp), then chains a [Modifier.clickable] in whose `onClick` lambda we
 * call our lambda taking [String] parameter [onSuggestionSelect] with the [String] parameter
 * [suggestion], followed by a chain to a [Modifier.padding] that adds 24.dp padding to its `start`,
 * and last in the chain is a [Modifier.wrapContentSize] to allow the content to measure at its
 * desired size without regard for the incoming minimum width or minimum height constraints with
 * the `align` argument [Alignment.CenterStart] to align the content to the `start` and center it.
 *
 * @param suggestion the [String] we are to display.
 * @param onSuggestionSelect a lambda that should be called with our [String] parameter [suggestion]
 * when the [Text] displaying it is clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [SearchSuggestions] passes us a [LazyItemScope.fillParentMaxWidth] to have
 * our [Suggestion] fill the width of its parent.
 */
@Composable
private fun Suggestion(
    suggestion: String,
    onSuggestionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = suggestion,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .heightIn(min = 48.dp)
            .clickable { onSuggestionSelect(suggestion) }
            .padding(start = 24.dp)
            .wrapContentSize(align = Alignment.CenterStart)
    )
}

/**
 * Three previews of our [SearchSuggestions] composable using different device configurations.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun PreviewSuggestions() {
    JetsnackTheme {
        JetsnackSurface {
            SearchSuggestions(
                suggestions = SearchRepo.getSuggestions(),
                onSuggestionSelect = { }
            )
        }
    }
}
