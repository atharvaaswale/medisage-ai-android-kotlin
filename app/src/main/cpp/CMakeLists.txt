cmake_minimum_required(VERSION 3.18.1)
project(native-rag-engine)

add_library(native-lib SHARED
        native-lib.cpp
        index_reader.cpp
)

target_include_directories(native-lib PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

target_link_libraries(native-lib
        log
        android
)
