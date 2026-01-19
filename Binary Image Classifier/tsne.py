# -*- coding: utf-8 -*-
"""
Created on Tue Nov 26 22:24:15 2024

@author: JosepH2O
"""

import numpy as np
import matplotlib.pyplot as plt
from sklearn.manifold import TSNE

# Dosya adları
w1_adam = r"Bkismi\W1\adam\weights.txt";
w1_sgd = r"Bkismi\W1\sgd\weights.txt";
w1_gd = r"Bkismi\W1\gd\weights.txt";

w2_adam = r"Bkismi\W2\adam\weights.txt";
w2_sgd = r"Bkismi\W2\sgd\weights.txt";
w2_gd = r"C:\Users\JosepH2O\Desktop\Desktop\Projeler2\optimizasyon\Bkismi\W2\gd\weights.txt";

w3_adam = r"Bkismi\W3\adam\weights.txt";
w3_sgd = r"Bkismi\W3\sgd\weights.txt";
w3_gd = r"Bkismi\W3\gd\weights.txt";

w4_adam = r"Bkismi\W4\adam\weights.txt";
w4_sgd = r"Bkismi\W4\sgd\weights.txt";
w4_gd = r"Bkismi\W4\gd\weights.txt";

w5_adam = r"Bkismi\W5\adam\weights.txt";
w5_sgd = r"Bkismi\W5\sgd\weights.txt";
w5_gd = r"Bkismi\W5\gd\weights.txt";
# Dosya adları ve etiketler


file_names = [w1_gd,w1_sgd,w1_adam,w2_gd,w2_sgd,w2_adam,w3_gd,w3_sgd,w3_adam,w4_gd,w4_sgd,w4_adam,w5_gd,w5_sgd,w5_adam]
labels_names = ["w1_gd","w1_sgd","w1_adam","w2_gd","w2_sgd","w2_adam","w3_gd","w3_sgd","w3_adam","w4_gd","w4_sgd","w4_adam","w5_gd","w5_sgd","w5_adam"]# Her yörüngeye bir etiket

# Yörüngeleri ve etiketleri saklamak için listeler
trajectories = []
labels = []

# Her dosyayı oku ve veriyi ekle
for idx, file_name in enumerate(file_names):
    data = np.loadtxt(file_name)  # Dosyayı yükle
    trajectories.append(data)    # Veriyi kaydet
    labels.extend([labels_names[idx]] * data.shape[0])  # Her satır için bir etiket oluştur

# Trajektörleri birleştir
all_data = np.vstack(trajectories)

# t-SNE ile boyut indirgeme
tsne = TSNE(n_components=2, random_state=42, perplexity=600, learning_rate=200)
reduced_data = tsne.fit_transform(all_data)

# Veriyi gruplara ayırma
group_indices = [list(range(3)), list(range(3, 6)), list(range(6, 9)), list(range(9, 12)), list(range(12, 15))]
#group_indices = [list(range(15))]
colors = ['red', 'blue', 'green', 'purple']  # 5 farklı renk
markers = ['o', 'X', 's', 'P', 'D']  # Her yörünge için farklı marker

# Her grup için ayrı bir grafik
for group_id, indices in enumerate(group_indices):
    plt.figure(figsize=(8, 6))
    for idx, color, marker in zip(indices, colors, markers):
        # Yörünge noktalarını seç
        yörünge_noktaları = reduced_data[np.array(labels) == labels_names[idx]]

        # Tüm noktaları çiz
        plt.scatter(
            yörünge_noktaları[:, 0],
            yörünge_noktaları[:, 1],
            label=labels_names[idx],
            color=color,
            marker=marker,
            alpha=0.6
        )

        # Başlangıç noktası
        plt.scatter(
            yörünge_noktaları[0, 0],
            yörünge_noktaları[0, 1],
            color='brown',
            edgecolor='white',
            marker=marker,
            s=200,
            label=f"Start of {labels_names[idx]}"
        )

        # Son nokta
        plt.scatter(
            yörünge_noktaları[-1, 0],
            yörünge_noktaları[-1, 1],
            color='black',
            edgecolor='white',
            marker=marker,
            s=200,
            label=f"End of {labels_names[idx]}"
        )

    plt.title(f"t-SNE Visualization - Group {group_id + 1}")
    plt.xlabel("t-SNE Dimension 1")
    plt.ylabel("t-SNE Dimension 2")
    plt.legend(loc='best')
    plt.grid(True)
    plt.show()