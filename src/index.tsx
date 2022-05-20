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
 * Data structure describing a single Barcode
 * detected in an image.
 */
export type Barcode = {
  format: number;
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
 * @returns A promise resolving to a list of `Barcode` detected in the image.
 */
export function scan(imageUrl: string): Promise<Array<Barcode>> {
  return BarcodesDetector.scan(imageUrl);
}
