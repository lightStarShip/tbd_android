package com.star.theBigDipper.model.bean

import com.google.gson.annotations.SerializedName

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:28 PM
 */
class WalletBean {
    @SerializedName("Main")
    var main: String = ""

    @SerializedName("Sub")
    var sub: String = ""

    @SerializedName("Eth")
    var eth: Double = 0.0

    @SerializedName("Hop")
    var hop: Double = 0.0

    @SerializedName("Approved")
    var approved: Double = 0.0
    var IsOpen: Boolean = false

}