package com.cricut.quizapp.ui.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cricut.quizapp.data.model.Answer
import com.cricut.quizapp.data.model.QuizState
import com.cricut.quizapp.data.repository.QuizRepository
import com.cricut.quizapp.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Survives configuration changes (rotation, multi-window resize) because it lives
 * outside the Activity/Fragment lifecycle.  All mutable state is held in a
 * [StateFlow] so the UI reacts automatically to every change.
 *
 * Navigation and answer recording are pure state mutations — no side-effects leak
 * into the UI layer.
 */
class AssessmentViewModel(
    private val repository: QuizRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizState(isLoading = true))
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    // -------------------------------------------------------------------------
    // Data loading
    // -------------------------------------------------------------------------

    private fun loadQuestions() {
        viewModelScope.launch {
            val questions = repository.fetchQuestions()
            _uiState.update { it.copy(questions = questions, isLoading = false) }
        }
    }

    // -------------------------------------------------------------------------
    // Answer recording
    // -------------------------------------------------------------------------

    /**
     * Called by the UI whenever the user selects or types an answer.
     * The answer is stored by question-id so back-navigation always retrieves
     * the correct prior selection regardless of list reordering.
     */
    fun onAnswerChanged(questionId: Int, answer: Answer) {
        _uiState.update { state ->
            state.copy(answers = state.answers + (questionId to answer))
        }
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    fun navigateNext() {
        _uiState.update { state ->
            when {
                state.isLastQuestion -> state.copy(isComplete = true)
                else -> state.copy(currentIndex = state.currentIndex + 1)
            }
        }
    }

    fun navigateBack() {
        _uiState.update { state ->
            if (!state.isFirstQuestion) {
                state.copy(currentIndex = state.currentIndex - 1)
            } else {
                state
            }
        }
    }

    fun restartQuiz() {
        _uiState.update { state ->
            state.copy(
                currentIndex = 0,
                answers = emptyMap(),
                isComplete = false,
            )
        }
    }

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    companion object {
        /**
         * Provides a [ViewModelProvider.Factory] that injects the default
         * [QuizRepositoryImpl].  In tests, supply a fake repository directly.
         */
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AssessmentViewModel(QuizRepositoryImpl()) as T
            }
        }
    }
}
