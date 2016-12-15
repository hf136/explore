# -*- coding:utf-8 -*-

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# 画图
# 1. 前十的国家发表专利随时间的变化数 (包括去除美国 和 不去除美国的)
country = pd.read_csv('country.csv')
top = country['count'].groupby([country['country']]).sum().sort_values(ascending=False)
top10_country = top.index.values[:10]
# int -> datetime
country['year'] = country['year'].apply(lambda y: str(y))
country['year'] = pd.to_datetime(country['year'])
res = country['count'].groupby([country['country'], country['year']]).sum()
# 去除美国和日本
# top10_country = top10_country[2:]
for con in top10_country:
    res[con].plot()
plt.legend(top10_country, loc='best')
plt.show()

# 2. 前十的公司随时间的变化
org = pd.read_csv('organization.csv')
top = org[org['year'] == 2015]
top10_org = top['org_name'][:5]
org['year'] = org['year'].apply(lambda y: str(y))
org['year'] = pd.to_datetime(org['year'])
res = org['count'].groupby([org['org_name'], org['year']]).sum()
for name in top10_org:
    res[name].plot()
plt.legend(top10_org, loc='best')
plt.show()

# w_res = res.ix[top10_org]
# # w_res = w_res.to_frame().unstack()
# w_res.to_csv('w_organization.csv', sep=',', header=None)

# 中国的企业
org = pd.read_csv('cn_organization.csv')
top = org[org['year'] == 2015]
top10_org = top['org_name'][:5]
org['year'] = org['year'].apply(lambda y: str(y))
org['year'] = pd.to_datetime(org['year'])
res = org['count'].groupby([org['org_name'], org['year']]).sum()
for name in top10_org:
    res[name].plot()
plt.legend(top10_org, loc='best')
plt.show()

# 3. 前十的公司发明者列表
org_inv = pd.read_csv('org_inventor_patents.csv')
res = org_inv['count'].groupby([org_inv['orgname'], org_inv['year'], org_inv['firstname'], org_inv['lastname']]).sum()
w_res = res.ix[top10_org]
# w_res.to_csv('w_org_inventor_patents.csv', sep=',', header=None)
ibm_inv = res['international business machines corporation'][2015].sort_values(ascending=False)

# 4. 前十的公司的发明专利类型 以及 随时间变化
org_type = pd.read_csv('org_type.csv')
res = org_type['count'].groupby([org_type['orgname'], org_type['year'], org_type['appl_type']]).sum()
for name in top10_org:
    res[name].unstack().fillna(0).plot(kind='bar')
    plt.title(name)
    # plt.savefig('org_type ' + name)
    plt.show()

# 5. top 10 的公司的发明专利领域变化
org_class = pd.read_csv('org_class.csv')
res = org_class['count'].groupby([org_class['orgname'], org_class['year'], org_class['main_class']]).sum()
# res['international business machines corporation']

# 同领域的不同公司
class_org = org_class['count'].groupby([org_class['main_class'], org_class['year'], org_class['orgname']]).sum()
from pandas import DataFrame, Series
arr = {}
for i in range(2006, 2016):
    arr.update({i:class_org['707'][i].sort_values(ascending=False)[:3]})
    # print i
    # print class_org['707'][i].sort_values(ascending=False)[:3]
res = DataFrame(arr)
res.T.plot(kind='bar')
plt.title('class 707')
plt.xlabel('year')
plt.show()

# 6. top 10 行业领域变化图
field = pd.read_csv('class.csv')
res = field['count'].groupby([field['year'], field['class']]).sum().sort_values(ascending=False)
arr = {}
for i in range(2006, 2015):
    arr.update({i:res[i][:3]})
    # print i
    # print res[i][:3]
res = DataFrame(arr)
res.T.plot(kind='bar')
plt.xlabel('year')
plt.show()

# 7. top 10 的发明家发明专利变化
inventor_rank = pd.read_csv('inventor_rank.csv')
inventor_rank['year'] = inventor_rank['year'].apply(lambda y: str(y))
inventor_rank['year'] = pd.to_datetime(inventor_rank['year'])
res = inventor_rank['count'].groupby([inventor_rank['firstname'], inventor_rank['lastname'], inventor_rank['year']]).sum()
trans = res.unstack()
trans = trans.dropna()
sample = trans.sample(5)
sample.T.plot()
plt.show()

# 8. top 10 的发明家的专利类型
inventor_type = pd.read_csv('inventor_type.csv')
inventor_type['year'] = inventor_type['year'].apply(lambda y: str(y))
inventor_type['year'] = pd.to_datetime(inventor_type['year'])
grouped = inventor_type['count'].groupby([inventor_type['firstname'], inventor_type['lastname'], inventor_type['year']])

# 9. top 10 的发明家的发明领域变化

# 10. 专利被引用数随时间变化

# 11. 不同专利类型占用百分比随时间变化
