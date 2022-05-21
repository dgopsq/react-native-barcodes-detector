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
      var formatsArray = getFormats(formats)

      val optionsBuilder = BarcodeScannerOptions.Builder()

      if (formatsArray.size > 0) {
        optionsBuilder.setBarcodeFormats(
          formatsArray[0], 
          *formatsArray.drop(1).toIntArray()
        )
      }

      try {
        val uri = Uri.parse(imageUrl)
        val image = InputImage.fromFilePath(ctx, uri)

        val scanner = BarcodeScanning.getClient(optionsBuilder.build())

        scanner.process(image)
          .addOnSuccessListener { barcodes ->
            val transformedBarcodes = transformBarcodes(barcodes)

            promise.resolve(transformedBarcodes)
          }
          .addOnFailureListener {
            promise.reject("detection_error", "Error while detecting the barcodes")
          }
          .addOnCompleteListener {
            scanner.close()
          }
      } catch (_: Throwable) {
        promise.reject("image_load_error", "Couldn't load the image")
      }
    }

    fun getFormats(array: ReadableArray): Array<Int> {
      val result = mutableListOf<Int>()
      
      for (i in 0..array.size()) {
        val value = array.getInt(i)
        result.add(value)
      }

      return result.toTypedArray()
    }

    fun transformBarcodes(barcodes: List<Barcode>): WritableArray {
      val result = Arguments.createArray()
      
      for (barcode in barcodes) {
        result.pushString(barcode.rawValue)
      }

      return result
    }
    
}
