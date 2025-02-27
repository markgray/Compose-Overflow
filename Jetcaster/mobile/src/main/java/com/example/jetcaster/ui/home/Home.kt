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

package com.example.jetcaster.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.occludingVerticalHingeBounds
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewCategories
import com.example.jetcaster.core.domain.testing.PreviewPodcastEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.LibraryInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.ui.home.discover.discoverItems
import com.example.jetcaster.ui.home.library.libraryItems
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen
import com.example.jetcaster.ui.podcast.PodcastDetailsViewModel
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.tooling.DevicePreviews
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.fullWidthItem
import com.example.jetcaster.util.isCompact
import com.example.jetcaster.util.quantityStringResource
import com.example.jetcaster.util.radialGradientScrim
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

/**
 * Determines whether the main pane of a [SupportingPaneScaffold] is currently hidden.
 *
 * This function checks the current state of the [SupportingPaneScaffold] to see if the pane assigned
 * the [SupportingPaneScaffoldRole.Main] role is in the [PaneAdaptedValue.Hidden] state.
 *
 * @receiver [ThreePaneScaffoldNavigator] <[T]> The navigator instance providing access to the
 * [SupportingPaneScaffold]'s current state.
 * @return Boolean `true` if the main pane is hidden, `false` otherwise.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isMainPaneHidden(): Boolean {
    return scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden
}

/**
 * Calculates the [PaneScaffoldDirective] based on the provided [WindowAdaptiveInfo] and [HingePolicy].
 *
 * This function determines how a pane scaffold should be configured for a given window state,
 * considering factors such as window size class, posture (e.g., tabletop), and hinge position.
 * It computes the maximum number of horizontal and vertical partitions, as well as the spacer sizes
 * between panes, and any areas to be avoided.
 *
 * Copied from `calculatePaneScaffoldDirective()` in [PaneScaffoldDirective], with modifications to
 * only show 1 pane horizontally if either width or height size class is compact.
 *
 * @param windowAdaptiveInfo Information about the current window state, including size class
 * and posture.
 * @param verticalHingePolicy The policy for handling vertical hinges, determining whether they
 * should be avoided. Defaults to [HingePolicy.AvoidSeparating], meaning that content should not
 * be split by the hinge.
 * @return A [PaneScaffoldDirective] object containing the calculated configuration for the
 * pane scaffold.
 *
 * @see PaneScaffoldDirective
 * @see WindowAdaptiveInfo
 * @see HingePolicy
 * @see WindowWidthSizeClass
 */
fun calculateScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating
): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val verticalSpacerSize: Dp
    if (windowAdaptiveInfo.windowSizeClass.isCompact) {
        // Window width or height is compact. Limit to 1 pane horizontally.
        maxHorizontalPartitions = 1
        verticalSpacerSize = 0.dp
    } else {
        when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            WindowWidthSizeClass.MEDIUM -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            else -> {
                maxHorizontalPartitions = 2
                verticalSpacerSize = 24.dp
            }
        }
    }
    val maxVerticalPartitions: Int
    val horizontalSpacerSize: Dp

    if (windowAdaptiveInfo.windowPosture.isTabletop) {
        maxVerticalPartitions = 2
        horizontalSpacerSize = 24.dp
    } else {
        maxVerticalPartitions = 1
        horizontalSpacerSize = 0.dp
    }

    val defaultPanePreferredWidth = 360.dp

    return PaneScaffoldDirective(
        maxHorizontalPartitions,
        verticalSpacerSize,
        maxVerticalPartitions,
        horizontalSpacerSize,
        defaultPanePreferredWidth,
        getExcludedVerticalBounds(windowAdaptiveInfo.windowPosture, verticalHingePolicy)
    )
}

/**
 * Retrieves a list of vertical rectangular bounds that should be excluded based on the given
 * posture and hinge policy.
 *
 * This function determines which vertical hinge bounds should be considered as "excluded" areas
 * based on the current device posture and the specified hinge policy. These excluded bounds
 * typically represent areas where content should avoid being displayed to ensure optimal user
 * experience on foldable or dual-screen devices.
 *
 * Copied from `getExcludedVerticalBounds()` in [PaneScaffoldDirective] since it is private.
 *
 * @param posture The current posture of the device, which provides information about the hinge
 * state and position.
 * @param hingePolicy The policy determining how the hinge area should be treated:
 * - [HingePolicy.AvoidSeparating]: Content should avoid being split across a separating hinge.
 * - [HingePolicy.AvoidOccluding]: Content should avoid being occluded by a hinge.
 * - [HingePolicy.AlwaysAvoid]: Content should always avoid the hinge area, regardless of separation
 * or occlusion.
 *
 * @return A list of [Rect] objects representing the vertical rectangular areas to be excluded.
 * Returns an empty list if no bounds should be excluded based on the policy or if the hinge is not
 * in a relevant state.
 */
private fun getExcludedVerticalBounds(posture: Posture, hingePolicy: HingePolicy): List<Rect> {
    return when (hingePolicy) {
        HingePolicy.AvoidSeparating -> posture.separatingVerticalHingeBounds
        HingePolicy.AvoidOccluding -> posture.occludingVerticalHingeBounds
        HingePolicy.AlwaysAvoid -> posture.allVerticalHingeBounds
        else -> emptyList()
    }
}

/**
 * The main screen of the application, responsible for displaying the home screen content
 * and handling navigation to the player screen.
 *
 * This composable orchestrates the display of the home screen based on the current UI state
 * and window size class. It also manages error handling and provides a retry mechanism.
 *
 * Flow:
 * 1. Collects the [HomeScreenUiState] from the [HomeViewModel].
 * 2. Displays the [HomeScreenReady] composable, passing the UI state, window size class,
 * navigation lambda, and ViewModel.
 * 3. If the UI state contains an error message, displays the [HomeScreenError] composable
 * with a retry button that calls the [HomeViewModel.refresh] function.
 * 4. Uses [Box] to layer [HomeScreenReady] and [HomeScreenError] when needed.
 *
 * State:
 * The UI state is represented by [HomeScreenUiState], which is collected from the [HomeViewModel].
 *
 * Dependencies:
 * - [WindowSizeClass] from `androidx.compose.material3.windowsizeclass`
 * - [HomeScreenUiState] Defined in [HomeViewModel]
 * - [EpisodeInfo] defined by the [EpisodeInfo] data class.
 *
 * @param windowSizeClass The window size class of the current device, used for adaptive UI.
 * @param navigateToPlayer A lambda function that triggers navigation to the player screen,
 * receiving an [EpisodeInfo] as parameter representing the selected episode.
 * @param viewModel The [HomeViewModel] instance used to manage the home screen's data and state.
 * Defaults to a ViewModel provided by Hilt.
 */
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeScreenUiState: HomeScreenUiState by viewModel.state.collectAsStateWithLifecycle()
    val uiState: HomeScreenUiState = homeScreenUiState
    Box {
        HomeScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateToPlayer = navigateToPlayer,
            viewModel = viewModel,
        )

        if (uiState.errorMessage != null) {
            HomeScreenError(onRetry = viewModel::refresh)
        }
    }
}

