package com.cricut.quizapp.data.repository

import com.cricut.quizapp.data.model.Question
import kotlinx.coroutines.delay

/**
 * Mock repository that simulates an async data fetch (e.g. network or database).
 *
 * In a production app this interface would be backed by a Retrofit service or
 * Room DAO. The ViewModel depends only on this interface, making it trivial to
 * swap implementations or inject a fake in tests.
 */
interface QuizRepository {
    suspend fun fetchQuestions(): List<Question>
}

class QuizRepositoryImpl : QuizRepository {

    override suspend fun fetchQuestions(): List<Question> {
        // Simulate network latency so the loading state is observable during dev/demo.
        delay(600)

        return listOf(
            Question.TrueFalse(
                id = 1,
                text = "Kotlin is the official language for Android development.",
                correctAnswer = true,
            ),
            Question.MultipleChoice(
                id = 2,
                text = "Which of the following is the preferred programming language for Android app development?",
                options = listOf("Swift", "Kotlin", "Objective-C", "Java"),
                correctIndex = 1,
            ),
            Question.MultipleSelection(
                id = 3,
                text = "Which of the following are Jetpack Compose layout composables? (Select all that apply)",
                options = listOf("Column", "LinearLayout", "Row", "Box"),
                correctIndices = setOf(0, 2, 3),
            ),
            Question.OpenEnded(
                id = 4,
                text = "What is the name of the Android build system tool that replaced Ant?",
                maxLength = 100,
            ),
        )
    }
}
