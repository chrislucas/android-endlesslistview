package imagineapps.testrequestafterscroll.db.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Properties;

import imagineapps.testrequestafterscroll.entitiies.Info;

/**
 * Created by r028367 on 13/07/2017.
 */

public class TableHelperInfo extends AbstractTableHelper {

    public static final String TABLE_NAME = "info";

    public TableHelperInfo(Context context) throws DatabaseHelper.IOPropertyException {
        super(context);
    }

    @Override
    protected boolean createTable() {
        try {
            Context context = getContext();
            DatabaseHelper instanceDb = DatabaseHelper.getInstance(context);
            Properties properties = DatabaseHelper.readProperties(context, "assets/properties/tables.properties");
            String sql = properties.getProperty(TABLE_NAME);
            SQLiteDatabase db = instanceDb.getWritableDatabase();
            db.execSQL(sql);
        } catch (DatabaseHelper.IOPropertyException | SQLException e) {
            Log.e("EXCEPTION_CREATE_TBL", e.getMessage());
        }
        return false;
    }

    public long insert(Info info) {
        return 0;
    }
}
