import pickle
import json

# Load your LangChain pickle file
with open('app/src/main/assets/index.pkl', 'rb') as f:
    metadata = pickle.load(f)

# Export to a format C++ can easily parse
with open('metadata.json', 'w') as f:
    json.dump(metadata, f)