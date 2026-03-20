package com.cricut.quizapp

import app.cash.turbine.test
import com.cricut.quizapp.data.model.Answer
import com.cricut.quizapp.data.model.Question
import com.cricut.quizapp.data.repository.QuizRepository
import com.cricut.quizapp.ui.assessment.AssessmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Fake repository — no delay, deterministic data
    private val fakeRepository = object : QuizRepository {
        override suspend fun fetchQuestions() = listOf(
            Question.TrueFalse(id = 1, text = "Q1", correctAnswer = true),
            Question.MultipleChoice(
                id = 2, text = "Q2",
                options = listOf("A", "B", "C", "D"), correctIndex = 1,
            ),
            Question.OpenEnded(id = 3, text = "Q3"),
        )
    }

    private lateinit var viewModel: AssessmentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AssessmentViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // ViewModel emits loading=true before fetch completes
        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
    }

    @Test
    fun `questions loaded after fetch`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.questions.size)
    }

    @Test
    fun `navigateNext increments currentIndex`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.navigateNext()
        assertEquals(1, viewModel.uiState.value.currentIndex)
    }

    @Test
    fun `navigateBack decrements currentIndex`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.navigateNext()
        viewModel.navigateBack()
        assertEquals(0, viewModel.uiState.value.currentIndex)
    }

    @Test
    fun `navigateBack does nothing on first question`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.navigateBack()
        assertEquals(0, viewModel.uiState.value.currentIndex)
    }

    @Test
    fun `answer is preserved after navigating away and back`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val answer = Answer.TrueFalseAnswer(true)
        viewModel.onAnswerChanged(1, answer)
        viewModel.navigateNext()
        viewModel.navigateBack()
        assertEquals(answer, viewModel.uiState.value.answers[1])
    }

    @Test
    fun `isComplete set when navigating past last question`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        repeat(3) { viewModel.navigateNext() }
        assertTrue(viewModel.uiState.value.isComplete)
    }

    @Test
    fun `restartQuiz resets state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onAnswerChanged(1, Answer.TrueFalseAnswer(true))
        repeat(3) { viewModel.navigateNext() }
        viewModel.restartQuiz()
        val state = viewModel.uiState.value
        assertEquals(0, state.currentIndex)
        assertTrue(state.answers.isEmpty())
        assertFalse(state.isComplete)
    }

    @Test
    fun `hasAnsweredCurrent false when no answer given`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.hasAnsweredCurrent)
    }

    @Test
    fun `hasAnsweredCurrent true after answer given`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onAnswerChanged(1, Answer.TrueFalseAnswer(false))
        assertTrue(viewModel.uiState.value.hasAnsweredCurrent)
    }

    @Test
    fun `state flow emits on answer change`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            awaitItem() // current state
            viewModel.onAnswerChanged(1, Answer.TrueFalseAnswer(true))
            val updated = awaitItem()
            assertEquals(Answer.TrueFalseAnswer(true), updated.answers[1])
            cancelAndIgnoreRemainingEvents()
        }
    }
}
