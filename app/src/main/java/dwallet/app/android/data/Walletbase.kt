package dwallet.app.android.data

import android.content.Context
import dwallet.app.android.entities.WalletBasicInfo
import dwallet.app.android.entities.WalletKey
import dwallet.app.android.entities.WalletPeer
import dwallet.app.android.entities.WalletTx
import dwallet.core.bitcoin.application.wallet.Coins
import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.bitcoin.protocol.structures.Transaction
import dwallet.core.crypto.sha1
import dwallet.core.crypto.sha256
import dwallet.core.extensions.hexToByteArray
import dwallet.core.extensions.toHexString
import dwallet.core.infrastructure.Event
import dwallet.core.utils.BaseX
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.xutils.DbManager
import org.xutils.x
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec


/**
 * Created by unsignedint8 on 8/30/17.
 */

class Walletbase(val name: String, password: String, val coin: Coins = Coins.Bitcoin) : Event() {

    private lateinit var wallet: Wallet

    private lateinit var db: DbManager

    companion object {

        fun encryptMsg(message: String, password: String): String {
            val secret = SecretKeySpec(sha256(password.toByteArray()), "AES")
            val cipher = Cipher.getInstance("AES/OFB/PKCS5Padding")
            cipher!!.init(Cipher.ENCRYPT_MODE, secret, IvParameterSpec(sha1(password.toByteArray()).take(16).toByteArray()))
            val cipherText = cipher.doFinal(message.toByteArray(charset("UTF-8")))
            return BaseX.base64.encode(cipherText)
        }

        fun decryptMsg(cipherText: String, password: String): String {
            val secret = SecretKeySpec(sha256(password.toByteArray()), "AES")
            val cipher = Cipher.getInstance("AES/OFB/PKCS5Padding")
            cipher!!.init(Cipher.DECRYPT_MODE, secret, IvParameterSpec(sha1(password.toByteArray()).take(16).toByteArray()))
            return String(cipher.doFinal(BaseX.base64.decode(cipherText)))
        }

        fun testWalletExists(ctx: Context, name: String): Boolean {
            val dbName = filenameToDatabaseName(name)
            return ctx.databaseList().any { it == dbName }
        }

        private fun filenameToDatabaseName(name: String) = name + "_dwallet.db"

        fun listAllWallets(context: Context) = context.databaseList().filter { it.endsWith("_dwallet.db") }.map { it.split("_dwallet").first() }

        val coinsBootstrapNodes = mapOf(Pair(Coins.Bitcoin.name, BootstrapNodes.bitcoin), Pair(Coins.BitcoinTestnet.name, BootstrapNodes.bitcoinTestnet), Pair(Coins.Litecoin.name, BootstrapNodes.litecoin))
    }

    init {
        initWallet(password)
        prepareForSynchronization()
    }

    private fun insertKey(wif: String, usage: String) = db.save(WalletKey(wif, usage))

    private fun initWallet(password: String) {
        this.db = x.getDb(DbManager.DaoConfig().setDbName(filenameToDatabaseName(name)).setDbVersion(1))

        var info = db.findFirst(WalletBasicInfo::class.java)
        info?.db = db

        if (info == null) {
            val (wallet, mnemonic) = Wallet.create(password, coin)
            this.wallet = wallet

            info = WalletBasicInfo(db)
            info.coin = wallet.coin.name
            info.creationTime = Date()
            info.name = name
            info.masterPrivKey = encryptMsg(wallet.masterXprvKey.serializePrivate(), password)
            info.mnemonic = encryptMsg(mnemonic, password)

            db.save(info)

            wallet.externalPrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "external") }
            wallet.changePrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "change") }
            wallet.importedPrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "imported") }
        } else {
            val wallet = Wallet.fromMasterXprvKey(decryptMsg(info.masterPrivKey, password), info.externalKeys?.map { decryptMsg(it.value, password) } ?: listOf(), info.changeKeys?.map { decryptMsg(it.value, password) } ?: listOf(), info.importedKeys?.map { decryptMsg(it.value, password) } ?: listOf(), Coins.values().singleOrNull { it.name == info.coin } ?: coin)!!
            this.wallet = wallet
        }

        val txs = db.selector(WalletTx::class.java).where("utxo", "=", "true").findAll()?.map { Transaction.fromBytes(it.raw.hexToByteArray()) } ?: listOf()
        wallet.insertUtxos(txs)
    }

    private fun prepareForSynchronization() {
        val peers = (db.selector(WalletPeer::class.java).limit(30).orderBy("id", true).findAll()?.map { Pair(it.host, it.port) }?.toMutableList() ?: coinsBootstrapNodes[coin.name])!!
        (0 until peers.size).map {  }
    }
}