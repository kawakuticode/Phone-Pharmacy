package com.code.kawakuti.phonepharmacy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.File;

/**
 * Created by Russelius on 28/01/16.
 */
public class ImageLoader {

    public ImageLoader() {
    }

    public Bitmap setMedicineImage(String selectedImagePath, int pixels) {
        Bitmap output = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if (selectedImagePath != null) {
            File f = new File(selectedImagePath);
            if (f.exists()) {


                try {


                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;

                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                    output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(output);

                    final int color = 0xff424242;
                    final Paint paint = new Paint();
                    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    final RectF rectF = new RectF(rect);
                    final float roundPx = pixels;

                    paint.setAntiAlias(true);
                    canvas.drawARGB(0, 0, 0, 0);
                    paint.setColor(color);
                    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, rect, rect, paint);

                } catch (OutOfMemoryError o) {

                    o.printStackTrace();

                }
            }
        } else {

            selectedImagePath = "file:///android_asset/farmacy.jpg";
            output = setMedicineImage(selectedImagePath, 100);

        }
        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = null;


        final int REQUIRED_SIZE = 200;


        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}