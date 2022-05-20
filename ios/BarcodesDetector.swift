import MLKitVision
import MLKitBarcodeScanning
import Foundation

@objc(BarcodesDetector)
class BarcodesDetector: NSObject {

    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }

    @objc(scan:withResolver:withRejecter:)
    func scan(imageUri: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        let format = BarcodeFormat.all
        let barcodeOptions = BarcodeScannerOptions(formats: format)

        let sanitizedImageUri = sanitizePath(imageUri: imageUri)
        let fileExists = FileManager.default.fileExists(atPath: sanitizedImageUri)

        if (!fileExists) {
            reject("image_not_found", "The image has not been found", nil);
            return
        }

        let url = NSURL(string: "file://" + sanitizedImageUri)
        let data = NSData(contentsOf: url! as URL)
        let image = UIImage(data: data! as Data)

        let visionImage = VisionImage(image: image!)
        visionImage.orientation = image!.imageOrientation

        let barcodeScanner = BarcodeScanner.barcodeScanner(options: barcodeOptions)

        barcodeScanner.process(visionImage) { features, error in
            guard error == nil, let features = features, !features.isEmpty else {
                reject("no_barcodes_found", "No barcodes has been found", nil);
                return
            }
            
            resolve(0)
        }
    }

    func sanitizePath(imageUri: String) -> String {
        var sanitized = String(imageUri)

        // Remove the `file://` prefix if it exists.
        if (sanitized.hasPrefix("file://")) {
            sanitized = String(sanitized.dropFirst(7))
        }
        
        return sanitized
    }
}
