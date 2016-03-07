package pl.mirkoczat.forgottenfridge

import android.database.Cursor

data class Product(val id: Int = 0, val name: String = "", val amount: String = "", val expire: String = "", val uid: String = "", val updated: String = "") {
    constructor(cursor: Cursor): this(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)) {
        //info(updated)
    }
}
