package com.unreal.medisageai.data

import com.google.ai.client.generativeai.GenerativeModel
import com.unreal.medisageai.rag.RagEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MediSageRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val ragEngine: RagEngine,
) : MediSageRepository {

    override fun sendPrompt(prompt: String): Flow<ChatResponse> = flow {
        emit(ChatResponse.Loading)

        val context = ragEngine.retrieveContext(prompt)
        val augmentedPrompt = if (context.isNotEmpty()) {
            """Relevant medical context:
$context

User question: $prompt

Answer the question based on the provided context and your medical knowledge."""
        } else {
            prompt
        }

        val buffer = StringBuilder()
        generativeModel.generateContentStream(augmentedPrompt).collect { chunk ->
            chunk.text?.let { delta ->
                buffer.append(delta)
                emit(ChatResponse.Streaming(buffer.toString()))
            }
        }

        emit(
            ChatResponse.Success(
                ChatMessage(
                    text = buffer.toString(),
                    sender = Sender.AI,
                )
            )
        )
    }.catch { e ->
        emit(ChatResponse.Error(e.message ?: "Failed to reach Gemini", e))
    }
}
