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
#include "stdlib.h"

int main(){
void *p;
int cnt = 0;
while((p=malloc(100*1024*1024))){
  cnt++;
}
printf("分配了%d00MB的空间\n", cnt);

	return 0;
}
