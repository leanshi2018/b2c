package com.framework.loippi.service.impl.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonAreaDao;
import com.framework.loippi.dao.common.ShopCommonExpressNotAreaDao;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.common.ShopCommonExpressNotArea;
import com.framework.loippi.service.common.ShopCommonExpressNotAreaService;
import com.framework.loippi.service.impl.GenericServiceImpl;

/**
 * @author :ldq
 * @date:2019/12/13
 * @description:dubbo com.framework.loippi.service.impl.common
 */
@Service
public class ShopCommonExpressNotAreaServiceImpl extends GenericServiceImpl<ShopCommonExpressNotArea, Long> implements ShopCommonExpressNotAreaService {

	@Autowired
	private ShopCommonExpressNotAreaDao shopCommonExpressNotAreaDao;
	@Autowired
	private ShopCommonAreaDao commonAreaDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopCommonExpressNotAreaDao);
	}

	@Override
	public void addNotArea(ShopCommonExpress express, Long[] areaIds) {
		for (Long areaId : areaIds) {
			ShopCommonArea shopCommonArea = commonAreaDao.find(areaId);

			ShopCommonExpressNotArea shopCommonExpressNotArea = new ShopCommonExpressNotArea();
			shopCommonExpressNotArea.setExpressId(express.getId());
			shopCommonExpressNotArea.setAreaId(shopCommonArea.getId());
			shopCommonExpressNotArea.setAreaName(shopCommonArea.getAreaName());
			shopCommonExpressNotArea.setAreaDeep(shopCommonArea.getAreaDeep());
			shopCommonExpressNotAreaDao.insert(shopCommonExpressNotArea);
		}
	}

	@Override
	public ShopCommonExpressNotArea findByEIdAndAId(Long expressId,Long areaId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("expressId",expressId);
		map.put("areaId",areaId);
		return shopCommonExpressNotAreaDao.findByEIdAndAId(map);
	}
}
