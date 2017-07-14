package imagineapps.testrequestafterscroll.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import imagineapps.testrequestafterscroll.entitiies.Info;

/**
 * Created by r028367 on 13/07/2017.
 */

public class TableHelperInfo extends AbstractTableHelper {

    public static final String TABLE_NAME = "info";

    public static final String [] FIELDS = {
        "_id"
        ,"post_id"
        ,"title"
        ,"subtitle"
        ,"text"
        ,"img_src"
        ,"create_at"
    };

    public TableHelperInfo(Context context) throws DatabaseHelper.IOPropertyException {
        super(context);
    }

    @Override
    public boolean createTable() {
        try {
            Context context = getContext();
            DatabaseHelper instanceDb = DatabaseHelper.getInstance(context);
            Properties properties = DatabaseHelper.readProperties(context, "properties/tables.properties");
            String sql = properties.getProperty(TABLE_NAME);
            SQLiteDatabase db = instanceDb.getWritableDatabase();
            db.execSQL(sql);
        }
        catch (DatabaseHelper.IOPropertyException | SQLException e) {
            Log.e("EXCEPTION_CREATE_TBL", e.getMessage());
            return false;
        }
        return true;
    }

    public boolean insertAll(List<Info> list) {
        boolean success = true;
        for (Info info : list) {
            long answer = insert(info);
            if(answer < 1) {
                success = false;
                Log.e("ERRROR_INSERT_POST", String.format("Erro ao inserir post: %s", info.getId()));
            }
        }
        return success;
    }

    public long insert(Info info) {
        long id = -1;
        DatabaseHelper instanceDb = null;
        try {
            instanceDb = DatabaseHelper.getInstance(context);
            SQLiteDatabase db = instanceDb.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FIELDS[1], info.getId());
            contentValues.put(FIELDS[2], info.getTitle());
            contentValues.put(FIELDS[3], info.getSubtitle());
            contentValues.put(FIELDS[4], info.getText());
            contentValues.put(FIELDS[5], info.getUrlImage());
            contentValues.put(FIELDS[6], info.getDate());
            try {
                id = db.insertOrThrow(TABLE_NAME, null, contentValues);
            } catch (SQLException sqlex) {
                Log.e("EXCP_METHOD_INSERTION", sqlex.getMessage());
            }
            db.close();
        } catch (DatabaseHelper.IOPropertyException e) {
            Log.e("EXCP_INSERTION_GET_INS", e.getMessage());
        }
        return id;
    }

    public List<Info> getAll() {
        List<Info> list = new ArrayList<>();
        try {
            DatabaseHelper instanceDb = DatabaseHelper.getInstance(context);
            SQLiteDatabase db = instanceDb.getReadableDatabase();
            String query = String.format("SELECT * FROM %s", TABLE_NAME);
            Cursor cursor = getCursor(db, query);
            list = get(cursor);
        } catch (DatabaseHelper.IOPropertyException e) {
            Log.e("EXCP_GETALL", e.getMessage());
        }
        return list;
    }


    public List<Info> get(Cursor cursor) {
        List<Info> list = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()) {
            for( ;cursor.moveToNext(); ) {
                int idxID = cursor.getColumnIndex(FIELDS[1]);
                int idxTitle = cursor.getColumnIndex(FIELDS[2]);
                int idxSubTitle = cursor.getColumnIndex(FIELDS[3]);
                int idxText = cursor.getColumnIndex(FIELDS[4]);
                int idxImgSrc = cursor.getColumnIndex(FIELDS[5]);
                int idxCreateAt = cursor.getColumnIndex(FIELDS[6]);
                Info info = new Info();
                info.setId(cursor.getString(idxID));
                info.setTitle(cursor.getString(idxTitle));
                info.setSubtitle(cursor.getString(idxSubTitle));
                info.setText(cursor.getString(idxText));
                info.setUrlImage(cursor.getString(idxImgSrc));
                info.setDate(cursor.getLong(idxCreateAt));
                list.add(info);
            }
        }
        return list;
    }
}
