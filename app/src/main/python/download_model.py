import os, json, shutil
from huggingface_hub import hf_hub_download

assets_dir = os.path.abspath(
    os.path.join(os.path.dirname(__file__), '..', 'assets')
)
model_dir = os.path.join(assets_dir, 'embed_model')
os.makedirs(model_dir, exist_ok=True)

repo = 'sentence-transformers/all-MiniLM-L6-v2'

files = [
    'tokenizer.json',
    'tokenizer_config.json',
    'special_tokens_map.json',
    'vocab.txt',
    'onnx/model_qint8_arm64.onnx',
    '1_Pooling/config.json',
    'modules.json',
]

for fname in files:
    basename = os.path.basename(fname)
    dst_name = fname.replace('/', '_') if '/' in fname else basename
    if fname.startswith('onnx/'):
        dst_name = basename
    dst = os.path.join(model_dir, dst_name)
    if os.path.exists(dst):
        print(f'{dst_name} exists, skipping')
        continue
    print(f'Downloading {fname}...')
    path = hf_hub_download(repo_id=repo, filename=fname)
    shutil.copy2(path, dst)
    print(f'  saved to {dst} ({os.path.getsize(dst)} bytes)')

meta = {
    'model': 'sentence-transformers/all-MiniLM-L6-v2',
    'dimension': 384,
    'pooling': 'mean',
    'normalize': True,
    'onnx_file': 'model_qint8_arm64.onnx',
}
with open(os.path.join(model_dir, 'model_meta.json'), 'w') as f:
    json.dump(meta, f, indent=2)

print(f'All files downloaded to {model_dir}')
