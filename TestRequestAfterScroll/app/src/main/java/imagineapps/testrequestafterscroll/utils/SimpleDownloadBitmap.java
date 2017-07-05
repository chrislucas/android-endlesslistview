package imagineapps.testrequestafterscroll.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.github.kevinsawicki.http.HttpRequest;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by r028367 on 05/07/2017.
 */

public class SimpleDownloadBitmap {

    public static Bitmap download(String path) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            int statusCode = httpURLConnection.getResponseCode();
            if(statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            if(inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {

        }
        finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

}
