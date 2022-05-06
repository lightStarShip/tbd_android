package com.star.theBigDipper.widget.navigator

import androidx.fragment.app.Fragment


interface FragmentNavigatorAdapter {
    fun onCreateFragment(position: Int): Fragment
    fun getTag(position: Int): String?
    fun getCount(): Int
}