package com.fredrickosuala.ncheta.android.features.paywall

import android.app.Activity
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
import com.fredrickosuala.ncheta.features.paywall.AndroidPaywallViewModel
import com.fredrickosuala.ncheta.features.paywall.PaywallEvent
import com.fredrickosuala.ncheta.features.paywall.PaywallState
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.k.Purchases
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaywallScreen(
    onPurchaseSuccess: () -> Unit
) {
    val viewModel: AndroidPaywallViewModel = koinViewModel()
    val state by viewModel.payWallViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.payWallViewModel.events.collectLatest { event ->
            when (event) {
                is PaywallEvent.RequestPurchase -> {
                    try {
                        val purchaseResult = Purchases.sharedInstance.purchase(
                            purchaseParams = PurchaseParams.Builder().,
                        ) {}
                        viewModel.payWallViewModel.onPurchaseCompleted(purchaseResult)

                        // If purchase was successful, check entitlements
                        if (purchaseResult.customerInfo.entitlements.all["premium"]?.isActive == true) {
                            onPurchaseSuccess()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Purchase failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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
        Text("Go Premium", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
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
                Text("${pkg.product.title} - ${pkg.product.price.formatted}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}