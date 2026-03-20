package com.cricut.quizapp.data.model

/**
 * Sealed class hierarchy representing all supported quiz question types.
 * Adding a new type is as simple as adding a new subclass here and handling
 * it in the UI switch expressions.
 */
sealed class Question {
    abstract val id: Int
    abstract val text: String

    /**
     * A statement the user judges as true or false.
     * e.g. "Kotlin is the official language for Android development."
     */
    data class TrueFalse(
        override val id: Int,
        override val text: String,
        val correctAnswer: Boolean,
    ) : Question()

    /**
     * Four options, exactly one correct answer.
     */
    data class MultipleChoice(
        override val id: Int,
        override val text: String,
        val options: List<String>,
        val correctIndex: Int,
    ) : Question()

    /**
     * Four options, one or more correct answers.
     */
    data class MultipleSelection(
        override val id: Int,
        override val text: String,
        val options: List<String>,
        val correctIndices: Set<Int>,
    ) : Question()

    /**
     * Free-text response with an optional character cap.
     */
    data class OpenEnded(
        override val id: Int,
        override val text: String,
        val maxLength: Int = 100,
    ) : Question()
}
