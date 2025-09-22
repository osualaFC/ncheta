package com.fredrickosuala.ncheta.android.features.paywall

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.android.navigation.AppHeader
import com.fredrickosuala.ncheta.features.paywall.AndroidPaywallViewModel
import com.fredrickosuala.ncheta.features.paywall.PaywallEvent
import com.fredrickosuala.ncheta.features.paywall.PaywallState
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PackageType
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaywallScreen(
    onPurchaseSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: AndroidPaywallViewModel = koinViewModel()
    val state by viewModel.payWallViewModel.state.collectAsState()
    val promoCode by viewModel.payWallViewModel.promoCode.collectAsState()
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
                is PaywallEvent.PromoError -> {
                    Toast.makeText(context, event.error, Toast.LENGTH_LONG).show()
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
                is PaywallState.Error -> {
                    Text(currentState.message, textAlign = TextAlign.Center)
                }
                is PaywallState.Success -> {
                    PaywallContent(
                        offerings = currentState.offerings,
                        promoCode = promoCode,
                        onPromoCodeChanged = { viewModel.payWallViewModel.onPromoCodeChanged(it) },
                        applyPromoCode = { viewModel.payWallViewModel.applyPromoCode() },
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
    offerings: List<Offering>,
    promoCode: String,
    onPromoCodeChanged: (String) -> Unit,
    applyPromoCode: () -> Unit,
    onPurchaseClicked: (Package) -> Unit
) {

    var selectedPackage by remember {
        mutableStateOf(offerings.map { it.availablePackages.firstOrNull() }.firstOrNull())
    }

    var promoSectionVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Go Premium",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
            FeatureItem(text = "Unlock cloud sync across all devices")
            FeatureItem(text = "Input text via audio")
            FeatureItem(text = "And lots more coming...")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            offerings.forEach { offering ->
                val pkg = offering.availablePackages.firstOrNull() ?: return@forEach
                PackageCard(
                    modifier = Modifier.weight(1f),
                    pkg = pkg,
                    isSelected = selectedPackage == pkg,
                    onClick = { selectedPackage = pkg }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { promoSectionVisible = !promoSectionVisible }
            ) {
                Checkbox(checked = promoSectionVisible, onCheckedChange = { promoSectionVisible = it })
                Text("I have a promo code")
            }

            AnimatedVisibility(visible = promoSectionVisible) {
                OutlinedTextField(
                    value = promoCode,
                    onValueChange = onPromoCodeChanged,
                    label = { Text("Enter code") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (promoSectionVisible) {
                    applyPromoCode()
                } else {
                    selectedPackage?.let { onPurchaseClicked(it) }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = if (promoSectionVisible) promoCode.isNotBlank() else selectedPackage != null
        ) {
            Text(if (promoSectionVisible) "Apply Code" else "Upgrade Now")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Payments are managed by the Google Play Store.",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FeatureItem(text: String, icon: ImageVector = Icons.Default.CheckCircle) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PackageCard(
    modifier: Modifier = Modifier,
    pkg: Package,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    OutlinedCard(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = pkg.packageType.toDisplayString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${pkg.storeProduct.price.currencyCode} ${pkg.storeProduct.price.formatted.removePrefix("$")}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

private fun PackageType.toDisplayString(): String {

    return when (this) {
        PackageType.MONTHLY -> "Monthly"
        PackageType.ANNUAL -> "Annual"
        PackageType.LIFETIME -> "Lifetime"
        else -> this.name.replaceFirstChar { it.titlecase() }
    }
}

@Preview(showBackground = true)
@Composable
fun PayWallPreview() {
    PaywallScreen({}) { }
}