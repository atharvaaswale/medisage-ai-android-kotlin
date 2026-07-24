#include <jni.h>
#include <android/log.h>
#include <vector>
#include "index_reader.h"

#define LOG_TAG "NativeRAG"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static VectorIndex g_index;

extern "C" JNIEXPORT void JNICALL
Java_com_unreal_medisageai_NativeSearchEngine_loadIndex(
        JNIEnv* env, jobject /*thiz*/, jstring path) {
    const char* dir = env->GetStringUTFChars(path, nullptr);
    LOGI("loadIndex: %s", dir);

    bool ok = g_index.load(std::string(dir));
    env->ReleaseStringUTFChars(path, dir);

    if (!ok) {
        LOGE("Failed to load index from %s", dir);
        return;
    }
    LOGI("Index loaded: dim=%d, ntotal=%d", g_index.dimension(), g_index.numVectors());
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_unreal_medisageai_NativeSearchEngine_search(
        JNIEnv* env, jobject /*thiz*/, jfloatArray queryVector, jint k) {
    if (!g_index.loaded()) {
        LOGE("Index not loaded");
        return nullptr;
    }

    jsize queryLen = env->GetArrayLength(queryVector);
    if (queryLen != g_index.dimension()) {
        LOGE("Query dimension mismatch: expected %d, got %d", g_index.dimension(), queryLen);
        return nullptr;
    }

    jfloat* queryData = env->GetFloatArrayElements(queryVector, nullptr);
    auto results = g_index.search(queryData, k);
    env->ReleaseFloatArrayElements(queryVector, queryData, JNI_ABORT);

    int resultCount = static_cast<int>(results.size());
    jsize outLen = resultCount * 2;
    jfloatArray resultArray = env->NewFloatArray(outLen);
    if (resultArray == nullptr) return nullptr;

    std::vector<jfloat> flat(outLen);
    for (int i = 0; i < resultCount; ++i) {
        flat[i * 2 + 0] = static_cast<jfloat>(results[i].index);
        flat[i * 2 + 1] = results[i].distance;
    }
    env->SetFloatArrayRegion(resultArray, 0, outLen, flat.data());

    return resultArray;
}
