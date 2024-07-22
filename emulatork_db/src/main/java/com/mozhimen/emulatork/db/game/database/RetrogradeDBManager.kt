package com.mozhimen.emulatork.db.game.database

/**
 * @ClassName RetrogradeDBManager
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/22
 * @Version 1.0
 */
class RetrogradeDBManager {
    val db: RetrogradeDatabase by lazy { RetrogradeDatabase.getRetrogradeDb() }

    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private object INSTANCE {
        val holder = RetrogradeDBManager()
    }
}