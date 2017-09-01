package dwallet.app.android.entities

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

/**
 * Created by unsignedint8 on 9/1/17.
 */

@Table(name = "nodes")
class WalletNode {

    @Column(name = "id", isId = true, autoGen = true)
    var id = 0L

    @Column(name = "host")
    lateinit var host: String

    @Column(name = "port")
    var port = 0
}