package pl.mirkoczat.forgottenfridge

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.rollbar.android.Rollbar
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.toast

class ShareActivity: AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (ROLLBAR_ENABLED)
            Rollbar.init(this, ROLLBAR_ID, ROLLBAR_ENV);

        renderQR()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.action_scan -> {
            try {
                val intent = Intent("com.google.zxing.client.android.SCAN")
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE")
                startActivityForResult(intent, 0)
            } catch (e: Exception) {
                val intent = Intent(this, InstallActivity::class.java)
                startActivity(intent)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val contents = data.getStringExtra("SCAN_RESULT")
                    if (contents != null) {
                        val sync = Sync(this, DBHelper(this))
                        // TODO: validate?
                        sync.token = contents
                        sync.sync { onBackPressed() }
                    } else {
                        toast(R.string.error_scan)
                    }
                }
                else -> {toast(R.string.error_scan)}
            }
        }
    }

    fun renderQR() {
        val sync = Sync(this, DBHelper(this))
        val token = sync.token
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(token, BarcodeFormat.QR_CODE, 512, 512)
        var w = bitMatrix.width
        val h = bitMatrix.height
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        for (x in 0..w-1) {
            for (y in 0..h-1) {
                val color = when (bitMatrix.get(x, y)) {
                    true -> Color.BLACK
                    false -> Color.WHITE
                }
                bmp.setPixel(x, y, color)
            }
        }
        (findViewById(R.id.ivQR) as ImageView).imageBitmap = bmp
    }
}
