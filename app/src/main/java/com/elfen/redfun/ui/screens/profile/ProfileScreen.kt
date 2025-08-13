package com.elfen.redfun.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ScaffoldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.elfen.redfun.R
import com.elfen.redfun.domain.models.Profile
import com.elfen.redfun.ui.composables.shortenNumber
import com.elfen.redfun.ui.screens.profile.comps.ActionButton
import com.elfen.redfun.ui.screens.profile.comps.InfoList
import com.elfen.redfun.ui.screens.profile.comps.LoadingScreen
import com.elfen.redfun.ui.screens.profile.comps.ProfileBanner
import com.elfen.redfun.ui.screens.profile.comps.ProfileIcon
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
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile")
                },
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { scaffoldPadding ->
        if (profile == null) {
            LoadingScreen(modifier = Modifier.padding(scaffoldPadding))
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(scaffoldPadding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .padding(top = 32.dp)                    ,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-48).dp)
                ) {
                    if (profile.banner != null) {
                        ProfileBanner(banner = profile.banner)
                    }
                    ProfileIcon(
                        modifier = Modifier.size(128.dp),
                        icon = profile.icon
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (profile.fullName.isNullOrEmpty()) "u/${profile.username}" else profile.fullName,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    if (!profile.fullName.isNullOrEmpty()) {
                        Text(
                            text = "u/${profile.username}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(0.dp))
                HorizontalDivider()

                InfoList(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item(title = "Karma", value = profile.totalKarma.toLong().shortenNumber())
                    item(title = "Posts", value = profile.linkKarma.toLong().shortenNumber())
                    item(title = "Comments", value = profile.commentKarma.toLong().shortenNumber())
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                ) {
                    ActionButton(
                        onClick = {},
                        label = "Posts",
                        icon = {
                            Icon(Icons.Default.ArtTrack, null)
                        }
                    )
                    ActionButton(
                        onClick = {},
                        label = "Comments",
                        icon = {
                            Icon(Icons.Default.CommentBank, null)
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = DividerDefaults.color.copy(alpha = 0.5f)
                    )
                    ActionButton(
                        onClick = {},
                        label = "Settings",
                        icon = {
                            Icon(Icons.Default.Settings, null)
                        }
                    )
                    ActionButton(
                        onClick = {
                            onNavigate(SessionRoute)
                        },
                        label = "Switch Account",
                        icon = {
                            Icon(painterResource(R.drawable.outline_swap_horiz_24), null)
                        },
                    )
                    ActionButton(
                        onClick = {},
                        label = "Logout",
                        icon = {
                            Icon(Icons.AutoMirrored.Default.Logout, null)
                        },
                        showChevron = false
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