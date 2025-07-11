package com.fredrickosuala.ncheta.android.features.practice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.data.model.GeneratedContent
import com.fredrickosuala.ncheta.features.practice.PracticeState

@Composable
fun FlashcardPracticeView(
    practiceState: PracticeState,
    onFlipCard: () -> Unit,
    onNextCard: () -> Unit,
    onRestart: () -> Unit
) {
    val flashcards = (practiceState.entry.content as? GeneratedContent.FlashcardSet)?.items ?: emptyList()
    if (flashcards.isEmpty()) {
        Text("No flashcards available.")
        return
    }

    val card = flashcards[practiceState.currentCardIndex]

    val rotation by animateFloatAsState(
        targetValue = if (practiceState.isCardFlipped) 180f else 0f,
        label = "cardRotation"
    )

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
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .graphicsLayer { rotationY = rotation }
                    .clickable { onFlipCard() }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                    val cardText = if (rotation < 90) card.front else card.back
                    Text(
                        text = cardText,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .graphicsLayer { rotationY = if (rotation < 90) 0f else 180f }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNextCard) {
                Text("Next Card")
            }
        }
    }

}