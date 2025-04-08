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

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * [JetCasterTvApp] is the main application class for the JetCaster TV app.
 *
 * This class extends [Application] and is annotated with [HiltAndroidApp],
 * which triggers Hilt's code generation for dependency injection.
 *
 * **Key Responsibilities:**
 *  - **Application Entry Point:** Serves as the entry point for the application's lifecycle.
 *  - **Hilt Integration:** Initializes Hilt for dependency injection within the application.
 *  - **Global Application Context:** Provides access to the application context throughout the app.
 *
 * **Usage:**
 *  -  This class is automatically referenced in the AndroidManifest.xml file via the `android:name`
 *  attribute of the `<application>` tag. There is typically no need to instantiate it directly in
 *  your code.
 *
 * **Notes:**
 *  - All modules and components used by the app should be defined within the application's Hilt setup.
 *  - This class lives throughout the lifetime of the app.
 */
@HiltAndroidApp
class JetCasterTvApp : Application()
