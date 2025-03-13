/*
 * Copyright 2021 The Android Open Source Project
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

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * List of screens for [JetcasterApp]
 */
sealed class Screen(val route: String) {
    /**
     * Represents the "Home" screen in the application.
     *
     * This object encapsulates the route and any other screen-specific
     * configurations for the Home screen.
     *
     * @property route The unique route associated with the Home screen. In this case, it's "home".
     */
    object Home : Screen("home")

    /**
     * Represents the Player screen in the navigation graph.
     *
     * This object defines the route and provides a utility function for creating
     * specific routes to a particular episode's player screen.
     *
     * @property ARG_EPISODE_URI The argument key used to pass the episode URI to the Player screen.
     * This is used within the route string and for extracting the value in the screen's composable.
     * @property route The base route for the Player screen, including a placeholder for the episode
     * URI. It is defined as "player/{$ARG_EPISODE_URI}".
     */
    object Player : Screen(route = "player/{$ARG_EPISODE_URI}") {
        fun createRoute(episodeUri: String): String = "player/$episodeUri"
    }

    /**
     * Defines the navigation details for the Podcast Details screen.
     *
     * This object serves as a central point for managing the route and arguments
     * associated with the Podcast Details screen in the application's navigation.
     *
     * It provides:
     *   - A string identifier for the screen's route pattern, including a placeholder
     *   for the podcast URI.
     *   - A constant for the argument key used to pass the podcast URI.
     *   - A function to construct the specific route for a given podcast URI.
     */
    object PodcastDetails : Screen("podcast/{$ARG_PODCAST_URI}") {
        @Suppress("unused")
        val PODCAST_URI: String = "podcastUri"
        fun createRoute(podcastUri: String): String = "podcast/$podcastUri"
    }

    companion object {
        /**
         * Argument key used to pass the URI of a podcast to a destination.
         *
         * This constant is used as a key in a bundle or intent when navigating to a screen
         * that requires a podcast URI. The associated value should be a String representing
         * the URI (e.g., a URL or a file path) of the podcast.
         */
        const val ARG_PODCAST_URI: String = "podcastUri"

        /**
         *  The key used to pass the episode's URI as a String argument in a bundle or intent.
         */
        const val ARG_EPISODE_URI: String = "episodeUri"
    }
}

/**
 * Creates and remembers a [JetcasterAppState] instance.
 *
 * This function is a state holder creation and hoisting function for the Jetcaster application.
 * It provides a centralized place to manage the application's global state, including the
 * navigation controller and the application context.
 *
 * It uses [remember] to cache a new instance of [JetcasterAppState] across recompositions.
 * This prevents the creation of new instances on every recomposition, which can be expensive
 * and lead to unintended side effects.
 *
 * @param navController The [NavHostController] used for navigating between destinations in the
 * application. Defaults to a new [rememberNavController] if not provided.
 * @param context The [Context] of the application. Defaults to the current [LocalContext].
 * @return A remembered [JetcasterAppState] instance.
 */
@Composable
fun rememberJetcasterAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
): JetcasterAppState = remember(key1 = navController, key2 = context) {
    JetcasterAppState(navController = navController, context = context)
}

/**
 *  [JetcasterAppState] manages the overall state and navigation of the Jetcaster application.
 *
 *  It encapsulates the application's navigation controller, online status, and
 *  provides methods for navigating between different screens within the app.
 *
 *  @property navController The [NavHostController] responsible for managing navigation within the app.
 *  @property context The application [Context], used for accessing system services like the
 *  [ConnectivityManager].
 */
class JetcasterAppState(
    val navController: NavHostController,
    private val context: Context
) {
    /**
     * Indicates whether the device is currently connected to the internet.
     *
     * This property reflects the online status of the device based on the result of [checkIfOnline].
     * It's updated as a mutable state, allowing UI components to react to changes in connectivity.
     *
     * @see checkIfOnline
     */
    var isOnline: Boolean by mutableStateOf(value = checkIfOnline())
        private set

    /**
     * Refreshes the online status of the application.
     *
     * This function updates the [isOnline] property by calling the [checkIfOnline] function.
     * It essentially checks the current network connectivity and updates the internal state
     * to reflect whether the application is currently online or offline.
     *
     * Call this function whenever you need to re-evaluate the network connection status.
     */
    fun refreshOnline() {
        isOnline = checkIfOnline()
    }

    /**
     * Navigates to the player screen for the given episode URI.
     *
     * This function handles navigation to the player screen, ensuring that duplicate navigation
     * events are discarded by checking the lifecycle state of the calling navigation entry.
     *
     * @param episodeUri The URI of the episode to be played. This URI should identify the
     * audio/video content.
     * @param from The [NavBackStackEntry] from which the navigation is initiated. This is used to
     * check the lifecycle state and prevent duplicate navigation.
     *
     * @see NavHostController.navigate
     * @see NavBackStackEntry.lifecycleIsResumed
     * @see Screen.Player.createRoute
     */
    fun navigateToPlayer(episodeUri: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            val encodedUri: String = Uri.encode(episodeUri)
            navController.navigate(route = Screen.Player.createRoute(episodeUri = encodedUri))
        }
    }

    /**
     * Navigates the user to the Podcast Details screen.
     *
     * This function takes a podcast URI and the current navigation back stack entry as input.
     * It first checks if the current navigation back stack entry's lifecycle is resumed.
     * If it is, it encodes the podcast URI to ensure it's safe for use in a URL and then
     * navigates to the Podcast Details screen using the encoded URI.
     *
     * @param podcastUri The URI of the podcast to display details for. This should be a string
     * representing the podcast's unique identifier or resource locator.
     * @param from The current [NavBackStackEntry] from which the navigation is triggered. This is
     * used to check if the lifecycle is in a resumed state before navigation, preventing potential
     * issues with navigating from a non-active screen.
     *
     * @see NavHostController.navigate
     * @see NavBackStackEntry.lifecycleIsResumed
     * @see Uri.encode
     * @see Screen.PodcastDetails.createRoute
     */
    @Suppress("unused")
    fun navigateToPodcastDetails(podcastUri: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            val encodedUri: String = Uri.encode(podcastUri)
            navController.navigate(route = Screen.PodcastDetails.createRoute(podcastUri = encodedUri))
        }
    }

    /**
     * Navigates back to the previous destination in the navigation stack.
     *
     * This function utilizes the [NavHostController.popBackStack] method to remove the
     * current destination from the back stack and navigate to the immediately
     * preceding destination. If the back stack is empty, this function will do nothing.
     *
     * @see NavHostController.popBackStack
     */
    fun navigateBack() {
        navController.popBackStack()
    }

    /**
     * Checks if the device is currently online.
     *
     * This function determines the device's online status by checking for an active internet connection.
     * It uses different approaches based on the Android SDK version:
     *
     * - **Android Marshmallow (API 23) and above:**
     *   It utilizes the `NetworkCapabilities` API to check if the active network has both the
     *   `NET_CAPABILITY_INTERNET` and `NET_CAPABILITY_VALIDATED` capabilities. This ensures that
     *   the network can access the internet and has been validated as a functional connection.
     *
     * - **Below Android Marshmallow (API < 23):**
     *   It uses the deprecated `activeNetworkInfo` API to check if the active network is
     *   connected or connecting.
     *
     * @return `true` if the device is online (has a valid internet connection), `false` otherwise.
     */
    @Suppress("DEPRECATION")
    private fun checkIfOnline(): Boolean {
        val cm: ConnectivityManager? = getSystemService(context, ConnectivityManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities: NetworkCapabilities =
                cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
