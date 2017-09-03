package dwallet.app.android.entities

import dwallet.core.bitcoin.protocol.structures.MerkleBlock
import dwallet.core.extensions.toHexString
import org.xutils.DbManager
import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

/**
 * Created by unsignedint8 on 8/30/17.
 */

@Table(name = "merkleblocks")
class WalletMerkleblock() {

    private var db: DbManager? = null

    constructor(db: DbManager) : this() {
        this.db = db
    }

    constructor(block: MerkleBlock) : this() {
        this.hash = block.hash
        this.merkleRoot = block.merkleRootHash
        this.raw = block.toBytes().toHexString()
    }

    @Column(name = "hash", isId = true)
    lateinit var hash: String

    @Column(name = "merkleroot")
    lateinit var merkleRoot: String

    @Column(name = "raw")
    lateinit var raw: String

    @Column(name = "height")
    var height = 0

    fun getTxs() = try {
        db?.selector(WalletTx::class.java)?.where("block_hash", "=", hash)?.findAll()
    } catch (ex: Exception) {
        null
    }
}