# -*- coding:utf-8 -*-

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import os

start_date = 2006
end_date = 2015
while start_date <= end_date:
    year = str(start_date)
    if not os.path.exists(year + "/res"):
        os.mkdir(year + "/res")

    # 读取 csv 文件
    pg = pd.read_csv(year + "/patent_grant.csv")
    p_org = pd.read_csv(year + "/patent_organization.csv")
    org = pd.read_csv(year + "/organizations.csv")
    inventors = pd.read_csv(year + '/inventors.csv')
    pg_inventors = pd.read_csv(year + '/patent_inventor.csv')

    # 表连接
    res = pd.merge(pg, p_org, left_on="grant_id", right_on="patent_id")
    res = pd.merge(res, org, left_on="org_id", right_on="id")

    # 统一把 orgname 变为小写
    res['orgname'] = res['orgname'].apply(lambda x: str(x).lower())

    # 筛选出 assignee 专利受让人（所有权人）
    ass = res[res['parties'] == 'assignee']

    # int -> str -> datetime 转化成时间类型
    # 不建议这种赋值方法
    # http://pandas.pydata.org/pandas-docs/stable/indexing.html#indexing-view-versus-copy
    ass['date'] = ass['date'].apply(lambda d: str(d))
    ass['date'] = pd.to_datetime(ass['date'])
    ass['appl_date'] = ass['appl_date'].apply(lambda d: str(d))
    ass['appl_date'] = pd.to_datetime(ass['appl_date'])

    # 专利授权 和 发明家 表连接
    pg_inv = pd.merge(pg, pg_inventors, left_on="grant_id", right_on="patent_id")
    pg_inv = pd.merge(pg_inv, inventors, left_on="inventor_id", right_on="id")

    # 专利、组织、发明家 三表连接
    pg_org_inv = pd.merge(ass, pg_inventors, left_on="grant_id", right_on="patent_id")
    pg_org_inv = pd.merge(pg_org_inv, inventors, left_on="inventor_id", right_on="id")

    ##############################################################################################

    # 1. 前十的国家发表专利随时间的变化数 (包括去除美国 和 不去除美国的)
    country = ass['grant_id'].groupby(ass['country_y']).count().sort_values(ascending=False)
    # country_top10 = country.index.values[:10]
    country = country.to_frame()
    country['year'] = year
    country.to_csv(year + '/res/country.csv', header=['count', 'year'], index_label='country')

    # 2. 前十的公司随时间的变化
    organization_patents = ass['grant_id'].groupby(ass['orgname']).count().sort_values(ascending=False)
    organization_patents = organization_patents.to_frame()
    organization_patents['year'] = year
    organization_patents.to_csv(year + '/res/organization.csv', header=['count', 'year'], index_label='org_name')

    # 中国的企业
    ass_cn = ass[ass['country_y'] == 'CN']
    cn_orgname_patents = ass_cn['grant_id'].groupby(ass_cn['orgname']).count().sort_values(ascending=False)
    cn_orgname_patents = cn_orgname_patents.to_frame()
    cn_orgname_patents['year'] = year
    cn_orgname_patents.to_csv(year + '/res/cn_organization.csv', header=['count', 'year'], index_label='org_name')

    # 3. 前十的公司发明者列表
    org_inventor_patents = pg_org_inv['grant_id'].groupby([pg_org_inv['orgname'], pg_org_inv['firstname'], pg_org_inv['lastname']]).count().sort_values(ascending=False)
    org_inventor_patents = org_inventor_patents.to_frame()
    org_inventor_patents['year'] = year
    org_inventor_patents.to_csv(year + '/res/org_inventor_patents.csv', header=['count', 'year'])

    # 4. 前十的公司的发明专利类型 以及 随时间变化
    ass['main_class'] = ass['national_classification'].apply(lambda x: x[0:3])
    org_type = ass['grant_id'].groupby([ass['orgname'], ass['appl_type']]).count().sort_values(ascending=False)
    org_type = org_type.to_frame()
    org_type['year'] = year
    org_type.to_csv(year + '/res/org_type.csv', header=['count', 'year'])

    # 5. top 10 的公司的发明专利领域变化
    ass['main_class'] = ass['national_classification'].apply(lambda x: x[0:3])
    org_class_pg = ass['grant_id'].groupby([ass['orgname'], ass['main_class']]).count().sort_values(ascending=False)
    org_class_pg = org_class_pg.to_frame()
    org_class_pg['year'] = year
    org_class_pg.to_csv(year + '/res/org_class.csv', header=['count', 'year'])

    # 6. top 10 行业领域变化图
    pg['main_class'] = pg['national_classification'].apply(lambda x: x[0:3])
    class_pg = pg['grant_id'].groupby(pg['main_class']).count().sort_values(ascending=False)
    class_pg = class_pg.to_frame()
    class_pg['year'] = year
    class_pg.to_csv(year + '/res/class.csv', header=['count', 'year'], index_label='class')

    # 7. top 10 的发明家发明专利变化
    # 作者排行
    inventor_rank = pg_inv['grant_id'].groupby([pg_inv['firstname'], pg_inv['lastname']]).count().sort_values(ascending=False)
    inventor_rank = inventor_rank.to_frame()
    inventor_rank['year'] = year
    inventor_rank.to_csv(year + '/res/inventor_rank.csv', header=['count', 'year'])

    # 8. top 10 的发明家的专利类型
    inventor_type = pg_inv['grant_id'].groupby([pg_inv['firstname'], pg_inv['lastname'], pg_inv['appl_type']]).count().sort_values(ascending=False)
    inventor_type = inventor_type.to_frame()
    inventor_type['year'] = year
    inventor_type.to_csv(year + '/res/inventor_type.csv', header=['count', 'year'])

    # 9. top 10 的发明家的发明领域变化
    pg_inv['main_class'] = pg_inv['national_classification'].apply(lambda x: x[0:3])
    inventor_class = pg_inv['grant_id'].groupby([pg_inv['firstname'], pg_inv['lastname'], pg_inv['main_class']]).count().sort_values(ascending=False)
    inventor_class = inventor_class.to_frame()
    inventor_class['year'] = year
    inventor_class.to_csv(year + '/res/inventor_class.csv', header=['count', 'year'])

    # 10. 专利被引用数随时间变化

    # 11. 不同专利类型占用百分比随时间变化
    patent_type = pg['grant_id'].groupby([pg['appl_type']]).count().sort_values(ascending=False)
    patent_type = patent_type.to_frame()
    patent_type['year'] = year
    patent_type.to_csv(year + '/res/patent_type.csv', header=['count', 'year'])

    start_date += 1


# 合并相对应的 csv 文件
files = os.listdir('2006/res')
for filename in files:
    # 合并 csv
    start_date = 2006
    end_date = 2015
    li = []
    while start_date <= end_date:
        year = str(start_date)
        tmp = pd.read_csv(year + '/res/' + filename)
        li.append(tmp)
        start_date += 1
    res = pd.concat(li, ignore_index=True)
    res.to_csv('res/' + filename, index=None)
