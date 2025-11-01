package com.example.coffepet

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class LectorQr(
    private val onQrDetectado: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { codigos ->
                for (codigo in codigos) {
                    codigo.rawValue?.let { valor ->
                        Log.d("CoffeePetQR", "Código detectado: $valor")
                        onQrDetectado(valor)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CoffeePetQR", "Error al escanear", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
