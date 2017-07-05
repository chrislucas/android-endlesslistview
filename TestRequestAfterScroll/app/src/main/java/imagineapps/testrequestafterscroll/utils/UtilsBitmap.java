package imagineapps.testrequestafterscroll.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by r028367 on 05/07/2017.
 */

public class UtilsBitmap {

    public enum CompressFormat {
         JPEG(Bitmap.CompressFormat.JPEG)
        ,PNG(Bitmap.CompressFormat.PNG)
        ,WEBP(Bitmap.CompressFormat.WEBP);

        private Bitmap.CompressFormat type;

        CompressFormat(Bitmap.CompressFormat type) {
            this.type = type;
        }

        public Bitmap.CompressFormat getType() {
            return type;
        }
    }

    public static byte [] compress(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte [] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    public static byte [] compress(Bitmap bitmap, CompressFormat compressFormat) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat.getType(), 100, byteArrayOutputStream);
        byte [] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }


    public static Bitmap uncompress(byte [] buffer) {
        Bitmap bmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        return bmp;
    }
}
