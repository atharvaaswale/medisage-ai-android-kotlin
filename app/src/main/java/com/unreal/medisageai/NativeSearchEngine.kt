package com.unreal.medisageai

class NativeSearchEngine {
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun loadIndex(path: String)
    external fun search(queryVector: FloatArray, k: Int): FloatArray
}