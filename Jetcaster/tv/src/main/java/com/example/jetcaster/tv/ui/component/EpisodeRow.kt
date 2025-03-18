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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

/**
 * Displays a horizontal row of [EpisodeCard]s representing a list of podcast episodes.
 *
 * This composable presents a scrollable row of episode cards, allowing users to browse and select
 * from a list of available podcast episodes. It manages focus navigation within the row and
 * handles restoring the focused item when the list is re-rendered with the same data.
 *
 * @param playerEpisodeList The list of [PlayerEpisode]s to display.
 * @param onSelected Callback invoked when an episode is selected. It receives the selected
 * [PlayerEpisode] as a parameter.
 * @param modifier Modifier for styling and layout customization of the row.
 * @param horizontalArrangement The arrangement of the episode cards along the horizontal axis.
 * Defaults to [Arrangement.spacedBy] with a predefined gap.
 * @param contentPadding The padding to apply to the content within the row. Defaults to predefined
 * padding values.
 * @param focusRequester The [FocusRequester] used to manage focus for the entire row.
 * @param lazyListState The [LazyListState] used to manage the scroll position of the row.
 * It's automatically remembered keyed on the `playerEpisodeList`.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun EpisodeRow(
    playerEpisodeList: EpisodeList,
    onSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(space = JetcasterAppDefaults.gap.item),
    contentPadding: PaddingValues = JetcasterAppDefaults.padding.episodeRowContentPadding,
    focusRequester: FocusRequester = remember { FocusRequester() },
    lazyListState: LazyListState = remember(key1 = playerEpisodeList) { LazyListState() }
) {
    val firstItem: FocusRequester = remember { FocusRequester() }
    var previousEpisodeListHash: Int by remember { mutableIntStateOf(playerEpisodeList.hashCode()) }
    val isSameList: Boolean = previousEpisodeListHash == playerEpisodeList.hashCode()

    LazyRow(
        state = lazyListState,
        modifier = Modifier
            .focusRequester(focusRequester = focusRequester)
            .focusProperties {
                enter = {
                    when {
                        lazyListState.layoutInfo.visibleItemsInfo.isEmpty() -> FocusRequester.Cancel
                        isSameList && focusRequester.restoreFocusedChild() -> FocusRequester.Cancel
                        else -> firstItem
                    }
                }
                exit = {
                    previousEpisodeListHash = playerEpisodeList.hashCode()
                    focusRequester.saveFocusedChild()
                    FocusRequester.Default
                }
            }
            .then(other = modifier),
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
    ) {
        itemsIndexed(items = playerEpisodeList) { index: Int, item: PlayerEpisode ->
            val cardModifier: Modifier = if (index == 0) {
                Modifier.focusRequester(focusRequester = firstItem)
            } else {
                Modifier
            }
            EpisodeCard(
                playerEpisode = item,
                onClick = { onSelected(item) },
                modifier = cardModifier
            )
        }
    }
}
