package dwallet.app.android.adpaters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by unsignedint8 on 8/29/17.
 */

class FragmentsPagerAdapter(fm: FragmentManager?, private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int): CharSequence {
        return fragments[position].toString()
    }
}