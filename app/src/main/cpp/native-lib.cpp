#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

// Pass the AssetManager from Java to C++
extern "C" JNIEXPORT void JNICALL
Java_com_example_app_NativeSearchEngine_loadIndex(JNIEnv* env, jobject, jobject assetManager, jstring filename) {
AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
const char* file = env->GetStringUTFChars(filename, 0);

AAsset* asset = AAssetManager_open(mgr, file, AASSET_MODE_BUFFER);
// Now you have a handle to your file!

// Note: faiss::read_index requires a FILE* or a memory buffer.
// Since Android assets are compressed inside the APK,
// it is easiest to copy the asset to your app's internal cache directory first.
}