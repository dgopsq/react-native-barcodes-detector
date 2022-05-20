import MLKitVision
import MLKitBarcodeScanning
import Foundation

@objc(BarcodesDetector)
class BarcodesDetector: NSObject {

    /*
    Main function used to scan all the barcodes inside
    the given image. This uses Google MLKit under the hood.
    */
    @objc(scan:withResolver:withRejecter:)
    func scan(imageUrl: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        let format = BarcodeFormat.all
        let barcodeOptions = BarcodeScannerOptions(formats: format)

        let sanitizedimageUrl = sanitizePath(imageUrl: imageUrl)
        let fileExists = FileManager.default.fileExists(atPath: sanitizedimageUrl)

        if !fileExists {
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

        barcodeScanner.process(visionImage) { barcodes, error in
            guard error == nil, let barcodes = barcodes else {
                reject("detection_error", "Error while detecting the barcodes", nil);
                return
            }

            let transformedBarcodes = self.transformBarcodes(barcodes: barcodes)

            resolve(transformedBarcodes)
        }
    }

    /*
    Sanitize the given image path removing redundant
    characters like the `file://` prefix.
    */
    func sanitizePath(imageUrl: String) -> String {
        var sanitized = String(imageUrl)

        // Remove the `file://` prefix if it exists.
        if (sanitized.hasPrefix("file://")) {
            sanitized = String(sanitized.dropFirst(7))
        }
        
        return sanitized
    }

    /*
    Transform the array of `Barcode` into an array of
    `NSMutableDictionary` which can be sent to React Native.
    */
    func transformBarcodes(barcodes: [Barcode]) -> [NSMutableDictionary] {
        var result: [NSMutableDictionary] = []

        for barcode in barcodes {
            let barcodeDict: NSMutableDictionary = [:]
            
            barcodeDict["format"] = barcode.format.rawValue
            barcodeDict["rawValue"] = barcode.rawValue
            barcodeDict["displayValue"] = barcode.displayValue
            barcodeDict["cornerPoints"] = transformPoints(points: barcode.cornerPoints ?? [])

            result.append(barcodeDict)
        }

        return result
    }

    /*
    Transform an array of `NSValue` with a `CGPoint` inside into
    an array of `NSMutableDictionary` which can be sent to React Native.
    */
    func transformPoints(points: [NSValue]) -> [NSMutableDictionary] {
        return points.map { rawPoint in
            let point = rawPoint.cgPointValue
            let pointDict: NSMutableDictionary = [:]

            pointDict["x"] = point.x
            pointDict["y"] = point.y

            return pointDict
        }
    }
    
}