/**
 * Displays an error screen with a retry button.
 *
 * This composable function shows an error message and a button that allows the user to retry
 * the operation that failed. It's typically used to inform the user that something went wrong
 * and to provide a way to try again.
 *
 * @param onRetry A lambda function that is called when the retry button is clicked. This should
 * contain the logic to re-attempt the failed operation (e.g., fetching data again).
 * @param modifier Modifier for styling and layout of the error screen. Defaults to an empty
 * modifier.
 */
@Composable
private fun HomeScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(id = R.string.an_error_has_occurred),
                modifier = Modifier.padding(all = 16.dp)
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry_label))
            }
        }
    }
}

/**
 * Preview of the [HomeScreenError] composable.
 */
@Preview
@Composable
fun HomeScreenErrorPreview() {
    JetcasterTheme {
        HomeScreenError(onRetry = {})
    }
}

/**
 * Composable function that renders the main home screen and handles navigation to the podcast
 * details screen.
 *
 * This function utilizes [SupportingPaneScaffold] to manage a two-pane layout, allowing for the
 * simultaneous display of the home screen and a podcast details screen on larger screens. It also
 * handles back navigation and fetching the correct view models.
 *
 * We start by creating and remembering a [ThreePaneScaffoldNavigator] with its `scaffoldDirective`
 * argument a [calculateScaffoldDirective] constructed with its `windowAdaptiveInfo` argument the
 * [WindowAdaptiveInfo] of the current window returned by the [currentWindowAdaptiveInfo] method
 * (this determines how the two-pane layout should be displayed based on the current window size).
 * Next we compose a [BackHandler] whose `enabled` property is set to `navigator.canNavigateBack()`
 * (returns `true` if there is a previous destination to navigate back to) and whose `onBack` lambda
 * argument is a lambda that calls the `navigator.navigateBack()` method (Navigates to the previous
 * destination).
 *
 * Our root composable is a [Surface] whose `content` composable lambda is a [SupportingPaneScaffold]
 * whose arguments are:
 *  1. `value` is the [ThreePaneScaffoldNavigator.scaffoldValue] of the `navigator` variable (The
 *  current layout value of the associated three pane scaffold value, which represents unique layout
 *  states of the scaffold).
 *  2. `directive` is the [ThreePaneScaffoldNavigator.scaffoldDirective] of the `navigator` variable
 *  (The current layout directives that the associated three pane scaffold needs to follow. It's
 *  supposed to be automatically updated when the window configuration changes).
 *  3. `mainPane` is a lambda that renders the [HomeScreen] home screen content whose arguments are:
 *      - `windowSizeClass` is our [WindowSizeClass] parameter [windowSizeClass].
 *      - `isLoading` is the [HomeScreenUiState.isLoading] property of our [HomeScreenUiState]
 *      parameter [uiState].
 *      - `featuredPodcasts` is the [HomeScreenUiState.featuredPodcasts] property of our
 *      [HomeScreenUiState] parameter [uiState].
 *      - `homeCategories` is the [HomeScreenUiState.homeCategories] property of our
 *      [HomeScreenUiState] parameter [uiState].
 *      - `selectedHomeCategory` is the [HomeScreenUiState.selectedHomeCategory] property of our
 *      [HomeScreenUiState] parameter [uiState].
 *      - `filterableCategoriesModel` is the [HomeScreenUiState.filterableCategoriesModel] of our
 *      [HomeScreenUiState] parameter [uiState].
 *      - `podcastCategoryFilterResult` is the [HomeScreenUiState.podcastCategoryFilterResult] of our
 *      [HomeScreenUiState] parameter [uiState].
 *      - `library` is the [HomeScreenUiState.library] property of our [HomeScreenUiState] parameter
 *      [uiState].
 *      - `onHomeAction` is a lambda that calls the [HomeViewModel.onHomeAction] method of our
 *      [HomeViewModel] parameter [viewModel]. This called by the [HomeScreen] composable with
 *      different [HomeAction] values as its argument depending on what the user has selected.
 *      - `navigateToPodcastDetails` is a lambda that calls the [ThreePaneScaffoldNavigator.navigateTo]
 *      method of our [ThreePaneScaffoldNavigator] variable `navigator` with its `pane` argument
 *      [SupportingPaneScaffoldRole.Supporting], and its `content` argument the [PodcastInfo.uri]
 *      that is passed to the lambda (this navigates to the [SupportingPaneScaffoldRole.Supporting]
 *      pane whose [PodcastDetailsScreen] will display the podcast whose URI is the `content`
 *      argument of the `navigator.navigateTo` call).
 *      - `navigateToPlayer` is our [navigateToPlayer] lambda parameter.
 *      - `modifier` is [Modifier.fillMaxSize].
 *  4. `supportingPane` is a lambda which:
 *      - first initializes its [String] variable `podcastUri` with the `content` of the
 *      [ThreePaneScaffoldNavigator.currentDestination] property of our [ThreePaneScaffoldNavigator]
 *      variable `navigator` if it is not null.
 *      - if `podcastUri` is not empty or `null`, initializes a [PodcastDetailsViewModel] variable
 *      `podcastDetailsViewModel` with its `create` lambda argument using the `podcastUri` variable
 *      as its `podcastUri` argument.
 *  5. Then `supportingPane` renders the [PodcastDetailsScreen] composable whose arguments are:
 *      - `viewModel` is our [PodcastDetailsViewModel] variable `podcastDetailsViewModel`.
 *      - `navigateToPlayer` is our [navigateToPlayer] lambda parameter.
 *      - `navigateBack` is a lambda that calls the `navigator.navigateBack()` method if
 *      `navigator.canNavigateBack()` is true.
 *      - `showBackButton` is a boolean that is set to `navigator.isMainPaneHidden()`
 *
 * @param uiState The [HomeScreenUiState] representing the current state of the home screen.
 * @param windowSizeClass The [WindowSizeClass] of the current window.
 * @param navigateToPlayer A lambda function to navigate to the player screen, accepting an
 * [EpisodeInfo] as its argument.
 * @param viewModel The [HomeViewModel] for handling home screen actions. Defaults to a new
 * instance from hilt.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun HomeScreenReady(
    uiState: HomeScreenUiState,
    windowSizeClass: WindowSizeClass,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navigator: ThreePaneScaffoldNavigator<String> = rememberSupportingPaneScaffoldNavigator<String>(
        scaffoldDirective = calculateScaffoldDirective(windowAdaptiveInfo = currentWindowAdaptiveInfo())
    )
    BackHandler(enabled = navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    Surface {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                HomeScreen(
                    windowSizeClass = windowSizeClass,
                    isLoading = uiState.isLoading,
                    featuredPodcasts = uiState.featuredPodcasts,
                    homeCategories = uiState.homeCategories,
                    selectedHomeCategory = uiState.selectedHomeCategory,
                    filterableCategoriesModel = uiState.filterableCategoriesModel,
                    podcastCategoryFilterResult = uiState.podcastCategoryFilterResult,
                    library = uiState.library,
                    onHomeAction = viewModel::onHomeAction,
                    navigateToPodcastDetails = {
                        navigator.navigateTo(
                            pane = SupportingPaneScaffoldRole.Supporting,
                            content = it.uri
                        )
                    },
                    navigateToPlayer = navigateToPlayer,
                    modifier = Modifier.fillMaxSize()
                )
            },
            supportingPane = {
                val podcastUri: String? = navigator.currentDestination?.content
                if (!podcastUri.isNullOrEmpty()) {
                    val podcastDetailsViewModel: PodcastDetailsViewModel =
                        hiltViewModel<PodcastDetailsViewModel, PodcastDetailsViewModel.Factory>(
                            key = podcastUri
                        ) {
                            it.create(podcastUri = podcastUri)
                        }
                    PodcastDetailsScreen(
                        viewModel = podcastDetailsViewModel,
                        navigateToPlayer = navigateToPlayer,
                        navigateBack = {
                            if (navigator.canNavigateBack()) {
                                navigator.navigateBack()
                            }
                        },
                        showBackButton = navigator.isMainPaneHidden(),
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Composable function representing the Home screen's app bar.
 *
 * This app bar displays a search bar that allows users to search for podcasts.
 * It also includes an account icon for profile access.
 *
 * We start by initializings and remembering our [MutableState] wrapped [String] variable
 * `var queryText` to an initial value of the empty [String]. Then our root composable is a [Row]
 * whose `horizontalArrangement` argument is [Arrangement.End] (Places children horizontally such
 * that they are as close as possible to the end of the main axis), and whose [Modifier] `modifier`
 * argument chains a [Modifier.fillMaxWidth] to our [Modifier] parameter [modifier], followed by
 * [Modifier.background] whose `color` is [Color.Transparent], and at the end of the chain a
 * [Modifier.padding] that adds 16.dp to each `horizontal` side and 8.dp to each `vertical` side.
 * In the [RowScope] `content` composable lambda argument of the [Row] we compose a [SearchBar].
 * The `inputField` argument of the [SearchBar] is a lambda that renders a [SearchBarDefaults.InputField]
 * whose `query` argument is our [MutableState] wrapped [String] variable `queryText`, and whose
 * `onQueryChange` argument is a lambda that updates our [MutableState] wrapped [String] variable
 * with the new [String] value passed the lambda. The `onSearch` argument is set to an empty lambda,
 * and the `expanded` and `onExpandedChange` arguments are set to `false` and an empty lambda
 * respectively. The `enabled` argument is set to `true`. The `placeholder` argument is a lambda
 * that composes a [Text] with the `text` argument the string whose ID is `R.string.search_for_a_podcast`
 * ("Search for a podcast"). The `leadingIcon` argument is a lambda that composes an [Icon] whose
 * `imageVector` is [Icons.Filled.Search] and whose `contentDescription` is `null`. The `trailingIcon`
 * argument is a lambda that composes an [Icon] whose `imageVector` is [Icons.Filled.AccountCircle]
 * and whose `contentDescription` is the string whose ID is `R.string.cd_account` ("Account"). The
 * `interactionSource` argument is `null`. The `modifier` argument is [Modifier.fillMaxWidth] if
 * `isExpanded` is `true`, or [Modifier] otherwise.
 *
 * The `expanded` argument of the [SearchBar] is set to `false` and the `onExpandedChange` argument
 * is an empty lambda. The `content` lambda argument of the [SearchBar] is an empty lambda.
 *
 * @param isExpanded Boolean indicating whether the parent layout is in an expanded state. This
 * affects the width of the search bar. If `true`, the search bar will occupy the full available
 * width. If `false`, the search bar will have a default width.
 * @param modifier Modifier for customizing the layout and appearance of the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeAppBar(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    var queryText: String by remember {
        mutableStateOf(value = "")
    }
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = queryText,
                    onQueryChange = { queryText = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    enabled = true,
                    placeholder = {
                        Text(text = stringResource(id = R.string.search_for_a_podcast))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(id = R.string.cd_account)
                        )
                    },
                    interactionSource = null,
                    modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier
                )
            },
            expanded = false,
            onExpandedChange = {}
        ) {}
    }
}

/**
 * Creates a background for the Home Screen with a radial gradient overlay.
 *
 * This composable provides a background that matches the Material Theme's
 * `background` color and adds a subtle radial gradient effect to enhance the
 * visual depth. The gradient emanates from the center of the screen with a
 * soft, translucent primary color overlay.
 *
 * Our root composable is a [Box] whose `modifier` argument chains a [Modifier.background] to our
 * [Modifier] parameter [modifier] whose `color` is the [ColorScheme.background] of our custom
 * [MaterialTheme.colorScheme]. In its `content` lambda argument we compose another [Box] whose
 * `modifier` argument is a [Modifier.fillMaxSize], with a [Modifier.radialGradientScrim] chained
 * to that whose `color` argument is a copy of the [ColorScheme.primary] of our custom
 * [MaterialTheme.colorScheme] with its `alpha` property set to 0.15f. The [BoxScope] `content`
 * composable lambda argument of the [Box] composes our [content] Composable lambda parameter. 
 *
 * @param modifier The modifier to be applied to the background Box.
 * @param content The content to be displayed on top of the background. This lambda receives a
 * [BoxScope] which allows using Box-specific modifiers like `align` and `matchParentSize`.
 */
