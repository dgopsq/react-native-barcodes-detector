![Header](https://github.com/dgopsq/react-native-barcodes-detector/blob/main/assets/header.jpg)

Detect different barcodes in static images directly on React Native. This library has a [TypeScript](https://www.typescriptlang.org/)-first support and uses [Google ML Kit](https://developers.google.com/ml-kit) under the hood 🤖.

The **supported barcodes** are:
- QR Code
- Data Matrix
- EAN13
- [And more...](https://www.typescriptlang.org/)
## 🏗 Installation

```sh
# NPM
npm install react-native-barcodes-detector

# Yarn
yarn add react-native-barcodes-detector
```

## ⚡️ Usage

Take a look at the [example](https://github.com/dgopsq/react-native-barcodes-detector/blob/main/example/src/App.tsx) to see a use-case with [react-native-image-picker](https://github.com/react-native-image-picker/react-native-image-picker) (it's really short, I promise 🙏).

Anyway, this is the tl;dr:

```js
import { detectBarcodes, BarcodeFormat } from "react-native-barcodes-detector";

// This is the local image url usually retrieved
// through libraries like `react-native-image-picker`.
const imageUrl = "file://..."

// The `detectBarcodes` function needs the image url and
// a list of formats to detect. Using an empty array all
// the supported formats will be used making the detection
// process slower 🐌.
const formats = [BarcodeFormat.QR_CODE]

detectBarcodes(imageUrl, formats)
  .then(barcodes => {
    // Here `barcodes` will contain a list
    // of detected barcodes.
  })
```

## 🙋 FAQ

### Where is the detection process actually done?
This library uses [Google ML Kit](https://developers.google.com/ml-kit) to recognize barcodes, and this process is **completely done on the physical device**.

### Why I can't detect a Data Matrix code?
For a Data Matrix to be recognized it must intersect the center of the image, as stated [here](https://developers.google.com/ml-kit/vision/barcode-scanning/android#1.-configure-the-barcode-scanner). This means that only one Data Matrix code can be recognized per image.

## ⚖️ License
React Native Barcodes Detector is under the [MIT license](https://github.com/dgopsq/react-native-barcodes-detector/blob/main/LICENSE).
