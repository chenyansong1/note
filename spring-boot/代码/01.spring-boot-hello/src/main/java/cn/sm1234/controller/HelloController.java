package cn.sm1234.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@Controller
@RestController  //@RestController  这个注解代替的是@Controller+@ResponseBody
//@RequestMapping("/hello")
public class HelloController {

	private Map<String,Object> result = new HashMap<String,Object>();
	
	@RequestMapping("/hello")
	//@ResponseBody  // 转换json注解
	public Map<String,Object> hello(){
		result.put("name", "eric");
		result.put("gender", "男");
		return result;
	}
}
