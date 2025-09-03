/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Home view model for managing user data and notification permissions
 */

package com.iiwa.home.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.iiwa.R
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.pushnotification.FirebaseMessagingManager
import com.iiwa.pushnotification.NotificationPermissionHelper
import com.iiwa.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val userRepository: UserRepository,
    private val firebaseMessagingManager: FirebaseMessagingManager,
    private val notificationPermissionHelper: NotificationPermissionHelper
) : com.iiwa.viewmodels.BaseViewModel<HomeUiState>(context) {

    private val _uiState = MutableStateFlow(HomeUiState())
    override val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
        checkNotificationPermission()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getCurrentUserInfo().collect { userInfo ->
                _uiState.value = _uiState.value.copy(
                    username = userInfo?.username ?: "",
                    userId = userInfo?.id ?: -1,
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = userRepository.logout()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedOut = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // FCM related functions
    fun getFCMToken() {
        viewModelScope.launch {
            val token = firebaseMessagingManager.getToken()
            _uiState.value = _uiState.value.copy(fcmToken = token)
        }
    }
    
    fun subscribeToTopic(topic: String) {
        viewModelScope.launch {
            firebaseMessagingManager.subscribeToTopic(topic)
        }
    }
    
    fun unsubscribeFromTopic(topic: String) {
        viewModelScope.launch {
            firebaseMessagingManager.unsubscribeFromTopic(topic)
        }
    }
    
    // Notification permission functions
    private fun checkNotificationPermission() {
        val isGranted = notificationPermissionHelper.isNotificationPermissionGranted()
        _uiState.value = _uiState.value.copy(
            isNotificationPermissionGranted = isGranted,
            shouldShowNotificationPermissionDialog = !isGranted
        )
    }
    
    fun onNotificationPermissionResult(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(
            isNotificationPermissionGranted = isGranted,
            shouldShowNotificationPermissionDialog = false
        )
        
        if (isGranted) {
            // Subscribe to general topics or get FCM token when permission is granted
            subscribeToTopic(context.getString(R.string.topic_general))
            getFCMToken()
        }
    }
    
    fun dismissNotificationPermissionDialog() {
        _uiState.value = _uiState.value.copy(
            shouldShowNotificationPermissionDialog = false
        )
    }
}

data class HomeUiState(
    val username: String = "",
    val userId: Int = -1,
    val isLoading: Boolean = true,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null,
    val fcmToken: String? = null,
    val isNotificationPermissionGranted: Boolean = false,
    val shouldShowNotificationPermissionDialog: Boolean = false
)
