package com.star.theBigDipper.model.bean

import com.google.gson.annotations.SerializedName

class UserPoolData(@SerializedName("left_token_balance") var expire: Double, @SerializedName("token_balance") var token: Double, @SerializedName("traffic_balance") var packets: Double, @SerializedName("used_traffic") var credit: Double, ) {
    constructor() : this(0.0, 0.0, 0.0, 0.0)

}