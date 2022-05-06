package com.star.theBigDipper.util

import com.star.theBigDipper.Constants
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object CommonSchedulers {
    fun <T> io2mainAndTimeout(): SingleTransformer<T, T> {
        return SingleTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).timeout(Constants.TIME_OUT, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
        }
    }
}