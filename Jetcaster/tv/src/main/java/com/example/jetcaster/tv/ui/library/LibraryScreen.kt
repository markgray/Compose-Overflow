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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.TextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import androidx.tv.material3.Typography
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
 * to variable `s` and use a `when` statement to handle different states:
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
 *  `playerEpisode` argument `it`, and the `modifier` argument of the [Library] is our [Modifier]
 *  parameter [modifier].
 *
 * @param modifier [Modifier] for styling the Library screen. Our caller, the `Route` method of
 * `JetcasterApp`, passes us a [Modifier.fillMaxSize] instance.
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

/**
 * Displays the user's library, showing a catalog of podcasts and their latest episodes.
 *
 * This composable function acts as a wrapper for the [Catalog] composable, managing focus
 * and passing the necessary data and callbacks for displaying and interacting with the library.
 *
 * We start by composing a [LaunchedEffect] to run a corroutine to request focus on the library
 * when it's first rendered using the [FocusRequester.requestFocus] method of our [FocusRequester]
 * parameter [focusRequester] (this will explicitly request focus on the composable that is linked
 * with  [focusRequester]. In this case, it ensures that [Catalog] will gain focus when the screen
 * is loaded).
 *
 * Our root Composable [Catalog] is then composed with its `podcastList` argument our [PodcastList]
 * parameter [podcastList], its `latestEpisodeList` argument our [EpisodeList] parameter [episodeList],
 * its `onPodcastSelected` argument our [showPodcastDetails] lambda parameter, its `onEpisodeSelected`
 * argument our [onEpisodeSelected] lambda parameter, and its `modifier` argument chains to our
 * [Modifier] parameter [modifier] a [Modifier.focusRequester] whose `focusRequester` argument is our
 * [FocusRequester] parameter [focusRequester] (links the [Catalog] composable with the [FocusRequester]
 * instance) and at the end of the chain we add [Modifier.focusRestorer] to restore focus to the
 * [Catalog] when it becomes part of the composition tree (useful when navigating back and forth
 * between screens).
 *
 * @param podcastList A list of [PodcastInfo] representing the user's subscribed podcasts.
 * @param episodeList A list of [PlayerEpisode] representing the latest episodes from the
 * subscribed podcasts.
 * @param showPodcastDetails A lambda function that is called when a podcast is selected.
 * It receives the selected [PodcastInfo] as a parameter.
 * @param onEpisodeSelected A lambda function that is called when an episode is selected.
 * It receives the selected [PlayerEpisode] as a parameter.
 * @param modifier Modifier for styling and layout of the library. Our caller, [LibraryScreen],
 * passes us its own [Modifier] parameter which traces back to a [Modifier.fillMaxSize].
 * @param focusRequester A [FocusRequester] used to programmatically request focus on the library.
 *
 * @see Catalog
 * @see PodcastInfo
 * @see PlayerEpisode
 */
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

/**
 * Displays a message indicating that the user has no subscribed podcasts and
 * provides a button to navigate to the discover screen.
 *
 * We start by initializing and remembering our [FocusRequester] variable `val focusRequester` to a
 * new instance of [FocusRequester] (This will be used to programmatically request focus on our
 * [Button]). Then we compose a [LaunchedEffect] to run a corroutine to request focus on the button
 * when it's first rendered using the [FocusRequester.requestFocus] method of our [FocusRequester]
 * variable `focusRequester` (to which it is linked by a [Modifier.focusRequester]).
 *
 * Our root Composable [Box] is then composed with its `modifier` argument our [Modifier] parameter
 * [modifier], and its `contentAlignment` argument [Alignment.Center] (centers the content of the
 * [Box] vertically and horizontally). In the [BoxScope] `content` Composable argument of the [Box]
 * we compose a [Column] and in its [ColumnScope] `content` Composable argument we compose two
 * [Text]s and a [Button].
 *
 * The arguments of the first [Text] are:
 *  - `text`: The text to display in the first [Text], the [String] with resource ID
 *  `R.string.display_no_subscribed_podcast` ("Let's discover the podcasts!")
 *  - `style`: The [TextStyle] to apply to the text, the [Typography.displayMedium] of our custom
 *  [MaterialTheme.typography].
 *
 * The arguments of the second [Text] are:
 *  - `text`: The text to display in the second [Text], the [String] with resource ID
 *  `R.string.message_no_subscribed_podcast` ("You subscribe no podcast yet. Let's discover the
 *  podcasts and subscribe them!")
 *
 * The arguments of the [Button] are:
 *  - `onClick`: The callback to invoke when the button is clicked, our [onNavigationRequested]
 *  lambda parameter.
 *  - `modifier`: The [Modifier] to apply to the [Button], a [Modifier.padding] whose `top` is the
 *  constant `JetcasterAppDefaults.gap.podcastRow` (12.dp), with a [Modifier.focusRequester] chained
 *  to that whose `focusRequester` is our [FocusRequester] variable `focusRequester` (links the
 *  [Button] to the [FocusRequester] variable `focusRequester`).
 *
 * In the [RowScope] `content` Composable argument of the [Button] we compose a [Text] with its
 * `text` argument the [String] with resource ID `R.string.label_navigate_to_discover` ("Discover
 * the podcasts").
 *
 * @param onNavigationRequested A callback function invoked when the "Discover" button is clicked.
 * This function should handle the navigation to the discover screen.
 * @param modifier Modifier to be applied to the outer [Box]. Our caller, [LibraryScreen], passes
 * us its own [Modifier] parameter which traces back to a [Modifier.fillMaxSize].
 */
@Composable
private fun NavigateToDiscover(
    onNavigationRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * A [FocusRequester] used to programmatically request focus on our [Button].
     */
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
