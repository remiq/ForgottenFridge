package pl.mirkoczat.forgottenfridge

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import com.rollbar.android.Rollbar
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.onClick

class InstallActivity: AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_install)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (ROLLBAR_ENABLED)
            Rollbar.init(this, ROLLBAR_ID, ROLLBAR_ENV);

        val btnInstallStore = findViewById(R.id.btnInstallStore) as Button
        btnInstallStore.onClick {
            val marketIntent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=com.google.zxing.client.android");
            startActivity(marketIntent);
        }
        val btnInstallBrowser = findViewById(R.id.btnInstallBrowser) as Button
        btnInstallBrowser.onClick {
            val marketIntent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android");
            startActivity(marketIntent);
        }
    }
}
