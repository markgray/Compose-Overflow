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

package com.example.jetcaster.core.di

import com.example.jetcaster.core.data.Dispatcher
import com.example.jetcaster.core.data.JetcasterDispatchers
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.core.player.MockEpisodePlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

/**
 * **DomainDiModule**
 *
 * This Hilt module provides dependencies related to the domain layer of the application.
 * It is installed in the [SingletonComponent], meaning the provided dependencies
 * will be available throughout the application's lifecycle as singletons.
 *
 * **Currently, it provides the following:**
 *
 *  - [EpisodePlayer]: An implementation of the EpisodePlayer interface, specifically
 *  [MockEpisodePlayer]. This player is intended for development or testing,
 *  as indicated by its "Mock" prefix. It is configured to run its operations
 *  on the Main dispatcher.
 *
 * **Dependencies:**
 *
 *  - [CoroutineDispatcher]: The Main CoroutineDispatcher, provided via the `@Dispatcher` qualifier.
 *
 * **Scope:**
 *
 *  - `@Singleton`: All dependencies provided by this module are singleton scoped.
 *
 * **Installation:**
 *
 *  - `@InstallIn(SingletonComponent::class)`: This module is installed in the SingletonComponent,
 *  making its bindings available throughout the application.
 *
 * @see Module
 * @see InstallIn
 * @see SingletonComponent
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainDiModule {
    /**
     * Provides a singleton instance of [EpisodePlayer].
     *
     * This function is responsible for creating and providing a single instance of
     * [EpisodePlayer] that can be injected into other parts of the application.
     * Currently, it provides a [MockEpisodePlayer] implementation for demonstration
     * or testing purposes.
     *
     * The [EpisodePlayer] is configured to use the main dispatcher, ensuring that
     * any operations related to updating the UI are performed on the main thread.
     *
     * @param mainDispatcher The [CoroutineDispatcher] representing the main thread. This dispatcher
     * is used for any UI-related operations performed by the [EpisodePlayer]. It is injected via
     * Dagger's dependency injection.
     * @return A singleton instance of [EpisodePlayer].
     *
     * @see EpisodePlayer
     * @see MockEpisodePlayer
     * @see JetcasterDispatchers.Main
     * @see CoroutineDispatcher
     * @see Provides
     * @see Singleton
     */
    @Provides
    @Singleton
    fun provideEpisodePlayer(
        @Dispatcher(JetcasterDispatchers.Main) mainDispatcher: CoroutineDispatcher
    ): EpisodePlayer = MockEpisodePlayer(mainDispatcher = mainDispatcher)
}
