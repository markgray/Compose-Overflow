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

package com.example.jetcaster.tv.ui.podcast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.component.BackgroundContainer
import com.example.jetcaster.tv.ui.component.ButtonWithIcon
import com.example.jetcaster.tv.ui.component.EnqueueButton
import com.example.jetcaster.tv.ui.component.EpisodeDataAndDuration
import com.example.jetcaster.tv.ui.component.ErrorState
import com.example.jetcaster.tv.ui.component.InfoButton
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.PlayButton
import com.example.jetcaster.tv.ui.component.Thumbnail
import com.example.jetcaster.tv.ui.component.TwoColumn
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

/**
 * Displays the details screen for a specific podcast.
 *
 * This composable fetches and displays information about a podcast, including its description,
 * a list of episodes, and subscription status. It also provides interactions for playing episodes,
 * enqueueing episodes, viewing episode details, and managing the user's subscription.
 *
 * We start by initializing our [State] wrapped [PodcastScreenUiState] variable `uiState` by using
 * the [StateFlow.collectAsState] method of the [PodcastDetailsScreenViewModel.uiStateFlow] property
 * of our [PodcastDetailsScreenViewModel] parameter [podcastDetailsScreenViewModel]. Then in an
 * `when` statement we copy it to [PodcastScreenUiState] variable `s` and branch on the type of `s`:
 *  - [PodcastScreenUiState.Loading] -> We compose a [Loading] composable whose `modifier` argument
 *  is our [Modifier] parameter [modifier].
 *  - [PodcastScreenUiState.Error] -> We compose a [ErrorState] composable whose `backToHome` argument
 *  is our [backToHomeScreen] lambda parameter and whose `modifier` argument is our [Modifier] parameter
 *  [modifier].
 *  - [PodcastScreenUiState.Ready] -> We compose a [PodcastDetailsWithBackground] composable whose
 *  `podcastInfo` argument is the [PodcastScreenUiState.Ready.podcastInfo] property of `s`, whose
 *  `episodeList` argument is the [PodcastScreenUiState.Ready.episodeList] property of `s`, whose
 *  `isSubscribed` argument is the [PodcastScreenUiState.Ready.isSubscribed] property of `s`, whose
 *  `subscribe` argument is the [PodcastDetailsScreenViewModel.subscribe] method of our
 *  [PodcastDetailsScreenViewModel] parameter [podcastDetailsScreenViewModel], whose `unsubscribe`
 *  argument is the [PodcastDetailsScreenViewModel.unsubscribe] method of our
 *  [PodcastDetailsScreenViewModel] parameter [podcastDetailsScreenViewModel], whose `playEpisode`
 *  is a lambda that accepts the [PlayerEpisode] passed the lambda in variable `episodeToPlay` and
 *  calls the [PodcastDetailsScreenViewModel.play] method of our [PodcastDetailsScreenViewModel]
 *  parameter [podcastDetailsScreenViewModel] with `episodeToPlay` as its argument, and calls our
 *  [playEpisode] lambda parameter with `episodeToPlay` as its argument. The `enqueue` argument is
 *  is the [PodcastDetailsScreenViewModel.enqueue] method of our [PodcastDetailsScreenViewModel]
 *  parameter [podcastDetailsScreenViewModel], and the `showEpisodeDetails` argument is our
 *  [showEpisodeDetails] lambda parameter.
 *
 * @param backToHomeScreen Callback function invoked to navigate back to the home screen.
 * @param playEpisode Callback function invoked when an episode is selected for playback.
 * It receives the [PlayerEpisode] object representing the episode to be played as its argument.
 * @param showEpisodeDetails Callback function invoked when the user requests to view details
 * of a specific episode. It receives the [PlayerEpisode] object for which details should be shown
 * as its argument.
 * @param modifier [Modifier] for styling and layout customization. Our caller the `Route` composable
 * of `JetcasterApp` passes us a [Modifier.padding] (top = 40.dp, bottom = 40.dp, start = 80.dp,
 * end = 80.dp) to which it chains a [Modifier.fillMaxSize].
 * @param podcastDetailsScreenViewModel The [PodcastDetailsScreenViewModel] instance used to
 * manage the state and logic of this screen. Defaults to an instance provided by Hilt.
 */
