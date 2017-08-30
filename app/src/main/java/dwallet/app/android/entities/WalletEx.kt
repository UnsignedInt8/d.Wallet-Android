package dwallet.app.android.entities

import com.orm.SugarRecord
import com.orm.dsl.Ignore
import com.orm.dsl.Unique

/**
 * Created by unsignedint8 on 8/30/17.
 */

class WalletEx : SugarRecord() {

    lateinit var name: String
    lateinit var masterPrivKey: String

    val externalKeys: List<Key>
        get() = SugarRecord.find(Key::class.java, "wallet_id = ? and usage = ?", id.toString(), "external")

    val changeKeys: List<Key>
        get() = SugarRecord.find(Key::class.java, "wallet_id = ? and usage = ?", id.toString(), "change")

    val importedKeys: List<Key>
        get() = SugarRecord.find(Key::class.java, "wallet_id = ? and usage = ?", id.toString(), "imported")

    val txs: List<Tx>
        get() = SugarRecord.find(Tx::class.java, "wallet_id=? and utxo = true", id.toString())

    class Key : SugarRecord() {
        var walletId: Long = 0
        lateinit var value: String
        lateinit var usage: String
    }

    class Tx : SugarRecord() {
        var walletId: Long = 0
        var utxo = true
        lateinit var raw: String
        @Unique lateinit var txid: String
    }

}