import React from 'react';
import { BarcodeFormat, detectBarcodes } from 'react-native-barcodes-detector';
import { launchImageLibrary } from 'react-native-image-picker';

export default function App() {
  React.useEffect(() => {
    const exec = async () => {
      try {
        const selectedImage = await launchImageLibrary({ mediaType: 'photo' });
        const nextImageUri = selectedImage?.assets?.[0].uri || null;

        if (nextImageUri !== null) {
          console.log(nextImageUri);
          const result = await detectBarcodes(nextImageUri, [
            BarcodeFormat.QR_CODE,
          ]);
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
