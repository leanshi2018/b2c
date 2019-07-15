package com.framework.loippi.vo.goods;


import com.framework.loippi.entity.common.ShopCommonSpec;
import com.framework.loippi.entity.common.ShopCommonSpecValue;
import lombok.Data;

import java.util.List;

/**
 * 商品规格表
 */

@Data
public class SpecVo extends ShopCommonSpec {
    
    /**
     * 规格值
     */
    private List<ShopCommonSpecValue> specValueList;


}
