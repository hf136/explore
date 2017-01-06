# -*- coding:utf-8 -*-

import csv
import math

# 读取发明家发明专利的 csv 数据
def get_data(min_number=0):
    data = []
    with open('inventor_hist_dropna_data.csv') as f:
        f_csv = csv.reader(f)
        headers = next(f_csv)
        for row in f_csv:
            data.append(row)

    inve = []
    trend = []
    pi = 0.
    for line in data:
        # pi
        pi = pi + math.sqrt(sum(float(x) for x in line[7:]))
        e = [float(x) for x in line[:7]]
        pre = sum(float(x) for x in line[4:7]) / 3.0
        if pre < min_number:
            continue
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
    # 做差分
    train_x = difference(train_x)
    test_x = difference(test_x)
    return train_x, train_y, test_x, test_y

# 展开成多项式特征
def polynomial_features(x):
    res = []
    for row in x:
        res_row = []
        for i in row:
            res_row.append(i)
        for i in row:
            for j in row:
                res_row.append(i*j)
        res.append(res_row)
    return res

# 差分
def difference(x):
    feature = []
    for row in x:
        res = []
        for i in range(1, len(row)):
            res.append(row[i] - row[i-1])
        feature.append(res)
    return feature
