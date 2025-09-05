package com.fredrickosuala.ncheta.android.features.input

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.android.R
import com.fredrickosuala.ncheta.domain.audio.AudioRecorderState

@Composable
internal fun VoiceRecordingIndicator(
    audioState: AudioRecorderState,
    context: Context
) {

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse_scale"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (audioState) {
            is AudioRecorderState.Recording -> {
                Icon(
                    painter = painterResource(R.drawable.ic_mic),
                    contentDescription = "Listening",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(pulseScale)
                )
                Text("Recording...", style = MaterialTheme.typography.bodyMedium)
            }
            is AudioRecorderState.Success -> {
                Toast.makeText(context, "Recording complete!", Toast.LENGTH_SHORT).show()
            }
            is AudioRecorderState.Error -> {
                Toast.makeText(context, "Error: ${audioState.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}


@Composable
fun ColorScheme.surfaceColorAtElevation(elevation: Dp): Color {
    return this.surface.copy(alpha = 1f)
}