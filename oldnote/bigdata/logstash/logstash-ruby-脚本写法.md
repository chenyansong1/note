[toc]

logstash-ruby-脚本写法

logstash中配置filter如下：

```shell
    filter {
      ruby {
        # Cancel 90% of events
        path => "/etc/logstash/drop_percentage.rb"
        script_params => { "percentage" => 0.9 }
      }
    }
```



The ruby script file should define the following methods:

- `register(params)`: An optional register method that receives the key/value hash passed in the `script_params` configuration option
- `filter(event)`: A mandatory Ruby method that accepts a Logstash event and must return an array of events



ruby的脚本如下

```shell
# the value of `params` is the value of the hash passed to `script_params`
# in the logstash configuration
def register(params)
	@drop_percentage = params["percentage"]
end

# the filter method receives an event and must return a list of events.
# Dropping an event means not including it in the return array,
# while creating new ones only requires you to add a new instance of
# LogStash::Event to the returned array
def filter(event)
	if rand >= @drop_percentage
		return [event]
	else
		return [] # return empty array to cancel event
	end
end
```





参考：https://www.elastic.co/guide/en/logstash/current/plugins-filters-ruby.html