@Composable
private fun HomeScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        )
        content()
    }
}

/**
 * Displays the main home screen of the podcast application.
 *
 * This composable function orchestrates the layout and content of the home screen,
 * including the top app bar, loading indicator, main content area, and snackbar.
 * It handles displaying featured podcasts, home categories, filtering options,
 * and user library information.
 *
 * First we compose a [LaunchedEffect] whose `key1` argument is `featuredPodcasts` (it will run
 * whenever the value of [featuredPodcasts] changes). In the `block` composable lambda argument we
 * check if the [featuredPodcasts] list is empty. If it is, we we call our lambda parameter
 * [onHomeAction] (our caller passes a function reference to the [HomeViewModel.onHomeAction]
 * method) with a [HomeAction.HomeCategorySelected] action whose `category` argument is
 * [HomeCategory.Discover] (this will set the [selectedHomeCategory] to [HomeCategory.Discover] in
 * the [HomeScreenUiState] which will trigger a recomposition of the [LazyGridScope.discoverItems]
 * "Discover" items section of our UI).
 *
 * Then we initialize and remember our [CoroutineScope] variable `val coroutineScope` and our
 * [SnackbarHostState] variable `val snackbarHostState` to new instances. Next we compose a
 * [HomeScreenBackground] whose `modifier` argument chains a [Modifier.windowInsetsPadding] to
 * our [Modifier] parameter [modifier] whose `insets` argument is
 * [WindowInsets.Companion.navigationBars] (insets which represent where system UI places
 * navigation bars. In its `content` Composable lambda argument we compose a [Scaffold] whose
 * `topBar` argument is a lambda that Composes a [Column] whose `content` composable lambda argument
 * is a lambda which Composes a [HomeAppBar] whose `isExpanded` argument is the
 * [WindowSizeClass.isCompact] property of our [WindowSizeClass] parameter [windowSizeClass], and
 * whose `modifier` argument is a [Modifier.fillMaxWidth]. Below that if our [Boolean] parameter
 * [isLoading] is `true` it renders a [LinearProgressIndicator] whose `modifier` argument is a
 * [Modifier.fillMaxWidth] with a [Modifier.padding] whose `horizontal` argument is 16.dp chained
 * to that. The `snackbarHost` argument of the [Scaffold] is a lambda that Composes a [SnackbarHost]
 * whose `hostState` argument is our [SnackbarHostState] variable `snackbarHostState`, and the
 * `containerColor` argument is [Color.Transparent]. In the `content` composable lambda argument of
 * the [Scaffold] we accept the [PaddingValues] passed the lambda as variable `contentPadding`. Then
 * we initialize our [String] variable `val snackBarText` with the string whose ID is
 * `R.string.episode_added_to_your_queue` ("Episode added to your queue"). We initialize our
 * [Boolean] variable `val showHomeCategoryTabs` to `true` if the [featuredPodcasts] list is not
 * empty and the [homeCategories] list is not empty. Then our root composable is a [HomeContent]
 * whose arguments are:
 *  - `showHomeCategoryTabs` is our [Boolean] variable `showHomeCategoryTabs`.
 *  - `featuredPodcasts` is our [PersistentList] of [PodcastInfo] parameter [featuredPodcasts].
 *  - `selectedHomeCategory` is our [HomeCategory] parameter [selectedHomeCategory].
 *  - `homeCategories` is our [List] of [HomeCategory] parameter [homeCategories].
 *  - `filterableCategoriesModel` is our [FilterableCategoriesModel] parameter
 *  [filterableCategoriesModel].
 *  - `podcastCategoryFilterResult` is our [PodcastCategoryFilterResult] parameter
 *  [podcastCategoryFilterResult].
 *  - `library` is our [LibraryInfo] parameter [library].
 *  - `modifier` is a [Modifier.padding] whose `paddingValues` argument is our [PaddingValues]
 *  variable `contentPadding`.
 *  - `onHomeAction` is a lambda which accepts the [HomeAction] argument passed the lambda in
 *  variable `action` and if `action` is an instance of [HomeAction.QueueEpisode] calls
 *  [CoroutineScope.launch] method of our [CoroutineScope] variable `coroutineScope` in whose
 *  `block` suspend lambda argument we call the [SnackbarHostState.showSnackbar] method of our
 *  [SnackbarHostState] variable `snackbarHostState` with the `message` argument set to our [String]
 *  variable `snackBarText`. Then it calls the [HomeViewModel.onHomeAction] method of our
 *  [onHomeAction] lambda parameter with the [HomeAction] variable `action` passed the lambda.
 *  - `navigateToPodcastDetails` is our lambda parameter [navigateToPodcastDetails].
 *  - `navigateToPlayer` is our lambda parameter [navigateToPlayer].
 *
 * @param windowSizeClass The current window size class, used to adapt the UI to different
 * screen sizes (one of compact, medium, or expanded).
 * @param isLoading A [Boolean] indicating whether the home screen data is currently loading.
 * @param featuredPodcasts A [PersistentList] of [PodcastInfo] of featured podcasts to display on
 * the home screen.
 * @param selectedHomeCategory The currently selected home category, either [HomeCategory.Library]
 * or [HomeCategory.Discover].
 * @param homeCategories A list of available home categories.
 * @param filterableCategoriesModel The model containing filterable category data.
 * @param podcastCategoryFilterResult The result of filtering podcasts by category.
 * @param library Information about the user's podcast library.
 * @param onHomeAction Callback for actions triggered on the home screen (e.g., category selection,
 * queueing episodes).
 * @param navigateToPodcastDetails Callback to navigate to the details screen of a specific podcast.
 * @param navigateToPlayer Callback to navigate to the episode player screen.
 * @param modifier [Modifier] for styling and layout customization. Our caller [HomeScreenReady]
 * passes us a [Modifier.fillMaxSize]
 *
 * @see WindowWidthSizeClass
 * @see WindowHeightSizeClass
 */
