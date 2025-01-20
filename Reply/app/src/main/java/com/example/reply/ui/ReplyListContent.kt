/*
 * Copyright 2022 The Android Open Source Project
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

@file:Suppress("Destructure")

package com.example.reply.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.components.EmailDetailAppBar
import com.example.reply.ui.components.ReplyDockedSearchBar
import com.example.reply.ui.components.ReplyEmailListItem
import com.example.reply.ui.components.ReplyEmailThreadItem
import com.example.reply.ui.navigation.Route
import com.example.reply.ui.utils.ReplyContentType
import com.example.reply.ui.utils.ReplyNavigationType
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import kotlinx.coroutines.CoroutineScope

/**
 * This is the screen displayed for the [Route.Inbox] route, which is the "start destination" of the
 * app. Our first step is to launch a [LaunchedEffect] whose `key1` is our [ReplyContentType] parameter
 * [contentType] so that whenever it changes the [LaunchedEffect] will be run again. This is done so
 * that the [CoroutineScope] `block` lambda can call our [closeDetailScreen] lambda parameter to
 * close the detail screen if [contentType] has changed from [ReplyContentType.DUAL_PANE] to
 * [ReplyContentType.SINGLE_PANE]. Next we initialize and remember our [LazyListState] variable
 * `val emailLazyListState` to the instance returned by [rememberLazyListState]. Then we branch on
 * the value of our [ReplyContentType] parameter [contentType]:
 *  - [ReplyContentType.DUAL_PANE] we compose a [TwoPane] whose `first` argument is a lambda that
 *  composes a [ReplyEmailList] whose `emails` argument is the [ReplyHomeUIState.emails] property of
 *  our [ReplyHomeUIState] parameter [replyHomeUIState], whose `openedEmail` argument is the
 *  [ReplyHomeUIState.openedEmail] property of [replyHomeUIState], whose `selectedEmailIds` argument
 *  is the  [Set] of [Long] in the [ReplyHomeUIState.selectedEmails] property of [replyHomeUIState],
 *  whose `toggleEmailSelection` argument is our lambda parameter [toggleSelectedEmail], whose
 *  `emailLazyListState` argument is our [LazyListState] variable `emailLazyListState`, and whose
 *  `navigateToDetail` argument is our lambda parameter [navigateToDetail].
 *
 *  The `second` argument of our [TwoPane] is a lambda that composes a [ReplyEmailDetail] whose
 *  `email` argument is the [ReplyHomeUIState.openedEmail] property of [replyHomeUIState] if that is
 *  not `null` or the first [Email] in the [List] of [Email] in the [ReplyHomeUIState.emails] property
 *  of [replyHomeUIState] if it is `null`. The `isFullScreen` argument of the [ReplyEmailDetail] is
 *  `false`.
 *
 *  The `strategy` argument of our [TwoPane] is a [HorizontalTwoPaneStrategy] whose `splitFraction`
 *  argument is `0.5f`, and whose `gapWidth` argument is `16.dp`.
 *
 *  - [ReplyContentType.SINGLE_PANE] our root composable is a [Box] whose `modifier` argument chains
 *  to our [Modifier] parameter [modifier] a [Modifier.fillMaxSize] to have it take up all of its
 *  incoming size constraint. In the [BoxScope] `content` composable lambda argument of the [Box] we
 *  compose a [ReplySinglePaneContent] whose `replyHomeUIState` argument is our [ReplyHomeUIState]
 *  parameter [replyHomeUIState], whose `toggleEmailSelection` argument is our lambda parameter
 *  [toggleSelectedEmail], whose `emailLazyListState` argument is our [LazyListState] variable
 *  `emailLazyListState`, whose [Modifier] `modifier` argument is a [Modifier.fillMaxSize], whose
 *  `closeDetailScreen` argument is our lambda parameter [closeDetailScreen], and whose
 *  `navigateToDetail` argument is our lambda parameter [navigateToDetail]. Then if our
 *  [ReplyNavigationType] parameter [navigationType] is [ReplyNavigationType.BOTTOM_NAVIGATION] we
 *  compose an [ExtendedFloatingActionButton], whose `text` argument is a lambda that composes a
 *  [Text] whose `text` argument is the string resource with id `R.string.compose` ("Compose"), whose
 *  `icon` argument is a lambda  that composes an [Icon] whose [ImageVector] `imageVector` argument is
 *  the [ImageVector] drawn by [Icons.Filled.Edit] (a stylized pencil icon) and whose
 *  `contentDescription` argument is the [String] with resource id `R.string.compose` ("Compose"),
 *  the `onClick` argument of the [ExtendedFloatingActionButton] is an empty lambda, the [Modifier]
 *  `modifier` argument is a [BoxScope.align] whose `alignment` argument is [Alignment.BottomEnd] to
 *  align it at the bottom end of the [Box], with a [Modifier.padding] chained to that which adds
 *  `16.dp` padding to all sides. The [Color] `containerColor` argument is the
 *  [ColorScheme.tertiaryContainer] of our custom [MaterialTheme.colorScheme], the [Color]
 *  `contentColor` argument is the [ColorScheme.onTertiaryContainer] of our custom
 *  [MaterialTheme.colorScheme], and the `expanded` argument of the [ExtendedFloatingActionButton] is
 *  `true` if the [LazyListState.lastScrolledBackward] property of our [LazyListState] variable
 *  `emailLazyListState` is `true` or its [LazyListState.canScrollBackward] property is `false`.
 *
 * @param contentType the current [ReplyContentType] of the app, one of [ReplyContentType.SINGLE_PANE]
 * or [ReplyContentType.DUAL_PANE].
 * @param replyHomeUIState the [State] wrapped [ReplyHomeUIState] that holds the current UI state
 * of the app.
 * @param navigationType the current [ReplyNavigationType] of the app, one of
 * [ReplyNavigationType.BOTTOM_NAVIGATION] or [ReplyNavigationType.NAVIGATION_RAIL] or
 * [ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER].
 * @param displayFeatures the [List] of [DisplayFeature] that the device supports.
 * @param closeDetailScreen lambda function that closes the detail screen.
 * @param navigateToDetail lambda function that navigates to the detail screen to display the [Email]
 * whose [Email.id] matches the [Long] parameter of the lambda function, and with the [ReplyContentType]
 * parameter causing the [ReplyHomeUIState.isDetailOnlyOpen] to be set to `true` only if the parameter
 * is [ReplyContentType.SINGLE_PANE].
 * @param toggleSelectedEmail lambda function that toggles the selected state of the [Email] whose
 * [Email.id] matches the [Long] parameter of the lambda function.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 */
