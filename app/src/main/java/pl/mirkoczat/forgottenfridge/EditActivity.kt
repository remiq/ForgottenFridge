package pl.mirkoczat.forgottenfridge

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.rollbar.android.Rollbar
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.jetbrains.anko.onClick
import org.joda.time.DateTime
import java.util.*

const val DATE_FORMAT = "y-MM-dd"

class EditActivity: AppCompatActivity(), AnkoLogger {

    var id = 0
    var db: DBHelper? = null
    var etName: EditText? = null
    var etAmount: EditText? = null
    var etExpire: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (ROLLBAR_ENABLED)
            Rollbar.init(this, ROLLBAR_ID, ROLLBAR_ENV);

        etName = findViewById(R.id.etName) as EditText
        etAmount = findViewById(R.id.etAmount) as EditText
        etExpire = findViewById(R.id.etExpire) as EditText

        val btnExpire = findViewById(R.id.btnExpire) as Button
        btnExpire.onClick {
            val date = DateTime.parse(etExpire?.text.toString())
            CalendarDatePickerDialogFragment()
                    .setOnDateSetListener { calendarDatePickerDialogFragment, y, m, d ->
                        val dt = DateTime(y, m + 1, d, 0, 0).toString(DATE_FORMAT)
                        etExpire?.setText(dt)
                    }
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .setPreselectedDate(date.year, date.monthOfYear - 1, date.dayOfMonth)
                    .show(supportFragmentManager, "")
        }
        val btnSave = findViewById(R.id.btnSave) as Button
        val btnSaveTop = findViewById(R.id.btnSaveTop) as Button
        btnSave.onClick { saveAction() }
        btnSaveTop.onClick { saveAction() }

        db = DBHelper(this)
        id = intent.getIntExtra("id", 0)

        when (id) {
            0 -> {
                supportActionBar?.title = getString(R.string.title_activity_new)
                etExpire?.setText(DateTime.now().toString(DATE_FORMAT))
            }
            else -> {
                supportActionBar?.title = getString(R.string.title_activity_edit)
                val product = db?.getOne(id)

                etAmount?.setText(product?.amount)
                etName?.setText(product?.name)
                etExpire?.setText(product?.expire)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_delete -> {
            if (id > 0)
                db?.delete(id)
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun saveAction() {
        val item = Product(id, etName?.text.toString(), etAmount?.text.toString(), etExpire?.text.toString())
        when {
            id > 0 -> db?.edit(id, item)
            else  -> db?.add(item, true)
        }
        onBackPressed()
    }
}
