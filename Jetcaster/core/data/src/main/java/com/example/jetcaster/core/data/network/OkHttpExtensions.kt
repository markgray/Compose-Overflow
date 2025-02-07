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

package com.example.jetcaster.core.data.network

import kotlinx.coroutines.CancellableContinuation
import java.io.IOException
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.internal.closeQuietly


/**
 * Suspending wrapper around an OkHttp [Call], using [Call.enqueue]. Suspends the coroutine and
 * awaits the result of this [Call].
 *
 * This function provides a convenient way to use OkHttp's asynchronous API with Kotlin coroutines.
 * It internally uses [suspendCancellableCoroutine] to suspend the current coroutine and resumes
 * it when the [Call] completes, either successfully or with a failure.
 *
 * **Cancellation:**
 * - If the coroutine is cancelled while waiting, the underlying [Call] will be cancelled.
 * - If the coroutine is cancelled after a successful response is received but before it's resumed,
 * the response body will be closed.
 *
 * **Error Handling:**
 * - Any [IOException] that occurs during the network request will be thrown as an exception.
 *
 * Code explanation:
 *  1. [suspendCancellableCoroutine] is used to suspend the coroutine and provide a
 *  [CancellableContinuation] object.
 *  2. [CancellableContinuation]<[Response]>: This represents a continuation point for the coroutine.
 *  It's like a placeholder where the coroutine will resume its execution once the asynchronous
 *  operation (the network request) is complete. The <[Response]> type parameter indicates that this
 *  continuation will eventually provide a [Response] object.
 *  3. `continuation`: This is the [CancellableContinuation] object. You use it to resume the coroutine,
 *  either with a successful result or an exception.
 *  3. `block`: ([CancellableContinuation] `<T>`) -> Unit: This is a lambda that takes a
 *  [CancellableContinuation] as a parameter. It is the block of code that will be executed to handle
 *  the asynchronous operation.
 *  4. `enqueue()`: This is the standard asynchronous method from `OkHttp`'s Call class. It takes a
 *  `Callback` object, which has two methods: `onResponse()` and `onFailure()`.
 *  5. `object : Callback { ... }`: This creates an anonymous object that implements the `Callback`
 *  interface. This is where you define what happens when the network request succeeds or fails.
 *  6. `override fun onResponse(call: Call, response: Response) { ... }`: This method is called by
 *  `OkHttp` when the network request is successful.
 *    - `continuation.resume()`: This method resumes the coroutine.
 *    - `value = response`: This provides the [Response] object as the result of the coroutine.
 *    - `{ ... }`: This is a completion handler. It's a lambda that is executed if the coroutine is
 *    cancelled after the response has been received but before the coroutine has fully resumed.
 *    - `response.closeQuietly()`: This ensures that if the coroutine is cancelled in the completion
 *    handler, the response body is closed to prevent resource leaks.
 *  7. `override fun onFailure(call: Call, e: IOException) { ... }`: This method is called by `OkHttp`
 *  if the network request fails.
 *    - `continuation.resumeWithException(e)`: This resumes the coroutine by throwing the [IOException]
 *    that occurred during the request. This will propagate the exception to the code that called
 *    await().
 *
 * **Usage Example:**
 * ```kotlin
 * val client = OkHttpClient()
 * val request = Request.Builder()
 *     .url("https://www.example.com")
 *     .build()
 * val call = client.newCall(request)
 *
 * try {
 *     val response = call.await()
 *     // Process the response here
 *     println("Response code: ${response.code}")
 *     println("Response body: ${response.body?.string()}")
 *     response.close()
 * } catch (e: IOException) {
 *     // Handle the network error
 *     println("Network error: ${e.message}")
 * }
 * ```
 *
 * @return The [Response] from the server.
 * @throws IOException If a network error occurs during the request.
 */
@Suppress("unused")
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun Call.await(): Response =
    suspendCancellableCoroutine { continuation: CancellableContinuation<Response> ->
        enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(value = response) {
                        // If we have a response but we're cancelled while resuming, we need to
                        // close() the unused response
                        if (response.body != null) {
                            response.closeQuietly()
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
            }
        )

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (t: Throwable) {
                // Ignore cancel exception
            }
        }
    }
