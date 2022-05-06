package com.star.theBigDipper.model

import android.text.TextUtils
import androidLib.AndroidLib
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.star.theBigDipper.model.bean.MinerBean
import com.star.theBigDipper.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.collections.ArrayList

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/30 2:10 PM
 */
class MineMachineListModel : BaseModel() {
    fun getMineMachine(address: String, random: Int): Single<List<MinerBean>> {
        return Single.create(SingleOnSubscribe<List<MinerBean>> { emitter ->
            val minersMachineStr = AndroidLib.randomMiner(address, random.toLong())
            if(TextUtils.isEmpty(minersMachineStr)|| minersMachineStr == "null"){
                emitter.onSuccess(ArrayList<MinerBean>())
                return@SingleOnSubscribe
            }
            val type = object : TypeToken<ArrayList<MinerBean>>() {}.type
            val miners = Gson().fromJson<ArrayList<MinerBean>>(minersMachineStr, type)
            miners.sortByDescending { miner -> miner.zone }
            emitter.onSuccess(miners)
        }).compose(CommonSchedulers.io2mainAndTimeout<List<MinerBean>>())

    }

    suspend fun ping(address: String): Double {
        return withContext(Dispatchers.IO) {
            val pingJson = AndroidLib.testPing(address)
            if (TextUtils.isEmpty(pingJson)) {
                return@withContext -1.0
            } else {
                val obj = JSONObject(pingJson)
                obj.optDouble("ping")
            }
        }
    }

}