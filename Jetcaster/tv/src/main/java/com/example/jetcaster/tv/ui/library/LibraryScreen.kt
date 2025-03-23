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

package com.example.jetcaster.tv.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.component.Catalog
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import kotlinx.coroutines.flow.StateFlow

/**
 * Displays the Library screen, which shows the user's subscribed podcasts and the latest episodes.
 *
 * We start by initializing our [State] wrapped [LibraryScreenUiState] variable `val uiState` using
 * the [StateFlow.collectAsState] method of the [LibraryScreenViewModel.uiState] property of our
 * [LibraryScreenViewModel] parameter [libraryScreenViewModel]. We then copy this [LibraryScreenUiState]
 * to variable `s` and use a when statement to handle different states:
 *  - [LibraryScreenUiState.Loading]: We show a loading indicator using the [Loading] composable with
 *  its `modifier` argument our [Modifier] parameter [modifier].
 *  - [LibraryScreenUiState.NoSubscribedPodcast]: We call compose a [NavigateToDiscover] composable
 *  with its `onNavigationRequested` argument our [navigateToDiscover] lambda parameter and
 *  its `modifier` argument our [Modifier] parameter [modifier].
 *  - [LibraryScreenUiState.Ready]: We compose a [Library] composable with its `podcastList` argument
 *  the [LibraryScreenUiState.Ready.subscribedPodcastList] property of `s`, its `episodeList`
 *  tje [LibraryScreenUiState.Ready.latestEpisodeList] property of `s`, its `showPodcastDetails`
 *  argument our [showPodcastDetails] lambda parameter, its `onEpisodeSelected` argument a lambda
 *  that calls the [LibraryScreenViewModel.playEpisode] method of [libraryScreenViewModel] with
 *  the [PlayerEpisode] parameter `it`, and call our [playEpisode] lambda parameter with its
 *  `playerEpisode` argument `it`, and the `modifier` argument our [Modifier] parameter [modifier].
 *
 * @param modifier [Modifier] for styling the Library screen. Our caller the `Route` method of
 * `JetcasterApp` passes us a [Modifier.fillMaxSize] instance.
 * @param navigateToDiscover Callback to navigate to the Discover screen.
 * @param showPodcastDetails Callback to show the details of a specific podcast. It takes a
 * [PodcastInfo] object as a parameter.
 * @param playEpisode Callback to play a selected episode. It takes a [PlayerEpisode] object
 * as a parameter.
 * @param libraryScreenViewModel The [LibraryScreenViewModel] instance responsible for managing
 * the Library screen's data and state. Defaults to a Hilt-injected instance.
 */
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    navigateToDiscover: () -> Unit,
    showPodcastDetails: (PodcastInfo) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    libraryScreenViewModel: LibraryScreenViewModel = hiltViewModel()
) {
    val uiState: LibraryScreenUiState by libraryScreenViewModel.uiState.collectAsState()
    when (val s: LibraryScreenUiState = uiState) {
        LibraryScreenUiState.Loading -> Loading(modifier = modifier)
        LibraryScreenUiState.NoSubscribedPodcast -> {
            NavigateToDiscover(onNavigationRequested = navigateToDiscover, modifier = modifier)
        }

        is LibraryScreenUiState.Ready -> Library(
            podcastList = s.subscribedPodcastList,
            episodeList = s.latestEpisodeList,
            showPodcastDetails = showPodcastDetails,
            onEpisodeSelected = {
                libraryScreenViewModel.playEpisode(playerEpisode = it)
                playEpisode(it)
            },
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Library(
    podcastList: PodcastList,
    episodeList: EpisodeList,
    showPodcastDetails: (PodcastInfo) -> Unit,
    onEpisodeSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    Catalog(
        podcastList = podcastList,
        latestEpisodeList = episodeList,
        onPodcastSelected = showPodcastDetails,
        onEpisodeSelected = onEpisodeSelected,
        modifier = modifier
            .focusRequester(focusRequester = focusRequester)
            .focusRestorer()
    )
}

@Composable
private fun NavigateToDiscover(
    onNavigationRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester: FocusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = stringResource(id = R.string.display_no_subscribed_podcast),
                style = MaterialTheme.typography.displayMedium
            )
            Text(text = stringResource(id = R.string.message_no_subscribed_podcast))
            Button(
                onClick = onNavigationRequested,
                modifier = Modifier
                    .padding(top = JetcasterAppDefaults.gap.podcastRow)
                    .focusRequester(focusRequester = focusRequester)
            ) {
                Text(text = stringResource(id = R.string.label_navigate_to_discover))
            }
        }
    }
}
