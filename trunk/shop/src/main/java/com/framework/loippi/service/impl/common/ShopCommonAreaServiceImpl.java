package com.framework.loippi.service.impl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonAreaDao;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.vo.address.MemberAddresVo;

/**
 * SERVICE - ShopCommonArea(地区表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonAreaServiceImpl extends GenericServiceImpl<ShopCommonArea, Long> implements ShopCommonAreaService {
	
	@Autowired
	private ShopCommonAreaDao shopCommonAreaDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopCommonAreaDao);
	}

	@Override
	public MemberAddresVo findByAreaId(Map<String, Object> params) {
		return shopCommonAreaDao.findByAreaId(params);
	}

	@Override
	public List<ShopCommonArea> findByAreaName(String areaName) {
		return shopCommonAreaDao.findByAreaName(areaName);
	}

	@Override
	public void disableAndRestoreArea(Long id, int expressState) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id",id);
		map.put("expressState",expressState);
		shopCommonAreaDao.disableAndRestoreArea(map);
	}
}
