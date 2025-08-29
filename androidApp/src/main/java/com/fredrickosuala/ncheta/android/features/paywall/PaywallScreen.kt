package com.fredrickosuala.ncheta.android.features.paywall

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.android.navigation.AppHeader
import com.fredrickosuala.ncheta.features.paywall.AndroidPaywallViewModel
import com.fredrickosuala.ncheta.features.paywall.PaywallEvent
import com.fredrickosuala.ncheta.features.paywall.PaywallState
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaywallScreen(
    onPurchaseSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: AndroidPaywallViewModel = koinViewModel()
    val state by viewModel.payWallViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.payWallViewModel.events.collectLatest { event ->
            when (event) {
                is PaywallEvent.RequestPurchase -> {
                    Purchases.sharedInstance.purchase(
                        packageToPurchase = event.pkg,
                        onError = { error, userCancelled ->

                            val message = if (userCancelled) "Purchase was cancelled." else error.message
                            Toast.makeText(context, "Purchase failed: $message", Toast.LENGTH_LONG).show()

                        },
                        onSuccess = { storeTransaction, customerInfo ->
                            viewModel.payWallViewModel.onPurchaseSuccess()

                            if (customerInfo.entitlements.all["premium"]?.isActive == true) {
                                onPurchaseSuccess()
                            }
                        }
                    )
                }
            }
        }
    }


    Column {
        AppHeader(
            title = "Upgrade to Premium"
        ) {
            onNavigateBack()
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is PaywallState.Loading -> CircularProgressIndicator()
                is PaywallState.Error -> Text(currentState.message, textAlign = TextAlign.Center)
                is PaywallState.Success -> {
                    PaywallContent(
                        offering = currentState.offering,
                        onPurchaseClicked = { pkg ->
                            viewModel.payWallViewModel.onPurchaseClicked(pkg)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallContent(
    offering: Offering,
    onPurchaseClicked: (Package) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Go Premium",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Unlock cloud sync to access your entries on all your devices.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Display each package (e.g., monthly, annual) as a button
        offering.availablePackages.forEach { pkg ->
            Button(
                onClick = { onPurchaseClicked(pkg) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${pkg.storeProduct.title} - ${pkg.storeProduct.price.formatted}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}