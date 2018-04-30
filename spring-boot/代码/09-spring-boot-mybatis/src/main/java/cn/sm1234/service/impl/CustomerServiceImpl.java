package cn.sm1234.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.sm1234.dao.CustomerMapper;
import cn.sm1234.domain.Customer;
import cn.sm1234.service.CustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
	//注入mapper接口的对象
	@Resource
	private CustomerMapper customerMapper;
	
	@Override
	public void save(Customer customer) {
		//判断是否添加还是修改
		if(customer.getId()!=null){
			//修改
			customerMapper.update(customer);
		}else{
			//添加
			customerMapper.save(customer);
		}
	}

	@Override
	public List<Customer> findAll() {
		return customerMapper.findAll();
	}

	@Override
	public Customer findById(Integer id) {
		return customerMapper.findById(id);
	}

	@Override
	public void delete(Integer id) {
		customerMapper.delete(id);
	}

}
