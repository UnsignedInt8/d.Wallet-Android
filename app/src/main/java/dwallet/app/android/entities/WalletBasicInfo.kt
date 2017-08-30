package dwallet.app.android.entities

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table
import java.util.*


/**
 * Created by unsignedint8 on 8/30/17.
 */

@Table(name = "basicinfo")
class WalletBasicInfo {

    companion object {
        val single = WalletBasicInfo()
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

//    val externalKeys: List<Key>
//        get() = SugarRecord.find(Key::class.java, "wallet_id = ? and usage = ?", id.toString(), "external")
//
//    val changeKeys: List<Key>
//        get() = SugarRecord.find(Key::class.java, "wallet_id = ? and usage = ?", id.toString(), "change")
//
//    val importedKeys: List<Key>
//        get() = SugarRecord.find(Key::class.java, "wallet_id = ? and usage = ?", id.toString(), "imported")
//
//    val txs: List<Tx>
//        get() = SugarRecord.find(Tx::class.java, "wallet_id=? and utxo = true", id.toString())



}