package com.elfen.redfun.presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.elfen.redfun.BuildConfig
import androidx.core.net.toUri

@Serializable
data object LoginRoute

fun initiateAuth(context: Context) {
    val clientId = BuildConfig.clientId
    val redirectUri = BuildConfig.redirectUri


    if (clientId.isEmpty() || redirectUri.isEmpty()) {
        throw IllegalArgumentException("CLIENT_ID and/or REDIRECT_URI were not provided in .env")
    }

    val baseUrl = "https://www.reddit.com/api/v1/authorize"
    val scope = listOf(
        "identity", "edit", "flair", "history", "modconfig", "modflair",
        "modlog", "modposts", "modwiki", "mysubreddits", "privatemessages",
        "read", "report", "save", "submit", "subscribe", "vote",
        "wikiedit", "wikiread"
    ).joinToString(",")

    val uri = baseUrl.toUri().buildUpon()
        .appendQueryParameter("client_id", clientId)
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("state", "VERY_RANDOM_STATE")
        .appendQueryParameter("redirect_uri", redirectUri)
        .appendQueryParameter("duration", "permanent")
        .appendQueryParameter("scope", scope)
        .build()

    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to RedFun! :)", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(text = "Your simple to use Reddit client", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
            Button(modifier = Modifier.fillMaxWidth(),onClick = {
                initiateAuth(context)
            }) {
                Text(text = "Login with Reddit")
            }
        }
    }
}