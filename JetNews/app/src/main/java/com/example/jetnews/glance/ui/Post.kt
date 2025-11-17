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
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.example.jetnews.JetnewsApplication.Companion.JETNEWS_APP_URI
import com.example.jetnews.R
import com.example.jetnews.glance.ui.theme.JetnewsGlanceTextStyles
import com.example.jetnews.model.Post
import com.example.jetnews.ui.MainActivity

/**
 * Describes the different layouts that a post can have in the glance UI,
 * depending on the available size.
 */
enum class PostLayout {
    /**
     * A horizontal layout for medium-sized widgets, displaying a post with a smaller thumbnail.
     */
    HORIZONTAL_SMALL,
    /**
     * A horizontally-oriented layout for displaying a post. This layout is used for wider widgets
     * and shows a larger, cropped version of the post's image on the left, with the title,
     * metadata, and bookmark button to its right.
     */
    HORIZONTAL_LARGE,
    /**
     * A layout where the post image is displayed above the post's title and metadata.
     */
    VERTICAL
}

/**
 * Maps a [DpSize] to a [PostLayout] based on the available width.
 *
 * This is used to determine the best layout for a post in the widget, depending on the widget's
 * current size.
 */
fun DpSize.toPostLayout(): PostLayout {
    return when {
        (this.width <= 300.dp) -> PostLayout.VERTICAL
        (this.width <= 700.dp) -> PostLayout.HORIZONTAL_SMALL
        else -> PostLayout.HORIZONTAL_LARGE
    }
}

/**
 * A helper function to format the author and read time string.
 *
 * @param author the author's name.
 * @param readTimeMinutes the estimated read time in minutes.
 * @return a formatted string combining the author's name and the read time.
 */
private fun Context.authorReadTimeString(author: String, readTimeMinutes: Int) =
    getString(R.string.home_post_min_read)
        .format(author, readTimeMinutes)

/**
 * Creates an [Action] that opens the article details when the widget is clicked.
 *
 * @param context the context to use to create the intent.
 * @param post the post to open.
 * @return an [Action] that launches the [MainActivity] to display the post.
 */
private fun openPostDetails(context: Context, post: Post): Action {
    // actionStartActivity is the preferred way to start activities.
    return actionStartActivity(
        Intent(
            /* action = */ Intent.ACTION_VIEW,
            /* uri = */ "$JETNEWS_APP_URI/home?postId=${post.id}".toUri(),
            /* packageContext = */ context,
            /* cls = */ MainActivity::class.java
        )
    )
}

/**
 * A composable function that displays a single post item in a Glance widget, adapting its
 * layout based on the provided [postLayout].
 *
 * This function acts as a router, delegating the rendering to more specific composables
 * like [HorizontalPost] or [VerticalPost] depending on the layout strategy.
 *
 * @param post The [Post] data object to be displayed.
 * @param bookmarks A set of strings representing the IDs of bookmarked posts. This is used
 * to determine the state of the bookmark icon.
 * @param onToggleBookmark A lambda function that is invoked when the user clicks the bookmark
 * icon. It passes the ID of the post.
 * @param modifier A [GlanceModifier] to be applied to the root of the composable.
 * @param postLayout The [PostLayout] enum that dictates which layout (e.g., vertical or
 * horizontal) should be used to render the post.
 */
@Composable
fun Post(
    post: Post,
    bookmarks: Set<String>,
    onToggleBookmark: (String) -> Unit,
    modifier: GlanceModifier,
    postLayout: PostLayout,
) {
    when (postLayout) {
        PostLayout.HORIZONTAL_SMALL -> HorizontalPost(
            post = post,
            bookmarks = bookmarks,
            onToggleBookmark = onToggleBookmark,
            modifier = modifier,
        )

        PostLayout.HORIZONTAL_LARGE -> HorizontalPost(
            post = post,
            bookmarks = bookmarks,
            onToggleBookmark = onToggleBookmark,
            modifier = modifier,
            showImageThumbnail = false
        )

        PostLayout.VERTICAL -> VerticalPost(
            post = post,
            bookmarks = bookmarks,
            onToggleBookmark = onToggleBookmark,
            modifier = modifier,
        )
    }
}

/**
 * A composable that displays a post in a horizontal layout.
 *
 * This layout is suitable for medium to large-sized widgets. It displays the post's image on the
 * left and the title, metadata, and bookmark button to the right. The entire layout is clickable
 * and opens the post details.
 *
 * @param post The [Post] to be displayed.
 * @param bookmarks A set of bookmarked post IDs, used to determine the state of the bookmark icon.
 * @param onToggleBookmark A lambda function to be invoked when the bookmark button is clicked. It
 * receives the post ID as an argument.
 * @param modifier A [GlanceModifier] to be applied to the root [Row] composable.
 * @param showImageThumbnail A boolean that determines which version of the post's image to show.
 * If `true`, a smaller, fitted thumbnail is used. If `false`, a larger, cropped image is displayed.
 */
