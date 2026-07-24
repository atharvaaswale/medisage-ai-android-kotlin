package com.unreal.medisageai.rag

import android.content.Context
import com.unreal.medisageai.NativeSearchEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class RagEngine(private val context: Context) {

    private val embedder = EmbeddingEngine(context)
    private val nativeSearch = NativeSearchEngine()
    private var metadata: List<DocumentEntry> = emptyList()
    private var ready = false
    private var cacheDir: String? = null

    data class DocumentEntry(
        val id: Int,
        val uuid: String,
        val content: String,
    )

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        if (ready) return@withContext true
        try {
            val cache = File(context.cacheDir, "rag_index")
            cache.mkdirs()
            cacheDir = cache.absolutePath

            for (assetName in listOf("vectors.bin", "vectors.hdr", "metadata.json")) {
                context.assets.open(assetName).use { input ->
                    File(cache, assetName).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            nativeSearch.loadIndex(cacheDir!!)

            val metaFile = File(cache, "metadata.json")
            val jsonStr = metaFile.readText()
            val json = JSONObject(jsonStr)
            val docsArray = json.getJSONArray("documents")
            metadata = (0 until docsArray.length()).map { i ->
                val doc = docsArray.getJSONObject(i)
                DocumentEntry(
                    id = doc.getInt("id"),
                    uuid = doc.getString("uuid"),
                    content = doc.getString("content"),
                )
            }

            embedder.load()
            ready = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun retrieve(query: String, topK: Int = 5): List<DocumentEntry> = withContext(Dispatchers.IO) {
        if (!ready) initialize()
        if (!ready) return@withContext emptyList()

        val queryVec = embedder.embed(query) ?: return@withContext emptyList()
        val nativeResults = nativeSearch.search(queryVec, topK) ?: return@withContext emptyList()

        val documents = mutableListOf<DocumentEntry>()
        for (i in nativeResults.indices step 2) {
            val idx = nativeResults[i].toInt()
            if (idx >= 0 && idx < metadata.size) {
                documents.add(metadata[idx])
            }
        }
        documents
    }

    suspend fun retrieveContext(query: String, topK: Int = 5): String = withContext(Dispatchers.IO) {
        val docs = retrieve(query, topK)
        if (docs.isEmpty()) return@withContext ""
        docs.withIndex().joinToString("\n\n") { (i, doc) ->
            "[${i + 1}] ${doc.content.trim()}"
        }
    }

    fun close() {
        embedder.close()
    }
}
