cmake_minimum_required(VERSION 3.18.1)
project(native-rag-engine)

# 1. Define paths
set(LIB_DIR ${CMAKE_SOURCE_DIR}/lib)

# 2. Add FAISS and required system dependencies
add_library(faiss STATIC IMPORTED)
set_target_properties(faiss PROPERTIES IMPORTED_LOCATION ${LIB_DIR}/libfaiss.a)

# 3. Build native library
add_library(native-lib SHARED native-lib.cpp)

# 4. Link dependencies
# Note: FAISS often depends on BLAS/LAPACK; ensure those are also linked if not bundled in libfaiss.a
target_link_libraries(native-lib
        faiss
        log
        android
)