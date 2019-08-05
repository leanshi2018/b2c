package com.framework.loippi.dao.common;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.vo.address.MemberAddresVo;

/**
 * DAO - ShopCommonArea(地区表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonAreaDao  extends GenericDao<ShopCommonArea, Long> {

    /**
     * 根据地区ID查询出所有父节点的信息
     */
    MemberAddresVo findByAreaId(Map<String, Object> var1);

	List<ShopCommonArea> findByAreaName(String areaName);
}
