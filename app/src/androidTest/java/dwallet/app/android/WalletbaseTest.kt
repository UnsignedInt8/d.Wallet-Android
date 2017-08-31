package dwallet.app.android

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import dwallet.app.android.data.Walletbase
import dwallet.app.android.entities.WalletBasicInfo
import dwallet.core.bitcoin.application.bip39.BIP39
import dwallet.core.crypto.Crypto

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.security.SecureRandom
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class WalletbaseTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("dwallet.app.android", appContext.packageName)
    }

    init {
        Crypto.setupCryptoProvider()
    }

    @Test
    fun testEncrypting() {
        val raw = "11223344"
        val cipherText = Walletbase.encryptMsg(raw, "abc")
        val text = Walletbase.decryptMsg(cipherText, "abc")
        assertEquals(raw, text)
    }

    @Test
    fun testMnemonic() {
        val seed = BIP39.mnemonicToSeed(BIP39.getMnemonic(SecureRandom.getSeed(32)))
        assertEquals(64, seed.size)
    }
}