@Composable
fun ReplyInboxScreen(
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    navigationType: ReplyNavigationType,
    displayFeatures: List<DisplayFeature>,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
    toggleSelectedEmail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * When moving from LIST_AND_DETAIL page to LIST close the detail screen so the user will see
     * the LIST screen.
     */
    LaunchedEffect(key1 = contentType) {
        if (contentType == ReplyContentType.SINGLE_PANE && !replyHomeUIState.isDetailOnlyOpen) {
            closeDetailScreen()
        }
    }

    val emailLazyListState: LazyListState = rememberLazyListState()

    // TODO: Show top app bar over full width of app when in multi-select mode

    if (contentType == ReplyContentType.DUAL_PANE) {
        TwoPane(
            first = {
                ReplyEmailList(
                    emails = replyHomeUIState.emails,
                    openedEmail = replyHomeUIState.openedEmail,
                    selectedEmailIds = replyHomeUIState.selectedEmails,
                    toggleEmailSelection = toggleSelectedEmail,
                    emailLazyListState = emailLazyListState,
                    navigateToDetail = navigateToDetail
                )
            },
            second = {
                ReplyEmailDetail(
                    email = replyHomeUIState.openedEmail ?: replyHomeUIState.emails.first(),
                    isFullScreen = false
                )
            },
            strategy = HorizontalTwoPaneStrategy(splitFraction = 0.5f, gapWidth = 16.dp),
            displayFeatures = displayFeatures
        )
    } else {  // `ReplyContentType.SINGLE_PANE`
        Box(modifier = modifier.fillMaxSize()) {
            ReplySinglePaneContent(
                replyHomeUIState = replyHomeUIState,
                toggleEmailSelection = toggleSelectedEmail,
                emailLazyListState = emailLazyListState,
                modifier = Modifier.fillMaxSize(),
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail
            )
            // When we have bottom navigation we show FAB at the bottom end.
            if (navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.compose)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.compose)
                        )
                    },
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .padding(all = 16.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    expanded = emailLazyListState.lastScrolledBackward ||
                        !emailLazyListState.canScrollBackward
                )
            }
        }
    }
}

/**
 * This composable is used by [ReplyInboxScreen] if the [ReplyContentType] used for the device is
 * [ReplyContentType.SINGLE_PANE]. If the [ReplyHomeUIState.openedEmail] property of our
 * [ReplyHomeUIState] parameter [replyHomeUIState] is not `null` and its
 * [ReplyHomeUIState.isDetailOnlyOpen] is `true` we compose a [BackHandler] whose `onBack` argument
 * is a lambda that calls our [closeDetailScreen] lambda parameter. Then we compose a
 * [ReplyEmailDetail] whose `email` argument is the [ReplyHomeUIState.openedEmail] property of our
 * [ReplyHomeUIState] parameter [replyHomeUIState] and whose `onBackPressed` argument is a lambda
 * that calls our [closeDetailScreen] lambda parameter.
 *
 * Otherwise we compose a [ReplyEmailList] whose [List] of [Email] `emails` argument is the
 * [ReplyHomeUIState.emails] of our [ReplyHomeUIState] parameter [replyHomeUIState], whose `openedEmail`
 * argument is the [ReplyHomeUIState.openedEmail] property of our [ReplyHomeUIState] parameter
 * [replyHomeUIState], whose [Set] of [Long] `selectedEmailIds` argument is the
 * [ReplyHomeUIState.selectedEmails] property of our [ReplyHomeUIState] parameter [replyHomeUIState],
 * whose `toggleEmailSelection` argument is our lambda parameter [toggleEmailSelection], whose
 * `emailLazyListState` argument is our [LazyListState] parameter [emailLazyListState], whose
 * `modifier` argument is our [Modifier] argument [modifier], and whose `navigateToDetail` argument
 * is our lambda parameter [navigateToDetail].
 *
 * @param replyHomeUIState the [State] wrapped [ReplyHomeUIState] that holds the current UI state.
 * @param toggleEmailSelection lambda function that toggles the selected state of the [Email] whose
 * [Email.id] matches the [Long] parameter of the lambda function.
 * @param emailLazyListState the [LazyListState] that [ReplyEmailList] should use to control the
 * scrolling of its [LazyColumn].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us any so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param closeDetailScreen lambda function that closes the detail screen.
 * @param navigateToDetail lambda function that navigates to the detail screen to display the [Email]
 * whose [Email.id] matches the [Long] parameter of the lambda function, and with the [ReplyContentType]
 * parameter causing the [ReplyHomeUIState.isDetailOnlyOpen] to be set to `true` only if the parameter
 * is [ReplyContentType.SINGLE_PANE].
 */
