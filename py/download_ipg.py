#!/usr/bin/python
# -*- coding: UTF-8 -*-


import datetime
import urllib

if __name__ == '__main__':
	date = datetime.datetime.strptime('20060103', "%Y%m%d")
	end_date = datetime.datetime.strptime('20160101', "%Y%m%d")
	while date < end_date:
		url = "https://bulkdata.uspto.gov/data2/patent/grant/redbook/fulltext/" + date.strftime("%Y") + "/ipg"
		url = url + date.strftime("%Y%m%d")[2:] + ".zip"
		fname = "ipg" + date.strftime("%Y%m%d")[2:] + ".zip"
		print(url, fname)
		# urllib.urlretrieve(url, filename=fname)
		date = date + datetime.timedelta(days=7)
