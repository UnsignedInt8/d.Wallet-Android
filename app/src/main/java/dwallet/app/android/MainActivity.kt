package dwallet.app.android

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import dwallet.core.crypto.Crypto

class MainActivity : AppCompatActivity() {

    init {
        Crypto.setupCryptoProvider()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    private fun initUI(){
        findViewById<TextView>(R.id.textView_AppTitle).setTypeface(Typeface.createFromAsset(assets, "fonts/PoiretOne-Regular.ttf"))
    }
}
