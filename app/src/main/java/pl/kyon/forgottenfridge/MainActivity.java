package pl.kyon.forgottenfridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";
    Context ctx;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DBHelper(this);

        ctx = this;
        populateTable();
    }

    @Override
    public void onResume() {
        super.onResume();
        cleanTable();
        populateTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intent = new Intent(ctx, EditActivity.class);
            intent.putExtra("id", 0);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_drop) {
            db.truncate();
            Intent intent = new Intent(ctx, EditActivity.class);
            intent.putExtra("id", 0);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent(ctx, EditActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void cleanTable() {
        TableLayout item_table = (TableLayout) findViewById(R.id.item_table);
        item_table.removeViews(1, item_table.getChildCount() - 1);
    }

    private void populateTable() {
        TableLayout item_table = (TableLayout) findViewById(R.id.item_table);
        item_table.setStretchAllColumns(true);
        List<Item> list = db.getAllOrdered();
        Iterator<Item> iterator = list.iterator();
        while(iterator.hasNext()) {
            Item i = iterator.next();
            item_table.addView(addRow(i.id, i.name, i.amount, i.expire));
        }
    }
    private TableRow addRow(int id, String name, String amount, String expire) {
        TableRow tr = new TableRow(this);
        tr.setOnClickListener(this);
        tr.setId(id);

        TextView col_name = new TextView(this);
        col_name.setText(name);
        col_name.setTextAppearance(this, R.style.TableRow);
        tr.addView(col_name);

        TextView col_amount = new TextView(this);
        col_amount.setText(amount);
        col_amount.setTextAppearance(this, R.style.TableRow);
        tr.addView(col_amount);

        TextView col_expire = new TextView(this);
        col_expire.setText(expire);
        col_expire.setTextAppearance(this, R.style.TableRow);
        tr.addView(col_expire);
        return tr;
    }
}
