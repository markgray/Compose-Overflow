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

package com.example.jetcaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the Wear OS application.
 *
 * This activity serves as the entry point for the application and is responsible for
 * setting up the UI and handling the application lifecycle. It uses Jetpack Compose
 * to build the user interface.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * This method is responsible for initializing the activity's UI and performing any
     * one-time setup tasks. It does the following:
     *
     * 1. Installs the splash screen:
     *    - `installSplashScreen()`:  Configures and displays the splash screen while the
     *    application loads. This ensures a smooth transition from the launch to the app's main content.
     *
     * 2. Calls the superclass implementation:
     *    - `super.onCreate(savedInstanceState)`: Invokes the `onCreate` method of the parent class
     *    (ComponentActivity), ensuring proper lifecycle management.
     *
     * 3. Sets the content view:
     *    - `setContent { WearApp() }`:  Uses Jetpack Compose to define the UI content of the
     *    activity. `WearApp()` is a composable function that represents the root of the Wear OS
     *    application's UI hierarchy. This effectively starts the Compose UI rendering process.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this [Bundle] contains the data it most recently supplied in
     * [onSaveInstanceState]. Note: Otherwise it is `null`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}
