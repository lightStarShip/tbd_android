package com.star.theBigDipper.widget.navigator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.star.theBigDipper.R
import java.util.*

class BottomNavigatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val mImageArray = intArrayOf(R.drawable.tab_home_normal, R.drawable.tab_recharge_normal, R.drawable.tab_wallet_normal)
    private val mImageSelectedArray = intArrayOf(R.drawable.tab_home_selected, R.drawable.tab_recharge_selected, R.drawable.tab_wallet_selected)
    private val mTextArray = intArrayOf(R.string.tab_home, R.string.tab_flow_market, R.string.tab_wallet)
    var mOnBottomNavigatorViewItemClickListener: OnBottomNavigatorViewItemClickListener? = null

    interface OnBottomNavigatorViewItemClickListener {
        fun onBottomNavigatorViewItemClick(position: Int, view: View?)
    }

    private val frameLayoutList: MutableList<FrameLayout> = ArrayList()
    private fun initView() {
        for (i in frameLayoutList.indices) {
            val frameLayout = frameLayoutList[i]
            val icon = frameLayout.findViewById<ImageView>(R.id.tab_title_iv)
            val text = frameLayout.findViewById<TextView>(R.id.tab_title_tv)
            val badge = frameLayout.findViewById<TextView>(R.id.tab_title_badge_tv)
            icon.setImageResource(mImageArray[i])
            text.setText(mTextArray[i])
        }
    }

    fun select(position: Int) {
        for (i in frameLayoutList.indices) {
            val child: View = frameLayoutList[i]
            if (i == position) {
                selectChild(child, true, i)
            } else {
                selectChild(child, false, i)
            }
        }
    }

    private fun selectChild(child: View, select: Boolean, position: Int) {
        child.isSelected = select
        val icon = child.findViewById<ImageView>(R.id.tab_title_iv)
        val text = child.findViewById<TextView>(R.id.tab_title_tv)
        val badge = child.findViewById<TextView>(R.id.tab_title_badge_tv)
        if (select) {
            icon.setImageResource(mImageSelectedArray[position])
            text.setTextColor(resources.getColor(R.color.color_000000))
        } else {
            icon.setImageResource(mImageArray[position])
            text.setTextColor(resources.getColor(R.color.color_a4a4a7))
        }
    }

    fun setOnBottomNavigatorViewItemClickListener(listener: OnBottomNavigatorViewItemClickListener?) {
        mOnBottomNavigatorViewItemClickListener = listener
    }

    fun showBadgeView(position: Int, number: Int, show: Boolean) {
        if (position < 0 || position >= frameLayoutList.size) {
            return
        }
        val badge = frameLayoutList[position].findViewById<TextView>(R.id.tab_title_badge_tv)
        badge.visibility = if (show) View.VISIBLE else View.GONE
        if (number > 0) {
            badge.text = number.toString() + ""
        } else {
            badge.text = ""
        }
    }

    init {
        orientation = HORIZONTAL
        View.inflate(context, R.layout.layout_bottom_navigator, this)
        val bottomNavigatorLine = findViewById<View>(R.id.bottom_navigator_line)
        val home = findViewById<FrameLayout>(R.id.bottom_navigator_home)
        val pool = findViewById<FrameLayout>(R.id.bottom_navigator_pool)
        val wallet = findViewById<FrameLayout>(R.id.bottom_navigator_wallet)
        frameLayoutList.add(home)
        frameLayoutList.add(pool)
        frameLayoutList.add(wallet)
        for (i in frameLayoutList.indices) {
            val view: View = frameLayoutList[i]
            view.setOnClickListener {
                if (null != mOnBottomNavigatorViewItemClickListener) {
                    mOnBottomNavigatorViewItemClickListener!!.onBottomNavigatorViewItemClick(i, view)
                }
            }
        }
        initView()
    }
}