package com.framework.loippi.vo.fav;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Entity - 买家收藏表
 *
 * @author zijing
 * @version 2.0
 */
@Data
public class MemberGoodsFavVo {


    /**  */
    private Long id;

    /**
     * 收藏ID
     */
    private Long favId;


    /**
     * 收藏类型 1-收藏商品，2-收藏店铺
     */
    private Integer favType;

    /**
     * 活动
     */
    private Long activityId;

    /**
     * 活动规格
     */
    private Long specId;

    /**
     * 活动类型
     */
    private Integer activitType;

    //销售价
    private BigDecimal price;

    //市场价格
    private BigDecimal marketPrice;

    //图片路径
    private String goodsImg;

    //商品名称
    private  String goodsName;
    //商品id
    private Long goodsId;

}
