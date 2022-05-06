package com.star.theBigDipper.widget.navigator

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import java.util.*

class BottomNavigatorAdapter(private val mContext: Context) : FragmentNavigatorAdapter {
    class TabInfo(val tag: String, val clss: Class<*>, val args: Bundle?)

    private val tabInfoArrayList = ArrayList<TabInfo>()

    override fun onCreateFragment(position: Int): Fragment {
        val tabInfo = tabInfoArrayList[position]
        return Fragment.instantiate(mContext, tabInfo.clss.name, tabInfo.args)
    }

    override fun getTag(position: Int): String {
        return tabInfoArrayList[position].tag
    }

    override fun getCount(): Int {
        return tabInfoArrayList.size
    }

    fun addTab(tabInfo: TabInfo) {
        tabInfoArrayList.add(tabInfo)
    }

}