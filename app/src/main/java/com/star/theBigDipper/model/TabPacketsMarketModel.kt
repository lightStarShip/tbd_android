package com.star.theBigDipper.model

import androidLib.AndroidLib
import com.star.theBigDipper.model.bean.MinePoolBean
import com.star.theBigDipper.model.bean.PoolStat
import com.star.theBigDipper.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import org.json.JSONObject
import java.util.*

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 2:59 PM
 */
class TabPacketsMarketModel : BaseModel() {
    fun getPoolInfo(syncAllPools: Boolean): Single<List<MinePoolBean>> {
        return Single.create(SingleOnSubscribe<List<MinePoolBean>> { emitter ->
            if (syncAllPools) {
                AndroidLib.syncAllPoolsData()
            }
            val jsonStr = AndroidLib.poolInfosInMarket()
            val minePoolBeans: MutableList<MinePoolBean> = ArrayList()
            val pools = JSONObject(jsonStr)
            val it: Iterator<*> = pools.keys()
            while (it.hasNext()) {
                val key = it.next() as String
                val p = pools.optJSONObject(key) ?: continue
                val bean = MinePoolBean()
                bean.address = key
                bean.name = p.optString("Name")
                bean.email = p.optString("Email")
                bean.mortgageNumber = p.optDouble("GTN")
                bean.websiteAddress = p.optString("Url")
                val poolStat = p.optJSONObject("pool_stat")
                if(poolStat != null){
                    bean.pool_stat=PoolStat()
                    bean.pool_stat!!.total_used_g_bytes = poolStat.optDouble("total_used_g_bytes")
                    bean.pool_stat!!.total_charged_user_cnt = poolStat.optInt("total_charged_user_cnt")
                    bean.pool_stat!!.last_day_used_m_bytes = poolStat.optDouble("last_day_used_m_bytes")
                    bean.pool_stat!!.last_month_used_g_bytes = poolStat.optDouble("last_month_used_g_bytes")
                }
                minePoolBeans.add(bean)
            }
            minePoolBeans.sortByDescending { it.pool_stat?.total_used_g_bytes }
            emitter.onSuccess(minePoolBeans)
        }).compose(CommonSchedulers.io2mainAndTimeout<List<MinePoolBean>>())

    }
}