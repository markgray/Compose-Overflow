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

package com.example.jetcaster

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/*
 * Application which sets up our dependency `Graph` with a context.
 */

/**
 * [JetcasterApplication] is the main Application class for the Jetcaster app.
 *
 * It's annotated with [HiltAndroidApp] to enable dependency injection with Hilt.
 * This class also implements [ImageLoaderFactory] to provide a custom [ImageLoader]
 * instance for the Coil image loading library.
 *
 * This class does the following:
 *  1. **Enables Hilt Dependency Injection:**
 *     - The `@HiltAndroidApp` annotation triggers Hilt's code generation for dependency injection.
 *     - This allows classes throughout the application to receive dependencies via `@Inject`.
 *
 *  2. **Provides a Custom ImageLoader:**
 *     - Implements [ImageLoaderFactory] to configure and provide a custom [ImageLoader] for Coil.
 *     - The [newImageLoader] function returns the injected `imageLoader` instance, allowing Coil
 *       to use the application-wide configured ImageLoader.
 *     - The `imageLoader` dependency is injected using the `@Inject` annotation. This means
 *        Hilt is responsible for creating and providing this instance.
 *
 *  3. **Serves as the Application Entry Point:**
 *      - As the main application class, it's the first code to run when the application launches.
 *      - It handles application-level initialization.
 */
@HiltAndroidApp
class JetcasterApplication : Application(), ImageLoaderFactory {

    /**
     * The [ImageLoader] instance used for loading and caching images.
     *
     * This property is dependency-injected using Dagger/Hilt. It provides a centralized
     * mechanism for managing image loading tasks within the application.
     *
     * The [ImageLoader] handles:
     * - Fetching image data from various sources (network, local storage, resources).
     * - Decoding image data into bitmaps.
     * - Applying image transformations (resizing, cropping, etc.).
     * - Caching images in memory and on disk to optimize performance and reduce network usage.
     * - Managing image loading requests, including cancellations and priorities.
     *
     * You can use this instance to load images into UI components, such as `ImageView`s,
     * using Coil's API (e.g., `imageView.load(...)`).
     * @see coil.ImageLoader
     * @see coil.load
     */
    @Inject lateinit var imageLoader: ImageLoader

    /**
     * Returns the [ImageLoader] instance used by this component.
     *
     * This method provides access to the underlying [ImageLoader] used for image loading operations.
     * The returned [ImageLoader] is a singleton instance within this component, and can be used
     * to directly perform image loading, request transformations, or manage caching.
     *
     * @return The [ImageLoader] instance used by this component.
     */
    override fun newImageLoader(): ImageLoader = imageLoader
}
