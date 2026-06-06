package com.unreal.medisageai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A single chat conversation. One session owns many [ChatMessageEntity] rows.
 */
@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey
    val sessionId: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
