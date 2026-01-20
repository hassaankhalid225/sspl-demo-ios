package com.sspl.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.Post
import com.sspl.core.usecases.Comment
import com.sspl.core.usecases.CommentOnPostUseCase
import com.sspl.core.usecases.DislikePostUseCase
import com.sspl.core.usecases.GetPostDetailsUseCase
import com.sspl.core.usecases.GetPostsUseCase
import com.sspl.core.usecases.LikePostUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 09/01/2025.
 * se.muhammadimran@gmail.com
 */

class PostViewModel(
    private val getPostsUseCase: GetPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val dislikePostUseCase: DislikePostUseCase,
    private val getPostDetailsUseCase: GetPostDetailsUseCase,
    private val commentOnPostUseCase: CommentOnPostUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _postComments = MutableStateFlow<ApiStates<List<Comment>>>(ApiStates.Loading)
    val postComments = _postComments.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _uiState = MutableStateFlow<ApiStates<List<Post>>>(ApiStates.Loading)
    val uiState = _uiState.onStart {
        fetchPosts()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ApiStates.Loading
    )


    fun onEvent(event: PostScreenEvents) {
        viewModelScope.launch {
            when (event) {
                is PostScreenEvents.CommentPost -> {
                    commentOnPostUseCase(event.postId)
                }

                is PostScreenEvents.LostCommentsForPost -> {
                    _showBottomSheet.update { true }
                    getPostDetails(event.postId)
                }

                is PostScreenEvents.DislikePost -> {
                    dislikePostUseCase(event.postId)
                }

                is PostScreenEvents.LikePost -> {
                    likePostUseCase(event.postId)
                }

                PostScreenEvents.RefreshPosts -> {
                    refreshPost()
                }
                PostScreenEvents.ToggleBottomSheet -> {
                    _showBottomSheet.update {
                        !it
                    }
                }

                PostScreenEvents.LoadMorePosts -> {
                    _isLoadingMore.update {
                        true
                    }
                    delay(2000)
                    _isLoadingMore.update {
                        false
                    }
                }
            }
        }
    }

    private fun refreshPost() {
        _isRefreshing.value = true
        viewModelScope.launch {
            delay(1500)
            fetchPosts()
            _isRefreshing.value = false
        }
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            getPostsUseCase().collect { posts ->
                if (posts.isEmpty()) {
                    _uiState.value = ApiStates.Failure(ApiError("No posts available"))
                } else {
                    _uiState.value = ApiStates.Success(posts)
                    _isRefreshing.value = false
                }
            }
        }
    }

    private fun getPostDetails(id: Long) {
        viewModelScope.launch {
            getPostDetailsUseCase.invoke(id).collect { state ->
                println("Hello $state")
                _postComments.update { state }
            }
        }
    }

}

sealed interface PostScreenEvents {
    data object RefreshPosts : PostScreenEvents
    data object ToggleBottomSheet : PostScreenEvents
    data class LikePost(val postId: Long) : PostScreenEvents
    data class DislikePost(val postId: Long) : PostScreenEvents
    data class CommentPost(val postId: Long) : PostScreenEvents
    data class LostCommentsForPost(val postId: Long) : PostScreenEvents
    data object LoadMorePosts : PostScreenEvents
}

