package dwallet.app.android.entities

import dwallet.core.bitcoin.protocol.structures.Transaction
import dwallet.core.extensions.toHexString
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

    constructor(tx: Transaction) : this(){
        this.txid = tx.id
        this.raw = tx.toBytes().toHexString()
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