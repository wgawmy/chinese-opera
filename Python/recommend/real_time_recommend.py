"""基于向量检索的实时推荐服务，提供 HTTP 接口返回相似戏曲条目。"""

import torch
import numpy as np
import faiss
import json
from flask import Flask, request, jsonify

app = Flask(__name__)

# 1. 加载映射表
with open('../results/item2id.json', 'r') as f:
    item2id = json.load(f)

id2item = {v: k for k, v in item2id.items()}

# 2. 加载物品向量（假设是 shape [num_items, embedding_dim]）
item_embeddings = torch.load('../results/item_embeddings.pt')
if isinstance(item_embeddings, torch.Tensor):
    item_embeddings = item_embeddings.detach().cpu().numpy().astype('float32')
elif isinstance(item_embeddings, np.ndarray):
    item_embeddings = item_embeddings.astype('float32')
else:
    raise TypeError("item_embeddings 格式不对，既不是 torch.Tensor 也不是 numpy.ndarray")
index = faiss.IndexFlatIP(item_embeddings.shape[1])  # 余弦/点积相似度
faiss.normalize_L2(item_embeddings)  # 如果使用余弦相似度，需归一化
index.add(item_embeddings)

@app.route('/real-recommend', methods=['POST'])
def recommend():
    data = request.get_json()
    recent_real_ids = data.get('items_ids', [])
    print("收到 item_ids：", recent_real_ids)
    print("item2id 的前几个 key：", list(item2id.keys())[:10])
    # 将真实ID映射为内部ID
    internal_ids = [item2id[str(i)] for i in recent_real_ids if str(i) in item2id]
    if not internal_ids:
        return jsonify({'error': '无效的物品ID'}), 400

    # 获取对应的向量并平均（作为用户兴趣向量）
    vectors = item_embeddings[internal_ids]
    interest_vector = np.mean(vectors, axis=0, keepdims=True)
    faiss.normalize_L2(interest_vector)  # 同样归一化

    # 查找最相似的TopK个向量
    top_k = data.get('top_k', 10)
    distances, indices = index.search(interest_vector, top_k)

    # 映射回真实ID
    similar_real_ids = [id2item[int(i)] for i in indices[0]]

    return jsonify({'recommendations': similar_real_ids})


if __name__ == '__main__':
    app.run(port=5000, debug=True)
