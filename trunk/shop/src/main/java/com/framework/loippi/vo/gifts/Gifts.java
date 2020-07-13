package com.framework.loippi.vo.gifts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;

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
    @JsonSerialize(using = ToStringSerializer.class)
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
