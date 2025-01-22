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

package com.example.jetcaster.core.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Base DAO that defines basic CRUD operations. All our other DAOs should extend this.
 *
 * @param T the database entity type.
 */
interface BaseDao<T> {
    /**
     * The `@Insert` annotation marks this as an insert method. It will insert its [T] parameter
     * [entity] into the database. If the record already exists in the database then this will
     * replace it due to the `onConflict` parameter of [OnConflictStrategy.REPLACE].
     *
     * @param entity the entity to insert into the database.
     * @return the new rowId for the inserted item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    /**
     * The `@Insert` annotation marks this as an insert method. It will insert all of its vararg
     * [T] parameters [entity] into the database. If the records already exist in the database then
     * this will replace them due to the `onConflict` parameter of [OnConflictStrategy.REPLACE].
     *
     * @param entity the [T] entities to insert into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entity: T)

    /**
     * The `@Insert` annotation marks this as an insert method. It will insert the [Collection] of
     * [T] parameter [entities] into the database. If the record already exists in the database this
     * will replace it due to the `onConflict` parameter of [OnConflictStrategy.REPLACE].
     *
     * @param entities the [Collection] of [T] entities to insert into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: Collection<T>)

    /**
     * The `@Update` annotation marks this as an update method. The implementation of the method will
     * update its [T] parameter [entity] in the database if it already exists (checked by primary key).
     * If it doesn't exist this method will not change the database.
     *
     * @param entity the [T] entity to update in the database.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: T)

    /**
     * The `@Delete` annotation Marks a method in a Dao annotated class as a delete method. The
     * implementation of the method will delete its [T] parameter [entity] from the database.
     *
     * @param entity the [T] entity to delete from the database.
     * @return the number of rows deleted.
     */
    @Delete
    suspend fun delete(entity: T): Int
}
