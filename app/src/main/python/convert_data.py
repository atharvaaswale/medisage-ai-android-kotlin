import pickle
import json
import struct
import faiss
import sys
import os
from huggingface_hub import hf_hub_download

assets_dir = os.path.abspath(
    os.path.join(os.path.dirname(__file__), '..', 'assets')
)

faiss_path = os.path.join(assets_dir, 'index.faiss')
pkl_path = os.path.join(assets_dir, 'index.pkl')
vectors_out = os.path.join(assets_dir, 'vectors.bin')
meta_out = os.path.join(assets_dir, 'metadata.json')
hdr_out = os.path.join(assets_dir, 'vectors.hdr')
model_dir = os.path.join(assets_dir, 'embed_model')

# 1. Load and export FAISS index vectors
idx = faiss.read_index(faiss_path)
print(f'Index: {type(idx).__name__}, d={idx.d}, ntotal={idx.ntotal}, metric_type={idx.metric_type}')

vectors = idx.reconstruct_n(0, idx.ntotal)
vectors.astype('float32').tofile(vectors_out)
print(f'Exported vectors ({vectors.shape[0]} x {vectors.shape[1]}) to {vectors_out}')

# 2. Load pickle and convert to JSON
with open(pkl_path, 'rb') as f:
    tup = pickle.load(f)

docstore, id_map = tup
print(f'Docstore: {type(docstore).__name__}, entries: {len(docstore._dict)}')
print(f'ID map: {type(id_map).__name__}, entries: {len(id_map)}')

documents = []
for idx_val, uuid_str in id_map.items():
    doc = docstore._dict[uuid_str]
    doc_entry = {
        'id': idx_val,
        'uuid': uuid_str,
        'content': doc.page_content if hasattr(doc, 'page_content') else str(doc),
    }
    if hasattr(doc, 'metadata') and doc.metadata:
        doc_entry['metadata'] = doc.metadata
    documents.append(doc_entry)

print(f'First document content preview: {documents[0]["content"][:100]}...')

documents.sort(key=lambda d: d['id'])

output = {
    'dimension': idx.d,
    'num_vectors': idx.ntotal,
    'metric_type': int(idx.metric_type),
    'documents': documents,
}

with open(meta_out, 'w', encoding='utf-8') as f:
    json.dump(output, f, ensure_ascii=False, indent=2)
print(f'Exported metadata to {meta_out} ({len(documents)} docs)')

with open(hdr_out, 'w') as f:
    f.write(f'{idx.d}\n{idx.ntotal}\n{idx.metric_type}\n')
print(f'Exported header to {hdr_out}')

# 3. Download ONNX embedding model for on-device inference
# Uses sentence-transformers/all-MiniLM-L6-v2 (384-dim, matching the FAISS index)
os.makedirs(model_dir, exist_ok=True)

repo = "sentence-transformers/all-MiniLM-L6-v2"

# Tokenizer/ config files (at repo root)
root_files = [
    "tokenizer.json",
    "tokenizer_config.json",
    "special_tokens_map.json",
    "vocab.txt",
]
for fname in root_files:
    out_path = os.path.join(model_dir, fname)
    if not os.path.exists(out_path):
        print(f'Downloading {fname}...')
        hf_hub_download(repo_id=repo, filename=fname, local_dir=model_dir,
                        local_dir_use_symlinks=False)
    else:
        print(f'{out_path} already exists, skipping')

# ONNX model file - use quantized ARM64 version for mobile
onnx_files = [
    "onnx/model_qint8_arm64.onnx",
]
for fname in onnx_files:
    out_path = os.path.join(model_dir, os.path.basename(fname))
    if not os.path.exists(out_path):
        print(f'Downloading {fname}...')
        hf_hub_download(repo_id=repo, filename=fname, local_dir=model_dir,
                        local_dir_use_symlinks=False)
    else:
        print(f'{out_path} already exists, skipping')

# Also download 1_Pooling/config.json for mean pooling info
pooling_files = [
    "1_Pooling/config.json",
    "modules.json",
]
for fname in pooling_files:
    out_path = os.path.join(model_dir, os.path.basename(fname).replace('/', '_'))
    if not os.path.exists(out_path):
        print(f'Downloading {fname}...')
        content = hf_hub_download(repo_id=repo, filename=fname)
        import shutil
        shutil.copy2(content, out_path)
    else:
        print(f'{out_path} already exists, skipping')

# Save model metadata
model_meta = {
    'model': 'sentence-transformers/all-MiniLM-L6-v2',
    'dimension': 384,
    'pooling': 'mean',
    'normalize': True,
    'onnx_file': 'model_qint8_arm64.onnx',
}
with open(os.path.join(model_dir, 'model_meta.json'), 'w') as f:
    json.dump(model_meta, f, indent=2)

print(f'Embedding model downloaded to {model_dir}')
print('Done!')
