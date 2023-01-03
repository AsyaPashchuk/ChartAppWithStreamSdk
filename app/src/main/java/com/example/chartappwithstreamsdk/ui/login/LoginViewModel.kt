package com.example.chartappwithstreamsdk.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chartappwithstreamsdk.util.Constants.MIN_USERNAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent = _loginEvent.asSharedFlow()

    private fun isValidUserName(userName: String) =
        userName.length >= MIN_USERNAME_LENGTH

    fun connectUser(userName: String) {
        val trimmedUsername = userName.trim()
        viewModelScope.launch {
            if (isValidUserName(trimmedUsername)) {
                val result = client.connectGuestUser(
                    userId = trimmedUsername,
                    username = trimmedUsername
                ).await()
                if (result.isError) {
                    _loginEvent.emit(LoginEvent.ErrorLogin(result.error().message ?: "Unknown error"))
                    return@launch
                }
                _loginEvent.emit(LoginEvent.Success)
            } else {
                _loginEvent.emit(LoginEvent.ErrorInputTooShort)
            }
        }
    }

    sealed class LoginEvent {
        object ErrorInputTooShort : LoginEvent()
        data class ErrorLogin(val error: String): LoginEvent()
        object Success : LoginEvent()
    }
}