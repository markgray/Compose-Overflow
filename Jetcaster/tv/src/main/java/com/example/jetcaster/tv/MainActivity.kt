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

package com.example.jetcaster.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.Surface
import com.example.jetcaster.tv.ui.JetcasterApp
import com.example.jetcaster.tv.ui.theme.JetcasterTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point of the Jetcaster application.
 *
 * This activity sets up the application's UI using Jetpack Compose. It configures
 * the theme, including enforcing dark mode, and initializes the main application
 * composable, [JetcasterApp].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * The main entry point for the Activity. This function is called when the
     * Activity is first created.
     *
     * This method initializes the content of the Activity using Jetpack Compose.
     * It sets up the root composable content of the application.
     *
     * First we call our super's implementation of `onCreate`. Then we call [setContent]
     * to set the content of the Activity. In the `content` composable lambda argument we
     * compose a [JetcasterTheme] with its `isInDarkTheme` set to `true`. This ensures
     * that the theme is set to dark mode. In the `content` composable lambda argument
     * of the [JetcasterTheme] we compose a [Surface] with its `modifier` set to
     * [Modifier.fillMaxSize] and its `shape` set to [RectangleShape]. In the [BoxScope]
     * `content` composable lambda argument of the [Surface] we call our [JetcasterApp]
     * composable.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [onSaveInstanceState].  **Note: Otherwise it is null.**
     * We do not override [onSaveInstanceState] so it is not used.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState = savedInstanceState)
        setContent {
            // TV is hardcoded to dark mode to match TV ui
            JetcasterTheme(isInDarkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    JetcasterApp()
                }
            }
        }
    }
}
