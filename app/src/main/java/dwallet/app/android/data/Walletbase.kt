package dwallet.app.android.data

import dwallet.app.android.entities.WalletBasicInfo
import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.crypto.sha256
import dwallet.core.extensions.toHexString
import dwallet.core.utils.BaseX
import org.xutils.DbManager
import org.xutils.x
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher


/**
 * Created by unsignedint8 on 8/30/17.
 */

class Walletbase(name: String) {

    val dbFilename = sha256(name.toByteArray()).take(4).toByteArray().toHexString() + ".db"

    private val db by lazy {
        val dbConfigs = DbManager.DaoConfig().setDbName(dbFilename).setDbVersion(1)
        return@lazy x.getDb(dbConfigs)!!
    }

    companion object {
        fun create(passphrase: String) {

        }

        fun encryptMsg(message: String, password: String): String {
            val secret = SecretKeySpec(sha256(password.toByteArray()), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher!!.init(Cipher.ENCRYPT_MODE, secret)
            val cipherText = cipher.doFinal(message.toByteArray(charset("UTF-8")))
            return BaseX.base64.encode(cipherText)
        }

        fun decryptMsg(cipherText: String, password: String): String {
            val secret = SecretKeySpec(sha256(password.toByteArray()), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher!!.init(Cipher.DECRYPT_MODE, secret)
            return String(cipher.doFinal(BaseX.base64.decode(cipherText)))
        }
    }

    init {
        val baseInfo = db.findFirst(WalletBasicInfo::class.java)

        val wallet = Wallet.fromMasterXprvKey(baseInfo.masterPrivKey, baseInfo.externalKeys?.map { it.value } ?: listOf(), baseInfo.changeKeys?.map { it.value } ?: listOf(), baseInfo.importedKeys?.map { it.value } ?: listOf())
    }
}