package com.framework.loippi.service.common;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.vo.address.MemberAddresVo;

/**
 * SERVICE - ShopCommonArea(地区表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonAreaService  extends GenericService<ShopCommonArea, Long> {


    /**
     * 根据地区ID查询出所有父节点的信息
     */
    MemberAddresVo findByAreaId(Map<String, Object> params);

    List<ShopCommonArea> findByAreaName(String areaName);
}
