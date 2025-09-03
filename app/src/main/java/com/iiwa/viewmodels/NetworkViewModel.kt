/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Network view model for monitoring connectivity and network state
 */

package com.iiwa.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.iiwa.R
import com.iiwa.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val networkRepository: NetworkRepository
) : BaseViewModel<NetworkUiState>(context) {

    private val _uiState = MutableStateFlow(NetworkUiState())
    override val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()

    init {
        checkNetworkStatus()
    }

    fun checkNetworkStatus() {
        viewModelScope.launch {
            val status = networkRepository.checkNetworkStatus()
            val isConnected = networkRepository.isNetworkAvailable()
            
            _uiState.value = _uiState.value.copy(
                networkStatus = status,
                isConnected = isConnected,
                isLoading = false
            )
        }
    }

    fun getNetworkInfo(): String {
        return buildString {
            appendLine(context.getString(R.string.network_status_prefix, networkRepository.checkNetworkStatus()))
            //appendLine(context.getString(R.string.is_connected_prefix, networkRepository.isNetworkAvailable()))
            appendLine(context.getString(R.string.network_info_prefix, networkRepository.getNetworkInfo()))
        }
    }
}

data class NetworkUiState(
    val networkStatus: String = "",
    val isConnected: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
