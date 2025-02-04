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

package com.example.jetcaster.core.data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import coil.ImageLoader
import com.example.jetcaster.core.data.BuildConfig
import com.example.jetcaster.core.data.Dispatcher
import com.example.jetcaster.core.data.JetcasterDispatchers
import com.example.jetcaster.core.data.database.JetcasterDatabase
import com.example.jetcaster.core.data.database.dao.CategoriesDao
import com.example.jetcaster.core.data.database.dao.EpisodesDao
import com.example.jetcaster.core.data.database.dao.PodcastCategoryEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastFollowedEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastsDao
import com.example.jetcaster.core.data.database.dao.TransactionRunner
import com.example.jetcaster.core.data.network.PodcastsFetcher
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.LocalCategoryStore
import com.example.jetcaster.core.data.repository.LocalEpisodeStore
import com.example.jetcaster.core.data.repository.LocalPodcastStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.rometools.rome.io.SyndFeedInput
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import java.io.File
import javax.inject.Singleton

/**
 * This Kotlin object is a Dagger/Hilt [Module]. Its primary role is to provide dependencies for our
 * application, specifically related to data access and management. The meaning of the annotations:
 *  1. `@Module`: Annotates a class that contributes to the object graph. It defines a configuration
 *  point for your object graph, where you declare which objects you want to be available for
 *  injection, their dependencies, their scopes and how to obtain instances of them.
 *  2. `@InstallIn(SingletonComponent::class)`: An annotation that declares which component(s) the
 *  annotated class should be included in when Hilt generates the components. This may only be used
 *  with classes annotated with @Module or @EntryPoint.
 *    - `SingletonComponent::class`: A Hilt component for singleton bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {

    /**
     * [Provides] a [Singleton] configured [OkHttpClient] instance for making network requests. We
     * start by constructing an [OkHttpClient.Builder] whose [OkHttpClient.Builder.cache] method we
     * use to set the response cache to be used to read and write cached responses to the "http_cache"
     * file in the [Context.getCacheDir] returned by our [Context] parameter [context], then chain
     * to the [OkHttpClient.Builder] it returns an [apply] in which if [BuildConfig.DEBUG] is `true`
     * we chain to the [OkHttpClient.Builder] an [OkHttpClient.Builder.eventListenerFactory] to
     * configure a [LoggingEventListener.Factory] to receive per-call analytic events, then finally
     * we call [OkHttpClient.Builder.build] to build the configured [OkHttpClient] and return it to
     * the caller. It is injected by Hilt when a [PodcastsFetcher] is constructed.
     *
     * @param context the [Context] injected by Hilt thanks to the [ApplicationContext] annotation.
     * @return a configured [OkHttpClient] suitable for injection.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
        .apply {
            if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
        }
        .build()

    /**
     * Creates and [Provides] a [Singleton] instance of our [JetcasterDatabase] Room database. We
     * construct a [RoomDatabase.Builder] using the [Room.databaseBuilder] method using our [Context]
     * parameter [context] as its `context` argument, the java class of our [JetcasterDatabase]
     * class as its `klass` argument and "data.db" as its `name` argument, and chain a
     * [RoomDatabase.Builder.fallbackToDestructiveMigration] to that to allow Room to destructively
     * recreate database tables if Migrations that would migrate old database schemas to the latest
     * schema version are not found, and finally we call [RoomDatabase.Builder.build] to build and
     * initialize the [JetcasterDatabase] which we return to the caller. It is injected by Hilt
     * to the methods [provideCategoriesDao], [providePodcastCategoryEntryDao], [providePodcastsDao].
     * [provideEpisodesDao], [providePodcastFollowedEntryDao], and [provideTransactionRunner].
     *
     * @param context the [Context] injected by Hilt thanks to the [ApplicationContext] annotation.
     * @return a configured and initialized [JetcasterDatabase] suitable for injection.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): JetcasterDatabase =
        Room.databaseBuilder(context, JetcasterDatabase::class.java, "data.db")
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            .fallbackToDestructiveMigration()
            .build()

    /**
     * [Provides] a configured [Singleton] instance of an [ImageLoader] for loading and displaying
     * images. We construct an [ImageLoader.Builder] using our [Context] paramter [context], then
     * chain to it an [ImageLoader.Builder.respectCacheHeaders] with its `enable` argument `false`
     * to disable `Cache-Control` header support, then call the [ImageLoader.Builder.build] method
     * to build the [ImageLoader] which we return to the caller. It is injected in our [Application]
     * `JetcasterApplication` to be the value of its `imageLoader` field which it supplies to Coil
     * via its `newImageLoader` method, as well as in the `JetcasterWearApplication` for the same
     * purpose.
     *
     * @param context the [Context] injected by Hilt thanks to the [ApplicationContext] annotation.
     * @return a configured [Singleton] instance of an [ImageLoader] suitable for injection
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = ImageLoader.Builder(context)
        // Disable `Cache-Control` header support as some podcast images disable disk caching.
        .respectCacheHeaders(enable = false)
        .build()

    /**
     * [Provides] a [Singleton] instance of [CategoriesDao] to interact with the "categories" table
     * in the database. We just return the [CategoriesDao] that the [JetcasterDatabase.categoriesDao]
     * method of our [database] parameter returns to the caller. It is injected by Hilt to the method
     * [provideCategoryStore].
     *
     * @param database the [JetcasterDatabase] injected by Hilt using the [provideDatabase] method.
     * @return a [Singleton] instance of [CategoriesDao] to use to interact with the "categories"
     * table in the database suitable for injection.
     */
    @Provides
    @Singleton
    fun provideCategoriesDao(
        database: JetcasterDatabase
    ): CategoriesDao = database.categoriesDao()

    /**
     * [Provides] a [Singleton] instance of [PodcastCategoryEntryDao] to interact with the
     * "podcast_category_entries" table in the database. We just return the [PodcastCategoryEntryDao]
     * that the [JetcasterDatabase.podcastCategoryEntryDao] method of our [database] parameter returns
     * to our caller. It is injected by Hilt to the method [provideCategoryStore].
     *
     * @param database the [JetcasterDatabase] injected by Hilt using the [provideDatabase] method.
     * @return a [Singleton] instance of [PodcastCategoryEntryDao] to use to interact with the
     * "podcast_category_entries" table in the database suitable for injection.
     */
    @Provides
    @Singleton
    fun providePodcastCategoryEntryDao(
        database: JetcasterDatabase
    ): PodcastCategoryEntryDao = database.podcastCategoryEntryDao()

    /**
     * [Provides] a [Singleton] instance of [PodcastsDao] to interact with the "podcasts" table in
     * the database. We just return the [PodcastsDao] that the [JetcasterDatabase.podcastsDao] method
     * of our [database] parameter returns to our caller. It is injected by Hilt to the methods
     * [provideCategoryStore] and [providePodcastStore].
     *
     * @param database the [JetcasterDatabase] injected by Hilt using the [provideDatabase] method.
     * @return a [Singleton] instance of [PodcastsDao] to use to interact with the "podcasts" table
     * in the database suitable for injection.
     */
    @Provides
    @Singleton
    fun providePodcastsDao(
        database: JetcasterDatabase
    ): PodcastsDao = database.podcastsDao()

    /**
     * [Provides] a [Singleton] instance of [EpisodesDao] to interact with the "episodes" table in
     * the database. We just return the [EpisodesDao] that the [JetcasterDatabase.episodesDao] method
     * of our [database] parameter returns to our caller. It is injected by Hilt to the methods
     * [provideCategoryStore] and [provideEpisodeStore].
     *
     * @param database the [JetcasterDatabase] injected by Hilt using the [provideDatabase] method.
     * @return a [Singleton] instance of [EpisodesDao] to use to interact with the "episodes" table
     * in the database suitable for injection.
     */
    @Provides
    @Singleton
    fun provideEpisodesDao(
        database: JetcasterDatabase
    ): EpisodesDao = database.episodesDao()

    /**
     * [Provides] a [Singleton] instance of [PodcastFollowedEntryDao] to interact with the
     * "podcast_followed_entries" table in the database. We just return the [PodcastFollowedEntryDao]
     * that the [JetcasterDatabase.podcastFollowedEntryDao] method of our [database] parameter
     * returns to our caller. It is injected by Hilt to the method [providePodcastStore].
     *
     * @param database the [JetcasterDatabase] injected by Hilt using the [provideDatabase] method.
     * @return a [Singleton] instance of [PodcastFollowedEntryDao] to use to interact with the
     * "podcast_followed_entries" table in the database suitable for injection.
     */
    @Provides
    @Singleton
    fun providePodcastFollowedEntryDao(
        database: JetcasterDatabase
    ): PodcastFollowedEntryDao = database.podcastFollowedEntryDao()

    /**
     * [Provides] a [Singleton] instance of [TransactionRunner] to use to execute multiple database
     * operations as a single atomic transaction. We just return the [TransactionRunner] that the
     * [JetcasterDatabase.transactionRunnerDao] method of our [database] parameter returns to our
     * caller. It is injected by Hilt to the methods [PodcastsRepository] and [providePodcastStore].
     *
     * @param database the [JetcasterDatabase] injected by Hilt using the [provideDatabase] method.
     * @return a [Singleton] instance of [TransactionRunner] to use to run multiple database
     * operations as a single atomic transaction suitable for injection.
     */
    @Provides
    @Singleton
    fun provideTransactionRunner(
        database: JetcasterDatabase
    ): TransactionRunner = database.transactionRunnerDao()

    /**
     * [Provides] a [Singleton] instance of [SyndFeedInput] for parsing RSS/Atom feeds. We just
     * return a new [SyndFeedInput] instance to our caller. It is injected by Hilt to the constructor
     * of [PodcastsFetcher].
     *
     * @return a [Singleton] instance of [SyndFeedInput] for parsing RSS/Atom feeds suitable for
     * injection.
     */
    @Provides
    @Singleton
    fun provideSyndFeedInput(): SyndFeedInput = SyndFeedInput()

    /**
     * [Provides] a [Singleton] instance of [CoroutineDispatcher] for I/O-bound operations. We just
     * return [Dispatchers.IO] to our caller. It is injected by Hilt to the constructor of
     * [PodcastsFetcher].
     *
     * @return a [Singleton] instance of [CoroutineDispatcher] for I/O-bound operations suitable
     * for injection.
     */
    @Provides
    @Dispatcher(JetcasterDispatchers.IO)
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * [Provides] a [Singleton] instance of [CoroutineDispatcher] for UI-related tasks. We just
     * return [Dispatchers.Main] to our caller. It is injected by Hilt to the constructor of
     * [PodcastsRepository] and to the method `DomainDiModule.provideEpisodePlayer` (which [Provides]
     * an instance of `EpisodePlayer`) See `com/example/jetcaster/core/di/DomainDiModule.kt` and
     * `com/example/jetcaster/core/player/EpisodePlayer.kt`.
     *
     * @return a [Singleton] instance of [CoroutineDispatcher] for UI-related tasks suitable for
     * injection.
     */
    @Provides
    @Dispatcher(JetcasterDispatchers.Main)
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    /**
     * [Provides] a [Singleton] instance of [EpisodeStore] to manage podcast episodes.
     */
    @Provides
    @Singleton
    fun provideEpisodeStore(
        episodeDao: EpisodesDao
    ): EpisodeStore = LocalEpisodeStore(episodeDao)

    /**
     * Provides an instance of [PodcastStore] to manage podcast information.
     */
    @Provides
    @Singleton
    fun providePodcastStore(
        podcastDao: PodcastsDao,
        podcastFollowedEntryDao: PodcastFollowedEntryDao,
        transactionRunner: TransactionRunner,
    ): PodcastStore = LocalPodcastStore(
        podcastDao = podcastDao,
        podcastFollowedEntryDao = podcastFollowedEntryDao,
        transactionRunner = transactionRunner
    )

    /**
     * Provides an instance of [CategoryStore] to manage podcast categories.
     */
    @Provides
    @Singleton
    fun provideCategoryStore(
        categoriesDao: CategoriesDao,
        podcastCategoryEntryDao: PodcastCategoryEntryDao,
        podcastDao: PodcastsDao,
        episodeDao: EpisodesDao,
    ): CategoryStore = LocalCategoryStore(
        episodesDao = episodeDao,
        podcastsDao = podcastDao,
        categoriesDao = categoriesDao,
        categoryEntryDao = podcastCategoryEntryDao,
    )
}
