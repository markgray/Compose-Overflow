/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.core.player

import com.example.jetcaster.core.player.model.PlayerEpisode
import java.time.Duration
import kotlinx.coroutines.flow.StateFlow

/**
 * The default playback speed for a media item. This is expressed as a [Duration]
 * where a duration of 1 second means the media plays at normal speed.
 *
 * A playback speed of:
 *  - `1 second` (1.0x) indicates normal playback speed.
 *  - `2 seconds` (2.0x) indicates playback at double speed.
 *  - `0.5 seconds` (0.5x) indicates playback at half speed.
 *
 * This value is used as the initial playback speed if no other speed is specified.
 * It should generally correspond to a "real-time" playback rate.
 */
val DefaultPlaybackSpeed: Duration = Duration.ofSeconds(1)

/**
 * Represents the state of an episode player.
 *
 * This data class encapsulates all the necessary information about the current playback
 * session, including the currently playing episode, the queue of episodes, the playback
 * speed, whether playback is active, and the elapsed time within the current episode.
 *
 * @property currentEpisode The [PlayerEpisode] currently being played. Null if no episode is loaded.
 * @property queue The list of [PlayerEpisode]s that are queued for playback. An empty list if the
 * queue is empty.
 * @property playbackSpeed The current playback speed as a [Duration] (e.g., 1x, 1.5x, etc.).
 * Defaults to [DefaultPlaybackSpeed].
 * @property isPlaying `true` if an episode is currently playing; `false` otherwise (paused or
 * stopped).
 * @property timeElapsed The amount of time elapsed in the current episode, represented as a
 * [Duration]. Resets to [Duration.ZERO] when a new episode starts or when no episode is loaded.
 */
data class EpisodePlayerState(
    val currentEpisode: PlayerEpisode? = null,
    val queue: List<PlayerEpisode> = emptyList(),
    val playbackSpeed: Duration = DefaultPlaybackSpeed,
    val isPlaying: Boolean = false,
    val timeElapsed: Duration = Duration.ZERO,
)

/**
 * Interface definition for an episode player defining high-level functions such as queuing
 * episodes, playing an episode, pausing, seeking, etc.
 */
interface EpisodePlayer {

    /**
     * A [StateFlow] that emits the current [EpisodePlayerState] of this player.
     */
    val playerState: StateFlow<EpisodePlayerState>

    /**
     * Gets the current episode playing, or to be played, by this player.
     */
    var currentEpisode: PlayerEpisode?

    /**
     * The speed of which the player increments
     */
    var playerSpeed: Duration

    /**
     * Adds a [PlayerEpisode] to the playback queue.
     *
     * This function takes a [PlayerEpisode] as input and appends it to the end of the current
     * playback queue. The queue represents the sequence of episodes that will be played.
     *
     * @param episode The [PlayerEpisode] to be added to the queue.
     *
     * @see PlayerEpisode
     * @see removeAllFromQueue
     */
    fun addToQueue(episode: PlayerEpisode)

    /**
     * Removes all elements from the queue, effectively emptying it.
     *
     * After calling this function, the queue will have a size of 0 and will be considered empty.
     * Any subsequent attempts to peek or dequeue from the queue will result in an exception or
     * undefined behavior depending on the queue's specific implementation.
     *
     * This operation modifies the queue in-place.
     *
     * @throws IllegalStateException if the queue is not modifiable (e.g., if it's a fixed-size
     * queue that doesn't support removal) or if any other error occurs that prevents clearing the
     * queue. The specific exception thrown may depend on the queue's implementation.
     */
    fun removeAllFromQueue()

    /**
     *  Plays the current episode.
     */
    fun play()

    /**
     * Initiates playback of the provided [PlayerEpisode].
     *
     * This function is responsible for starting the playback of a specific episode
     * within the player. It takes a [PlayerEpisode] object as input, which
     * encapsulates all the necessary information about the episode to be played,
     * such as its content URI, title, and other relevant metadata.
     *
     * The function handles tasks such as:
     * - Preparing the media player with the episode's content URI.
     * - Starting the playback process.
     * - Updating the UI to reflect the current playback state.
     * - Handling any necessary pre-playback checks or setup.
     * - Managing playback errors and interruptions.
     *
     * @param playerEpisode The [PlayerEpisode] object containing the details of the episode to be played.
     * @see PlayerEpisode
     */
    fun play(playerEpisode: PlayerEpisode)

    /**
     * Initiates playback of a list of player episodes.
     *
     * This function takes a list of [PlayerEpisode] objects and starts playing them sequentially.
     * It's responsible for handling the playback order and potentially any associated logic
     * like pre-loading or handling end-of-episode events.
     *
     * @param playerEpisodes A [List] of [PlayerEpisode] objects representing the content to
     * be played. Must not be empty. If empty an exception will be thrown.
     * @throws IllegalArgumentException if the provided list [playerEpisodes] is empty.
     */
    fun play(playerEpisodes: List<PlayerEpisode>)

    /**
     * Pauses the currently playing episode.
     */
    fun pause()

    /**
     * Stops the currently playing episode
     */
    fun stop()

    /**
     * Plays the next episode in the queue (if available)
     */
    fun next()

    /**
     * Plays the previous episode in the queue (if available). Or if an episode is currently
     * playing this will start the episode from the beginning
     */
    fun previous()

    /**
     * Advances a currently played episode by the time interval specified by [Duration] parameter
     * [duration].
     */
    fun advanceBy(duration: Duration)

    /**
     * Rewinds a currently played episode by the time interval specified by [Duration] parameter
     * [duration].
     */
    fun rewindBy(duration: Duration)

    /**
     * Called when the user starts seeking within the media.
     *
     * This function is invoked when the user initiates a seek operation, such as dragging a slider
     * or using fast-forward/rewind controls. It signifies the beginning of a seek action, before
     * the actual seek position is finalized.
     *
     * This is useful for:
     *  - Displaying a loading indicator or updating the UI to reflect the ongoing seek.
     *  - Pausing or muting audio/video temporarily during the seek operation.
     *  - Canceling any ongoing timed operations that might conflict with the seek.
     */
    fun onSeekingStarted()

    /**
     * Seeks to the time interval specified by [Duration] parameter [duration].
     */
    fun onSeekingFinished(duration: Duration)

    /**
     * Increases the speed of Player playback by the time specified by [Duration] parameter [speed].
     *
     * @param speed The duration representing the speed increase. Defaults to 500 milliseconds. A
     * shorter duration represents a faster increase in speed. Must be a non-negative duration.
     * @throws IllegalArgumentException if the provided speed duration is negative.
     */
    fun increaseSpeed(speed: Duration = Duration.ofMillis(500))

    /**
     * Decreases the speed of Player playback by the time specified by [Duration] parameter [speed].
     *
     * @param speed The duration by which to decrease the speed. Defaults to 500 milliseconds. Must
     * be a non-negative duration.
     * @throws IllegalArgumentException if the provided speed duration is negative.
     */
    fun decreaseSpeed(speed: Duration = Duration.ofMillis(500))
}
