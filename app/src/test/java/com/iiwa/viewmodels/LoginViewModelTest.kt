/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Comprehensive test cases for LoginViewModel in authorization package
 */

package com.iiwa.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import com.iiwa.R
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.authorization.viewmodels.LoginViewModel
import com.iiwa.biometric.BiometricHelper
import com.iiwa.data.model.LoginResponse
import com.iiwa.utils.NetworkUtils
import com.iiwa.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

/**
 * Test class for LoginViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockBiometricHelper: BiometricHelper
    
    @Mock
    private lateinit var mockUserRepository: UserRepository
    
    @Mock
    private lateinit var mockNetworkUtils: NetworkUtils
    
    @Mock
    private lateinit var mockActivity: FragmentActivity
    
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        
        // Setup mock context with string resources
        setupMockContext()
        
        // Setup mock biometric helper
        setupMockBiometricHelper()
        
        // Setup mock network utils
        setupMockNetworkUtils()
        
        loginViewModel = LoginViewModel(
            mockContext,
            mockBiometricHelper,
            mockUserRepository,
            mockNetworkUtils
        )
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    private fun setupMockContext() {
        whenever(mockContext.getString(R.string.email_required)).thenReturn("Email is required")
        whenever(mockContext.getString(R.string.email_invalid_format)).thenReturn("Invalid email format")
        whenever(mockContext.getString(R.string.password_required)).thenReturn("Password is required")
        whenever(mockContext.getString(R.string.password_min_length)).thenReturn("Password must be at least 6 characters")
        whenever(mockContext.getString(R.string.biometric_type_auto)).thenReturn("auto")
        whenever(mockContext.getString(R.string.biometric_type_fingerprint)).thenReturn("fingerprint")
        whenever(mockContext.getString(R.string.biometric_type_face)).thenReturn("face")
        whenever(mockContext.getString(R.string.biometric_not_available)).thenReturn("Biometric authentication not available")
        whenever(mockContext.getString(R.string.biometric_auth_canceled)).thenReturn("Biometric authentication canceled")
        whenever(mockContext.getString(R.string.biometric_too_many_attempts)).thenReturn("Too many failed attempts")
        whenever(mockContext.getString(R.string.biometric_no_biometrics)).thenReturn("No biometrics enrolled")
        whenever(mockContext.getString(R.string.biometric_face_not_recognized)).thenReturn("Face not recognized")
        whenever(mockContext.getString(R.string.biometric_not_recognized)).thenReturn("Biometric not recognized")
        whenever(mockContext.getString(R.string.biometric_try_again)).thenReturn("Try again")
        whenever(mockContext.getString(R.string.biometric_face_not_supported)).thenReturn("Face authentication not supported")
        whenever(mockContext.getString(R.string.biometric_auth_failed_generic)).thenReturn("Biometric authentication failed")
    }

    private fun setupMockBiometricHelper() {
        whenever(mockBiometricHelper.getAvailableBiometricTypes()).thenReturn(listOf("fingerprint", "face"))
        whenever(mockBiometricHelper.isBiometricEnabled()).thenReturn(true)
        whenever(mockBiometricHelper.isBiometricAvailable()).thenReturn(true)
        whenever(mockBiometricHelper.hasActualFaceAuthentication()).thenReturn(true)
    }

    private fun setupMockNetworkUtils() {
        whenever(mockNetworkUtils.isInternetAvailable()).thenReturn(true)
    }

    @Test
    fun `test ViewModel initialization`() = runTest {
        val initialState = loginViewModel.uiState.first()

        assertEquals("", initialState.email)
        assertEquals("", initialState.password)
        assertFalse(initialState.isPasswordVisible)
        assertNull(initialState.emailError)
        assertNull(initialState.passwordError)
        assertFalse(initialState.isLoading)
        assertFalse(initialState.isLoginSuccessful)
        assertNull(initialState.errorMessage)
        assertFalse(initialState.showNetworkDialog)
        assertFalse(initialState.showBiometricSetupDialog)
        assertFalse(initialState.showBiometricBottomSheet)
        assertFalse(initialState.showBiometricChoiceDialog)
        assertFalse(initialState.showFaceAuthInfoDialog)
        assertNull(initialState.biometricAuthSuccess)
        assertNull(initialState.biometricAuthError)
        assertEquals(listOf("fingerprint", "face"), initialState.availableBiometricTypes)
        assertEquals("", initialState.selectedBiometricType)
    }

    @Test
    fun `test update email with valid email`() = runTest {
        val testEmail = "test@example.com"
        
        loginViewModel.updateEmail(testEmail)
        
        val state = loginViewModel.uiState.first()
        assertEquals(testEmail, state.email)
        assertNull(state.emailError)
    }

    @Test
    fun `test update email with invalid email`() = runTest {
        val invalidEmail = "invalid-email"
        
        loginViewModel.updateEmail(invalidEmail)
        
        val state = loginViewModel.uiState.first()
        assertEquals(invalidEmail, state.email)
        assertEquals("Invalid email format", state.emailError)
    }

    @Test
    fun `test update password with valid password`() = runTest {
        val testPassword = "password123"
        
        loginViewModel.updatePassword(testPassword)
        
        val state = loginViewModel.uiState.first()
        assertEquals(testPassword, state.password)
        assertNull(state.passwordError)
    }

    @Test
    fun `test update password with short password`() = runTest {
        val shortPassword = "12345"
        
        loginViewModel.updatePassword(shortPassword)
        
        val state = loginViewModel.uiState.first()
        assertEquals(shortPassword, state.password)
        assertEquals("Password must be at least 6 characters", state.passwordError)
    }

    @Test
    fun `test toggle password visibility`() = runTest {
        // Initially password should not be visible
        var state = loginViewModel.uiState.first()
        assertFalse(state.isPasswordVisible)
        
        // Toggle to visible
        loginViewModel.togglePasswordVisibility()
        state = loginViewModel.uiState.first()
        assertTrue(state.isPasswordVisible)
        
        // Toggle back to hidden
        loginViewModel.togglePasswordVisibility()
        state = loginViewModel.uiState.first()
        assertFalse(state.isPasswordVisible)
    }

    @Test
    fun `test login with no internet connection`() = runTest {
        // Setup no internet
        whenever(mockNetworkUtils.isInternetAvailable()).thenReturn(false)
        
        loginViewModel.updateEmail("test@example.com")
        loginViewModel.updatePassword("password123")
        
        loginViewModel.login()
        advanceUntilIdle()
        
        val state = loginViewModel.uiState.first()
        assertTrue(state.showNetworkDialog)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `test successful login`() = runTest {
        // Setup successful login response
        val mockLoginResponse = LoginResponse(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            gender = "male",
            image = "https://example.com/image.jpg",
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token"
        )
        whenever(mockUserRepository.login("test@example.com", "password123"))
            .thenReturn(Result.Success(mockLoginResponse))
        
        loginViewModel.updateEmail("test@example.com")
        loginViewModel.updatePassword("password123")
        
        loginViewModel.login()
        advanceUntilIdle()
        
        val state = loginViewModel.uiState.first()
        assertTrue(state.isLoginSuccessful)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test login failure`() = runTest {
        // Setup failed login response
        whenever(mockUserRepository.login("test@example.com", "wrongpassword"))
            .thenReturn(Result.Error("Invalid credentials"))
        
        loginViewModel.updateEmail("test@example.com")
        loginViewModel.updatePassword("wrongpassword")
        
        loginViewModel.login()
        advanceUntilIdle()
        
        val state = loginViewModel.uiState.first()
        assertFalse(state.isLoginSuccessful)
        assertFalse(state.isLoading)
        assertEquals("Invalid credentials", state.errorMessage)
    }

    @Test
    fun `test biometric login with biometric enabled`() = runTest {
        loginViewModel.biometricLogin()
        
        val state = loginViewModel.uiState.first()
        assertTrue(state.showBiometricChoiceDialog)
    }

    @Test
    fun `test biometric login with biometric disabled`() = runTest {
        whenever(mockBiometricHelper.isBiometricEnabled()).thenReturn(false)
        
        loginViewModel.biometricLogin()
        
        val state = loginViewModel.uiState.first()
        assertTrue(state.showBiometricSetupDialog)
    }

    @Test
    fun `test enable biometric`() = runTest {
        loginViewModel.enableBiometric()
        
        val state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricSetupDialog)
        assertTrue(state.showBiometricBottomSheet)
        assertNull(state.biometricAuthSuccess)
        assertNull(state.biometricAuthError)
    }

    @Test
    fun `test select fingerprint authentication`() = runTest {
        loginViewModel.selectFingerprintAuth()
        
        val state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricChoiceDialog)
        assertTrue(state.showBiometricBottomSheet)
        assertEquals("fingerprint", state.selectedBiometricType)
        assertNull(state.biometricAuthSuccess)
        assertNull(state.biometricAuthError)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test select face authentication with support`() = runTest {
        loginViewModel.selectFaceAuth()
        
        val state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricChoiceDialog)
        assertTrue(state.showBiometricBottomSheet)
        assertEquals("face", state.selectedBiometricType)
        assertNull(state.biometricAuthSuccess)
        assertNull(state.biometricAuthError)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test select face authentication without support`() = runTest {
        whenever(mockBiometricHelper.hasActualFaceAuthentication()).thenReturn(false)
        
        loginViewModel.selectFaceAuth()
        
        val state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricChoiceDialog)
        assertTrue(state.showFaceAuthInfoDialog)
    }

    @Test
    fun `test biometric authentication success`() = runTest {
        loginViewModel.onBiometricResult(true, null)
        
        val state = loginViewModel.uiState.first()
        assertTrue(state.biometricAuthSuccess == true)
        assertNull(state.biometricAuthError)
    }

    @Test
    fun `test biometric authentication failure`() = runTest {
        val errorMessage = "Authentication failed"
        loginViewModel.onBiometricResult(false, errorMessage)
        
        val state = loginViewModel.uiState.first()
        assertTrue(state.biometricAuthSuccess == false)
        assertEquals(errorMessage, state.biometricAuthError)
    }

    @Test
    fun `test hide network dialog`() = runTest {
        // First show network dialog
        loginViewModel.updateEmail("test@example.com")
        loginViewModel.updatePassword("password123")
        whenever(mockNetworkUtils.isInternetAvailable()).thenReturn(false)
        loginViewModel.login()
        advanceUntilIdle()
        
        var state = loginViewModel.uiState.first()
        assertTrue(state.showNetworkDialog)
        
        // Hide network dialog
        loginViewModel.hideNetworkDialog()
        state = loginViewModel.uiState.first()
        assertFalse(state.showNetworkDialog)
    }

    @Test
    fun `test retry login`() = runTest {
        // Setup successful retry
        val mockLoginResponse = LoginResponse(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            gender = "male",
            image = "https://example.com/image.jpg",
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token"
        )
        whenever(mockUserRepository.login("test@example.com", "password123"))
            .thenReturn(Result.Success(mockLoginResponse))
        
        loginViewModel.updateEmail("test@example.com")
        loginViewModel.updatePassword("password123")
        
        // First attempt fails due to no internet
        whenever(mockNetworkUtils.isInternetAvailable()).thenReturn(false)
        loginViewModel.login()
        advanceUntilIdle()
        
        var state = loginViewModel.uiState.first()
        assertTrue(state.showNetworkDialog)
        
        // Retry with internet available
        whenever(mockNetworkUtils.isInternetAvailable()).thenReturn(true)
        loginViewModel.retryLogin()
        advanceUntilIdle()
        
        state = loginViewModel.uiState.first()
        assertFalse(state.showNetworkDialog)
        assertTrue(state.isLoginSuccessful)
    }

    @Test
    fun `test logout`() = runTest {
        // First perform a successful login
        val mockLoginResponse = LoginResponse(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            gender = "male",
            image = "https://example.com/image.jpg",
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token"
        )
        whenever(mockUserRepository.login("test@example.com", "password123"))
            .thenReturn(Result.Success(mockLoginResponse))
        
        loginViewModel.updateEmail("test@example.com")
        loginViewModel.updatePassword("password123")
        loginViewModel.login()
        advanceUntilIdle()
        
        var state = loginViewModel.uiState.first()
        assertTrue(state.isLoginSuccessful)
        
        // Logout
        loginViewModel.logout()
        advanceUntilIdle()
        
        state = loginViewModel.uiState.first()
        assertFalse(state.isLoginSuccessful)
        assertEquals("", state.email)
        assertEquals("", state.password)
    }

    @Test
    fun `test dismiss biometric dialogs`() = runTest {
        // Test dismiss biometric setup dialog
        loginViewModel.enableBiometric()
        loginViewModel.dismissBiometricSetupDialog()
        var state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricSetupDialog)
        
        // Test dismiss biometric bottom sheet
        loginViewModel.enableBiometric()
        loginViewModel.dismissBiometricBottomSheet()
        state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricBottomSheet)
        assertNull(state.biometricAuthSuccess)
        assertNull(state.biometricAuthError)
        
        // Test dismiss biometric choice dialog
        loginViewModel.biometricLogin()
        loginViewModel.dismissBiometricChoiceDialog()
        state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricChoiceDialog)
        
        // Test dismiss face auth info dialog
        loginViewModel.selectFaceAuth()
        loginViewModel.dismissFaceAuthInfoDialog()
        state = loginViewModel.uiState.first()
        assertFalse(state.showFaceAuthInfoDialog)
    }

    @Test
    fun `test switch to fingerprint from face info`() = runTest {
        loginViewModel.selectFaceAuth()
        loginViewModel.switchToFingerprintFromFaceInfo()
        
        val state = loginViewModel.uiState.first()
        assertFalse(state.showFaceAuthInfoDialog)
        assertTrue(state.showBiometricBottomSheet)
        assertEquals("fingerprint", state.selectedBiometricType)
        assertNull(state.biometricAuthSuccess)
        assertNull(state.biometricAuthError)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test start biometric authentication with biometric not available`() = runTest {
        whenever(mockBiometricHelper.isBiometricAvailable()).thenReturn(false)
        
        loginViewModel.startBiometricAuthentication(mockActivity)
        advanceUntilIdle()
        
        val state = loginViewModel.uiState.first()
        assertFalse(state.showBiometricBottomSheet)
        assertEquals("Biometric authentication not available", state.errorMessage)
    }

    @Test
    fun `test form validation with empty fields`() = runTest {
        loginViewModel.updateEmail("")
        loginViewModel.updatePassword("")
        
        loginViewModel.login()
        advanceUntilIdle()
        
        val state = loginViewModel.uiState.first()
        assertEquals("Email is required", state.emailError)
        assertEquals("Password is required", state.passwordError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `test form validation with invalid email and short password`() = runTest {
        loginViewModel.updateEmail("invalid-email")
        loginViewModel.updatePassword("12345")
        
        loginViewModel.login()
        advanceUntilIdle()
        
        val state = loginViewModel.uiState.first()
        assertEquals("Invalid email format", state.emailError)
        assertEquals("Password must be at least 6 characters", state.passwordError)
        assertFalse(state.isLoading)
    }
}