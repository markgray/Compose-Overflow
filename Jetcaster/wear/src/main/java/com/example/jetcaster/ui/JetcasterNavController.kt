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

package com.example.jetcaster.ui

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.android.horologist.media.ui.navigation.NavigationScreens

/**
 * NavController extensions that links to the screens of the Jetcaster app.
 */
object JetcasterNavController {

    /**
     * Navigates the user to the "Your Podcasts" screen.
     *
     * This function utilizes the [NavController] to navigate to the destination
     * specified by [YourPodcasts.destination]. It provides a convenient and
     * encapsulated way to navigate to the "Your Podcasts" screen within the
     * application's navigation graph.
     *
     * @receiver [NavController] The navigation controller responsible for managing the app's
     * navigation.
     * @see YourPodcasts
     * @see NavController.navigate
     */
    fun NavController.navigateToYourPodcast() {
        navigate(route = YourPodcasts.destination())
    }

    /**
     * Navigates to the latest episodes screen.
     *
     * This function uses the [NavController] to navigate to the destination
     * defined by [LatestEpisodes.destination]. It provides a convenient
     * way to navigate to the screen where the latest episodes are displayed.
     *
     * @receiver The [NavController] used for navigation.
     */
    fun NavController.navigateToLatestEpisode() {
        navigate(route = LatestEpisodes.destination())
    }

    /**
     * Navigates to the podcast details screen for a given podcast URI.
     *
     * This function uses the [NavController] to navigate to the screen that displays the details
     * of a specific podcast. It constructs the destination route from the [String] parameter
     * [podcastUri].
     *
     * @param podcastUri The URI of the podcast whose details should be displayed. This string is
     * used to construct the navigation route and is passed as an argument to the podcast details
     * screen. It is expected to be a valid URI representing a podcast.
     *
     * @see PodcastDetails
     * @see NavController
     */
    fun NavController.navigateToPodcastDetails(podcastUri: String) {
        navigate(route = PodcastDetails.destination(podcastUri = podcastUri))
    }

    /**
     * Navigates the user to the "Up Next" screen.
     *
     * This extension function on [NavController] simplifies navigation to the Up Next
     * destination. It uses the predefined route from the [UpNext] object to perform
     * the navigation.
     *
     * @receiver [NavController] The navigation controller used to perform the navigation.
     * @see UpNext.destination
     */
    fun NavController.navigateToUpNext() {
        navigate(route = UpNext.destination())
    }

    /**
     * Navigates to the Episode screen.
     *
     * This function uses the [NavController] to navigate to the episode screen,
     * passing the episode URI as a parameter. It leverages the [Episode.destination]
     * function to construct the correct navigation route.
     *
     * @param episodeUri The URI of the episode to navigate to. This should be a string
     * representing a unique identifier or location of the episode.
     */
    fun NavController.navigateToEpisode(episodeUri: String) {
        navigate(route = Episode.destination(episodeUri = episodeUri))
    }
}

/**
 * Represents the "Your Podcasts" screen in the navigation hierarchy.
 *
 * This object is a singleton that encapsulates the navigation route and provides
 * a convenient way to access the destination string for navigating to the
 * "Your Podcasts" screen. It inherits from [NavigationScreens], which 
 * defines the common properties and behaviors for all navigation screens in the app.
 *
 * @property navRoute The unique string identifier for the "Your Podcasts" screen's navigation
 * route. This is used by the navigation component to identify and navigate to this screen.
 * In this case, the route is "yourPodcasts".
 */
object YourPodcasts : NavigationScreens(navRoute = "yourPodcasts") {
    /**
     * Returns the navigation route string for the current destination.
     *
     * This function provides the string representation of the navigation destination.
     * This string is used by navigation components to identify the screen being navigated to.
     *
     * @return A string representing the navigation route.
     */
    fun destination(): String = navRoute
}

/**
 * [LatestEpisodes] is a singleton object representing the "Latest Episodes" screen in the
 * application's navigation.
 *
 * It inherits from [NavigationScreens] and provides a convenient way to refer to the route
 * associated with the latest episodes screen.
 *
 * This object is primarily used for defining and accessing the navigation route for the
 * Latest Episodes section. It ensures consistency and type safety when navigating to this screen.
 * 
 * @property navRoute The navigation route string associated with the "Latest Episodes" screen.
 * It's defined as "latestEpisodes".
 *
 * @see NavigationScreens
 */
