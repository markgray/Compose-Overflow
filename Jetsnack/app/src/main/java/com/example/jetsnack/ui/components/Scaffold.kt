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

package com.example.jetsnack.ui.components

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import com.example.jetsnack.model.Message
import com.example.jetsnack.model.SnackbarManager
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.JetsnackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Wraps Material [androidx.compose.material3.Scaffold] and sets [JetsnackTheme] colors. Our root
 * composable is a [Scaffold] whose `modifier` argument is our [Modifier] parameter [modifier],
 * whose `topBar` argument is our Composable lambda parameter [topBar], whose `bottomBar` argument
 * is our Composable lambda parameter [bottomBar], whose `snackbarHost` argument is a lambda that
 * calls our lambda parameter [snackbarHost] with our [SnackbarHostState] parameter [snackBarHostState],
 * whose `floatingActionButton` argument is our lambda parameter [floatingActionButton], whose
 * `floatingActionButtonPosition` argument is our [FabPosition] parameter [floatingActionButtonPosition],
 * whose `containerColor` argument is our [Color] parameter [backgroundColor], whose `contentColor`
 * argument is our [Color] parameter [contentColor], and whose `content` argument is our Composable
 * lambda parameter [content].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [MainContainer] passes us its own [Modifier] parameter which since its own
 * caller passes it none is the empty, default, or starter [Modifier] that contains no elements.
 * @param snackBarHostState the [SnackbarHostState] that will control the [SnackbarHost] that we
 * use as the `snackbarHost` argument of our [Scaffold]. Our caller [MainContainer] passes us the
 * [JetsnackScaffoldState.snackBarHostState] of a remembered default [JetsnackScaffoldState].
 * @param topBar a lambda to use as the `topBar` argument of our [Scaffold]. Our caller [MainContainer]
 * does not pass us one so our default do-nothing lambda is used.
 * @param bottomBar the Composable lambda that we should use as the `bottomBar` of our [Scaffold].
 * Our caller calls us with an animated shared transition [JetsnackBottomBar] that has a lot of
 * complex arguments passed it which need some careful study (when I get there).
 * @param snackbarHost the Composable lambda taking a [SnackbarHostState] we should use as the
 * `snackbarHost` argument of our [Scaffold].
 * @param floatingActionButton the Composable lambda we should use as the `floatingActionButton`
 * argument of our [Scaffold]. Our caller does not pass us one so the default do-nothing lambda is
 * used.
 *  @param floatingActionButtonPosition the [FabPosition] we should use as the
 *  `floatingActionButtonPosition` of our [Scaffold]. Our caller does not pass us one so the default
 *  [FabPosition.End] is used.
 *  @param backgroundColor the [Color] we should use as the `containerColor` argument of our
 *  [Scaffold]. Our caller does not pass us one so the default [JetsnackColors.uiBackground] of our
 *  custom [JetsnackTheme.colors] is used.
 *  @param contentColor the [Color] we should use as the `contentColor` argument of our [Scaffold].
 *  Our caller does not pass us one so the default [JetsnackColors.textSecondary] of our custom
 *  [JetsnackTheme.colors] is used.
 *  @param content the Composable lambda taking [PaddingValues] that we should use as the `content`
 *  argument of our [Scaffold]. Our caller [MainContainer] passes us a [NavHost] that controls which
 *  screen is being displayed.
 */
@Composable
fun JetsnackScaffold(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    backgroundColor: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textSecondary,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = {
            snackbarHost(snackBarHostState)
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = backgroundColor,
        contentColor = contentColor,
        content = content
    )
}

/**
 * Remember and creates an instance of [JetsnackScaffoldState]. Our caller does not pass us any
 * arguments so all our default values are used.
 *
 * @param snackBarHostState the [SnackbarHostState] we should use as the `snackBarHostState` argument
 * of the [JetsnackScaffoldState] we remember and return. Since our caller [MainContainer] does not
 * pass us any the default of a remembered [SnackbarHostState] is used.
 * @param snackbarManager the [SnackbarManager] that is responsible for managing Snackbar messages
 * to show on the screen. This is our object [SnackbarManager] which we use as the `snackbarManager`
 * of the [JetsnackScaffoldState] we remember and return.
 * @param resources a [Resources] instance to use as the `resources` argument of the [JetsnackScaffoldState]
 * we remember and return. We use the [Resources] returned by our [resources] function, which is the
 * Resources instance for the application's package according to the current [LocalContext].
 * @param coroutineScope a remembered [CoroutineScope] to use as the `coroutineScope` argument of the
 * [JetsnackScaffoldState] we remember and return. We use the remembered [CoroutineScope] that the
 * method [rememberCoroutineScope] returns.
 */
@Composable
fun rememberJetsnackScaffoldState(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): JetsnackScaffoldState = remember(snackBarHostState, snackbarManager, resources, coroutineScope) {
    JetsnackScaffoldState(
        snackBarHostState = snackBarHostState,
        snackbarManager = snackbarManager,
        resources = resources,
        coroutineScope = coroutineScope
    )
}

/**
 * Responsible for holding [SnackbarHostState] and handling the logic of showing snackbar messages.
 * In our `init` block we use the [CoroutineScope.launch] method of our [CoroutineScope] parameter
 * [coroutineScope] to launch a coroutine in which we use the [StateFlow.collect] method of the
 * [StateFlow] wrapped [List] of [Message] field [SnackbarManager.messages] of our [SnackbarManager]
 * parameter [snackbarManager] to collect and emit into the `collector` [FlowCollector] block the
 * current [List] of [Message] it contains which the block accepts as its `currentMessages` variable.
 * In the `collector` block we check if `currentMessages` is not empty and if so we set our [Message]
 * variable `val message` to the [Message] at index 0 in `currentMessages`, and set our [CharSequence]
 * variable `val text` to the [CharSequence] whose resource ID is the [Message.messageId] of our
 * `message` variable. We then call the [SnackbarManager.setMessageShown] method of our [SnackbarManager]
 * parameter [snackbarManager] to Notify the SnackbarManager that it can remove the current message
 * from its list. Finally we call the [SnackbarHostState.showSnackbar] of our [SnackbarHostState]
 * parameter [snackBarHostState] to Display the snackbar on the screen. `showSnackbar` is a function
 * that suspends until the snackbar disappears from the screen.
 *
 * @param snackBarHostState the [SnackbarHostState] we are to hold.
 * @param snackbarManager the singleton [SnackbarManager] of the app.
 * @param resources a [Resources] instance for the application's package.
 * @param coroutineScope a [CoroutineScope] we can use to launch a coroutine.
 */
@Stable
class JetsnackScaffoldState(
    val snackBarHostState: SnackbarHostState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    private val coroutineScope: CoroutineScope
) {
    // Process snackbars coming from SnackbarManager
    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages: List<Message> ->
                if (currentMessages.isNotEmpty()) {
                    val message: Message = currentMessages[0]
                    val text: CharSequence = resources.getText(message.messageId)
                    // Notify the SnackbarManager so it can remove the current message from the list
                    snackbarManager.setMessageShown(messageId = message.id)
                    // Display the snackbar on the screen. `showSnackbar` is a function
                    // that suspends until the snackbar disappears from the screen
                    snackBarHostState.showSnackbar(message = text.toString())
                }
            }
        }
    }
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when `Configuration`
 * gets updated.
 *
 * @return the [Resources] instance for the application's package according to the current
 * [LocalContext].
 */
@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
