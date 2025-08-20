package com.elfen.redfun.presentation.screens.profile

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.data.ProfileService
import com.elfen.redfun.data.local.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileService: ProfileService,
    @ApplicationContext context: Context,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val profile = context.dataStore.data.flatMapMerge {
        profileService.getProfileByUserID(
            it[stringPreferencesKey("session_id")]!!
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            profileService.fetchActiveProfile()
        }
    }
}