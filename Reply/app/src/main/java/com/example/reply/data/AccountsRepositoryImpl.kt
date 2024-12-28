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

import com.example.reply.data.local.LocalAccountsDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * This is apparently an unused stub for a future internet or database repository(?). All uses of
 * the data in [LocalAccountsDataProvider] are direct rather than indirect calls to this class.
 */
@Suppress("unused")
class AccountsRepositoryImpl : AccountsRepository {

    /**
     * Get the current user's default account as a [Flow] of [Account].
     */
    override fun getDefaultUserAccount(): Flow<Account> = flow {
        emit(LocalAccountsDataProvider.getDefaultUserAccount())
    }

    /**
     * Get all of the accounts owned by the current user as a [Flow] of [List] of [Account].
     */
    override fun getAllUserAccounts(): Flow<List<Account>> = flow {
        emit(LocalAccountsDataProvider.allUserAccounts)
    }

    /**
     * Get the contact of the user whose [Account.id] is the [Long] parameter [uid] as a
     * [Flow] of [Account].
     *
     * @param uid The [Account.id] of the contact to fetch.
     */
    override fun getContactAccountByUid(uid: Long): Flow<Account> = flow {
        emit(LocalAccountsDataProvider.getContactAccountByUid(uid))
    }
}
