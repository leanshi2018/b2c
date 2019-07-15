package com.framework.loippi.dao.common;

import com.framework.loippi.entity.common.ShopCommonSpec;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.vo.goods.SpecVo;

import java.util.List;

/**
 * DAO - ShopCommonSpec(商品规格模板表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonSpecDao  extends GenericDao<ShopCommonSpec, Long> {

    List<SpecVo> findListByType2(Long  typeId);

}
