package cn.sm1234.service;

import java.util.List;

import cn.sm1234.domain.Customer;

public interface CustomerService {
	public void save(Customer customer);
	
	public List<Customer> findAll();

	public Customer findById(Integer id);

	public void delete(Integer id);
}
