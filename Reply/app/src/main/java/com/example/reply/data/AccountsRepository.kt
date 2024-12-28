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
 * An Interface contract to get all accounts info for the current User.
 */
interface AccountsRepository {
    /**
     * Get the current user's default account.
     */
    fun getDefaultUserAccount(): Flow<Account>

    /**
     * Get all of the accounts owned by the current user.
     */
    fun getAllUserAccounts(): Flow<List<Account>>

    /**
     * Get the contact of the user whose [Account.id] is the [Long] parameter [uid] among all of the
     * current user's contacts.
     */
    fun getContactAccountByUid(uid: Long): Flow<Account>
}
