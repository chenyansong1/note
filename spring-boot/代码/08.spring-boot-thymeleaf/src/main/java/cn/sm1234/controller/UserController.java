package cn.sm1234.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.sm1234.domain.User;

@Controller
public class UserController {

	@RequestMapping("/demo1")
	public String demo1(Model model){
		model.addAttribute("message", "你好，Thymeleaf");
		//跳转到templates/demo1.html
		return "demo1";
	}
	
	//变量输出
	@RequestMapping("/demo2")
	public String demo2(Model model){
		model.addAttribute("name", "张三");
		return "demo2";
	}
	
	//条件判断
	@RequestMapping("/demo3")
	public String demo3(Model model){
		model.addAttribute("gender", "女");
		
		model.addAttribute("grade",3);
		return "demo2";
	}
	
	//迭代遍历
	@RequestMapping("/demo4")
	public String demo4(Model model){
		List<User> list = new ArrayList<User>();
		list.add(new User(1,"eric",20));
		list.add(new User(2,"jack",22));
		list.add(new User(3,"rose",24));
		
		model.addAttribute("list", list);
		
		return "demo2";
	}
	
	//域对象的获取
	@RequestMapping("/demo5")
	public String demo5(HttpServletRequest request,Model model){
		
		//request
		request.setAttribute("request", "request's data");
		
		//session
		request.getSession().setAttribute("session", "session's data");
		
		//application
		request.getSession().getServletContext().setAttribute("application", "application's data");
		
		return "demo2";
	}
	
}
