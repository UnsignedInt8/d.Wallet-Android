package dwallet.app.android.entities

import org.xutils.DbManager
import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

/**
 * Created by unsignedint8 on 8/30/17.
 */

@Table(name = "merkleblocks", onCreated = "CREATE UNIQUE INDEX index_hash ON hash")
class WalletMerkleblock() {

    private var db: DbManager? = null

    constructor(db: DbManager) : this() {
        this.db = db
    }

    @Column(name = "hash", isId = true)
    lateinit var hash: String

    @Column(name = "merkleroot")
    lateinit var merkleRoot: String

    @Column(name = "height")
    var height = 0

    fun getTxs() = try {
        db?.selector(WalletTx::class.java)?.where("block_hash", "=", hash)?.findAll()
    } catch (ex: Exception) {
        null
    }
}