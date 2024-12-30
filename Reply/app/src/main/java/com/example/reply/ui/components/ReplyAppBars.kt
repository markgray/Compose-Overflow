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

package com.example.reply.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Account
import com.example.reply.data.Email
import com.example.reply.ui.ReplyEmailDetail
import com.example.reply.ui.ReplyEmailList
import com.example.reply.ui.ReplyHomeUIState
import com.example.reply.ui.ReplyHomeViewModel
import kotlinx.coroutines.CoroutineScope

/**
 * This Composable is displayed at the top of the [ReplyEmailList] Composable. We start by initializing
 * and remembering our [MutableState] wrapped [String] variable `var query` to an empty string. We
 * initialize and remember ouf [MutableState] wrapped [Boolean] variable `var expanded` to `false`.
 * We initialize and remember our [SnapshotStateList] wrapped [MutableList] of [Email] variable
 * `var searchResults` to an empty [MutableList]. We initialize our lambda taking [Boolean] variable
 * `val onExpandedChange` to a lambda which sets `expanded` to its [Boolean] parameter.
 *
 * First we call [LaunchedEffect] with its `key1` argument set to `query`. In its [CoroutineScope]
 * suspend `block` lambda argument we call the [SnapshotStateList.clear] method of `searchResults`
 * to remove all its contents, then if [MutableState] wrapped [String] variable `query` is not empty
 * we call the [SnapshotStateList.addAll] method of `searchResults` to add all [Email]s from `emails`
 * whose [Email.subject] starts with the [MutableState] wrapped [String] variable `query` or whose
 * [Account.fullName] of the [Email.sender] starts with the [MutableState] wrapped [String] variable
 * `query` (this [LaunchedEffect] will be relaunched every time `query` changes).
 *
 * Our root Composable is a [DockedSearchBar] whose `inputField` argument is a lambda that composes
 * a [SearchBarDefaults.InputField] whose `query` argument is our [MutableState] wrapped [String]
 * variable `query`, whose `onQueryChange` argument is a lambda that sets `query` to the [String]
 * passed the lambda, whose `onSearch` argument is a lambda that sets our [MutableState] wrapped
 * variable `expanded` to `false`, whose `onExpandedChange` argument is our lambda variable
 * `onExpandedChange`, whose `modifier` argument is a [Modifier.fillMaxWidth] to have us use all our
 * incoming horizontal constraint, whose `placeholder` argument is a [Text] displaying the [String]
 * with resource ID `R.string.search_emails` ("Search emails"), whose `leadingIcon` argument is an
 * [Icon] displaying the [ImageVector] `imageVector` argument drawn by [Icons.AutoMirrored.Filled.ArrowBack]
 * if [MutableState] wrapped [Boolean] variable `expanded` is `true`, with a [Modifier.padding]
 * `modifier` argument that adds `16.dp` to the `start`, chained to a [Modifier.clickable] whose
 * `onClick` lambda argument is a lambda that sets `expanded` to `false`, and `query` to an empty
 * [String]. If [MutableState] wrapped [Boolean] variable `expanded` is `false` `leadingIcon` is
 * an [Icon] displaying the [ImageVector] `imageVector` argument drawn by [Icons.Filled.Search] with
 * a [Modifier.padding] `modifier` argument that adds `16.dp` to the `start`. The `trailingIcon`
 * argument is a lambda composing a [ReplyProfileImage] whose `drawableResource` argument is the
 * jpeg with resource ID `R.drawable.avatar_6`, whose `description` argument is the [String] with
 * resource ID `R.string.profile` ("Profile"), and whose [Modifier] `modifier` argument is a
 * [Modifier.padding] that adds `12.dp` to all sides, with a [Modifier.size] chained to that which
 * sets its `size` to `32.dp`.
 *
 * The `expanded` argument of the [DockedSearchBar] is our [MutableState] wrapped [Boolean]
 * variable `expanded`. The `onExpandedChange` argument is our lambda variable `onExpandedChange`.
 * The [Modifier] `modifier` argument of the [DockedSearchBar] is our [Modifier] parameter [modifier].
 *
 * The `content` [ColumnScope] Composable lambda argument is a lambda that composes three different
 * Composable elements depending on whether:
 *  - [SnapshotStateList] of [Email] variable `searchResults` is _not_ empty: we compose a [LazyColumn]
 *  whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth], whose `contentPadding` argument
 *  is a [PaddingValues] of `16.dp` on all sides, and whose `verticalArrangement` argument is a
 *  [Arrangement.spacedBy] whose `space` is `4.dp`. In the `content` [LazyListScope] Composable lambda
 *  argument of the [LazyColumn] we call the [LazyListScope.items] method with its `items` argument
 *  our [SnapshotStateList] of [Email] variable `searchResults`, and its `key` argument the [Email.id]
 *  of the current [Email]. In the `itemContent` [LazyItemScope] composable lambda argument of the
 *  [LazyListScope.items] we accept the [Email] passed the lambda in variable `email` and then
 *  we compose a [ListItem] whose `headlineContent` Composable lambda argument is a lambda that
 *  composes a [Text] whose `text` argument is the [Email.subject] of the [Email] passed the lambda.
 *  The `supportingContent` Composable lambda argument is a lambda that composes a [Text] whose
 *  `text` is the [Account.fullName] of the [Email.sender] of `email`. The `leadingContent` lambda
 *  argument is a lambda that composes a [ReplyProfileImage] whose `drawableResource` argument is
 *  the [Account.avatar] of the [Email.sender] of `email`, whose `description` argument is the
 *  [String] with resource ID `R.string.profile` ("Profile"), and whose [Modifier] `modifier`
 *  argument is a [Modifier.size] of `size` `32.dp`. The [Modifier] `modifier` argument of the
 *  [ListItem] is a [Modifier.clickable] whose `onClick` lambda argument is a lambda that calls our
 *  lambda parameter [onSearchItemSelected] with the [Email] `email`, sets `query` to an empty
 *  [String], and sets `expanded` to `false`.
 *  - [MutableState] wrapped [String] variable `query` is _not_ empty: we compose a [Text] whose
 *  `text` argument is the [String] with resource ID `R.string.no_item_found` ("No item found"),
 *  and whose [Modifier] `modifier` argument is a [Modifier.padding] that adds `16.dp` to all
 *  sides.
 *  - [MutableState] wrapped [String] variable `query` is empty: we compose a [Text] whose `text`
 *  argument is the [String] with resource ID `R.string.no_search_history` ("No search history"),
 *  and whose [Modifier] `modifier` argument is a [Modifier.padding] that adds `16.dp` to all sides.
 *
 * @param emails [List] of all emails.
 * @param onSearchItemSelected callback to be invoked when a [Email] is selected. Our caller
 * [ReplyEmailList] passes us a lambda that traces back to a call to [ReplyHomeViewModel.setOpenedEmail]
 * with the [Email.id] of the [Email] that we pass to the lambda.
 * @param modifier a [Modifier] instance which our caller can use to modify our behavior and/or
 * appearance. Our caller [ReplyEmailList] calls us with a [Modifier.fillMaxWidth] to have us use
 * all of our incoming horizontal constraint, to which it chains a [Modifier.padding] that adds
 * `16.dp` to each horizontal side, and `16.dp` to each vertical side.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyDockedSearchBar(
    emails: List<Email>,
    onSearchItemSelected: (Email) -> Unit,
    modifier: Modifier = Modifier
) {
    var query: String by remember { mutableStateOf(value = "") }
    var expanded: Boolean by remember { mutableStateOf(value = false) }
    val searchResults: SnapshotStateList<Email> = remember { mutableStateListOf() }
    val onExpandedChange: (Boolean) -> Unit = {
        expanded = it
    }

    LaunchedEffect(key1 = query) {
        searchResults.clear()
        if (query.isNotEmpty()) {
            searchResults.addAll(
                emails.filter {
                    it.subject.startsWith(
                        prefix = query,
                        ignoreCase = true
                    ) || it.sender.fullName.startsWith(
                        prefix = query,
                        ignoreCase = true
                    )
                }
            )
        }
    }

    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    query = it
                },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.search_emails)) },
                leadingIcon = {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable {
                                    expanded = false
                                    query = ""
                                },
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search),
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                },
                trailingIcon = {
                    ReplyProfileImage(
                        drawableResource = R.drawable.avatar_6,
                        description = stringResource(id = R.string.profile),
                        modifier = Modifier
                            .padding(all = 12.dp)
                            .size(size = 32.dp)
                    )
                },
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
        content = {
            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    items(items = searchResults, key = { it.id }) { email: Email ->
                        ListItem(
                            headlineContent = { Text(text = email.subject) },
                            supportingContent = { Text(text = email.sender.fullName) },
                            leadingContent = {
                                ReplyProfileImage(
                                    drawableResource = email.sender.avatar,
                                    description = stringResource(id = R.string.profile),
                                    modifier = Modifier
                                        .size(size = 32.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                onSearchItemSelected.invoke(email)
                                query = ""
                                expanded = false
                            }
                        )
                    }
                }
            } else if (query.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_item_found),
                    modifier = Modifier.padding(all = 16.dp)
                )
            } else
                Text(
                    text = stringResource(id = R.string.no_search_history),
                    modifier = Modifier.padding(all = 16.dp)
                )
        }
    )
}

/**
 * This is used by [ReplyEmailDetail] as the top [LazyListScope.item] in its [LazyColumn], where it
 * functions as a [TopAppBar] that can be scrolled off the screen.
 *
 * @param email [Email] that is being displayed in the [ReplyEmailDetail]. Our caller calls us
 * with the [ReplyHomeUIState.openedEmail] that is passed to it in its `email` parameter.
 * @param isFullScreen if `true` the [ReplyEmailDetail] is being displayed in full screen mode
 * (the device is single pane), if `false` the [ReplyEmailDetail] is being displayed on a two pane
 * device.
 * @param modifier a [Modifier] instance which our caller can use to modify our behavior and/or
 * behavior. Our caller [ReplyEmailDetail] does not pass one so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 * @param onBackPressed lambda to be called when the back button is pressed. Our caller
 * [ReplyEmailDetail] passes us its `onBackPressed` lambda parameter which traces back to a
 * call to [ReplyHomeViewModel.closeDetailScreen]. (Note that we only call it if the device is in
 * single pane mode, ie. if [MutableState] wrapped [Boolean] variable `isFullScreen` is `true`.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailDetailAppBar(
    email: Email,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface
        ),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isFullScreen) {
                    Alignment.CenterHorizontally
                } else {
                    Alignment.Start
                }
            ) {
                Text(
                    text = email.subject,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "${email.threads.size} ${stringResource(id = R.string.messages)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        navigationIcon = {
            if (isFullScreen) {
                FilledIconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.padding(all = 8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        modifier = Modifier.size(size = 14.dp)
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options_button),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
