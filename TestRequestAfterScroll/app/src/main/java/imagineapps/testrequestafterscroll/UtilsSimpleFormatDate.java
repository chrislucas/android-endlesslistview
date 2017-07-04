package imagineapps.testrequestafterscroll;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by r028367 on 04/07/2017.
 */

public class UtilsSimpleFormatDate {

    public static long convertUTCToMilliseconds(String utcTime, String fmtOrigin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmtOrigin, Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = dateFormat.parse(utcTime);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("EXCP_PARSE_DATE", e.getMessage());
        }
        return -1;
    }

    public static String convertLongToDateFormat(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String fmt = dateFormat.format(new Date(date));
        return fmt;
    }

    public static String convertLongToDateFormat(long date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        String fmt = dateFormat.format(new Date(date));
        return fmt;
    }
}
