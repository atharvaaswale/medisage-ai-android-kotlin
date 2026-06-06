package com.unreal.medisageai.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A chat session together with all of its messages, assembled by Room from the
 * one-to-many relationship between [ChatSessionEntity] and [ChatMessageEntity].
 */
data class PopulatedChatSession(
    @Embedded
    val session: ChatSessionEntity,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId",
    )
    val messages: List<ChatMessageEntity>,
)
