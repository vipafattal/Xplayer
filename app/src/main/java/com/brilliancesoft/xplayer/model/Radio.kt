package com.brilliancesoft.xplayer.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Radio(val name: String, @SerializedName("radio_url") val url: String)