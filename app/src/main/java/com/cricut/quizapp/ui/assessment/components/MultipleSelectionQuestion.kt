package com.cricut.quizapp.ui.assessment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.quizapp.data.model.Answer
import com.cricut.quizapp.data.model.Question
import com.cricut.quizapp.ui.theme.QuizAppTheme

/**
 * Renders a multiple-selection question where one or more options may be chosen.
 * Each option is a tappable card with a leading checkbox for clear affordance.
 */
@Composable
fun MultipleSelectionQuestion(
    question: Question.MultipleSelection,
    answer: Answer.MultipleSelectionAnswer?,
    onAnswerChanged: (Answer.MultipleSelectionAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndices = answer?.selectedIndices ?: emptySet()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        question.options.forEachIndexed { index, option ->
            val isSelected = index in selectedIndices
            OutlinedCard(
                onClick = {
                    val newSet = if (isSelected) {
                        selectedIndices - index
                    } else {
                        selectedIndices + index
                    }
                    onAnswerChanged(Answer.MultipleSelectionAnswer(newSet))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null, // handled by the card click
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MultipleSelectionQuestionPreview() {
    QuizAppTheme {
        MultipleSelectionQuestion(
            question = Question.MultipleSelection(
                id = 3,
                text = "Which are Compose layout composables?",
                options = listOf("Column", "LinearLayout", "Row", "Box"),
                correctIndices = setOf(0, 2, 3),
            ),
            answer = Answer.MultipleSelectionAnswer(setOf(0, 2)),
            onAnswerChanged = {},
        )
    }
}
