@file:Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")

package com.elfen.redfun.ui.composables

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elfen.redfun.data.remote.models.Link
import java.time.Duration
import java.time.Instant
import kotlin.math.abs
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import java.net.URL


@RequiresApi(Build.VERSION_CODES.O)
fun formatDistanceToNowStrict(date: Instant): String {
    val now = Instant.now()
    val duration = Duration.between(date, now)
    val seconds = abs(duration.seconds)

    return when {
        seconds < 60 -> "$seconds second${if (seconds != 1L) "s" else ""}"
        seconds < 3600 -> {
            val minutes = seconds / 60
            "$minutes minute${if (minutes != 1L) "s" else ""}"
        }

        seconds < 86400 -> {
            val hours = seconds / 3600
            "$hours hour${if (hours != 1L) "s" else ""}"
        }

        seconds < 2592000 -> {
            val days = seconds / 86400
            "$days day${if (days != 1L) "s" else ""}"
        }

        seconds < 31536000 -> {
            val months = seconds / 2592000
            "$months month${if (months != 1L) "s" else ""}"
        }

        else -> {
            val years = seconds / 31536000
            "$years year${if (years != 1L) "s" else ""}"
        }
    }
}

@SuppressLint("DefaultLocale")
fun shortenNumber(value: Long): String {
    return when {
        value >= 1_000_000_000 -> String.format("%.1fB", value / 1_000_000_000.0)
        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000.0)
        value >= 1_000 -> String.format("%.1fK", value / 1_000.0)
        else -> value.toString()
    }.replace(".0", "") // Remove trailing ".0" if not needed
}

@Composable
fun PostCompact(modifier: Modifier = Modifier, post: Link) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Text(
                "r/${post.subreddit} • ${formatDistanceToNowStrict(Instant.ofEpochSecond(post.created))}",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        } else {
            Text(
                "r/${post.subreddit} • ${post.created}",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    post.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    lineHeight = 21.sp
                )
                if (post.selftext.isNotEmpty())
                    Text(post.selftext, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Justify, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.outline)
            }
            if (post.thumbnail != null && Patterns.WEB_URL.matcher(post.thumbnail).matches()) {
                AsyncImage(
                    model = post.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .width(96.dp)
                        .aspectRatio(4f / 3f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        val isLink = !post.isRedditMediaDomain &&
                post.urlOverriddenByDest?.isNotEmpty() === true &&
                post.mediaMetadata === null;
        if (isLink) {
            Row(
                modifier = Modifier
                    .border(1.dp, color = Color.LightGray, CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        if (post.urlOverriddenByDest.isNotEmpty() == true) {

                            try {
                                context.startActivity(
                                    android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        post.urlOverriddenByDest.toUri()
                                    )
                                )
                            } catch (e: Exception) {
                                Log.e("PostCompact", "Error opening link", e)
                            }
                        }
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = post.urlOverriddenByDest,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painterResource(id = R.drawable.baseline_link_24),
                    contentDescription = null,
                    tint = Color.Gray
                )

            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Badge {
                Icon(
                    painterResource(R.drawable.baseline_arrow_upward_24),
                    contentDescription = null,
                    Modifier.size(12.dp)
                )
                Text(shortenNumber(post.score.toLong()))
            }
            Badge {
                Icon(
                    painterResource(R.drawable.baseline_mode_comment_24),
                    contentDescription = null,
                    Modifier.size(12.dp)
                )
                Text(shortenNumber(post.numComments.toLong()))
            }
        }
    }
}

@Composable
fun Badge(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
            LocalTextStyle provides TextStyle(fontSize = 10.sp)
        ) {
            content()
        }
    }
}