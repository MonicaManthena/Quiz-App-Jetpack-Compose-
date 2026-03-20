package com.cricut.quizapp.ui.assessment

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.quizapp.data.model.Answer
import com.cricut.quizapp.data.model.Question
import com.cricut.quizapp.ui.assessment.components.MultipleChoiceQuestion
import com.cricut.quizapp.ui.assessment.components.MultipleSelectionQuestion
import com.cricut.quizapp.ui.assessment.components.OpenEndedQuestion
import com.cricut.quizapp.ui.assessment.components.QuizCompleteScreen
import com.cricut.quizapp.ui.assessment.components.TrueFalseQuestion

/**
 * Root screen composable for the quiz assessment.
 *
 * Responsibilities:
 * - Observes [AssessmentViewModel.uiState] and re-renders on every change.
 * - Delegates question rendering to focused child composables.
 * - Forwards user interactions back to the ViewModel as events.
 *
 * The ViewModel is scoped to the NavBackStackEntry (or Activity if no nav graph
 * is used), so it survives configuration changes automatically.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    modifier: Modifier = Modifier,
    viewModel: AssessmentViewModel = viewModel(factory = AssessmentViewModel.factory()),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Android Quiz") })
        },
        modifier = modifier,
    ) { innerPadding ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.isComplete -> {
                QuizCompleteScreen(
                    onRestart = viewModel::restartQuiz,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            state.currentQuestion != null -> {
                QuizContent(
                    state = state,
                    onAnswerChanged = viewModel::onAnswerChanged,
                    onNext = viewModel::navigateNext,
                    onBack = viewModel::navigateBack,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun QuizContent(
    state: com.cricut.quizapp.data.model.QuizState,
    onAnswerChanged: (Int, Answer) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Progress bar
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth(),
        )

        // Question counter
        Text(
            text = "Question ${state.currentIndex + 1} of ${state.questions.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        // Animated question area
        AnimatedContent(
            targetState = state.currentIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "question_transition",
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) { index ->
            val question = state.questions.getOrNull(index) ?: return@AnimatedContent
            val answer = state.answers[question.id]

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(32.dp))

                when (question) {
                    is Question.TrueFalse -> TrueFalseQuestion(
                        question = question,
                        answer = answer as? Answer.TrueFalseAnswer,
                        onAnswerSelected = { onAnswerChanged(question.id, it) },
                    )
                    is Question.MultipleChoice -> MultipleChoiceQuestion(
                        question = question,
                        answer = answer as? Answer.MultipleChoiceAnswer,
                        onAnswerSelected = { onAnswerChanged(question.id, it) },
                    )
                    is Question.MultipleSelection -> MultipleSelectionQuestion(
                        question = question,
                        answer = answer as? Answer.MultipleSelectionAnswer,
                        onAnswerChanged = { onAnswerChanged(question.id, it) },
                    )
                    is Question.OpenEnded -> OpenEndedQuestion(
                        question = question,
                        answer = answer as? Answer.OpenEndedAnswer,
                        onAnswerChanged = { onAnswerChanged(question.id, it) },
                    )
                }
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!state.isFirstQuestion) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                ) {
                    Text("Back")
                }
            }

            Button(
                onClick = onNext,
                enabled = state.hasAnsweredCurrent,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) {
                Text(if (state.isLastQuestion) "Submit" else "Next Question")
            }
        }
    }
}
