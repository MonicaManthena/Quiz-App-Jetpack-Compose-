package com.cricut.quizapp.ui.assessment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.quizapp.data.model.Answer
import com.cricut.quizapp.data.model.Question
import com.cricut.quizapp.ui.theme.QuizAppTheme

/**
 * Renders a multiple-choice question as a vertical list of option buttons.
 * Only one option can be selected at a time; selecting a new option
 * automatically deselects the previous one.
 */
@Composable
fun MultipleChoiceQuestion(
    question: Question.MultipleChoice,
    answer: Answer.MultipleChoiceAnswer?,
    onAnswerSelected: (Answer.MultipleChoiceAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        question.options.forEachIndexed { index, option ->
            val isSelected = answer?.selectedIndex == index
            OutlinedButton(
                onClick = { onAnswerSelected(Answer.MultipleChoiceAnswer(index)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MultipleChoiceQuestionPreview() {
    QuizAppTheme {
        MultipleChoiceQuestion(
            question = Question.MultipleChoice(
                id = 2,
                text = "Which language is preferred for Android?",
                options = listOf("Swift", "Kotlin", "Objective-C", "Java"),
                correctIndex = 1,
            ),
            answer = Answer.MultipleChoiceAnswer(1),
            onAnswerSelected = {},
        )
    }
}
