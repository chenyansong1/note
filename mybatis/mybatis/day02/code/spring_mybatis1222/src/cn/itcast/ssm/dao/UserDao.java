package cn.itcast.ssm.dao;

import java.util.List;

import cn.itcast.ssm.po.User;

/**
 * 
 * <p>Title: UserDao</p>
 * <p>Description: dao接口，用户管理</p>
 * <p>Company: www.itcast.com</p> 
 * @author	传智.燕青
 * @date	2015-4-22下午2:45:12
 * @version 1.0
 */
public interface UserDao {
	
	//根据id查询用户信息
	public User findUserById(int id) throws Exception;
	
	

}
