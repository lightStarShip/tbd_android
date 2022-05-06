package com.star.theBigDipper.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.star.theBigDipper.R

class GuideAdapter(private val mContext: Context, private val images: IntArray) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = View.inflate(mContext, R.layout.item_pager, null)
        view.findViewById<View>(R.id.image_iv).setBackgroundResource(images[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        container.removeView(o as View)
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

}