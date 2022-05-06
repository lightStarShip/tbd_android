package com.star.theBigDipper.model.bean

import com.google.gson.annotations.SerializedName

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/9 9:55 AM
 */
class OwnPool(@SerializedName("MainAddr") var address: String? = null, @SerializedName("Name") var name: String? = null, @SerializedName("Email") var email: String? = null, @SerializedName("GTN") var mortgageNumber: Double = 0.0, @SerializedName("Url") var websiteAddress: String? = null)