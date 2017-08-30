package dwallet.app.android.data

import dwallet.app.android.entities.WalletBasicInfo
import dwallet.core.crypto.sha256
import dwallet.core.extensions.toHexString
import org.xutils.DbManager
import org.xutils.x

/**
 * Created by unsignedint8 on 8/30/17.
 */

class Walletbase(name: String) {

    private val db by lazy {
        val dbConfigs = DbManager.DaoConfig().setDbName(sha256(name.toByteArray()).take(4).toByteArray().toHexString() + ".db").setDbVersion(1)
        return@lazy x.getDb(dbConfigs)!!
    }

    init {
        db.findFirst(WalletBasicInfo::class.java)
    }
}