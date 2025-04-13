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

import android.app.Application
import android.os.StrictMode
import android.widget.ImageView
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * [JetcasterWearApplication] is the main application class for the Jetcaster Wear app.
 *
 * It extends [Application] and uses [HiltAndroidApp] for dependency injection.
 * It also implements [ImageLoaderFactory] to provide a custom [ImageLoader] instance for Coil.
 *
 * This class initializes the application, configures StrictMode for debugging,
 * and makes the [ImageLoader] available for injection.
 */
@HiltAndroidApp
class JetcasterWearApplication : Application(), ImageLoaderFactory {

    /**
     * The [ImageLoader] instance used for loading and caching images.
     *
     * This property is dependency-injected using Dagger/Hilt. It provides a centralized
     * mechanism for managing image loading tasks within the application.
     *
     * The [ImageLoader] handles:
     *  - Fetching image data from various sources (network, local storage, resources).
     *  - Decoding image data into bitmaps.
     *  - Applying image transformations (resizing, cropping, etc.).
     *  - Caching images in memory and on disk to optimize performance and reduce network usage.
     *  - Managing image loading requests, including cancellations and priorities.
     *
     * You can use this instance to load images into UI components, such as [ImageView]s,
     * using Coil's API (e.g., `imageView.load(...)`).
     *
     * Note: This property should be considered a singleton within the application's
     * dependency graph. Avoid creating multiple [ImageLoader] instances.
     *
     * @see coil.ImageLoader
     * @see coil.load
     */
    @Inject lateinit var imageLoader: ImageLoader

    /**
     * Called when the activity is first created.
     *
     * This method performs the following tasks:
     *  1. Calls the superclass's [onCreate] method to perform standard activity initialization.
     *  2. Calls [setStrictMode] to configure [StrictMode] strict mode for development purposes.
     *  This typically includes detecting things like disk or network access on the main thread.
     */
    override fun onCreate() {
        super.onCreate()
        setStrictMode()
    }

    /**
     * Enables Strict Mode for the application.
     *
     * This function configures StrictMode to detect and report violations of common
     * performance and best-practice rules during development. Specifically, it configures
     * the thread policy to:
     *
     * - `detectDiskReads()`: Detects any disk read operations performed on the main thread.
     * - `detectDiskWrites()`: Detects any disk write operations performed on the main thread.
     * - `detectNetwork()`: Detects any network operations performed on the main thread.
     * - `penaltyLog()`: Logs any detected violations to the system log.
     *
     * By enabling these checks during development, potential performance bottlenecks and
     * issues related to main thread responsiveness can be identified and resolved early.
     *
     * Note: Strict Mode is primarily intended for use during development and should generally
     * be disabled or its penalties significantly reduced in production builds.
     */
    private fun setStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build(),
        )
    }

    /**
     * Provides a new instance of the application's shared [ImageLoader].
     *
     * This function returns the same [ImageLoader] instance that is used
     * throughout the application for image loading. It's designed to be
     * overridden in subclasses (e.g., within test classes) to provide
     * a different, potentially mocked or stubbed, [ImageLoader] for
     * testing purposes.
     *
     * @return The shared [ImageLoader] instance used by the application.
     */
    override fun newImageLoader(): ImageLoader =
        imageLoader
}
