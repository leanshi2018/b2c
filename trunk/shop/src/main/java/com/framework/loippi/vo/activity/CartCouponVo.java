package com.framework.loippi.vo.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;

/**
 * 购物车按店铺id和商品所在一级分类id分组
 * @author liukai
 */
@Data
public class CartCouponVo implements GenericEntity {

//	private List<ShopCartActivity> cartList;
//
//	/**
//     * 商品所在一级分类id(购物车商品按storeId和一级分类id分组时使用,优惠券查询时)
//     */
//    private String firstGcId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 该分组下的商品总价格
     */
    private double goodsTotalPrice;

    /**
     * 商品总数
     */
    private int goodsTotalNum;

//    /**
//     * 分类id
//     */
//    private String gcId;
//
//    /**
//     * 品牌id
//     */
//    private String brandId;
//
//    /**
//     * 类型id
//     */
//    private  String typeId;


//    /**
//     * 商品id
//     */
//    private  String goodsId;

//    /**
//     *  0全部商品
//     *  1指定商品分类
//     *  2指定商品类型
//     *  3指定品牌
//     *  4指定商品
//     * 商品分类、品牌、类型、单个或多个商品、sku 全部
//     */
//    private  Integer goodsType;

//    private  String activityTypes;

    private Long memberId;
}
