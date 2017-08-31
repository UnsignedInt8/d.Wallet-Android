package dwallet.app.android.entities

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

/**
 * Created by unsignedint8 on 8/30/17.
 */

@Table(name = "keys")
class WalletKey() {

    constructor(value: String, usage: String) : this() {
        this.value = value
        this.usage = usage
    }

    @Column(name = "id", isId = true, autoGen = true)
    var id = 0L

    @Column(name = "value") // encrypted data
    lateinit var value: String

    @Column(name = "usage") // external/change/imported
    lateinit var usage: String
}