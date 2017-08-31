package dwallet.app.android.data

import android.content.Context
import dwallet.app.android.entities.WalletBasicInfo
import dwallet.app.android.entities.WalletKey
import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.crypto.sha1
import dwallet.core.crypto.sha256
import dwallet.core.extensions.toHexString
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

class Walletbase {

    private var wallet: Wallet

    private var dbFilename = ""

    var name: String
        private set

    private val db by lazy {
        val dbConfigs = DbManager.DaoConfig().setDbName(dbFilename).setDbVersion(1)
        return@lazy x.getDb(dbConfigs)
    }

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

        fun createAsync(passphrase: String): Pair<Wallet, String> {
            Wallet.externalKeysAmount = 5
            Wallet.changeKeysAmount = 10
            return Wallet.create(passphrase)
        }

        fun testWalletExists(ctx: Context, name: String): Boolean {
            return ctx.databaseList().any { it == "${filenameToDatabaseName(name)}.db" }
        }

        private fun filenameToDatabaseName(name: String) = sha256(name.toByteArray()).take(4).toByteArray().toHexString() + ".db"

    }

    constructor(name: String, password: String) {
        this.name = name
        this.dbFilename = filenameToDatabaseName(name)
        val info = db.findFirst(WalletBasicInfo::class.java)
        this.wallet = Wallet.fromMasterXprvKey(info.masterPrivKey, info.externalKeys?.map { decryptMsg(it.value, password) } ?: listOf(), info.changeKeys?.map { decryptMsg(it.value, password) } ?: listOf(), info.importedKeys?.map { decryptMsg(it.value, password) } ?: listOf())!!
    }

    constructor(name: String, password: String, wallet: Wallet) {
        this.wallet = wallet
        this.dbFilename = filenameToDatabaseName(name)
        this.name = name

        val info = WalletBasicInfo(db)
        info.coin = wallet.coin.name
        info.creationTime = Date()
        info.name = name
        info.masterPrivKey = encryptMsg(wallet.masterXprvKey.serializePrivate(), password)

        db.save(info)

        wallet.externalPrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "external") }
        wallet.changePrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "change") }
        wallet.importedPrivKeys.forEach { insertKey(encryptMsg(it.wif, password), "imported") }
    }

    private fun insertKey(wif: String, usage: String) {
        db.save(WalletKey(wif, usage))
    }
}