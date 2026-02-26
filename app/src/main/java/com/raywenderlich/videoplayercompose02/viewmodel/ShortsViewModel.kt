package com.raywenderlich.videoplayercompose02.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.videoplayercompose02.repository.ShortsRepository
import com.raywenderlich.videoplayercompose02.state.ShortsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShortsViewModel(
    private val repository: ShortsRepository = ShortsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShortsUiState())
    val uiState: StateFlow<ShortsUiState> = _uiState.asStateFlow()

    init {
        loadShorts()
    }

    fun loadShorts() {
        viewModelScope.launch {
            repository.getShorts()
                .catch { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.message
                    )}
                }
                .collect { shortsList ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        shorts = shortsList,
                        error = null
                    )}
                }
        }
    }

    fun setInitialShortId(shortId: String?) {
        if (shortId != null) {
            val index = _uiState.value.shorts.indexOfFirst { it.id == shortId }
            if (index != -1) {
                _uiState.update { it.copy(initialPageIndex = index) }
            }
        }
    }
}