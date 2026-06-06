package com.unreal.medisageai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ChatSessionEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class MediSageDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    companion object {
        const val NAME = "medisage.db"
    }
}
