package com.gorman.chatroom.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.entities.networkData.SmsRequest
import com.gorman.chatroom.domain.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class AuthUiState(
    val isLoading: Boolean = false,
    val codeSent: Boolean = false,
    val success: String? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val smsRepository: SmsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val otpValue = mutableStateOf("")

    fun sendCode(phoneNumber: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            otpValue.value = generateOtpCode()
            val request = SmsRequest(
                number = "1344243",
                destination = phoneNumber,
                text = "Ваш код для входа: ${otpValue.value}"
            )
            try {
                val response = smsRepository.sendMessage(request)
                if (response.isSuccessful) {
                    _uiState.value = AuthUiState(codeSent = true)
                } else {
                    _uiState.value = AuthUiState(error = "Ошибка отправки SMS")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun generateOtpCode(): String =
        Random.nextInt(1000, 10000).toString()
}