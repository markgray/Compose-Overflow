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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewCategories
import com.example.jetcaster.core.domain.testing.PreviewPodcastEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
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

/*
 * Copied from `getExcludedVerticalBounds()` in [PaneScaffoldDirective] since it is private.
 */

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
 * - [HomeScreenUiState] (Assumed to be defined elsewhere in the project)
 * - [EpisodeInfo] (Assumed to be defined)
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
 * Composable function that renders the main home screen and handles navigation to the podcas
 * t details screen. TODO: More detail
 *
 * This function utilizes [SupportingPaneScaffold] to manage a two-pane layout, allowing for the
 * simultaneous display of the home screen and a podcast details screen on larger screens. It also
 * handles back navigation and fetching the correct view models.
 *
 * @param uiState The [HomeScreenUiState] representing the current state of the home screen.
 * @param windowSizeClass The [WindowSizeClass] of the current window.
 * @param navigateToPlayer A lambda function to navigate to the player screen, accepting an
 * [EpisodeInfo].
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
                        navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.uri)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeAppBar(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    var queryText by remember {
        mutableStateOf("")
    }
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
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
                        Text(stringResource(id = R.string.search_for_a_podcast))
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
                            contentDescription = stringResource(R.string.cd_account)
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
                .radialGradientScrim(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        )
        content()
    }
}

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
            onHomeAction(HomeAction.HomeCategorySelected(HomeCategory.Discover))
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    HomeScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
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
        ) { contentPadding ->
            // Main Content
            val snackBarText = stringResource(id = R.string.episode_added_to_your_queue)
            val showHomeCategoryTabs = featuredPodcasts.isNotEmpty() && homeCategories.isNotEmpty()
            HomeContent(
                showHomeCategoryTabs = showHomeCategoryTabs,
                featuredPodcasts = featuredPodcasts,
                selectedHomeCategory = selectedHomeCategory,
                homeCategories = homeCategories,
                filterableCategoriesModel = filterableCategoriesModel,
                podcastCategoryFilterResult = podcastCategoryFilterResult,
                library = library,
                modifier = Modifier.padding(contentPadding),
                onHomeAction = { action ->
                    if (action is HomeAction.QueueEpisode) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(snackBarText)
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
    val pagerState = rememberPagerState { featuredPodcasts.size }
    LaunchedEffect(pagerState, featuredPodcasts) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                val podcast = featuredPodcasts.getOrNull(it)
                onHomeAction(HomeAction.LibraryPodcastSelected(podcast))
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
        columns = GridCells.Adaptive(362.dp),
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
                        modifier = Modifier.width(240.dp)
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

@Composable
private fun FollowedPodcastItem(
    pagerState: PagerState,
    items: PersistentList<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(16.dp))

        FollowedPodcasts(
            pagerState = pagerState,
            items = items,
            onPodcastUnfollowed = onPodcastUnfollowed,
            navigateToPodcastDetails = navigateToPodcastDetails,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
    }
}

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

    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
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
        categories.forEachIndexed { index, category ->
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

@Composable
private fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

private val FEATURED_PODCAST_IMAGE_SIZE_DP = 160.dp

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
        val horizontalPadding = (this.maxWidth - FEATURED_PODCAST_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_PODCAST_IMAGE_SIZE_DP)
        ) { page ->
            val podcast = items[page]
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
                .size(FEATURED_PODCAST_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            PodcastImage(
                podcastImageUrl = podcastImageUrl,
                contentDescription = podcastTitle,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
            )

            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(Alignment.BottomEnd)
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
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

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

@Preview
@Composable
private fun HomeAppBarPreview() {
    JetcasterTheme {
        HomeAppBar(
            isExpanded = false,
        )
    }
}

private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

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
