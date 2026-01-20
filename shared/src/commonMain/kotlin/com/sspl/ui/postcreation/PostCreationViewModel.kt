package com.sspl.ui.postcreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PostCreationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PostCreationUiState())
    val uiState: StateFlow<PostCreationUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreatePostUIEvents) {
        when (event) {
            is CreatePostUIEvents.OnDescriptionChange -> {
                _uiState.update { it.copy(description = event.message) }
                checkUploadEnabled()
            }
            is CreatePostUIEvents.ToggleBottomSheet -> {
                _uiState.update { it.copy(showChooserBottomSheet = event.showChooserBottomSheet) }
            }
            is CreatePostUIEvents.ToggleCameraView -> {
                _uiState.update { it.copy(isCameraOpen = !it.isCameraOpen) }
            }

            is CreatePostUIEvents.OnImageAdd -> {
                _uiState.update { currentState ->
                    if (currentState.images.size < 2) {
                        currentState.copy(images = currentState.images + event.image, isLoadingImage = false)
                    } else {
                        currentState.copy(isLoadingImage = false)
                    }
                }
                checkUploadEnabled()
            }

            is CreatePostUIEvents.OnImageDelete -> {
                _uiState.update { currentState ->
                    currentState.copy(images = currentState.images.filterIndexed { i, _ -> i != event.index })
                }
                checkUploadEnabled()
            }
            is CreatePostUIEvents.OnImageLoading -> {
                _uiState.update { it.copy(isLoadingImage = event.isLoading) }
            }

            is CreatePostUIEvents.UploadPost -> {
                uploadPost()
            }
        }
    }

    private fun checkUploadEnabled() {
        // The upload button is enabled if there is either description or images present
        val isUploadEnabled =
            _uiState.value.description.isNotEmpty() || _uiState.value.images.isNotEmpty()
        _uiState.update { it.copy(isUploadEnabled = isUploadEnabled) }
    }

    private fun uploadPost() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(2000)
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}

data class PostCreationUiState(
    val description: String = "",
    val isCameraOpen:Boolean=false,
    val isLoadingImage: Boolean = false,
    val isLoading: Boolean = false,
    val isUploadEnabled: Boolean = false,val showChooserBottomSheet: Boolean = false,
    val images: List<ByteArray> = emptyList(),
)

sealed interface CreatePostUIEvents {
    data class OnDescriptionChange(val message: String) : CreatePostUIEvents
    data class OnImageLoading(val isLoading: Boolean) : CreatePostUIEvents
    data class OnImageAdd(val image: List<ByteArray>) : CreatePostUIEvents
    data class OnImageDelete(val index: Int) : CreatePostUIEvents
    data class ToggleBottomSheet(val showChooserBottomSheet: Boolean) : CreatePostUIEvents
    data object UploadPost : CreatePostUIEvents
    data object  ToggleCameraView : CreatePostUIEvents
}
