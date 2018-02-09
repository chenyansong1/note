

```
def getEcharData():
	#read file into line
	log_data_file=open('./getNum.log')
	lines = log_data_file.readlines() #拿到所有行的一个list
	count_line = len(lines)

	batch_num = count_line/5
	xAxis_data=[]
	sys_data=[]
	gen_data=[]
	event_data=[]


	for batch_index in range(0,batch_num):
		index_lines = lines[batch_index*5:batch_index*5+5] #取list的first到last元素

		time = index_lines[0].split("################")[1].strip()
		sys_num = index_lines[1].strip('\n')
		gen_num = index_lines[2].strip('\n')
		event_num = index_lines[3].strip('\n')
		

		xAxis_data.append(time)
		sys_data.append(sys_num)
		gen_data.append(gen_num)
		event_data.append(event_num)

	return [xAxis_data,sys_data,gen_data,event_data]

```