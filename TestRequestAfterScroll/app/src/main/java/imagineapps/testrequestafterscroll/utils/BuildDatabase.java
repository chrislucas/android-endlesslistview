package imagineapps.testrequestafterscroll.utils;

import android.content.Context;
import android.util.Log;

import imagineapps.testrequestafterscroll.db.helpers.DatabaseHelper;

/**
 * Created by r028367 on 13/07/2017.
 */

public class BuildDatabase {

    public static boolean checkDbExists(Context context) {
        return DatabaseHelper.existsDatabase(context);
    }

    public static boolean createDbIfNotExists(Context context) {
        boolean create = false;
        try {
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            databaseHelper.open();
            create = DatabaseHelper.existsDatabase(context);
            databaseHelper.close();
        }
        catch (DatabaseHelper.IOPropertyException e) {
            Log.e("EXCP_OPEN_PROPERTIES", e.getMessage());
        }
        return create;
    }

}
