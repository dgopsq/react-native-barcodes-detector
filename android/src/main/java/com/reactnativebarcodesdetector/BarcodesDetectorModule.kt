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

    /**
     * Main function used to scan all the barcodes inside
     * the given image. It's possible to define a list of
     * `BarcodeFormat` in order to reduce the detection
     * time, since by default it detects all the supported 
     * barcodes. This uses Google MLKit under the hood.
     */
    @ReactMethod
    fun detectBarcodes(imageUrl: String, formats: ReadableArray, promise: Promise) {
      var formatsArray = getFormats(formats)
      val sanitizedimageUrl = sanitizePath(imageUrl)
      val optionsBuilder = BarcodeScannerOptions.Builder()

      if (formatsArray.size > 0) {
        val additionalFormats = 
          if (formatsArray.size > 1) formatsArray.drop(1).toIntArray() else IntArray(0)

        optionsBuilder.setBarcodeFormats(
          formatsArray[0], 
          *additionalFormats
        )
      }

      try {
        val uri = Uri.parse(sanitizedimageUrl)
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

    /**
     * Sanitize the given image path adding the 
     * required `file://` protocol when needed.
     */
    fun sanitizePath(imageUrl: String): String {
      var sanitized = imageUrl

      if (!imageUrl.startsWith("file://")) {
        sanitized = "file://" + sanitized
      }

      return sanitized
    }

    /**
     * Transform a `ReadableArray` reveived from React Native
     * into an Array of Int representing the various barcodes
     * formats to detect.
     */
    fun getFormats(array: ReadableArray): Array<Int> {
      val result = mutableListOf<Int>()
      
      for (i in 0..array.size() - 1) {
        val value = array.getInt(i)
        result.add(value)
      }

      return result.toTypedArray()
    }

    /**
     * Transform a `List` of `Barcode` into a `WritableArray` which
     * can be passed to the React Native context as the result
     * of the detection process.
     */
    fun transformBarcodes(barcodes: List<Barcode>): WritableArray {
      val result = Arguments.createArray()
      
      for (barcode in barcodes) {
        val barcodeMap = Arguments.createMap();

        val cornerPoints = barcode.getCornerPoints()
        val cornerPointsArray = Arguments.createArray()

        barcodeMap.putInt("format", barcode.getFormat())
        barcodeMap.putString("rawValue", barcode.getRawValue())
        barcodeMap.putString("displayValue", barcode.getDisplayValue())

        if (cornerPoints != null) {
          for (point in cornerPoints) {
            val pointMap = Arguments.createMap();

            pointMap.putInt("x", point.x)
            pointMap.putInt("y", point.y)

            cornerPointsArray.pushMap(pointMap)
          }
        }

        barcodeMap.putArray("cornerPoints", cornerPointsArray)

        result.pushMap(barcodeMap)
      }

      return result
    }
    
}
