import { NativeModules, Platform } from 'react-native';

/**
 * Data structure describing the `x` and `y` position
 * of a point on a 2D plane.
 */
export type Point = {
  x: number;
  y: number;
};

/**
 * Enum representing a Barcode Format and
 * its identification number.
 */
export enum BarcodeFormat {
  UNKNOWN = -1,
  ALL_FORMATS = 0,
  CODE_128 = 1,
  CODE_39 = 2,
  CODE_93 = 4,
  CODABAR = 8,
  DATA_MATRIX = 16,
  EAN_13 = 32,
  EAN_8 = 64,
  ITF = 128,
  QR_CODE = 256,
  UPC_A = 512,
  UPC_E = 1024,
  PDF417 = 2048,
  AZTEC = 4096,
}

/**
 * Data structure describing a single Barcode
 * detected in an image.
 */
export type Barcode = {
  format: BarcodeFormat;
  rawValue: string | null;
  displayValue: string | null;
  cornerPoints: Array<Point>;
};

const LINKING_ERROR =
  `The package 'react-native-barcodes-detector' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const BarcodesDetector = NativeModules.BarcodesDetector
  ? NativeModules.BarcodesDetector
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * Detect all the barcodes in the given image.
 *
 * @param imageUrl The url to the local image used in the detection process. This could be either with the `file://` protocol or without it.
 * @param formats An array of `BarcodeFormat` to detect. Passing an empty array is the same as passing `Barcode.ALL_FORMATS`.
 * @returns A promise resolving to a list of `Barcode` detected in the image.
 */
export function detectBarcodes(
  imageUrl: string,
  formats: Array<BarcodeFormat>
): Promise<Array<Barcode>> {
  return BarcodesDetector.detectBarcodes(imageUrl, formats);
}
