package com.star.theBigDipper.model.bean

import androidx.databinding.ObservableField
import com.google.gson.annotations.SerializedName

class MinerBean constructor(@SerializedName("sub_addr") var address: String, @SerializedName("zone") var zone: String, var minerPoolAdd: String, var time: ObservableField<String>, var selected: Boolean) {
    constructor() : this("", "", "", ObservableField<String>(""), false)

}