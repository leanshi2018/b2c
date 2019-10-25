package com.framework.loippi.vo.gifts;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 赠品
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gifts implements GenericEntity {
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品图片
     */
    private String goodsImage;
    /**
     * 赠送数量
     */
    private Integer giftsNum;
    /**
     * 商品库存数量
     */
    private Long stock;
}
