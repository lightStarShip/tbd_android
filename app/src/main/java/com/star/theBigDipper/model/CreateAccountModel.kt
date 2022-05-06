package com.star.theBigDipper.model

import androidLib.AndroidLib
import com.star.theBigDipper.util.CommonSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe


/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/25 11:17 AM
 */
class CreateAccountModel : InitServiceModel() {
    fun createAccount(password: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val newWallet = AndroidLib.newWallet(password)
            emitter.onSuccess(newWallet)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())
    }


    fun importWallet(walletStr: String, password: String): Single<Int> {
        return Single.create(SingleOnSubscribe<Int> { emitter ->
            val result = AndroidLib.importWallet(walletStr, password)
            emitter.onSuccess(result)
        }).compose(CommonSchedulers.io2mainAndTimeout<Int>())

    }

    fun importImtokenPrivateKey(password: String, privateKey: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val wallet = AndroidLib.importWalletPrivate(privateKey, password)
            emitter.onSuccess(wallet)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())

    }

}