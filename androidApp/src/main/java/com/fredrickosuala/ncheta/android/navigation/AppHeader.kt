package com.fredrickosuala.ncheta.android.navigation


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AppHeader(
    title: String,
    modifier: Modifier = Modifier,
    showBackArrow: Boolean = true,
    onBackArrowClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackArrow) {
            IconButton(onClick = onBackArrowClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Text(
            title,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            fontWeight = FontWeight.Bold,
            textAlign = if (showBackArrow) TextAlign.Start else TextAlign.Center
        )
    }
}