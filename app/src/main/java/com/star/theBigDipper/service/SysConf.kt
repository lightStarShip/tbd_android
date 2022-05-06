package com.star.theBigDipper.service

import com.star.theBigDipper.Constants
import com.star.theBigDipper.util.Utils

object SysConf {
    const val KEY_CACHED_POOL_IN_USE = "KEY_CACHED_POOL_IN_USE_OF_NET_TYPE_%s"
    const val KEY_CACHED_POOL_NAME_IN_USE = "KEY_CACHED_POOL_NAME_IN_USE_OF_NET_TYPE_%s"
    const val KEY_CACHED_MINER_ID_IN_USE = "KEY_CACHED_MINER_ID_IN_USE_OF_%s"
    var CurPoolAddress = ""
    var CurPoolName = ""
    var CurMinerID = ""
    var PacketsBalance = 0.0
    var PacketsCredit = 0.0
    fun changeCurPool(address: String, newPool: String) {
        CurPoolName = newPool
        if (CurPoolAddress == address) {
            return
        }
        CurPoolAddress = address
        val netType = Utils.getInt(Constants.NET_TYPE, Constants.DEFAULT_MAIN_NET)
        if (netType == Constants.DEFAULT_MAIN_NET) {
            val keyCachedPool = String.format(SysConf.KEY_CACHED_POOL_IN_USE, Constants.MAIN_TOKEN_ADDRESS)
            val keyCachedPoolName = String.format(SysConf.KEY_CACHED_POOL_NAME_IN_USE, Constants.MAIN_TOKEN_ADDRESS)
            Utils.saveData(keyCachedPool, address)
            Utils.saveData(keyCachedPoolName, CurPoolName)

        } else if (netType == Constants.TEST_NET) {
            val keyCachedPool = String.format(SysConf.KEY_CACHED_POOL_IN_USE, Constants.TOKEN_ADDRESS)
            val keyCachedPoolName = String.format(SysConf.KEY_CACHED_POOL_NAME_IN_USE, Constants.TOKEN_ADDRESS)
            Utils.saveData(keyCachedPool, address)
            Utils.saveData(keyCachedPoolName, CurPoolName)

        }

        val mKey = String.format(KEY_CACHED_MINER_ID_IN_USE, CurPoolAddress)
        CurMinerID = Utils.getString(mKey, "")
    }

    fun setCurMiner(newMiner: String) {
        if (CurMinerID == newMiner) {
            return
        }
        CurMinerID = newMiner
        val mKey = String.format(KEY_CACHED_MINER_ID_IN_USE, CurPoolAddress)
        Utils.saveData(mKey, newMiner)
    }
}