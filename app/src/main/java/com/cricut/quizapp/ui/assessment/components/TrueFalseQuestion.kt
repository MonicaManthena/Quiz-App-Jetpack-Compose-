package com.cricut.quizapp.ui.assessment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
 * Renders a True / False question as two pill-shaped buttons.
 * The selected option receives the primary container colour; the
 * other remains outlined so the choice is immediately obvious.
 */
@Composable
fun TrueFalseQuestion(
    question: Question.TrueFalse,
    answer: Answer.TrueFalseAnswer?,
    onAnswerSelected: (Answer.TrueFalseAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        listOf(true, false).forEach { option ->
            val isSelected = answer?.value == option
            OutlinedButton(
                onClick = { onAnswerSelected(Answer.TrueFalseAnswer(option)) },
                modifier = Modifier
                    .weight(1f)
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
                    text = if (option) "True" else "False",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrueFalseQuestionPreview() {
    QuizAppTheme {
        TrueFalseQuestion(
            question = Question.TrueFalse(
                id = 1,
                text = "Kotlin is the official language for Android development.",
                correctAnswer = true,
            ),
            answer = Answer.TrueFalseAnswer(true),
            onAnswerSelected = {},
        )
    }
}
