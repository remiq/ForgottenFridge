package pl.mirkoczat.forgottenfridge

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.joda.time.DateTime
import java.util.*

const val NAME_REMOVED = "*removed*"
const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val DATABASE_NAME = "forgotten_fridge"
const val DATABASE_VERSION = 2
const val TABLE_NAME = "items"
const val COL_ID = "id"
const val COL_UID = "uid"
const val COL_NAME = "name"
const val COL_AMOUNT = "amount"
const val COL_EXPIRE = "expire"
const val COL_UPDATED = "updated"
const val TABLE_CREATE_V1 = """
    CREATE TABLE $TABLE_NAME (
        $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_NAME TEXT,
        $COL_AMOUNT TEXT,
        $COL_EXPIRE TEXT
    );
"""
const val TABLE_UPDATE_V1_V2 = """
ALTER TABLE $TABLE_NAME ADD COLUMN $COL_UID TEXT;
ALTER TABLE $TABLE_NAME ADD COLUMN $COL_UPDATED TEXT;
UPDATE $TABLE_NAME SET $COL_UPDATED = DATE('now');
"""
const val TABLE_CREATE_V2 = """
    CREATE TABLE $TABLE_NAME (
        $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_UID TEXT,
        $COL_NAME TEXT,
        $COL_AMOUNT TEXT,
        $COL_EXPIRE TEXT,
        $COL_UPDATED TEXT
    );
"""

class DBHelper(val context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), AnkoLogger {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE_V2)
    }

    override fun onUpgrade(db: SQLiteDatabase, fromVer: Int, toVer: Int) = when (Pair(fromVer, toVer)) {
        Pair(1, 2) -> {
            db.execSQL(TABLE_UPDATE_V1_V2);
            var sql = ""
            val cursor = db.query(TABLE_NAME, arrayOf(COL_ID), null, null, null, null, null)
            while (!cursor.isAfterLast) {
                val id = cursor.getInt(0)
                val uid = uuid()
                sql += "UPDATE $TABLE_NAME SET $COL_UID = '$uid' WHERE $COL_ID = $id;"
                cursor.moveToNext()
            }
            cursor.close()
            db.execSQL(sql);
        }
        else -> info("No upgrade path for $fromVer -> $toVer")
    }

    fun add(item: Product, newUid: Boolean = false) {
        val cv = cv(item)
        if (newUid) {
            cv.put(COL_UID, uuid())
        }
        writableDatabase.insert(TABLE_NAME, null, cv)
    }

    fun edit(id: Int, item: Product) {
        val old_item = getOne(id)
        val cv = cv(item)
        cv.put(COL_UID, old_item.uid)
        writableDatabase.update(TABLE_NAME, cv, "$COL_ID = ?", arrayOf("$id"))
    }

    fun delete(id: Int) {
        val item = getOne(id)
        val cv = cv(item)
        cv.put(COL_NAME, NAME_REMOVED) // why not delete? to have data of deleted items to sync
        writableDatabase.update(TABLE_NAME, cv, "$COL_ID = ?", arrayOf("$id"))
    }

    fun truncate() {
        writableDatabase.delete(TABLE_NAME, null, null)
    }

    fun getOne(id: Int): Product {
        val cursor = readableDatabase.query(TABLE_NAME, arrayOf(COL_ID, COL_NAME, COL_AMOUNT, COL_EXPIRE, COL_UID, COL_UPDATED),
                "$COL_ID = ?", arrayOf("$id"), null, null, null)
        cursor.moveToFirst()
        val item = Product(cursor)
        cursor.close()
        return item
    }

    fun getAllOrdered(withDeleted: Boolean = false): List<Product> {
        val ret = ArrayList<Product>()
        val cursor = readableDatabase.query(TABLE_NAME, arrayOf(COL_ID, COL_NAME, COL_AMOUNT, COL_EXPIRE, COL_UID, COL_UPDATED),
                null, null, null, null, "$COL_EXPIRE ASC")
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            if (withDeleted || !cursor.getString(1).equals(NAME_REMOVED)) {
                ret.add(Product(cursor))
            }
            cursor.moveToNext()
        }
        cursor.close()
        return ret
    }

    fun uuid(): String {
        return UUID.randomUUID().toString()
    }

    private fun cv(item: Product): ContentValues {
        val cv = ContentValues()
        cv.put(COL_NAME, item.name)
        cv.put(COL_AMOUNT, item.amount)
        cv.put(COL_EXPIRE, item.expire)
        cv.put(COL_UID, item.uid)
        cv.put(COL_UPDATED, DateTime.now().toString(DATETIME_FORMAT))
        return cv
    }
}
