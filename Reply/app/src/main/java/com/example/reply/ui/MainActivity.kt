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

package com.example.reply.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.layout.DisplayFeature
import com.example.reply.data.Email
import com.example.reply.data.local.LocalEmailsDataProvider
import com.example.reply.ui.theme.ContrastAwareReplyTheme
import com.example.reply.ui.utils.ReplyContentType
import com.google.accompanist.adaptive.calculateDisplayFeatures
import kotlinx.coroutines.flow.StateFlow

/**
 * Main activity of the application.
 */
class MainActivity : ComponentActivity() {

    /**
     * This is the [ReplyHomeViewModel] that is used to communicate between the UI and the business
     * model throughout the app.
     */
    private val viewModel: ReplyHomeViewModel by viewModels()

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable the edge to
     * edge display for Andoid versions older than Android 15 (where it is enabled by default). Then
     * we call our super's implementation of `onCreate`. Finally, we call [setContent] to have it
     * it Compose its `content` composable lambda argument into our activity. In that lambda we use
     * our [ContrastAwareReplyTheme] custom [MaterialTheme] to wrap its `content` Composable lambda
     * argument and supply [MaterialTheme] values for the Composables in that lambda. Within that
     * lambda we first initialize our [WindowSizeClass] variable `val windowSize` to the
     * [WindowSizeClass] returned by [calculateWindowSizeClass] and initialize our [List] of
     * [DisplayFeature] variable `val displayFeatures` to the [List] of [DisplayFeature] returned by
     * the [calculateDisplayFeatures] function. Then we initialize our [State] wrapped
     * [ReplyHomeUIState] variable `val uiState` to the [State] wrapped [ReplyHomeUIState] returned
     * by the [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of [ReplyHomeUIState]
     * field [ReplyHomeViewModel.uiState] of our [viewModel] field. We then compose a [ReplyApp]
     * with its [WindowSizeClass] `windowSize` argument set to our `windowSize` variable, its [List]
     * of [DisplayFeature] `displayFeatures` argument set to our `displayFeatures` variable, its
     * [ReplyHomeUIState] `replyHomeUIState` argument set to our `uiState` variable, its lambda
     * `closeDetailScreen` argument set to a lambda that calls the [ReplyHomeViewModel.closeDetailScreen]
     * method of our [viewModel] field, its lambda `navigateToDetail` argument set to a lambda that
     * calls the [ReplyHomeViewModel.setOpenedEmail] method of our [viewModel] field with the
     * [Email.id] of the [Email] to be displayed and the [ReplyContentType] appropriate for the
     * device we are running on, and its lambda `toggleSelectedEmail` argument is a lambda that
     * calls the [ReplyHomeViewModel.toggleSelectedEmail] method of our [viewModel] field with the
     * [Email.id] of the [Email] whose selected state is to be toggled.
     *
     * @param savedInstanceState The [Bundle] of this activity's previously saved state. We do not
     * override [onSaveInstanceState] so do not use.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ContrastAwareReplyTheme {
                val windowSize: WindowSizeClass = calculateWindowSizeClass(activity = this)
                val displayFeatures: List<DisplayFeature> = calculateDisplayFeatures(activity = this)
                val uiState: ReplyHomeUIState by viewModel.uiState.collectAsStateWithLifecycle()

                ReplyApp(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                    replyHomeUIState = uiState,
                    closeDetailScreen = {
                        viewModel.closeDetailScreen()
                    },
                    navigateToDetail = { emailId: Long, pane: ReplyContentType ->
                        viewModel.setOpenedEmail(emailId, pane)
                    },
                    toggleSelectedEmail = { emailId ->
                        viewModel.toggleSelectedEmail(emailId)
                    }
                )
            }
        }
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun ReplyAppPreview() {
    ContrastAwareReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
            displayFeatures = emptyList(),
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 700, heightDp = 500)
@Composable
fun ReplyAppPreviewTablet() {
    ContrastAwareReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(700.dp, 500.dp)),
            displayFeatures = emptyList(),
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 500, heightDp = 700)
@Composable
fun ReplyAppPreviewTabletPortrait() {
    ContrastAwareReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(500.dp, 700.dp)),
            displayFeatures = emptyList(),
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 1100, heightDp = 600)
@Composable
fun ReplyAppPreviewDesktop() {
    ContrastAwareReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(1100.dp, 600.dp)),
            displayFeatures = emptyList(),
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 600, heightDp = 1100)
@Composable
fun ReplyAppPreviewDesktopPortrait() {
    ContrastAwareReplyTheme {
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(600.dp, 1100.dp)),
            displayFeatures = emptyList(),
        )
    }
}
