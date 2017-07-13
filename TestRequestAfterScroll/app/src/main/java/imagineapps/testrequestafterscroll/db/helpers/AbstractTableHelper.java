package imagineapps.testrequestafterscroll.db.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by r028367 on 13/07/2017.
 */

public abstract class AbstractTableHelper {

    protected SQLiteDatabase sqLiteDatabase;
    protected DatabaseHelper databaseHelper;
    protected Context context;

    public AbstractTableHelper(Context context) throws DatabaseHelper.IOPropertyException {
        this.context = context;
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }


    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public Context getContext() {
        return context;
    }

    public Cursor getCursor(SQLiteDatabase db, String sql, String[] args) {
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(sql, args);
        }
        return cursor;
    }

    public Cursor getCursor(SQLiteDatabase db, String sql) {
        return getCursor(db, sql, null);
    }

    protected abstract boolean createTable();

}
