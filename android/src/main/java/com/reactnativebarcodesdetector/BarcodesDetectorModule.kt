package com.reactnativebarcodesdetector

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.Arguments
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import android.net.Uri

class BarcodesDetectorModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    val ctx: ReactApplicationContext

    init {
      ctx = reactContext
    }

    override fun getName(): String {
        return "BarcodesDetector"
    }

    @ReactMethod
    fun scan(imageUrl: String, formats: ReadableArray, promise: Promise) {
      val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
          Barcode.FORMAT_QR_CODE,
          Barcode.FORMAT_AZTEC
        )
        .build()

      try {
          val uri = Uri.parse(imageUrl)
          val image = InputImage.fromFilePath(ctx, uri)

          val scanner = BarcodeScanning.getClient()

          val result = scanner.process(image)
            .addOnSuccessListener { barcodes ->
              val transformedBarcodes = transformBarcodes(barcodes)

              promise.resolve(transformedBarcodes)
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
      } catch (e: IOException) {
          e.printStackTrace()
      }
    }

    fun transformBarcodes(barcodes: List<Barcode>): WritableArray {
      val result = Arguments.createArray()
      
      for (barcode in barcodes) {
        result.pushString(barcode.rawValue)
      }

      return result
    }
    
}
