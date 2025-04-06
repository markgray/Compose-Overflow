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

package com.example.jetcaster.tv.ui

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Typography
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerScope
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.ui.discover.DiscoverScreen
import com.example.jetcaster.tv.ui.episode.EpisodeScreen
import com.example.jetcaster.tv.ui.library.LibraryScreen
import com.example.jetcaster.tv.ui.player.PlayerScreen
import com.example.jetcaster.tv.ui.podcast.PodcastDetailsScreen
import com.example.jetcaster.tv.ui.profile.ProfileScreen
import com.example.jetcaster.tv.ui.search.SearchScreen
import com.example.jetcaster.tv.ui.settings.SettingsScreen
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import kotlinx.coroutines.flow.Flow

/**
 * The main composable for the Jetcaster application.
 *
 * This function serves as the entry point for the Jetcaster UI, handling navigation and overall
 * application state. It utilizes a [JetcasterAppState] to manage the application's current
 * state and navigation. It just calls our [Route] method with its `jetcasterAppState` argument
 * our [JetcasterAppState] parameter [jetcasterAppState].
 *
 * @param jetcasterAppState The state holder for the Jetcaster application. Defaults to a new
 * instance created by [rememberJetcasterAppState] if not provided.
 */
@Composable
fun JetcasterApp(jetcasterAppState: JetcasterAppState = rememberJetcasterAppState()) {
    Route(jetcasterAppState = jetcasterAppState)
}

