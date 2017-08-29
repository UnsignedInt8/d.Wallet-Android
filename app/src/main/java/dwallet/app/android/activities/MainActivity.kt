package dwallet.app.android.activities

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.widget.TextView
import dwallet.app.android.R
import dwallet.app.android.adpaters.FragmentsPagerAdapter
import dwallet.app.android.fragments.DWorldFragment
import dwallet.app.android.fragments.SettingsFragment
import dwallet.app.android.fragments.WalletsFragment
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
