package com.framework.loippi.dao;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.User;
import com.framework.loippi.mybatis.dao.GenericDao;
/**
 * DAO - USER
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface UserDao extends GenericDao<User, Long> {
	
	User findUserAndRole(@Param("id") Long id);
}
