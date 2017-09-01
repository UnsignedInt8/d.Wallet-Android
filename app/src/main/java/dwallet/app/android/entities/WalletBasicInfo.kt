package dwallet.app.android.entities

import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.crypto.sha1
import dwallet.core.crypto.sha256
import dwallet.core.utils.BaseX
import org.xutils.DbManager
import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * Created by unsignedint8 on 8/30/17.
 */

@Table(name = "basicinfo")
class WalletBasicInfo() {

    var db: DbManager? = null

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

    @Column(name = "mnemonic")
    lateinit var mnemonic: String

    val externalKeys: List<WalletKey>?
        get() = try {
            db?.selector(WalletKey::class.java)?.where("usage", "=", "external")?.findAll()
        } catch (ex: Exception) {
            null
        }

    val changeKeys: List<WalletKey>?
        get() = try {
            db?.selector(WalletKey::class.java)?.where("usage", "=", "change")?.findAll()
        } catch (ex: Exception) {
            null
        }

    val importedKeys: List<WalletKey>?
        get() = try {
            db?.selector(WalletKey::class.java)?.where("usage", "=", "imported")?.findAll()
        } catch (ex: Exception) {
            null
        }

    val txs: List<WalletTx>?
        get() = try {
            db?.selector(WalletTx::class.java)?.where("utxo", "=", "true")?.findAll()
        } catch (ex: Exception) {
            null
        }

    companion object {
        val single = WalletBasicInfo()

    }

    fun toWallet() = Wallet.fromMasterXprvKey(masterPrivKey, externalKeys?.map { it.value } ?: listOf(), changeKeys?.map { it.value } ?: listOf(), importedKeys?.map { it.value } ?: listOf())

}