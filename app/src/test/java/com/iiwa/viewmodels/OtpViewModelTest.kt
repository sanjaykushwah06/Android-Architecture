/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Comprehensive test cases for OtpViewModel in authorization package
 */

package com.iiwa.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iiwa.R
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.authorization.viewmodels.OtpViewModel
import com.iiwa.data.model.ForgotPasswordResponse
import com.iiwa.data.model.VerifyOtpResponse
import com.iiwa.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
 * Test class for OtpViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockUserRepository: UserRepository
    
    private lateinit var otpViewModel: OtpViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        
        // Setup mock context with string resources
        setupMockContext()
        
        otpViewModel = OtpViewModel(
            mockContext,
            mockUserRepository
        )
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    private fun setupMockContext() {
        whenever(mockContext.getString(R.string.otp_required)).thenReturn("OTP is required")
        whenever(mockContext.getString(R.string.otp_must_be_4_digits)).thenReturn("OTP must be 4 digits")
        whenever(mockContext.getString(R.string.otp_must_be_numbers)).thenReturn("OTP must contain only numbers")
        whenever(mockContext.getString(R.string.invalid_otp_test)).thenReturn("Invalid OTP. Please enter 1234.")
        whenever(mockContext.getString(R.string.no_internet_connection)).thenReturn("No internet connection")
        whenever(mockContext.getString(R.string.request_timeout)).thenReturn("Request timeout")
        whenever(mockContext.getString(R.string.network_error_generic)).thenReturn("Network error occurred")
    }

    @Test
    fun `test ViewModel initialization`() = runTest {
        val initialState = otpViewModel.uiState.first()

        assertEquals("", initialState.email)
        assertEquals("", initialState.otp)
        assertNull(initialState.otpError)
        assertFalse(initialState.isLoading)
        assertFalse(initialState.isOtpVerified)
        assertNull(initialState.errorMessage)
        assertFalse(initialState.showNetworkDialog)
        assertEquals(60, initialState.resendCountdown)
        assertFalse(initialState.canResendOtp)
        assertFalse(initialState.isResendingOtp)
        assertFalse(initialState.showOtpResentMessage)
    }

    @Test
    fun `test update OTP with valid 4-digit number`() = runTest {
        val validOtp = "1234"
        
        otpViewModel.updateOtp(validOtp)
        
        val state = otpViewModel.uiState.first()
        assertEquals(validOtp, state.otp)
        assertNull(state.otpError)
    }

    @Test
    fun `test update OTP with valid 3-digit number`() = runTest {
        val validOtp = "123"
        
        otpViewModel.updateOtp(validOtp)
        
        val state = otpViewModel.uiState.first()
        assertEquals(validOtp, state.otp)
        assertNull(state.otpError)
    }

    @Test
    fun `test update OTP with invalid characters`() = runTest {
        val invalidOtp = "12a4"
        
        otpViewModel.updateOtp(invalidOtp)
        
        // Should not update OTP since it contains non-digit characters
        val state = otpViewModel.uiState.first()
        assertEquals("", state.otp)
    }

    @Test
    fun `test update OTP with more than 4 digits`() = runTest {
        val longOtp = "12345"
        
        otpViewModel.updateOtp(longOtp)
        
        // Should not update OTP since it's longer than 4 digits
        val state = otpViewModel.uiState.first()
        assertEquals("", state.otp)
    }

    @Test
    fun `test update OTP with empty string`() = runTest {
        otpViewModel.updateOtp("")
        
        val state = otpViewModel.uiState.first()
        assertEquals("", state.otp)
        assertNull(state.otpError)
    }

    @Test
    fun `test verify OTP with empty OTP`() = runTest {
        otpViewModel.updateOtp("")
        
        otpViewModel.verifyOtp()
        advanceUntilIdle()
        
        val state = otpViewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isOtpVerified)
        assertEquals("OTP is required", state.otpError)
    }

    @Test
    fun `test verify OTP with 3-digit OTP`() = runTest {
        otpViewModel.updateOtp("123")
        
        otpViewModel.verifyOtp()
        advanceUntilIdle()
        
        val state = otpViewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.isOtpVerified)
        assertEquals("OTP must be 4 digits", state.otpError)
    }

    @Test
    fun `test verify OTP with correct test OTP`() = runTest {
        otpViewModel.updateOtp("1234")
        
        otpViewModel.verifyOtp()
        advanceUntilIdle()
        
        val state = otpViewModel.uiState.first()
        assertTrue(state.isOtpVerified)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test verify OTP with incorrect OTP`() = runTest {
        otpViewModel.updateOtp("5678")
        
        otpViewModel.verifyOtp()
        advanceUntilIdle()
        
        val state = otpViewModel.uiState.first()
        assertFalse(state.isOtpVerified)
        assertFalse(state.isLoading)
        assertEquals("Invalid OTP. Please enter 1234.", state.errorMessage)
    }

    @Test
    fun `test loading state during OTP verification`() = runTest {
        otpViewModel.updateOtp("1234")
        
        otpViewModel.verifyOtp()
        
        // Should be loading immediately
        var state = otpViewModel.uiState.first()
        assertTrue(state.isLoading)
        
        advanceUntilIdle()
        
        // Should not be loading after completion
        state = otpViewModel.uiState.first()
        assertFalse(state.isLoading)
    }

    @Test
    fun `test set email`() = runTest {
        val testEmail = "test@example.com"
        
        otpViewModel.setEmail(testEmail)
        
        val state = otpViewModel.uiState.first()
        assertEquals(testEmail, state.email)
    }

    @Test
    fun `test resend OTP when countdown is active`() = runTest {
        // Initially countdown is active, so resend should not work
        otpViewModel.resendOtp()
        advanceUntilIdle()
        
        val state = otpViewModel.uiState.first()
        assertFalse(state.isResendingOtp)
        assertFalse(state.showOtpResentMessage)
    }

    @Test
    fun `test resend OTP when countdown is finished`() = runTest {
        // Setup successful resend response
        val mockResponse = ForgotPasswordResponse(
            message = "OTP sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        otpViewModel.setEmail("test@example.com")
        
        // Fast forward the countdown timer
        advanceTimeBy(61000) // 61 seconds
        advanceUntilIdle()
        
        // Now resend should work
        otpViewModel.resendOtp()
        advanceUntilIdle()
        
        val state = otpViewModel.uiState.first()
        assertFalse(state.isResendingOtp)
        assertTrue(state.showOtpResentMessage)
        assertEquals(60, state.resendCountdown) // Timer should restart
        assertFalse(state.canResendOtp) // Should be false again due to new timer
    }

    @Test
    fun `test resend OTP failure`() = runTest {
        // Setup failed resend response
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Error("Failed to send OTP"))
        
        otpViewModel.setEmail("test@example.com")
        
        // Fast forward the countdown timer
        advanceTimeBy(61000) // 61 seconds
        advanceUntilIdle()
        
        // Now resend should work but fail
        otpViewModel.resendOtp()
        advanceUntilIdle()
        
        val state = otpViewModel.uiState.first()
        assertFalse(state.isResendingOtp)
        assertFalse(state.showOtpResentMessage)
        assertEquals("Failed to send OTP", state.errorMessage)
    }

    @Test
    fun `test hide network dialog`() = runTest {
        // First show network dialog (this would normally happen with network errors)
        // For this test, we'll simulate the state directly
        otpViewModel.hideNetworkDialog()
        
        val state = otpViewModel.uiState.first()
        assertFalse(state.showNetworkDialog)
    }

    @Test
    fun `test retry verification`() = runTest {
        // First attempt with wrong OTP
        otpViewModel.updateOtp("5678")
        otpViewModel.verifyOtp()
        advanceUntilIdle()
        
        var state = otpViewModel.uiState.first()
        assertFalse(state.isOtpVerified)
        assertEquals("Invalid OTP. Please enter 1234.", state.errorMessage)
        
        // Retry with correct OTP
        otpViewModel.updateOtp("1234")
        otpViewModel.retryVerification()
        advanceUntilIdle()
        
        state = otpViewModel.uiState.first()
        assertTrue(state.isOtpVerified)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test hide OTP resent message`() = runTest {
        // First trigger the resent message
        val mockResponse = ForgotPasswordResponse(
            message = "OTP sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        otpViewModel.setEmail("test@example.com")
        
        // Fast forward the countdown timer
        advanceTimeBy(61000) // 61 seconds
        advanceUntilIdle()
        
        otpViewModel.resendOtp()
        advanceUntilIdle()
        
        var state = otpViewModel.uiState.first()
        assertTrue(state.showOtpResentMessage)
        
        // Hide the message
        otpViewModel.hideOtpResentMessage()
        
        state = otpViewModel.uiState.first()
        assertFalse(state.showOtpResentMessage)
    }

    @Test
    fun `test resend countdown timer`() = runTest {
        val initialState = otpViewModel.uiState.first()
        assertEquals(60, initialState.resendCountdown)
        assertFalse(initialState.canResendOtp)
        
        // Fast forward by 30 seconds
        advanceTimeBy(30000)
        advanceUntilIdle()
        
        var state = otpViewModel.uiState.first()
        assertEquals(30, state.resendCountdown)
        assertFalse(state.canResendOtp)
        
        // Fast forward by another 30 seconds
        advanceTimeBy(30000)
        advanceUntilIdle()
        
        state = otpViewModel.uiState.first()
        assertEquals(0, state.resendCountdown)
        assertTrue(state.canResendOtp)
    }

    @Test
    fun `test OTP validation with various inputs`() = runTest {
        val testCases = listOf(
            "" to "OTP is required",
            "1" to "OTP must be 4 digits",
            "12" to "OTP must be 4 digits",
            "123" to "OTP must be 4 digits",
            "1234" to null,
            "12345" to "OTP must be 4 digits"
        )
        
        testCases.forEach { (otp, expectedError) ->
            otpViewModel.updateOtp(otp)
            otpViewModel.verifyOtp()
            advanceUntilIdle()
            
            val state = otpViewModel.uiState.first()
            
            if (expectedError == null) {
                assertNull("OTP '$otp' should be valid", state.otpError)
            } else {
                assertEquals("OTP '$otp' should have error: $expectedError", expectedError, state.otpError)
            }
        }
    }

    @Test
    fun `test multiple OTP updates`() = runTest {
        // First update with valid OTP
        otpViewModel.updateOtp("1234")
        var state = otpViewModel.uiState.first()
        assertEquals("1234", state.otp)
        assertNull(state.otpError)
        
        // Update with shorter OTP
        otpViewModel.updateOtp("123")
        state = otpViewModel.uiState.first()
        assertEquals("123", state.otp)
        assertNull(state.otpError)
        
        // Update with empty OTP
        otpViewModel.updateOtp("")
        state = otpViewModel.uiState.first()
        assertEquals("", state.otp)
        assertNull(state.otpError)
    }

    @Test
    fun `test error message clearing on new verification`() = runTest {
        // First verify with wrong OTP
        otpViewModel.updateOtp("5678")
        otpViewModel.verifyOtp()
        advanceUntilIdle()
        
        var state = otpViewModel.uiState.first()
        assertEquals("Invalid OTP. Please enter 1234.", state.errorMessage)
        
        // Verify again with correct OTP
        otpViewModel.updateOtp("1234")
        otpViewModel.verifyOtp()
        advanceUntilIdle()
        
        state = otpViewModel.uiState.first()
        assertNull(state.errorMessage)
        assertTrue(state.isOtpVerified)
    }

    @Test
    fun `test resend timer restart after successful resend`() = runTest {
        val mockResponse = ForgotPasswordResponse(
            message = "OTP sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        otpViewModel.setEmail("test@example.com")
        
        // Fast forward the initial countdown
        advanceTimeBy(61000) // 61 seconds
        advanceUntilIdle()
        
        var state = otpViewModel.uiState.first()
        assertTrue(state.canResendOtp)
        
        // Resend OTP
        otpViewModel.resendOtp()
        advanceUntilIdle()
        
        state = otpViewModel.uiState.first()
        assertTrue(state.showOtpResentMessage)
        assertEquals(60, state.resendCountdown) // Timer should restart
        assertFalse(state.canResendOtp) // Should be false again
        
        // Fast forward by 30 seconds
        advanceTimeBy(30000)
        advanceUntilIdle()
        
        state = otpViewModel.uiState.first()
        assertEquals(30, state.resendCountdown)
        assertFalse(state.canResendOtp)
    }

    @Test
    fun `test loading state during resend OTP`() = runTest {
        val mockResponse = ForgotPasswordResponse(
            message = "OTP sent successfully",
            success = true
        )
        whenever(mockUserRepository.forgotPassword("test@example.com"))
            .thenReturn(Result.Success(mockResponse))
        
        otpViewModel.setEmail("test@example.com")
        
        // Fast forward the countdown timer
        advanceTimeBy(61000) // 61 seconds
        advanceUntilIdle()
        
        otpViewModel.resendOtp()
        
        // Should be resending immediately
        var state = otpViewModel.uiState.first()
        assertTrue(state.isResendingOtp)
        
        advanceUntilIdle()
        
        // Should not be resending after completion
        state = otpViewModel.uiState.first()
        assertFalse(state.isResendingOtp)
    }
}


