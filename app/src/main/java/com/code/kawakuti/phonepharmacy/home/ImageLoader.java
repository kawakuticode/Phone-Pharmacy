package com.code.kawakuti.phonepharmacy.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.code.kawakuti.phonepharmacy.R;

import java.io.File;

/**
 * Created by Russelius on 28/01/16.
 */
public class ImageLoader {

    Context context;

    public ImageLoader(Context mContext) {
        this.context = mContext;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }

    public static Bitmap decodeSampledBitmapFromFile(String selectedImagePath) {

        Bitmap outputBitmap = null;
        if (selectedImagePath != null) {
            File f = new File(selectedImagePath);
            if (f.exists()) {
                try {
                    // First decode with inJustDecodeBounds=true to check dimensions
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inDither = false; // Disable Dithering mode
                    options.inPurgeable = true; // Tell to gc that whether it needs free
                    // memory, the Bitmap can be cleared
                    options.inInputShareable = true;

                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(selectedImagePath, options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;

                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, imageHeight, imageWidth);

                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;
                    return BitmapFactory.decodeFile(selectedImagePath, options).copy(Bitmap.Config.ARGB_4444, true);

                } catch (OutOfMemoryError mem) {
                    mem.printStackTrace();

                }
            }
        }
        return null;
    }


    private Bitmap decodeFileByPath(String fPath) {
        // Decode image size
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = null;
    /*
     * If set to true, the decoder will return null (no bitmap), but the
     * out... fields will still be set, allowing the caller to query the
     * bitmap without having to allocate the memory for its pixels.
     */
        opts.inJustDecodeBounds = true;
        opts.inDither = false; // Disable Dithering mode
        opts.inPurgeable = true; // Tell to gc that whether it needs free
        // memory, the Bitmap can be cleared
        opts.inInputShareable = true; // Which kind of reference will be used to
        // recover the Bitmap data after being
        // clear, when it will be used in the
        // future
        if (fPath != null) {
            File f = new File(fPath);
            if (f.exists()) {
                BitmapFactory.decodeFile(fPath, opts);
                // The new size we want to scale to
                final int REQUIRED_SIZE = 70;

                // Find the correct scale value.
                int scale = 1;

                if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {

                    // Calculate ratios of height and width to requested height and width
                    final int heightRatio = Math.round((float) opts.outHeight
                            / (float) REQUIRED_SIZE);
                    final int widthRatio = Math.round((float) opts.outWidth
                            / (float) REQUIRED_SIZE);

                    // Choose the smallest ratio as inSampleSize value, this will guarantee
                    // a final image with both dimensions larger than or equal to the
                    // requested height and width.
                    scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
                }

                // Decode bitmap with inSampleSize set
                opts.inJustDecodeBounds = false;

                opts.inSampleSize = scale;

                bm = BitmapFactory.decodeFile(fPath, opts).copy(
                        Bitmap.Config.ARGB_4444, true);


            } else {
                //user delected a pic from sdCard
                bm = decodeFileInternalFile();
            }

        }
        //did not select any photo
        if (fPath == null) {
            bm = decodeFileInternalFile();
        }
        return bm;
    }


    private Bitmap decodeFileInternalFile() {
        // Decode image size
        BitmapFactory.Options opts = new BitmapFactory.Options();
    /*
     * If set to true, the decoder will return null (no bitmap), but the
     * out... fields will still be set, allowing the caller to query the
     * bitmap without having to allocate the memory for its pixels.
     */
        opts.inJustDecodeBounds = true;
        opts.inDither = false; // Disable Dithering mode
        opts.inPurgeable = true; // Tell to gc that whether it needs free
        // memory, the Bitmap can be cleared
        opts.inInputShareable = true; // Which kind of reference will be used to
        // recover the Bitmap data after being
        // clear, when it will be used in the
        // future

        BitmapFactory.decodeResource(context.getResources(), R.raw.med_default, opts);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 70;

        // Find the correct scale value.
        int scale = 1;

        if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) opts.outHeight
                    / (float) REQUIRED_SIZE);
            final int widthRatio = Math.round((float) opts.outWidth
                    / (float) REQUIRED_SIZE);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
        }

        // Decode bitmap with inSampleSize set
        opts.inJustDecodeBounds = false;

        opts.inSampleSize = scale;

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.raw.med_default, opts).copy(
                Bitmap.Config.ARGB_4444, true);

        return bm;

    }

    public Bitmap setMedicineImage(String selectedImagePath, int pixels) {

        Bitmap bitmap = decodeFileByPath(selectedImagePath);
        Canvas canvas = new Canvas(bitmap);
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


        return bitmap;
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = null;


        final int REQUIRED_SIZE = 200;


        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
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