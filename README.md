![Header](https://raw.githubusercontent.com/dgopsq/react-native-barcodes-detector/main/assets/header.jpg?token=GHSAT0AAAAAABQMUFRBM6B4EL3TLJB5CYZSYUI45HQ)

Detect different barcodes in static images directly on React Native. This library uses [Google ML Kit](https://developers.google.com/ml-kit) under the hood 🤖.

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

// The `detectBarcode` function needs the image url and
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
