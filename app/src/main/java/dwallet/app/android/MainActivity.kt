package dwallet.app.android

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.crypto.Crypto

class MainActivity : AppCompatActivity() {

    init {
        Crypto.setupCryptoProvider()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.textView_AppTitle).setTypeface(Typeface.createFromAsset(assets, "fonts/PoiretOne-Regular.ttf"))
    }

}
