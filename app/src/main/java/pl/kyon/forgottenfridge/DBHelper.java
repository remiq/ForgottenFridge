package pl.kyon.forgottenfridge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remiq on 10.02.16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_EXPIRE = "expire";
    private static final String DATABASE_NAME = "forgotten_fridge";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_AMOUNT + " TEXT, " +
            COLUMN_EXPIRE + " TEXT);";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing
        Log.v("DBHelper", "onUpgrade");
    }

    public List<Item> getAllOrdered() {
        List<Item> ret = new ArrayList<Item>();
        String[] allColumns = {COLUMN_ID, COLUMN_NAME, COLUMN_AMOUNT, COLUMN_EXPIRE};
        Cursor cursor = getReadableDatabase().query(TABLE, allColumns, null, null, null, null,
                COLUMN_EXPIRE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item i = new Item(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            ret.add(i);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return ret;
    }

    public Item getOne(int id) {
        String[] allColumns = {COLUMN_ID, COLUMN_NAME, COLUMN_AMOUNT, COLUMN_EXPIRE};
        Cursor cursor = getReadableDatabase().query(TABLE, allColumns,
                COLUMN_ID + " = ?", new String[] {String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        Item i = new Item(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));
        // make sure to close the cursor
        cursor.close();
        return i;
    }

    public void add(String name, String amount, String expire) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_EXPIRE, expire);
        getWritableDatabase().insert(TABLE, null, cv);
    }

    public void edit(int id, String name, String amount, String expire) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_EXPIRE, expire);
        getWritableDatabase().update(TABLE, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
    }

    public void delete(int id) {
        getWritableDatabase()
                .delete(TABLE, COLUMN_ID + " = " + id, null);
    }

    public void truncate() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
    }
}
