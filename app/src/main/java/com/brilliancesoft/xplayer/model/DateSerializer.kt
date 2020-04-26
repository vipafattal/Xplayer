package com.brilliancesoft.xplayer.model

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date?> {
    private val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.getDefault())

    override fun serialize(encoder: Encoder, obj: Date?) {
        if (obj != null)
            encoder.encodeString(df.format(obj))
    }

    override fun deserialize(decoder: Decoder): Date? {
        return df.parse(decoder.decodeString())
    }

}
