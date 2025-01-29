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

package com.example.jetcaster.core.data.database.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Transaction
import com.example.jetcaster.core.data.repository.PodcastsRepository

/**
 * `Room` DAO which provides the implementation for our [TransactionRunner]. It is injected by
 * `Hilt` to be used by [PodcastsRepository] to run multiple `Room` operations in an atomic
 * `@Transaction`.
 */
@Dao
abstract class TransactionRunnerDao : TransactionRunner {
    /**
     * This method runs its `lambda` parmameter [tx] in an atomic `@Transaction`. It is not used
     * directly, instead it is called by invoking an instance of [TransactionRunner] as if it were
     * a function. The `@Transaction` annotation is the key to ensuring database integrity. It
     * guarantees that either all the operations within the lambda succeed, or none of them do.
     *
     * @param tx `suspend` Lambda that should be executed inside an atomic `@Transaction`.
     */
    @Transaction
    protected open suspend fun runInTransaction(tx: suspend () -> Unit): Unit = tx()

    /**
     * [invoke] is a very Kotlin-idiomatic way to make a [TransactionRunner] instance callable like
     * a function. It makes the code more concise and readable. The `@Ignore` annotation is used to
     * prevent Room from generating an implementation for the invoke function. This is because the
     * implementation is provided in the abstract class. It just calls our protected [runInTransaction]
     * method with its lambda parameter [tx].
     *
     * @param tx `suspend` Lambda that should be executed inside an atomic `@Transaction`.
     */
    @Ignore
    override suspend fun invoke(tx: suspend () -> Unit) {
        runInTransaction(tx)
    }
}

/**
 * Interface with operator function which will invoke the suspending lambda within a single database
 * transaction.
 */
interface TransactionRunner {
    /**
     * Invoke the [tx] lambda within an atomic database transaction.
     *
     * @param tx `suspend` Lambda that should be executed inside an atomic `@Transaction`.
     */
    suspend operator fun invoke(tx: suspend () -> Unit)
}
