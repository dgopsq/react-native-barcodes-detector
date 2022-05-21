import React from 'react';
import { BarcodeFormat, detectBarcodes } from 'react-native-barcodes-detector';
import { launchImageLibrary } from 'react-native-image-picker';

export default function App() {
  React.useEffect(() => {
    const exec = async () => {
      try {
        // Select an image through `react-native-image-picker`.
        const selectedImage = await launchImageLibrary({ mediaType: 'photo' });

        // Get the image url to process.
        const nextImageUri = selectedImage?.assets?.[0].uri || null;

        // Narrow down the list of barcodes to detect.
        // This will improve the performance of the process.
        const formats = [BarcodeFormat.QR_CODE];

        if (nextImageUri !== null) {
          // Execute the detection process.
          const result = await detectBarcodes(nextImageUri, formats);

          // Log the array of barcodes obtained.
          console.log(result);
        }
      } catch (e) {
        console.error(e);
      }
    };

    exec();
  }, []);

  return null;
}
