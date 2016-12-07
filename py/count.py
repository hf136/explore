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

# 筛选出 assignee 专利受让人（所有权人）
ass = res[res['parties'] == 'assignee']

# 每个公司的专利数
organization_patents = ass['grant_id'].groupby(ass['orgname']).count().sort_values(ascending=False)
#organization_patents.to_csv('organization_patent_numbers.csv')
plt.show(organization_patents[0:10].plot(kind='bar'))
# 排名前十的公司名字
top10_orgname = organization_patents[0:10].index.values

# design设计专利
design = ass[ass['appl_type'] == 'design']
design_res = design['grant_id'].groupby(design['orgname']).count().sort_values(ascending=False)
#design_res.to_csv('design_res.csv')

# 不同的专利类型专利数(总的)
patent_type = pg['grant_id'].groupby([pg['appl_type']]).count().sort_values(ascending=False)

# 不同的专利类型专利数(按公司分)
type_res = ass['grant_id'].groupby([ass['orgname'], ass['appl_type']]).count()
# type_res['international business machines corporation']
companys = {}
for orgname in top10_orgname:
    companys[orgname] = {}
    companys[orgname]['appl_type'] = type_res[orgname]
    # print orgname
    # print type_res[orgname]['appl_type']

# 按国家分组
country = ass['grant_id'].groupby(ass['country_y']).count().sort_values(ascending=False)
# 画柱状图
x = np.arange(0, 10)
plt.bar(x, country[0:10].values, align='center', alpha=0.4)
plt.xticks(x, country[0:10].index.values)
plt.show()

# 中国的企业
ass_cn = ass[ass['country_y'] == 'CN']
cn_orgname_patents = ass_cn['grant_id'].groupby(ass_cn['orgname']).count().sort_values(ascending=False)

# 按分类类别统计
pg['main_class'] = pg['national_classification'].apply(lambda x: x[0:3])
class_pg = pg['grant_id'].groupby(pg['main_class']).count().sort_values(ascending=False)

# 根据 number_of_claims 排序
pg.sort_values(by='number_of_claims', ascending=False)


# 读取 inventors 数据
inventors = pd.read_csv('inventors.csv')
pg_inventors = pd.read_csv('patent_inventor.csv')

# 专利授权 和 发明家 表连接
pg_inv = pd.merge(pg, pg_inventors, left_on="grant_id", right_on="patent_id")
pg_inv = pd.merge(pg_inv, inventors, left_on="inventor_id", right_on="id")

# 作者排行
inventor_rank = pg_inv['grant_id'].groupby([pg_inv['firstname'], pg_inv['lastname']]).count().sort_values(ascending=False)

# 专利、组织、发明家 三表连接
pg_org_inv = pd.merge(ass, pg_inventors, left_on="grant_id", right_on="patent_id")
pg_org_inv = pd.merge(pg_org_inv, inventors, left_on="inventor_id", right_on="id")

# 每个公司的发明家的专利数
org_inventor_patents = pg_org_inv['grant_id'].groupby([pg_org_inv['orgname'], pg_org_inv['firstname'], pg_org_inv['lastname']]).count().sort_values(ascending=False)
# top10 公司的发明家排行
for orgname in top10_orgname:
    companys[orgname]['inventors'] = org_inventor_patents[orgname]


# 转化成时间类型
pg_inv['date'] = pg_inv['date'].apply(lambda x : str(x))
pg_inv['date'] = pd.to_datetime(pg_inv['date'])

pg_inv['appl_date'] = pg_inv['appl_date'].apply(lambda  x : str(x))
pg_inv['appl_date'] = pd.to_datetime(pg_inv['appl_date'])

# 每个作者授予专利数时间序列图
inventor_date_pg = pg_inv['grant_id'].groupby([pg_inv['firstname'], pg_inv['lastname'], pg_inv['date']]).count()

# 发明家 Shunpei Yamazaki 发明专利数随时间变化图
# plt.show(inventor_date_pg['Shunpei']['Yamazaki'].plot())
