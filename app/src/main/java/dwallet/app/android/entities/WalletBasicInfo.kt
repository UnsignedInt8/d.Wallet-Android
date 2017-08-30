package dwallet.app.android.entities

import org.xutils.DbManager
import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table
import java.util.*


/**
 * Created by unsignedint8 on 8/30/17.
 */

@Table(name = "basicinfo")
class WalletBasicInfo() {

    companion object {
        val single = WalletBasicInfo()
    }

    private var db: DbManager? = null

    constructor(dbManager: DbManager) : this() {
        db = dbManager
    }

    @Column(name = "id", isId = true, autoGen = true)
    var id = 0L

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "master_priv_key")
    lateinit var masterPrivKey: String

    @Column(name = "creation_time")
    lateinit var creationTime: Date

    @Column(name = "order_id")
    var orderId = 0

    @Column(name = "coin")
    lateinit var coin: String

    val externalKeys = try {
        db?.selector(WalletKey::class.java)?.where("usage", "=", "external")?.findAll()
    } catch (ex: Exception) {
        null
    }

    val changeKeys = try {
        db?.selector(WalletKey::class.java)?.where("usage", "=", "change")?.findAll()
    } catch (ex: Exception) {
        null
    }

    val importedKeys = try {
        db?.selector(WalletKey::class.java)?.where("usage", "=", "imported")?.findAll()
    } catch (ex: Exception) {
        null
    }

//    val txs: List<Tx>
//        get() = SugarRecord.find(Tx::class.java, "wallet_id=? and utxo = true", id.toString())


}