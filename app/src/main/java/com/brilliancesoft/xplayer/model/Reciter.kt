package com.brilliancesoft.xplayer.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Created by ${User} on ${Date}
 */
@Serializable
data class Reciter(
    val id: String,
    val name: String,
    val rewaya: String,
    @SerializedName("Server") val servers: String,
    @SerializedName("count") val numberOfAvailableSuras: Int = 114
)