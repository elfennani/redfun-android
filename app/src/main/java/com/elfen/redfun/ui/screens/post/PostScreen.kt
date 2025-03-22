package com.elfen.redfun.ui.screens.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elfen.redfun.ui.composables.PostCompact
import kotlinx.serialization.Serializable

@Serializable
data class PostRoute(val id: String)

@Composable
fun PostScreen(navController: NavController, viewModel: PostViewModel = hiltViewModel()) {
    val post = viewModel.post.collectAsState()
    val comments = viewModel.comments.collectAsState()

    Scaffold { paddingValues ->
        if(post.value != null)
            LazyColumn(contentPadding = paddingValues) {
                item{
                    PostCompact(post = post.value!!)
                }

                items(comments.value) {
                    Text("Comment: ${it.body ?: it.id}")
                }
            }
        else {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.padding(paddingValues).align(alignment = androidx.compose.ui.Alignment.CenterHorizontally))
                }
            }
        }
    }
}