@Composable
fun HorizontalPost(
    post: Post,
    bookmarks: Set<String>,
    onToggleBookmark: (String) -> Unit,
    modifier: GlanceModifier,
    showImageThumbnail: Boolean = true
) {
    val context: Context = LocalContext.current
    Row(
        verticalAlignment = Alignment.Vertical.CenterVertically,
        modifier = modifier.clickable(onClick = openPostDetails(context = context, post = post))
    ) {
        if (showImageThumbnail) {
            PostImage(
                imageId = post.imageThumbId,
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier.size(size = 80.dp)
            )
        } else {
            PostImage(
                imageId = post.imageId,
                contentScale = ContentScale.Crop,
                modifier = GlanceModifier.width(width = 250.dp)
            )
        }
        PostDescription(
            title = post.title,
            metadata = context.authorReadTimeString(
                author = post.metadata.author.name,
                readTimeMinutes = post.metadata.readTimeMinutes
            ),
            modifier = GlanceModifier.defaultWeight().padding(horizontal = 20.dp)
        )
        BookmarkButton(
            id = post.id,
            isBookmarked = bookmarks.contains(element = post.id),
            onToggleBookmark = onToggleBookmark
        )
    }
}

/**
 * A composable that displays a post in a vertical layout.
 *
 * This layout is ideal for narrow widgets where the width is constrained. It shows a full-width
 * image at the top, followed by the post's title, metadata, and a bookmark button below.
 *
 * @param post The post to display.
 * @param bookmarks A set of bookmarked post IDs, used to determine the state of the bookmark button.
 * @param onToggleBookmark A lambda function to be invoked when the bookmark button is clicked. It
 * receives the post's ID.
 * @param modifier A [GlanceModifier] to be applied to the root Column of the composable.
 */
@Composable
fun VerticalPost(
    post: Post,
    bookmarks: Set<String>,
    onToggleBookmark: (String) -> Unit,
    modifier: GlanceModifier,
) {
    val context: Context = LocalContext.current
    Column(
        verticalAlignment = Alignment.Vertical.CenterVertically,
        modifier = modifier.clickable(onClick = openPostDetails(context = context, post = post))
    ) {
        PostImage(imageId = post.imageId, modifier = GlanceModifier.fillMaxWidth())
        Spacer(modifier = GlanceModifier.height(height = 4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PostDescription(
                title = post.title,
                metadata = context.authorReadTimeString(
                    author = post.metadata.author.name,
                    readTimeMinutes = post.metadata.readTimeMinutes
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Spacer(modifier = GlanceModifier.width(width = 10.dp))
            BookmarkButton(
                id = post.id,
                isBookmarked = bookmarks.contains(element = post.id),
                onToggleBookmark = onToggleBookmark
            )
        }
    }
}

/**
 * A composable function that displays a bookmark icon button. The icon changes state
 * (filled or outlined) based on whether the post is bookmarked.
 *
 * @param id The unique identifier of the post associated with this bookmark button.
 * @param isBookmarked A boolean indicating if the post is currently bookmarked. This determines
 * which icon is displayed.
 * @param onToggleBookmark A lambda function that is invoked when the button is clicked. It
 * passes the post's [id] to handle the bookmarking logic.
 */
@Composable
fun BookmarkButton(id: String, isBookmarked: Boolean, onToggleBookmark: (String) -> Unit) {
    Image(
        provider = ImageProvider(
            if (isBookmarked) {
                R.drawable.ic_jetnews_bookmark_filled
            } else {
                R.drawable.ic_jetnews_bookmark
            }
        ),
        colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.primary),
        contentDescription = "${if (isBookmarked) R.string.unbookmark else R.string.bookmark}",
        modifier = GlanceModifier.clickable { onToggleBookmark(id) }
    )
}

/**
 * A composable that displays an image for a post, with a default corner radius.
 *
 * This is a simple wrapper around the Glance [Image] composable that applies a corner radius
 * and loads the image from a drawable resource.
 *
 * @param imageId The resource ID of the drawable to be displayed.
 * @param contentScale The scaling strategy to use for the image. Defaults to [ContentScale.Crop].
 * @param modifier A [GlanceModifier] to be applied to the image. Defaults to an empty modifier.
 */
@Composable
fun PostImage(
    imageId: Int,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: GlanceModifier = GlanceModifier
) {
    Image(
        provider = ImageProvider(resId = imageId),
        contentScale = contentScale,
        contentDescription = null,
        modifier = modifier.cornerRadius(radius = 5.dp)
    )
}

/**
 * A composable that displays the title and metadata of a post in a vertical column.
 *
 * @param title The main title of the post. It is displayed with a larger font and can span up
 * to three lines.
 * @param metadata A string containing secondary information about the post, such as the author
 * and read time. It is displayed below the title with a smaller font.
 * @param modifier A [GlanceModifier] to be applied to the root [Column] of the composable.
 */
@Composable
fun PostDescription(title: String, metadata: String, modifier: GlanceModifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            maxLines = 3,
            style = JetnewsGlanceTextStyles.bodyLarge
                .copy(color = GlanceTheme.colors.onBackground)
        )
        Spacer(modifier = GlanceModifier.height(height = 4.dp))
        Text(
            text = metadata,
            style = JetnewsGlanceTextStyles.bodySmall
                .copy(color = GlanceTheme.colors.onBackground)
        )
    }
}
