package com.hrizvi.offlineapinotes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState {
    object Loading : UiState
    data class Success(val posts: List<Post>) : UiState
    data class Error(val message: String) : UiState
}

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).postDao()

    val saved = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState = UiState.Loading
            uiState = try {
                UiState.Success(RetrofitClient.api.getPosts())
            } catch (e: Exception) {
                UiState.Error("No internet or server error. Open the Saved tab to view offline data.")
            }
        }
    }

    fun save(post: Post) = viewModelScope.launch { dao.insert(post.toEntity()) }
    fun delete(entity: PostEntity) = viewModelScope.launch { dao.delete(entity) }
}