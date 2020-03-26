package com.brilliancesoft.xplayer.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Created by ${User} on ${Date}
 */
@Serializable
data class Sura(
    @SerializedName("id")
    val numberInMushaf: String,
    val name: String
)