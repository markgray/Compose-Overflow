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

import com.example.reply.data.local.LocalEmailsDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * This is an implementation of the [EmailsRepository] Interface contract to get all emails info
 * for a User.
 */
class EmailsRepositoryImpl : EmailsRepository {

    /**
     * Returns a [Flow] of the [List] of [Email] objects found in [LocalEmailsDataProvider.allEmails]
     */
    override fun getAllEmails(): Flow<List<Email>> = flow {
        emit(LocalEmailsDataProvider.allEmails)
    }

    /**
     * Returns a [Flow] of the [List] of [Email] objects found in [LocalEmailsDataProvider.allEmails]
     * filtered to include only [Email] whose [Email.mailbox] field matches our [MailboxType] parameter
     * [category].
     */
    override fun getCategoryEmails(category: MailboxType): Flow<List<Email>> = flow {
        val categoryEmails = LocalEmailsDataProvider.allEmails.filter { it.mailbox == category }
        emit(categoryEmails)
    }

    /**
     * Returns the [List] of `EmailFolder`s by which [Email]s can be categorized.
     */
    override fun getAllFolders(): List<String> {
        return LocalEmailsDataProvider.getAllFolders()
    }

    /**
     * Returns a [Flow] of a single [Email] if one can be found whose [Email.id] matches our [Long]
     * parameter [id], otherwise it returns `null`.
     */
    @Suppress("UNUSED_VARIABLE") // Easier to breakpoint this way
    override fun getEmailFromId(id: Long): Flow<Email?> = flow {
        val categoryEmails = LocalEmailsDataProvider.allEmails.firstOrNull { it.id == id }
    }
}
