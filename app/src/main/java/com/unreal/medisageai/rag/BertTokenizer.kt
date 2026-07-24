package com.unreal.medisageai.rag

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class BertTokenizer(private val vocab: Map<String, Int>) {

    companion object {
        private const val CLS = "[CLS]"
        private const val SEP = "[SEP]"
        private const val UNK = "[UNK]"
        private const val PAD = "[PAD]"
        private const val MAX_LEN = 256

        fun load(inputStream: InputStream): BertTokenizer {
            val vocab = mutableMapOf<String, Int>()
            BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                var index = 0
                var line: String? = reader.readLine()
                while (line != null) {
                    vocab[line.trim()] = index++
                    line = reader.readLine()
                }
            }
            return BertTokenizer(vocab)
        }

        private val punctuationSplit = Regex("""([\w]+|[\x00-\x7F])""")
    }

    private val tokenIds: Map<String, Int> = vocab
    private val idTokens: Map<Int, String> = vocab.entries.associate { (k, v) -> v to k }

    fun tokenize(text: String): List<String> {
        val cleaned = text.lowercase()
            .replace(Regex("""[^\x00-\x7F]"""), " ") // remove non-ASCII
            .replace(Regex("""\s+"""), " ")
            .trim()

        val words = cleaned.split(" ")
        val tokens = mutableListOf<String>()
        for (word in words) {
            if (word.isEmpty()) continue
            tokens.addAll(wordpieceTokenize(word))
        }
        return tokens
    }

    private fun wordpieceTokenize(word: String): List<String> {
        if (word.isEmpty()) return emptyList()
        val tokens = mutableListOf<String>()
        var remaining = word
        while (remaining.isNotEmpty()) {
            var found = false
            for (end in remaining.length downTo 1) {
                val sub = if (tokens.isEmpty()) remaining.substring(0, end)
                          else "##" + remaining.substring(0, end)
                if (vocab.containsKey(sub)) {
                    tokens.add(sub)
                    remaining = remaining.substring(end)
                    found = true
                    break
                }
            }
            if (!found) {
                tokens.add(UNK)
                break
            }
        }
        return tokens
    }

    fun encode(text: String): EncodingResult {
        val tokens = tokenize(text)
        val inputIds = mutableListOf(vocab[CLS]!!)
        val tokenTypeIds = mutableListOf(0)
        val attentionMask = mutableListOf(1)

        for (token in tokens) {
            val id = vocab[token] ?: vocab[UNK]!!
            inputIds.add(id)
            tokenTypeIds.add(0)
            attentionMask.add(1)
            if (inputIds.size >= MAX_LEN - 1) break
        }

        inputIds.add(vocab[SEP]!!)
        tokenTypeIds.add(0)
        attentionMask.add(1)

        while (inputIds.size < MAX_LEN) {
            inputIds.add(vocab[PAD]!!)
            tokenTypeIds.add(0)
            attentionMask.add(0)
        }

        return EncodingResult(
            inputIds = inputIds.toIntArray(),
            attentionMask = attentionMask.toIntArray(),
            tokenTypeIds = tokenTypeIds.toIntArray(),
        )
    }

    data class EncodingResult(
        val inputIds: IntArray,
        val attentionMask: IntArray,
        val tokenTypeIds: IntArray,
    )
}
