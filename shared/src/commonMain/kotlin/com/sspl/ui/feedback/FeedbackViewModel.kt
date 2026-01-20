package com.sspl.ui.feedback

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.CommonResponse
import com.sspl.core.models.Option
import com.sspl.core.models.Statement
import com.sspl.core.usecases.GetFeedbackFormUseCase
import com.sspl.core.usecases.PostFeedbackFormUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val getFeedbackFormUseCase: GetFeedbackFormUseCase,
    private val postFeedbackFormUseCase: PostFeedbackFormUseCase,
) : ViewModel() {
    private val _conferenceId = MutableStateFlow<Long?>(null)
    private val _sessionId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val feedbackFormResponse =
        _conferenceId.filterNotNull().filterNotNull().flatMapLatest { confID ->
            getFeedbackFormUseCase(confID)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ApiStates.Loading
        )

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    private val _isFeedbackSubmitted: MutableSharedFlow<ApiStates<CommonResponse>> = MutableSharedFlow()
    val isFeedbackSubmitted = _isFeedbackSubmitted.asSharedFlow()


    fun setConferenceId(conferenceId: Long) {
        viewModelScope.launch {
            _conferenceId.update { conferenceId }
        }
    }
fun setSessionId(sessionId: Long?) {
    viewModelScope.launch {
        _sessionId.update { sessionId }
    }
}

    fun onEvent(event: FeedbackUiEvents) {
        when (event) {


            is FeedbackUiEvents.OnAnswerSelected -> {
                val updatedAnswers = _uiState.value.selectedAnswers.toMutableList().apply {
                    // Remove existing entry for the same statement ID
                    removeAll { it.first.id == event.statement.id }

                    // If it's a comment, store it as an Option with the comment as its value
                    if (!event.comment.isNullOrBlank()) {
                        add(event.statement to Option(label = "Comment", value = event.comment))
                    } else if (event.option != null) {
                        add(event.statement to event.option)
                    }
                }

                _uiState.value = _uiState.value.copy(selectedAnswers = updatedAnswers, isEnabled = true)
            }



            is FeedbackUiEvents.OnSubmitClicked -> {
                _conferenceId.value?.let { id ->
                    viewModelScope.launch {
                        postFeedbackFormUseCase(
                          sessionId = _sessionId.value,  conferenceId = id, selectedAnswers = _uiState.value.selectedAnswers
                        ).collect { state ->
                            _uiState.value =
                                _uiState.value.copy(isLoading = state is ApiStates.Loading)

                            _isFeedbackSubmitted.emit(state)
                        }
                    }
                }
            }

        }

    }


}

@Stable
@Immutable
data class FeedbackUiState(
    val isLoading: Boolean = false,
    val isEnabled: Boolean = false,
    val bestAspectsComment: String = "",
    val areaToImproveComment: String = "",
    val selectedAnswers: List<Pair<Statement, Option>> = emptyList()
)


sealed interface FeedbackUiEvents {
    data class OnAnswerSelected(val statement: Statement, val option: Option? = null, val comment: String? = null) : FeedbackUiEvents
     data object OnSubmitClicked : FeedbackUiEvents

}