package com.elfen.redfun.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.domain.models.Profile
import com.elfen.redfun.ui.composables.shortenNumber
import com.elfen.redfun.ui.screens.sessions.SessionRoute
import com.elfen.redfun.ui.theme.AppTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigate: (Any) -> Unit = {},
) {
    val profile by viewModel.profile.collectAsState()

    ProfileScreen(
        profile = profile,
        onNavigate = onNavigate,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    profile: Profile? = null,
    onNavigate: (Any) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile")
                },
                actions = {
                    IconButton(onClick = {
                        onNavigate(SessionRoute)
                    }) {
                        Icon(painterResource(R.drawable.outline_swap_horiz_24), "Change Account")
                    }
                }
            )
        }
    ) {
        if (profile == null) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-48).dp)
                ) {
                    if (profile.banner != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        ){
                            AsyncImage(
                                model = profile.banner,
                                contentDescription = "Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (LocalInspectionMode.current) Color.LightGray else Color.Transparent)
                                    .aspectRatio(21f / 9f),
                            )
                            val colorStops = arrayOf(
                                0.5f to Color.Black.copy(alpha = 0f),
                                1f to Color.Black.copy(alpha = 0.5f),
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Brush.verticalGradient(colorStops = colorStops))
                            )
                        }
                    }
                    if (profile.icon != null) {
                        AsyncImage(
                            model = profile.icon,
                            contentDescription = "Banner",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (LocalInspectionMode.current) Color.LightGray else Color.Transparent)
                                .size(96.dp)
                                .border(
                                    6.dp, MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(24.dp)
                                )
                        )
                    }
                }
                Column {
                    Text(
                        text = if (profile.fullName.isNullOrEmpty()) "u/${profile.username}" else profile.fullName,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    if (!profile.fullName.isNullOrEmpty()) {
                        Text(
                            text = "u/${profile.username}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                "${
                                    profile.linkKarma.toLong().shortenNumber()
                                } Posts Karma, ${
                                    profile.commentKarma.toLong().shortenNumber()
                                } Comments Karma"
                            )
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(profile.totalKarma.toLong().shortenNumber())
                            }
                            append(" Karma")
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

val dummyProfile = Profile(
    id = "33o2ukxg",
    username = "elfennani",
    fullName = "Nizar Elfennani",
    icon = "https://i.redd.it/snoovatar/avatars/0df16d3d-02fa-481a-b32e-ec2cdbfc4393-headshot.png",
    banner = "https://mir-s3-cdn-cf.behance.net/project_modules/fs/bfc722134035093.61cc580421f48.png",
    commentKarma = 16819,
    linkKarma = 7703,
    totalKarma = 24522,
    isMod = true
)

@Preview
@Composable
private fun ProfileScreenPreview() {
    AppTheme {
        val profile = remember { mutableStateOf(dummyProfile) }
        ProfileScreen(
            profile = profile.value,
            onNavigate = {}
        )
    }
}