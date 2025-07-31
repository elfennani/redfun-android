package com.elfen.redfun.ui.composables

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elfen.redfun.R
import com.elfen.redfun.domain.models.Comment
import kotlin.time.ExperimentalTime

@SuppressLint("NewApi")
@OptIn(ExperimentalTime::class)
@Composable
fun CommentCard(comment: Comment) {
    if (comment is Comment.Body) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(start = (comment.depth * 8).dp)
        ) {
            if(comment.depth > 0)
                VerticalDivider()

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "u/${comment.author} • ${formatDistanceToNowStrict(comment.created)}",
                    style = MaterialTheme.typography.labelSmall
                )

                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
                    MarkdownRenderer(content = comment.body)
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
                        Text(shortenNumber(comment.score.toLong()))
                    }
                }
            }
        }
    }
}