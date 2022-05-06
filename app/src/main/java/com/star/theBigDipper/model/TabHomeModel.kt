package com.star.theBigDipper.model

import androidLib.AndroidLib
import com.google.gson.Gson
import com.star.theBigDipper.model.bean.UserPoolData
import com.star.theBigDipper.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe

/**
 * @description:
 * @author: mr.x
 * @date :   2020/6/4 12:11 PM
 */
class TabHomeModel : BaseModel() {
    fun getPool(user: String, pool: String): Single<UserPoolData> {
        return Single.create(SingleOnSubscribe<UserPoolData> { emitter ->
            val jsonStr = AndroidLib.getUserDate(user, pool)
            val userPool = Gson().fromJson<UserPoolData>(jsonStr, UserPoolData::class.java)
            emitter.onSuccess(userPool)
        }).compose(CommonSchedulers.io2mainAndTimeout<UserPoolData>())
    }

    fun openWallet(password: String): Single<Int> {
        return Single.create(SingleOnSubscribe<Int> { emitter ->
            val resultCode = AndroidLib.openWallet(password)
            emitter.onSuccess(resultCode)
        }).compose(CommonSchedulers.io2mainAndTimeout<Int>())
    }

}