/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Details view model for managing item detail data and navigation
 */

package com.iiwa.home.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle
) : com.iiwa.viewmodels.BaseViewModel<DetailsUiState>(context) {

    private val _uiState = MutableStateFlow(DetailsUiState())
    override val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    val itemId: Int = savedStateHandle["itemId"] ?: -1

    init {
        _uiState.value = _uiState.value.copy(itemId = itemId)
    }
}

data class DetailsUiState(
    val itemId: Int = -1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
