package com.abed.xplayer.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Created by ${User} on ${Date}
 */


@Serializable
data class Language(val id: String, val language: String, @SerializedName("json") val reciterLinks: String)