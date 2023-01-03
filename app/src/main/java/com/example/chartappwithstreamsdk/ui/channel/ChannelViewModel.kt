package com.example.chartappwithstreamsdk.ui.channel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chartappwithstreamsdk.R
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val client: ChatClient
): ViewModel() {

    private val _createChannelEvent = MutableSharedFlow<CreateChannelEvent>()
    val createChannelEvent = _createChannelEvent.asSharedFlow()

    fun createChannel(channelName: String) {
        val trimmedChannelName = channelName.trim()
        val extraData = mutableMapOf<String, Any>(
            "name" to trimmedChannelName
        )
        val channelClient = client.channel(
            channelType = "messaging",
            channelId = UUID.randomUUID().toString()
        )

        viewModelScope.launch {
            if (trimmedChannelName.isEmpty()) {
                _createChannelEvent.emit(
                    CreateChannelEvent.Error("The channel name can't be empty")
                )
                return@launch
            }

            val result = channelClient.create(
                memberIds = emptyList(),
                extraData = extraData
            ).await()

            if (trimmedChannelName.isEmpty()) {
                _createChannelEvent.emit(
                    CreateChannelEvent.Error("The channel name can't be empty")
                )
                return@launch
            }
//            channelClient.watch().enqueue()
//            val result = client.channel(
//                channelType = "messaging",
//                channelId = UUID.randomUUID().toString()
//            ).create(
//                mapOf(
//                    "name" to trimmedChannelName
//                )
//            ).await()

            if (result.isError) {
                _createChannelEvent.emit(
                    CreateChannelEvent.Error(result.error().message ?: "Unknown error")
                )
                return@launch
            }
            _createChannelEvent.emit(CreateChannelEvent.Success)
        }
    }

    fun logout() {
        client.disconnect(true)
    }

    fun getUser(): User? {
        return client.getCurrentUser()
    }

    sealed class CreateChannelEvent {
        data class Error(val error: String) : CreateChannelEvent()
        object Success : CreateChannelEvent()
    }

}