object LatestEpisodes : NavigationScreens(navRoute = "latestEpisodes") {
    /**
     * Returns the navigation route string for the current destination.
     *
     * This function provides the string representation of the navigation destination.
     * This string is used by navigation components to identify the screen being navigated to.
     *
     * @return A string representing the navigation route.
     */
    fun destination(): String = navRoute
}

/**
 * Represents the navigation details for the Podcast details screen.
 *
 * This object defines the route, arguments, and functions needed to navigate to and from
 * the Podcast details screen within the application's navigation graph.
 */
object PodcastDetails : NavigationScreens(navRoute = "podcast?podcastUri={podcastUri}") {
    /**
     * The name of the argument representing the podcast URI.
     */
    const val PODCAST_URI: String = "podcastUri"

    /**
     * Creates a destination string for a podcast based on its URI.
     *
     * This function takes a podcast URI as input, encodes it for safe inclusion in a URL,
     * and then constructs a destination string that can be used for navigation or deep linking.
     * The destination string is formatted as "podcast?podcastUri=<encoded_uri>",
     * where <encoded_uri> is the URI-encoded version of the input podcast URI.
     *
     * @param podcastUri The URI of the podcast (e.g., "https://example.com/podcast/123").
     * @return A destination string formatted as "podcast?podcastUri=<encoded_uri>".
     */
    fun destination(podcastUri: String): String {
        val encodedUri: String? = Uri.encode(podcastUri)
        return "podcast?$PODCAST_URI=$encodedUri"
    }

    /**
     * Defines the list of navigation arguments for this route.
     *
     * In this case, it specifies a single argument named [PODCAST_URI] which
     * represents the URI of a podcast. This argument is of type String and is
     * expected to be provided when navigating to this route.
     *
     * @property arguments The list of navigation arguments.
     * @see NamedNavArgument
     * @see NavType
     * @see navArgument
     */
    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(name = PODCAST_URI) {
                type = NavType.StringType
            },
        )
}

/**
 * Represents the navigation screen for displaying a single episode.
 *
 * This object defines the navigation route, argument key, and argument type
 * for the "episode" screen within the application's navigation graph. It
 * also provides a utility function to construct the full navigation destination
 * URI, including the encoded episode URI.
 */
object Episode : NavigationScreens(navRoute = "episode?episodeUri={episodeUri}") {
    /**
     * The name of the argument representing the episode URI.
     */
    const val EPISODE_URI: String = "episodeUri"

    /**
     * Constructs a destination string for navigating to an episode detail screen.
     *
     * This function takes an episode URI as input and encodes it for safe use in a URI string.
     * It then constructs a destination string in the format "episode?episodeUri={encodedUri}",
     * which can be used for navigation purposes.
     *
     * @param episodeUri The raw URI string representing the episode. This will be URL-encoded.
     * @return A destination string suitable for navigation, containing the encoded episode URI.
     */
    fun destination(episodeUri: String): String {
        /**
         * Encodes the provided episode URI for use in a URL.
         */
        val encodedUri: String? = Uri.encode(episodeUri)
        return "episode?$EPISODE_URI=$encodedUri"
    }

    /**
     * Defines the navigation arguments required by this destination.
     */
    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(name = EPISODE_URI) {
                type = NavType.StringType
            },
        )
}

/**
 * Represents the "Up Next" screen in the navigation hierarchy.
 *
 * This object defines the navigation route and related properties for the "Up Next" screen.
 * It inherits from [NavigationScreens], providing a common interface for all screens.
 *
 * The "Up Next" screen is used to display a list of items or actions that the user is expected
 * to engage with next.
 *
 * @property navRoute The unique string identifier used to navigate to this screen. It's set to
 * "upNext".
 */
object UpNext : NavigationScreens(navRoute = "upNext") {
    /**
     * Returns the navigation route string for the current destination.
     *
     * This function provides the string representation of the navigation destination.
     * This string is used by navigation components to identify the screen being navigated to.
     *
     * @return A string representing the navigation route.
     */
    fun destination(): String = navRoute
}
