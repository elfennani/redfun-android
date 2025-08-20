package com.elfen.redfun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.presentation.screens.Navigation
import com.elfen.redfun.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val ANIM_DURATION_MILLIS = 150

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        runBlocking{
            withContext(Dispatchers.IO){
                dataStore.edit {
                    it[booleanPreferencesKey("shouldMute")] = true
                }
            }
        }

        setContent {
            AppTheme {
                Navigation()
            }
        }
    }
}
