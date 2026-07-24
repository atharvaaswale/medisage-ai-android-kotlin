#include "index_reader.h"
#include <fstream>
#include <sstream>
#include <algorithm>
#include <cmath>
#include <cstring>

VectorIndex::VectorIndex()
    : loaded_(false), dim_(0), ntotal_(0), metric_type_(1) {}

VectorIndex::~VectorIndex() {}

bool VectorIndex::load(const std::string& directory) {
    std::string hdr_path = directory + "/vectors.hdr";
    std::string bin_path = directory + "/vectors.bin";

    std::ifstream hdr(hdr_path);
    if (!hdr.is_open()) return false;

    std::string line;
    std::getline(hdr, line); dim_ = std::stoi(line);
    std::getline(hdr, line); ntotal_ = std::stoi(line);
    std::getline(hdr, line); metric_type_ = std::stoi(line);
    hdr.close();

    if (dim_ <= 0 || ntotal_ <= 0) return false;

    size_t num_floats = static_cast<size_t>(ntotal_) * static_cast<size_t>(dim_);
    vectors_.resize(num_floats);

    std::ifstream bin(bin_path, std::ios::binary);
    if (!bin.is_open()) return false;

    bin.read(reinterpret_cast<char*>(vectors_.data()),
             static_cast<std::streamsize>(num_floats * sizeof(float)));

    if (!bin) {
        size_t read_bytes = static_cast<size_t>(bin.gcount());
        size_t expected = num_floats * sizeof(float);
        if (read_bytes != expected) {
            vectors_.clear();
            bin.close();
            return false;
        }
    }

    bin.close();
    loaded_ = true;
    return true;
}

float VectorIndex::computeDistance(const float* a, const float* b) const {
    float sum = 0.0f;
    for (int i = 0; i < dim_; ++i) {
        float diff = a[i] - b[i];
        sum += diff * diff;
    }
    return sum;
}

std::vector<VectorIndex::SearchResult> VectorIndex::search(
        const float* query, int k) const {
    if (!loaded_ || k <= 0) return {};

    int actual_k = std::min(k, ntotal_);
    std::vector<SearchResult> results(ntotal_);

    for (int i = 0; i < ntotal_; ++i) {
        results[i].index = i;
        results[i].distance = computeDistance(query, vectors_.data() + i * dim_);
    }

    std::partial_sort(results.begin(), results.begin() + actual_k, results.end(),
        [](const SearchResult& a, const SearchResult& b) {
            return a.distance < b.distance;
        });

    results.resize(actual_k);
    return results;
}
