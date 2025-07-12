package com.fredrickosuala.ncheta.android.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.android.R

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.4f)),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google logo",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "Sign in with Google",
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}