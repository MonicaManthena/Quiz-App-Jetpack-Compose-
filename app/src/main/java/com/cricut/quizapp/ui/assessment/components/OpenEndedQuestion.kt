package com.cricut.quizapp.ui.assessment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.quizapp.data.model.Answer
import com.cricut.quizapp.data.model.Question
import com.cricut.quizapp.ui.theme.QuizAppTheme

/**
 * Renders an open-ended question with a text field and a live character counter.
 * Input is capped at [Question.OpenEnded.maxLength] characters.
 */
@Composable
fun OpenEndedQuestion(
    question: Question.OpenEnded,
    answer: Answer.OpenEndedAnswer?,
    onAnswerChanged: (Answer.OpenEndedAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = answer?.text ?: ""
    val remaining = question.maxLength - text.length

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                if (newText.length <= question.maxLength) {
                    onAnswerChanged(Answer.OpenEndedAnswer(newText))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Your answer") },
            singleLine = false,
            minLines = 2,
            maxLines = 4,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$remaining characters remaining",
            style = MaterialTheme.typography.bodySmall,
            color = if (remaining < 10) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OpenEndedQuestionPreview() {
    QuizAppTheme {
        OpenEndedQuestion(
            question = Question.OpenEnded(
                id = 4,
                text = "What build tool replaced Ant on Android?",
                maxLength = 100,
            ),
            answer = Answer.OpenEndedAnswer("Gradle"),
            onAnswerChanged = {},
        )
    }
}
