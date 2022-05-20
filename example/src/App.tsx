import React from 'react';
import { scan } from 'react-native-barcodes-detector';
import { launchImageLibrary } from 'react-native-image-picker';

export default function App() {
  React.useEffect(() => {
    const exec = async () => {
      try {
        const selectedImage = await launchImageLibrary({ mediaType: 'photo' });
        const nextImageUri = selectedImage?.assets?.[0].uri || null;

        if (nextImageUri !== null) {
          console.log(nextImageUri);
          const result = await scan(nextImageUri);
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
