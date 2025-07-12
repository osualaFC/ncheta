package com.fredrickosuala.ncheta.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    data object Create : BottomNavItem("create", Icons.Default.Create, "Create")
    data object Entries : BottomNavItem("entries", Icons.AutoMirrored.Filled.List, "Entries")
}