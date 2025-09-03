/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Comprehensive test cases for ForgotPasswordViewModel in authorization package
 */

package com.iiwa.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iiwa.R
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.authorization.viewmodels.ForgotPasswordViewModel
import com.iiwa.data.model.ForgotPasswordResponse
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
 * Test class for ForgotPasswordViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockUserRepository: UserRepository
    
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        
        // Setup mock context with string resources
        setupMockContext()
        
        forgotPasswordViewModel = ForgotPasswordViewModel(
            mockContext,
            mockUserRepository
        )
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    private fun setupMockContext() {
        whenever(mockContext.getString(R.string.email_required)).thenReturn("Email is required")
        whenever(mockContext.getString(R.string.email_invalid_format)).thenReturn("Invalid email format")
        whenever(mockContext.getString(R.string.no_internet_connection)).thenReturn("No internet connection")
        whenever(mockContext.getString(R.string.request_timeout)).thenReturn("Request timeout")
        whenever(mockContext.getString(R.string.network_error_generic)).thenReturn("Network error occurred")
    }

    @Test
    fun `test ViewModel initialization`() = runTest {
        val initialState = forgotPasswordViewModel.uiState.first()

        assertEquals("", initialState.email)
        assertNull(initialState.emailError)
        assertFalse(initialState.isLoading)
        assertFalse(initialState.isEmailSent)
        assertNull(initialState.errorMessage)
        assertFalse(initialState.showNetworkDialog)
    }

    @Test
    fun `test update email with valid email`() = runTest {
        val testEmail = "test@example.com"
        
        forgotPasswordViewModel.updateEmail(testEmail)
        
        val state = forgotPasswordViewModel.uiState.first()
        assertEquals(testEmail, state.email)
        assertNull(state.emailError)
    }

    @Test
    fun `test update email with empty email`() = runTest {
        forgotPasswordViewModel.updateEmail("")
        
        val state = forgotPasswordViewModel.uiState.first()
        assertEquals("", state.email)
        assertEquals("Email is required", state.emailError)
    }

    @Test
    fun `test update email with invalid email format`() = runTest {
        val invalidEmail = "invalid-email"
        
        forgotPasswordViewModel.updateEmail(invalidEmail)
        
        val state = forgotPasswordViewModel.uiState.first()
        assertEquals(invalidEmail, state.email)
        assertEquals("Invalid email format", state.emailError)
    }

    @Test
    fun `test submit forgot password with empty email`() = runTest {
        forgotPasswordViewModel.updateEmail("")
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        val state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isEmailSent)
        assertEquals("Email is required", state.emailError)
    }

    @Test
    fun `test submit forgot password with invalid email format`() = runTest {
        forgotPasswordViewModel.updateEmail("invalid-email")
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        val state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isEmailSent)
        assertEquals("Invalid email format", state.emailError)
    }

    @Test
    fun `test successful forgot password submission`() = runTest {
        // Setup successful response
        val mockResponse = ForgotPasswordResponse(
            message = "Password reset email sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        val state = forgotPasswordViewModel.uiState.first()
        assertTrue(state.isEmailSent)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertFalse(state.showNetworkDialog)
    }

    @Test
    fun `test failed forgot password submission with generic error`() = runTest {
        // Setup failed response
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("Email not found"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        val state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isEmailSent)
        assertFalse(state.isLoading)
        assertEquals("Email not found", state.errorMessage)
        assertFalse(state.showNetworkDialog)
    }

    @Test
    fun `test failed forgot password submission with network error - no internet`() = runTest {
        // Setup network error response
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("No internet connection"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        val state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isEmailSent)
        assertFalse(state.isLoading)
        assertEquals("No internet connection", state.errorMessage)
        assertTrue(state.showNetworkDialog)
    }

    @Test
    fun `test failed forgot password submission with network error - request timeout`() = runTest {
        // Setup network error response
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("Request timeout"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        val state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isEmailSent)
        assertFalse(state.isLoading)
        assertEquals("Request timeout", state.errorMessage)
        assertTrue(state.showNetworkDialog)
    }

    @Test
    fun `test failed forgot password submission with network error - generic network error`() = runTest {
        // Setup network error response
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("Network error occurred"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        val state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isEmailSent)
        assertFalse(state.isLoading)
        assertEquals("Network error occurred", state.errorMessage)
        assertTrue(state.showNetworkDialog)
    }

    @Test
    fun `test resend email functionality`() = runTest {
        // First submit successfully
        val mockResponse = ForgotPasswordResponse(
            message = "Password reset email sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        var state = forgotPasswordViewModel.uiState.first()
        assertTrue(state.isEmailSent)
        assertEquals("test@example.com", state.email)
        
        // Resend email
        forgotPasswordViewModel.resendEmail()
        
        state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isEmailSent)
        assertEquals("", state.email)
        assertNull(state.emailError)
    }

    @Test
    fun `test hide network dialog`() = runTest {
        // First trigger network dialog
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("No internet connection"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        var state = forgotPasswordViewModel.uiState.first()
        assertTrue(state.showNetworkDialog)
        
        // Hide network dialog
        forgotPasswordViewModel.hideNetworkDialog()
        
        state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.showNetworkDialog)
    }

    @Test
    fun `test retry submit functionality`() = runTest {
        // First attempt fails with network error
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("No internet connection"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        var state = forgotPasswordViewModel.uiState.first()
        assertTrue(state.showNetworkDialog)
        assertFalse(state.isEmailSent)
        
        // Setup successful retry
        val mockResponse = ForgotPasswordResponse(
            message = "Password reset email sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        // Retry submit
        forgotPasswordViewModel.retrySubmit()
        advanceUntilIdle()
        
        state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.showNetworkDialog)
        assertTrue(state.isEmailSent)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test loading state during submission`() = runTest {
        // Setup delayed response
        val mockResponse = ForgotPasswordResponse(
            message = "Password reset email sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        
        forgotPasswordViewModel.submitForgotPassword()
        
        // Should be loading immediately
        var state = forgotPasswordViewModel.uiState.first()
        assertTrue(state.isLoading)
        
        advanceUntilIdle()
        
        // Should not be loading after completion
        state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.isLoading)
    }

    @Test
    fun `test multiple email updates`() = runTest {
        // First update with invalid email
        forgotPasswordViewModel.updateEmail("invalid-email")
        var state = forgotPasswordViewModel.uiState.first()
        assertEquals("invalid-email", state.email)
        assertEquals("Invalid email format", state.emailError)
        
        // Update with valid email
        forgotPasswordViewModel.updateEmail("test@example.com")
        state = forgotPasswordViewModel.uiState.first()
        assertEquals("test@example.com", state.email)
        assertNull(state.emailError)
        
        // Update with empty email
        forgotPasswordViewModel.updateEmail("")
        state = forgotPasswordViewModel.uiState.first()
        assertEquals("", state.email)
        assertEquals("Email is required", state.emailError)
    }

    @Test
    fun `test form validation with various email formats`() = runTest {
        val testCases = listOf(
            "" to "Email is required",
            "invalid" to "Invalid email format",
            "test@" to "Invalid email format",
            "@example.com" to "Invalid email format",
            "test@example" to "Invalid email format",
            "test@example.com" to null,
            "user.name@domain.co.uk" to null,
            "test+tag@example.org" to null
        )
        
        testCases.forEach { (email, expectedError) ->
            forgotPasswordViewModel.updateEmail(email)
            val state = forgotPasswordViewModel.uiState.first()
            
            if (expectedError == null) {
                assertNull("Email '$email' should be valid", state.emailError)
            } else {
                assertEquals("Email '$email' should have error: $expectedError", expectedError, state.emailError)
            }
        }
    }

    @Test
    fun `test error message clearing on new submission`() = runTest {
        // First submit with error
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("Email not found"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        var state = forgotPasswordViewModel.uiState.first()
        assertEquals("Email not found", state.errorMessage)
        
        // Submit again with success
        val mockResponse = ForgotPasswordResponse(
            message = "Password reset email sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        state = forgotPasswordViewModel.uiState.first()
        assertNull(state.errorMessage)
        assertTrue(state.isEmailSent)
    }

    @Test
    fun `test network dialog state management`() = runTest {
        // Test showing network dialog
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("No internet connection"))
        
        forgotPasswordViewModel.updateEmail("test@example.com")
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        var state = forgotPasswordViewModel.uiState.first()
        assertTrue(state.showNetworkDialog)
        
        // Test hiding network dialog
        forgotPasswordViewModel.hideNetworkDialog()
        state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.showNetworkDialog)
        
        // Test retry clears network dialog
        forgotPasswordViewModel.submitForgotPassword()
        advanceUntilIdle()
        
        state = forgotPasswordViewModel.uiState.first()
        assertTrue(state.showNetworkDialog)
        
        val mockResponse = ForgotPasswordResponse(
            message = "Password reset email sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        forgotPasswordViewModel.retrySubmit()
        advanceUntilIdle()
        
        state = forgotPasswordViewModel.uiState.first()
        assertFalse(state.showNetworkDialog)
    }
}


