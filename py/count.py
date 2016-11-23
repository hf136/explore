# -*- coding:utf-8 -*-

import pandas as pd

# 读取 csv 文件
pg = pd.read_csv("patent_grant.csv")
p_org = pd.read_csv("patent_organization.csv")
org = pd.read_csv("organizations.csv")

# 表连接
res = pd.merge(pg, p_org, left_on="grant_id", right_on="patent_id")
res = pd.merge(res, org, left_on="org_id", right_on="id")

# group 分组
grouped = res['grant_id'].groupby([res['orgname'], res['parties']])
g = grouped.count()
gs = g.sort_values(ascending=False)