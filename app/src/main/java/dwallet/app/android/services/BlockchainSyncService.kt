package dwallet.app.android.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by unsignedint8 on 8/29/17.
 * Chinese Doc http://blog.csdn.net/javazejian/article/details/52709857
 * Just a nothing-to-do service, to prevent from stopping process when app is in syncing
 */

class BlockchainSyncService : Service() {

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}