@Composable
private fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    featuredPodcasts: PersistentList<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    // Effect that changes the home category selection when there are no subscribed podcasts
    LaunchedEffect(key1 = featuredPodcasts) {
        if (featuredPodcasts.isEmpty()) {
            onHomeAction(HomeAction.HomeCategorySelected(category = HomeCategory.Discover))
        }
    }

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    HomeScreenBackground(
        modifier = modifier.windowInsetsPadding(insets = WindowInsets.navigationBars)
    ) {
        Scaffold(
            topBar = {
                Column {
                    HomeAppBar(
                        isExpanded = windowSizeClass.isCompact,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color.Transparent
        ) { contentPadding: PaddingValues ->
            // Main Content
            val snackBarText: String = stringResource(id = R.string.episode_added_to_your_queue)
            val showHomeCategoryTabs: Boolean =
                featuredPodcasts.isNotEmpty() && homeCategories.isNotEmpty()
            HomeContent(
                showHomeCategoryTabs = showHomeCategoryTabs,
                featuredPodcasts = featuredPodcasts,
                selectedHomeCategory = selectedHomeCategory,
                homeCategories = homeCategories,
                filterableCategoriesModel = filterableCategoriesModel,
                podcastCategoryFilterResult = podcastCategoryFilterResult,
                library = library,
                modifier = Modifier.padding(paddingValues = contentPadding),
                onHomeAction = { action: HomeAction ->
                    if (action is HomeAction.QueueEpisode) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message = snackBarText)
                        }
                    }
                    onHomeAction(action)
                },
                navigateToPodcastDetails = navigateToPodcastDetails,
                navigateToPlayer = navigateToPlayer,
            )
        }
    }
}

