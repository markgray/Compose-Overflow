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

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetcaster.core.player.model.PlayerEpisode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [JetcasterAppState] is a class responsible for managing the navigation and application state
 * of the Jetcaster app. It provides a set of functions to navigate between different screens
 * and to retrieve information about the current navigation state.
 *
 * @property navHostController The [NavHostController] that manages the navigation graph.
 */
class JetcasterAppState(
    val navHostController: NavHostController
) {

    /**
     * A [Flow] that emits the current route of the navigation host.
     *
     * This property provides a stream of the current route as a nullable String.
     * It is updated whenever the navigation back stack changes, reflecting the
     * current visible destination.
     *
     * The emitted values correspond to the `route` attribute defined for each
     * destination in the navigation graph. If there's no current destination
     * or if the destination doesn't have a defined route, it will emit `null`.
     *
     * **Use Cases:**
     *  - Observing the currently displayed screen in the navigation graph.
     *  - Performing actions or updating UI based on the current route.
     *  - Implementing analytics or logging for screen views.
     *  - Driving conditional UI changes depending on the current destination.
     *
     * **Note:** This flow emits values on the main thread.
     */
    val currentRouteFlow: Flow<String?> =
        navHostController.currentBackStackEntryFlow.map { navBack: NavBackStackEntry ->
            navBack.destination.route
        }

    /**
     * Navigates to the specified screen.
     *
     * This function utilizes the [NavHostController] to navigate to a new destination within the
     * navigation graph. It takes a [Screen] object as input, which defines the target destination,
     * and navigates to the [Screen.route] associated with that screen.
     *
     * @param screen The [Screen] object representing the destination to navigate to. The
     * [Screen.route] property is used to identify the specific route in the navigation graph.
     *
     * @see NavHostController.navigate
     * @see Screen
     */
    private fun navigate(screen: Screen) {
        navHostController.navigate(route = screen.route)
    }

    /**
     * Navigates the user to the Discover screen.
     *
     * This function is a convenience wrapper around the [navigate] function, specifically
     * designed to direct the user to the screen designated for content discovery.
     *
     * It utilizes the [Screen.Discover] object to identify the target destination
     * and calls the underlying navigation mechanism to perform the transition.
     */
    fun navigateToDiscover() {
        navigate(screen = Screen.Discover)
    }

    /**
     * Navigates the user to the Library screen.
     *
     * This function utilizes the [navigate] function to trigger navigation to the [Screen.Library]
     * destination.
     *
     * @see Screen.Library The screen destination representing the library section of the application.
     * @see navigate The underlying navigation function that handles the actual navigation.
     */
    fun navigateToLibrary() {
        navigate(screen = Screen.Library)
    }

    /**
     * Navigates the user to the profile screen.
     *
     * This function uses the navigation controller to navigate to the screen
     * represented by [Screen.Profile]. It's a simple wrapper around the
     * [navigate] function, providing a clear and concise way to navigate to
     * the user's profile from any screen where it's needed.
     *
     */
    fun navigateToProfile() {
        navigate(screen = Screen.Profile)
    }

    /**
     * Navigates the user to the Search screen.
     *
     * This function uses the navigation controller to navigate to the screen
     * represented by [Screen.Search]. It's a simple wrapper around the
     * [navigate] function, providing a clear and concise way to navigate to
     * the search feature from any screen where it's needed.
     *
     * @see Screen.Search The destination screen for navigation.
     * @see NavHostController The navigation controller used for screen transitions.
     */
    fun navigateToSearch() {
        navigate(screen = Screen.Search)
    }

    /**
     * Navigates the user to the Settings screen.
     *
     * This function triggers the navigation to the Settings screen, utilizing the
     * [navigate] function with the [Screen.Settings] destination.  It's a
     * convenience method to centralize the navigation logic for this specific screen.
     *
     * @see navigate
     * @see Screen.Settings
     */
    fun navigateToSettings() {
        navigate(screen = Screen.Settings)
    }

    /**
     * Navigates to the podcast details screen for a given podcast URI.
     *
     * This function encodes the provided podcast URI to ensure it's properly formatted for navigation.
     * It then creates a [Screen.Podcast] object with the encoded URI and uses the [navigate]
     * function to display the podcast details screen.
     *
     * @param podcastUri The URI of the podcast to show details for. This should be a valid URI string
     * representing the podcast's location or identifier. Example : "https://example.com/podcast/123"
     * @throws IllegalArgumentException if the provided `podcastUri` is not a valid or well formed URI.
     */
    fun showPodcastDetails(podcastUri: String) {
        val encodedUrL: String = Uri.encode(podcastUri)
        val screen = Screen.Podcast(podcastUri = encodedUrL)
        navigate(screen = screen)
    }

    /**
     * Navigates to the episode details screen for the specified episode.
     *
     * This function takes the URI of an episode as a string, encodes it for URL safety,
     * constructs a [Screen.Episode] object with the encoded URI, and then calls the
     * [navigate] function to navigate to the episode details screen.
     *
     * @param episodeUri The raw URI string of the episode to display details for.
     * This URI should uniquely identify the episode.
     */
    fun showEpisodeDetails(episodeUri: String) {
        val encodeUrl: String = Uri.encode(episodeUri)
        val screen = Screen.Episode(episodeUri = encodeUrl)
        navigate(screen = screen)
    }

    /**
     * Displays the details of an episode.
     *
     * This function serves as a convenient way to show episode details when provided with a
     * [PlayerEpisode] object. It internally extracts the episode's URI from the [PlayerEpisode]
     * and then calls the overloaded [showEpisodeDetails] function that takes an episode URI as
     * its argument.
     *
     * @param playerEpisode The [PlayerEpisode] object containing the episode's information,
     * including the URI.
     */
    fun showEpisodeDetails(playerEpisode: PlayerEpisode) {
        showEpisodeDetails(episodeUri = playerEpisode.uri)
    }

    /**
     * Navigates to the player screen to play an episode.
     *
     * This function is responsible for initiating the playback of an episode by
     * navigating the user to the dedicated player screen within the application.
     * It just calls the [navigate] function with the [Screen.Player] object as its
     * `screen` argument.
     */
    fun playEpisode() {
        navigate(screen = Screen.Player)
    }

    /**
     * Navigates the user back to the previous screen in the navigation stack and then to the
     * "Discover" screen.
     *
     * This function performs the following actions:
     * 1. **Pops the back stack:** Removes the current screen from the navigation stack using
     * `navHostController.popBackStack()`. This effectively takes the user back to the screen
     * they were previously on.
     * 2. **Navigates to Discover:** After popping the back stack, it calls `navigateToDiscover()`,
     * which navigates the user to the designated "Discover" screen.
     *
     * This is useful when you want to ensure that the user ends up on the "Discover" screen after
     * potentially moving back from a deeper navigation point. For example, after completing an
     * action on a detail screen, you might want to use this to go back to the previous screen,
     * and from there straight to Discover.
     *
     * Note that if there is no screen in the backstack `navHostController.popBackStack()` will
     * do nothing.
     *
     * @see NavHostController.popBackStack
     * @see navigateToDiscover
     */
    fun backToHome() {
        navHostController.popBackStack()
        navigateToDiscover()
    }
}

