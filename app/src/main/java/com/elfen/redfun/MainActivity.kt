package com.elfen.redfun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.elfen.redfun.ui.screens.Navigation
import com.elfen.redfun.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

const val ANIM_DURATION_MILLIS = 150

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Navigation()
            }
        }
    }
}