/**
 * Displays the main content of the home screen, including featured podcasts,
 * category tabs, and a grid of content based on the selected category.
 *
 * First we initialize and remember our [PagerState] variable `val pagerState` to an instance whose
 * `pageCount` lambda argument is a lambda that returns the [PersistentList.size] of our
 * [PersistentList] of [PodcastInfo] parameter [featuredPodcasts]. Then we compose a
 * [LaunchedEffect] whose `key1` argument is our [PagerState] variable `pagerState`, and whose
 * `key2` argument is our [PersistentList] of [PodcastInfo] parameter [featuredPodcasts]. In the
 * `block` [CoroutineScope] suspend lambda argument of the [LaunchedEffect] we use [snapshotFlow]
 * to create a [Flow] that runs its `block` lambda argument whenever it is collected. For its
 * `block` lambda argument we pass a lambda which emits the [PagerState.currentPage] of our
 * [PagerState] variable `pagerState`. To the [Flow] of the [snapshotFlow] we chain a [Flow.collect]
 * and in the `collector` lambda argument we accept the [Int] passed the lambda in our variable
 * `pageNumber` then initialize our [PodcastInfo] variable `val podcast` to the [PodcastInfo] at
 * index `pageNumber` in [PersistentList] of [PodcastInfo] parameter [featuredPodcasts], then
 * we call our lambda parameter [onHomeAction] with an [HomeAction.LibraryPodcastSelected] whose
 * `podcast` argument is our `podcast` variable.
 *
 * Finally our root composable is a [HomeContentGrid] whose arguments are:
 *  - `pagerState` our [PagerState] variable `pagerState`
 *  - `showHomeCategoryTabs` our [Boolean] parameter [showHomeCategoryTabs]
 *  - `featuredPodcasts` our [PersistentList] of [PodcastInfo] parameter [featuredPodcasts]
 *  - `selectedHomeCategory` our [HomeCategory] parameter [selectedHomeCategory]
 *  - `homeCategories` our [List] of [HomeCategory] parameter [homeCategories]
 *  - `filterableCategoriesModel` our [FilterableCategoriesModel] parameter
 *  [filterableCategoriesModel]
 *  - `podcastCategoryFilterResult` our [PodcastCategoryFilterResult] parameter
 *  [podcastCategoryFilterResult]
 *  - `library` our [LibraryInfo] parameter [library]
 *  - `modifier` our [Modifier] parameter [modifier]
 *  - `onHomeAction` our lambda parameter [onHomeAction]
 *  - `navigateToPodcastDetails` our lambda parameter [navigateToPodcastDetails]
 *  - `navigateToPlayer` our lambda parameter [navigateToPlayer]
 *
 * @param showHomeCategoryTabs [Boolean] indicating whether to show the category tabs at the top.
 * @param featuredPodcasts A [PersistentList] of [PodcastInfo] of the featured podcasts to display
 * in a horizontal pager.
 * @param selectedHomeCategory The currently selected category in the home screen.
 * @param homeCategories The list of available home categories.
 * @param filterableCategoriesModel The model for filtering podcasts by categories.
 * @param podcastCategoryFilterResult The result of the podcast category filter operation.
 * @param library Information about the user's library, such as saved podcasts or episodes.
 * @param modifier [Modifier] for styling and layout customization. Our caller [HomeScreen] passes
 * us a [Modifier.padding] with the [PaddingValues] passed the `content` lambda of the [Scaffold] it
 * composes us in.
 * @param onHomeAction Callback function triggered when a user interacts with the home screen.
 * It provides information about the specific action taken.
 * @param navigateToPodcastDetails Callback function to navigate to the podcast details screen.
 * Takes the selected [PodcastInfo] as a parameter.
 * @param navigateToPlayer Callback function to navigate to the player screen.
 * Takes the selected [EpisodeInfo] as a parameter.
 */
@Composable
private fun HomeContent(
    showHomeCategoryTabs: Boolean,
    featuredPodcasts: PersistentList<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
) {
    val pagerState: PagerState = rememberPagerState { featuredPodcasts.size }
    LaunchedEffect(key1 = pagerState, key2 = featuredPodcasts) {
        snapshotFlow { pagerState.currentPage }
            .collect { pageNumber: Int ->
                val podcast: PodcastInfo? = featuredPodcasts.getOrNull(pageNumber)
                onHomeAction(HomeAction.LibraryPodcastSelected(podcast = podcast))
            }
    }

    HomeContentGrid(
        pagerState = pagerState,
        showHomeCategoryTabs = showHomeCategoryTabs,
        featuredPodcasts = featuredPodcasts,
        selectedHomeCategory = selectedHomeCategory,
        homeCategories = homeCategories,
        filterableCategoriesModel = filterableCategoriesModel,
        podcastCategoryFilterResult = podcastCategoryFilterResult,
        library = library,
        modifier = modifier,
        onHomeAction = onHomeAction,
        navigateToPodcastDetails = navigateToPodcastDetails,
        navigateToPlayer = navigateToPlayer,
    )
}

