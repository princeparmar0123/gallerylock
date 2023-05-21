package com.calculator.vault.lock.hide.photo.video.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdsData {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null
}