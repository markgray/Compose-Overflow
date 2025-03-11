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
     * configurations for the Home screen.  It's designed to be used within
     * a navigation system, likely with Jetpack Compose's Navigation library.
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
    object Player : Screen("player/{$ARG_EPISODE_URI}") {
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
         *  This URI typically points to the audio file of the episode.
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
 * It uses [remember] to cache the [JetcasterAppState] instance across recompositions.
 * This prevents the creation of new instances on every recomposition, which can be
 * expensive and lead to unintended side effects.
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
): JetcasterAppState = remember(navController, context) {
    JetcasterAppState(navController, context)
}

class JetcasterAppState(
    val navController: NavHostController,
    private val context: Context
) {
    var isOnline: Boolean by mutableStateOf(checkIfOnline())
        private set

    fun refreshOnline() {
        isOnline = checkIfOnline()
    }

    fun navigateToPlayer(episodeUri: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            val encodedUri = Uri.encode(episodeUri)
            navController.navigate(Screen.Player.createRoute(encodedUri))
        }
    }

    @Suppress("unused")
    fun navigateToPodcastDetails(podcastUri: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            val encodedUri = Uri.encode(podcastUri)
            navController.navigate(Screen.PodcastDetails.createRoute(encodedUri))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    @Suppress("DEPRECATION")
    private fun checkIfOnline(): Boolean {
        val cm = getSystemService(context, ConnectivityManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
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