/**
 * Displays the main content grid for the home screen.
 *
 * This composable manages the layout and content display for the home screen,
 * including featured podcasts, category tabs, and category-specific content
 * (Library or Discover). It uses a [LazyVerticalGrid] to efficiently render
 * the potentially large amount of content.
 *
 * Our root composable is a [LazyVerticalGrid] whose `columns` argument is a [GridCells.Adaptive]
 * whose `minSize` argument is 362.dp. The `modifier` argument of [LazyVerticalGrid] chains a
 * [Modifier.fillMaxSize] to our [Modifier] parameter [modifier]. In the [LazyGridScope] `content`
 * composable lambda argument if our [PersistentList] of [PodcastInfo] parameter [featuredPodcasts]
 * is not empty we compose a [fullWidthItem] which Composes a [FollowedPodcastItem] whose `pagerState`
 * argument is our [PagerState] parameter [pagerState], `items` argument is our [PersistentList] of
 * [PodcastInfo] parameter [featuredPodcasts], `onPodcastUnfollowed` argument is a lambda which
 * calls our lambda parameter [onHomeAction] with a [HomeAction.PodcastUnfollowed] whose `podcast`
 * argument is the [PodcastInfo] passed the lambda, `navigateToPodcastDetails` argument is our
 * lambda parameter [navigateToPodcastDetails], and `modifier` argument is a [Modifier.fillMaxWidth].
 *
 * Then if our [Boolean] parameter [showHomeCategoryTabs] is `true` we compose a [fullWidthItem]
 * that holds a [Row] whose [RowScope] `content` composable lambda argument is a [HomeCategoryTabs]
 * whose `categories` argument is our [List] of [HomeCategory] parameter [homeCategories], whose
 * `selectedCategory` argument is our [HomeCategory] parameter [selectedHomeCategory], whose
 * `showHorizontalLine` argument is `false`, whose `onCategorySelected` argument is a lambda which
 * calls our lambda parameter [onHomeAction] with a [HomeAction.HomeCategorySelected] whose
 * `category` argument is the [HomeCategory] passed the lambda, and whose `modifier` argument is
 * a [Modifier.width] whose `width` argument is 240.dp.
 *
 * Then we use a when statement to determine which content to display based on the value of our
 * [HomeCategory] parameter [selectedHomeCategory]. If it is [HomeCategory.Library] we compose a
 * [libraryItems] whose `library` argument is our [LibraryInfo] parameter [library], whose
 * `navigateToPlayer` argument is our lambda parameter [navigateToPlayer], and whose
 * `onQueueEpisode` argument is a lambda which calls our lambda parameter [onHomeAction] with a
 * [HomeAction.QueueEpisode] whose `episode` argument is the [EpisodeInfo] passed the lambda. If
 * [selectedHomeCategory] is [HomeCategory.Discover] we compose a [discoverItems] whose
 * `filterableCategoriesModel` argument is our [FilterableCategoriesModel] parameter
 * [filterableCategoriesModel], whose `podcastCategoryFilterResult` argument is our
 * [PodcastCategoryFilterResult] parameter [podcastCategoryFilterResult], whose `navigateToPodcastDetails`
 * is our lambda parameter [navigateToPodcastDetails], whose `navigateToPlayer` argument is our
 * lambda parameter [navigateToPlayer], whose `onCategorySelected` argument is a lambda which calls
 * our lambda parameter [onHomeAction] with a [HomeAction.CategorySelected] whose `category` is the
 * [CategoryInfo] passed the lambda, whose `onTogglePodcastFollowed` argument is a lambda which
 * calls our lambda parameter [onHomeAction] with a [HomeAction.TogglePodcastFollowed] whose
 * `podcast` argument is the [PodcastInfo] passed the lambda, and whose `onQueueEpisode` argument
 * is a lambda which calls our lambda parameter [onHomeAction] with a [HomeAction.QueueEpisode]
 * with the [EpisodeInfo] passed the lambda.
 *
 * @param showHomeCategoryTabs [Boolean] indicating whether to show the home category tabs.
 * @param pagerState The [PagerState] used for the [HorizontalPager] that holds the featured podcasts.
 * @param featuredPodcasts A list of featured podcasts to display in the [FollowedPodcastItem].
 * @param selectedHomeCategory The currently selected home category (`Library` or `Discover`).
 * @param homeCategories The list of available home categories.
 * @param filterableCategoriesModel The model for managing filterable categories.
 * @param podcastCategoryFilterResult The result of filtering podcasts by category.
 * @param library Information about the user's library (e.g., downloaded episodes).
 * @param modifier [Modifier] for the root layout of the grid. Our caller [HomeContent] passes us
 * its own `modifier` parameter which traces back to its [HomeScreen] caller which passes a
 * [Modifier.padding] with the [PaddingValues] passed the `content` lambda of the [Scaffold] it
 * composes [HomeContent] into.
 * @param onHomeAction Callback for handling actions triggered from the home screen, such as
 * selecting a category, unfollowing a podcast, queueing an episode, etc.
 * @param navigateToPodcastDetails Callback to navigate to the podcast details screen.
 * @param navigateToPlayer Callback to navigate to the episode player screen.
 */
@Composable
private fun HomeContentGrid(
    showHomeCategoryTabs: Boolean,
    pagerState: PagerState,
    featuredPodcasts: PersistentList<PodcastInfo>,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 362.dp),
        modifier = modifier.fillMaxSize()
    ) {
        if (featuredPodcasts.isNotEmpty()) {
            fullWidthItem {
                FollowedPodcastItem(
                    pagerState = pagerState,
                    items = featuredPodcasts,
                    onPodcastUnfollowed = { onHomeAction(HomeAction.PodcastUnfollowed(it)) },
                    navigateToPodcastDetails = navigateToPodcastDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        if (showHomeCategoryTabs) {
            fullWidthItem {
                Row {
                    HomeCategoryTabs(
                        categories = homeCategories,
                        selectedCategory = selectedHomeCategory,
                        showHorizontalLine = false,
                        onCategorySelected = { onHomeAction(HomeAction.HomeCategorySelected(it)) },
                        modifier = Modifier.width(width = 240.dp)
                    )
                }
            }
        }

        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                libraryItems(
                    library = library,
                    navigateToPlayer = navigateToPlayer,
                    onQueueEpisode = { onHomeAction(HomeAction.QueueEpisode(it)) }
                )
            }

            HomeCategory.Discover -> {
                discoverItems(
                    filterableCategoriesModel = filterableCategoriesModel,
                    podcastCategoryFilterResult = podcastCategoryFilterResult,
                    navigateToPodcastDetails = navigateToPodcastDetails,
                    navigateToPlayer = navigateToPlayer,
                    onCategorySelected = { onHomeAction(HomeAction.CategorySelected(it)) },
                    onTogglePodcastFollowed = {
                        onHomeAction(HomeAction.TogglePodcastFollowed(it))
                    },
                    onQueueEpisode = { onHomeAction(HomeAction.QueueEpisode(it)) },
                )
            }
        }
    }
}

