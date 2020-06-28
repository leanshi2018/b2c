package com.framework.loippi.service.user;


import java.util.List;

import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdGoodsAdjustment(入库记录表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdGoodsAdjustmentService  extends GenericService<RdGoodsAdjustment, Long> {

	void insert(RdGoodsAdjustment rdGoodsAdjustment);

	List<RdGoodsAdjustment> findByWidAndSign(int wId, int sign);
}
