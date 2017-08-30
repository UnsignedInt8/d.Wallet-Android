package dwallet.app.android.activities

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import dwallet.app.android.R
import dwallet.app.android.data.AppData
import dwallet.app.android.entities.WalletBasicInfo
import dwallet.app.android.services.BlockchainSyncService
import dwallet.core.crypto.Crypto

class MainActivity : AppCompatActivity() {

    init {
        Crypto.setupCryptoProvider()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()

        val ws = AppData.db.selector(WalletBasicInfo::class.java).findAll()

        if (ws == null || ws.isEmpty()) {
            val w = WalletBasicInfo()
            w.name = "xxx"
            AppData.db.saveOrUpdate(w)
        }

        startService(Intent(this, BlockchainSyncService::class.java))
        Log.v("xxx", WalletBasicInfo.single.toString())
    }

    private fun initUI() {
        findViewById<TextView>(R.id.textView_AppTitle).setTypeface(Typeface.createFromAsset(assets, "fonts/PoiretOne-Regular.ttf"))
//
//        val mainFragments = listOf(WalletsFragment.newInstance(), DWorldFragment.newInstance(), SettingsFragment.newInstance())
//        val adapter = FragmentsPagerAdapter(supportFragmentManager, mainFragments)
//        val viewPager = findViewById<ViewPager>(R.id.viewPager_Home)
//        viewPager.adapter = adapter
//        findViewById<TabLayout>(R.id.tabs).setupWithViewPager(viewPager)
    }


}
