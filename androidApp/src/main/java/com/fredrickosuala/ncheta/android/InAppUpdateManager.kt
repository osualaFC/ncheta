package com.fredrickosuala.ncheta.android

import android.app.Activity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val UPDATE_REQUEST_CODE = 1234


@Composable
fun InAppUpdateManager(
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val appUpdateManager = remember { AppUpdateManagerFactory.create(context) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                    if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        activity?.let {
                            appUpdateManager.startUpdateFlowForResult(
                                info,
                                AppUpdateType.FLEXIBLE,
                                it,
                                UPDATE_REQUEST_CODE
                            )
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    DisposableEffect(Unit) {
        val listener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "A new version is ready to be installed.",
                        actionLabel = "Restart",
                        duration = SnackbarDuration.Indefinite
                    )
                    if (result != SnackbarResult.ActionPerformed) {
                        delay(60_000) // auto apply after 1 min
                        appUpdateManager.completeUpdate()
                    } else {
                        appUpdateManager.completeUpdate()
                    }
                }
            }
        }
        appUpdateManager.registerListener(listener)
        onDispose { appUpdateManager.unregisterListener(listener) }
    }

    LaunchedEffect(Unit) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isFlexibleUpdateAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            if (isUpdateAvailable && isFlexibleUpdateAllowed) {
                activity?.let {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        AppUpdateType.FLEXIBLE,
                        it,
                        UPDATE_REQUEST_CODE
                    )
                }
            }
        }
    }
}
