/*================================================================
*   Copyright (C) 2019 Sangfor Ltd. All rights reserved.
*   
*   file name  ：test_sizeof.c
*   creator    ：chenyansong
*   create_date：2019-06-24
*   description：
*
================================================================*/


#include "stdio.h"


int main(){
	char c = 127;
	c = c+1;
	printf("c=%d\n", c);
	//printf("sizeof(char)=%ld\n", sizeof(char));
	//printf("sizeof(short)=%ld\n", sizeof(short));
	//printf("sizeof(int)=%ld\n", sizeof(int));
	//printf("sizeof(long)=%ld\n", sizeof(long));
	return 0;
}
