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

package com.example.jetcaster.core.data.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * This is the data class that defines our `podcasts` table in the database.
 *  - `@Entity(...)`: This is a Room annotation that tells Room that this class represents a table
 *  in our database.
 *  - `tableName = "podcasts"`: This specifies that the table in the database should be named
 *  "podcasts". If you omit this, the table name defaults to the class name ("Podcast").
 *  - `indices = [...]`: This defines indexes for the table. Indexes speed up data retrieval.
 *  - `Index("uri", unique = true)`: This creates an index on the uri column. The unique = true part
 *  means that the values in the uri column must be unique across all rows in the table.
 *  - `data class Podcast(...)`: This defines a Kotlin data class named [Podcast]. Data classes are
 *  concise way to create classes that primarily hold data. The compiler automatically generates
 *  useful methods like equals(), hashCode(), toString(), and copy().
 *  - `@Immutable`: This annotation from Compose indicates that instances of this class are
 *  immutable. Once created, their properties cannot be changed. This is a good practice for data
 *  classes and helps with performance in Compose.
 */
@Entity(
    tableName = "podcasts",
    indices = [
        Index("uri", unique = true)
    ]
)
@Immutable
data class Podcast(
    /**
     * This is the uri of the podcast, which is the primary key of the table.
     *  - `@PrimaryKey`: This annotation indicates that the uri property is the primary key for the
     *  table. A primary key uniquely identifies each row in the table.
     *  - `@ColumnInfo(name = "uri")`: This annotation specifies that the column in the database
     *  should be named "uri". If you omitted this, the column name would default to the property
     *  name ("uri").
     *  - `val uri: String`: This is the actual property. It's a [String] and is immutable (val).
     *  Because it's the primary key, it must be unique and not `null`.
     */
    @PrimaryKey @ColumnInfo(name = "uri") val uri: String,
    /**
     * This is the name of the podcast.
     *  - `@ColumnInfo(name = "title")`: Specifies the column name as "title".
     *  - `val title: String`: The podcast's title, stored as a [String].
     */
    @ColumnInfo(name = "title") val title: String,
    /**
     * This is the description of the podcast.
     *  - `@ColumnInfo(name = "description")`: Specifies the column name as "description".
     *  - `val description: String? = null`: The podcast's description. The ? means it's nullable,
     *  and = null sets the default value to null.
     */
    @ColumnInfo(name = "description") val description: String? = null,
    /**
     * This is the author of the podcast.
     *  - `@ColumnInfo(name = "author")`: Specifies the column name as "author".
     *  - `val author: String? = null`: The podcast's author, which can be null.
     */
    @ColumnInfo(name = "author") val author: String? = null,
    /**
     * This is the URL of the podcast's image.
     *  - `@ColumnInfo(name = "image_url")`: Specifies the column name as "image_url".
     *  - `val imageUrl: String? = null`: The URL of the podcast's image, which can be null.
     */
    @ColumnInfo(name = "image_url") val imageUrl: String? = null,
    /**
     * This is the copyright information of the podcast.
     *  - `@ColumnInfo(name = "copyright")`: Specifies the column name as "copyright".
     *  - `val copyright: String? = null`: The podcast's copyright information, which can be null.
     */
    @ColumnInfo(name = "copyright") val copyright: String? = null
)
