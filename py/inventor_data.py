# -*- coding:utf-8 -*-

import csv

# 读取发明家发明专利的 csv 数据
def get_data():
    data = []
    with open('inventor_hist_dropna_data.csv') as f:
        f_csv = csv.reader(f)
        headers = next(f_csv)
        for row in f_csv:
            data.append(row)

    inve = []
    trend = []
    for line in data:
        e = [float(x) for x in line[:7]]
        pre = sum(float(x) for x in line[4:7]) / 3.0
        now = sum(float(x) for x in line[7:]) / 3.0
        label = [0, 1, 0]
        if now - pre > 1:
            label = [1, 0, 0]
        elif now - pre < -1:
            label = [0, 0, 1]
        inve.append(e)
        trend.append(label)

    # 切分数数据， 70% 为训练集， 30% 为测试集
    split_index = int(len(inve)*0.7)
    train_x = inve[:split_index]
    train_y = trend[:split_index]
    test_x = inve[split_index:]
    test_y = trend[split_index:]
    return train_x, train_y, test_x, test_y
