package com.elfen.redfun.presentation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.data.rememberSettings
import com.elfen.redfun.domain.model.Post
import com.elfen.redfun.presentation.components.ui.AppBottomSheet
import com.elfen.redfun.presentation.theme.AppTheme
import com.elfen.redfun.presentation.utils.isWifiNetwork
import com.elfen.redfun.presentation.utils.rememberExoPlayer
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@SuppressLint("DefaultLocale")
fun Long.shortenNumber(): String {
    return when {
        this >= 1_000_000_000 -> String.format("%.1fB", this / 1_000_000_000.0)
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> this.toString()
    }.replace(".0", "") // Remove trailing ".0" if not needed
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    title: String,
    body: String?,
    subreddit: String,
    icon: String?,
    author: String,
    score: Int,
    comments: Int,
    created: Instant,
    onNavigateSubreddit: () -> Unit = {},
    onNavigateUserProfile: () -> Unit = {},
    flairs: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    var navigateToSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val onCloseSheet: (() -> Unit) -> Unit = { callback ->
        coroutineScope.launch {
            sheetState.hide()
            callback()
        }.invokeOnCompletion {
            navigateToSheetOpen = false
        }
    }

    if (navigateToSheetOpen)
        AppBottomSheet(
            sheetState = sheetState,
            title = {
                Text("Navigate To")
            },
            onDismissRequest = {
                navigateToSheetOpen = false
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable {
                            onCloseSheet {
                                onNavigateSubreddit()
                            }
                        }
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        )
                    }


                    Column {
                        Text(
                            text = "Subreddit",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "r/${subreddit}")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable {
                            onCloseSheet {
                                onNavigateUserProfile()
                            }
                        }
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person2, "User Profile")
                    }


                    Column {
                        Text(
                            text = "User Profile",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "u/${author}")
                    }
                }
            }
        }


    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = {}
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, "Upvote")
                }
                Text(
                    text = score.toLong().shortenNumber(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = {}
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, "Downvote")
                }
            }

            Spacer(Modifier.height(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = "Comments",
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = comments.toLong().shortenNumber(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                }

                Column() {
                    Text(
                        "r/$subreddit • ${formatDistanceToNowStrict(created)}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight(600))
                    flairs()
                }
            }

            if (!body.isNullOrBlank())
                Text(
                    text = body,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(0.75f)
                )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: Post,
    onNavigateSubreddit: () -> Unit,
    onNavigateUserProfile: () -> Unit,
    onNavigateToFlair: (String) -> Unit,
    autoPlay: Boolean = false,
    exoPlayer: ExoPlayer? = null,
    shape: Shape = MaterialTheme.shapes.medium
) {
    val context = LocalContext.current

    PostCard(
        modifier = modifier,
        title = post.title,
        body = post.body,
        subreddit = post.subreddit,
        icon = post.subredditIcon,
        author = post.author,
        created = post.created,
        comments = post.numComments,
        score = post.score,
        onNavigateSubreddit = onNavigateSubreddit,
        onNavigateUserProfile = onNavigateUserProfile,
        flairs = {
            if (post.nsfw || post.flair != null) {
                PostFlairs(
                    isNSFW = post.nsfw,
                    flair = post.flair,
                    navigateFlair = onNavigateToFlair
                )
            }
        }
    ){
        if (post.video != null) {
            var videoEnabled by rememberSaveable { mutableStateOf(autoPlay) }
            if (!videoEnabled) {
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat())
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { videoEnabled = true },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = post.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape)
                            .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat()),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    }
                }
            } else {
                val settings by rememberSettings()
                val player = exoPlayer ?: rememberExoPlayer(post.video.source)

                LaunchedEffect(settings) {
                    if (settings != null) {
                        val isWifi = isWifiNetwork(context)
                        val maxResolution =
                            if (isWifi) settings!!.maxWifiResolution else settings!!.maxMobileResolution
                        player.trackSelectionParameters = player.trackSelectionParameters
                            .buildUpon()
                            .setMaxVideoSize(maxResolution, maxResolution)
                            .build()
                    }
                }

                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat()),
                    exoPlayer = player
                )
            }
        }

        if (!post.images.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape),
                contentAlignment = Alignment.BottomStart
            ) {
                if (post.images.size == 1) {
                    val image = post.images.first()
                    AsyncImage(
                        model = image.source,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape)
                            .background(MaterialTheme.colorScheme.surface, shape)
                            .fillMaxWidth()
                            .aspectRatio(image.width.toFloat() / image.height.toFloat())
                    )
                } else {
                    val pagerState = rememberPagerState(pageCount = {
                        post.images.size
                    })
                    Box {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.clip(shape)
                        ) { page ->
                            val image = post.images[page]
                            AsyncImage(
                                model = image.source,
                                contentDescription = null,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .fillMaxWidth()
                                    .aspectRatio(image.width.toFloat() / image.height.toFloat())
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(8.dp, 8.dp)
                                .background(Color.Black.copy(alpha = 0.33f), CircleShape)
                                .padding(4.dp)
                        ) {
                            Icon(
                                painterResource(
                                    R.drawable.baseline_photo_library_24
                                ),
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: Post,
    showSubreddit: Boolean = true,
    truncate: Boolean = true,
    onNavigateSubreddit: () -> Unit,
    onNavigateUserProfile: () -> Unit,
    navigateToFlair: (String) -> Unit = {},
    exoPlayer: ExoPlayer? = null,
    autoPlay: Boolean = false
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(12.dp)
    var navigateToSheetOpen by remember { mutableStateOf(false) }


    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(CircleShape)
                .clickable { navigateToSheetOpen = true }
        ) {
            AsyncImage(
                model = post.subredditIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start
            ) {
                if (showSubreddit) {
                    Text(
                        "r/${post.subreddit}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Text(
                        "u/${post.author} · ${formatDistanceToNowStrict(post.created)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }
        Text(
            post.title,
            overflow = TextOverflow.Ellipsis,
            maxLines = if (truncate) 2 else Int.MAX_VALUE,
            style = MaterialTheme.typography.titleMedium,
        )
        if (post.nsfw || post.flair != null) {
            PostFlairs(
                isNSFW = post.nsfw,
                flair = post.flair,
                navigateFlair = navigateToFlair
            )
        }
        if (!post.body.isNullOrEmpty()) {
            if (truncate)
                Text(
                    post.body,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            else {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                    MarkdownRenderer(content = post.body)
                }
            }
        }
        if (post.video != null) {
            var videoEnabled by rememberSaveable { mutableStateOf(autoPlay) }
            if (!videoEnabled) {
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat())
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { videoEnabled = true },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = post.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape)
                            .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat()),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    }
                }
            } else {
                val settings by rememberSettings()
                val player = exoPlayer ?: rememberExoPlayer(post.video.source)

                LaunchedEffect(settings) {
                    if (settings != null) {
                        val isWifi = isWifiNetwork(context)
                        val maxResolution =
                            if (isWifi) settings!!.maxWifiResolution else settings!!.maxMobileResolution
                        player.trackSelectionParameters = player.trackSelectionParameters
                            .buildUpon()
                            .setMaxVideoSize(maxResolution, maxResolution)
                            .build()
                    }
                }

                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(post.video.width.toFloat() / post.video.height.toFloat()),
                    exoPlayer = player
                )
            }
        }

        if (!post.images.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape),
                contentAlignment = Alignment.BottomStart
            ) {
                if (post.images.size == 1) {
                    val image = post.images.first()
                    AsyncImage(
                        model = image.source,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape)
                            .fillMaxWidth()
                            .aspectRatio(image.width.toFloat() / image.height.toFloat())
                    )
                } else {
                    val pagerState = rememberPagerState(pageCount = {
                        post.images.size
                    })
                    Box {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.clip(shape)
                        ) { page ->
                            val image = post.images[page]
                            AsyncImage(
                                model = image.source,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(image.width.toFloat() / image.height.toFloat())
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(8.dp, 8.dp)
                                .background(Color.Black.copy(alpha = 0.33f), CircleShape)
                                .padding(4.dp)
                        ) {
                            Icon(
                                painterResource(
                                    R.drawable.baseline_photo_library_24
                                ),
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    painterResource(R.drawable.baseline_arrow_upward_24),
                    "upvote",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    post.score.toLong().shortenNumber(),
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.Comment,
                    "upvote",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    post.numComments.toLong().shortenNumber(),
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun PostCardPrev() {
    AppTheme {
        Scaffold() {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                PostCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "This is a placeholder reddit post title",
                    body = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                    subreddit = "test",
                    icon = null,
                    score = 1000,
                    comments = 100,
                    author = "elfennani",
                    created = Instant.fromEpochSeconds(Clock.System.now().epochSeconds - 60 * 60 * 24 * 30),
                    flairs = {
                        PostFlairs(
                            isNSFW = true,
                            flair = "Hello World",
                            navigateFlair = {}
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9)
                            .background(Color.LightGray, MaterialTheme.shapes.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Placeholder", color = Color.Gray)
                    }
                }
            }
        }
    }
}