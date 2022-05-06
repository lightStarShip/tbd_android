package com.star.theBigDipper.guide

import android.graphics.RectF
import zhy.com.highlight.HighLight
import zhy.com.highlight.position.OnBaseCallback

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class PirateOnBottomPosCallback : OnBaseCallback() {

    override fun getPosition(rightMargin: Float, bottomMargin: Float, rectF: RectF?, marginInfo: HighLight.MarginInfo?) {
        marginInfo?.leftMargin = rectF?.right?.minus(rectF.width())
        marginInfo!!.topMargin = rectF!!.top + rectF.height() + offset
    }
}