package com.app.findhome.ui.pages.message

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.findhome.data.model.ChatCardItemContent
import com.app.findhome.data.model.ChatScreenUiState
import com.app.findhome.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ChatScreenUiState())
    val state = _state
    //val state = _state.asStateFlow()

//    init {
//        getMessages()
//    }

    fun getMessages() {
        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            Log.d("getMessages", "Current user id: $currentUserId")
            if (currentUserId != null) {
                FirebaseUtils.getLatestMessagesForUser(currentUserId).collect { latestMessages ->
                    Log.d("getMessages", "Latest messages: $latestMessages")
//                    _state.update {
//                        it.copy(messages = latestMessages)
//                    }
                    _state.value = ChatScreenUiState(messages = latestMessages)
                    Log.d("getMessages", "State: ${_state.value}")
                }
            }
        }
    }
}
