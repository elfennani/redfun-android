package com.elfen.redfun.presentation.screens.sessions

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepo: SessionRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    val sessions =
        sessionRepo.sessions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun changeSession(sessionId: String) {
        viewModelScope.launch {
            sessionRepo.changeSession(sessionId)
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)


            // Required for API 34 and later
            // Ref: https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents
            mainIntent.setPackage(context.packageName)
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }

    }
}