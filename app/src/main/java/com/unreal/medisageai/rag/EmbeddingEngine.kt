package com.unreal.medisageai.rag

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import java.nio.FloatBuffer
import java.nio.LongBuffer

class EmbeddingEngine(private val context: Context) {

    private var ortEnv: OrtEnvironment? = null
    private var ortSession: OrtSession? = null
    private var tokenizer: BertTokenizer? = null

    private val modelDir = "embed_model"
    private val onnxFile = "model_qint8_arm64.onnx"

    val dim: Int get() = 384

    fun load(): Boolean {
        try {
            ortEnv = OrtEnvironment.getEnvironment()
            val modelStream = context.assets.open("$modelDir/$onnxFile")
            val modelBytes = modelStream.readBytes()
            modelStream.close()
            ortSession = ortEnv!!.createSession(modelBytes)
            val vocabStream = context.assets.open("$modelDir/vocab.txt")
            tokenizer = BertTokenizer.load(vocabStream)
            vocabStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun embed(text: String): FloatArray? {
        val env = ortEnv ?: return null
        val session = ortSession ?: return null
        val tok = tokenizer ?: return null

        val encoding = tok.encode(text)
        val batchSize = 1L
        val seqLen = 256L

        val inputIds = LongArray(batchSize.toInt() * seqLen.toInt())
        val attentionMask = LongArray(batchSize.toInt() * seqLen.toInt())
        val tokenTypeIds = LongArray(batchSize.toInt() * seqLen.toInt())

        for (i in 0 until seqLen.toInt()) {
            inputIds[i] = encoding.inputIds[i].toLong()
            attentionMask[i] = encoding.attentionMask[i].toLong()
            tokenTypeIds[i] = encoding.tokenTypeIds[i].toLong()
        }

        val inputShape = longArrayOf(batchSize, seqLen)

        return try {
            val inputIdsTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(inputIds), inputShape)
            val attentionMaskTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(attentionMask), inputShape)
            val tokenTypeIdsTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(tokenTypeIds), inputShape)

            val inputs = mapOf(
                "input_ids" to inputIdsTensor,
                "attention_mask" to attentionMaskTensor,
                "token_type_ids" to tokenTypeIdsTensor,
            )

            val results = session.run(inputs)
            val outputTensor = results.get(0) as OnnxTensor
            val outputBuffer = outputTensor.floatBuffer

            // output shape: (1, seq_len, 384) - need mean pooling
            val tokenEmbeddings = FloatArray(seqLen.toInt() * dim) { i -> outputBuffer.get(i) }

            val pooled = meanPool(tokenEmbeddings, encoding.attentionMask)
            val normalized = normalize(pooled)

            results.close()
            inputIdsTensor.close()
            attentionMaskTensor.close()
            tokenTypeIdsTensor.close()

            normalized
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun meanPool(tokenEmbeds: FloatArray, attentionMask: IntArray): FloatArray {
        val result = FloatArray(dim)
        val maskSum = attentionMask.sum().coerceAtLeast(1)
        for (t in tokenEmbeds.indices) {
            val tokenIdx = t / dim
            val dimIdx = t % dim
            result[dimIdx] += tokenEmbeds[t] * attentionMask[tokenIdx]
        }
        for (i in result.indices) {
            result[i] /= maskSum.toFloat()
        }
        return result
    }

    private fun normalize(vec: FloatArray): FloatArray {
        var norm = 0.0f
        for (v in vec) norm += v * v
        norm = kotlin.math.sqrt(norm.toDouble()).toFloat()
        if (norm > 0f) {
            for (i in vec.indices) vec[i] /= norm
        }
        return vec
    }

    fun close() {
        ortSession?.close()
        ortEnv?.close()
    }
}
