package com.elfen.redfun.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.redfun.domain.usecase.GetAutoCompleteResultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAutoCompleteResults: GetAutoCompleteResultsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            state.map { it.query }.distinctUntilChanged().debounce(300L).collect { query ->
                if (query.isNotEmpty()) {
                    _state.update { it.copy(isLoading = true) }
                    val result = getAutoCompleteResults(query)
                    _state.update {
                        it.copy(autoCompleteResult = result, isLoading = false)
                    }
                } else {
                    _state.value = state.value.copy(autoCompleteResult = null, isLoading = false)
                }
            }
        }
    }

    fun onEvent(event: SearchEvent){
        when(event){
            is SearchEvent.OnQueryChange -> {
                _state.value = state.value.copy(query = event.query)
            }
            is SearchEvent.ClearQuery -> {
                _state.value = state.value.copy(query = "", autoCompleteResult = null)
            }
        }
    }
}