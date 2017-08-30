package dwallet.app.android.entities

import org.xutils.DbManager
import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

/**
 * Created by unsignedint8 on 8/30/17.
 */


@Table(name = "txs")
class WalletTx() {

    private var db: DbManager? = null

    constructor(dbManager: DbManager) : this() {
        db = dbManager
    }

    @Column(name = "utxo")
    var utxo = true

    @Column(name = "raw")
    lateinit var raw: String

    @Column(name = "txid", isId = true)
    lateinit var txid: String

    @Column(name = "block_hash")
    lateinit var blockHash: String

    fun getBlock() = try {
        db?.selector(WalletMerkleblock::class.java)?.where("hash", "=", blockHash)?.findFirst()
    } catch (ex: Exception) {
        null
    }

}