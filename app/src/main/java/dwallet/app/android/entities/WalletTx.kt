package dwallet.app.android.entities

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

/**
 * Created by unsignedint8 on 8/30/17.
 */


@Table(name = "txs")
class WalletTx {

    @Column(name = "utxo")
    var utxo = true

    @Column(name = "raw")
    lateinit var raw: String

    @Column(name = "txid")
    lateinit var txid: String
}