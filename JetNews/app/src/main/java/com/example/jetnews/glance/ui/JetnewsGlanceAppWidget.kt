/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetnews.glance.ui

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.R
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.successOr
import com.example.jetnews.glance.ui.theme.JetnewsGlanceColorScheme
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostsFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Glance app widget that displays the latest Jetnews posts.
 *
 * When the widget is added to the home screen, it will be able to display a list of posts
 * directly without having to open the app. The user can also toggle bookmarks from the widget.
 *
 * The widget is responsive to the size it is allocated, displaying a single column of posts
 * in smaller sizes and a two-column layout in larger sizes.
 */
class JetnewsGlanceAppWidget : GlanceAppWidget() {
    /**
     * Define a fixed size for this widget.
     */
    override val sizeMode: SizeMode = SizeMode.Exact

    /**
     * Called by the App Widget Host to build the Glance-based App Widget. This function is
     * responsible for loading the necessary data and providing the composable content of
     * the widget.
     *
     * It fetches the initial posts feed and bookmarked posts from the [PostsRepository]. This data
     * is then observed as a [State] within the `provideContent` block, allowing the widget to
     * automatically update its UI when the underlying data changes.
     *
     * The UI is composed by the [JetnewsContent] composable, which is wrapped in a [GlanceTheme]
     * to provide a consistent color scheme. A custom color scheme is used for SDK versions older
     * than S, where dynamic colors are not available.
     *
     * @param context The application context.
     * @param id The unique identifier for this widget instance.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val application = context.applicationContext as JetnewsApplication
        val postsRepository: PostsRepository = application.container.postsRepository

        // Load data needed to render the composable.
        // The widget is configured to refresh periodically using the "android:updatePeriodMillis"
        // configuration, and during each refresh, the data is loaded here.
        // The repository can internally return cached results here if it already has fresh data.
        val initialPostsFeed: PostsFeed? = withContext(context = Dispatchers.IO) {
            postsRepository.getPostsFeed().successOr(fallback = null)
        }
        val initialBookmarks: Set<String> = withContext(context = Dispatchers.IO) {
            postsRepository.observeFavorites().first()
        }

        provideContent {
            val scope: CoroutineScope = rememberCoroutineScope()
            val bookmarks: Set<String> by postsRepository.observeFavorites().collectAsState(initialBookmarks)
            val postsFeed: PostsFeed? by postsRepository.observePostsFeed().collectAsState(initialPostsFeed)
            val recommendedTopPosts: List<Post> =
                postsFeed?.let { listOf(it.highlightedPost) + it.recommendedPosts } ?: emptyList()

            // Provide a custom color scheme if the SDK version doesn't support dynamic colors.
            GlanceTheme(
                colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    GlanceTheme.colors
                } else {
                    JetnewsGlanceColorScheme.colors
                }
            ) {
                JetnewsContent(
                    posts = recommendedTopPosts,
                    bookmarks = bookmarks,
                    onToggleBookmark = { scope.launch { postsRepository.toggleFavorite(it) } }
                )
            }
        }
    }

    /**
     * The content of the Jetnews App Widget.
     *
     * @param posts The list of posts to display.
     * @param bookmarks A set of bookmarked post IDs.
     * @param onToggleBookmark A lambda to be invoked when the user toggles a bookmark.
     */
    @Composable
    private fun JetnewsContent(
        posts: List<Post>,
        bookmarks: Set<String>?,
        onToggleBookmark: (String) -> Unit
    ) {
        Column(
            modifier = GlanceModifier
                .background(colorProvider = GlanceTheme.colors.surface)
                .cornerRadius(radius = 24.dp)
        ) {
            Header(modifier = GlanceModifier.fillMaxWidth())
            // Set key for each size so that the onToggleBookmark lambda is called only once for the
            // active size.
            key(LocalSize.current) {
                Body(
                    modifier = GlanceModifier.fillMaxWidth(),
                    posts = posts,
                    bookmarks = bookmarks ?: setOf(),
                    onToggleBookmark = onToggleBookmark
                )
            }
        }
    }

    /**
     * A composable function that displays the header of the Jetnews app widget.
     *
     * This header contains the Jetnews logo and wordmark, centered horizontally. It provides a
     * consistent branding element at the top of the widget's layout.
     *
     * @param modifier The [GlanceModifier] to be applied to the header layout.
     */
    @Composable
    fun Header(modifier: GlanceModifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(horizontal = 10.dp, vertical = 20.dp)
        ) {
            val context = LocalContext.current
            Image(
                provider = ImageProvider(resId = R.drawable.ic_jetnews_logo),
                colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.primary),
                contentDescription = null,
                modifier = GlanceModifier.size(size = 24.dp)
            )
            Spacer(modifier = GlanceModifier.width(width = 8.dp))
            Image(
                contentDescription = context.getString(R.string.app_name),
                colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.onSurfaceVariant),
                provider = ImageProvider(resId = R.drawable.ic_jetnews_wordmark)
            )
        }
    }

    /**
     * The main content of the widget, which displays a list of posts.
     *
     * This composable uses a [LazyColumn] to efficiently display a potentially long list of posts.
     * The layout of each post item is determined by the available size, switching between a more
     * detailed view for larger areas and a more compact one for smaller areas.
     *
     * @param modifier The modifier to be applied to the lazy column.
     * @param posts The list of [Post]s to display.
     * @param bookmarks A set of bookmarked post IDs, used to show the correct bookmark state for
     * each post.
     * @param onToggleBookmark A lambda function to be invoked when the user taps the bookmark icon
     * on a post.
     */
    @Composable
    fun Body(
        modifier: GlanceModifier,
        posts: List<Post>,
        bookmarks: Set<String>,
        onToggleBookmark: (String) -> Unit,
    ) {
        val postLayout: PostLayout = LocalSize.current.toPostLayout()
        LazyColumn(modifier = modifier.background(colorProvider = GlanceTheme.colors.background)) {
            itemsIndexed(posts) { index: Int, post: Post ->
                Column(modifier = GlanceModifier.padding(horizontal = 14.dp)) {
                    Post(
                        post = post,
                        bookmarks = bookmarks,
                        onToggleBookmark = onToggleBookmark,
                        modifier = GlanceModifier.fillMaxWidth().padding(all = 15.dp),
                        postLayout = postLayout,
                    )
                    if (index < posts.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }
}
