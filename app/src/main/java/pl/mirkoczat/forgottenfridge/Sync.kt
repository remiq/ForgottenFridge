package pl.mirkoczat.forgottenfridge

import android.content.Context
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

const val API_URL = "http://ff.mirkoczat.pl/fridge"
//const val API_URL = "https://b35ff48.ngrok.com/fridge"
const val APP_NAME = "forgotten_fridge"
const val TOKEN = "token"

class Sync(var context: Context, var db: DBHelper): AnkoLogger {
    val settings = context.getSharedPreferences(APP_NAME, 0)
    var token: String
        get() = when (settings.contains(TOKEN)) {
            true -> settings.getString(TOKEN, "")
            else -> {
                val token = UUID.randomUUID().toString() // CHAR(36)
                saveToken(token)
                token
            }
        }
        set(token) { saveToken(token) }

    fun clear() {
        db.truncate()
        token = UUID.randomUUID().toString()
    }

    fun sync(callback: () -> Unit) {
        var arr = JSONArray()
        for (item in db.getAllOrdered(true)) {
            val row = JSONObject()
                .put(COL_NAME, item.name)
                .put(COL_AMOUNT, item.amount)
                .put(COL_EXPIRE, item.expire)
                .put(COL_UID, item.uid)
                .put(COL_UPDATED, item.updated)
            arr = arr.put(row)
        }
        val json = arr.toString()
        Fuel.post(API_URL)
                .body("token=$token&data=$json")
                .responseJson { request, response, result ->
                    val responseId = when (response.httpStatusCode) {
                        200 -> {
                            db.truncate()
                            val data = result.get().getJSONArray("data")
                            for (i in 0..(data.length() - 1)) {
                                val item = data.getJSONObject(i)
                                db.add(Product(0,
                                        item.getString(COL_NAME),
                                        item.getString(COL_AMOUNT),
                                        item.getString(COL_EXPIRE),
                                        item.getString(COL_UID),
                                        item.getString(COL_UPDATED)
                                ))
                            }
                            callback()
                            R.string.ok_sync
                        }
                        else -> R.string.error_sync
                    }
                    context.toast(responseId)
                }
    }

    private fun saveToken(token: String) {
        val editor = settings.edit()
        editor.putString(TOKEN, token)
        editor.commit()
    }
}
