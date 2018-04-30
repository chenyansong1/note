package cn.sm1234.dao;

import java.util.List;

import cn.sm1234.domain.Customer;

public interface CustomerMapper {
	public void save(Customer customer);
	
	public List<Customer> findAll();

	public Customer findById(Integer id);

	public void update(Customer customer);

	public void delete(Integer id);
}
