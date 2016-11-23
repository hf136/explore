# -*- coding:utf-8 -*-

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# 读取 csv 文件
pg = pd.read_csv("patent_grant.csv")
p_org = pd.read_csv("patent_organization.csv")
org = pd.read_csv("organizations.csv")

# 表连接
res = pd.merge(pg, p_org, left_on="grant_id", right_on="patent_id")
res = pd.merge(res, org, left_on="org_id", right_on="id")

# 统一把 orgname 变为小写
res['orgname'] = res['orgname'].apply(lambda x: x.lower())

# assignee
ass = res[res['parties']=='assignee']
ass_res = ass['grant_id'].groupby(ass['orgname']).count().sort_values(ascending=False)
#ass_res.to_csv('ass_res.csv')

# design设计专利
design = ass[ass['appl_type']=='design']
design_res = design['grant_id'].groupby(design['orgname']).count().sort_values(ascending=False)
#design_res.to_csv('design_res.csv')

# 不同的专利类型专利数(总的)
patent_type = pg['grant_id'].groupby([pg['appl_type']]).count().sort_values(ascending=False)
# 不同的专利类型专利数(按公司分)
type_res = ass['grant_id'].groupby([ass['orgname'], ass['appl_type']]).count()
type_res['international business machines corporation']

# 按国家分组
country = ass['grant_id'].groupby(ass['country_y']).count().sort_values(ascending=False)

# 画柱状图
x = np.arange(0,10)
plt.bar(x, country[0:10].values,align = 'center',alpha = 0.4)
plt.xticks(x, country[0:10].index.values)
plt.show()