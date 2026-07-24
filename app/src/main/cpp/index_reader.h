#ifndef INDEX_READER_H
#define INDEX_READER_H

#include <vector>
#include <cstdint>
#include <string>

class VectorIndex {
public:
    VectorIndex();
    ~VectorIndex();

    bool load(const std::string& directory);

    int dimension() const { return dim_; }
    int numVectors() const { return ntotal_; }
    int metricType() const { return metric_type_; }
    bool loaded() const { return loaded_; }

    struct SearchResult {
        int index;
        float distance;
    };

    std::vector<SearchResult> search(const float* query, int k) const;

private:
    bool loaded_;
    int dim_;
    int ntotal_;
    int metric_type_;
    std::vector<float> vectors_;

    float computeDistance(const float* a, const float* b) const;
};

#endif
