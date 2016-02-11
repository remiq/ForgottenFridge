package pl.kyon.forgottenfridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    int id;
    Context ctx;
    DBHelper db;
    EditText etName;
    EditText etAmount;
    DatePicker dpExpire;
    final static String TAG = "EditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ctx = this;
        db = new DBHelper(this);
        etName = (EditText) findViewById(R.id.etName);
        etAmount = (EditText) findViewById(R.id.etAmount);
        dpExpire = (DatePicker) findViewById(R.id.dpExpire);

        Button btnSave = (Button)findViewById(R.id.btnSave);
        Button btnSaveTop = (Button)findViewById(R.id.btnSaveTop);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        if (id > 0) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_edit));
            Item i = db.getOne(id);
            etAmount.setText(i.amount);
            etName.setText(i.name);
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
                LocalDate date = dtf.parseLocalDate(i.expire);
                dpExpire.updateDate(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
            } catch (Exception e) {}

        } else {
            getSupportActionBar().setTitle(getString(R.string.title_activity_new));
        }
        View.OnClickListener clSave = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate date = new LocalDate()
                        .withYear(dpExpire.getYear())
                        .withMonthOfYear(dpExpire.getMonth() + 1)
                        .withDayOfMonth(dpExpire.getDayOfMonth());
                Log.v(TAG, date.toString());
                if (id > 0) {
                    db.edit(id, etName.getText().toString(), etAmount.getText().toString(), date.toString());
                } else {
                    db.add(etName.getText().toString(), etAmount.getText().toString(), date.toString());
                }
                onBackPressed();
            }
        };
        btnSave.setOnClickListener(clSave);
        btnSaveTop.setOnClickListener(clSave);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            if (this.id > 0) {
                db.delete(this.id);
            }
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