@Composable
fun PodcastDetailsScreen(
    backToHomeScreen: () -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    showEpisodeDetails: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    podcastDetailsScreenViewModel: PodcastDetailsScreenViewModel = hiltViewModel(),
) {
    val uiState: PodcastScreenUiState by podcastDetailsScreenViewModel.uiStateFlow.collectAsState()
    when (val s: PodcastScreenUiState = uiState) {
        PodcastScreenUiState.Loading -> Loading(modifier = modifier)
        PodcastScreenUiState.Error -> ErrorState(backToHome = backToHomeScreen, modifier = modifier)
        is PodcastScreenUiState.Ready -> PodcastDetailsWithBackground(
            podcastInfo = s.podcastInfo,
            episodeList = s.episodeList,
            isSubscribed = s.isSubscribed,
            subscribe = podcastDetailsScreenViewModel::subscribe,
            unsubscribe = podcastDetailsScreenViewModel::unsubscribe,
            playEpisode = { episodeToPlay: PlayerEpisode ->
                podcastDetailsScreenViewModel.play(playerEpisode = episodeToPlay)
                playEpisode(episodeToPlay)
            },
            enqueue = podcastDetailsScreenViewModel::enqueue,
            showEpisodeDetails = showEpisodeDetails,
        )
    }
}

/**
 * Displays the details of a podcast, including its background image, description, and list of episodes.
 *
 * This composable function provides a visually appealing presentation of podcast information by
 * layering the [PodcastDetails] content on top of a [BackgroundContainer] which handles displaying
 * the podcast's background image. It also handles subscription status and user interactions.
 *
 * Our root composable is a [BackgroundContainer] whose `podcastInfo` argument is our [PodcastInfo]
 * parameter [podcastInfo], and whose `modifier` argument is our [Modifier] parameter [modifier].
 * In the [BoxScope] `content` composable lambda argument we compose a [PodcastDetails] composable
 * whose arguments are:
 *  - `podcastInfo` is our [PodcastInfo] parameter [podcastInfo],
 *  - `episodeList` is our [EpisodeList] parameter [episodeList],
 *  - `isSubscribed` is our [Boolean] parameter [isSubscribed],
 *  - `subscribe` is our [subscribe] lambda parameter,
 *  - `unsubscribe` is our [unsubscribe] lambda parameter,
 *  - `playEpisode` is our [playEpisode] lambda parameter,
 *  - `focusRequester` is our [FocusRequester] parameter [focusRequester],
 *  - `showEpisodeDetails` is our [showEpisodeDetails] lambda parameter,
 *  - `enqueue` is our [enqueue] lambda parameter,
 *  - `modifier` is a [Modifier.fillMaxSize]
 *
 * @param podcastInfo The [PodcastInfo] object containing the details of the podcast.
 * @param episodeList The [EpisodeList] object containing the list of episodes for the podcast.
 * @param isSubscribed A boolean indicating whether the user is currently subscribed to the podcast.
 * @param subscribe A lambda function to handle subscribing to the podcast. It takes the [PodcastInfo]
 * and a boolean (true for successful subscription) as parameters.
 * @param unsubscribe A lambda function to handle unsubscribing from the podcast. It takes the
 * [PodcastInfo] and a boolean (true for successful unsubscription) as parameters.
 * @param playEpisode A lambda function to handle playing a selected episode. It takes the
 * [PlayerEpisode] as a parameter.
 * @param showEpisodeDetails A lambda function to handle showing the details of a selected episode.
 * It takes the [PlayerEpisode] as a parameter.
 * @param enqueue A lambda function to handle adding an episode to the play queue. It takes the
 * [PlayerEpisode] as a parameter.
 * @param modifier The [Modifier] to be applied to the outer container. Our caller,
 * [PodcastDetailsScreen], does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param focusRequester The [FocusRequester] to be used for requesting focus on elements within
 * the podcast details. Defaults to a newly created [FocusRequester].
 */
