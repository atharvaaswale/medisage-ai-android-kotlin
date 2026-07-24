import onnxruntime as ort
import numpy as np
from transformers import AutoTokenizer

tokenizer = AutoTokenizer.from_pretrained('sentence-transformers/all-MiniLM-L6-v2')

model_path = 'D:/5. Projects/MediSageAI/app/src/main/assets/embed_model/model_qint8_arm64.onnx'
session = ort.InferenceSession(model_path)

text = 'What causes headaches?'
inputs = tokenizer(text, padding='max_length', truncation=True, max_length=256, return_tensors='np')

outputs = session.run(None, {
    'input_ids': inputs['input_ids'].astype(np.int64),
    'attention_mask': inputs['attention_mask'].astype(np.int64),
    'token_type_ids': inputs['token_type_ids'].astype(np.int64),
})

print(f'Output shape: {outputs[0].shape}')

# Mean pooling
token_embeds = outputs[0]
mask = inputs['attention_mask']
mask_expanded = mask[:, :, np.newaxis].astype(float)
sum_embeds = np.sum(token_embeds * mask_expanded, axis=1)
sum_mask = np.sum(mask, axis=1, keepdims=True).astype(float)
pooled = sum_embeds / np.maximum(sum_mask, 1e-9)

norm = np.linalg.norm(pooled, axis=1, keepdims=True)
pooled = pooled / norm

print(f'Embedding first 10 values: {pooled[0][:10]}')
print(f'Norm: {np.linalg.norm(pooled[0]):.6f}')
print('SUCCESS')
