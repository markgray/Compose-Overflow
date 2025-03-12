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

package com.example.jetcaster.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.window.layout.DisplayFeature
import com.example.jetcaster.R
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.ui.home.MainScreen
import com.example.jetcaster.ui.player.PlayerScreen

/**
 * Main composable function for the Jetcaster application.
 *
 * This function sets up the navigation and displays the appropriate screen based on the
 * current application state, including the online status and the device's display features.
 *
 * We start by initializing our [WindowAdaptiveInfo] variable `adaptiveInfo` to the current
 * [WindowAdaptiveInfo] calculated by the [currentWindowAdaptiveInfo] function. If the
 * [JetcasterAppState.isOnline] property of our [JetcasterAppState] parameter [appState] is `true`,
 * we compose a [NavHost] whose `navController` is the [JetcasterAppState.navController] property
 * of [appState], and whose `startDestination` is the [Screen.route] of [Screen.Home].
 *
 * Then in the [NavGraphBuilder] `builder` lambda argument of the [NavHost], we first add a
 * [composable] whose `route` is the [Screen.route] of [Screen.Home] and in whose
 * [AnimatedContentScope] `content` Composable lambda argument we accept the [NavBackStackEntry]
 * passed the lambda in variable `backStackEntry`. Then we compose a [MainScreen] composable whose
 * `windowSizeClass` argument is the [WindowAdaptiveInfo.windowSizeClass] property of
 * [WindowAdaptiveInfo] variable `adaptiveInfo`, and whose `navigateToPlayer` argument is a lambda
 * that accepts an [EpisodeInfo] object in variable
 * `episode` and calls the [JetcasterAppState.navigateToPlayer] method of [appState] with the
 * [EpisodeInfo.uri] property of the [EpisodeInfo] object as its `episodeUri` argument and
 * [NavBackStackEntry] variable `backStackEntry` as its `from` argument.
 *
 * Next in the [NavGraphBuilder] `builder` lambda argument of the [NavHost], we add another
 * [composable] whose `route` is the [Screen.route] of [Screen.Player] and in whose `content`
 * Composable lambda argument we compose a [PlayerScreen] composable whose `windowSizeClass`
 * argument is the [WindowAdaptiveInfo.windowSizeClass] property of [WindowAdaptiveInfo] variable
 * `adaptiveInfo`, whose `displayFeatures` argument is our [List] of [DisplayFeature] parameter
 * [displayFeatures], and whose `onBackPress` argument is the [JetcasterAppState.navigateBack]
 * method of [JetcasterAppState] parameter [appState].
 *
 * If the [JetcasterAppState.isOnline] property of [appState] is `false` however, we compose an
 * [OfflineDialog] composable whose `onRetry` lambda argument is a lambda that calls the
 * [JetcasterAppState.refreshOnline] method of [JetcasterAppState] parameter [appState].
 *
 * @param displayFeatures A list of [DisplayFeature] objects representing the folding features
 * or other unique characteristics of the device's display. Used for adapting the layout to
 * different form factors.
 * @param appState The [JetcasterAppState] instance that holds the application's state, including
 * navigation controller, online status, and other relevant information. Defaults to a remembered
 * state using [rememberJetcasterAppState].
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun JetcasterApp(
    displayFeatures: List<DisplayFeature>,
    appState: JetcasterAppState = rememberJetcasterAppState()
) {
    val adaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    if (appState.isOnline) {
        NavHost(
            navController = appState.navController,
            startDestination = Screen.Home.route
        ) {
            composable(route = Screen.Home.route) {
                backStackEntry: NavBackStackEntry ->
                MainScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    navigateToPlayer = { episode: EpisodeInfo ->
                        appState.navigateToPlayer(episodeUri = episode.uri, from = backStackEntry)
                    }
                )
            }
            composable(route = Screen.Player.route) {
                PlayerScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    displayFeatures = displayFeatures,
                    onBackPress = appState::navigateBack
                )
            }
        }
    } else {
        OfflineDialog { appState.refreshOnline() }
    }
}

/**
 * Displays a dialog informing the user that they are offline and provides a retry button.
 *
 * This composable shows an alert dialog with a title and message indicating a connection error.
 * It includes a "Retry" button that, when clicked, triggers the provided [onRetry] callback.
 * The dialog is non-dismissible by clicking outside of it or pressing the back button.
 *
 * Our root Composable is an [AlertDialog] whose arguments are:
 *  - `onDismissRequest`: This is an empty lambda function, indicating that the dialog should not
 *  be dismissed when the user interacts with the dialog.
 *  - `title`: A lambda that composes a [Text] composable displaying the title "Connection Error"
 *  - `text`: A lambda that composes a [Text] composable displaying the message with resource ID
 *  `R.string.connection_error_message` ("Unable to fetch podcasts feeds. Check your internet
 *  connection and try again.")
 *  - `confirmButton`: A lambda that composes a [TextButton] whose `onClick` lambda argument is
 *  our [onRetry] lambda parameter. In its [RowScope] `content` Composable lambda argument, we
 *  compose a [Text] whose `text` is the string resource with ID `R.string.retry_label` ("Retry")
 *
 * @param onRetry A callback function that is invoked when the user clicks the "Retry" button.
 */
@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.connection_error_title)) },
        text = { Text(text = stringResource(R.string.connection_error_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry_label))
            }
        }
    )
}
