package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.common.ShopCommonExpressNotArea;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2019/12/13
 * @description:dubbo com.framework.loippi.service.common
 */
public interface ShopCommonExpressNotAreaService extends GenericService<ShopCommonExpressNotArea, Long> {
	void addNotArea(ShopCommonExpress express, Long[] areaIds);

	ShopCommonExpressNotArea findByEIdAndAId(Long expressId,Long areaId);
}