/**
 * A container composable that wraps the main application content within a [NavigationDrawer].
 * It handles the navigation drawer's content, which includes links to different screens
 * like Profile, Search, Discover, Library, and Settings. It also manages focus within the drawer
 * for accessibility and keyboard navigation.
 *
 * We start by initializing and remembering our [FocusRequester] variables `discover` and `library`
 * to new instances. We then initialize our [State] wrapped [String] variable `currentRoute` to the
 * value returned by the [Flow.collectAsStateWithLifecycle] method of the
 * [JetcasterAppState.currentRouteFlow] property of our [JetcasterAppState] parameter
 * [jetcasterAppState] with an initial value of `null`.
 *
 * Our root composable is a [NavigationDrawer] whose `content` argument is our composable lambda
 * parameter [content] and whose `modifier` argument is our [Modifier] parameter [modifier]. In its
 * [NavigationDrawerScope] `drawerContent` composable lambda argument, we start by initializing
 * our [Boolean] variable `isClosed` to `true` if the [DrawerValue] passed the lambda in variable
 * `drawerValue` is equal to the value of the [DrawerValue.Closed] enum constant. Then we compose
 * a [Column] whose `modifier` argument a [Modifier.padding] whose `paddingValues` argument is
 * the constant `JetcasterAppDefaults.overScanMargin.drawer` (start = 16.dp, end = 16.dp) to which
 * is chained a [Modifier.focusProperties] whose `enter` lambda argument that switches on the
 * value of [String] variable `currentRoute` and returns the appropriate [FocusRequester] based on
 * the current route (either `discover` or `library`), and at the end of chain is a
 * [Modifier.focusGroup] which marks this component as a focus group.
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we compose:
 *
 * A [NavigationDrawerItem] whose arguments are:
 *  - `selected` is `true` if `isClosed` is `true` and `currentRoute` is equal to
 *  [Screen.Profile.route]
 *  - `onClick` is a lambda that calls the [JetcasterAppState.navigateToProfile] method of
 *  [jetcasterAppState].
 *  - `leadingContent` is an [Icon] whose `imageVector` argument is [Icons.Filled.Person] and
 *  whose `contentDescription` argument is `null`.
 *  - In its `content` composable lambda argument, we compose a [Column] whose `content` composable
 *  lambda argument a [Column] which contains a [Text] whose text is "Name" and a [Text] whose
 *  text is "Switch Account" and whose style is the [Typography.labelSmall] from our custom
 *  [MaterialTheme.typography].
 *
 * Next we compose a [Spacer] whose `modifier` argument is a [ColumnScope.weight] whose `weight`
 * is `1f`.
 *
 * We then compose another [NavigationDrawerItem] whose arguments are:
 *  - `selected` is `true` if `isClosed` is `true` and `currentRoute` is equal to
 *  [Screen.Search.route]
 *  - `onClick` is a lambda that calls the [JetcasterAppState.navigateToSearch] method of
 *  [jetcasterAppState].
 *  - `leadingContent` is an [Icon] whose `imageVector` argument is [Icons.Filled.Search] and
 *  whose `contentDescription` argument is `null`.
 *  - In its `content` composable lambda argument, we compose a [Text] whose text is "Search".
 *
 * We then compose another [NavigationDrawerItem] whose arguments are:
 *  - `selected` is `true` if `isClosed` is `true` and `currentRoute` is equal to
 *  [Screen.Discover.route]
 *  - `onClick` is a lambda that calls the [JetcasterAppState.navigateToDiscover] method of
 *  [jetcasterAppState].
 *  - `leadingContent` is an [Icon] whose `imageVector` argument is [Icons.Filled.Home] and
 *  whose `contentDescription` argument is `null`.
 *  - `modifier` is a [Modifier.focusRequester] whose `focusRequester` argument is `discover`.
 *  - In its `content` composable lambda argument, we compose a [Text] whose text is "Discover".
 *
 * We then compose another [NavigationDrawerItem] whose arguments are:
 *  - `selected` is `true` if `isClosed` is `true` and `currentRoute` is equal to
 *  [Screen.Library.route]
 *  - `onClick` is a lambda that calls the [JetcasterAppState.navigateToLibrary] method of
 *  [jetcasterAppState].
 *  - `leadingContent` is an [Icon] whose `imageVector` argument is [Icons.Filled.VideoLibrary] and
 *  whose `contentDescription` argument is `null`.
 *  - `modifier` is a [Modifier.focusRequester] whose `focusRequester` argument is `library`.
 *  - In its `content` composable lambda argument, we compose a [Text] whose text is "Library".
 *
 * We then compose a [Spacer] whose `modifier` argument is a [ColumnScope.weight] whose `weight`
 * is `1f`.
 *
 * We then compose another [NavigationDrawerItem] whose arguments are:
 *  - `selected` is `true` if `isClosed` is `true` and `currentRoute` is equal to
 *  [Screen.Settings.route]
 *  - `onClick` is a lambda that calls the [JetcasterAppState.navigateToSettings] method of
 *  [jetcasterAppState].
 *  - `leadingContent` is an [Icon] whose `imageVector` argument is [Icons.Filled.Settings] and
 *  whose `contentDescription` argument is `null`.
 *  - In its `content` composable lambda argument, we compose a [Text] whose text is "Settings".
 *
 * @param jetcasterAppState The [JetcasterAppState] instance that holds the navigation state
 * and provides navigation actions.
 * @param modifier Modifier for the root element of this composable.
 * @param content The composable content to be displayed within the [NavigationDrawer] when the
 * drawer is closed or overlayed.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun GlobalNavigationContainer(
    jetcasterAppState: JetcasterAppState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val (discover: FocusRequester, library: FocusRequester) = remember { FocusRequester.createRefs() }
    val currentRoute: String?
        by jetcasterAppState.currentRouteFlow.collectAsStateWithLifecycle(initialValue = null)

    NavigationDrawer(
        drawerContent = { drawerValue: DrawerValue ->
            val isClosed: Boolean = drawerValue == DrawerValue.Closed
            Column(
                modifier = Modifier
                    .padding(paddingValues = JetcasterAppDefaults.overScanMargin.drawer.intoPaddingValues())
                    .focusProperties {
                        enter = {
                            when (currentRoute) {
                                Screen.Discover.route -> discover
                                Screen.Library.route -> library
                                else -> FocusRequester.Default
                            }
                        }
                    }
                    .focusGroup()
            ) {
                NavigationDrawerItem(
                    selected = isClosed && currentRoute == Screen.Profile.route,
                    onClick = jetcasterAppState::navigateToProfile,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                ) {
                    Column {
                        Text(text = "Name")
                        Text(
                            text = "Switch Account",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(weight = 1f))
                NavigationDrawerItem(
                    selected = isClosed && currentRoute == Screen.Search.route,
                    onClick = jetcasterAppState::navigateToSearch,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                ) {
                    Text(text = "Search")
                }
                NavigationDrawerItem(
                    selected = isClosed && currentRoute == Screen.Discover.route,
                    onClick = jetcasterAppState::navigateToDiscover,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.focusRequester(focusRequester = discover)
                ) {
                    Text(text = "Discover")
                }
                NavigationDrawerItem(
                    selected = isClosed && currentRoute == Screen.Library.route,
                    onClick = jetcasterAppState::navigateToLibrary,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.focusRequester(focusRequester = library)
                ) {
                    Text(text = "Library")
                }
                Spacer(modifier = Modifier.weight(weight = 1f))
                NavigationDrawerItem(
                    selected = isClosed && currentRoute == Screen.Settings.route,
                    onClick = jetcasterAppState::navigateToSettings,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                ) {
                    Text(text = "Settings")
                }
            }
        },
        content = content,
        modifier = modifier
    )
}

@Composable
private fun Route(jetcasterAppState: JetcasterAppState) {
    NavHost(
        navController = jetcasterAppState.navHostController,
        startDestination = Screen.Discover.route
    ) {
        composable(route = Screen.Discover.route) {
            GlobalNavigationContainer(jetcasterAppState = jetcasterAppState) {
                DiscoverScreen(
                    showPodcastDetails = { podcast: PodcastInfo ->
                        jetcasterAppState.showPodcastDetails(podcastUri = podcast.uri)
                    },
                    playEpisode = {
                        jetcasterAppState.playEpisode()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composable(route = Screen.Library.route) {
            GlobalNavigationContainer(jetcasterAppState = jetcasterAppState) {
                LibraryScreen(
                    navigateToDiscover = jetcasterAppState::navigateToDiscover,
                    showPodcastDetails = { podcast: PodcastInfo ->
                        jetcasterAppState.showPodcastDetails(podcastUri = podcast.uri)
                    },
                    playEpisode = {
                        jetcasterAppState.playEpisode()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composable(route = Screen.Search.route) {
            SearchScreen(
                onPodcastSelected = { podcast: PodcastInfo ->
                    jetcasterAppState.showPodcastDetails(podcastUri = podcast.uri)
                },
                modifier = Modifier
                    .padding(paddingValues = JetcasterAppDefaults.overScanMargin.default.intoPaddingValues())
                    .fillMaxSize()
            )
        }

        composable(route = Screen.Podcast.route) {
            PodcastDetailsScreen(
                backToHomeScreen = jetcasterAppState::navigateToDiscover,
                playEpisode = {
                    jetcasterAppState.playEpisode()
                },
                showEpisodeDetails = { episode: PlayerEpisode ->
                    jetcasterAppState.showEpisodeDetails(
                        episodeUri = episode.uri
                    )
                },
                modifier = Modifier
                    .padding(paddingValues = JetcasterAppDefaults.overScanMargin.podcast.intoPaddingValues())
                    .fillMaxSize(),
            )
        }

        composable(route = Screen.Episode.route) {
            EpisodeScreen(
                playEpisode = {
                    jetcasterAppState.playEpisode()
                },
                backToHome = jetcasterAppState::backToHome,
            )
        }

        composable(route = Screen.Player.route) {
            PlayerScreen(
                backToHome = jetcasterAppState::backToHome,
                modifier = Modifier.fillMaxSize(),
                showDetails = jetcasterAppState::showEpisodeDetails,
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = JetcasterAppDefaults.overScanMargin.default.intoPaddingValues())
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = JetcasterAppDefaults.overScanMargin.default.intoPaddingValues())
            )
        }
    }
}