@Composable
private fun PodcastDetailsWithBackground(
    podcastInfo: PodcastInfo,
    episodeList: EpisodeList,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    showEpisodeDetails: (PlayerEpisode) -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {

    BackgroundContainer(podcastInfo = podcastInfo, modifier = modifier) {
        PodcastDetails(
            podcastInfo = podcastInfo,
            episodeList = episodeList,
            isSubscribed = isSubscribed,
            subscribe = subscribe,
            unsubscribe = unsubscribe,
            playEpisode = playEpisode,
            focusRequester = focusRequester,
            showEpisodeDetails = showEpisodeDetails,
            enqueue = enqueue,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PodcastDetails(
    podcastInfo: PodcastInfo,
    episodeList: EpisodeList,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    showEpisodeDetails: (PlayerEpisode) -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    TwoColumn(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.twoColumn),
        first = {
            PodcastInfo(
                podcastInfo = podcastInfo,
                isSubscribed = isSubscribed,
                subscribe = subscribe,
                unsubscribe = unsubscribe,
                modifier = Modifier
                    .weight(weight = 0.3f)
                    .padding(
                        paddingValues = JetcasterAppDefaults.overScanMargin.podcast.copy(end = 0.dp)
                            .intoPaddingValues()
                    ),
            )
        },
        second = {
            PodcastEpisodeList(
                episodeList = episodeList,
                playEpisode = { playEpisode(it) },
                showDetails = showEpisodeDetails,
                enqueue = enqueue,
                modifier = Modifier
                    .focusRequester(focusRequester = focusRequester)
                    .focusRestorer()
                    .weight(weight = 0.7f)
            )
        }
    )

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun PodcastInfo(
    podcastInfo: PodcastInfo,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Thumbnail(podcastInfo = podcastInfo)
        Spacer(modifier = Modifier.height(height = 16.dp))

        Text(
            text = podcastInfo.author,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = podcastInfo.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = podcastInfo.description,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        ToggleSubscriptionButton(
            podcastInfo,
            isSubscribed,
            subscribe,
            unsubscribe,
            modifier = Modifier
                .padding(top = JetcasterAppDefaults.gap.podcastRow)
        )
    }
}

@Composable
private fun ToggleSubscriptionButton(
    podcastInfo: PodcastInfo,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = if (isSubscribed) {
        Icons.Default.Remove
    } else {
        Icons.Default.Add
    }
    val label: String = if (isSubscribed) {
        stringResource(id = R.string.label_unsubscribe)
    } else {
        stringResource(id = R.string.label_subscribe)
    }
    val action: (PodcastInfo, Boolean) -> Unit = if (isSubscribed) {
        unsubscribe
    } else {
        subscribe
    }
    ButtonWithIcon(
        label = label,
        icon = icon,
        onClick = { action(podcastInfo, isSubscribed) },
        scale = ButtonDefaults.scale(scale = 1f),
        modifier = modifier
    )
}

@Composable
private fun PodcastEpisodeList(
    episodeList: EpisodeList,
    playEpisode: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.podcastRow),
        modifier = modifier,
        contentPadding = JetcasterAppDefaults.overScanMargin.podcast.intoPaddingValues()
    ) {
        items(items = episodeList) { episode: PlayerEpisode ->
            EpisodeListItem(
                playerEpisode = episode,
                onEpisodeSelected = { playEpisode(episode) },
                onInfoClicked = { showDetails(episode) },
                onEnqueueClicked = { enqueue(episode) },
            )
        }
    }
}

@Composable
private fun EpisodeListItem(
    playerEpisode: PlayerEpisode,
    onEpisodeSelected: () -> Unit,
    onInfoClicked: () -> Unit,
    onEnqueueClicked: () -> Unit,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 12.dp,
) {
    var hasFocus: Boolean by remember {
        mutableStateOf(value = false)
    }
    val shape = RoundedCornerShape(size = cornerRadius)

    val backgroundColor: Color = if (hasFocus) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.Transparent
    }

    val borderColor: Color = if (hasFocus) {
        MaterialTheme.colorScheme.border
    } else {
        Color.Transparent
    }
    val elevation: Dp = if (hasFocus) {
        10.dp
    } else {
        0.dp
    }

    EpisodeListItemContentLayer(
        playerEpisode = playerEpisode,
        onEpisodeSelected = onEpisodeSelected,
        onInfoClicked = onInfoClicked,
        onEnqueueClicked = onEnqueueClicked,
        modifier = modifier
            .clip(shape = shape)
            .onFocusChanged { focusState: FocusState ->
                hasFocus = focusState.hasFocus
            }
            .border(width = borderWidth, color = borderColor, shape = shape)
            .background(color = backgroundColor)
            .shadow(elevation = elevation, shape = shape)
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 16.dp)
    )
}

@Composable
private fun EpisodeListItemContentLayer(
    playerEpisode: PlayerEpisode,
    onEpisodeSelected: () -> Unit,
    onInfoClicked: () -> Unit,
    onEnqueueClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val duration: Duration? = playerEpisode.duration
    val playButton: FocusRequester = remember { FocusRequester() }
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.tiny),
        ) {
            EpisodeTitle(playerEpisode = playerEpisode)
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = JetcasterAppDefaults.gap.default),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = JetcasterAppDefaults.gap.paragraph)
            ) {
                PlayButton(
                    onClick = onEpisodeSelected,
                    modifier = Modifier.focusRequester(focusRequester = playButton)
                )
                if (duration != null) {
                    EpisodeDataAndDuration(
                        offsetDateTime = playerEpisode.published,
                        duration = duration
                    )
                }
                Spacer(modifier = Modifier.weight(weight = 1f))
                EnqueueButton(onClick = onEnqueueClicked)
                InfoButton(onClick = onInfoClicked)
            }
        }
    }
}

@Composable
private fun EpisodeTitle(playerEpisode: PlayerEpisode, modifier: Modifier = Modifier) {
    Text(
        text = playerEpisode.title,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}
