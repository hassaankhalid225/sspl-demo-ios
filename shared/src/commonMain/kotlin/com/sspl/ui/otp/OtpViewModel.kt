package com.sspl.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class OtpViewModel : ViewModel() {
    private val _resultFlow = MutableSharedFlow<OtpOneTimeEvents>()
    val resultFlow = _resultFlow.asSharedFlow()

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.VerifyOtp -> verifyOtp(event.otp)
            is OtpEvent.ResendOtp -> resendOtp()
            is OtpEvent.OtpValueChanged -> updateOtpValue(event.value)
        }
    }

    private fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isVerifying = true)
            delay(VERIFICATION_DELAY)
            when (otp) {
                VALID_OTP -> _resultFlow.emit(OtpOneTimeEvents.Success)
                else -> _resultFlow.emit(OtpOneTimeEvents.ShowMessage("Invalid OTP"))
            }
            _state.value = _state.value.copy(isVerifying = false)
        }
    }

    private fun resendOtp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(otpValue = "")
            _resultFlow.emit(OtpOneTimeEvents.ShowMessage("Sent OTP"))
        }
    }

    private fun updateOtpValue(value: String) {
        _state.value = _state.value.copy(otpValue = value)
    }

    companion object {
        private const val VALID_OTP = "123456"
        private const val VERIFICATION_DELAY = 1500L
    }
}

data class OtpState(
    val isVerifying: Boolean = false,
    val otpValue: String = ""
)

sealed interface OtpEvent {
    data class VerifyOtp(val otp: String) : OtpEvent
    data object ResendOtp : OtpEvent
    data class OtpValueChanged(val value: String) : OtpEvent
}

sealed interface OtpOneTimeEvents {
    data class ShowMessage(val message: String) : OtpOneTimeEvents
    data object Success : OtpOneTimeEvents
}

