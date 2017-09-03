package dwallet.app.android.data

import android.content.Context
import android.util.Log
import dwallet.app.android.entities.*
import dwallet.core.bitcoin.application.spv.Network
import dwallet.core.bitcoin.application.spv.SPVNode
import dwallet.core.bitcoin.application.wallet.Coins
import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.bitcoin.protocol.structures.Transaction
import dwallet.core.crypto.hash256
import dwallet.core.crypto.sha1
import dwallet.core.crypto.sha256
import dwallet.core.extensions.ZEROHASH
import dwallet.core.extensions.hexToByteArray
import dwallet.core.extensions.toHexString
import dwallet.core.extensions.toRandomList
import dwallet.core.infrastructure.Event
import dwallet.core.utils.BaseX
import kotlinx.coroutines.experimental.runBlocking
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
        val nodesNetwork = mapOf(Pair(Coins.Bitcoin.name, Network.BitcoinMain), Pair(Coins.BitcoinTestnet.name, Network.BitcoinTestnet), Pair(Coins.Litecoin.name, Network.Litecoin))
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
            info.password = hash256(password.toByteArray()).toHexString()

            db.save(info)

            wallet.externalPrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "external") }
            wallet.changePrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "change") }
            wallet.importedPrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "imported") }
        } else {
            if (info.password != hash256(password.toByteArray()).toHexString()) throw Exception("password error")
            val wallet = Wallet.fromMasterXprvKey(decryptMsg(info.masterPrivKey, password), info.externalKeys?.map { decryptMsg(it.value, password) } ?: listOf(), info.changeKeys?.map { decryptMsg(it.value, password) } ?: listOf(), info.importedKeys?.map { decryptMsg(it.value, password) } ?: listOf(), Coins.values().singleOrNull { it.name == info.coin } ?: coin)!!
            this.wallet = wallet
        }

        val txs = db.selector(WalletTx::class.java).where("utxo", "=", "true").findAll()?.map { Transaction.fromBytes(it.raw.hexToByteArray()) } ?: listOf()
        wallet.insertUtxos(txs)
    }

    private fun prepareForSynchronization() = runBlocking {
        val latestBlocks = db.selector(WalletMerkleblock::class.java).orderBy("height", true).limit(3).findAll() ?: listOf()
        val startHeight = latestBlocks.lastOrNull()?.height ?: 0
        val startHash = latestBlocks.lastOrNull()?.hash ?: String.ZEROHASH

        val peers = (db.selector(WalletPeer::class.java).limit(30).orderBy("id", true).findAll()?.map { Pair(it.host, it.port) }?.toMutableList() ?: coinsBootstrapNodes[coin.name])!!.toRandomList()
        peers.forEach {
            val node = SPVNode(nodesNetwork[coin.name]!!, wallet.dumpKeysToFilterItems(), startHeight, startHash)

            node.onAddr { _, addrs -> addrs.map { db.save(WalletPeer(it.ip, it.port.toInt())) } }

            node.onTx { _, tx ->
                val isUserTx = wallet.insertUtxo(tx) || wallet.isUserTx(tx)
                if (!isUserTx) return@onTx
                Log.v("xxx", "balance: ${wallet.balance}")
                db.save(WalletTx(tx))
            }

            node.onMerkleblock { _, merkleblock -> db.save(WalletMerkleblock(merkleblock)) }

            node.connectAsync(it.first, it.second)
        }
    }


}