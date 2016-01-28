package com.code.kawakuti.phonepharmacy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Russelius on 28/01/16.
 */
public class ImageLoader {

    public ImageLoader() {
    }

    public Bitmap setMedicineImage(String selectedImagePath) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
       if (selectedImagePath != null) {

        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

    }else {

          // selectedImagePath =

       }
        return bm;
    }
}