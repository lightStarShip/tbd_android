package com.star.theBigDipper.model

import androidLib.AndroidLib
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.star.theBigDipper.model.bean.OwnPool
import com.star.theBigDipper.model.bean.UserPoolData
import com.star.theBigDipper.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe


/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:07 PM
 */
class OwnPoolModel : BaseModel() {
    fun getPoolDataOfUser(refresh: Boolean): Single<ArrayList<OwnPool>> {
        return Single.create(SingleOnSubscribe<ArrayList<OwnPool>> { emitter ->
            if (refresh) {
                AndroidLib.syncSubPoolsData()
            }
            val poolsStr = AndroidLib.getSubPools()
            if (poolsStr == "null") {
                val ownPools = ArrayList<OwnPool>()
                emitter.onSuccess(ownPools)
                return@SingleOnSubscribe
            }
            poolsStr?.let {
                val groupListType = object : TypeToken<ArrayList<OwnPool>>() {}.type
                val ownPools = Gson().fromJson<ArrayList<OwnPool>>(poolsStr, groupListType)
                emitter.onSuccess(ownPools)
                return@SingleOnSubscribe
            }
            emitter.onError(Throwable("data is empty!"))
        }).compose(CommonSchedulers.io2mainAndTimeout<ArrayList<OwnPool>>())

    }

    fun getPacketsByPool(user: String, pool: String): Single<UserPoolData> {
        return Single.create(SingleOnSubscribe<UserPoolData> { emitter ->
            val jsonStr = AndroidLib.getUserDate(user, pool)
            val userPool = Gson().fromJson<UserPoolData>(jsonStr, UserPoolData::class.java)
            emitter.onSuccess(userPool)
        }).compose(CommonSchedulers.io2mainAndTimeout<UserPoolData>())


    }
}