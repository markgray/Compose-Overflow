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

package com.example.jetcaster.core.data

import javax.inject.Qualifier

/**
 * Qualifier annotation for injecting different [kotlinx.coroutines.CoroutineDispatcher]'s.
 *
 * This annotation is used in conjunction with Hilt to differentiate between different types of
 * [kotlinx.coroutines.CoroutineDispatcher] instances.
 *
 * By annotating a `CoroutineDispatcher` dependency with `@Dispatcher`, you specify which
 * kind of dispatcher you need, using the `jetcasterDispatcher` parameter.
 *
 * This allows you to inject different dispatchers for specific tasks like:
 *  - `IO`: For network or disk operations.
 *  - `Default`: For CPU-intensive tasks.
 *  - `Main`: For UI related operations.
 *  - `MainImmediate`: For immediate UI related operations.
 *
 * @property jetcasterDispatcher The specific type of [JetcasterDispatchers] that this
 * dispatcher instance represents.
 *
 * @see JetcasterDispatchers
 * @see kotlinx.coroutines.CoroutineDispatcher
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val jetcasterDispatcher: JetcasterDispatchers)

/**
 * Enum class representing the different dispatchers used in the Jetcaster application.
 *
 * Dispatchers determine which thread or thread pool a coroutine will run on.
 * This enum provides convenient aliases for common dispatchers.
 */
enum class JetcasterDispatchers {
    /**
     * This dispatcher is used for UI related operations.
     */
    Main,
    /**
     * This dispatcher is used for network or disk operations.
     */
    IO,
}
