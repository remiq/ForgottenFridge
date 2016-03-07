package pl.mirkoczat.forgottenfridge

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.rollbar.android.Rollbar
import org.jetbrains.anko.*

const val ROLLBAR_ENABLED = true
const val ROLLBAR_ID = "a017df810819408da9c28d48ed1d2698"
const val ROLLBAR_ENV = "production"

class MainActivity: AppCompatActivity(), AnkoLogger {
    var db: DBHelper? = null
    var adapter: ProductAdapter? = null
    var sync: Sync? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        if (ROLLBAR_ENABLED)
            Rollbar.init(this, ROLLBAR_ID, ROLLBAR_ENV);

        val dbp = DBHelper(this)
        sync = Sync(this,  dbp)
        db = dbp
        adapter = ProductAdapter(this)
        val list = findViewById(R.id.products) as ListView
        list.adapter = adapter
        list.onItemClick { adapterView, view, i, l ->
            val product = adapterView?.getItemAtPosition(i) as Product
            editItem(product.id)
        }
        refreshTable()
    }

    override fun onResume() {
        super.onResume()
        refreshTable()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.action_add -> {
            editItem(0)
            true
        }
        R.id.action_sync -> {
            async() {
                sync?.sync {
                    refreshTable()
                }
            }
            true
        }
        R.id.action_share -> {
            val intent = Intent(this, ShareActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.action_drop -> {
            sync?.clear()
            editItem(0)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun editItem(id: Int) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    fun refreshTable() {
        val list = db?.getAllOrdered()
        adapter?.clear()
        adapter?.addAll(list)
        adapter?.notifyDataSetChanged()
    }
}
