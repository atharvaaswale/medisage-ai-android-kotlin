package com.unreal.medisageai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    /**
     * Observes every session with its messages, newest session first. [Transaction]
     * guarantees the session row and its messages are read consistently.
     */
    @Transaction
    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    fun observeSessions(): Flow<List<PopulatedChatSession>>

    /** Observes a single session and its messages, emitting null if it is deleted. */
    @Transaction
    @Query("SELECT * FROM chat_sessions WHERE sessionId = :sessionId")
    fun observeSession(sessionId: String): Flow<PopulatedChatSession?>

    @Upsert
    suspend fun upsertSession(session: ChatSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_sessions SET updatedAt = :timestamp WHERE sessionId = :sessionId")
    suspend fun touchSession(sessionId: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteSession(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)
}
