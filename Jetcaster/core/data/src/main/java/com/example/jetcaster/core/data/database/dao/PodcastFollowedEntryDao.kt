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
import androidx.room.Query
import com.example.jetcaster.core.data.database.model.PodcastFollowedEntry

/**
 * `Room` DAO for [PodcastFollowedEntry] related operations. This table holds the podcasts which the
 * user has indictated that they want to follow by clicking the "+" button on the podcast's icon.
 */
@Dao
abstract class PodcastFollowedEntryDao : BaseDao<PodcastFollowedEntry> {
    /**
     * This method deletes the podcast whose [PodcastFollowedEntry.podcastUri] is equal to our
     * [String] parameter [podcastUri] from the `podcast_followed_entries` table.
     *
     * @param podcastUri the uri of the podcast to delete.
     */
    @Query("DELETE FROM podcast_followed_entries WHERE podcast_uri = :podcastUri")
    abstract suspend fun deleteWithPodcastUri(podcastUri: String)

    /**
     * This method counts the number of times the podcast whose [PodcastFollowedEntry.podcastUri] is
     * equal to our [String] parameter [podcastUri] occurs in the `podcast_followed_entries` table.
     * (this will be 0 or 1 of course).
     *
     * @param podcastUri the uri of the podcast to count.
     * @return the number of times the podcast occurs in the `podcast_followed_entries` table.
     */
    @Query("SELECT COUNT(*) FROM podcast_followed_entries WHERE podcast_uri = :podcastUri")
    protected abstract suspend fun podcastFollowRowCount(podcastUri: String): Int

    /**
     * This method checks if the podcast whose [PodcastFollowedEntry.podcastUri] is equal to our
     * [String] parameter [podcastUri] occurs in the `podcast_followed_entries` table. It does this
     * by returning `true` if the count returned by the [podcastFollowRowCount] method for the
     * podcast is greater than 0, otherwise `false`.
     *
     * @param podcastUri the uri of the podcast to check if it is followed.
     * @return `true` if the podcast is followed, otherwise `false`.
     */
    suspend fun isPodcastFollowed(podcastUri: String): Boolean {
        return podcastFollowRowCount(podcastUri) > 0
    }
}
