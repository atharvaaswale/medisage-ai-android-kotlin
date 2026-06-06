package com.unreal.medisageai.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.unreal.medisageai.data.Sender
import java.util.UUID

/**
 * A single message inside a [ChatSessionEntity]. Deleting the parent session
 * cascades to its messages, and an index on [sessionId] keeps the relational
 * fetch in [ChatDao] fast.
 */
@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class ChatMessageEntity(
    @PrimaryKey
    val messageId: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val text: String,
    val sender: Sender,
    val timestamp: Long = System.currentTimeMillis(),
)
