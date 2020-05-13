package com.framework.loippi.service.user;


import com.framework.loippi.entity.user.RdMmEdit;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdMmEdit(会员修改记录信息)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdMmEditService  extends GenericService<RdMmEdit, Long> {

	void updateByStatusAndMCode(String mmCode);
}
