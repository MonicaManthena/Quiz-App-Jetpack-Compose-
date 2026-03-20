package com.cricut.quizapp.data.model

/**
 * Sealed hierarchy of typed answers, one subtype per [Question] subtype.
 * Stored in a Map<questionId, Answer> so back-navigation always restores
 * the exact prior selection without any index-coupling fragility.
 */
sealed class Answer {
    data class TrueFalseAnswer(val value: Boolean?) : Answer()
    data class MultipleChoiceAnswer(val selectedIndex: Int?) : Answer()
    data class MultipleSelectionAnswer(val selectedIndices: Set<Int> = emptySet()) : Answer()
    data class OpenEndedAnswer(val text: String = "") : Answer()
}

/**
 * Snapshot of everything the UI needs to render correctly.
 *
 * @param questions      Ordered list of questions loaded from the repository.
 * @param currentIndex   Which question is currently visible (0-based).
 * @param answers        Map of question-id → user's current answer. Absent keys
 *                       mean the question hasn't been touched yet.
 * @param isLoading      True while the repository fetch is in-flight.
 * @param isComplete     True after the user submits the final question.
 */
data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<Int, Answer> = emptyMap(),
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isFirstQuestion: Boolean get() = currentIndex == 0
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    val progress: Float
        get() = if (questions.isEmpty()) 0f else (currentIndex + 1).toFloat() / questions.size
    val currentAnswer: Answer? get() = currentQuestion?.let { answers[it.id] }
    val hasAnsweredCurrent: Boolean
        get() = when (val a = currentAnswer) {
            is Answer.TrueFalseAnswer -> a.value != null
            is Answer.MultipleChoiceAnswer -> a.selectedIndex != null
            is Answer.MultipleSelectionAnswer -> a.selectedIndices.isNotEmpty()
            is Answer.OpenEndedAnswer -> a.text.isNotBlank()
            null -> false
        }
}