@Composable
fun rememberJetcasterAppState(
    navHostController: NavHostController = rememberNavController()
): JetcasterAppState =
    remember(key1 = navHostController) {
        JetcasterAppState(navHostController = navHostController)
    }

sealed interface Screen {
    val route: String

    data object Discover : Screen {
        override val route: String = "/discover"
    }

    data object Library : Screen {
        override val route: String = "/library"
    }

    data object Search : Screen {
        override val route: String = "/search"
    }

    data object Profile : Screen {
        override val route: String = "/profile"
    }

    data object Settings : Screen {
        override val route: String = "settings"
    }

    data class Podcast(private val podcastUri: String) : Screen {
        override val route: String = "$ROOT/$podcastUri"

        companion object : Screen {
            private const val ROOT = "/podcast"
            const val PARAMETER_NAME: String = "podcastUri"
            override val route: String = "$ROOT/{$PARAMETER_NAME}"
        }
    }

    data class Episode(private val episodeUri: String) : Screen {

        override val route: String = "$ROOT/$episodeUri"

        companion object : Screen {
            private const val ROOT = "/episode"
            const val PARAMETER_NAME: String = "episodeUri"
            override val route: String = "$ROOT/{$PARAMETER_NAME}"
        }
    }

    data object Player : Screen {
        override val route: String = "player"
    }
}