/**
 * Composable function that displays a list of followed podcasts within a Column layout.
 *
 * This function wraps the [FollowedPodcasts] composable, adding spacing and handling the
 * pager state, list of podcasts, and actions for unfollowing and navigating to details.
 *
 * Our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we first compose a
 * [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` argument is 16.dp.
 * Then we compose a [FollowedPodcasts] whose `pagerState` argument is our [PagerState] parameter
 * [pagerState]`, `items` argument is our [PersistentList] of [PodcastInfo] parameter [items], whose
 * `onPodcastUnfollowed` argument our lambda parameter [onPodcastUnfollowed], whose
 * `navigateToPodcastDetails` argument is our lambda parameter [navigateToPodcastDetails], and whose
 * `modifier` argument is a [Modifier.fillMaxWidth]. Below that we compose a [Spacer] whose
 * `modifier` argument is a [Modifier.height] whose `height` argument is 16.dp.
 *
 * @param pagerState The state object to be used to control the [HorizontalPager].
 * @param items The list of [PodcastInfo] representing the followed podcasts.
 * @param onPodcastUnfollowed Lambda function to be invoked when a podcast is unfollowed.
 * It takes the [PodcastInfo] of the unfollowed podcast as a parameter.
 * @param navigateToPodcastDetails Lambda function to be invoked when a podcast item is clicked
 * to navigate to its details. It takes the [PodcastInfo] of the selected podcast as a parameter.
 * @param modifier Modifier to be applied to the root Column. This traces back to [HomeScreen] which
 * passes a [Modifier.padding] with the [PaddingValues] passed the `content` lambda of the
 * [Scaffold] it composes [HomeContent] into.
 */
@Composable
private fun FollowedPodcastItem(
    pagerState: PagerState,
    items: PersistentList<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(height = 16.dp))

        FollowedPodcasts(
            pagerState = pagerState,
            items = items,
            onPodcastUnfollowed = onPodcastUnfollowed,
            navigateToPodcastDetails = navigateToPodcastDetails,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(height = 16.dp))
    }
}

/**
 * Displays a horizontal row of category tabs for the home screen.
 *
 * This composable displays a [TabRow] that allows the user to switch between different
 * categories within the home screen, such as "Library" and "Discover". It highlights the
 * currently selected category with an indicator and provides visual feedback when a tab is tapped.
 *
 * Note:
 * - If the [categories] list is empty, nothing will be displayed.
 * - The `selectedIndex` is calculated based on the [selectedCategory].
 * - The `indicator` argument for the [TabRow] uses a custom composable, [HomeCategoryTabIndicator].
 * - The text displayed in each tab is determined by the [HomeCategory] value and loaded from
 * resources.
 * - If [showHorizontalLine] is true, a [HorizontalDivider] will be shown below the tabs.
 *
 * @param categories A list of [HomeCategory] representing the available categories.
 * @param selectedCategory The currently selected [HomeCategory].
 * @param onCategorySelected A callback function that is invoked when a new category is selected.
 * It receives the newly selected [HomeCategory] as a parameter.
 * @param showHorizontalLine Whether to display a horizontal line divider below the tabs.
 * @param modifier Modifier to be applied to the [TabRow].
 */
@Suppress("SameParameterValue")
@Composable
private fun HomeCategoryTabs(
    categories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    showHorizontalLine: Boolean,
    modifier: Modifier = Modifier,
) {
    if (categories.isEmpty()) {
        return
    }

    val selectedIndex: Int = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            modifier = Modifier.tabIndicatorOffset(currentTabPosition = tabPositions[selectedIndex])
        )
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = indicator,
        modifier = modifier,
        divider = {
            if (showHorizontalLine) {
                HorizontalDivider()
            }
        }
    ) {
        categories.forEachIndexed { index: Int, category: HomeCategory ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = when (category) {
                            HomeCategory.Library -> stringResource(R.string.home_library)
                            HomeCategory.Discover -> stringResource(R.string.home_discover)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}

/**
 * A composable function that displays a horizontal tab indicator for the home category.
 *
 * This function creates a thin, rounded rectangle that serves as a visual cue
 * to indicate which tab in a category selection is currently selected.
 * It's placed below the row of category labels.
 *
 * @param modifier The modifier to apply to the indicator. This can be used to adjust
 * padding, size, or other visual properties of the indicator. Our caller [HomeCategoryTabs] passes
 * us a [TabRowDefaults.tabIndicatorOffset] whose `currentTabPosition` argument is the [TabPosition]
 * of the selected tab.
 * @param color The color of the indicator. Defaults to the [ColorScheme.onSurface] of our custom
 * [MaterialTheme.colorScheme].
 */
@Composable
private fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(height = 4.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(topStartPercent = 100, topEndPercent = 100)
            )
    )
}

/**
 * The size of the featured podcast image in density-independent pixels (dp). This constant defines
 * the width and height of the image used to represent a featured podcast in the UI. Using a fixed
 * size ensures consistency across different screen densities. A size of 160.dp typically corresponds
 * to a moderately large image, suitable for prominent display.
 */
private val FEATURED_PODCAST_IMAGE_SIZE_DP = 160.dp

/**
 * Displays a horizontal carousel of followed podcasts.
 *
 * This composable uses a [HorizontalPager] to display a list of [PodcastInfo] items. Each item in
 * the carousel represents a followed podcast and displays the podcast's image, title, and last
 * episode date. Users can unfollow a podcast or navigate to its details screen.
 *
 * @param pagerState The state object to be used to control or observe the list's state.
 * @param items The list of followed podcasts to display.
 * @param onPodcastUnfollowed Callback invoked when the user unfollows a podcast. It receives the
 * [PodcastInfo] of the unfollowed podcast.
 * @param navigateToPodcastDetails Callback invoked when the user clicks on a podcast item. It
 * receives the [PodcastInfo] of the selected podcast.
 * @param modifier [Modifier] to be applied to the carousel container. Our caller [FollowedPodcastItem]
 * passes us a [Modifier.fillMaxWidth].
 */
