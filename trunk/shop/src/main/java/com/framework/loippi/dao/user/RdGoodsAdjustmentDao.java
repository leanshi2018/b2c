package com.framework.loippi.dao.user;


import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdGoodsAdjustment(入库记录表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdGoodsAdjustmentDao  extends GenericDao<RdGoodsAdjustment, Long> {

	List<RdGoodsAdjustment> findByWidAndSign(Map<String, Object> map);
}
