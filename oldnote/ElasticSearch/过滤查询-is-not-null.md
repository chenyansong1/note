

```
#select * from tb where time>=start_time and time<end_time and srcip is not null;

{
    "query": {
        "filtered": {
            "query" : {
                    "exists":   {
				        "field":    "srcip"
				    }
            },
            "filter": {
                "range" : {
                    "recordtime": {
                        "gte":"1521744500",
						"lte":"1522744800"
                    }
                }
            }
        }
		
    }
}

```