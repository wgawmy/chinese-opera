"""离线训练与评估 NCF 模型的脚本，用于生成推荐结果和中间产物。"""

import os
import sys
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
sys.path.append(BASE_DIR)
import torch

from dataset.ncf_dataset import NCFDataPreprocessor
from model.ncf_arch import NCF
from recommend.ncf_recommend import load_model, recommend_all_users, translate_recommendations, \
    save_recommendation_results
from trainer.ncf_trainer import NCFTrainer

if __name__ == '__main__':
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    top_k = 10
    data_path = '../data/ratings.dat'
    save_model_dir = '../checkpoints'
    save_model_path = '../checkpoints/best_model.pt'
    result_dir = '../results'
    processor = NCFDataPreprocessor(data_path, test_size=0.25, num_neg=1)
    train_data, test_data,num_users,num_items,user_item_dict,user2id,item2id= processor.load_and_process()
    model = NCF(num_users, num_items)
    trainer = NCFTrainer(model,train_data,test_data,save_dir=save_model_dir)
    trainer.train()
    #获取推荐列表
    now_model = load_model(save_model_path, num_users, num_items,device)
    recommendations = recommend_all_users(now_model,num_users,num_items,user_item_dict,top_k)
    translated_recommendations = translate_recommendations(recommendations,user2id,item2id)
    save_recommendation_results(now_model,user2id,item2id,translated_recommendations,save_dir =result_dir,device=device)

