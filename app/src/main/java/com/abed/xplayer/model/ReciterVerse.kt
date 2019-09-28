package com.abed.xplayer.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ReciterVerse(val id:Int, val name:String, @SerializedName("audio_url") val audioUrl:String,@SerializedName("is_completed") val isCompleted:Boolean,val rewaya:String )