@Composable
private fun FollowedPodcasts(
    pagerState: PagerState,
    items: PersistentList<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: Using BoxWithConstraints is not quite performant since it requires 2 passes to compute
    // the content padding. This should be revisited once a carousel component is available.
    // Alternatively, version 1.7.0-alpha05 of Compose Foundation supports `snapPosition`
    // which solves this problem and avoids this calculation altogether. Once 1.7.0 is
    // stable, this implementation can be updated.
    BoxWithConstraints(
        modifier = modifier.background(Color.Transparent)
    ) {
        val horizontalPadding: Dp = (this.maxWidth - FEATURED_PODCAST_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_PODCAST_IMAGE_SIZE_DP)
        ) { page: Int ->
            val podcast: PodcastInfo = items[page]
            FollowedPodcastCarouselItem(
                podcastImageUrl = podcast.imageUrl,
                podcastTitle = podcast.title,
                onUnfollowedClick = { onPodcastUnfollowed(podcast) },
                lastEpisodeDateText = podcast.lastEpisodeDate?.let { lastUpdated(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        navigateToPodcastDetails(podcast)
                    }
            )
        }
    }
}

/**
 * Displays a single podcast item in a horizontal carousel of followed podcasts.
 *
 * This composable shows a podcast's image, title, and optionally the last episode's
 * release date. It also includes a button to unfollow the podcast.
 *
 * @param podcastTitle The title of the podcast. This is used as the content description for the image.
 * @param podcastImageUrl The URL of the podcast's image.
 * @param modifier Modifier to be applied to the root Column layout. Our caller [FollowedPodcasts]
 * passes us a [Modifier.fillMaxSize] with a [Modifier.clickable] chained to that which will
 * navigate to the podcast details screen for the podcast when clicked.
 * @param lastEpisodeDateText Optional text representing the last episode's release date.
 * If null, the date text will not be displayed. Example: "2 days ago", "Yesterday", "1 week ago".
 * @param onUnfollowedClick Callback to be invoked when the unfollow button is clicked.
 * Used to update the underlying data source.
 */
@Composable
private fun FollowedPodcastCarouselItem(
    podcastTitle: String,
    podcastImageUrl: String,
    modifier: Modifier = Modifier,
    lastEpisodeDateText: String? = null,
    onUnfollowedClick: () -> Unit,
) {
    Column(modifier) {
        Box(
            Modifier
                .size(size = FEATURED_PODCAST_IMAGE_SIZE_DP)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            PodcastImage(
                podcastImageUrl = podcastImageUrl,
                contentDescription = podcastTitle,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = MaterialTheme.shapes.medium),
            )

            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(alignment = Alignment.BottomEnd)
            )
        }

        if (lastEpisodeDateText != null) {
            Text(
                text = lastEpisodeDateText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * Formats the time elapsed since the last update as a user-friendly string.
 *
 * This function calculates the difference in days between the provided [updated]
 * time and the current time. Based on the duration, it returns a string
 * indicating when the last update occurred. The output strings are localized
 * using string resources.
 *
 * @param updated The [OffsetDateTime] representing the time of the last update.
 * @return A `String` describing the time elapsed since the last update.
 * - "Updated a while ago" (more than 28 days ago)
 * - "Updated X week(s) ago" (between 7 and 28 days ago)
 * - "Updated yesterday" (one day ago)
 * - "Updated X days ago" (between 2 and 6 days ago)
 * - "Updated today" (less than 1 day ago)
 */
@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)
        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }

        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}

/**
 * Preview of the [HomeAppBar] composable.
 */
@Preview
@Composable
private fun HomeAppBarPreview() {
    JetcasterTheme {
        HomeAppBar(
            isExpanded = false,
        )
    }
}

/**
 * Represents the compact window size class based on a fixed width and height.
 *
 * This property defines a [WindowSizeClass] instance that is specifically configured
 * to represent a compact window. In this context, "compact" typically refers to
 * smaller screen sizes, such as those found on most smartphones.
 *
 * The dimensions used to determine this compact window are:
 * - Width: 360 density-independent pixels (dp)
 * - Height: 780 density-independent pixels (dp)
 *
 * This is a common baseline for a compact window size and can be used for UI layout
 * decisions that need to adapt based on screen size availability. For example, if your
 * window's dimensions are less than or equal to these, then you should consider
 * using a compact UI layout.
 *
 * Note: This is a pre-calculated, fixed size class. For dynamic window size class
 * detection based on the current window's actual dimensions, the system APIs should
 * be used to observe window changes. This property is useful in scenarios where
 * a fixed compact size is needed, for example, for preview layouts or when testing.
 */
private val CompactWindowSizeClass: WindowSizeClass = WindowSizeClass.compute(
    dpWidth = 360f,
    dpHeight = 780f
)

/**
 * Previews of the [HomeScreen] composable.
 */
@DevicePreviews
@Composable
private fun PreviewHome() {
    JetcasterTheme {
        HomeScreen(
            windowSizeClass = CompactWindowSizeClass,
            isLoading = true,
            featuredPodcasts = PreviewPodcasts.toPersistentList(),
            homeCategories = HomeCategory.entries,
            selectedHomeCategory = HomeCategory.Discover,
            filterableCategoriesModel = FilterableCategoriesModel(
                categories = PreviewCategories,
                selectedCategory = PreviewCategories.firstOrNull()
            ),
            podcastCategoryFilterResult = PodcastCategoryFilterResult(
                topPodcasts = PreviewPodcasts,
                episodes = PreviewPodcastEpisodes
            ),
            library = LibraryInfo(),
            onHomeAction = {},
            navigateToPodcastDetails = {},
            navigateToPlayer = {},
        )
    }
}

/**
 * Preview of the [FollowedPodcastCarouselItem] composable.
 */
@Composable
@Preview
private fun PreviewPodcastCard() {
    JetcasterTheme {
        FollowedPodcastCarouselItem(
            modifier = Modifier.size(128.dp),
            podcastTitle = "",
            podcastImageUrl = "",
            onUnfollowedClick = {}
        )
    }
}
