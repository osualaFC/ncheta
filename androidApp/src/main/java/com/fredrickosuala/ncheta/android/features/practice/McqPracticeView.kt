package com.fredrickosuala.ncheta.android.features.practice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.data.model.GeneratedContent
import com.fredrickosuala.ncheta.features.practice.PracticeState

@Composable
fun McqPracticeView(
    practiceState: PracticeState,
    onSelectOption: (Int) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onRestart: () -> Unit
) {
    if (practiceState.isPracticeComplete) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("You've completed this set!", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRestart) {
                Text("Practice Again")
            }
        }
        return
    }

    val mcqSet = practiceState.entry.content as? GeneratedContent.McqSet
    val question = mcqSet?.items?.getOrNull(practiceState.currentQuestionIndex)

    if (question == null) {
        Text("Question not available.")
        return
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = question.questionText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            question.options.forEachIndexed { index, optionText ->
                val isSelected = practiceState.selectedOptionIndex == index
                val isCorrect = question.correctOptionIndex == index

                OptionRow(
                    text = optionText,
                    isSelected = isSelected,
                    isCorrect = isCorrect,
                    isAnswerRevealed = practiceState.isAnswerRevealed,
                    onClick = { onSelectOption(index) }
                )
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (practiceState.isAnswerRevealed) {
                Button(onClick = onNextQuestion) {
                    val isLastQuestion = practiceState.currentQuestionIndex == (mcqSet.items.size - 1)
                    Text(if (isLastQuestion) "Finish" else "Next Question")
                }
            } else {
                Button(
                    onClick = onCheckAnswer,
                    enabled = practiceState.selectedOptionIndex != null
                ) {
                    Text("Check Answer")
                }
            }
        }
    }
}

@Composable
private fun OptionRow(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswerRevealed: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isAnswerRevealed && isCorrect -> Color.Green.copy(alpha = 0.2f)
        isAnswerRevealed && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isAnswerRevealed && isCorrect -> Color.Green.copy(alpha = 0.8f)
        isAnswerRevealed && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.outline
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}