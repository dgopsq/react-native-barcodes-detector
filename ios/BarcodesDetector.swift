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
    func scan(imageUrl: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        let format = BarcodeFormat.all
        let barcodeOptions = BarcodeScannerOptions(formats: format)

        let sanitizedimageUrl = sanitizePath(imageUrl: imageUrl)
        let fileExists = FileManager.default.fileExists(atPath: sanitizedimageUrl)

        if (!fileExists) {
            reject("image_not_found", "The image has not been found", nil);
            return
        }

        guard let url = NSURL(string: "file://" + sanitizedimageUrl) else {
            reject("malformed_url", "Malformed image URL", nil);
            return
        }

        guard let data = NSData(contentsOf: url as URL), let image = UIImage(data: data as Data) else {
            reject("image_load_error", "Couldn't load the image", nil);
            return
        }

        let visionImage = VisionImage(image: image)
        visionImage.orientation = image.imageOrientation

        let barcodeScanner = BarcodeScanner.barcodeScanner(options: barcodeOptions)

        barcodeScanner.process(visionImage) { features, error in
            guard error == nil, let features = features, !features.isEmpty else {
                reject("no_barcodes_found", "No barcodes has been found", nil);
                return
            }
            
            resolve(0)
        }
    }

    func sanitizePath(imageUrl: String) -> String {
        var sanitized = String(imageUrl)

        // Remove the `file://` prefix if it exists.
        if (sanitized.hasPrefix("file://")) {
            sanitized = String(sanitized.dropFirst(7))
        }
        
        return sanitized
    }
}
