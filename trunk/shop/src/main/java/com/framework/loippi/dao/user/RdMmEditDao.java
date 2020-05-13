package com.framework.loippi.dao.user;


import com.framework.loippi.entity.user.RdMmEdit;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdMmEdit(会员修改记录信息)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdMmEditDao  extends GenericDao<RdMmEdit, Long> {

	void updateByStatusAndMCode(String mmCode);
}
