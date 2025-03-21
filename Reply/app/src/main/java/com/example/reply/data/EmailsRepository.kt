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

package com.example.reply.data

import kotlinx.coroutines.flow.Flow

/**
 * An Interface contract to get all emails info for a User. It is implemented by [EmailsRepositoryImpl]
 */
interface EmailsRepository {
    /**
     * Returns a [Flow] of a [List] of [Email] objects.
     */
    fun getAllEmails(): Flow<List<Email>>

    /**
     * Returns a [Flow] of a [List] of [Email] objects filtered to include only [Email] whose
     * [Email.mailbox] field matches our [MailboxType] parameter [category].
     */
    fun getCategoryEmails(category: MailboxType): Flow<List<Email>>

    /**
     * Returns a [List] of `EmailFolder`s by which [Email]s can be categorized.
     */
    fun getAllFolders(): List<String>

    /**
     * Returns a [Flow] of a single [Email] if one can be found whose [Email.id] matches our [Long]
     * parameter [id], otherwise it returns `null`.
     */
    fun getEmailFromId(id: Long): Flow<Email?>
}
