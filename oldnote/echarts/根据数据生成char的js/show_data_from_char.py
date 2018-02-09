#!/bin/python
# coding: utf-8

import re


def getEcharData():
	#read file into line
	log_data_file=open('./getNum.log')
	lines = log_data_file.readlines()
	count_line = len(lines)

	batch_num = count_line/5
	xAxis_data=[]
	sys_data=[]
	gen_data=[]
	event_data=[]


	for batch_index in range(0,batch_num):
		index_lines = lines[batch_index*5:batch_index*5+5]

		time = index_lines[0].split("################")[1].strip()
		sys_num = index_lines[1].strip('\n')
		gen_num = index_lines[2].strip('\n')
		event_num = index_lines[3].strip('\n')
		

		xAxis_data.append(time)
		sys_data.append(sys_num)
		gen_data.append(gen_num)
		event_data.append(event_num)

	return [xAxis_data,sys_data,gen_data,event_data]


def geneEcharOption():
	data_arr = getEcharData()

	xAxis_data=str(data_arr[0])
	sys_data=str(data_arr[1])
	gen_data=str(data_arr[2])
	event_data=str(data_arr[3])


	old_file='/home/workspace/showlogechart/echar_render.js'
	new_file='/home/workspace/showlogechart/echar_render_tmp.js'
	fopen=open(old_file,'r')

	w_str=""
	for line in fopen:
	        if re.search('\$xAxis\$',line):
	                line=re.sub('\$xAxis\$',xAxis_data,line)
	                w_str+=line
	        elif re.search('\$sys\$',line):
	                line=re.sub('\$sys\$',sys_data,line)
	                w_str+=line
	        elif re.search('\$gen\$',line):
	                line=re.sub('\$gen\$',gen_data,line)
	                w_str+=line
	        elif re.search('\$event\$',line):
	                line=re.sub('\$event\$',event_data,line)
	                w_str+=line
	        else:
	                w_str+=line

	wopen=open(new_file,'w')
	wopen.write(w_str)
	fopen.close()
	wopen.close()



if __name__=='__main__':
	data_arr = getEcharData()

	# print data_arr
	geneEcharOption()


