#!/usr/bin/python


'''
time			A	B	C	D
20170511		1	2	3	1
20170512		2	6	2	2
20170513		3	3	4	4
20170514		9	1	1	2
20170515		1	2	3	1
'''

import sys
import os

#file and default char type
file_path = ""
char_type = "line"


if len(sys.argv)>=3:
	file_path = sys.argv[1]
	char_type = sys.argv[2]
	if(os.path.isfile(file_path)):
		print "ok file exist"
	else:
		print "File not exist!!!!"
		sys.exit(0)
elif len(sys.argv) >= 2:
	if(os.path.isfile(file_path)):
                print "ok file exist"
        else:
                print "File not exist!!!!"
                sys.exit(0)
	print "default char type is line"
elif len(sys.argv) == 1:
	print "please input filename...."
	sys.exit(0) 



is_first_line="true"

lengend_data = []
x_data = []
series = []

for line in open(file_path):
	#line list
	cols = [ col.strip() for col in line.split("\t")]
	if is_first_line == "true":
		#lengend_data
		lengend_data = cols[1:]

		#set series empty data
		for lengend in lengend_data:
			series.append({})		

		#set flag not first line
		is_first_line = "false"
	else:
		#print "not first.."
		x_data.append(cols[0])
		
		series_line = cols[1:]		
		#series
		for i in range(len(lengend_data)):	
			#print lengend_data[i]
			series_i_data = series[i].get("data")
			if not series_i_data:
				series_i = {"name":lengend_data[i], "type":char_type, "data":[series_line[i]]}
				series[i] = series_i

				#print series_i
			else:
				series_i_data.append(series_line[i])

		
		#print series


echar_json = """
option = {{
    legend: {{
        data:{0}
    }},
    tooltip : {{
        trigger: 'axis'
    }},
    calculable : true,
    xAxis : [
        {{
            type : 'category',
            data : {1}
        }}
    ],
    yAxis : [
        {{
            type : 'value'
        }}
    ],
    series : {2}
}};
""".format(lengend_data,x_data,series)

print echar_json
