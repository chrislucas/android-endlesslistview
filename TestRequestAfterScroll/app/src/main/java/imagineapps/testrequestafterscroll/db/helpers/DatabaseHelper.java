package imagineapps.testrequestafterscroll.db.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by r028367 on 13/07/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DB_NAME  = "icomoncomvcprototipo.db";
    private static DatabaseHelper instance = null;
    private Context context;


    public static boolean existsDatabase(Context context) {
        File file = context.getDatabasePath(DB_NAME);
        boolean f = file != null ? file.exists() : false;
        return f;
    }

    public static Properties readProperties(Context context, String filename) {
        Properties properties   = null;
        InputStream inputStream = null;
        try {
            AssetManager assetManager = context.getAssets();
            //String [] locales = assetManager.getLocales();
            inputStream = assetManager.open(filename);
            //inputStream = new FileInputStream(new File(filename));
            if(inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
            }
        }
        catch (IOException ieox) {
            Log.e("EXCP_INPUT_STREAM_OPEN", ieox.getMessage());
        }
        finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("EXCP_INPUT_STREAM_CLOSE", e.getMessage());
                }
            }
        }
        return properties;
    }

    private static Properties getDbVersion(Context context) {
        //String path = new ContextWrapper(context).getFilesDir().getPath();
        Properties properties = readProperties(context, "properties/db.properties");
        return properties;
    }

    public static class IOPropertyException extends Exception {
        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public IOPropertyException(String message) {
            super(message);
        }
    }
    public synchronized static DatabaseHelper getInstance(Context context) throws IOPropertyException {
        Properties properties = getDbVersion(context);
        if(properties == null) {
            throw new IOPropertyException("Não foi possível ler o arquivo de configuração do banco");
        }
        else  {
            int version = Integer.parseInt(properties.getProperty("version"));
            if(DatabaseHelper.instance == null) {
                DatabaseHelper.instance = new DatabaseHelper(context, DB_NAME, null, version);
                DatabaseHelper.instance.context = context;
            }
        }

        return instance;
    }

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    private DatabaseHelper(Context context, String name
            , SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Create a helper object to create, open, and/or manage a database.
     * The database is not actually created or opened until one of
     * {@link #getWritableDatabase} or {@link #getReadableDatabase} is called.
     * <p>
     * <p>Accepts input param: a concrete instance of {@link DatabaseErrorHandler} to be
     * used to handle corruption when sqlite reports database corruption.</p>
     *
     * @param context      to use to open or create the database
     * @param name         of the database file, or null for an in-memory database
     * @param factory      to use for creating cursor objects, or null for the default
     * @param version      number of the database (starting at 1); if the database is older,
     *                     {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                     newer, {@link #onDowngrade} will be used to downgrade the database
     * @param errorHandler the {@link DatabaseErrorHandler} to be used when sqlite reports database
     */
    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory
            , int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if(db != null) {

        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(db != null) {

        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public SQLiteDatabase open() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase;
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
