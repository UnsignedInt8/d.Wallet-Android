package dwallet.app.android.data

import android.app.Application
import org.xutils.DbManager
import org.xutils.x

/**
 * Created by unsignedint8 on 8/30/17.
 */

class AppData {

    companion object {
        val db by lazy {
            val dbConfigs = DbManager.DaoConfig().setDbName("wallets2.db").setDbVersion(1)
            return@lazy x.getDb(dbConfigs)!!
        }
    }


}