package dwallet.app.base

import android.app.Application
import dwallet.core.bitcoin.application.wallet.Wallet
import org.xutils.x
import org.xutils.DbManager


/**
 * Created by unsignedint8 on 8/30/17.
 */

class WalletApp : Application() {

    override fun onCreate() {
        super.onCreate()
        x.Ext.init(this)
        Wallet.externalKeysAmount = 3
        Wallet.changeKeysAmount = 5
    }


}
