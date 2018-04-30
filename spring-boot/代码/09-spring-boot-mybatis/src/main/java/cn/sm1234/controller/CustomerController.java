package cn.sm1234.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.sm1234.domain.Customer;
import cn.sm1234.service.CustomerService;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	@Resource
	private CustomerService customerService;
	
	//跳转到input.html页面
	@RequestMapping("/input")
	public String input(){
		return "input";
	}
	
	/**
	 * 保存方法
	 */
	@RequestMapping("/save")
	public String save(Customer customer){
		customerService.save(customer);
		return "succ";
	}
	
	/**
	 * 列表展示
	 */
	@RequestMapping("/list")
	public String list(Model model){
		List<Customer> list = customerService.findAll();
		model.addAttribute("list", list);
		return "list";
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping("/findById")
	public String findById(Integer id,Model model){
		Customer cust = customerService.findById(id);
		model.addAttribute("cust", cust);
		return "input";
	}
	
	/**
	 * 删除客户
	 */
	@RequestMapping("/delete")
	public String delete(Integer id){
		customerService.delete(id);
		return "redirect:/customer/list";
	}
}
