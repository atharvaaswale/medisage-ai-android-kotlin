package com.unreal.medisageai.data.local

import androidx.room.TypeConverter
import com.unreal.medisageai.data.Sender

/**
 * Persists the [Sender] enum as its name so Room can store it in a TEXT column.
 */
class Converters {

    @TypeConverter
    fun fromSender(sender: Sender): String = sender.name

    @TypeConverter
    fun toSender(value: String): Sender = Sender.valueOf(value)
}
