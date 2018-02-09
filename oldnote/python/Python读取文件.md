

```
def getEcharData():
	#read file into line
	log_data_file=open('./getNum.log')
	lines = log_data_file.readlines() #拿到所有行的一个list

	for line in lines:
		print line


# 去掉行为的换行符 "\n"
line=line.strip('\n')

```