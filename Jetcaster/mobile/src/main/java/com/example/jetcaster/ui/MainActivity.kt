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

package com.example.jetcaster.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.window.layout.DisplayFeature
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the Jetcaster application.
 *
 * This activity serves as the entry point for the application and is responsible for:
 * - Setting up the application's UI using Jetpack Compose.
 * - Enabling edge-to-edge display for immersive experience.
 * - Initializing the root composable, `JetcasterApp`, which manages the main navigation.
 * - Calculating and passing display features to `JetcasterApp` for adapting to different screen
 * configurations (e.g., foldables).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization
     * should go: calling [setContent] to create the composable UI hierarchy,
     * setting up listeners, etc.
     *
     * This function performs the following:
     * 1. Calls the superclass's `onCreate` method to perform the default
     *    activity initialization.
     * 2. Enables edge-to-edge display, allowing the app to draw behind the
     *    system bars (status bar and navigation bar). This is achieved using
     *    [enableEdgeToEdge].
     * 3. Sets the content of the activity using Jetpack Compose's [setContent]
     *    function.
     * 4. Calculates the display features (such as foldables, etc.) using
     *    [calculateDisplayFeatures].
     * 5. Uses the [JetcasterTheme] to apply the app's theme.
     * 6. Launches the main application composable [JetcasterApp], passing the
     *    calculated `displayFeatures` for UI adaptations.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [onSaveInstanceState]. **Note: Otherwise it is null.**
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val displayFeatures: List<DisplayFeature> = calculateDisplayFeatures(activity = this)

            JetcasterTheme {
                JetcasterApp(
                    displayFeatures = displayFeatures
                )
            }
        }
    }
}
