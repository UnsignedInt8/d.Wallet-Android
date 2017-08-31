package dwallet.app.android.data

import dwallet.app.android.entities.WalletBasicInfo
import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.crypto.sha1
import dwallet.core.crypto.sha256
import dwallet.core.extensions.toHexString
import dwallet.core.utils.BaseX
import org.xutils.DbManager
import org.xutils.x
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec


/**
 * Created by unsignedint8 on 8/30/17.
 */

class Walletbase(name: String) {

    private val dbFilename = sha256(name.toByteArray()).take(4).toByteArray().toHexString() + ".db"

    private val db by lazy {
        val dbConfigs = DbManager.DaoConfig().setDbName(dbFilename).setDbVersion(1)
        return@lazy x.getDb(dbConfigs)!!
    }

    companion object {
        fun create(passphrase: String): Pair<Wallet, String> {
            Wallet.externalKeysAmount = 5
            Wallet.changeKeysAmount = 10
            val (wallet, mnemonic) = Wallet.create(passphrase)


            return Pair(wallet, mnemonic)
        }

    }

    init {
        val baseInfo = db.findFirst(WalletBasicInfo::class.java)

        val wallet = Wallet.fromMasterXprvKey(baseInfo.masterPrivKey, baseInfo.externalKeys?.map { it.value } ?: listOf(), baseInfo.changeKeys?.map { it.value } ?: listOf(), baseInfo.importedKeys?.map { it.value } ?: listOf())
    }
}