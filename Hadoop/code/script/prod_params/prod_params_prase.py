#!/usr/bin/python 
#-*- coding:utf-8 –*-
#coding=utf-8
from HTMLParser import HTMLParser
import sys

class MyHTMLParser(HTMLParser):
	def __init__(self):
		self.recstr = ""
		HTMLParser.__init__(self)
 
	def handle_data(self, data):
		self.recstr+=(data+",")
		
if __name__ == "__main__":
	for ln in sys.stdin:
	        ln = ln.strip()
		rec = ln.split('\t')
		
		hp = MyHTMLParser()   
		hp.feed(rec[1])
		#print(hp.recstr)
	
		recs = []
		for rec1 in hp.recstr.split(', ,'):
			rs = rec1.split(',')
			if(len(rs)>1 and len(rs[0])>0 and len(rs[1])>0):
				#recs.append(':'.join([rec[0],rs[0],rs[1]]))
				sys.stdout.write('\t'.join([rec[0],rs[0],rs[1]]))
				sys.stdout.write('\n')
		sys.stdout.flush()
		#print(','.join(recs))
	
		hp.close()
