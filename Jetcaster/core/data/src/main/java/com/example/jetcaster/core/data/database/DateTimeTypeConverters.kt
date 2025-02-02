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

package com.example.jetcaster.core.data.database

import androidx.room.TypeConverter
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * This code defines a set of [TypeConverter] functions for Room, which are used to convert between
 * `java.time.*` classes and their string or numeric representations for storage in the database.
 * `object DateTimeTypeConverters`: This defines a Kotlin object, which is a singleton. This is a
 * common pattern for utility classes like this one.
 */
object DateTimeTypeConverters {
    /**
     * Obtains an instance of [OffsetDateTime] from a text string such as 2007-12-03T10:15:30+01:00.
     * The string is parsed using `DateTimeFormatter.ISO_OFFSET_DATE_TIME`. The anotations:
     *  1. `@TypeConverter`: This annotation tells Room that the function is a type converter.
     *  2. `@JvmStatic`: This annotation makes the function a static method in the generated Java
     *  bytecode. This is necessary for Room to be able to find and use these converters.
     *
     * @param value the text to parse such as "2007-12-03T10:15:30+01:00".
     * @return the parsed [OffsetDateTime] or `null` if [value] is `null`.
     */
    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let { OffsetDateTime.parse(it) }
    }

    /**
     * Converts an [OffsetDateTime] object to its [String] representation. The anotations:
     *  1. `@TypeConverter`: This annotation tells Room that the function is a type converter.
     *  2. `@JvmStatic`: This annotation makes the function a static method in the generated Java
     *  bytecode. This is necessary for Room to be able to find and use these converters.
     *
     * @param date a nullable [OffsetDateTime].
     * @return the [String] representation of the [OffsetDateTime] or `null` if [date] is `null`.
     */
    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    /**
     * Converts a [String] to a [LocalDateTime]. The anotations:
     *  1. `@TypeConverter`: This annotation tells Room that the function is a type converter.
     *  2. `@JvmStatic`: This annotation makes the function a static method in the generated Java
     *  bytecode. This is necessary for Room to be able to find and use these converters.
     *
     * @param value a nullable [String] to convert to a [LocalDateTime].
     * @return the [LocalDateTime] or `null` if [value] is `null`.
     */
    @Suppress("unused")
    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value) }
    }

    /**
     * Converts a LocalDateTime to a String. The anotations:
     *  1. `@TypeConverter`: This annotation tells Room that the function is a type converter.
     *  2. `@JvmStatic`: This annotation makes the function a static method in the generated Java
     *  bytecode. This is necessary for Room to be able to find and use these converters.
     *
     * @param value a nullable [LocalDateTime] to convert to a [String].
     * @return a [String] representation of the [LocalDateTime] or `null` if [value] is `null`.
     */
    @Suppress("unused")
    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    /**
     * Converts a [Long] (representing milliseconds) to a [Duration]. The anotations:
     *  1. `@TypeConverter`: This annotation tells Room that the function is a type converter.
     *  2. `@JvmStatic`: This annotation makes the function a static method in the generated Java
     *  bytecode. This is necessary for Room to be able to find and use these converters.
     *
     * @param value a nullable [Long] to convert to a [Duration].
     * @return a [Duration] version of [value] or `null` if [value] is `null`.
     */
    @TypeConverter
    @JvmStatic
    fun toDuration(value: Long?): Duration? {
        return value?.let { Duration.ofMillis(it) }
    }

    /**
     * Converts a [Duration] to a [Long] (representing milliseconds). The anotations:
     *  1. `@TypeConverter`: This annotation tells Room that the function is a type converter.
     *  2. `@JvmStatic`: This annotation makes the function a static method in the generated Java
     *  bytecode. This is necessary for Room to be able to find and use these converters.
     *
     * @param value a nullable [Duration] to convert to a [Long] number of milliseconds.
     * @return a [Long] representation of the milliseconds in [Duration] or `null` if [value]
     * is `null`.
     */
    @TypeConverter
    @JvmStatic
    fun fromDuration(value: Duration?): Long? {
        return value?.toMillis()
    }
}