@Composable
fun ReplySinglePaneContent(
    replyHomeUIState: ReplyHomeUIState,
    toggleEmailSelection: (Long) -> Unit,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit
) {
    if (replyHomeUIState.openedEmail != null && replyHomeUIState.isDetailOnlyOpen) {
        BackHandler {
            closeDetailScreen()
        }
        ReplyEmailDetail(email = replyHomeUIState.openedEmail) {
            closeDetailScreen()
        }
    } else {
        ReplyEmailList(
            emails = replyHomeUIState.emails,
            openedEmail = replyHomeUIState.openedEmail,
            selectedEmailIds = replyHomeUIState.selectedEmails,
            toggleEmailSelection = toggleEmailSelection,
            emailLazyListState = emailLazyListState,
            modifier = modifier,
            navigateToDetail = navigateToDetail
        )
    }
}

/**
 * This Composable displays the [List] of [Email]s in its [List] of [Email]s parameter [emails]. Our
 * root Composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter [modifier]
 * a [Modifier.windowInsetsPadding] whose `insets` argument is [WindowInsets.Companion.statusBars]
 * to
 *
 * @param emails the [List] of [Email]s to display.
 * @param openedEmail the [Email] that is currently being displayed in the detail screen.
 * @param selectedEmailIds the [Set] of [Long]s of the [Email.id] of the [Email]s that are
 * currently selected.
 * @param toggleEmailSelection lambda function that toggles the selected state of the [Email] whose
 * [Email.id] matches the [Long] parameter of the lambda function.
 * @param emailLazyListState the [LazyListState] that we should use to control the scrolling of our
 * [LazyColumn].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [ReplyInboxScreen] does not pass us any when the [ReplyContentType] is
 * [ReplyContentType.DUAL_PANE] so the empty, default, or starter [Modifier] that contains no
 * elements is used for it, but [ReplySinglePaneContent] passes us a [Modifier.fillMaxSize] when
 * [ReplyInboxScreen] calls it for [ReplyContentType.SINGLE_PANE].
 * @param navigateToDetail lambda function that navigates to the detail screen to display the [Email]
 * whose [Email.id] matches the [Long] parameter of the lambda function, and with the [ReplyContentType]
 * parameter causing the [ReplyHomeUIState.isDetailOnlyOpen] to be set to `true` only if the parameter
 * is [ReplyContentType.SINGLE_PANE].
 */
@Composable
fun ReplyEmailList(
    emails: List<Email>,
    openedEmail: Email?,
    selectedEmailIds: Set<Long>,
    toggleEmailSelection: (Long) -> Unit,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    navigateToDetail: (Long, ReplyContentType) -> Unit
) {
    Box(modifier = modifier.windowInsetsPadding(insets = WindowInsets.statusBars)) {
        ReplyDockedSearchBar(
            emails = emails,
            onSearchItemSelected = { searchedEmail: Email ->
                navigateToDetail(searchedEmail.id, ReplyContentType.SINGLE_PANE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            state = emailLazyListState
        ) {
            items(items = emails, key = { it.id }) { email: Email ->
                ReplyEmailListItem(
                    email = email,
                    navigateToDetail = { emailId: Long ->
                        navigateToDetail(emailId, ReplyContentType.SINGLE_PANE)
                    },
                    toggleSelection = toggleEmailSelection,
                    isOpened = openedEmail?.id == email.id,
                    isSelected = selectedEmailIds.contains(element = email.id)
                )
            }
            // Add extra spacing at the bottom if
            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(insets = WindowInsets.systemBars))
            }
        }
    }
}

/**
 *
 */
@Composable
fun ReplyEmailDetail(
    email: Email,
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = true,
    onBackPressed: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
    ) {
        item {
            EmailDetailAppBar(email = email, isFullScreen = isFullScreen) {
                onBackPressed()
            }
        }
        items(items = email.threads, key = { it.id }) { email ->
            ReplyEmailThreadItem(email = email)
        }
        item {
            Spacer(modifier = Modifier.windowInsetsBottomHeight(insets = WindowInsets.systemBars))
        }
    }
}
