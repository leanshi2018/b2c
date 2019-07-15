package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.ShopCommonSpec;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.vo.goods.SpecVo;

import java.util.List;

/**
 * SERVICE - ShopCommonSpec(商品规格模板表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonSpecService extends GenericService<ShopCommonSpec, Long> {

    void save(ShopCommonSpec spec, String specValues);

    void update(ShopCommonSpec spec, String specValues);

    List<SpecVo> findListByType2(Long typeId);

}
