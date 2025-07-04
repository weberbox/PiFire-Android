package com.weberbox.pifire.setup.presentation.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.common.presentation.util.showToast
import com.weberbox.pifire.setup.presentation.contract.ConnectContract

internal fun isCameraAvailable(context: Context): Boolean {
    return context.packageManager.hasSystemFeature(
        PackageManager.FEATURE_CAMERA_ANY
    )
}

internal fun isGooglePlayServicesAvailable(context: Context): Boolean {
    return GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
}

internal fun openQRCodeScanner(
    activity: Activity?,
    onEventSent: (event: ConnectContract.Event) -> Unit,
) {
    activity?.also {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        val scanner = GmsBarcodeScanning.getClient(activity, options)
        scanner.startScan()
            .addOnSuccessListener { barcode: Barcode ->
                if (barcode.valueType == Barcode.TYPE_URL) {
                    barcode.url?.url?.let {
                        onEventSent(ConnectContract.Event.ValidateAddress(it))
                    } ?: activity.showAlerter(
                        message = UiText(R.string.setup_scan_qr_failed),
                        isError = true
                    )
                } else {
                    activity.showAlerter(
                        message = UiText(R.string.setup_qr_invalid_url)
                    )
                }
            }
            .addOnCanceledListener {
                activity.showToast(R.string.setup_scan_qr_canceled)
            }
            .addOnFailureListener {
                activity.showToast(R.string.setup_scan_qr_failed)
            }
    }
}